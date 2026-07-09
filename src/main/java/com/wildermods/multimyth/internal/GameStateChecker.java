package com.wildermods.multimyth.internal;

import com.wildermods.wilderforge.api.mixins.v1.Cast;

import com.worldwalkergames.legacy.integations.SteamManager;
import com.wildermods.wilderforge.mixins.integrations.SteamManagerAccessor;

public class GameStateChecker {
	
	public static boolean gameInstanceOnClasspath() {
		try {
			//LegacyDesktop.class.getClass();
			return true;
		}
		catch(Throwable t) {
			return false;
		}
	}
	
	public static boolean steamworksAvailable() {
		if(gameInstanceOnClasspath()) {
			SteamManagerAccessor steamAccessor = Cast.from(new SteamManager());
			return SteamManager.launcherDetected && SteamManager.isConnectedToSteam() && steamAccessor.getSteamFriends() != null;
		}
		return false;
	}
	
}
