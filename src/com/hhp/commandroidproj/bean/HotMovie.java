 
package com.hhp.commandroidproj.bean;

import java.io.Serializable;

import com.hhp.commandroidproj.utils.StrUtils;

public class HotMovie implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String movieId = ""; // 影片ID
	public String movieName = ""; // 影片名称
	public String generalMark = ""; // 影片评分
	public String logo = ""; // 影片海报
	public String logo2list = "";// 影片海报小号用于LIST显示
	public String boughtcount = "";// 购票数
	public String highlight = "";// 影评
	public String edition = "";// 电影版本制式
	public String diffRelease = "";// 离上映的天数
	public String cinemaCount = "";// 今日上映电影院
	public String mpiCount = "";// 今日上映总场次
	public String videoid = "";// 预告片ID
	public String director = "";// 导演
	public String actor = "";// 演员
	public String releasedate = "";// 上映日期

	public String SPLITESTR = "&&&";
	public int INDEX = 15;// 总共字段。

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return movieId + SPLITESTR + movieName + SPLITESTR + generalMark
				+ SPLITESTR + logo + SPLITESTR + logo2list + SPLITESTR
				+ boughtcount + SPLITESTR + highlight + SPLITESTR + edition
				+ SPLITESTR + diffRelease + SPLITESTR + cinemaCount + SPLITESTR
				+ mpiCount + SPLITESTR + videoid + SPLITESTR + director
				+ SPLITESTR + actor + SPLITESTR + releasedate;
	}

	public HotMovie() {
		// TODO Auto-generated constructor stub
	}

	public HotMovie(String data) {
		// TODO Auto-generated constructor stub
		if (StrUtils.isEmpty(data)) {
			return;
		}
		String[] splitestr = data.split(SPLITESTR);
		if (splitestr != null && splitestr.length == INDEX) {
			movieId = splitestr[0];
			movieName = splitestr[1];
			generalMark = splitestr[2];
			logo = splitestr[3];
			logo2list = splitestr[4];
			boughtcount = splitestr[5];
			highlight = splitestr[6];
			edition = splitestr[7];
			diffRelease = splitestr[8];
			cinemaCount = splitestr[9];
			mpiCount = splitestr[10];
			videoid = splitestr[11];
			director = splitestr[12];
			actor = splitestr[13];
			releasedate = splitestr[14];
		}
	}
}
