package com.github.pfichtner.jrunalyser.ui.base.components;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.HighlightSegmentMessage;
import com.google.common.eventbus.EventBus;

public class SegmentTrackMouseListener extends MouseAdapter {

	private final SegmentationUnit segmentationUnit;

	private final EventBus eventBus;

	private Cursor oldPointer;

	public SegmentTrackMouseListener(SegmentationUnit segmentationUnit,
			EventBus eventBus) {
		this.segmentationUnit = segmentationUnit;
		this.eventBus = eventBus;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.oldPointer = ((Component) e.getSource()).getCursor();
		((Component) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.eventBus.post(new HighlightSegmentMessage(this.segmentationUnit));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		((Component) e.getSource()).setCursor(this.oldPointer);
	}

}