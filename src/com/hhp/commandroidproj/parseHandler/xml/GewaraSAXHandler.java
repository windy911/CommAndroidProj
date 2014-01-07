package com.hhp.commandroidproj.parseHandler.xml;

import org.xml.sax.helpers.DefaultHandler;

import com.hhp.commandroidproj.bean.Feed;
 

public abstract class GewaraSAXHandler extends DefaultHandler{
	public final static int DATA_CODE = -1;
	public final static int DATA_ERROR = -2;
	public abstract Feed getFeed();
}
