package com.github.pfichtner.jrunalyser.ui.base;

import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;

public final class UiPlugins {

	private static final I18N i18n = I18N.builder(AbstractUiPlugin.class)
			.build();

	private UiPlugins() {
		super();
	}

	public static I18N getI18n() {
		return i18n;
	}

}
