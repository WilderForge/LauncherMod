package com.wildermods.multimyth.internal;

import java.nio.file.Path;

import com.wildermods.workspace.capabilities.CapabilityHandler;

public record BadInstall(Path path, Throwable exception) implements IInstall {
	
	public BadInstall(Path path) {
		this(path, null);
	}
	
	@Override
	public String name() {
		return path.getFileName().toString();
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
	public CapabilityHandler getCapabiltiies() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path getRootDir() {
		// TODO Auto-generated method stub
		return null;
	}



}
