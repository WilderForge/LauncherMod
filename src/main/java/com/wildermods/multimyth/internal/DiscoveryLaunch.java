package com.wildermods.multimyth.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class DiscoveryLaunch {

	private static final int TIMEOUT_SECONDS = 60;

	private DiscoveryLaunch() {}

	public record Result(Path response, Path log) {}

	public static List<String> command(Path java, Path root, int port, String id) {
		Path directory = root.toAbsolutePath().normalize();
		return List.of(java.toAbsolutePath().toString(),
				"--add-opens", "java.base/sun.net.www.protocol.jar=ALL-UNNAMED",
				"--add-opens", "java.base/java.net=ALL-UNNAMED",
				"-Dfabric.socketAddress=127.0.0.1",
				"-Dfabric.socketPort=" + port,
				"-Dfabric.socketId=" + id,
				"-Dfabric.socketTimeout=" + TIMEOUT_SECONDS,
				"-Dfabric.websocketDiscovery=true",
				"-Dfabric.log.file=logs/discovery-loader.log",
				"-Dlog4j2.configurationFile=" + directory.resolve(".multimyth/discovery-log4j2.xml").toUri(),
				"-Xlog:class+load=info",
				"-cp", directory.resolve("*") + File.pathSeparator + directory.resolve("fabric").resolve("*"),
				"net.fabricmc.loader.impl.launch.knot.KnotRemote$Client");
	}

	public static Result run(Path java, Path root) throws IOException, InterruptedException {
		Path directory = root.toAbsolutePath().normalize();
		Path logs = directory.resolve("logs");
		Files.createDirectories(logs);
		Path loggingConfiguration = directory.resolve(".multimyth/discovery-log4j2.xml");
		Files.createDirectories(loggingConfiguration.getParent());
		try(InputStream configuration = DiscoveryLaunch.class.getResourceAsStream("/discovery-log4j2.xml")) {
			if(configuration == null) throw new IOException("Missing discovery logging configuration");
			Files.copy(configuration, loggingConfiguration, StandardCopyOption.REPLACE_EXISTING);
		}
		String id = UUID.randomUUID().toString();
		Path log = logs.resolve("discovery-" + id + ".log");
		Path response = logs.resolve("discovery-" + id + ".bin");
		try(ServerSocket server = new ServerSocket()) {
			server.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 1);
			server.setSoTimeout(250);
			Process process = new ProcessBuilder(command(java, directory, server.getLocalPort(), id))
					.directory(directory.toFile()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
			try {
				try(Socket socket = accept(server, process)) {
					socket.setSoTimeout(TIMEOUT_SECONDS * 1000);
					try(InputStream input = socket.getInputStream(); var output = Files.newOutputStream(response)) {
						// Preserve KnotRemote's framed stream for subsequent discovery/result decoding.
						int marker = input.read();
						int schema = input.read();
						int version = input.read();
						int newline = input.read();
						if(marker != 0x1E || schema != 1 || version < 1 || newline != '\n') {
							throw new IOException("Invalid KnotRemote stream. See " + log);
						}
						output.write(new byte[] {(byte) marker, (byte) schema, (byte) version, (byte) newline});
						input.transferTo(output);
					}
				}
				if(!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					throw new IOException("Discovery process did not exit. See " + log);
				}
				if(process.exitValue() != 0) {
					throw new IOException("Discovery exited with code " + process.exitValue() + ". See " + log);
				}
				return new Result(response, log);
			}
			catch(IOException e) {
				throw new IOException("Discovery failed. See " + log + ": " + e.getMessage(), e);
			}
			finally {
				if(process.isAlive()) process.destroyForcibly();
			}
		}
	}

	private static Socket accept(ServerSocket server, Process process) throws IOException, InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
		while(System.nanoTime() < deadline) {
			if(Thread.currentThread().isInterrupted()) throw new InterruptedException();
			try {
				return server.accept();
			}
			catch(SocketTimeoutException e) {
				if(!process.isAlive()) {
					throw new IOException("Discovery exited before connecting (code " + process.exitValue() + "). Check its discovery log.");
				}
			}
		}
		throw new SocketTimeoutException("KnotRemote did not connect within " + TIMEOUT_SECONDS + " seconds");
	}
}