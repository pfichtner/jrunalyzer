package com.github.pfichtner.jrunalyser.ui.map;

import static com.github.pfichtner.jrunalyser.ui.map.util.GeoUtil.calcCenter;
import static com.github.pfichtner.jrunalyser.ui.map.util.GeoUtil.calcMaxZoomLevel;
import static com.github.pfichtner.jrunalyser.ui.map.util.GeoUtil.CenterType.BOXPLOT_AVERAGE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapKit.DefaultProviders;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;
import org.jdesktop.swingx.painter.Painter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenter;
import com.github.pfichtner.jrunalyser.base.data.segmenter.Segmenters;
import com.github.pfichtner.jrunalyser.base.data.stat.Orderings;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.DefaultGridData;
import com.github.pfichtner.jrunalyser.ui.base.GridData;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.AdditionalTracks;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.HighlightSegmentMessage;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.MouseOverWaypoint;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.PositionSelected;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentSelectedMessage;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentationSelected;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.map.painter.HighlightTrackPainter;
import com.github.pfichtner.jrunalyser.ui.map.painter.InfoBoxPainter;
import com.github.pfichtner.jrunalyser.ui.map.painter.MarkedWaypointPainter;
import com.github.pfichtner.jrunalyser.ui.map.painter.OffsetPainterDelegate;
import com.github.pfichtner.jrunalyser.ui.map.painter.OffsetWaypointRendererDelegate;
import com.github.pfichtner.jrunalyser.ui.map.painter.SegmentBorderWaypointPainter;
import com.github.pfichtner.jrunalyser.ui.map.painter.StackedPainter;
import com.github.pfichtner.jrunalyser.ui.map.painter.TrackOutlinePainter;
import com.github.pfichtner.jrunalyser.ui.map.painter.TrackPainter;
import com.github.pfichtner.jrunalyser.ui.map.theme.DefaultTheme;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;
import com.github.pfichtner.jrunalyser.ui.map.util.GeoUtil;
import com.github.pfichtner.jrunalyser.ui.map.wp.SegmentBorderWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wp.SelectedWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wp.TrackEndWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wp.TrackStartWaypoint;
import com.github.pfichtner.jrunalyser.ui.map.wprenderer.SegmentBorderWaypointRendererEnd;
import com.github.pfichtner.jrunalyser.ui.map.wprenderer.SegmentBorderWaypointRendererPoint;
import com.github.pfichtner.jrunalyser.ui.map.wprenderer.SegmentBorderWaypointRendererStart;
import com.github.pfichtner.jrunalyser.ui.map.wprenderer.SelectedWaypointRenderer;
import com.github.pfichtner.jrunalyser.ui.map.wprenderer.WaypointRendererDelegate;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MapViewPlugin extends AbstractUiPlugin implements GridDataProvider {

	private static final Logger log = LoggerFactory
			.getLogger(MapViewPlugin.class);

	private static final GridData gridData = new DefaultGridData(1, 0, 3, 3);

	private final static int stroke = 3;

	private static final Theme theme = new DefaultTheme.Builder()
			.bgColor(new Color(53, 187, 240)).fgColor(Color.WHITE)
			.hlColor(new Color(53, 187, 240).darker()).build();

	private static final I18N i18n = I18N
			.builder(MapViewPlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	private final JXMapKit mapKit;

	private EventBus eventBus;

	private WaypointPainter<JXMapViewer> waypointPainter;

	private Track track;
	private List<AdditonalTrackInfo> additionalTracks = Lists.newArrayList();

	private SegmentationUnit segmentationUnit;

	private Segmenter highlighter = Segmenter.NULL_SEGMENTER;
	private Segment activeSegment;

	private boolean autoCenter = true;
	private boolean autoZoom = true;

	private List<Theme> themeStack = Arrays.asList(
			new DefaultTheme.Builder().bgColor(Color.ORANGE)
					.fgColor(Color.BLACK).hlColor(Color.ORANGE.brighter())
					.build(), new DefaultTheme.Builder().bgColor(Color.GREEN)
					.fgColor(Color.BLACK).hlColor(Color.GREEN.brighter())
					.build(), new DefaultTheme.Builder().bgColor(Color.YELLOW)
					.fgColor(Color.BLACK).hlColor(Color.YELLOW.brighter())
					.build());

	private int offsetStep;

	private boolean infoBoxEnabled = true;

	private DatasourceFascade dsf;

	public MapViewPlugin() {
		this.mapKit = new JXMapKit();
		this.mapKit.setDefaultProvider(DefaultProviders.OpenStreetMaps);
		this.mapKit.getMainMap().addMouseListener(createMouseListener());
		this.mapKit.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent ev) {
				centerToTrack();
				zoomToTrack();
			}
		});
	}

	public void setOffsetStep(int offsetStep) {
		this.offsetStep = offsetStep;
	}

	private MouseListener createMouseListener() {
		return new MouseAdapter() {
			JPopupMenu popupMenu = createPopupMenu();

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					GeoPosition position = getMouseLoc(e);
					if (e.getClickCount() == 2) {
						JXMapViewer mk = ((JXMapViewer) e.getSource());
						mk.setZoom(mk.getZoom() - 1);
						mk.setCenterPosition(position);
					}
					MapViewPlugin.this.eventBus.post(new PositionSelected(
							position.getLatitude(), position.getLongitude()));
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			private GeoPosition getMouseLoc(MouseEvent e) {
				JXMapViewer viewer = ((JXMapViewer) e.getSource());
				Rectangle viewportBounds = viewer.getViewportBounds();
				return viewer.getTileFactory().pixelToGeo(
						new Point(viewportBounds.x + e.getX(), viewportBounds.y
								+ e.getY()), viewer.getZoom());
			}

			private JPopupMenu createPopupMenu() {
				JPopupMenu popupMenu = new JPopupMenu();

				JMenuItem ztt = new JMenuItem(
						i18n.getText("com.github.pfichtner.jrunalyser.ui.map.MapViewPlugin.centerAndZoom")); //$NON-NLS-1$
				ztt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						forceZoomToTrack();
						forceCenterToTrack();
					}
				});

				JCheckBoxMenuItem ac = new JCheckBoxMenuItem(
						i18n.getText("com.github.pfichtner.jrunalyser.ui.map.MapViewPlugin.autocenter"), //$NON-NLS-1$
						MapViewPlugin.this.autoCenter);
				ac.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						MapViewPlugin.this.autoCenter = ((JCheckBoxMenuItem) e
								.getSource()).isSelected();

					}
				});

				JCheckBoxMenuItem az = new JCheckBoxMenuItem(
						i18n.getText("com.github.pfichtner.jrunalyser.ui.map.MapViewPlugin.autozoom"), //$NON-NLS-1$
						MapViewPlugin.this.autoZoom);
				az.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						MapViewPlugin.this.autoZoom = ((JCheckBoxMenuItem) e
								.getSource()).isSelected();

					}
				});

				popupMenu.add(ztt);
				popupMenu.add(ac);
				popupMenu.add(az);
				return popupMenu;
			}

		};
	}

	@Override
	public String getTitle() {
		return i18n
				.getText("com.github.pfichtner.jrunalyser.ui.map.MapViewPlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JPanel getPanel() {
		return this.mapKit;
	}

	@Override
	public GridData getGridData() {
		return gridData;
	}

	@Subscribe
	public void setTrackLoaded(TrackLoaded message) {
		// reset track dependent attributes
		this.activeSegment = null;
		this.eventBus
				.post(new AdditionalTracks(Collections.<Track> emptyList()));
		setTrack(message.getTrack());
	}

	public void setTrack(Track track) {
		init(track);
		centerToTrack();
		zoomToTrack();
	}

	private void centerToTrack() {
		if (this.autoCenter) {
			forceCenterToTrack();
		}
	}

	private void zoomToTrack() {
		if (this.autoZoom) {
			forceZoomToTrack();
		}
	}

	private void forceCenterToTrack() {
		if (MapViewPlugin.this.track != null) {
			this.mapKit.setCenterPosition(calcCenter(this.track));
		}
	}

	private void forceZoomToTrack() {
		if (MapViewPlugin.this.track != null) {
			this.mapKit.setZoom(calcMaxZoomLevel(this.mapKit, this.track));
		}
	}

	private void init(Track track) {
		Segmenter segmenter = this.segmentationUnit == null ? Segmenter.NULL_SEGMENTER
				: Segmenters.getSegmenter(this.segmentationUnit);

		this.track = segmenter.segment(checkNotNull(track, "Track is null")); //$NON-NLS-1$
		Theme trackTheme = theme;

		this.waypointPainter = new SegmentBorderWaypointPainter(this.track,
				this.segmentationUnit);

		this.waypointPainter.setRenderer(new WaypointRendererDelegate(
				createWaypointRenderers(trackTheme)));

		Font font = new Font(Font.SANS_SERIF, 0, 12);
		List<Segment> activeSegment = this.activeSegment == null ? Collections
				.<Segment> emptyList() : Collections
				.singletonList(this.activeSegment);

		List<Painter<JXMapViewer>> painters = Lists.newArrayList();
		int cnt = 0;
		for (AdditonalTrackInfo adti : this.additionalTracks) {
			Track addTrack = segmenter.segment(adti.getTrack());
			WaypointPainter<JXMapViewer> painter = new SegmentBorderWaypointPainter(
					addTrack, this.segmentationUnit);
			painter.setRenderer(new WaypointRendererDelegate(proxyAll(
					adti.getXoffset(), adti.getYoffset(),
					createWaypointRenderers(adti.getTheme()))));
			painters.addAll(ImmutableList.of(
					new OffsetPainterDelegate(new TrackOutlinePainter(addTrack,
							adti.getTheme(), stroke), adti.getXoffset(), adti
							.getYoffset()),
					new OffsetPainterDelegate(new TrackPainter(addTrack, adti
							.getTheme(), stroke), adti.getXoffset(), adti
							.getYoffset()),
					new OffsetPainterDelegate(new HighlightTrackPainter(
							this.highlighter.segment(addTrack),
							adti.getTheme(), stroke), adti.getXoffset(), adti
							.getYoffset()),
					painter,
					new OffsetPainterDelegate(new HighlightTrackPainter(
							new DefaultTrack(track.getId(),
									track.getMetadata(), track.getWaypoints(),
									activeSegment, track.getStatistics()), adti
									.getTheme(), stroke), adti.getXoffset(),
							adti.getYoffset()), painter));
			if (this.infoBoxEnabled) {
				painters.add(new OffsetPainterDelegate(new InfoBoxPainter(
						addTrack, adti.getTheme(), font), 0, (++cnt) * 25));
			}

		}

		painters.addAll(ImmutableList.of(
				new TrackOutlinePainter(this.track, trackTheme, stroke),
				new TrackPainter(this.track, trackTheme, stroke),
				new MarkedWaypointPainter(this.dsf, track),
				new HighlightTrackPainter(this.highlighter.segment(this.track),
						trackTheme, stroke),
				this.waypointPainter,
				new HighlightTrackPainter(new DefaultTrack(track.getId(), track
						.getMetadata(), track.getWaypoints(), activeSegment,
						track.getStatistics()), trackTheme, stroke),
				this.waypointPainter));
		if (this.infoBoxEnabled) {
			painters.add(new InfoBoxPainter(this.track, trackTheme, font));
		}

		this.mapKit.getMainMap().setOverlayPainter(
				new StackedPainter<JXMapViewer>(Orderings.classTypeOrdering(
						ImmutableList.of(TrackOutlinePainter.class,
								TrackPainter.class,
								HighlightTrackPainter.class,
								WaypointPainter.class,
								SegmentBorderWaypointPainter.class,
								InfoBoxPainter.class)).sortedCopy(painters)));
	}

	private Map<Class<? extends Waypoint>, WaypointRenderer> proxyAll(
			final int xOffset, final int yOffset,
			Map<Class<? extends Waypoint>, WaypointRenderer> map) {
		Function<WaypointRenderer, WaypointRenderer> f = new Function<WaypointRenderer, WaypointRenderer>() {
			@Override
			public WaypointRenderer apply(WaypointRenderer renderer) {
				return new OffsetWaypointRendererDelegate(xOffset, yOffset,
						renderer);
			}
		};
		return ImmutableMap.copyOf(Maps.transformValues(map, f));
	}

	@Subscribe
	public void setSegmenter(SegmentationSelected message) {
		this.segmentationUnit = message.getSegmentationUnit();
		if (this.track != null) {
			init(this.track);
			this.mapKit.repaint();
		}
	}

	@Subscribe
	public void setHighlighter(HighlightSegmentMessage message) {
		this.highlighter = Segmenters.getFloatingSegmenter(message
				.getSegmentationUnit());
		if (this.track != null) {
			init(this.track);
			this.mapKit.repaint();
		}
	}

	@Subscribe
	public void setActiveSegment(SegmentSelectedMessage message) {
		this.activeSegment = message.getSegment();
		if (this.track != null) {
			init(this.track);
			this.mapKit.repaint();
		}
	}

	@Subscribe
	public void setActiveWaypoint(MouseOverWaypoint mouseOverWaypoint) {
		if (this.track != null
				&& this.track.getId().equals(
						mouseOverWaypoint.getTrack().getId())) {
			GeoPosition geoPos = GeoUtil.toGeoPoint(mouseOverWaypoint
					.getSelectedWayPoint());
			Set<Waypoint> wps = this.waypointPainter.getWaypoints();
			Set<SelectedWaypoint> selected = Sets.newHashSet(filter(wps,
					SelectedWaypoint.class));
			// TODO use binary search!?
			SelectedWaypoint first = getFirst(selected, null);
			if (first == null) {
				wps.add(new SelectedWaypoint(geoPos));
			} else {
				first.setPosition(geoPos);
			}
			this.mapKit.getMainMap().repaint();
		}
	}

	@Subscribe
	public void additionalTrack(AdditionalTracks msg) {
		this.additionalTracks.clear();
		int offset = 0;
		for (Track track : msg.getTracks()) {
			offset += this.offsetStep;
			Theme theme = Iterables.get(this.themeStack,
					this.additionalTracks.size(),
					Iterables.getLast(this.themeStack));
			this.additionalTracks.add(new AdditonalTrackInfo(track, theme,
					offset, offset));
		}
		if (this.track != null) {
			init(this.track);
		}
	}

	private Map<Class<? extends Waypoint>, WaypointRenderer> createWaypointRenderers(
			Theme theme) {
		Font font = new Font(Font.SANS_SERIF, 0, 12);
		return ImmutableMap.<Class<? extends Waypoint>, WaypointRenderer> of(
				TrackStartWaypoint.class,
				new SegmentBorderWaypointRendererStart(theme, font),
				SegmentBorderWaypoint.class,
				new SegmentBorderWaypointRendererPoint(theme, font),
				TrackEndWaypoint.class, new SegmentBorderWaypointRendererEnd(
						theme, font), SelectedWaypoint.class,
				new SelectedWaypointRenderer(theme, font));
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Inject
	public void setDatasourceFascade(DatasourceFascade dsf) throws IOException {
		this.dsf = dsf;
		log.debug("Calculating center of tracks"); //$NON-NLS-1$
		GeoPosition center = calcCenter(dsf.loadTracks(dsf.getTrackIds()),
				BOXPLOT_AVERAGE);
		log.info("Center of tracks calculated ({})", center); //$NON-NLS-1$
		this.mapKit.setCenterPosition(center);
		this.mapKit.setAddressLocationShown(false);

		TileFactoryInfo info = this.mapKit.getMainMap().getTileFactory()
				.getInfo();
		int minZoom = info.getMinimumZoomLevel();
		int maxZoom = info.getMaximumZoomLevel();
		this.mapKit.setZoom(Math.min(minZoom + 4, maxZoom));
	}

	public void setMiniMapVisible(boolean miniMapVisible) {
		this.mapKit.setMiniMapVisible(miniMapVisible);
	}

	public void setZoomButtonsVisible(boolean zoomButtonsVisible) {
		this.mapKit.setZoomButtonsVisible(zoomButtonsVisible);
	}

	public void setZoomSliderVisible(boolean zoomSliderVisible) {
		this.mapKit.setZoomSliderVisible(zoomSliderVisible);
	}

	public void setInfoBoxEnabled(boolean infoBoxEnabled) {
		this.infoBoxEnabled = infoBoxEnabled;
	}

}
