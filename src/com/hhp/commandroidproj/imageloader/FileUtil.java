package com.hhp.commandroidproj.imageloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
 

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
//import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";
	//
	// private String path;

	private static final String path = Environment.getExternalStorageDirectory()
			.getPath() + "/fund_weibo_image/";

	/**
	 * 保存图片到SD卡上
	 * 
	 * @param bm
	 * @param fileName
	 */
	public static void saveFile(Bitmap bm, String url) {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (!sdCardExist || bm==null) {
			return;
		}

		// 获得文件名字
		final String fileNa = getFileName(url);
		File file = new File(path + fileNa);

		File maiduo = new File(path);
		// 如果文件夹不存在
		if (!maiduo.exists()) {
			// 按照指定的路径创建文件夹
			maiduo.mkdir();
		}

		// 检查图片是否存在
		try {
			if (!file.exists()) {
				file.createNewFile();
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
				
				Logger.i(TAG, "保存成功");
			}
		} catch (FileNotFoundException e) {
			Logger.i(TAG, "保存失败FileNotFoundException");
			Logger.e(e.toString());
		} catch (IOException e) {
			Logger.i(TAG, "保存失败IOException");
			Logger.e(e.toString());
		}
	}

	/**
	 * 
	 * 使用SD卡上的图片
	 * 
	 * 
	 */

	public static Bitmap useTheImage(String imageUrl) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (!sdCardExist) {
			return null;
		}

		Bitmap bmpDefaultPic = null;

		// 获得文件路径
		String imageSDCardPath = path + getFileName(imageUrl);

		File file = new File(imageSDCardPath);

		// 检查图片是否存在
		if (!file.exists()) {
			return null;
		}

		bmpDefaultPic = BitmapFactory.decodeFile(imageSDCardPath, null);

		return bmpDefaultPic;

	}

	public static String getFileName(String url) {
		int index = url.indexOf("/", 10);
		return url.substring(index + 1).replace("/", "_");
	}

	// public static File getCacheFile(String imageUri) {
	// File cacheFile = null;
	// try {
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// File sdCardDir = Environment.getExternalStorageDirectory();
	// String fileName = getFileName(imageUri);
	// File dir = new File(sdCardDir.getCanonicalPath()
	// + AsynImageLoader.CACHE_DIR);
	// if (!dir.exists()) {
	// dir.mkdirs();
	// }
	// cacheFile = new File(dir, fileName);
	// Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir
	// + ",file:" + fileName);
	// }
	// } catch (IOException e) {
	// Logger.e(e.toString());
	// Log.e(TAG, "getCacheFileError:" + e.getMessage());
	// }
	//
	// return cacheFile;
	// }
}