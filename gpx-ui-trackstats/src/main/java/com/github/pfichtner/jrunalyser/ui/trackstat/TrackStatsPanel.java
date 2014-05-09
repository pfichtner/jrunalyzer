package com.github.pfichtner.jrunalyser.ui.trackstat;

import static com.github.pfichtner.jrunalyser.base.data.Speeds.is;
import static com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPlugin.getI18n;
import static com.google.common.base.Functions.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.indexOf;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.github.pfichtner.jrunalyser.base.data.DefaultDistance;
import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.DefaultSpeed;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.stat.Functions;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.StatisticsProvider;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.data.track.Tracks;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.base.util.FixedDistance;
import com.github.pfichtner.jrunalyser.base.util.FixedDuration;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.StaticSettings;
import com.github.pfichtner.jrunalyser.ui.base.components.SegmentTrackMouseListener;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.format.PaceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.SpeedFormatter;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;

public class TrackStatsPanel extends JPanel {

	private static final long serialVersionUID = 617730841965291592L;

	private DatasourceFascade dsf;

	private JTextField name;
	private JTextField description;
	private JTextField startTime;
	private JTextField distance;
	private JTextField duration;
	private JTextField elevation;
	private JTextField ascentDescent;
	private JTextField placing;
	private JTextField speed;
	private JTextField pace;
	// --------------------------
	private JTextField _400minpos;
	private JTextField _12metpos;

	private EventBus eventBus;

