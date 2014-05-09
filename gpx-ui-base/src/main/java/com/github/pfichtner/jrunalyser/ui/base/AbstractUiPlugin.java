package com.github.pfichtner.jrunalyser.ui.base;

public abstract class AbstractUiPlugin implements DockPlugin, GridDataProvider {

	private static final GridData gridData = new DefaultGridData(1, 3, 3, 1);

	@Override
	public String getId() {
		return getClass().getName();
	}

	@Override
	public GridData getGridData() {
		return gridData;
	}

}
