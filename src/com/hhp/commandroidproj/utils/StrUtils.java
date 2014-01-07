package com.hhp.commandroidproj.utils;

public class StrUtils {

	public static boolean isEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else
			return false;
	}

	public static String delBlank(String str) {
		return str.replace(" ", "");
	}

	public static String stripEnd(String str, String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}

		if (stripChars == null) {
			while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
				end--;
			}
		} else if (stripChars.length() == 0) {
			return str;
		} else {
			while ((end != 0)
					&& (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
				end--;
			}
		}
		return str.substring(0, end);
	}

	public static String null2Zero(String str) {
		if (str == null || str.equals("")) {
			return "0";
		} else {
			return str;
		}

	}

	public static String replaceWhiteSpace(String str) {
		return str.replaceAll("\n", "");
	}

	public static String replaceTableSpace(String str) {
		return str.replaceAll(" ", "").replaceAll("\t", "");
	}

	public static String trimForFront(String str) {
		StringBuffer sb = new StringBuffer();
		boolean first = false;
		char aa;
		for (int i = 0, length = str.length(); i < length; i++) {
			aa = str.charAt(i);
			if (!first && aa == '\t') {
			} else {
				first = true;
				sb.append(aa);
			}
		}

		return sb.toString();
	}

	public static String trimAllWhitespace(String str) {
		return str.replaceAll(" ", "").replaceAll("\n", "")
				.replaceAll("\t", "").toString();
	}
}
