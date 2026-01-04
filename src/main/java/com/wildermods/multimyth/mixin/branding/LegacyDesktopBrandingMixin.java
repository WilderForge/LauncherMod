package com.wildermods.multimyth.mixin.branding;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.worldwalkergames.legacy.LegacyDesktop;
import com.worldwalkergames.legacy.Version;
import com.worldwalkergames.logging.ALogger;

@Mixin(LegacyDesktop.class)
public class LegacyDesktopBrandingMixin {
	
	@Inject(
		method = "main",
		at = @At(
			value = "FIELD",
			target = "Lcom/worldwalkergames/legacy/integations/SteamManager;"
					+ "launcherDetected",
			opcode = Opcodes.PUTSTATIC,
			shift = Shift.BEFORE
		)
	)
	private static void setAppInfo(CallbackInfo c) {
		Version.APP_ID = "multimyth";
		Version.VERSION = "@modVersion@";
		if("@patchline@".equals("@" + "patchline@")) {
			Version.PATCHLINE = "git - development";
		}
		else {
			Version.PATCHLINE = "Wilderforge Org";
		}
	}
	
	@WrapOperation(
		method = "main",
		at = @At (
			value = "INVOKE",
			target = "Lcom/badlogic/gdx/backends/lwjgl3/Lwjgl3ApplicationConfiguration;"
					+ "setTitle("
						+ "Ljava/lang/String;"
					+ ")V"
		)
	)
	private static void setTitle(Lwjgl3ApplicationConfiguration config, String title, Operation<Void> original) {
		original.call(config, "Multimyth");
	}
	
	@WrapOperation(
		method = "create",
		at = @At (
			value = "INVOKE",
			target = "Lcom/badlogic/gdx/Graphics;"
					+ "setTitle("
						+ "Ljava/lang/String;"
					+ ")V"
		)
	)
	private static void setTitle(Graphics graphics, String title, Operation<Void> original) {
		original.call(graphics, "Multimyth");
	}
	
	@WrapOperation(
		method = "main",
		at = {
			@At(
				value = "INVOKE",
				target = "Lcom/worldwalkergames/logging/ALogger;log0"
			),
			@At(
				value = "INVOKE",
				target = "Lcom/worldwalkergames/logging/ALogger;log1"
			),
			@At(
				value = "INVOKE",
				target = "Lcom/worldwalkergames/logging/ALogger;log2"
			),
			@At(
				value = "INVOKE",
				target = "Lcom/worldwalkergames/logging/ALogger;log3"
			),
			@At(
				value = "INVOKE",
				target = "Lcom/worldwalkergames/logging/ALogger;log4"
			),
			@At(
				value = "INVOKE",
				target = "Lcom/worldwalkergames/logging/ALogger;log5"
			)
		}
	)
	private static void fixApplicationNameInLogs(ALogger instance, String message, Object[] parameters, Operation<Void> original) {
		message = message.replace("Wildermyth", "Multimyth");
		for(int i = 0; i < parameters.length; i++) {
			if(parameters[i] instanceof String) {
				parameters[i] = ((String)parameters[i]).replace("Wildermyth", "Multimyth");
			}
		}
		original.call(instance, message, parameters);
	}
	
}
