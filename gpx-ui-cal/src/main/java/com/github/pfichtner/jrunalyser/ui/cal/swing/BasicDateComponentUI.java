package com.github.pfichtner.jrunalyser.ui.cal.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import sun.swing.SwingUtilities2;

@SuppressWarnings("restriction")
public class BasicDateComponentUI extends DateComponentUI {

	protected Border defaultBorder = BorderFactory.createMatteBorder(0, 1, 1,
			0, Color.BLACK);
	protected Border todayBorder = BorderFactory.createMatteBorder(1, 2, 2, 1,
			Color.BLACK);

	protected Rectangle paintIconR = new Rectangle();
	protected Rectangle paintTextR = new Rectangle();

	@Override
	public void paint(Graphics g, JComponent c) {
		DateComponent dc = (DateComponent) c;

		// ---

		boolean today = dc.isToday();
		boolean selected = dc.isSelected();
		boolean selectedMonth = dc.isSelectedMonth();

		boolean isNone = !today && !selected && !selectedMonth;
		if (selectedMonth) {
			fillContentArea(g, dc, Color.WHITE);
			drawBorder(g, dc, this.defaultBorder);
		}
		if (selected) {
			fillContentArea(g, dc, Color.LIGHT_GRAY);
			drawBorder(g, dc, this.defaultBorder);
		}
		if (today) {
			drawBorder(g, dc, this.todayBorder);
		}
		if (isNone) {
			drawBorder(g, dc, this.defaultBorder);
			g.setColor(Color.GRAY);
		}

		g.setColor(c.getForeground());
		g.setFont(c.getFont());
		// ---

		String text = getText(dc);
		boolean enabled = isEnabled(dc);
		Icon icon = enabled ? dc.getIcon() : dc.getDisabledIcon();

		if (icon == null && text == null) {
			return;
		}

		FontMetrics fm = SwingUtilities2.getFontMetrics(dc, g);
		String clippedText = layout(dc, fm, c.getWidth(), c.getHeight());

		if (icon != null) {
			icon.paintIcon(c, g, this.paintIconR.x, this.paintIconR.y);
		}

		if (text != null) {
			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null) {
				v.paint(g, this.paintTextR);
			} else {
				int textX = this.paintTextR.x;
				int textY = this.paintTextR.y + fm.getAscent();

				if (enabled) {
					paintEnabledText(dc, g, clippedText, textX, textY);
				} else {
					paintDisabledText(dc, g, clippedText, textX, textY);
				}
			}
		}
	}

	private void drawBorder(Graphics g, JComponent c, Border border) {
		// ((DateComponent) c).setBorder(border);
		// border.paintBorder(c, g, 0, 0, c.getWidth() - 1, c.getHeight() - 1);
		border.paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());

	}

	protected void fillContentArea(Graphics g, DateComponent b, Color background) {
		Insets margin = new Insets(2, 2, 2, 2);
		Insets insets = b.getInsets();
		Dimension size = b.getSize();
		g.setColor(background);
		g.fillRect(insets.left - margin.left, insets.top - margin.top,
				size.width - (insets.left - margin.left)
						- (insets.right - margin.right), size.height
						- (insets.top - margin.top)
						- (insets.bottom - margin.bottom));
	}

	protected String layout(DateComponent dc, FontMetrics fm, int width,
			int height) {
		Insets insets = dc.getInsets(null);
		String text = getText(dc);
		Icon icon = isEnabled(dc) ? dc.getIcon() : dc.getDisabledIcon();
		Rectangle paintViewR = new Rectangle();
		paintViewR.x = insets.left;
		paintViewR.y = insets.top;
		paintViewR.width = width - (insets.left + insets.right);
		paintViewR.height = height - (insets.top + insets.bottom);

		this.paintTextR.x = 3;
		this.paintTextR.y = 3;
		this.paintTextR.width = 0;
		this.paintTextR.height = 0;

		if (icon != null) {
			int gap = 5;
			int xx = fm.charWidth('X') * 2;
			this.paintIconR.x = this.paintTextR.x + xx + gap;
			this.paintIconR.y = (paintViewR.height - icon.getIconHeight()) / 2;
			this.paintIconR.width = 0;
			this.paintIconR.height = 0;
		}
		return text;
	}

	protected void paintEnabledText(DateComponent dc, Graphics g, String s,
			int textX, int textY) {
		SwingUtilities2.drawString(dc, g, s, textX, textY);
	}

	protected void paintDisabledText(DateComponent dc, Graphics g, String s,
			int textX, int textY) {
		Color background = dc.getBackground();
		g.setColor(background.brighter());
		SwingUtilities2.drawString(dc, g, s, textX + 1, textY + 1);
		g.setColor(background.darker());
		SwingUtilities2.drawString(dc, g, s, textX, textY);
	}

	// ---------------------------------------------------------------------

	protected boolean isEnabled(DateComponent dc) {
		return dc.isSelectedMonth();
	}

	protected String getText(DateComponent dc) {
		return String.valueOf(dc.getDayOfMonth());
	}

}
