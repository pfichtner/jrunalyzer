package com.github.pfichtner.jrunalyser.ui.tracklist;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.github.pfichtner.jrunalyser.ui.table.renderers.PositionRendererDecorator;

public class MedalRendererDecorator extends PositionRendererDecorator {

	private static final Color GOLD = new Color(201, 137, 16);
	private static final Color SILVER = new Color(168, 168, 168);
	private static final Color BRONZE = new Color(150, 90, 56);

	private static final Color[] colors = new Color[] { GOLD, SILVER, BRONZE };

	public MedalRendererDecorator(TableCellRenderer delegate) {
		super(delegate);
	}

	@Override
	protected void renderPos(JTable table, Component c, int pos) {
		c.setBackground(getColor(pos));
	}

	private static Color getColor(int pos) {
		return pos < colors.length ? colors[pos] : null;
	}

}
