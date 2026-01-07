package com.wildermods.multimyth;

import com.wildermods.provider.util.logging.Logger;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;

public class Launcher {
	
	public static final String MOD_ID = "launcher"; //If you change this value, be sure to also change it in src/main/resources/fabric.mod.json
	public static final String MOD_VERSION = "@modVersion@"; //the mod version is applied to the code at compile time. You can change the version of your mod by editing gradle.properties in the project's root directory
	public static final String BUILT_BY = "@builtBy@".equals("@" + "builtBy" + "@") ? "DEVELOPMENT VERSION" : "@builtBy@";
	public static final String BUILD_DATE = "@buildDate@";
	public static final String BUILD_INFO = BUILT_BY.equals("DEVELOPMENT VERSION") ? "DEVELOPMENT_VERSION" : "built by " + BUILT_BY + " on " + BUILD_DATE;
	
	public static final Logger LOGGER = new Logger(Launcher.class);
	
	public static LegacyViewDependencies dependencies;
	
	private Launcher(){};
	
}
