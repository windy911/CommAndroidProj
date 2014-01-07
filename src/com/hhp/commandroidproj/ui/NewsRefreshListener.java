package com.hhp.commandroidproj.ui;


/**
 * Interface definition for a callback to be invoked when list should be
 * refreshed.
 *  
 *  
 *  implemented by Adapter
 */
public interface NewsRefreshListener {
	/**
	 * Called when the list should be refreshed.
	 * <p>
	 * A call to {@link PullToRefreshListView #onRefreshComplete()} is
	 * expected to indicate that the refresh has completed.
	 */
	/**
	 * 
	 * called when ask for pre-page
	 * */
	public void onRefresh();

	/**
	 * 
	 * called when ask for post-page
	 * */
	public void onGetDown();
}