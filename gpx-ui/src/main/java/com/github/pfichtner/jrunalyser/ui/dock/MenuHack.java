package com.github.pfichtner.jrunalyser.ui.dock;

import static com.github.pfichtner.jrunalyser.base.data.DistanceUnit.KILOMETERS;
import static com.github.pfichtner.jrunalyser.base.data.DistanceUnit.METERS;
import static com.github.pfichtner.jrunalyser.base.data.DistanceUnit.MILES;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Distances;
import com.github.pfichtner.jrunalyser.base.data.DivideTrack;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Durations;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.HighlightSegmentMessage;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentationSelected;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter.Type;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public final class MenuHack {

	private static final I18N i18n = I18N
			.builder(MenuHack.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	private static I18N getI18n() {
		return i18n;
	}

	private final JMenu segMenu;
	private final JMenu hlMenu;

	private static final int[] parts = new int[] { 2, 3, 4, 5, 6, 8, 10, 12 };

	public MenuHack(EventBus eventBus) {
		eventBus.register(this);
		this.segMenu = initSegmentMenu(eventBus);
		this.hlMenu = initHighlightMenu(eventBus);
	}

	private static abstract class SegmentationUnitMenuItem extends
			JRadioButtonMenuItem {

		private static final long serialVersionUID = -8657815994237279399L;

		public abstract SegmentationUnit getSegmentationUnit();

	}

	private static class DistanceJRadioButtonMenuItem extends
			SegmentationUnitMenuItem {

		private static final long serialVersionUID = 6271439317839822421L;

		private final SegmentationUnit segmentationUnit;

		private static final DistanceFormatter df = new DistanceFormatter(
				DistanceFormatter.Type.SHORT);

		public DistanceJRadioButtonMenuItem(Distance segmentationUnit) {
			this.segmentationUnit = segmentationUnit;
			setText(df.format(segmentationUnit));
		}

		public DistanceJRadioButtonMenuItem(DivideTrack divideTrack) {
			this.segmentationUnit = divideTrack;
			setText(getI18n()
					.getText(
							"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.miDivideByDistance.title", //$NON-NLS-1$
							Integer.valueOf(divideTrack.getParts())));
		}

		@Override
		public SegmentationUnit getSegmentationUnit() {
			return this.segmentationUnit;
		}

	}

	private static class DurationJRadioButtonMenuItem extends
			SegmentationUnitMenuItem {

		private static final long serialVersionUID = 6271439317839822421L;

		private final SegmentationUnit segmentationUnit;

		private static final DurationFormatter df = new DurationFormatter(
				Type.SHORT_SYMBOLS);

		public DurationJRadioButtonMenuItem(Duration segmentationUnit) {
			this.segmentationUnit = segmentationUnit;
			setText(df.format(segmentationUnit));
		}

		public DurationJRadioButtonMenuItem(DivideTrack divideTrack) {
			this.segmentationUnit = divideTrack;
			setText(getI18n()
					.getText(
							"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.miDivideByTime.title", //$NON-NLS-1$
							Integer.valueOf(divideTrack.getParts())));
		}

		@Override
		public SegmentationUnit getSegmentationUnit() {
			return this.segmentationUnit;
		}
	}

	private static JMenu initSegmentMenu(final EventBus eventBus) {
		JMenu result = new JMenu(
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.mSegmentation.title")); //$NON-NLS-1$
		JMenu segDistanceMenu = new JMenu(
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.mDistance.title")); //$NON-NLS-1$
		ActionListener listenerDistance = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source instanceof JRadioButtonMenuItem) {
					JRadioButtonMenuItem item = (JRadioButtonMenuItem) source;
					if (item.isSelected()) {
						eventBus.post(new SegmentationSelected(
								item instanceof DistanceJRadioButtonMenuItem ? ((DistanceJRadioButtonMenuItem) item).segmentationUnit
										: null));
					}
				}
			}
		};
		ActionListener listenerTime = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source instanceof JRadioButtonMenuItem) {
					JRadioButtonMenuItem item = (JRadioButtonMenuItem) source;
					if (item.isSelected()) {
						eventBus.post(new SegmentationSelected(
								item instanceof DurationJRadioButtonMenuItem ? ((DurationJRadioButtonMenuItem) item).segmentationUnit
										: null));
					}
				}
			}
		};

		ButtonGroup buttonGroup = new ButtonGroup();
		segDistanceMenu
				.add(setSelected(register(
						new JRadioButtonMenuItem(
								getI18n()
										.getText(
												"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.off")), //$NON-NLS-1$
						buttonGroup, listenerDistance)));
		segDistanceMenu
				.add(register(
						new DistanceJRadioButtonMenuItem(DefaultDistance.of(
								500, METERS)), buttonGroup, listenerDistance));
		segDistanceMenu
				.add(register(
						new DistanceJRadioButtonMenuItem(DefaultDistance.of(
								0.5, MILES)), buttonGroup, listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(1, KILOMETERS)), buttonGroup,
				listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(1, MILES)), buttonGroup, listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(2, KILOMETERS)), buttonGroup,
				listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(2, MILES)), buttonGroup, listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(5, KILOMETERS)), buttonGroup,
				listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(5, MILES)), buttonGroup, listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(10, KILOMETERS)), buttonGroup,
				listenerDistance));
		segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
				DefaultDistance.of(10, MILES)), buttonGroup, listenerDistance));

		// --------------------------------------------------------------------

		for (int i = 0; i < parts.length; i++) {
			segDistanceMenu.add(register(new DistanceJRadioButtonMenuItem(
					new DivideTrack(parts[i], Distance.class)), buttonGroup,
					listenerDistance));
		}

		// --------------------------------------------------------------------

		JMenu segTimeMenu = new JMenu(getI18n().getText(
				"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.mTime.title")); //$NON-NLS-1$
		segTimeMenu
				.add(setSelected(register(
						new JRadioButtonMenuItem(
								getI18n()
										.getText(
												"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.off")), //$NON-NLS-1$
						buttonGroup, listenerTime)));
		segTimeMenu.add(register(new DurationJRadioButtonMenuItem(
				DefaultDuration.of(1, TimeUnit.MINUTES)), buttonGroup,
				listenerTime));
		segTimeMenu.add(register(new DurationJRadioButtonMenuItem(
				DefaultDuration.of(2, TimeUnit.MINUTES)), buttonGroup,
				listenerTime));
		segTimeMenu.add(register(new DurationJRadioButtonMenuItem(
				DefaultDuration.of(5, TimeUnit.MINUTES)), buttonGroup,
				listenerTime));
		segTimeMenu.add(register(new DurationJRadioButtonMenuItem(
				DefaultDuration.of(10, TimeUnit.MINUTES)), buttonGroup,
				listenerTime));

		// --------------------------------------------------------------------

		for (int i = 0; i < parts.length; i++) {
			segTimeMenu.add(register(new DurationJRadioButtonMenuItem(
					new DivideTrack(parts[i], Duration.class)), buttonGroup,
					listenerTime));
		}

		// --------------------------------------------------------------------

		result.add(segDistanceMenu);
		result.add(segTimeMenu);
		return result;
	}

	private static JMenu initHighlightMenu(final EventBus eventBus) {
		JMenu result = new JMenu(
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.mHighlight.title")); //$NON-NLS-1$
		JMenu hlDistance = new JMenu(
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.mDistance.title")); //$NON-NLS-1$
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source instanceof JRadioButtonMenuItem) {
					JRadioButtonMenuItem item = (JRadioButtonMenuItem) source;
					if (item.isSelected()) {
						eventBus.post(new HighlightSegmentMessage(
								item instanceof SegmentationUnitMenuItem ? ((SegmentationUnitMenuItem) item)
										.getSegmentationUnit() : null));
					}
				}
			}
		};

		ButtonGroup buttonGroup = new ButtonGroup();
		hlDistance
				.add(setSelected(register(
						new JRadioButtonMenuItem(
								getI18n()
										.getText(
												"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.off")), buttonGroup, //$NON-NLS-1$
						listener)));

		for (Distance distance : Distances.getDefaultDistances()) {
			hlDistance.add(register(new DistanceJRadioButtonMenuItem(distance),
					buttonGroup, listener));
		}

		JMenu hlTime = new JMenu(getI18n().getText(
				"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.mTime.title")); //$NON-NLS-1$
		hlTime.add(setSelected(register(
				new JRadioButtonMenuItem(getI18n().getText(
						"com.github.pfichtner.jrunalyser.ui.dock.MenuHack.off")), buttonGroup, //$NON-NLS-1$
				listener)));

		for (Duration duration : Durations.getDefaultDurations()) {
			hlTime.add(register(new DurationJRadioButtonMenuItem(duration),
					buttonGroup, listener));
		}

		result.add(hlDistance);
		result.add(hlTime);

		return result;
	}

	private static JRadioButtonMenuItem register(JRadioButtonMenuItem item,
			ButtonGroup buttonGroup, ActionListener listener) {
		item.addActionListener(listener);
		buttonGroup.add(item);
		return item;
	}

	private static JRadioButtonMenuItem setSelected(JRadioButtonMenuItem item) {
		item.setSelected(true);
		return item;
	}

	// --------------------------------------------------------------------------------

	public JMenu getSegmentMenu() {
		return this.segMenu;
	}

	public JMenu getHighlightMenu() {
		return this.hlMenu;
	}

	// --------------------------------------------------------------------------------

	@Subscribe
	public void setSegmenter(SegmentationSelected message) {
		// TODO null = off
		findAndSelect(message.getSegmentationUnit(), this.segMenu);
	}

	@Subscribe
	public void setHighlighter(HighlightSegmentMessage message) {
		// TODO null = off
		findAndSelect(message.getSegmentationUnit(), this.hlMenu);
	}

	private void findAndSelect(SegmentationUnit segmentationUnit, JMenu menu) {
		SegmentationUnitMenuItem menuItem = findMenuItem(segmentationUnit, menu);
		if (menuItem != null)
			menuItem.setSelected(true);
	}

	private SegmentationUnitMenuItem findMenuItem(
			SegmentationUnit segmentationUnit, JMenuItem start) {
		if (start instanceof JMenu) {
			JMenu jMenu = (JMenu) start;
			for (int i = 0; i < jMenu.getItemCount(); i++) {
				SegmentationUnitMenuItem result = findMenuItem(
						segmentationUnit, jMenu.getItem(i));
				if (result != null) {
					return result;
				}
			}
		} else if (start instanceof SegmentationUnitMenuItem) {
			SegmentationUnitMenuItem menuItem = (SegmentationUnitMenuItem) start;
			if (menuItem.getSegmentationUnit().equals(segmentationUnit)) {
				return menuItem;
			}
		}
		return null;
	}

}
