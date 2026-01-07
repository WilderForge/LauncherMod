package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

@Mixin(Skin.class)
public interface SkinAccessor {

	public @Accessor float getScale();
	
}
