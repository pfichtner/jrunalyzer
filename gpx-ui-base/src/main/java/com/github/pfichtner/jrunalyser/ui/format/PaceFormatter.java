package com.github.pfichtner.jrunalyser.ui.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.Settings.PaceUnit;
import com.github.pfichtner.jrunalyser.ui.base.UiPlugins;

public class PaceFormatter {

	public enum Type {
		SHORT;
	}

	private final Type type;

	public PaceFormatter(Type type) {
		this.type = type;
	}

	public String format(Settings settings, Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Pace) {
			return format(settings, (Pace) object);
		} else if (object instanceof Speed) {
			PaceUnit pu = settings.getPaceUnit();
			Pace pace = ((Speed) object).toPace(pu.getTimeUnit(),
					pu.getDistanceUnit());
			return format(settings, pace);
		} else {
			throw new IllegalArgumentException("Cannot handle " + object + "(" //$NON-NLS-1$ //$NON-NLS-2$
					+ object.getClass() + ")"); //$NON-NLS-1$
		}

	}

	// http://stackoverflow.com/questions/266825/how-to-format-a-duration-in-java-e-g-format-hmmss
	private String format(Settings settings, Pace pace) {
		PaceUnit pu = settings.getPaceUnit();
		String key = this.type == Type.SHORT ? "pace.template$SHORT" //$NON-NLS-1$
				: "pace.template"; //$NON-NLS-1$
		String app = UiPlugins.getI18n().getText(key,
				UiPlugins.getI18n().getText(pu.getTimeUnit(), this.type),
				UiPlugins.getI18n().getText(pu.getDistanceUnit(), this.type));
		DateFormat df = new SimpleDateFormat("mm:ss"); //$NON-NLS-1$
		return df.format(Double.valueOf((long) pace.getValue(
				TimeUnit.MILLISECONDS, pace.getDistanceUnit()))) + " " + app; //$NON-NLS-1$
	}

}
