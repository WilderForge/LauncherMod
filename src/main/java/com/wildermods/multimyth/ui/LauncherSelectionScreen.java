package com.wildermods.multimyth.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;
import com.worldwalkergames.legacy.ui.Screen;
import com.worldwalkergames.ui.NiceButton;
import com.worldwalkergames.ui.layout.CanvasGroup;

public class LauncherSelectionScreen extends Screen {

	protected LauncherSelectionScreen(LegacyViewDependencies dependencies) {
		super(dependencies);
		
		dependencies.dataContext.calculateConnections(dependencies);
	}
	
	@Override
	public void before() {
		super.before();
		dependencies.skin.skinChanged.add(this, this::build);
		build();
	}
	
	@Override
	public void after() {
		dependencies.skin.skinChanged.remove(this);
		super.after();
	}
	
	private void build() {
		this.getGroup().clearChildren();
		CanvasGroup canvasGroup = new CanvasGroup();
		canvasGroup.setFillParent(true);
		
		Table mainTable = new Table(this.skin);
		mainTable.setFillParent(true);
		
		
		//TODO: build UI

		
		canvasGroup.add(mainTable);
		
		Texture c = dependencies.dataContext.loadTexture("assets/ui/creditsBG_1920x1080.jpg", false);
		TextureRegionDrawable drawable = new TextureRegionDrawable(c);
		mainTable.setBackground(drawable);
		


		canvasGroup.invalidate();
		this.addActor(canvasGroup);
	}
}
