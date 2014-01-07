package com.hhp.commandroidproj.bean;

import java.io.Serializable;
import java.util.HashMap;

public class Feed implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3882593805010103829L;
	public String code;
	public String error;
	private static HashMap<String,String> propertyMap;
	static{
		propertyMap = new HashMap<String, String>();
		propertyMap.put("code", "code");
		propertyMap.put("error", "error");
	}
	public static HashMap<String,String> getParserPropertyMap(){
		return propertyMap;
	}
}
