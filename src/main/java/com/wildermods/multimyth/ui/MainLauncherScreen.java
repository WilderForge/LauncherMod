package com.wildermods.multimyth.ui;

import com.badlogic.gdx.scenes.scene2d.ui.RuntimeSkin;
import com.wildermods.multimyth.Launcher;
import com.worldwalkergames.legacy.context.ClientDataContext;
import com.worldwalkergames.legacy.integations.SteamManager;
import com.worldwalkergames.legacy.ui.Screen;
import com.worldwalkergames.legacy.ui.titlescreen.ITopLevelScreen;
import com.worldwalkergames.scratchpad.context.MenuDependencies;
import com.worldwalkergames.scratchpad.ui.ScreenManager;

public class MainLauncherScreen extends Screen implements ITopLevelScreen {
	public MenuDependencies menuDependencies;
	public ScreenManager screenManager;
	public Screen currentScreen;
	
	public MainLauncherScreen() {
		super(Launcher.dependencies);
	}
	
	@Override
	public void before() {
		super.before();
		this.menuDependencies = new MenuDependencies(dependencies);
		RuntimeSkin scaleUI = dependencies.dataContext.getSkin(ClientDataContext.Skins.SCALE_UI);
		this.screenManager = new ScreenManager(this.dependencies.spriteBatch, scaleUI, menuDependencies.controllers, menuDependencies.screenInfo);
		this.driveChild(screenManager);
		this.dependencies.inputMultiplexer.addProcessor(screenManager.getStage());
		currentScreen = new LauncherSelectionScreen(dependencies);
		refreshScreen();
		SteamManager.setGameLauncherMode(true);
	}
	
	private void refreshScreen() {
		if(currentScreen != null && screenManager != null) {
			screenManager.clear();
		}
		
		this.dataContext.disposeCachedTextures();
		if(currentScreen != null) {
			this.screenManager.screenPush(currentScreen);
		}
	}
	
	@Override
	public void onResize() {
		super.onResize();
		if(screenManager != null) {
			screenManager.onResize();
		}
		if(currentScreen != null && currentScreen.isRunning()) {
			currentScreen.onResize();
		}
	}

}
