package com.hhp.commandroidproj.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.commandroidproj.R;
import com.hhp.commandroidproj.adapter.MovieGalleryAdapter;
import com.hhp.commandroidproj.bean.HotMovie;
import com.hhp.commandroidproj.bean.HotMovieFeed;
import com.hhp.commandroidproj.network.AsyncHttpGet;
import com.hhp.commandroidproj.network.DefaultThreadPool;
import com.hhp.commandroidproj.network.RequestResultCallback;
import com.hhp.commandroidproj.parseHandler.xml.CommSAXParserUtil;
import com.hhp.commandroidproj.ui.PosterGallery;
import com.hhp.commandroidproj.utils.ApiConsts;

public class GewaraActivity extends BaseActivity {

	HotMovieFeed hotMovieFeed;
	private PosterGallery movieGallery;
	private MovieGalleryAdapter<HotMovie> mFilmGalleryAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gewara);

		findView();

		getGewaraMovie();
	}

	private void initView() {

		// TODO Auto-generated method stub
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels;
		int screenHeight = metric.heightPixels;
		if (screenHeight > 900) {
			LinearLayout.LayoutParams params = (LayoutParams) movieGallery
					.getLayoutParams();
			params.height = screenHeight * 3 / 5 + 50;
			movieGallery.setLayoutParams(params);
		}
		movieGallery.setAdapter(mFilmGalleryAdapter);
		movieGallery.setAnimationDuration(200);
		movieGallery.setUnselectedAlpha(255);
		movieGallery.setSelection(0);
	}

	private void findView() {
		// TODO Auto-generated method stub
		movieGallery = (PosterGallery) findViewById(R.id.hot_movie_gallery);
		movieGallery.setVisibility(View.VISIBLE);
	}

	private void getGewaraMovie() {
		AsyncHttpGet httpget1 = new AsyncHttpGet(null, ApiConsts.GewaraUrl,
				null, new RequestResultCallback() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(Object o) {
						try {
							if (o != null) {

								CommSAXParserUtil parserUtil = new CommSAXParserUtil();
								hotMovieFeed = (HotMovieFeed) parserUtil
										.getFeed(
												CommSAXParserUtil.HOTMOVIEHANDLER,
												(String) o);

								mFilmGalleryAdapter = new MovieGalleryAdapter<HotMovie>(
										GewaraActivity.this, hotMovieFeed
												.getHotMovieList());
								
								mHandler.sendEmptyMessage(0);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(Exception e) {
						// TODO Auto-generated method stub

					}
				});

		DefaultThreadPool.getInstance().execute(httpget1);
		this.requestList.add(httpget1);
	}

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			initView();
		}
	};
}
