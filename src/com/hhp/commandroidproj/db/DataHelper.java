package com.hhp.commandroidproj.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.hhp.commandroidproj.bean.joke;
 
/*
 * 记录在SharedPreference里的少量数据存储
 */
public class DataHelper {
	
	private static DataHelper mDataInstance;
	private Context mContext;
	public static final String DATA_COLLECTED = "data_collected";
	public ArrayList<joke> mDataList;
	
	private DataHelper(Context c){
		mContext = c;
		mDataList = new ArrayList<joke>();
	}
	
	public static DataHelper getInstance(Context c){
		if(mDataInstance == null){
			mDataInstance = new DataHelper(c);
		}
		return mDataInstance;
	}
	
	//添加收藏
	public void addJokeCollected(joke jk){
		if(jk == null)return;
		loadJokeListCollected();
		if(isJokeCollected(jk))return;
		mDataList.add(jk);
		saveListData(mDataList);
		Toast.makeText(mContext, "已收藏", 200).show();
	}
	
	//取消收藏
	public void delJokeCollected(joke jk){
		if(jk == null)return;
		loadJokeListCollected();
		for(int i = 0;i<mDataList.size();i++){
			if(mDataList.get(i).getjId() == jk.getjId()){
				mDataList.remove(i);
			}
		}
		saveListData(mDataList);
		Toast.makeText(mContext, "已取消收藏", 200).show();
	}
	
	public boolean isJokeCollected(joke jk){
		loadJokeListCollected();
		for(int i = 0;i<mDataList.size();i++){
			if(mDataList.get(i).getjId() == jk.getjId()){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<joke> loadJokeListCollected(){
		
		try {
			SharedPreferences mySharedPreferences = mContext.getSharedPreferences("base64", Activity.MODE_PRIVATE);
			String productBase64 = mySharedPreferences.getString(DATA_COLLECTED, "");
			// 对Base64格式的字符串进行解码
			byte[] base64Bytes = Base64.decodeBase64(productBase64.getBytes());
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			ArrayList<joke> list = (ArrayList<joke>) ois.readObject();
			mDataList = new ArrayList<joke>();
			mDataList.addAll(list); 
			Log.i("loadJokeListCollected:","success " +"size = "+mDataList.size() + productBase64 );
		} catch (Exception e) {

		} 
		return mDataList;
	}
	
	private void saveListData(Object o) {
		// TODO Auto-generated method stub
		try {
			if (o == null)
				return;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			SharedPreferences mySharedPreferences = mContext.getSharedPreferences(
					"base64", Activity.MODE_PRIVATE);
			String productBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			// 将编码后的字符串写到base64.xml文件中
			editor.putString(DATA_COLLECTED, productBase64);
			editor.commit();
			Log.i("saveListData:"," success" + productBase64);
		} catch (Exception e) {

		}
	}
	
}
