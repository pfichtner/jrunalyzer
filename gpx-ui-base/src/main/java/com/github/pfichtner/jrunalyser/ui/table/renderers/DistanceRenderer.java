package com.github.pfichtner.jrunalyser.ui.table.renderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;

public class DistanceRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private final DistanceFormatter formatter = new DistanceFormatter(
			DistanceFormatter.Type.SHORT);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setFont(null);
		setText(value == null ? null : this.formatter.format((Distance) value));
		setBackground(isSelected ? table.getSelectionBackground() : null);
		return this;
	}

}