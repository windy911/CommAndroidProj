 
package com.hhp.commandroidproj.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HotMovieFeed extends Feed {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int hotMovieCount = 0;
	private ArrayList<HotMovie> hotMovieList;

	public HotMovieFeed() {
		hotMovieList = new ArrayList<HotMovie>(0);
	}

	public int addItem(HotMovie hotMovie) {
		hotMovieList.add(hotMovie);
		hotMovieCount++;
		return hotMovieCount;
	}

	public HotMovie getHotMovie(int location) {
		return hotMovieList.get(location);
	}

	public List<HotMovie> getHotMovieList() {
		return hotMovieList;
	}

	public int getHotMovieCount() {
		return hotMovieCount;
	}

	public void clearHotMovieFeed() {
		hotMovieList.clear();
		hotMovieCount = 0;
	}
}
