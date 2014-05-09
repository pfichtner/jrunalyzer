package com.github.pfichtner.jrunalyser.ui.segmentstat;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.segment.Segment;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.ui.base.Settings;
import com.github.pfichtner.jrunalyser.ui.base.StaticSettings;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.SegmentSelectedMessage;
import com.github.pfichtner.jrunalyser.ui.format.DistanceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter;
import com.github.pfichtner.jrunalyser.ui.format.PaceFormatter;
import com.github.pfichtner.jrunalyser.ui.format.SpeedFormatter;
import com.google.common.collect.Iterables;

public class SegmentStatsPanel extends JPanel {

	private DatasourceFascade dsf;

	private JTextField index;
	private JTextField startTime;
	private JTextField distance;
	private JTextField duration;
	private JTextField elevation;
	private JTextField ascentDescent;
	private JTextField speed;
	private JTextField pace;

	public SegmentStatsPanel() {
		super(createLayout());
		init(this);
	}

	private void init(JPanel pnl) {
		int row = 0;
		this.index = createTextField(pnl, "Nummer", row++);
		this.distance = createTextField(pnl, "Distanz", row++);
		this.startTime = createTextField(pnl, "Startzeit", row++);
		this.duration = createTextField(pnl, "Dauer", row++);
		this.elevation = createTextField(pnl, "Höhe", row++);
		this.ascentDescent = createTextField(pnl, "Elevation", row++);
		this.speed = createTextField(pnl, "∅ Geschwindigkeit", row++);
		this.pace = createTextField(pnl, "∅ Schritt", row++);
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

	public void setActiveSegment(SegmentSelectedMessage message) {
		Segment segment = message.getSegment();
		Statistics stats = segment.getStatistics();

		Settings settings = StaticSettings.INSTANCE;
		DistanceFormatter dif = new DistanceFormatter(
				DistanceFormatter.Type.SHORT);
		DurationFormatter duf = new DurationFormatter(
				DurationFormatter.Type.SHORT);
		SpeedFormatter spf = new SpeedFormatter(SpeedFormatter.Type.SHORT);
		PaceFormatter paf = new PaceFormatter(PaceFormatter.Type.SHORT);

		this.index.setText("#" + (message.getIndex() + 1)); //$NON-NLS-1$
		this.startTime.setText(DateFormat.getDateTimeInstance().format(
				new Date(Iterables.get(segment.getTrackpoints(), 0).getTime()
						.longValue())));
		this.distance.setText(dif.format(stats.getDistance().convertTo(
				settings.getDistanceUnit())));
		this.duration.setText(duf.format(stats.getDuration().convertTo(
				settings.getTimeUnit())));
		WayPoint minEle = stats.getMinElevation();
		WayPoint maxEle = stats.getMaxElevation();
		NumberFormat nf = NumberFormat.getNumberInstance();
		this.elevation.setText(minEle == null || maxEle == null
				|| minEle.getElevation() == null
				|| maxEle.getElevation() == null ? null : nf.format(minEle
				.getElevation().intValue())
				+ "m / "
				+ nf.format(maxEle.getElevation().intValue()) + "m");
		this.ascentDescent.setText("↑" + nf.format(stats.getAscent()) + "m / ↓"
				+ nf.format(stats.getDescent()) + "m");
		Speed avgSpeed = stats.getAvgSpeed();
		this.speed.setText(spf.format(settings, avgSpeed));
		this.pace.setText(paf.format(settings, avgSpeed));
	}

}
