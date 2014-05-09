package com.github.pfichtner.jrunalyser.base.data;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Durations;
import com.google.common.collect.Lists;

public class DurationsTest {

	@Test
	public void test15m() {
		Duration upto = DefaultDuration.of(15, TimeUnit.MINUTES);
		assertEquals(
				Lists.newArrayList(DefaultDuration.of(12, TimeUnit.MINUTES)),
				Lists.newArrayList(Durations.durationIterator(upto)));
	}

	@Test
	public void test30m() {
		Duration upto = DefaultDuration.of(30, TimeUnit.MINUTES);
		assertEquals(
				Lists.newArrayList(DefaultDuration.of(12, TimeUnit.MINUTES)),
				Lists.newArrayList(Durations.durationIterator(upto)));
	}

	@Test
	public void test60m() {
		Duration upto = DefaultDuration.of(60, TimeUnit.MINUTES);
		assertEquals(Lists.newArrayList(
				DefaultDuration.of(12, TimeUnit.MINUTES),
				DefaultDuration.of(1, TimeUnit.HOURS)),
				Lists.newArrayList(Durations.durationIterator(upto)));
	}

	@Test
	public void test90m() {
		Duration upto = DefaultDuration.of(60, TimeUnit.MINUTES);
		assertEquals(Lists.newArrayList(
				DefaultDuration.of(12, TimeUnit.MINUTES),
				DefaultDuration.of(1, TimeUnit.HOURS)),
				Lists.newArrayList(Durations.durationIterator(upto)));
	}

}
