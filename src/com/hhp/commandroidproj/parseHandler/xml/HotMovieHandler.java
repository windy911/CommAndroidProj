package com.hhp.commandroidproj.parseHandler.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.hhp.commandroidproj.bean.HotMovie;
import com.hhp.commandroidproj.bean.HotMovieFeed;
import com.hhp.commandroidproj.utils.StrUtils;

public class HotMovieHandler extends GewaraSAXHandler {
	private HotMovieFeed hotMovieFeed;
	private HotMovie hotMovie;
	private final int HOTMOVIE_MOVIEID = 1;
	private final int HOTMOVIE_MOVIENAME = 2;
	private final int HOTMOVIE_LOGO = 3;
	private final int HOTMOVIE_GENERALMARK = 4;
	private final int HOTMOVIE_BOUGHTCOUNT = 5;
	private final int HOTMOVIE_HIGHLIGHT = 6;
	private final int HOTMOVIE_EDITION = 7;
	private final int HOTMOVIE_DIFFRELEASE = 8;
	private final int HOTMOVIE_CINEMACOUNT = 9;
	private final int HOTMOVIE_MPICOUNT = 10;
	private final int HOTMOVIE_VIDEOID = 11;
	private final int HOTMOVIE_DIRECTOR = 12;
	private final int HOTMOVIE_ACTOR = 13;
	private final int HOTMOVIE_RELEASEDATE = 14;
	private int currentstate = 0;
	private StringBuffer sb;

	@Override
	public HotMovieFeed getFeed() {
		return hotMovieFeed;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String s = new String(ch, start, length);
		sb.append(s);
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("movie")) {
			hotMovieFeed.addItem(hotMovie);
			return;
		}
		switch (currentstate) {
		case HOTMOVIE_MOVIEID:
			hotMovie.movieId = StrUtils.replaceWhiteSpace(sb.toString());
			currentstate = 0;
			break;
		case HOTMOVIE_MOVIENAME:
			hotMovie.movieName = StrUtils.replaceWhiteSpace(sb.toString());
			currentstate = 0;
			break;
		case HOTMOVIE_LOGO:
			hotMovie.logo = StrUtils.replaceWhiteSpace(sb.toString());
			String logo2logo = hotMovie.logo;
			// 增加返回海报图片的宽高属性
			hotMovie.logo += "?h=320" + "&" + "w=240" + "&r=c";
			hotMovie.logo2list = logo2logo + "?h=160" + "&" + "w=120" + "&r=c";
			currentstate = 0;
			break;
		case HOTMOVIE_GENERALMARK:
			hotMovie.generalMark = StrUtils.replaceWhiteSpace(sb.toString());
			currentstate = 0;
			break;
		case HOTMOVIE_BOUGHTCOUNT:
			hotMovie.boughtcount = StrUtils.replaceWhiteSpace(sb.toString());
			currentstate = 0;
			break;
		case HOTMOVIE_HIGHLIGHT:
			hotMovie.highlight = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;
		case HOTMOVIE_EDITION:
			hotMovie.edition = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;
		case HOTMOVIE_DIFFRELEASE:
			hotMovie.diffRelease = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;
		case HOTMOVIE_CINEMACOUNT:
			hotMovie.cinemaCount = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;
		case HOTMOVIE_MPICOUNT:
			hotMovie.mpiCount = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;

		case HOTMOVIE_VIDEOID:
			hotMovie.videoid = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;

		case HOTMOVIE_DIRECTOR:
			hotMovie.director = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;

		case HOTMOVIE_ACTOR:
			hotMovie.actor = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;

		case HOTMOVIE_RELEASEDATE:
			hotMovie.releasedate = StrUtils.stripEnd(sb.toString(), null);
			currentstate = 0;
			break;

		default:
			currentstate = 0;
			return;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		hotMovieFeed = new HotMovieFeed();
		hotMovie = new HotMovie();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		sb = new StringBuffer();
		if ("data".equals(localName)) {
			currentstate = 0;
		} else if ("movie".equals(localName)) {
			hotMovie = new HotMovie();
		} else if ("movieid".equals(localName)) {
			currentstate = HOTMOVIE_MOVIEID;
		} else if ("moviename".equals(localName)) {
			currentstate = HOTMOVIE_MOVIENAME;
		} else if ("generalmark".equals(localName)) {
			currentstate = HOTMOVIE_GENERALMARK;
		} else if ("logo".equals(localName)) {
			currentstate = HOTMOVIE_LOGO;
		} else if ("boughtcount".equals(localName)) {
			currentstate = HOTMOVIE_BOUGHTCOUNT;
		} else if ("highlight".equals(localName)) {
			currentstate = HOTMOVIE_HIGHLIGHT;
		} else if ("gcedition".equals(localName)) {
			currentstate = HOTMOVIE_EDITION;
		} else if ("diffrelease".equals(localName)) {
			currentstate = HOTMOVIE_DIFFRELEASE;
		} else if ("cinemacount".equals(localName)) {
			currentstate = HOTMOVIE_CINEMACOUNT;
		} else if ("mpicount".equals(localName)) {
			currentstate = HOTMOVIE_MPICOUNT;
		} else if ("videoid".equals(localName)) {
			currentstate = HOTMOVIE_VIDEOID;
		} else if ("director".equals(localName)) {
			currentstate = HOTMOVIE_DIRECTOR;
		} else if ("actors".equals(localName)) {
			currentstate = HOTMOVIE_ACTOR;
		} else if ("releasedate".equals(localName)) {
			currentstate = HOTMOVIE_RELEASEDATE;
		}

		else
			currentstate = 0;
	}

}
