package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wildermods.multimyth.Launcher;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;
import com.worldwalkergames.legacy.game.mission.InputActionBus;
import com.worldwalkergames.legacy.game.mission.InputActionBus.SimpleCallback;
import com.worldwalkergames.legacy.options.Keymap.Actions;
import com.worldwalkergames.legacy.ui.LegacyUIRoot;

@Mixin(LegacyUIRoot.class)
public class LegacyUIRootMixin {

	protected @Shadow LegacyViewDependencies dependencies;
	
	@Inject(
		method = "before()V",
		at = @At(value = "RETURN"),
		require = 1
	)
	public void setupDependencies(CallbackInfo c) throws ClassNotFoundException {
		Launcher.dependencies = dependencies;
	}
	
	@WrapOperation(
		method = "before",
		at = @At(
			value = "INVOKE",
			target = "Lcom/worldwalkergames/legacy/game/mission/InputActionBus;"
					+ "subscribe("
						+ "Ljava/lang/Object;"
						+ "Lcom/worldwalkergames/legacy/options/Keymap$Actions;"
						+ "Lcom/worldwalkergames/legacy/game/mission/InputActionBus$SimpleCallback;"
					+ ")V"
		)
	)
	private void disableFeedback(InputActionBus bus, Object owner, Actions action, SimpleCallback callback, Operation<Void> original) {
		if(action != Actions.global_giveFeedback) {
			original.call(bus, owner, action, callback);
		}
	}
}
