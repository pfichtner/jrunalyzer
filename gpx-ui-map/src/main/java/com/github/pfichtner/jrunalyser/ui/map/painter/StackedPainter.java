package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;

import org.jdesktop.swingx.painter.Painter;

import com.google.common.collect.ImmutableList;

public class StackedPainter<T> implements Painter<T> {

	private final List<Painter<T>> painters;

	public StackedPainter(Collection<Painter<T>> painters) {
		this.painters = ImmutableList.copyOf(painters);
	}

	@Override
	public void paint(Graphics2D g, T t, int w, int h) {
		for (Painter<T> painter : this.painters) {
			Graphics2D clone = (Graphics2D) g.create();
			painter.paint(clone, t, w, h);
			clone.dispose();
		}
	}

}
