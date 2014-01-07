package com.hhp.commandroidproj.parseHandler.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hhp.commandroidproj.bean.Feed;

public class CommSAXParserUtil {
	public static final int EXPRESS_HANDLER = 1; //

	private SAXParserFactory factory;
	private SAXParser parser;
	private GewaraSAXHandler handler;
	private int handlerName;
	public static final int HOTMOVIEHANDLER = 1;

	public CommSAXParserUtil() {
		try {
			factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public Feed getFeed(int theHandler, InputStream in) {
		handlerName = theHandler;
		this.handler = getSAXHandler();
		try {
			parser.parse(in, this.handler);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return handler.getFeed();
	}

	public Feed getFeed(int theHandler, String xml) {
		handlerName = theHandler;
		this.handler = getSAXHandler();
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		try {
			parser.parse(source, handler);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handler.getFeed();
	}

	private GewaraSAXHandler getSAXHandler() {
		GewaraSAXHandler handler = null;
		switch (handlerName) {

		case HOTMOVIEHANDLER:
			handler = new HotMovieHandler();
			break;

		default:
			break;
		}
		return handler;
	}
}
