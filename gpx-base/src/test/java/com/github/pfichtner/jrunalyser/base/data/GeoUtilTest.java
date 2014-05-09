package com.github.pfichtner.jrunalyser.base.data;

import static com.github.pfichtner.jrunalyser.base.data.DistanceUnit.KILOMETERS;
import static com.github.pfichtner.jrunalyser.base.data.DistanceUnit.METERS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("unused")
public class GeoUtilTest {

	private static final Coordinate berlin = new DefaultCoordinate(52.5234051,
			13.4113999);
	private static final Coordinate hamburg = new DefaultCoordinate(53.5534074,
			9.9921962);
	private static final Coordinate muenchen = new DefaultCoordinate(
			48.1391265, 11.5801863);
	private static final Coordinate koeln = new DefaultCoordinate(50.9406645,
			6.9599115);
	private static final Coordinate frankfurt_am_Main = new DefaultCoordinate(
			50.1115118, 8.6805059);
	private static final Coordinate stuttgart = new DefaultCoordinate(
			48.7771056, 9.1807688);
	private static final Coordinate dortmund = new DefaultCoordinate(
			51.5120542, 7.4635729);
	private static final Coordinate bremen = new DefaultCoordinate(53.074981,
			8.807081);
	private static final Coordinate dresden = new DefaultCoordinate(51.0509576,
			13.733658);
	private static final Coordinate karlsruhe = new DefaultCoordinate(
			49.0080848, 8.4037563);
	private static final Coordinate magdeburg = new DefaultCoordinate(
			52.130956, 11.636701);
	private static final Coordinate freiburg = new DefaultCoordinate(
			47.9971865, 7.8537668);
	private static final Coordinate rostock = new DefaultCoordinate(54.0901331,
			12.1329562);
	private static final Coordinate koblenz = new DefaultCoordinate(50.356718,
			7.599485);
	private static final Coordinate flensburg = new DefaultCoordinate(
			54.780395, 9.435707);

	@Test
	public void testZero() {
		assertEquals(
				0,
				GeoUtil.calcDistance(karlsruhe, karlsruhe).getValue(
						DistanceUnit.KILOMETERS), 0.0);
		assertEquals(
				0.0,
				DefaultDistance.of(GeoUtil.bearing(karlsruhe, karlsruhe),
						METERS).getValue(KILOMETERS), 0.0);

	}

	@Test
	public void testCalcDiff_HB_KA() {
		assertEquals(453.60367492757223, GeoUtil
				.calcDistance(bremen, karlsruhe).getValue(KILOMETERS), 0.0);
	}

	@Test
	public void testCalcDiff_KA_M() {
		assertEquals(253.14493851493603,
				GeoUtil.calcDistance(karlsruhe, muenchen).getValue(KILOMETERS),
				0.0);
	}

	@Test
	public void testCalcDiff_FR_FL() {
		assertEquals(763.0076012446141,
				GeoUtil.calcDistance(freiburg, flensburg).getValue(KILOMETERS),
				0.0);
	}

	@Test
	public void testCalcBearing_HB_KA() {
		assertEquals(183.72575898034205, GeoUtil.bearing(bremen, karlsruhe),
				0.0);
	}

	@Test
	public void testCalcBearing_KA_M() {
		assertEquals(111.26641406275888, GeoUtil.bearing(karlsruhe, muenchen),
				0.0);
	}

	@Test
	public void testCalcBearing_FR_FL() {
		assertEquals(7.666433373201926, GeoUtil.bearing(freiburg, flensburg),
				0.0);
	}

	@Test
	public void testProject_HB_KA() {
		Coordinate src = bremen;
		Coordinate target = karlsruhe;
		Distance diff = GeoUtil.calcDistance(
				GeoUtil.project(GeoUtil.bearingInfo(src, target), src), target);
		assertEquals(0.0, diff.getValue(METERS), 510);
	}

	@Test
	public void testProject_FR_FL() {
		Coordinate src = freiburg;
		Coordinate target = flensburg;
		Distance diff = GeoUtil.calcDistance(
				GeoUtil.project(GeoUtil.bearingInfo(src, target), src), target);
		assertEquals(0.0, diff.getValue(METERS), 860);
	}

	@Test
	public void testProject_KA_M() {
		Coordinate src = karlsruhe;
		Coordinate target = muenchen;
		Distance diff = GeoUtil.calcDistance(
				GeoUtil.project(GeoUtil.bearingInfo(src, target), src), target);
		assertEquals(0.0, diff.getValue(METERS), 500);
	}

}
