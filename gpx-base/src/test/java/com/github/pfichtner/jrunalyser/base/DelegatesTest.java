package com.github.pfichtner.jrunalyser.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.github.pfichtner.jrunalyser.base.Delegate;
import com.github.pfichtner.jrunalyser.base.Delegates;

public class DelegatesTest {

	private static class Bar {

		private String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	private static class Foo extends Bar {
		//
	}

	private static class TestDelegate extends Bar implements Delegate<Bar> {

		private final Bar delegate;

		public TestDelegate(Bar delegate) {
			this.delegate = delegate;
		}

		@Override
		public Bar getDelegate() {
			return this.delegate;
		}
	}

	@Test
	public void testNoDelegate() {
		String s = "foo";
		assertSame(s, Delegates.getRoot(s, String.class));
	}

	@Test
	public void testChained() {
		Bar bar = new Foo();
		bar.setValue("foo");
		assertSame("foo", Delegates.getRoot(new TestDelegate(bar), Bar.class)
				.getValue());

		Bar chain = new TestDelegate(new TestDelegate(new TestDelegate(
				new TestDelegate(new TestDelegate(bar)))));
		assertEquals("foo", Delegates.getRoot(chain, Bar.class).getValue());
	}

}
