package com.github.pfichtner.jrunalyser.base.data;

import java.io.File;

public final class StaticFileProvider {

	private StaticFileProvider() {
		super();
	}

	@Deprecated
	public static File getFixFile() {
		return getFixFiles()[12];
	}

	@Deprecated
	public static File[] getFixFiles() {
		String dir = "/home/xck10h6/gpx/";
		return new File[] { new File(dir, "03_04_2012 17_18.gpx"),
				new File(dir, "05_04_2012 17_30.gpx"),
				new File(dir, "08_04_2012 11_43.gpx"),
				new File(dir, "10_04_2012 11_42.gpx"),
				new File(dir, "12_04_2012 12_53.gpx"),
				new File(dir, "15_04_2012 14_12.gpx"),
				new File(dir, "17_04_2012 18_30.gpx"),
				new File(dir, "19_04_2012 18_49.gpx"),
				new File(dir, "21_04_2012 17_21.gpx"),
				new File(dir, "23_04_2012 19_04.gpx"),
				new File(dir, "26_04_2012 18_35.gpx"),
				new File(dir, "29_04_2012 15_46.gpx"),
				new File(dir, "01_05_2012 14_19.gpx"),
				new File(dir, "03_05_2012 17_32.gpx"),
				new File(dir, "06_05_2012 8_23.gpx"),
				new File(dir, "08_05_2012 14_05.gpx"),
				new File(dir, "10_05_2012 11_00.gpx"),
				new File(dir, "12_05_2012 15_27.gpx"),
				new File(dir, "15_05_2012 17_51.gpx"),
				new File(dir, "21_05_2012 17_29.gpx"),
				new File(dir, "24_05_2012 18_13.gpx"),
				new File(dir, "27_05_2012 16_04.gpx"),
				new File(dir, "29_05_2012 18_53.gpx"),
				new File(dir, "31_05_2012 19_19.gpx"),
				new File(dir, "04_06_2012 18_50.gpx"),
				new File(dir, "06_06_2012 17_50.gpx"),
				new File(dir, "08_06_2012 13_10.gpx"),
				new File(dir, "10_06_2012 13_36.gpx"),
				new File(dir, "12_06_2012 18_48.gpx"),
				new File(dir, "17_06_2012 14_21.gpx"),
				new File(dir, "19_06_2012 19_05.gpx"),
				new File(dir, "21_06_2012 18_35.gpx"),
				new File(dir, "24_06_2012 13_14.gpx"),
				new File(dir, "26_06_2012 18_02.gpx"),
				new File(dir, "29_06_2012 19_56.gpx"),
				new File(dir, "01_07_2012 16_05.gpx"),
				new File(dir, "01_07_2012 16_05.gpx"),
				new File(dir, "03_07_2012 19_32.gpx"),
				new File(dir, "05_07_2012 19_20.gpx"),
				new File(dir, "07_07_2012 13_58.gpx"),
				new File(dir, "10_07_2012 19_36.gpx"),
				new File(dir, "13_07_2012 20_14.gpx"),
				new File(dir, "15_07_2012 20_00.gpx"),
				new File(dir, "17_07_2012 19_35.gpx"),
				new File(dir, "19_07_2012 19_35.gpx"),
				new File(dir, "21_07_2012 13_10.gpx"),
				new File(dir, "23_07_2012 19_04.gpx"),
				new File(dir, "25_07_2012 19_16.gpx"),
				new File(dir, "28_07_2012 13_40.gpx"),
				new File(dir, "31_07_2012 19_41.gpx"),
				new File(dir, "03_08_2012 17_51.gpx") };
	}
}
