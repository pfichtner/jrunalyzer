package com.github.pfichtner.jrunalyser.ui.base.components;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.google.common.eventbus.EventBus;

public class LoadTrackMouseListener extends MouseAdapter {

	private final Track track;

	private final EventBus eventBus;

	private Cursor oldPointer;

	public LoadTrackMouseListener(Track track, EventBus eventBus) {
		this.track = track;
		this.eventBus = eventBus;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.oldPointer = ((Component) e.getSource()).getCursor();
		((Component) e.getSource())
				.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.eventBus.post(new TrackLoaded(this.track));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		((Component) e.getSource()).setCursor(this.oldPointer);
	}

}