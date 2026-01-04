package com.wildermods.multimyth;

import com.wildermods.provider.util.logging.Logger;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;

public class Launcher {
	
	public static final String MOD_ID = "launcher"; //If you change this value, be sure to also change it in src/main/resources/fabric.mod.json
	public static final String MOD_VERSION = "@modVersion@"; //the mod version is applied to the code at compile time. You can change the version of your mod by editing gradle.properties in the project's root directory
	public static final String BUILD_INFO = "@builtBy@".equals("@" + "builtBy" + "@") ? "DEVELOPMENT VERSION" : "built by @builtBy@ on @buildDate@";
	
	public static final Logger LOGGER = new Logger(Launcher.class);
	
	public static LegacyViewDependencies dependencies;
	
	private Launcher(){};
	
}
