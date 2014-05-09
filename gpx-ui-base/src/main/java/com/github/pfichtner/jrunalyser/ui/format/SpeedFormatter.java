package com.github.pfichtner.jrunalyser.ui.format;

import java.text.NumberFormat;

import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.Settings.SpeedUnit;
import com.github.pfichtner.jrunalyser.ui.base.UiPlugins;

public class SpeedFormatter {

	public enum Type {
		SHORT;
	}

	private final Type type;

	public SpeedFormatter(Type type) {
		this.type = type;
	}

	public String format(Settings settings, Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Speed) {
			return format(settings, (Speed) object);
		} else if (object instanceof Pace) {
			SpeedUnit su = settings.getSpeedUnit();
			Speed speed = ((Pace) object).toSpeed(su.getDistanceUnit(),
					su.getTimeUnit());
			return format(settings, speed);
		} else {
			throw new IllegalArgumentException("Cannot handle " + object + "(" //$NON-NLS-1$ //$NON-NLS-2$
					+ object.getClass() + ")"); //$NON-NLS-1$
		}

	}

	private String format(Settings settings, Speed speed) {
		SpeedUnit spu = settings.getSpeedUnit();
		double distance = speed.getValue(spu.getDistanceUnit(),
				spu.getTimeUnit());
		NumberFormat nf = NumberFormat.getNumberInstance();
		String key = this.type == Type.SHORT ? "speed.template$SHORT" //$NON-NLS-1$
				: "speed.template"; //$NON-NLS-1$
		String app = UiPlugins.getI18n().getText(key,
				UiPlugins.getI18n().getText(spu.getDistanceUnit(), this.type),
				UiPlugins.getI18n().getText(spu.getTimeUnit(), this.type));
		return nf.format(distance) + " " + app; //$NON-NLS-1$
	}

}
