package com.hhp.commandroidproj.parseHandler.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hhp.commandroidproj.bean.joke;
import com.hhp.commandroidproj.network.ParseHandler;

public class JokeParseHandler implements ParseHandler {

	@Override
	public Object handle(String str) {
		ArrayList<joke> listjoke = new ArrayList<joke>();

		try {
			str = str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1);
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("JOKE");
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject item = jsonArray.getJSONObject(i);
				joke mj = new joke();
				mj.setjId(item.getInt("ID"));
				mj.setjType(item.getString("TYPE"));
				mj.setjTag(item.getString("TAG"));
				mj.setjTime(item.getString("TIME"));
				mj.setjDing(item.getInt("DING"));
				mj.setjCai(item.getInt("CAI"));
				mj.setjTitle(item.getString("TITLE"));
				mj.setjContent(item.getString("CONTENT"));
				mj.setjSource(item.getString("SOURCE"));
				mj.setjImg(item.getString("IMG"));
				listjoke.add(mj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listjoke;

	}

}
