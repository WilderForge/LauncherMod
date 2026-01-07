package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

@Mixin(Skin.class)
public abstract class SkinMixin implements SkinAccessor {}
