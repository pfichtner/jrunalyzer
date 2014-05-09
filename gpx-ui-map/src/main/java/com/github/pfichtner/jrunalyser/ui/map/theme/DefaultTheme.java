package com.github.pfichtner.jrunalyser.ui.map.theme;

import java.awt.Color;

public class DefaultTheme implements Theme {

	private final Color bgColor;
	private final Color fgColor;
	private final Color hlColor;

	private DefaultTheme(Builder builder) {
		this.bgColor = builder.bgColor;
		this.fgColor = builder.fgColor;
		this.hlColor = builder.hlColor;
	}

	public Color getBgColor() {
		return this.bgColor;
	}

	public Color getFgColor() {
		return this.fgColor;
	}

	public Color getHlColor() {
		return this.hlColor;
	}

	public static class Builder {

		private Color bgColor = Color.ORANGE;
		private Color fgColor = Color.BLACK;
		private Color hlColor = Color.GREEN;

		public Theme build() {
			return new DefaultTheme(this);
		}

		public Builder bgColor(Color bgColor) {
			this.bgColor = bgColor;
			return this;
		}

		public Builder fgColor(Color fgColor) {
			this.fgColor = fgColor;
			return this;
		}

		public Builder hlColor(Color hlColor) {
			this.hlColor = hlColor;
			return this;
		}

	}

}
