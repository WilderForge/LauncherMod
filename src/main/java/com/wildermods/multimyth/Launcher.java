package com.wildermods.multimyth;

import java.nio.file.Path;

import com.google.gson.Gson;
import com.wildermods.multimyth.internal.GsonHelper;
import com.wildermods.provider.util.logging.Logger;
import com.wildermods.wilderforge.api.modLoadingV1.Mod;
import com.wildermods.wilderforge.api.modLoadingV1.event.PostInitializationEvent;
import com.wildermods.wilderforge.launch.WilderForge;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;
import com.worldwalkergames.scratchpad.context.MockClientContext;

import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(modid = "multimyth", version = "@modVersion@")
public class Launcher {
	
	public static final String MOD_ID = "launcher"; //If you change this value, be sure to also change it in src/main/resources/fabric.mod.json
	public static final String MOD_VERSION = "@modVersion@"; //the mod version is applied to the code at compile time. You can change the version of your mod by editing gradle.properties in the project's root directory
	public static final String BUILT_BY = "@builtBy@".equals("@" + "builtBy" + "@") ? "DEVELOPMENT VERSION" : "@builtBy@";
	public static final String BUILD_DATE = "@buildDate@";
	public static final String BUILD_INFO = BUILT_BY.equals("DEVELOPMENT VERSION") ? "DEVELOPMENT_VERSION" : "built by " + BUILT_BY + " on " + BUILD_DATE;
	public static final Path MULTIMYTH_DIR = Path.of(System.getProperty("user.home")).resolve("multimyth");
	public static final Path SAVE_DIR = MULTIMYTH_DIR.resolve("instances");
	public static final Gson GSON;
	static {
		GSON = GsonHelper.GSON_BUILDER.create();
	}
	
	public static final Logger LOGGER = new Logger(Launcher.class);
	
	public static LegacyViewDependencies dependencies;
	
	private Launcher(){};
	
	@SubscribeEvent
	public static void postInit(PostInitializationEvent e) {
		dependencies = WilderForge.getViewDependencies();
		dependencies.context.account.selectAccount(new MockClientContext.MockAccountClient(dependencies.dataContext).getAccount());
	}
	
}
