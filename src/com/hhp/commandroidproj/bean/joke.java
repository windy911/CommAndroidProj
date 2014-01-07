package com.hhp.commandroidproj.bean;

import java.io.Serializable;

import com.hhp.commandroidproj.utils.StrUtils;

public class joke implements Serializable {

	public int jId;
	public String jType;
	public String jTag;
	public String jTime;
	public int jDing;
	public int jCai;
	public String jTitle;
	public String jContent;
	public String jSource;
	public String jImg;

	public int getjId() {
		return jId;
	}

	public void setjId(int jId) {
		this.jId = jId;
	}

	public String getjType() {
		return jType;
	}

	public void setjType(String jType) {
		this.jType = jType;
	}

	public String getjTag() {
		return jTag;
	}

	public void setjTag(String jTag) {
		this.jTag = jTag;
	}

	public String getjTime() {
		return jTime;
	}

	public void setjTime(String jTime) {
		this.jTime = jTime;
	}

	public int getjDing() {
		return jDing;
	}

	public void setjDing(int jDing) {
		this.jDing = jDing;
	}

	public int getjCai() {
		return jCai;
	}

	public void setjCai(int jCai) {
		this.jCai = jCai;
	}

	public String getjTitle() {
		return jTitle;
	}

	public void setjTitle(String jTitle) {
		this.jTitle = jTitle;
	}

	public String getjContent() {
		return jContent;
	}

	public void setjContent(String jContent) {
		this.jContent = jContent;
	}

	public String getjSource() {
		return jSource;
	}

	public void setjSource(String jSource) {
		this.jSource = jSource;
	}

	public String getjImg() {
		return StrUtils.delBlank(jImg);
	}

	public void setjImg(String jImg) {
		this.jImg = jImg;
	}
}
