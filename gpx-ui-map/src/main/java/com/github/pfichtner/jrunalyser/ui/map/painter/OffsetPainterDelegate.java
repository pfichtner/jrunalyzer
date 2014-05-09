package com.github.pfichtner.jrunalyser.ui.map.painter;

import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

import com.github.pfichtner.jrunalyser.base.Delegate;

/**
 * Class that implements {@link Painter} that delegates to the passed Painter
 * but let it draw with the offset passed.
 * 
 * @author Peter Fichtner
 */
public class OffsetPainterDelegate implements Painter<JXMapViewer>,
		Delegate<Painter<JXMapViewer>> {

	private final Painter<JXMapViewer> delegate;
	private final int xOffset;
	private final int yOffset;

	public OffsetPainterDelegate(Painter<JXMapViewer> delegate, int xOffset,
			int yOffset) {
		this.delegate = delegate;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public Painter<JXMapViewer> getDelegate() {
		return this.delegate;
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer viewer, int x, int y) {
		g.translate(this.xOffset, this.yOffset);
		this.delegate.paint(g, viewer, x, y);
		g.translate(-this.xOffset, -this.yOffset);
	}

}