	public TrackStatsPanel() {
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
		this.name = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.name.title"), row++); //$NON-NLS-1$
		this.description = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.description.title"), row++); //$NON-NLS-1$
		this.distance = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.distance.title"), row++); //$NON-NLS-1$
		this.startTime = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.starttime.title"), row++); //$NON-NLS-1$
		this.duration = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.duration.title"), row++); //$NON-NLS-1$
		this.elevation = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.elevation.title"), row++); //$NON-NLS-1$
		this.ascentDescent = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.elevationGainedLost.title"), row++); //$NON-NLS-1$
		this.speed = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.avgSpeed.title"), row++); //$NON-NLS-1$
		this.pace = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.avgPace.title"), row++); //$NON-NLS-1$
		this.placing = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.placement.title"), row++); //$NON-NLS-1$
		this._400minpos = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.400m.title"), row++); //$NON-NLS-1$
		this._12metpos = createTextField(
				pnl,
				getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.cooper.title"), row++); //$NON-NLS-1$
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

	public void setTrack(TrackLoaded message) throws IOException {
		Track track = message.getTrack();
		Statistics stats = track.getStatistics();

		Settings settings = StaticSettings.INSTANCE;
		DistanceFormatter dif = new DistanceFormatter(
				DistanceFormatter.Type.SHORT);
		DurationFormatter duf = new DurationFormatter(
				DurationFormatter.Type.SHORT);
		SpeedFormatter spf = new SpeedFormatter(SpeedFormatter.Type.SHORT);
		PaceFormatter paf = new PaceFormatter(PaceFormatter.Type.SHORT);

		this.name.setText(track.getMetadata().getName());
		this.description.setText(track.getMetadata().getDescription());
		this.startTime.setText(DateFormat.getDateTimeInstance().format(
				new Date(Tracks.getStartPoint(track).getTime().longValue())));
		this.distance.setText(dif.format(stats.getDistance().convertTo(
				settings.getDistanceUnit())));
		this.duration.setText(duf.format(stats.getDuration().convertTo(
				settings.getTimeUnit())));
		WayPoint minEle = stats.getMinElevation();
		WayPoint maxEle = stats.getMaxElevation();
		NumberFormat nf = NumberFormat.getNumberInstance();
		this.elevation
				.setText(minEle == null || maxEle == null
						|| minEle.getElevation() == null
						|| maxEle.getElevation() == null ? null
						: getI18n()
								.getText(
										"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.elevationGainedLost.format", nf.format(minEle.getElevation().intValue()), //$NON-NLS-1$
										nf.format(maxEle.getElevation()
												.intValue())));
		this.ascentDescent
				.setText(getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.elevation.format", //$NON-NLS-1$
								nf.format(stats.getAscent()),
								nf.format(stats.getDescent())));
		Speed avgSpeed = stats.getAvgSpeed();
		this.speed.setText(spf.format(settings, avgSpeed));
		this.pace.setText(paf.format(settings, avgSpeed));
		// ------------------------------------------------
		{
			Set<Id> all = ImmutableSet.<Id> builder()
					.addAll(this.dsf.getSimilarTracks(track.getId()))
					.add(track.getId()).build();
			Function<StatisticsProvider, Speed> maxAvgSpeedFunc = compose(
					Functions.Statisticss.avgSpeed,
					Functions.StatisticsProviders.statistics);
			Ordering<StatisticsProvider> maxAvgSpeedFuncOrdering = Ordering
					.natural().onResultOf(maxAvgSpeedFunc);

			List<Track> sortedTracks = maxAvgSpeedFuncOrdering.reverse()
					.sortedCopy(this.dsf.loadTracks(all));

			int idx = Iterables.indexOf(sortedTracks, Predicates.compose(
					equalTo(track.getId()), Functions.Tracks.id));
			String txt = String
					.format(getI18n()
							.getText(
									"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.placement.format"), Integer.valueOf((idx + 1)), //$NON-NLS-1$
							Integer.valueOf(sortedTracks.size()));
			if (sortedTracks.size() > 1) {
				boolean best = idx == 0;
				DecimalFormat nfp = new DecimalFormat("+#;-#"); //$NON-NLS-1$
				if (!best) {
					// Compared to best
					int percent = is(
							sortedTracks.get(0).getStatistics().getAvgSpeed())
							.fasterThan(avgSpeed).inPercent();
					txt += " (" + nfp.format(percent) + "% vs. best)"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				// Compared to average (including actual track)
				int percent = is(calcAverageSpeed(sortedTracks)).fasterThan(
						avgSpeed).inPercent();
				txt += " (" + nfp.format(percent) + "% vs. Ø)"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.placing.setText(txt);
		}
		{
			Distance di = DefaultDistance.of(400, DistanceUnit.METERS);
			List<Id> top400m = this.dsf.listTracks(di);
			int idx = indexOf(top400m, equalTo(track.getId()));
			if (idx >= 0) {
				Statistics statistics = this.dsf.loadBestSegment(
						top400m.get(idx), di).orNull();
				FixedDistance fixedDistance = new FixedDistance(di);
				String text = getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.valueAndPlacement.format", //$NON-NLS-1$
								Integer.valueOf(idx + 1),
								duf.format(fixedDistance.getDuration(
										statistics.getDistance(),
										statistics.getDuration())));
				setTextAndAddMouseListener(this._400minpos, text, di);
			}
		}
		{
			Duration du = DefaultDuration.of(12, TimeUnit.MINUTES);
			List<Id> top12min = this.dsf.listTracks(du);
			int idx = indexOf(top12min, equalTo(track.getId()));
			if (idx >= 0) {
				Statistics statistics = this.dsf.loadBestSegment(
						top12min.get(idx), du).orNull();
				FixedDuration fixedDuration = new FixedDuration(du);
				String text = getI18n()
						.getText(
								"com.github.pfichtner.jrunalyser.ui.trackstat.TrackStatsPanel.valueAndPlacement.format", //$NON-NLS-1$
								Integer.valueOf(idx + 1),
								dif.format(fixedDuration.getDistance(
										statistics.getDistance(),
										statistics.getDuration())));
				setTextAndAddMouseListener(this._12metpos, text, du);
			}
		}

	}

	// TODO Move to class Tracks
	private static Speed calcAverageSpeed(Iterable<Track> tracks) {
		return new DefaultSpeed(dis(tracks), dus(tracks));
	}

	// TODO Move to class Tracks
	private static Distance dis(Iterable<Track> tracks) {
		Distance distance = DefaultDistance.of(0, DistanceUnit.KILOMETERS);
		for (Track track : tracks) {
			Distance distance2 = track.getStatistics().getDistance();
			distance = distance.add(distance2).convertTo(
					distance2.getDistanceUnit());
		}
		return distance;
	}

	// TODO Move to class Tracks
	private static Duration dus(Iterable<Track> tracks) {
		Duration duration = DefaultDuration.of(0, TimeUnit.DAYS);
		for (Track track : tracks) {
			Duration duration2 = track.getStatistics().getDuration();
			duration = duration.add(duration2).convertTo(
					duration2.getTimeUnit());
		}
		return duration;
	}

	private void setTextAndAddMouseListener(JTextField textField, String text,
			SegmentationUnit segmentationUnit) {
		textField.setText(text);
		textField.addMouseListener(new SegmentTrackMouseListener(
				segmentationUnit, this.eventBus));
	}

}
