package com.github.pfichtner.jrunalyser.ui.base;


public class DefaultGridData implements GridData {

	private final int gridX;
	private final int gridY;
	private final int gridWidth;
	private final int gridHeight;

	public DefaultGridData(int gridX, int gridY, int gridWidth, int gridHeight) {
		super();
		this.gridX = gridX;
		this.gridY = gridY;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
	}

	public int getGridX() {
		return this.gridX;
	}

	public int getGridY() {
		return this.gridY;
	}

	public int getGridWidth() {
		return this.gridWidth;
	}

	public int getGridHeight() {
		return this.gridHeight;
	}

}
