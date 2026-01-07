package com.wildermods.multimyth.ui.element;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class Grid extends WidgetGroup {
	private int cellSize;
	private Array<Actor> children;
	private float padding = 0;
	private float spacing = 0;
	
	public Grid(int cellSize) {
		this.cellSize = cellSize;
		this.children = new Array<>();
	}
	
	public Grid(int cellSize, float padding, float spacing) {
		this(cellSize);
		this.padding = padding;
		this.spacing = spacing;
	}
	
	@Override
	public void addActor(Actor actor) {
		super.addActor(actor);
		children.add(actor);
		invalidateHierarchy();
	}
	
	@Override
	public boolean removeActor(Actor actor) {
		boolean result = super.removeActor(actor);
		children.removeValue(actor, true);
		invalidateHierarchy();
		return result;
	}
	
	@Override
	public void clearChildren() {
		super.clearChildren();
		children.clear();
		invalidateHierarchy();
	}
	
	@Override
	public void layout() {
		float width = getWidth();
		float availableWidth = width - (padding * 2);

		int cols = Math.max(1, (int)((availableWidth + spacing) / (cellSize + spacing)));
		int rows = (int)Math.ceil((float)children.size / cols);

		float contentHeight =
			(padding * 2) + (rows * (cellSize + spacing)) - spacing;

		float baseY = getHeight() > 0 ? getHeight() : contentHeight;

		for (int i = 0; i < children.size; i++) {
			Actor child = children.get(i);
			int row = i / cols;
			int col = i % cols;

			float x = padding + col * (cellSize + spacing);
			float y = baseY - padding - (row + 1) * (cellSize + spacing);

			child.setBounds(x, y, cellSize, cellSize);
		}
	}

	
	@Override
	public float getPrefWidth() {
		return getWidth(); // Take whatever width is available
	}
	
	@Override
	public float getPrefHeight() {
		float width = getLayoutWidthForPref();

		float availableWidth = width - (padding * 2);
		int cols = Math.max(1, (int)((availableWidth + spacing) / (cellSize + spacing)));
		int rows = (int)Math.ceil((float)children.size / cols);

		return (padding * 2) + (rows * (cellSize + spacing)) - spacing;
	}
	
	private float getLayoutWidthForPref() {
		if (getParent() != null) {
			float parentWidth = getParent().getWidth();
			if (parentWidth > 0) return parentWidth;
		}
		return Math.max(getWidth(), getMinWidth());
	}
	
	@Override
	public float getMinWidth() {
		return cellSize + (padding * 2); // Minimum width is one cell plus padding
	}
	
	@Override
	public float getMinHeight() {
		return cellSize + (padding * 2); // Minimum height is one cell plus padding
	}
	
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
		invalidateHierarchy();
	}
	
	public int getCellSize() {
		return cellSize;
	}
	
	public void setPadding(float padding) {
		this.padding = padding;
		invalidateHierarchy();
	}
	
	public float getPadding() {
		return padding;
	}
	
	public void setSpacing(float spacing) {
		this.spacing = spacing;
		invalidateHierarchy();
	}
	
	public float getSpacing() {
		return spacing;
	}
}
