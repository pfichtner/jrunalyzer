package com.github.pfichtner.jrunalyser.ui.map;

import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.ui.map.theme.Theme;

public class AdditonalTrackInfo {

	private final Track track;
	private final Theme theme;
	private final int xoffset;
	private final int yoffset;

	public AdditonalTrackInfo(Track track, Theme theme, int xoffset, int yoffset) {
		this.track = track;
		this.theme = theme;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
	}

	public Track getTrack() {
		return this.track;
	}

	public Theme getTheme() {
		return this.theme;
	}

	public int getXoffset() {
		return this.xoffset;
	}

	public int getYoffset() {
		return this.yoffset;
	}

}
