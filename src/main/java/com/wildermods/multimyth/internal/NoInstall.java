package com.wildermods.multimyth.internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NoInstall implements IInstall {

	private static final Path randPath;
	static {
		try {
			randPath = Files.createTempDirectory("noinstall");
			Files.deleteIfExists(randPath);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		
	}
	public static final NoInstall INSTANCE = new NoInstall();
	
	private NoInstall() {
		//NO-OP
	}
	
	@Override
	public String name() {
		return "none";
	}

	@Override
	public Path artifactPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long game() {
		return -1;
	}

	@Override
	public Path getRootDir() {
		try {
			Files.deleteIfExists(randPath);
		} catch (IOException e) {
			// swallow
		}
		return randPath;
	}

}
