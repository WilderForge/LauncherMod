package com.wildermods.multimyth.mixin.branding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.badlogic.gdx.Graphics;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.worldwalkergames.legacy.ui.MainScreen;
import com.worldwalkergames.logging.ALogger;

@Mixin(MainScreen.class)
public class MainScreenBrandingMixin {

	@WrapOperation(
		method = "*", //intended to target before() and setTitle()
		at = @At (
			value = "INVOKE",
			target = "Lcom/badlogic/gdx/Graphics;setTitle(Ljava/lang/String;)V"
		)
	)
	private static void overwriteTitle(Graphics graphics, String title, Operation<Void> original) {
		original.call(graphics, "Multimyth");
	}
	
	@WrapOperation(
			method = "*",
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
