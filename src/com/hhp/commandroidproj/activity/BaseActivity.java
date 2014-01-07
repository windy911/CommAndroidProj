package com.hhp.commandroidproj.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.hhp.commandroidproj.network.BaseRequest;

public class BaseActivity extends Activity {

	/**
	 * 当前activity所持有的所有请求
	 */
	  protected List<BaseRequest> requestList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 requestList = new ArrayList<BaseRequest>();
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		/**
		 * 在activity销毁的时候同时设置停止请求，停止线程请求回调
		 */
		// cancelRequest();
		super.onResume();
	}

	@Override
	protected void onPause() {
		/**
		 * 在activity销毁的时候同时设置停止请求，停止线程请求回调
		 */
		// cancelRequest();
		super.onPause();
	}

	// private void cancelRequest() {
	// if (requestList != null && requestList.size() > 0) {
	// for (BaseRequest request : requestList) {
	// if (request.getRequest() != null) {
	// try {
	// request.getRequest().abort();
	// requestList.remove(request.getRequest());
	// Log.d("netlib", "netlib ,onDestroy request to  "
	// + request.getRequest().getURI()
	// + "  is removed");
	// } catch (UnsupportedOperationException e) {
	// //do nothing .
	// }
	// }
	// }
	// }
	// }
}
