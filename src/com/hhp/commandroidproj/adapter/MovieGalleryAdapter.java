package com.hhp.commandroidproj.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.commandroidproj.R;
import com.hhp.commandroidproj.bean.HotMovie;
import com.hhp.commandroidproj.imageloader.ImageLoader;
import com.hhp.commandroidproj.ui.ScoreView;

public class MovieGalleryAdapter<T> extends BaseAdapter {

	private Activity activity;
	private List<T> hotMovies;
	public ImageLoader imageLoader;
	private Context context;

	private int screenWidth;
	private int screenHeight;

	public MovieGalleryAdapter(Activity activity, List<T> hotMovieFeed) {
		this.activity = activity;
		this.hotMovies = hotMovieFeed;
		this.context = activity;
		imageLoader = new ImageLoader(activity.getApplicationContext());

		DisplayMetrics metric = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		screenWidth = metric.widthPixels;
		screenHeight = metric.heightPixels;
	}

	public List<T> getmList() {
		return hotMovies;
	}

	@Override
	public int getCount() {
		return hotMovies.size();
	}

	@Override
	public Object getItem(int position) {
		return hotMovies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater;
		ViewHolder holder;

		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.hot_movie_poster_item, null);
			holder = new ViewHolder();
			holder.imgPoster = (ImageView) convertView
					.findViewById(R.id.imgMoviePoster);
			holder.imgType = (ImageView) convertView
					.findViewById(R.id.imgMovieType);
			holder.tvMovieName = (TextView) convertView
					.findViewById(R.id.tvMovieName);
			holder.tvScore = (ScoreView) convertView
					.findViewById(R.id.tvMovieScore);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (hotMovies.get(position) instanceof HotMovie) {
			HotMovie fm = (HotMovie) hotMovies.get(position);
			holder.imgPoster.setScaleType(ImageView.ScaleType.FIT_XY);
			holder.imgPoster.setTag(fm.logo);
			int edition = MovieAppUtil.GetMoiveEdition(fm.edition);
			holder.imgType
					.setBackgroundResource(MovieAppUtil.IMG_MOVIE_EDITION[edition]);
			String showName = fm.movieName;
			if (showName.length() > 7) {
				holder.tvMovieName.setTextSize(15);
			}
			holder.tvMovieName.setText(fm.movieName);
			holder.tvScore.setText(fm.generalMark);

			imageLoader.DisplayImage(fm.logo, activity, holder.imgPoster);
		}

		// 将非高密度屏幕的Gallery layout 减小
		if (screenHeight > 900) {
			convertView.setLayoutParams(new Gallery.LayoutParams(
					screenHeight * 9 / 20, screenHeight * 3 / 5));
		} else {
			// if (Constant.largeScreen == true) {
			convertView.setLayoutParams(new Gallery.LayoutParams(360, 480));
			// } else {
			// convertView.setLayoutParams(new Gallery.LayoutParams(210, 280));
			// }
		}
		return convertView;

	}

	class ViewHolder {
		ImageView imgPoster;
		ImageView imgType;
		TextView tvMovieName;
		ScoreView tvScore;
	}

}
