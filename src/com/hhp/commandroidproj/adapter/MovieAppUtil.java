package com.hhp.commandroidproj.adapter;

import com.example.commandroidproj.R;
import com.hhp.commandroidproj.utils.StrUtils;

public class MovieAppUtil {

	public static final int MOVIE_EDITION_2D = 0;
	public static final int MOVIE_EDITION_3D = 1;
	public static final int MOVIE_EDITION_IMAX = 2;
	public static final int MOVIE_EDITION_IMAX3D = 3;
	public static final int MOVIE_EDITION_4D = 4;

	public static final String EDITION_2D = "2D";
	public static final String EDITION_3D = "3D";
	public static final String EDITION_IMAX = "Imax";
	public static final String EDITION_IMAX2 = "IMAX";
	public static final String EDITION_IMAX3D = "Imax3D";
	public static final String EDITION_IMAX3D2 = "IMAX3D";
	public static final String EDITION_4D = "4D";

	public static int GetMoiveEdition(String edition) {
		if (StrUtils.isEmpty(edition))
			return 0;
		int retEdition = MOVIE_EDITION_2D;
		if (edition.contains(EDITION_4D)) {
			retEdition = MOVIE_EDITION_4D;
		} else if (edition.contains(EDITION_IMAX3D)
				|| edition.contains(EDITION_IMAX3D2)) {
			retEdition = MOVIE_EDITION_IMAX3D;
		} else if (edition.contains(EDITION_IMAX)
				|| edition.contains(EDITION_IMAX2)) {
			retEdition = MOVIE_EDITION_IMAX;
		} else if (edition.contains(EDITION_3D)) {
			retEdition = MOVIE_EDITION_3D;
		}
		return retEdition;
	}

	public static final int[] IMG_MOVIE_EDITION = { 0, R.drawable.movie_type3d,
			R.drawable.movie_typeimax, R.drawable.movie_typeimax3d,
			R.drawable.movie_type4d };

	public static final int[] IMG_MOVIE_EDITION_SMALL = { 0,
			R.drawable.movie_typesmall_3d, R.drawable.movie_typesmall_imax,
			R.drawable.movie_typesmall_imax3d, R.drawable.movie_typesmall_4d };

	public static boolean isNewFilm(String diffrelease, String releasedate) {
		if (StrUtils.isEmpty(diffrelease) || StrUtils.isEmpty(releasedate))
			return false;
		int value = Integer.valueOf(diffrelease);

		if (value > -3 & value < 3) {
			return true;
		}
		return false;
	}

	public static boolean isPresell(String ispresell) {
		if (StrUtils.isEmpty(ispresell))
			return false;
		return ispresell.equals("true") ? true : false;
	}
}
