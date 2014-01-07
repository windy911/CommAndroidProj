package com.hhp.commandroidproj.imageloader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsynImageLoader {
	private static final String TAG = "AsynImageLoader";

	public static final String GUBA_DIR = "/commandroidproj";
	public static final String CACHE_DIR = GUBA_DIR + "/images";
	public static final String CAMERA_TEMP = GUBA_DIR
			+ "/images/camera_temp.jpg";
	public static final int ROUND = 4;
 
	private List<Task> taskQueue;
	private boolean isRunning = false;

	private ImageMemoryCache imageMemoryCache;
	private ImageFileCache imageFileCache;

	private ExecutorService executorService;

	private static AsynImageLoader asynImageLoader;

	public static AsynImageLoader getInstance(Context context) {
		if (asynImageLoader == null) {
			synchronized (AsynImageLoader.class) {
				if (asynImageLoader == null) {
					asynImageLoader = new AsynImageLoader(context);
				}
			}
		}
		return asynImageLoader;
	}
 

	private AsynImageLoader(Context context) {
		// 初始化变量 
		taskQueue = new ArrayList<AsynImageLoader.Task>(); 
		imageMemoryCache = new ImageMemoryCache(context);
		imageFileCache = new ImageFileCache();
		executorService = Executors.newFixedThreadPool(5); 
		// 启动图片下载线程
		isRunning = true; 
	}

	/**
	 * 
	 * @param imageView
	 *            需要延迟加载图片的对象
	 * @param url
	 *            图片的URL地址
	 * @param resId
	 *            图片加载过程中显示的图片资源
	 */
	public void showImageAsyn(ImageView imageView, String url, int resId) {
		// imageView.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView, resId));

		if (bitmap == null) {
			imageView.setImageResource(resId);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	public void showImageAsyn(ImageView imageView, String url, int resId,
			int round) {
		Bitmap bitmap = loadImageAsyn(url,
				getImageCallback(imageView, resId, round));

		if (bitmap == null) {
			imageView.setImageResource(resId);
		} else {
			if (round > 0) {
				bitmap = toRoundCorner(bitmap, round);
			}
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * 使用新图片覆盖内存与sdcard中的图片
	 * 
	 * @param bitmap
	 *            新的图片
	 * @param url
	 *            要覆盖图片的地址
	 */
	public void overWrittenImage(Bitmap bitmap, String url) {
		imageFileCache.saveBitmap(bitmap, url);
		imageMemoryCache.addBitmapToCache(url, bitmap);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
 
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	private Bitmap loadImageAsyn(String path, ImageCallback callback) {
 

		if (path == null) {
			return null;
		}

		Bitmap bitmap = imageMemoryCache.getBitmapFromCache(path);
		if (bitmap != null) {
			Logger.i(TAG, "从内存读取成功");
			return bitmap;
		}

		// 如果缓存中不常在该图片，则创建图片下载任务
		Task task = new Task();
		task.path = path;
		task.callback = callback;
		Logger.i(TAG, "new Task ," + path);

		executorService.submit(new DownloadRunnable(task));//
 
		return null;
	}

	/**
	 * 
	 * @param imageView
	 * @param resId
	 *            图片加载完成前显示的图片资源ID
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageView imageView,
			final int resId) {
		return new ImageCallback() {

			@Override
			public void loadImage(String path, Bitmap bitmap) {
				// if(path.equals(imageView.getTag().toString())
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(resId);
				}
			}
		};
	}

	/**
	 * 
	 * @param imageView
	 * @param resId
	 *            图片加载完成前显示的图片资源ID
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageView imageView,
			final int resId, final int round) {
		return new ImageCallback() {

			@Override
			public void loadImage(String path, Bitmap bitmap) {
				// if(path.equals(imageView.getTag().toString())
				if (bitmap != null) {
					if (round > 0) {
						bitmap = toRoundCorner(bitmap, round);
						imageView.setImageBitmap(bitmap);
					} else {
						imageView.setImageBitmap(bitmap);
					}
				} else {
					imageView.setImageResource(resId);
				}
			}
		};
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			Task task = (Task) msg.obj;
			// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
			task.callback.loadImage(task.path, task.bitmap);
		}

	};

	class DownloadRunnable implements Runnable {
		Task task;

		public DownloadRunnable(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			task.bitmap = imageFileCache.getImage(task.path);
			if (task.bitmap == null) {
				// 将下载的图片添加到缓存
				task.bitmap = PicUtil.getbitmap(task.path);
				if (task.bitmap != null) {
					imageFileCache.saveBitmap(task.bitmap, task.path);
					imageMemoryCache.addBitmapToCache(task.path, task.bitmap);

					Logger.i(TAG, "从网络上读取成功" + Thread.currentThread().getId());
				}
			} else {
				imageMemoryCache.addBitmapToCache(task.path, task.bitmap);
				Logger.i(TAG, "从sdcard中读取成功" + Thread.currentThread().getId());
			}
 

			if (handler != null) {
				// 创建消息对象，并将完成的任务添加到消息对象中
				Message msg = handler.obtainMessage();
				msg.obj = task;
				// 发送消息回主线程
				handler.sendMessage(msg);
			}
		}
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			while (isRunning) {
				// 当队列中还有未处理的任务时，执行下载任务
				while (taskQueue.size() > 0) {
					// 获取第一个任务，并将之从任务队列中删除
					Task task = taskQueue.remove(0);

					task.bitmap = imageFileCache.getImage(task.path);
					if (task.bitmap == null) {
						// 将下载的图片添加到缓存
						task.bitmap = PicUtil.getbitmap(task.path);
						if (task.bitmap != null) {
							imageFileCache.saveBitmap(task.bitmap, task.path);
							imageMemoryCache.addBitmapToCache(task.path,
									task.bitmap);

							Logger.i(TAG, "从网络上读取成功");
						}
					} else {
						imageMemoryCache.addBitmapToCache(task.path,
								task.bitmap);
						Logger.i(TAG, "从sdcard中读取成功");
					}
 
					if (handler != null) {
						// 创建消息对象，并将完成的任务添加到消息对象中
						Message msg = handler.obtainMessage();
						msg.obj = task;
						// 发送消息回主线程
						handler.sendMessage(msg);
					}
				}

				// 如果队列为空,则令线程等待
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						Logger.e(e.toString());
					}
				}
			}
		}
	};

	// 回调接口
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}

	class Task {
		// 下载任务的下载路径
		String path;
		// 下载的图片
		Bitmap bitmap;
		// 回调对象
		ImageCallback callback;

		@Override
		public boolean equals(Object o) {
			Task task = (Task) o;
			return task.path.equals(path);
		}
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			return baos.toByteArray();
		} catch (Exception e) {
			return null;
		}

	}

	public static String byte2hex(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		String tmp = "";
		for (int n = 0; n < b.length; n++) {
			tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString().toUpperCase();
	}

	public void showImageAsyn(String url, ImageCallback imageCallback) {
		Bitmap bitmap = loadImageAsyn(url, imageCallback);
		if (bitmap != null) {
			imageCallback.loadImage(url, bitmap);
		}
	}
}