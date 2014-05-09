package com.github.pfichtner.jrunalyser.ui.table.renderers;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

// TODO We should cache the column data but then we would have to implement TableModelListener for dropping these cached values
public abstract class PositionRendererDecorator implements TableCellRenderer,
		Cloneable {

	private final TableCellRenderer delegate;

	private static final Ordering<Comparable<?>> N = Ordering
			.<Comparable<?>> natural().reverse().nullsLast();
	private static final Ordering<Object> S = Ordering.usingToString()
			.reverse().nullsLast();

	private Ordering<Comparable<?>> natural = N;

	private Ordering<Object> string = S;

	public PositionRendererDecorator(TableCellRenderer delegate) {
		this.delegate = delegate;
	}

	public PositionRendererDecorator reverse() {
		try {
			PositionRendererDecorator clone = (PositionRendererDecorator) clone();
			clone.natural = this.natural.reverse().nullsLast();
			clone.string = this.string.reverse().nullsLast();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = this.delegate.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		if (value != null && !isSelected && getRowCount(table.getModel()) > 1) {
			renderPos(table, c, getPos(table, row, column));
		}

		return c;
	}

	protected abstract void renderPos(JTable table, Component c, int pos);

	private int getPos(JTable table, int row, int column) {
		int cnt = 0;
		Object value = table.getModel().getValueAt(
				table.convertRowIndexToModel(row),
				table.convertColumnIndexToModel(column));
		List<?> sorted = getSorted(table, column);
		for (Object object : sorted) {
			if (object == value || object.equals(value)) {
				return cnt;
			}
			cnt++;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private List<?> getSorted(JTable table, int col) {
		return (Comparable.class.isAssignableFrom(table.getColumnClass(col)) ? this.natural
				: this.string).sortedCopy(getValues(table, col));
	}

	@SuppressWarnings("rawtypes")
	private List getValues(JTable table, int col) {
		TableModel tm = table.getModel();
		int modelCol = table.convertColumnIndexToModel(col);
		int rowCount = getRowCount(tm);
		List<Object> rowValues = Lists.newArrayListWithCapacity(rowCount);
		for (int row = 0; row < rowCount; row++) {
			rowValues.add(tm.getValueAt(row, modelCol));
		}
		return rowValues;
	}

	public int getRowCount(TableModel tm) {
		return tm.getRowCount();
	}

}
