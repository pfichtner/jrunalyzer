package com.github.pfichtner.jrunalyser.ui.overallstats;

import static com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPlugin.getI18n;
import static com.google.common.base.Functions.compose;
import static com.google.common.base.Predicates.notNull;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.stat.CombinedStatistics;
import com.github.pfichtner.jrunalyser.base.data.stat.Functions;
import com.github.pfichtner.jrunalyser.base.data.stat.Orderings;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.StatisticsProvider;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.TrackpointProvider;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.base.util.format.LatitudeFormatter;
import com.github.pfichtner.jrunalyser.base.util.format.LongitudeFormatter;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.StaticSettings;
import com.github.pfichtner.jrunalyser.ui.base.components.LoadTrackMouseListener;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.format.PaceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.SpeedFormatter;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;

public class OverallStatsPanel extends JPanel {

	private static final String NO_VALUE = "-";

	private final Function<Id, Track> id2track = new Function<Id, Track>() {
		@Override
		public Track apply(Id id) {
			try {
				return OverallStatsPanel.this.dsf.loadTrack(id);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	};

	private static final long serialVersionUID = -3790468982246646282L;

	private DatasourceFascade dsf;

	private JTextField nrOfWorkouts;
	private JTextField distance;
	private JTextField duration;
	private JTextField avgSpeed;
	private JTextField avgPace;
	private JTextField ascentDescent;
	private JTextField longestDuration;
	private JTextField longestDistance;

	private JTextField mostWorkouts;
	private JTextField mostActiveWeekDay;
	private JTextField mostActiveMonth;

	private JTextField topSpeed;
	private JTextField topAvgSpeed;
	private JTextField maxEle;
	private JTextField minEle;
	private JTextField maxEleDiff;
	private JTextField minEleDiff;
	// TODO max ascent
	// TODO max descent
	// TODO Best 400m, 1mile, 1km, ...?
	private JTextField maxLat;
	private JTextField minLat;
	private JTextField maxLng;
	private JTextField minLng;

	private JTextField mostRun;
	private JTextField longestBreak;

	private JTextField workoutsTwTmTy;
	private JTextField distanceTwTmTy;
	private JTextField durationTwTmTy;

	private JTextField workoutsPdPmPy;
	private JTextField distancePdPmPy;
	private JTextField durationPdPmPy;

	private EventBus eventBus;

	public OverallStatsPanel() {
		super(createLayout());
		init(this);
	}

	public void addEventBusPoster(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	private void init(JPanel outer) {

		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(pnl,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		outer.setLayout(new GridLayout(1, 1));
		outer.add(scrollPane);

		int row = 0;
		this.nrOfWorkouts = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.amountOfWorkouts.title"), row++); //$NON-NLS-1$
		this.distance = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.distance.title"), row++); //$NON-NLS-1$
		this.duration = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.duration.title"), row++); //$NON-NLS-1$
		this.avgSpeed = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.avgSpeed.title"), row++); //$NON-NLS-1$
		this.avgPace = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.avgPace.title"), row++); //$NON-NLS-1$
		this.ascentDescent = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.elevation.title"), row++); //$NON-NLS-1$
		this.longestDuration = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.longestDuration.title"), row++); //$NON-NLS-1$
		this.longestDistance = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.longestDistance.title"), row++); //$NON-NLS-1$

		this.mostWorkouts = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostWorkouts.title"), row++); //$NON-NLS-1$

		this.mostActiveWeekDay = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostActiveWeekDay.title"), row++); //$NON-NLS-1$

		this.mostActiveMonth = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostActiveMonth.title"), row++); //$NON-NLS-1$

		this.topSpeed = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.highestSpeed.title"), row++); //$NON-NLS-1$
		this.topAvgSpeed = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.highestPace.title"), //$NON-NLS-1$
				row++);
		this.maxEle = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.highestEleveation.title"), row++); //$NON-NLS-1$
		this.minEle = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.lowestEleveation.title"), row++); //$NON-NLS-1$
		this.maxEleDiff = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.greatestElevationDiff.title"), row++); //$NON-NLS-1$
		this.minEleDiff = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.lowestElevationDiff.title"), row++); //$NON-NLS-1$

		this.maxLat = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.maxLatitude.title"), row++); //$NON-NLS-1$
		this.minLat = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.minLatitude.title"), row++); //$NON-NLS-1$
		this.maxLng = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.maxLongitude.title"), row++); //$NON-NLS-1$
		this.minLng = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.minLongitude.title"), row++); //$NON-NLS-1$

		this.mostRun = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostRun.title"), row++); //$NON-NLS-1$
		this.longestBreak = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.longestBreak.title"), row++); //$NON-NLS-1$

		this.workoutsTwTmTy = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.workoutsWMY.title"), row++); //$NON-NLS-1$
		this.distanceTwTmTy = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.distanceWMY.title"), row++); //$NON-NLS-1$
		this.durationTwTmTy = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.durationWMY.title"), row++); //$NON-NLS-1$

		this.workoutsPdPmPy = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.workoutsPdPmPy.title"), row++); //$NON-NLS-1$
		this.distancePdPmPy = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.distancePdPmPy.title"), row++); //$NON-NLS-1$
		this.durationPdPmPy = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.durationPdPmPy.title"), row++); //$NON-NLS-1$

		JPanel filler = new JPanel();
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = row++;
		c1.weighty = 1;
		c1.fill = GridBagConstraints.BOTH;
		pnl.add(filler, c1);
	}

	private static GridBagLayout createLayout() {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[] { 0.0, 1.0 };
		return gbl;
	}

	private static JTextField createTextField(Container pnl, String label,
			int row) {
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = row;
		c1.anchor = GridBagConstraints.WEST;
		c1.insets = new Insets(5, 5, 0, 0);
		pnl.add(new JLabel(label), c1);

		GridBagConstraints c2 = (GridBagConstraints) c1.clone();
		c2.gridx = 1;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.HORIZONTAL;
		JTextField field = createTextField();
		pnl.add(field, c2);
		return field;
	}

	private static JTextField createTextField() {
		JTextField result = new JTextField();
		result.setEditable(false);
		result.setBackground(Color.WHITE);
		return result;
	}

	public void setDatasourceFascade(DatasourceFascade dsf) {
		this.dsf = dsf;
	}

	public void initialize() throws IOException {
		Set<Id> allIds = this.dsf.getTrackIds();

		Iterable<Track> tracks = this.dsf.loadTracks(allIds);

		// TODO Null all fields if tracks are empty

		if (!Iterables.isEmpty(tracks)) {

			int workoutCount = allIds.size();

			Statistics stats = CombinedStatistics.of(FluentIterable.from(
					this.dsf.loadTracks(allIds)).transform(
					Functions.StatisticsProviders.statistics));

			Settings settings = StaticSettings.INSTANCE;
			DistanceFormatter dif = new DistanceFormatter(
					DistanceFormatter.Type.SHORT);
			DurationFormatter duf = new DurationFormatter(
					DurationFormatter.Type.MEDIUM_SYMBOLS);
			SpeedFormatter spf = new SpeedFormatter(SpeedFormatter.Type.SHORT);
			PaceFormatter paf = new PaceFormatter(PaceFormatter.Type.SHORT);

			this.nrOfWorkouts.setText(String.valueOf(workoutCount));
			if (stats.getDistance() != null) {
				Distance distance = stats.getDistance().convertTo(
						settings.getDistanceUnit());
				this.distance
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.valueAndAvg.format", //$NON-NLS-1$
										dif.format(distance),
										dif.format(distance
												.divide(workoutCount))));
			}
			if (stats.getDuration() != null) {
				Duration b = stats.getDuration().convertTo(
						settings.getTimeUnit());
				this.duration
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.valueAndAvg.format", duf.format(b), //$NON-NLS-1$
										duf.format(b.divide(workoutCount))));
			}
			if (stats.getAvgSpeed() != null) {
				Speed avgSpeed = stats.getAvgSpeed();
				this.avgSpeed.setText(spf.format(settings, avgSpeed));
				this.avgPace.setText(paf.format(settings, avgSpeed));
			}
			NumberFormat nf = NumberFormat.getNumberInstance();
			this.ascentDescent
					.setText(getI18n()
							.getText(
									"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.elevation.format", //$NON-NLS-1$
									nf.format(stats.getAscent()),
									nf.format(stats.getDescent())));

			Track longestDurationTrack = Orderings.duration.max(tracks);
			setTextAndAddMouseListener(
					this.longestDuration,
					getString(longestDurationTrack, duf
							.format(Orderings.durationFunc
									.apply(longestDurationTrack))),
					longestDurationTrack);

			Track longestDistanceTrack = Orderings.distance.max(tracks);
			setTextAndAddMouseListener(
					this.longestDistance,
					getString(longestDistanceTrack, dif
							.format(Orderings.distanceFunc
									.apply(longestDurationTrack))),
					longestDistanceTrack);

			Function<StatisticsProvider, Speed> maxSpeedFunc = compose(
					Functions.LinkedWayPoints.speed(),
					compose(Functions.Statisticss.maxSpeed,
							Functions.StatisticsProviders.statistics));

			Ordering<StatisticsProvider> maxSpeedOrdering = Ordering.natural()
					.onResultOf(maxSpeedFunc);

			Track topSpeedTrack = maxSpeedOrdering.max(tracks);
			setTextAndAddMouseListener(
					this.topSpeed,
					getString(
							topSpeedTrack,
							spf.format(settings,
									maxSpeedFunc.apply(topSpeedTrack))),
					topSpeedTrack);

			Function<StatisticsProvider, Speed> maxAvgSpeedFunc = compose(
					Functions.Statisticss.avgSpeed,
					Functions.StatisticsProviders.statistics);
			Ordering<StatisticsProvider> maxAvgSpeedFuncOrdering = Ordering
					.natural().onResultOf(maxAvgSpeedFunc);

			Track avgSpeedTrack = maxAvgSpeedFuncOrdering.max(tracks);
			setTextAndAddMouseListener(
					this.topAvgSpeed,
					getString(
							avgSpeedTrack,
							spf.format(settings,
									maxAvgSpeedFunc.apply(avgSpeedTrack))),
					avgSpeedTrack);

			Function<StatisticsProvider, Integer> maxEleFunc = compose(
					Functions.WayPoints.elevation(),
					compose(Functions.Statisticss.maxEle,
							Functions.StatisticsProviders.statistics));
			Ordering<StatisticsProvider> maxEleOrdering = Ordering.natural()
					.onResultOf(maxEleFunc);

			Track maxEleTrack = maxEleOrdering.max(tracks);
			setTextAndAddMouseListener(this.maxEle,
					getString(maxEleTrack, maxEleFunc.apply(maxEleTrack)),
					maxEleTrack);

			Function<StatisticsProvider, Integer> minEleFunc = compose(
					Functions.WayPoints.elevation(),
					compose(Functions.Statisticss.minEle,
							Functions.StatisticsProviders.statistics));
			Ordering<StatisticsProvider> minEleOrdering = Ordering.natural()
					.onResultOf(minEleFunc);

			Track minEleTrack = minEleOrdering.min(tracks);
			setTextAndAddMouseListener(this.minEle,
					getString(minEleTrack, minEleFunc.apply(minEleTrack)),
					minEleTrack);

			Function<StatisticsProvider, Integer> getEleDiffFunc = compose(
					Functions.Statisticss.eleDiff,
					Functions.StatisticsProviders.statistics);
			Ordering<StatisticsProvider> eleDiffOrdering = Ordering.natural()
					.onResultOf(getEleDiffFunc);

			Track maxEleDiffTrack = eleDiffOrdering.max(tracks);
			setTextAndAddMouseListener(
					this.maxEleDiff,
					getString(maxEleDiffTrack,
							getEleDiffFunc.apply(maxEleDiffTrack)),
					maxEleDiffTrack);

			Track minEleDiffTrack = eleDiffOrdering.min(tracks);
			setTextAndAddMouseListener(
					this.minEleDiff,
					getString(minEleDiffTrack,
							getEleDiffFunc.apply(minEleDiffTrack)),
					minEleDiffTrack);

			// -----------------------------------------------------------------------

			{
				Multiset<String> byWeek = groupBy(allIds, format("ww yyyy")); //$NON-NLS-1$
				String week = Iterators.get(byWeek.iterator(), 0);

				Multiset<String> byMonth = groupBy(allIds, format("MMMM yyyy")); //$NON-NLS-1$
				String month = Iterators.get(byMonth.iterator(), 0);

				Multiset<String> byYear = groupBy(allIds, format("yyyy")); //$NON-NLS-1$
				String year = Iterators.get(byYear.iterator(), 0);

				this.mostWorkouts
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostWorkouts.format", //$NON-NLS-1$
										year,
										String.valueOf(byYear.count(year)),
										month,
										String.valueOf(byMonth.count(month)),
										week,
										String.valueOf(byWeek.count(week))));

