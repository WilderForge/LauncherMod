package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.worldwalkergames.legacy.game.mission.InputActionBus;
import com.worldwalkergames.legacy.game.mission.InputActionBus.SimpleCallback;
import com.worldwalkergames.legacy.options.Keymap.Actions;
import com.worldwalkergames.legacy.ui.LegacyUIRoot;

@Mixin(LegacyUIRoot.class)
public class LegacyUIRootMixin {
	
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
