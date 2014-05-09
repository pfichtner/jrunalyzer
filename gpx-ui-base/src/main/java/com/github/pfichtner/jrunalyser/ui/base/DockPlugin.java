package com.github.pfichtner.jrunalyser.ui.base;

import javax.swing.JComponent;

public interface DockPlugin extends UiPlugin {

	String getId();

	String getTitle();

	JComponent getPanel();

}
