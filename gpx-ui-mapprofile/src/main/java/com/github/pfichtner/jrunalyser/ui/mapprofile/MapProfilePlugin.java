package com.github.pfichtner.jrunalyser.ui.mapprofile;

import static com.google.common.collect.Iterables.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.github.pfichtner.jrunalyser.base.ViewMode;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Pace;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Predicates;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.MouseOverWaypoint;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.mapprofile.config.DatasetConfig;
import com.github.pfichtner.jrunalyser.ui.mapprofile.config.DatasetConfigDelegate;
import com.github.pfichtner.jrunalyser.ui.mapprofile.config.DefaultDatasetConfig;
import com.github.pfichtner.jrunalyser.ui.mapprofile.config.MovingAverageConfigDecorator;
import com.github.pfichtner.jrunalyser.ui.mapprofile.config.StrokeRendererConfigDecorator;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MapProfilePlugin extends AbstractUiPlugin implements
		GridDataProvider {

	private static final I18N i18n = new I18N.Builder(MapProfilePlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	private EventBus eventBus;

	private final static List<DatasetConfig> configs = ImmutableList.of(
			createElevationConfig(), createSpeedConfig(),
			createGradientConfig());

	private static final int MOVING_AVG_VALUE = 100;

	private static final int MOVING_AVG_SKIP = 0;

	private final JFreeChart chart;
	private final ChartPanel chartPanel;

	private ViewMode viewMode = ViewMode.BY_DISTANCE;
	private Function<LinkedTrackPoint, ? extends Number> xFunc = createXFunc(this.viewMode);
	private String yAxisLabel = createYAxisLabel(this.viewMode);

	private Track track;

	public MapProfilePlugin() {
		XYDataset empty = new DefaultXYDataset();
		this.chart = ChartFactory.createXYLineChart(null, this.yAxisLabel, "", //$NON-NLS-1$
				empty, PlotOrientation.VERTICAL, true, true, false);
		Plot plot = this.chart.getPlot();
		((XYPlot) plot).setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		this.chartPanel = new ChartPanel(this.chart);
		this.chartPanel.getPopupMenu().add(createMenu());

		this.chartPanel.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseMoved(ChartMouseEvent mouseChartEvent) {
				if (MapProfilePlugin.this.track == null)
					return;

				Plot plot = mouseChartEvent.getChart().getPlot();
				if (plot instanceof XYPlot) {
					XYPlot xyPlot = (XYPlot) plot;
					double point = MapProfilePlugin.this.chartPanel
							.translateScreenToJava2D(
									mouseChartEvent.getTrigger().getPoint())
							.getX();
					PlotRenderingInfo plotInfo = MapProfilePlugin.this.chartPanel
							.getChartRenderingInfo().getPlotInfo();
					double xPos = xyPlot.getDomainAxis().java2DToValue(point,
							plotInfo.getDataArea(), xyPlot.getDomainAxisEdge());

					WayPoint selected = searchWaypoint(
							filter(MapProfilePlugin.this.track.getTrackpoints(),
									Predicates.LinkedWayPoints.hasLink()),
							MapProfilePlugin.this.xFunc, xPos);
					if (selected != null) {
						MapProfilePlugin.this.eventBus
								.post(new MouseOverWaypoint(
										MapProfilePlugin.this.track, selected));
					}
				}
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
				// nothing to do
			}

			private LinkedTrackPoint searchWaypoint(
					Iterable<? extends LinkedTrackPoint> wps,
					Function<LinkedTrackPoint, ? extends Number> xFunc,
					double xPos) {
				BigDecimal actXpos = BigDecimal.ZERO;
				BigDecimal searchedXpos = new BigDecimal(xPos);
				for (LinkedTrackPoint wp : wps) {
					actXpos = actXpos.add(new BigDecimal(xFunc.apply(wp)
							.toString()));
					if (actXpos.compareTo(searchedXpos) >= 0) {
						return wp;
					}
				}
				return null;
			}

		});
	}

	private JMenu createMenu() {
		JMenu jMenu = new JMenu(
				i18n.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.xaxis")); //$NON-NLS-1$
		JMenuItem mi1 = new JRadioButtonMenuItem(
				i18n.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.distance"), //$NON-NLS-1$
				this.viewMode == ViewMode.BY_DISTANCE);
		mi1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
					setViewMode(ViewMode.BY_DISTANCE);
					NumberAxis newAxis = new NumberAxis();
					MapProfilePlugin.this.chart.getXYPlot().setDomainAxis(
							newAxis);
				}
			}
		});
		jMenu.add(mi1);

		JMenuItem mi2 = new JRadioButtonMenuItem(
				i18n.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.duration"), //$NON-NLS-1$
				this.viewMode == ViewMode.BY_DURATION);
		mi2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
					setViewMode(ViewMode.BY_DURATION);
				}
			}

		});
		jMenu.add(mi2);

		ButtonGroup group = new ButtonGroup();
		group.add(mi1);
		group.add(mi2);

		return jMenu;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
		this.xFunc = createXFunc(viewMode);
		this.yAxisLabel = createYAxisLabel(viewMode);
		if (this.track != null) {
			createDatasetsFromTrack();
		}
		if (this.chartPanel != null) {
			MapProfilePlugin.this.chart.getXYPlot().setDomainAxis(
					createYAxisRenderer(viewMode, this.yAxisLabel));
			this.chart.fireChartChanged();
		}
	}

	@Override
	public String getTitle() {
		return getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JPanel getPanel() {
		return this.chartPanel;
	}

	@Subscribe
	public void setTrack(TrackLoaded message) {
		this.track = message.getTrack();

		DatasetConfig primary = getIndex(configs, 0);

		final XYPlot plot = this.chart.getXYPlot();
		plot.setRangeAxis(primary.getIndex(),
				primary.createNumberAxis(this.track));
		createDatasetsFromTrack();
		for (DatasetConfig config : configs) {
			plot.setRenderer(config.getIndex(), config.getRenderer());
		}
	}

	private void createDatasetsFromTrack() {
		final XYPlot plot = this.chart.getXYPlot();
		for (DatasetConfig config : configs) {
			int idx = config.getIndex();
			plot.setRangeAxis(idx, config.createNumberAxis(this.track));
			plot.setDataset(idx, config.createDataset(this.track, this.xFunc));
			plot.mapDatasetToRangeAxis(idx, idx);
		}
	}

	private static Function<LinkedTrackPoint, ? extends Number> createXFunc(
			ViewMode vm) {
		switch (vm) {
		case BY_DISTANCE:
			return createDistanceFunction(DistanceUnit.METERS);
		case BY_DURATION:
			return createDurationFunction(TimeUnit.MILLISECONDS);
		default:
			throw new IllegalStateException("Unknown mode " + vm); //$NON-NLS-1$
		}
	}

	private static String createYAxisLabel(ViewMode vm) {
		switch (vm) {
		case BY_DISTANCE:
			return i18n
					.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.distanceInMeter"); //$NON-NLS-1$
		case BY_DURATION:
			return i18n
					.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.durationInSecs"); //$NON-NLS-1$
		default:
			throw new IllegalStateException("Unknown mode " + vm); //$NON-NLS-1$
		}
	}

	private static ValueAxis createYAxisRenderer(ViewMode vm, String label) {
		switch (vm) {
		case BY_DISTANCE:
			return new NumberAxis(label);
		case BY_DURATION:
			DateAxis domainAxis = new DateAxis(label);
			domainAxis.setDateFormatOverride(new RelativeDateFormat());
			return domainAxis;
		default:
			throw new IllegalStateException("Unknown mode " + vm); //$NON-NLS-1$
		}
	}

	private static DatasetConfig createElevationConfig() {
		DatasetConfig config = new DatasetConfigDelegate(
				new DefaultDatasetConfig.Builder(0)
						.description(
								getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.elevation")) //$NON-NLS-1$
						.yFunc(createElevationFunction())
						.renderer(
								rendererColor(new XYAreaRenderer(
										XYAreaRenderer.AREA), Color.lightGray
										.darker())).build()) {

			@Override
			public NumberAxis createNumberAxis(Track track) {
				NumberAxis axis = super.createNumberAxis(track);
				axis.setAutoRangeIncludesZero(false);
				WayPoint maxEle = track.getStatistics().getMaxElevation();
				WayPoint minEle = track.getStatistics().getMinElevation();
				if (maxEle != null && maxEle.getElevation() != null
						&& minEle != null && minEle.getElevation() != null) {
					int max = maxEle.getElevation().intValue();
					int min = minEle.getElevation().intValue();
					int add = (int) (((double) (max - min)) / 3);
					axis.setRange(new Range((double) min - add, (double) max
							+ add));
				}
				return axis;
			}

		};
		return new StrokeRendererConfigDecorator(
				new MovingAverageConfigDecorator(config,
						"", MOVING_AVG_VALUE, MOVING_AVG_SKIP), //$NON-NLS-1$
				new BasicStroke(2));
	}

	private static <T extends AbstractRenderer> T rendererColor(T renderer,
			Color color) {
		renderer.setSeriesPaint(0, color);
		return renderer;
	}

	private static DatasetConfig createSpeedConfig() {
		DatasetConfig config = new DatasetConfigDelegate(
				new DefaultDatasetConfig.Builder(1)
						.description(
								i18n.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.pace")) //$NON-NLS-1$
						.yFunc(createPaceFunction(TimeUnit.MINUTES,
								DistanceUnit.KILOMETERS))
						.renderer(
								rendererColor(new StandardXYItemRenderer(),
										Color.ORANGE)).build()) {
			@Override
			public NumberAxis createNumberAxis(Track track) {
				NumberAxis axis = super.createNumberAxis(track);
				axis.setInverted(true);
				axis.setAutoRange(true);
				axis.setAutoRangeIncludesZero(false);
				return axis;
			}

		};
		return new StrokeRendererConfigDecorator(
				new MovingAverageConfigDecorator(config,
						"", MOVING_AVG_VALUE, MOVING_AVG_SKIP), //$NON-NLS-1$
				new BasicStroke(2));
	}

	private static DatasetConfig createGradientConfig() {
		DatasetConfig config = new DatasetConfigDelegate(
				new DefaultDatasetConfig.Builder(2)
						.description(
								i18n.getText("com.github.pfichtner.jrunalyser.ui.mapprofile.MapProfilePlugin.gradient")) //$NON-NLS-1$
						.yFunc(createGradientFunction(DistanceUnit.METERS))
						.renderer(
								rendererColor(new StandardXYItemRenderer(),
										Color.RED)).build()) {
			@Override
			public NumberAxis createNumberAxis(Track track) {
				NumberAxis axis = super.createNumberAxis(track);
				axis.setAutoRange(true);
				return axis;
			}

		};
		return new MovingAverageConfigDecorator(config,
				"", MOVING_AVG_VALUE, MOVING_AVG_SKIP); //$NON-NLS-1$
	}

	private static DatasetConfig getIndex(List<DatasetConfig> configs, int val) {
		for (DatasetConfig config : configs) {
			if (config.getIndex() == val) {
				return config;
			}
		}
		return null;
	}

	private static Function<LinkedTrackPoint, Integer> createElevationFunction() {
		return new Function<LinkedTrackPoint, Integer>() {
			@Override
			public Integer apply(LinkedTrackPoint wp) {
				return wp.getElevation();
			}
		};
	}

	private static Function<Speed, Pace> createSpeed2PaceFunction(
			final TimeUnit minutes, final DistanceUnit kilometers) {
		return new Function<Speed, Pace>() {

			@Override
			public Pace apply(Speed speed) {
				return speed.toPace(minutes, kilometers);
			}
		};
	}

	private static Function<LinkedTrackPoint, Double> createPaceFunction(
			final TimeUnit minutes, final DistanceUnit kilometers) {
		return Functions
				.compose(
						new Function<Pace, Double>() {
							@Override
							public Double apply(Pace pace) {
								return Double.valueOf(pace.getValue(
										pace.getTimeUnit(),
										pace.getDistanceUnit()));
							}
						},
						Functions
								.compose(
										createSpeed2PaceFunction(minutes,
												kilometers),
										com.github.pfichtner.jrunalyser.base.data.stat.Functions.Links.speedOfLink));
	}

	private static Function<LinkedTrackPoint, Double> createDistanceFunction(
			final DistanceUnit distanceUnit) {
		return new Function<LinkedTrackPoint, Double>() {
			@Override
			public Double apply(LinkedTrackPoint wp) {
				return Double.valueOf(wp.getLink().getDistance()
						.getValue(distanceUnit));
			}
		};
	}

	private static Function<LinkedTrackPoint, Double> createDurationFunction(
			final TimeUnit timeUnit) {
		return new Function<LinkedTrackPoint, Double>() {
			@Override
			public Double apply(LinkedTrackPoint tpd) {
				return Double.valueOf(tpd.getLink().getDuration()
						.getValue(timeUnit));
			}
		};
	}

	private static Function<LinkedTrackPoint, Double> createGradientFunction(
			final DistanceUnit distanceUnit) {
		return new Function<LinkedTrackPoint, Double>() {
			@Override
			public Double apply(LinkedTrackPoint tpd) {
				return Double.valueOf(tpd.getLink().getGradient()
						.convertTo(distanceUnit).getValue());
			}
		};
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public static String getText(String key, Object... args) {
		return i18n.getText(key, args);
	}

}