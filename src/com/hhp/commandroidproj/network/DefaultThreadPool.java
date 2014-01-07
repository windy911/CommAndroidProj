package com.hhp.commandroidproj.network;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

/*
 * 线程池的管理
 */
public class DefaultThreadPool {

	static ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(
			15);

	static AbstractExecutorService pool = new ThreadPoolExecutor(10, 20, 15L,
			TimeUnit.SECONDS, blockingQueue,
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private static DefaultThreadPool instance = null;

	public static DefaultThreadPool getInstance() {
		if (instance == null) {
			instance = new DefaultThreadPool();
		}
		return instance;
	}

	public void execute(Runnable r) {
		pool.execute(r);
	}

	public static void shutdown() {
		if (pool != null) {
			pool.shutdown();
			Log.i(DefaultThreadPool.class.getName(),
					"DefaultThreadPool shutdown");
		}
	}

	public static void shutdownRightnow() {
		if (pool != null) {
			pool.shutdownNow();
			try {
				pool.awaitTermination(1, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.i(DefaultThreadPool.class.getName(),
					"DefaultThreadPool shutdownRightnow");
		}
	}

}
