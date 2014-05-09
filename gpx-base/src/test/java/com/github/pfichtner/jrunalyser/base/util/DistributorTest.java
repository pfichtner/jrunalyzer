package com.github.pfichtner.jrunalyser.base.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.util.Distributor;
import com.google.common.collect.Lists;

public class DistributorTest {

	@Test
	public void testDistribute_abcd_on2() {
		List<Character> list = Lists.charactersOf("abcd");
		List<Collection<Character>> distribute = Distributor
				.distribute(list, 2);
		assertEquals(2, distribute.size());
		assertEquals(Lists.charactersOf("ac"), distribute.get(0));
		assertEquals(Lists.charactersOf("bd"), distribute.get(1));
	}

	@Test
	public void testDistribute_abcde_on2() {
		List<Character> list = Lists.charactersOf("abcde");
		List<Collection<Character>> distribute = Distributor
				.distribute(list, 2);
		assertEquals(2, distribute.size());
		assertEquals(Lists.charactersOf("ace"), distribute.get(0));
		assertEquals(Lists.charactersOf("bd"), distribute.get(1));
	}

	@Test
	public void testDistribute_abcd_on3() {
		List<Character> list = Lists.charactersOf("abcd");
		List<Collection<Character>> distribute = Distributor
				.distribute(list, 3);
		assertEquals(3, distribute.size());
		assertEquals(Lists.charactersOf("ad"), distribute.get(0));
		assertEquals(Lists.charactersOf("b"), distribute.get(1));
		assertEquals(Lists.charactersOf("c"), distribute.get(2));
	}

	@Test
	public void testDistribute_abcde_on3() {
		List<Character> list = Lists.charactersOf("abcde");
		List<Collection<Character>> distribute = Distributor
				.distribute(list, 3);
		assertEquals(3, distribute.size());
		assertEquals(Lists.charactersOf("ad"), distribute.get(0));
		assertEquals(Lists.charactersOf("be"), distribute.get(1));
		assertEquals(Lists.charactersOf("c"), distribute.get(2));
	}

	@Test
	public void testDistribute_abcdef_on3() {
		List<Character> list = Lists.charactersOf("abcdef");
		List<Collection<Character>> distribute = Distributor
				.distribute(list, 3);
		assertEquals(3, distribute.size());
		assertEquals(Lists.charactersOf("ad"), distribute.get(0));
		assertEquals(Lists.charactersOf("be"), distribute.get(1));
		assertEquals(Lists.charactersOf("cf"), distribute.get(2));
	}

	@Test
	public void testDistribute_abcdefg_on3() {
		List<Character> list = Lists.charactersOf("abcdefg");
		List<Collection<Character>> distribute = Distributor
				.distribute(list, 3);
		assertEquals(3, distribute.size());
		assertEquals(Lists.charactersOf("adg"), distribute.get(0));
		assertEquals(Lists.charactersOf("be"), distribute.get(1));
		assertEquals(Lists.charactersOf("cf"), distribute.get(2));
	}

}
