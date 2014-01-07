package com.hhp.commandroidproj.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.example.commandroidproj.R;
import com.hhp.commandroidproj.utils.BitmapUtils;
 


public class ImageLoader {
 
	public static HashMap<String,SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>(); 
	public static final String PRO_DIR = "/commandroidproj"; 
	public static final String CACHE_DIR = PRO_DIR + "/images";
	public static final String CAMERA_TEMP = PRO_DIR + "/images/camera_temp.jpg";

	private File cacheDir; 
	private int corner;
	private PhotosLoader photoLoaderThread = new PhotosLoader();
	private PhotosQueue photosQueue = new PhotosQueue();
	final int stub_id = R.drawable.ic_launcher;
	
	
	public ImageLoader(Context context) {
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			cacheDir = new File(android.os.Environment
					.getExternalStorageDirectory(), CACHE_DIR);
		}else{
			cacheDir = context.getCacheDir();
		}
			
		if (!cacheDir.exists()){
			cacheDir.mkdirs();
		}else{
			File ff[] = cacheDir.listFiles();
			long totalLength = 0;
			for(File f : ff){
				totalLength += f.length();
			}
			if(totalLength > 50*1024*1024){
				clearCache();
			}
		}
	}

 


	public void DisplayImage(String url, Activity activity, ImageView imageView) {
		Log.i("DisplayImage", url);
		String newUrl = "";
		String reg = "^(http://)([\\w\\.]+/{1})";
		if(null == url || "null".equals(url)){
			imageView.setImageResource(stub_id);
			return;
		}
		String arr[] = url.split(reg);
		if(null != arr && arr.length > 1){
			newUrl = arr[1];
		}else{
			newUrl = url;
		}
		corner = 0;
		if (cache.containsKey(newUrl)){
			SoftReference<Bitmap> bmp = cache.get(newUrl);
			if(bmp == null || bmp.get() == null){
				queuePhoto(url, activity, imageView, corner);
				imageView.setImageResource(stub_id);
			}else{
				imageView.setImageBitmap(bmp.get());
			}
		}else {
			queuePhoto(url, activity, imageView , corner);
			imageView.setImageResource(stub_id);
		}
		
	}

	public void DisplayImage(String url, Activity activity, ImageView imageView , int corner) {
 
		this.corner = corner;
		String newUrl = "";
		String reg = "^(http://)([\\w\\.]+/{1})";
		String arr[] = url.split(reg);
		imageView.setTag(url);
		if(null != arr && arr.length > 1){
			newUrl = arr[1];
		}else{
			newUrl = url;
		}
		if (cache.containsKey(newUrl)){
			//如果从CACHE和softReference里获取不到缓存则queuePhoto
			SoftReference<Bitmap> bmp = cache.get(newUrl);
			if(bmp == null || bmp.get() == null){
				queuePhoto(url, activity, imageView, corner);
				imageView.setImageBitmap(BitmapUtils.toRoundCorner(BitmapFactory.decodeResource(activity.getResources(), stub_id), corner));
			}else{
				imageView.setImageBitmap(bmp.get());
			}
		}else {
			queuePhoto(url, activity, imageView , corner);
			imageView.setImageResource(stub_id);
		}
		this.corner = 0;
	}


	private void queuePhoto(String url, Activity activity, ImageView imageView , int corner) {
 
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		photoLoaderThread.setYuanjiao(corner);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW){
			photoLoaderThread.start();
		}
	}

	public Bitmap getBitmap(String url , Activity act) {
 
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String newUrl = "";
		String reg = "^(http://)([\\w\\.]+/{1})";
		String arr[] = url.split(reg);
		if(null != arr && arr.length > 1){
			newUrl = arr[1];
		}else{
			newUrl = url;
		}
		String filename = String.valueOf(newUrl.hashCode());
		File f = new File(cacheDir, filename);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;
		Bitmap bitmap = null;
		// from web
		try {
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			is.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return BitmapFactory.decodeResource(act.getResources(), R.drawable.ic_launcher);
		}
	}
	public Bitmap getBitmap(String url , ImageSwitcher mSwitcher) {
 
//			GifView gifView = (GifView)mSwitcher.getCurrentView();
			String newUrl = "";
			String reg = "^(http://)([\\w\\.]+/{1})";
			String arr[] = url.split(reg);
			if(null != arr && arr.length > 1){
				newUrl = arr[1];
			}else{
				newUrl = url;
			}
			String filename = String.valueOf(newUrl.hashCode());
			File f = new File(cacheDir, filename);

			// from SD cache
			Bitmap b = decodeFile(f);
			if (b != null){
//				gifView.stopPlayGif();
//				gifView.setBackgroundDrawable(null);
				return b;
			}
			Bitmap bitmap = null;
			// from web
			try {
				InputStream is = new URL(url).openStream();
				OutputStream os = new FileOutputStream(f);
				CopyStream(is, os);
				os.close();
				bitmap = decodeFile(f);
//				gifView.stopPlayGif();
//				gifView.setBackgroundDrawable(null);
				return bitmap;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return bitmap;
		}


	public Bitmap getBitmap(String url) {
 
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		Log.e("url = ", url);
		String newUrl = "";
		String reg = "^(http://)([\\w\\.]+/{1})";
		String arr[] = url.split(reg);
		if(null != arr && arr.length > 1){
			newUrl = arr[1];
		}else{
			newUrl = url;
		}
		
		Log.e("url2 = ", newUrl);
		
		String filename = String.valueOf(newUrl.hashCode());
		File f = new File(cacheDir, filename);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null){
			return b;
		}
		Bitmap bitmap = null;
		// from web
		try {
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	public Bitmap getBitmap(String url , int yuanjiao) {
 
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String newUrl = "";
		String reg = "^(http://)([\\w\\.]+/{1})";
		String arr[] = url.split(reg);
		if(null != arr && arr.length > 1){
			newUrl = arr[1];
		}else{
			newUrl = url;
		}
		String filename = String.valueOf(newUrl.hashCode());
		File f = new File(cacheDir, filename);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null){
			return BitmapUtils.toRoundCorner(b, yuanjiao);
		}
		Bitmap bitmap = null;
		// from web
		try {
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			if(null != bitmap){
				return BitmapUtils.toRoundCorner(bitmap, yuanjiao);
			}
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	// decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale += 0.5;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
 
		} catch (FileNotFoundException e) {
			
		} catch (OutOfMemoryError e) {
			cache.clear();
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	} 
	
	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	// stores list of photos to download
	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();j++) {
				if (photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
			}
		}
	}

	class PhotosLoader extends Thread {
		private int yuanjiao; 

		public int getYuanjiao() {
			return yuanjiao;
		} 
		public void setYuanjiao(int yuanjiao) {
			this.yuanjiao = yuanjiao;
		} 

		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp;
						if(yuanjiao > 0){
							bmp = getBitmap(photoToLoad.url , yuanjiao);
						}else{
							bmp = getBitmap(photoToLoad.url);
						}
						String newUrl = "";
						String reg = "^(http://)([\\w\\.]+/{1})";
						String arr[] = photoToLoad.url.split(reg);
						if(null != arr && arr.length > 1){
							newUrl = arr[1];
						}else{
							newUrl = photoToLoad.url;
						}
						cache.put(newUrl, new SoftReference<Bitmap>(bmp));
						if (((String) photoToLoad.imageView.getTag()).equals(photoToLoad.url)) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView);
							Activity a = (Activity) photoToLoad.imageView.getContext();
							a.runOnUiThread(bd);
						}

					}
					if (Thread.interrupted())
						break;
				}
			} catch (Exception e) {
				// allow thread to exit
				Log.i("Exception", e.toString());
			}
		}
	}



	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public void run() {
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
			else
				imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		// clear memory cache
		cache.clear(); 
		// clear SD cache
		File[] files = cacheDir.listFiles();
		if(files!=null){
			for (File f : files)
				f.delete();	
		}
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}


}
