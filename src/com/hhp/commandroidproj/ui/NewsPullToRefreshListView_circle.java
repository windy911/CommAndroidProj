package com.hhp.commandroidproj.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commandroidproj.R;

 
//底部加载方式为 见到底部自动加载
public class NewsPullToRefreshListView_circle extends ListView implements
		OnScrollListener, PullToRefreshCallBack {

	private static final String TAG = "listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 2;
	private boolean bottomFreshOpen = true;
	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private Button mRefreshButton;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;
	private Handler mHandler = new Handler();
	private int stateUP;
	private int stateDown;
	private boolean isBack;
	private NewsRefreshListener refreshListener;
	private boolean isRefreshable;
	private boolean isRefreshValid = true;// 是否支持下拉刷新
	private boolean isRefreshBottomAuto = true;// 是否滑倒底部自动加载，false则为点击更多加载。
	private RelativeLayout mRefreshView2;
	private ProgressBar mRefreshViewProgress2;

	public boolean isRefreshBottomAuto() {
		return isRefreshBottomAuto;
	}

	public void setRefreshBottomAuto(boolean isRefreshBottomAuto) {
		this.isRefreshBottomAuto = isRefreshBottomAuto;
	}

	public boolean isRefreshValid() {
		return isRefreshValid;
	}

	public void setRefreshValid(boolean isRefreshValid) {
		this.isRefreshValid = isRefreshValid;
	}

	public boolean isRefreshable() {
		return isRefreshable;
	}

	public void setRefreshable(boolean isRefreshable) {
		this.isRefreshable = isRefreshable;
	}

	public NewsPullToRefreshListView_circle(Context context) {
		super(context);
		init(context);
	}

	public NewsPullToRefreshListView_circle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
	 
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.item_refresh_head,
				null);

		mRefreshView2 = (RelativeLayout) inflater.inflate(
				R.layout.pull_to_refresh_footer, this, false);
		mRefreshViewProgress2 = (ProgressBar) mRefreshView2
				.findViewById(R.id.pull_to_refresh_progress);
		mRefreshViewProgress2.setVisibility(View.VISIBLE);

		mRefreshButton = (Button) mRefreshView2.findViewById(R.id.button1);
		mRefreshButton.setVisibility(View.GONE);
		mRefreshButton.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub 
				mRefreshButton.setVisibility(View.GONE);
				mRefreshViewProgress2.setVisibility(View.VISIBLE); 
				onGetDown();
			} 
		});

		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		// Log.v("size", "width:" + headContentWidth + " height:"
		// + headContentHeight);

		addHeaderView(headView, null, false);

		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		stateUP = DONE;
		stateDown = DONE;
		isRefreshable = false;
		// iniList();
	}

	public void reset() {
		// resetHeader();
		resetFooter();
	}

	private void resetFooter() {
		// log("xxxxxxx", "resetHeader");

		if (getFooterViewsCount() > 0) {
			try {
				removeFooterView(mRefreshView2);
			} catch (Exception e) {
			}
		}
		// mRefreshViewProgress2.setVisibility(View.INVISIBLE);
	}

	public void prepareForRefreshBottom() {
		// log("xxxxxxx", "prepareForRefreshBottom");
		if (getFooterViewsCount() == 0)
			addFooterView(mRefreshView2);
	}

	public void removeHeaderFooter() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (getFooterViewsCount() == 1) {
					removeFooterView(mRefreshView2);
				}
				bottomFreshOpen = false;
			}

		});

	}

	public void setFooterHeight(int height) {
		android.view.ViewGroup.LayoutParams params = mRefreshView2
				.getLayoutParams();
		params.height = height;
		mRefreshView2.setLayoutParams(params);
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
		// if(arg3>(arg2+firstVisiableItem)){
		// prepareForRefreshBottom();
		// }
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		if (bottomFreshOpen
				&& this.getLastVisiblePosition() == this.getCount() - 1
				&& getFooterViewsCount() != 0 && isRefreshBottomAuto) {
			onGetDown();
		}
	}

	public void resetFooter(Boolean isFootAvilable) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (getFooterViewsCount() == 1) {
					removeFooterView(mRefreshView2);
				}
			}

		});
		bottomFreshOpen = isFootAvilable;
	}

	public void iniList() {
		if (!isRefreshable)
			return;

		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {

				stateUP = DONE;
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0,
						100, 0);
				dispatchTouchEvent(e);
				for (int i = 110; i < headContentHeight * 4 + 100; i += 100) {

					// Log.e("listview", ""+i);
					MotionEvent e1 = MotionEvent.obtain(
							SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, 100, i, 0);
					onTouchEvent(e1);

				}
				// MotionEvent e1 =
				// MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
				// MotionEvent.ACTION_MOVE, 100, 200, 0);
				// MotionEvent e2 =
				// MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
				// MotionEvent.ACTION_MOVE, 100, 300, 0);
				// MotionEvent e3 =
				// MotionEvent.obtain(SystemClock.uptimeMillis(),current+300,
				// MotionEvent.ACTION_MOVE, 100, 400, 0);
				MotionEvent e4 = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 100,
						headContentHeight * 4 + 100, 0);

				onTouchEvent(e4);

			}

		}.execute();

	}

	private int mLastMotionY;

	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					// Log.v(TAG, "在down时候记录当前位置‘");
				}
				mLastMotionY = (int) event.getY();
				break;

			case MotionEvent.ACTION_UP:
				if (!isRefreshValid)
					break;
				if (stateUP != REFRESHING && stateUP != LOADING) {
					if (stateUP == DONE) {
						// 什么都不做
					}
					if (stateUP == PULL_To_REFRESH) {
						stateUP = DONE;
						changeHeaderViewByState();

						// Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (stateUP == RELEASE_To_REFRESH) {
						stateUP = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						isRefreshable = false;
						// Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				// Log.e("aaaa",this.computeVerticalScrollOffset()+" "+this.computeVerticalScrollExtent()+" "+this.computeHorizontalScrollRange());

				if (bottomFreshOpen && getFooterViewsCount() == 0
						&& this.computeVerticalScrollOffset() > 0
						&& event.getY() < mLastMotionY) {
					prepareForRefreshBottom();

				}

				if (!isRefreshValid)
					break;

				if (!isRecored && firstItemIndex == 0) {
					// Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (stateUP != REFRESHING && isRecored && stateUP != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (stateUP == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							stateUP = PULL_To_REFRESH;
							changeHeaderViewByState();

							// Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							stateUP = DONE;
							changeHeaderViewByState();

							// Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (stateUP == PULL_To_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							stateUP = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							// Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							stateUP = DONE;
							changeHeaderViewByState();

							// Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (stateUP == DONE) {
						if (tempY - startY > 0) {
							stateUP = PULL_To_REFRESH;
							changeHeaderViewByState();
							// Log.v(TAG, "更细nsize");
						}
					}

					// 更新headView的size
					if (stateUP == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
						// Log.v(TAG, "更细ssize");

					}

					// 更新headView的paddingTop
					if (stateUP == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (stateUP) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开刷新");

			// Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.INVISIBLE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			// Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.INVISIBLE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.INVISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow_down);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void onRefreshComplete() {
		onRefreshComplete(null, UP);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub

		super.setAdapter(adapter);
		checkBottom();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
			checkBottom();
		}
	}

	public void checkBottom() {
		onRefreshComplete(null, DOWN);
 
	}

	public void setBottomEnable(Boolean b) {
		bottomFreshOpen = b;
		checkBottom();
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String time = format.format(new Date()); 
		lastUpdatedTextView.setText("最近更新:" + time);
		super.setAdapter(adapter);
	}

	@Override
	public void onRefreshComplete(CharSequence lastUpdated, int upDown) {
		if (upDown == UP)
			// TODO Auto-generated method stub
			stateUP = DONE;
		else {
			stateDown = DONE;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String time = format.format(new Date()); 
		lastUpdatedTextView.setText("最近更新:" + time);
		changeHeaderViewByState();

		isRefreshable = true;

		if (!isRefreshBottomAuto) {
			if (mRefreshView2.getVisibility() != View.VISIBLE) {
				mRefreshView2.setVisibility(View.VISIBLE);
			}
			mRefreshButton.setVisibility(View.VISIBLE);
			mRefreshViewProgress2.setVisibility(View.GONE);
		}

	}

	@Override
	public void setOnRefreshListener(NewsRefreshListener onRefreshListener) {
		// TODO Auto-generated method stub
		refreshListener = onRefreshListener;
		isRefreshable = true;
	}

	public synchronized void onGetDown() {
		onRefreshComplete(null, UP);
		if (refreshListener != null) {

			if (stateDown == DONE) {
				stateDown = LOADING;
				refreshListener.onGetDown();
			}
		}
	}

}
