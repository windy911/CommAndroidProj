package com.hhp.commandroidproj.parseHandler.xml;

import com.hhp.commandroidproj.imageloader.Logger;
import com.hhp.commandroidproj.network.ParseHandler;

public class GewaraParseHandler implements ParseHandler {

	@Override
	public Object handle(String str) {

		Logger.d(str);

		return str;
	}

}
