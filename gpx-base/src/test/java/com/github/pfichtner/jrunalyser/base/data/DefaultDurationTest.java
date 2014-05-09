package com.github.pfichtner.jrunalyser.base.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.data.DefaultDuration;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.google.common.collect.Lists;

public class DefaultDurationTest {

	@Test
	public void testComprable() {
		Duration a = DefaultDuration.of(5, TimeUnit.MINUTES);
		Duration b = DefaultDuration.of(1, TimeUnit.HOURS);
		Duration c = DefaultDuration.of(65, TimeUnit.MINUTES);
		Duration d = DefaultDuration.of(2, TimeUnit.MINUTES);
		Duration e = DefaultDuration.of(190, TimeUnit.SECONDS);

		List<Duration> list = Lists.newArrayList(Arrays.asList(a, b, c, d, e));
		Collections.sort(list);

		assertEquals(Arrays.asList(d, e, a, b, c), list);
	}

	@Test
	public void testAdd() {
		Duration d1 = DefaultDuration.of(1, TimeUnit.SECONDS);
		Duration d2 = DefaultDuration.of(1, TimeUnit.MINUTES);
		assertEquals(DefaultDuration.of(61, TimeUnit.SECONDS), d1.add(d2));
		assertEquals(DefaultDuration.of(1.0166666666666666, TimeUnit.MINUTES),
				d2.add(d1));
	}

}
