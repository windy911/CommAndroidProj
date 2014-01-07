package com.hhp.commandroidproj.ui;

public interface PullToRefreshCallBack {
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int FIRST_INIAL = 2;
	void onRefreshComplete(CharSequence lastUpdated, int upDown);
	void setOnRefreshListener(NewsRefreshListener onRefreshListener);
}
