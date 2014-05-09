package com.github.pfichtner.jrunalyser.ui.tracklist;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter.Type;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;

public class TableHeaderRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -8728929563494972224L;

	private final DistanceFormatter df = new DistanceFormatter(Type.SHORT);

	private final DurationFormatter du = new DurationFormatter(
			com.github.pfichtner.jrunalyser.ui.format.DurationFormatter.Type.SHORT_SYMBOLS);

	@Override
	protected void setValue(Object value) {
		if (value instanceof Duration) {
			super.setValue(this.du.format((Duration) value));
		} else if (value instanceof Distance) {
			super.setValue(this.df.format((Distance) value));
		} else
			super.setValue(value);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		JTableHeader tableHeader = table.getTableHeader();
		if (tableHeader != null) {
			setForeground(tableHeader.getForeground());
		}
		setIcon(getIcon(table, column));
		setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
		return this;
	}

	protected Icon getIcon(JTable table, int column) {
		RowSorter.SortKey sortKey = getSortKey(table, column);
		if (sortKey != null
				&& table.convertColumnIndexToView(sortKey.getColumn()) == column) {
			switch (sortKey.getSortOrder()) {
			case ASCENDING:
				return UIManager.getIcon("Table.ascendingSortIcon"); //$NON-NLS-1$
			case DESCENDING:
				return UIManager.getIcon("Table.descendingSortIcon"); //$NON-NLS-1$
			}
		}
		return null;
	}

	protected RowSorter.SortKey getSortKey(JTable table, int column) {
		RowSorter<? extends TableModel> rowSorter = table.getRowSorter();
		if (rowSorter == null) {
			return null;
		}
		List<? extends RowSorter.SortKey> sortedColumns = rowSorter
				.getSortKeys();
		return sortedColumns.isEmpty() ? null : sortedColumns.get(0);
	}
}
