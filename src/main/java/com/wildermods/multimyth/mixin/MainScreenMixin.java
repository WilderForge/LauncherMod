package com.wildermods.multimyth.mixin;

import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wildermods.multimyth.ui.MainLauncherScreen;
import com.worldwalkergames.legacy.ui.MainScreen;
import com.worldwalkergames.legacy.ui.menu.RootMenuScreen;
import com.worldwalkergames.legacy.ui.titlescreen.ITopLevelScreen;

@Mixin(MainScreen.class)
public class MainScreenMixin {
	
	@WrapMethod(
		method = { "setContent" }
	)
	public void onSetInitialScreen(ITopLevelScreen content, Operation<Void> original) {
		if(content instanceof RootMenuScreen) {
			content = new MainLauncherScreen();
		}
		original.call(content);
		System.out.println(content.getClass().getSimpleName());
	}
	
}
