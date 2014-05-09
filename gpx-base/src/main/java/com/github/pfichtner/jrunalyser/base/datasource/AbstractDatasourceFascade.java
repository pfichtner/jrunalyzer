package com.github.pfichtner.jrunalyser.base.datasource;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A DatasourceFascade that implements the methods for
 * {@link DatasourceFascadeListener}s.
 * 
 * @author Peter Fichtner
 */
public abstract class AbstractDatasourceFascade implements DatasourceFascade {

	private final List<DatasourceFascadeListener> listeners = new CopyOnWriteArrayList<DatasourceFascadeListener>();

	protected void fire(DatasourceFascadeEvent event) {
		for (DatasourceFascadeListener listener : this.listeners) {
			listener.contentChanged(event);
		}
	}

	@Override
	public void addListener(DatasourceFascadeListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(DatasourceFascadeListener listener) {
		this.listeners.remove(listener);
	}

}
