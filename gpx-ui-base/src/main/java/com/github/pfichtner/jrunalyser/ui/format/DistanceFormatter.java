package com.github.pfichtner.jrunalyser.ui.format;

import java.text.NumberFormat;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.ui.base.UiPlugins;

public class DistanceFormatter {

	public enum Type {
		SHORT;
	}

	private final Type type;

	public DistanceFormatter(Type type) {
		this.type = type;
	}

	public String format(Distance distance) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		DistanceUnit du = distance.getDistanceUnit();
		return nf.format(distance.getValue(du))
				+ UiPlugins.getI18n().getText(du, this.type);
	}

}
