package com.github.pfichtner.jrunalyser.ui.format;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.ui.format.DurationFormatter.Type;

public class DurationFormatterTest {

	@Test
	public void testDigits() {
		DurationFormatter df = new DurationFormatter(Type.SHORT);
		Duration secDur = DefaultDuration.of(530, SECONDS);
		Duration minDur = secDur.convertTo(MINUTES);
		assertEquals("00:08:50", df.format(secDur)); //$NON-NLS-1$
		assertEquals("00:08:50", df.format(minDur)); //$NON-NLS-1$
	}

	@Test
	public void testSymbols() {
		DurationFormatter df = new DurationFormatter(Type.SHORT_SYMBOLS);
		Duration secDur = DefaultDuration.of(530, SECONDS);
		Duration minDur = secDur.convertTo(MINUTES);
		assertEquals("8min 50s", df.format(secDur)); //$NON-NLS-1$
		assertEquals("8min 50s", df.format(minDur)); //$NON-NLS-1$
	}

	@Test
	public void testSymbols_5m_10m_15m() {
		DurationFormatter df = new DurationFormatter(Type.SHORT_SYMBOLS);
		assertEquals("5min", df.format(DefaultDuration.of(5, MINUTES))); //$NON-NLS-1$
		assertEquals("10min", df.format(DefaultDuration.of(10, MINUTES))); //$NON-NLS-1$
		assertEquals("15min", df.format(DefaultDuration.of(15, MINUTES))); //$NON-NLS-1$
	}

	@Test
	public void testSymbols_120m() {
		DurationFormatter df = new DurationFormatter(Type.SHORT_SYMBOLS);
		assertEquals("2h", df.format(DefaultDuration.of(120, MINUTES))); //$NON-NLS-1$
	}

	@Test
	public void testSymbols_150m() {
		DurationFormatter df = new DurationFormatter(Type.SHORT_SYMBOLS);
		assertEquals("2h 30min", df.format(DefaultDuration.of(150, MINUTES))); //$NON-NLS-1$
	}

}
