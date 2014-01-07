package com.hhp.commandroidproj.network;
 
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder; 
 

public class Utils {
 
	public static String encode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20")
					.replace("*", "%2A").replace("%7E", "~")
					.replace("#", "%23");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

 
	public static String decode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
}

