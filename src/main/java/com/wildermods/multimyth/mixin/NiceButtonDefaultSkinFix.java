package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wildermods.multimyth.Type;
import com.worldwalkergames.ui.NiceButton;

@Mixin(NiceButton.class)
public class NiceButtonDefaultSkinFix {

	@WrapOperation(
		method = "<init>("
					+ "Ljava/lang/String;"
					+ "Lcom/badlogic/gdx/scenes/scene2d/ui/Skin;"
				+ ")V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/badlogic/gdx/scenes/scene2d/ui/Skin;"
						+ "get("
							+ "Ljava/lang/Class;"
						+ ")"
						+ "Ljava/lang/Object;"
		)
	)
	private static @Type(Skin.class) Object setDefaultStyle(Skin skin, Class<Skin> type, Operation<Void> original) {
		return skin.get("mediumDialogButton", type);
	}
	
}
