package com.github.pfichtner.jrunalyser.ui.map.theme;

import java.awt.Color;

public interface Theme {

	/**
	 * Returns the background color.
	 * 
	 * @return background color
	 */
	Color getBgColor();

	/**
	 * Returns the foreground color.
	 * 
	 * @return foreground color
	 */
	Color getFgColor();

	/**
	 * Returns the color used for highlights such as fasted segments.
	 * 
	 * @return highlight color
	 */
	Color getHlColor();

}
