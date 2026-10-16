package com.wildermods.multimyth.internal;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class InstanceDeletion {

	private InstanceDeletion() {}

	public static void delete(Path instancesDirectory, Path instanceDirectory) throws IOException {
		Path root = instancesDirectory.toAbsolutePath().normalize();
		Path target = instanceDirectory.toAbsolutePath().normalize();
		if(!root.equals(target.getParent())) {
			throw new IOException("Instance must be a direct child of " + root + ": " + target);
		}

		Files.walkFileTree(target, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path directory, IOException problem) throws IOException {
				if(problem != null) throw problem;
				Files.delete(directory);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}