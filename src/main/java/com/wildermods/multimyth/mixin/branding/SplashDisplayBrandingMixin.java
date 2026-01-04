package com.wildermods.multimyth.mixin.branding;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.RuntimeSkin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wildermods.multimyth.ui.FancyBackground;
import com.worldwalkergames.legacy.context.ClientDataContext;
import com.worldwalkergames.legacy.splash.SplashDisplay;
import com.worldwalkergames.ui.FancyPanelDrawable;
import com.worldwalkergames.ui.layout.CanvasCell;
import com.worldwalkergames.ui.layout.CanvasGroup;

@Mixin(SplashDisplay.class)
public class SplashDisplayBrandingMixin {

	private @Shadow RuntimeSkin skin;
	private @Shadow ClientDataContext dataContext;
	private @Shadow CanvasGroup canvas;
	private @Unique Table backgroundTable = new Table();
	private @Shadow Table creditLine;
	private @Shadow Table statusTable;
	private @Shadow boolean skinChanged;
	private @Shadow Image bgImage;
	private @Mutable @Shadow Texture spiritTex;
	
	@WrapOperation(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "spiritTex",
			opcode = Opcodes.PUTFIELD
		),
		require = 4
	)
	private void replaceBrandingImage(SplashDisplay thiz, Texture texture, Operation<Void> original) {
		original.call(thiz, new FancyBackground(dataContext).get());
	}
	
	@WrapOperation(
		method = "<init>",
		at = @At(
			value = "NEW",
			target = "(Lcom/badlogic/gdx/graphics/g2d/TextureRegion;)Lcom/badlogic/gdx/scenes/scene2d/ui/Image;"
		)
	)
	public Image fixBackgroundImageScaling(TextureRegion region, Operation<Image> original, ClientDataContext context) {
		
		Image image = original.call(region);
		image.setScaling(Scaling.fit);
		
		return image;
	}
	
	@ModifyExpressionValue(
		method = "update",
		at = @At("MIXINEXTRAS:EXPRESSION")
	)
	@Definition(id = "creditLine", field = "creditLine")
	@Expression("this.creditLine != null")
	private boolean removeBranding(boolean original) {
		if(creditLine != null) {
			creditLine.clearChildren();
			skinChanged = false;
		}
		return false;
	}
	
	@WrapOperation(
		method = "update",
		at = @At(
			value = "FIELD",
			target = "statusTable",
			opcode = Opcodes.PUTFIELD
		)
	)
	private void addTableBackground(SplashDisplay thiz, Table table, Operation<Void> original) {
		canvas.add(backgroundTable).setLeft(0).setRight(0).setTop(0).setBottom(0);

		backgroundTable.add(table);
		statusTable = table;
		FancyPanelDrawable drawable = new FancyPanelDrawable(FancyPanelDrawable.Style.light);
		drawable.setPadding(15, 15, 15, 15);
		statusTable.setBackground(drawable);

	}
	
	@WrapOperation(
		method = "update",
		at = @At(
			value = "INVOKE",
			target = "Lcom/worldwalkergames/ui/layout/CanvasGroup;add",
			ordinal = 1
		)
	)
	private CanvasCell fixBackgroundTablePosition(CanvasGroup canvas, Actor statusTable, Operation<CanvasCell> original) {
		Button b = new Button();
		CanvasCell cell = canvas.add(b);
		canvas.removeActor(b);
		return cell;
	}
	
	@WrapOperation(
		method = "update",
		at = @At(
			value = "INVOKE",
			target = "Lcom/worldwalkergames/ui/layout/CanvasCell;setLeft",
			ordinal = 1
		)
	)
	public CanvasCell adjustBackgroundTableLeftness(CanvasCell cell, float width, Operation<CanvasCell> original) {
		return original.call(cell, Float.NaN);
	}
	
	@WrapOperation(
		method = "update",
		at = @At(
			value = "INVOKE",
			target = "Lcom/worldwalkergames/ui/layout/CanvasCell;setRight",
			ordinal = 1
		)
	)
	public CanvasCell adjustBackgroundTableRightness(CanvasCell cell, float width, Operation<CanvasCell> original) {
		return original.call(cell, Float.NaN);
	}
}
