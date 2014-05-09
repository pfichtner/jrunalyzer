package com.github.pfichtner.jrunalyser.ui.mapprofile.config;

import java.awt.Stroke;

import org.jfree.chart.renderer.xy.XYItemRenderer;

public class StrokeRendererConfigDecorator extends DatasetConfigDelegate {

	private final Stroke stroke;

	public StrokeRendererConfigDecorator(DatasetConfig delegate, Stroke stroke) {
		super(delegate);
		this.stroke = stroke;
	}

	@Override
	public XYItemRenderer getRenderer() {
		XYItemRenderer renderer = super.getRenderer();
		renderer.setSeriesStroke(0, this.stroke);
		return renderer;
	}

}
