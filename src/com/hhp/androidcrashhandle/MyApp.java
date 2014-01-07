package com.hhp.androidcrashhandle;

import android.app.Application;
 
/*
 * Application可以存储一些全局cache的东西。
 */
public class MyApp extends Application {
	 
	private static MyApp self;

	public void onCreate() {
		self = this;
		super.onCreate();
		// 不要造成软件不可预知问题！
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		crashHandler.sendPreviousReportsToServer();
	}

	 
	//Application退出时调用
	public void onTerminate() {
		self = null;
		// super.onTerminate();
	}

	public static MyApp getMyApp() {
		return self;
	}

	
	
	 
}