				Multiset<String> byDayOfWeek = groupBy(allIds, format("E")); //$NON-NLS-1$
				List<String> texts = Lists.newArrayList();
				for (String day : byDayOfWeek.elementSet()) {
					texts.add(getI18n()
							.getText(
									"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostActiveWeekDay.format", //$NON-NLS-1$
									day,
									Integer.valueOf(byDayOfWeek.count(day))));

				}
				this.mostActiveWeekDay.setText(Joiner.on(", ").join(texts)); //$NON-NLS-1$

				Multiset<String> bySingleMonth = groupBy(allIds, format("MMMM")); //$NON-NLS-1$
				String singleMonth = Iterables.get(bySingleMonth, 0);
				this.mostActiveMonth
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostActiveMonth.format", //$NON-NLS-1$
										singleMonth,
										Integer.valueOf(bySingleMonth
												.count(singleMonth))));
			}

			// -----------------------------------------------------------------------

			Track maxLat = Orderings.maxLatitude.max(tracks);
			setTextAndAddMouseListener(this.maxLat,
					getStringLat(maxLat, Orderings.maxLatitudeFunc), maxLat);

			Track minLat = Orderings.minLatitude.min(tracks);
			setTextAndAddMouseListener(this.minLat,
					getStringLat(minLat, Orderings.minLatitudeFunc), minLat);

			Track maxLng = Orderings.maxLongitude.max(tracks);
			setTextAndAddMouseListener(this.maxLng,
					getStringLng(maxLng, Orderings.maxLongitudeFunc), maxLng);

			Track minLng = Orderings.minLongitude.min(tracks);
			setTextAndAddMouseListener(this.minLng,
					getStringLng(minLng, Orderings.minLongitudeFunc), minLng);

			// ---------------------------------------------------------------

			this.mostRun.setText(calculateMostRun(allIds));

			{
				FluentIterable<Track> orderedTracks = FluentIterable.from(
						this.dsf.getTrackIds(new Date(Long.MIN_VALUE),
								new Date(Long.MAX_VALUE))).transform(
						this.id2track);

				Track prev = null, t1 = null, t2 = null;
				long absDiff = 0;
				for (Track track : orderedTracks) {
					long day = getDay(track);
					if (prev != null) {
						long pDay = getDay(prev);
						assert day > pDay : day
								+ "<=" //$NON-NLS-1$
								+ pDay
								+ "(" //$NON-NLS-1$
								+ new Date(Tracks.getStartPoint(track)
										.getTime().longValue())
								+ " <?> " //$NON-NLS-1$
								+ new Date(Tracks.getStartPoint(prev).getTime()
										.longValue()) + ")"; //$NON-NLS-1$
						long diff = day - pDay;
						if (diff >= absDiff) {
							absDiff = diff;
							t1 = prev;
							t2 = track;
						}
					}
					prev = track;
				}
				DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
				long oneDay = TimeUnit.DAYS.toMillis(1);
				String text = absDiff < 2 ? null
						: getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.longestBreak.format", //$NON-NLS-1$
										Long.valueOf(absDiff - 1),
										df.format(new Date(Tracks
												.getStartPoint(t1).getTime()
												.longValue()
												+ oneDay)),
										df.format(new Date(Tracks
												.getStartPoint(t2).getTime()
												.longValue()
												- oneDay)));
				this.longestBreak.setText(text);
			}

			{
				Date now = new Date();
				Iterable<Id> w = this.dsf.getTrackIds(getWeekStart(now), now);
				Iterable<Id> m = this.dsf.getTrackIds(getMonthStart(now), now);
				Iterable<Id> y = this.dsf.getTrackIds(getYearStart(now), now);

				Statistics wStats = CombinedStatistics.of(FluentIterable.from(
						this.dsf.loadTracks(w)).transform(
						Functions.StatisticsProviders.statistics));
				Statistics mStats = CombinedStatistics.of(FluentIterable.from(
						this.dsf.loadTracks(m)).transform(
						Functions.StatisticsProviders.statistics));
				Statistics yStats = CombinedStatistics.of(FluentIterable.from(
						this.dsf.loadTracks(y)).transform(
						Functions.StatisticsProviders.statistics));

				this.workoutsTwTmTy
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.TwTmTy.format", //$NON-NLS-1$
										Integer.valueOf(Iterables.size(w)),
										Integer.valueOf(Iterables.size(m)),
										Integer.valueOf(Iterables.size(y))));
				this.distanceTwTmTy
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.TwTmTy.format", //$NON-NLS-1$
										wStats.getDistance() == null ? NO_VALUE
												: dif.format(wStats
														.getDistance()
														.convertTo(
																settings.getDistanceUnit())),
										mStats.getDistance() == null ? NO_VALUE
												: dif.format(mStats
														.getDistance()
														.convertTo(
																settings.getDistanceUnit())),
										yStats.getDistance() == null ? NO_VALUE
												: dif.format(yStats
														.getDistance()
														.convertTo(
																settings.getDistanceUnit()))));
				this.durationTwTmTy
						.setText(getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.TwTmTy.format", //$NON-NLS-1$
										wStats.getDuration() == null ? NO_VALUE
												: duf.format(wStats
														.getDuration()
														.convertTo(
																settings.getTimeUnit())),
										mStats.getDuration() == null ? NO_VALUE
												: duf.format(mStats
														.getDuration()
														.convertTo(
																settings.getTimeUnit())),
										yStats.getDuration() == null ? NO_VALUE
												: duf.format(yStats
														.getDuration()
														.convertTo(
																settings.getTimeUnit()))));
			}

			Iterable<Id> trackIds = this.dsf.getTrackIds(new Date(
					Long.MIN_VALUE), new Date(Long.MAX_VALUE));
			Id first = Iterables.getFirst(trackIds, null);
			Id last = Iterables.getLast(trackIds, null);
			Long days = null;
			if (first != null && last != null) {
				days = Long.valueOf(TimeUnit.DAYS.convert(
						Tracks.getStartPoint(this.dsf.loadTrack(last))
								.getTime().longValue()
								- Tracks.getStartPoint(
										this.dsf.loadTrack(first)).getTime()
										.longValue(), TimeUnit.MILLISECONDS));
			}
			{

				this.workoutsPdPmPy.setText(days == null ? null : nf
						.format(((double) workoutCount) / days.longValue())
						+ "/" //$NON-NLS-1$
						+ nf.format(((double) workoutCount) / days.longValue()
								* 30)
						+ "/" //$NON-NLS-1$
						+ nf.format(((double) workoutCount) / days.longValue()
								* 365));
			}
			{

				Distance distance = stats.getDistance().convertTo(
						settings.getDistanceUnit());
				this.distancePdPmPy.setText(days == null ? null
						: dif.format(distance.divide(days.longValue()))
								+ "/" //$NON-NLS-1$
								+ dif.format(distance.divide(((double) days
										.longValue()) / 30))
								+ "/" //$NON-NLS-1$
								+ dif.format(distance.divide(((double) days
										.longValue()) / 365)));
			}
			{

				Duration duration = stats.getDuration().convertTo(
						settings.getTimeUnit());
				this.durationPdPmPy.setText(days == null ? null
						: duf.format(duration.divide(days.longValue()))
								+ "/" //$NON-NLS-1$
								+ duf.format(duration.divide(((double) days
										.longValue()) / 30))
								+ "/" //$NON-NLS-1$
								+ duf.format(duration.divide(((double) days
										.longValue()) / 365)));
			}

		}

	}

	private static Date getWeekStart(Date date) {
		Calendar cal = newCalendar(date);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return clearFields(cal);
	}

	private static Date getMonthStart(Date date) {
		Calendar cal = newCalendar(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return clearFields(cal);
	}

	private static Date getYearStart(Date date) {
		Calendar cal = newCalendar(date);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return clearFields(cal);
	}

	private static Calendar newCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	private static Date clearFields(Calendar cal) {
		for (int field : new int[] { Calendar.HOUR_OF_DAY, Calendar.MINUTE,
				Calendar.SECOND, Calendar.MILLISECOND }) {
			cal.clear(field);
		}
		return cal.getTime();
	}

	private static long getDay(Track track) {
		return TimeUnit.DAYS.convert(Tracks.getStartPoint(track).getTime()
				.longValue(), TimeUnit.MILLISECONDS);
	}

	private Function<Id, String> format(final String string) {
		return new Function<Id, String>() {
			SimpleDateFormat sdf = new SimpleDateFormat(string);

			@Override
			public String apply(Id id) {
				try {
					return this.sdf.format(new Date(Tracks
							.getStartPoint(
									OverallStatsPanel.this.dsf.loadTrack(id))
							.getTime().longValue()));
				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			}
		};
	}

	private Multiset<String> groupBy(Set<Id> ids, Function<Id, String> f) {
		Multiset<String> ms = HashMultiset.create();
		Iterables.addAll(ms, FluentIterable.from(ids).transform(f));
		return Multisets.copyHighestCountFirst(ms);
	}

	private String calculateMostRun(Set<Id> allIds) throws IOException {
		Multiset<Id> ms = HashMultiset.create();
		for (Id id : allIds) {
			ms.add(id,
					OverallStatsPanel.this.dsf.getSimilarTracks(id).size() + 1);
		}
		Id top = Iterables.get(Multisets.copyHighestCountFirst(ms), 0);
		if (top == null) {
			return null;
		}

		Set<Id> similarTracks = ImmutableSet.<Id> builder().add(top)
				.addAll(this.dsf.getSimilarTracks(top)).build();
		FluentIterable<Metadata> metadata = FluentIterable.from(similarTracks)
				.transform(this.id2track).transform(Functions.Tracks.metadata);

		// do the have a common description?
		Multiset<String> desc = Multisets.copyHighestCountFirst(HashMultiset
				.create(metadata.transform(Functions.Metadatas.description)
						.filter(notNull()).filter(notEmpty())));
		String cd = desc.size() > 0 ? Iterables.get(desc, 0) : ""; //$NON-NLS-1$

		// do the have a common name?
		Multiset<String> names = Multisets.copyHighestCountFirst(HashMultiset
				.create(metadata.transform(Functions.Metadatas.name)
						.filter(notNull()).filter(notEmpty())));
		String cn = names.size() > 0 ? Iterables.get(names, 0) : ""; //$NON-NLS-1$

		String val;
		if (desc.count(cd) > desc.size() / 2) {
			val = cd;
		} else if (names.count(cn) > names.size() / 2) {
			val = cn;
		} else if (!desc.isEmpty()) {
			val = Joiner.on(", ").join(desc); //$NON-NLS-1$
		} else if (!names.isEmpty()) {
			val = Joiner.on(", ").join(names); //$NON-NLS-1$
		} else {
			val = "???"; //$NON-NLS-1$
		}
		return getI18n()
				.getText(
						"com.github.pfichtner.jrunalyser.ui.overallstats.OverallStatsPanel.mostRun.format", val, //$NON-NLS-1$
						Integer.valueOf(similarTracks.size()));
	}

	private Predicate<String> notEmpty() {
		return new Predicate<String>() {
			@Override
			public boolean apply(String string) {
				return !Strings.isNullOrEmpty(string);
			}
		};
	}

	private void setTextAndAddMouseListener(JTextField textField, String text,
			Track track) {
		textField.setText(text);
		textField.addMouseListener(new LoadTrackMouseListener(track,
				this.eventBus));
	}

	private String getStringLat(Track track,
			Function<Track, ? extends Number> function) {
		return getString(track, new LatitudeFormatter().format(function.apply(
				track).doubleValue()));
	}

	private String getStringLng(Track track,
			Function<Track, ? extends Number> function) {
		return getString(track, new LongitudeFormatter().format(function.apply(
				track).doubleValue()));
	}

	private String getString(Track track, Object text) {
		return getStartDate(track) + " (" //$NON-NLS-1$
				+ text + ")"; //$NON-NLS-1$
	}

	private static String getStartDate(TrackpointProvider waypointProvider) {
		return DateFormat.getDateTimeInstance().format(
				new Date(Iterables.get(waypointProvider.getTrackpoints(), 0)
						.getTime().longValue()));

	}

}
