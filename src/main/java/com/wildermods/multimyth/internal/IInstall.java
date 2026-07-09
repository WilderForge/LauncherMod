package com.wildermods.multimyth.internal;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

import com.wildermods.thrixlvault.steam.IDownloadable;
import com.wildermods.thrixlvault.steam.IGame;
import com.wildermods.workspace.capabilities.CapabilityHandler;
import com.wildermods.workspace.capabilities.GameProject;

public interface IInstall extends IDownloadable, IGame, GameProject {

	public static final Logger LOGGER = LogManager.getLogger("INSTALL");
	
	public default CapabilityHandler getCapabiltiies() throws IOException {
		return new CapabilityHandler(this);
	}
	
	@Override
	public default void trace(String message) {
		LOGGER.trace(MarkerManager.getMarker(name()), message);
	}

	@Override
	public default void debug(String message) {
		LOGGER.debug(MarkerManager.getMarker(name()), message);
	}

	@Override
	public default void info(String message) {
		LOGGER.info(MarkerManager.getMarker(name()), message);
	}

	@Override
	public default void warn(String message) {
		LOGGER.warn(MarkerManager.getMarker(name()), message);
	}

	@Override
	public default void error(String message) {
		LOGGER.error(MarkerManager.getMarker(name()), message);
	}

	@Override
	public default void fatal(String message) {
		LOGGER.fatal(MarkerManager.getMarker(name()), message);
	}

	@Override
	public default void trace(Throwable t, String message) {
		LOGGER.trace(MarkerManager.getMarker(name()), message, t);
	}

	@Override
	public default void debug(Throwable t, String message) {
		LOGGER.debug(MarkerManager.getMarker(name()), message, t);
	}

	@Override
	public default void info(Throwable t, String message) {
		LOGGER.info(MarkerManager.getMarker(name()), message, t);
	}

	@Override
	public default void warn(Throwable t, String message) {
		LOGGER.warn(MarkerManager.getMarker(name()), message, t);
	}

	@Override
	public default void error(Throwable t, String message) {
		LOGGER.error(MarkerManager.getMarker(name()), message, t);
	}

	@Override
	public default void fatal(Throwable t, String message) {
		LOGGER.fatal(MarkerManager.getMarker(name()), message, t);
	}
	
}
