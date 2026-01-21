package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.wildermods.multimyth.internal.Steam;
import com.worldwalkergames.legacy.integations.SteamManager;

@Mixin(SteamManager.class)
public class SteamManagerMixin implements Steam {

	@Override
	public String username() {
		return SteamManager.getUserPersonaName();
	}

}
