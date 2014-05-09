package com.github.pfichtner.jrunalyser.ui.cal.swing;

import java.util.Calendar;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class DateComponent extends JComponent {

	private static final long serialVersionUID = 4027097258180321575L;

	private static final String uiClassID = "DateComponentUI"; //$NON-NLS-1$

	private final Calendar calendar = Calendar.getInstance();

	private final int num;
	private int dayOfMonth;

	private Icon icon;

	private Icon disabledIcon;

	private boolean isToday, isSelected, isSelectedMonth;

	private long date;

	public DateComponent(int num) {
		this.num = num;
		updateUI();
	}

	public int getDayOfMonth() {
		return this.dayOfMonth;
	}

	public long getDate() {
		return this.date;
	}

	public Icon getDisabledIcon() {
		return this.disabledIcon;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public int getNum() {
		return this.num;
	}

	public DateComponentUI getUI() {
		return (DateComponentUI) this.ui;
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public boolean isSelectedMonth() {
		return this.isSelectedMonth;
	}

	public boolean isToday() {
		return this.isToday;
	}

	public void setDate(long date) {
		this.date = date;
		// Swing is SingleThreaded so no need to synchronize
		this.calendar.setTimeInMillis(date);
		this.dayOfMonth = this.calendar.get(Calendar.DAY_OF_MONTH);
		revalidate();
		repaint();
	}

	public void setDisabledIcon(Icon disabledIcon) {
		this.disabledIcon = disabledIcon;
		revalidate();
		repaint();
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		revalidate();
		repaint();
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		revalidate();
		repaint();
	}

	public void setSelectedMonth(boolean isSelectedMonth) {
		this.isSelectedMonth = isSelectedMonth;
		revalidate();
		repaint();
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
		revalidate();
		repaint();
	}

	public void setUI(DateComponentUI ui) {
		super.setUI(ui);
	}

	public void updateUI() {
		if (UIManager.get(getUIClassID()) != null) {
			setUI((DateComponentUI) UIManager.getUI(this));
		} else {
			setUI(new BasicDateComponentUI());
		}
	}

}
