package com.wildermods.multimyth.ui.element;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;

public class AutoFocusingScrollPane extends ScrollPane2 {

	public AutoFocusingScrollPane(Actor actor, Skin skin) {
		super(actor, skin);
		this.addListener(new InputListener() {
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				Stage stage = getStage();
				if(stage != null) {
					getStage().setScrollFocus(AutoFocusingScrollPane.this);
				}
			}
			
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				Stage stage = getStage();
				if(stage != null) {
					getStage().setScrollFocus(AutoFocusingScrollPane.this);
				}
			}
		});
	}
	
}
