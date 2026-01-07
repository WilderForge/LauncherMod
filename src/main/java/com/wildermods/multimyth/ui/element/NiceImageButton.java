package com.wildermods.multimyth.ui.element;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.worldwalkergames.ui.NiceButton;
import com.worldwalkergames.ui.NiceLabel;

public class NiceImageButton<T> extends NiceButton<T> {
	protected Image image;
	protected Cell<Image> imageCell;
	
	protected Cell<NiceLabel> labelCell;
	
	public NiceImageButton(Image image, String text, Skin skin, String styleName) {
		this(image, text, skin, skin.get(styleName, NiceButton.Style.class));
	}
	
	public NiceImageButton(Image image, String text, Skin skin, NiceButton.Style style) {
		this(image, Align.left, text, skin, style);
	}
	
	public NiceImageButton(Image image, int imageSide, String text, Skin skin, String styleName) {
		this(image, imageSide, text, skin, skin.get(styleName, NiceButton.Style.class));
	}
	
	public NiceImageButton(Image image, int imageSide, String text, Skin skin, NiceButton.Style style) {
		super(text, skin, style);
		this.image = image;
		
		NiceLabel label = new NiceLabel(text, this.label.getStyle());
		label.setEllipsis(true);
		
		if(Align.isTop(imageSide)) {
			imageCell = this.add(image).align(imageSide);
			this.row();
			this.add(label).expandX().fillX();
		}
		else if(Align.isLeft(imageSide)) {
			imageCell = this.add(image).align(imageSide);
			this.add(label).expandX().fillX();
			label.setAlignment(imageSide);
		}
		else if(Align.isRight(imageSide)) {
			this.add(label).expandX().fillX();
			imageCell = this.add(image).align(imageSide);
			label.setAlignment(imageSide);
		}
		else if(Align.isBottom(imageSide)) {
			this.add(label).expandX().fillX();
			this.row();
			imageCell = this.add(image).align(imageSide);
		}
		
	}
	
	@Override
	public void build(String text) {
		//NO-OP
	}
	
	public Cell<Image> getImageCell() {
		return imageCell;
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public NiceLabel getLabel() {
		return label;
	}
	
	public void setLabel(NiceLabel label) {
		this.label = label;
	}
	
}
