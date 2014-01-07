package com.hhp.commandroidproj.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commandroidproj.R;
import com.hhp.commandroidproj.bean.joke;
import com.hhp.commandroidproj.imageloader.ImageLoader;
import com.hhp.commandroidproj.network.AsyncHttpGet;
import com.hhp.commandroidproj.network.DefaultThreadPool;
import com.hhp.commandroidproj.network.RequestResultCallback;
import com.hhp.commandroidproj.parseHandler.json.JokeParseHandler;
import com.hhp.commandroidproj.ui.DragImageView;
import com.hhp.commandroidproj.ui.XListView;
import com.hhp.commandroidproj.ui.XListView.IXListViewListener;
import com.hhp.commandroidproj.utils.ApiConsts;
import com.hhp.commandroidproj.utils.Constants;
import com.hhp.commandroidproj.utils.StrUtils;

public class JokeListActivity extends BaseActivity implements OnClickListener,
		IXListViewListener {

	private XListView myListJoke;
	private AdapterJoke mAdapterJoke = new AdapterJoke();
	private ArrayList<joke> mArrayJoke = new ArrayList<joke>();
	private Button btnLeft, btnRight;
	private ImageLoader imgloader;
	private AsyncHttpGet httpget1 = null;
	private boolean mIsBottomRefresh = false;
	
	
	private final int HANDLER_DATA_UPDATE = 0; 
	private int PAGESIZE = 20;
	private int JOKE_TYPE = Constants.TYPE_JOKE_QQ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list);
		imgloader = new ImageLoader(this);

		findView();
		initView();
		initData();

	}

	private void findView() {
		// TODO Auto-generated method stub
		myListJoke = (XListView) findViewById(R.id.listviewMain);
		myListJoke.setAdapter(mAdapterJoke);
		myListJoke.setSelector(R.drawable.bg_list_item);
		myListJoke.setXListViewListener(this);
		myListJoke.setPullLoadEnable(true);
		myListJoke.setPullRefreshEnable(true);

	}

	private void initView() {

		initTitle();

		myListJoke.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// try{
				// Intent intent = new Intent();
				// intent.setClass(JokeListActivity.this,
				// JokeDetailActivity.class);
				// intent.putExtra("POSITION", position - 1);
				// startActivity(intent);
				// }
				// catch(Exception e){
				// }
			}
		});
	}

	private void initData() {
		// TODO Auto-generated method stub
		loadListData(JOKE_TYPE);
		if (mArrayJoke.size() == 0) {
			getData(0, PAGESIZE, JOKE_TYPE);
		}
	}

	private int getLastID() {
		int lastid = 0;
		try {
			lastid = mArrayJoke.get(mArrayJoke.size() - 1).getjId();
		} catch (Exception e) {
		}
		return lastid;
	}

	private void getData(int lastId, int pageSize, int Type) {

		String url = ApiConsts.API_JOKE_LIST + "?type=" + Type + "&ps="
				+ PAGESIZE + "&id=" + lastId;

		// TODO Auto-generated method stub
		httpget1 = new AsyncHttpGet(new JokeParseHandler(), url, null,
				new RequestResultCallback() {
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(Object o) {
						try {

							if (o != null && ((ArrayList<joke>) o).size() > 0) {
								if (!mIsBottomRefresh) {
									mArrayJoke.clear();
								}
								mArrayJoke.addAll((ArrayList<joke>) o);
								mRefreshHandler
										.sendEmptyMessage(HANDLER_DATA_UPDATE);
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

	Handler mRefreshHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_DATA_UPDATE:
				mAdapterJoke.notifyDataSetChanged();
				saveListData(JOKE_TYPE, mArrayJoke);
				onLoad();
				break;

			default:
				break;
			}
		};
	};

	private void initTitle() {
		// TODO Auto-generated method stub
		btnLeft = (Button) findViewById(R.id.btnLeft);
		btnRight = (Button) findViewById(R.id.btnRight);
 
		btnRight.setVisibility(View.VISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// myListJoke.iniList();
			}
		});

	}

	class AdapterJoke extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayJoke.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mArrayJoke.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder;
			if (null == convertView) {
				convertView = (View) getLayoutInflater().inflate(
						R.layout.joke_list_item, null);
				holder = new Holder(convertView);
			} else {
				holder = (Holder) convertView.getTag();
			}

			joke mj = mArrayJoke.get(position);
			if (!StrUtils.isEmpty(mj.getjImg())) {
				holder.imgPic.setVisibility(View.VISIBLE);
				holder.imgPic.setTag(mj.getjImg());
				imgloader.DisplayImage(mj.getjImg(), JokeListActivity.this,
						holder.imgPic);
			} else {
				holder.imgPic.setVisibility(View.GONE);
			}

			if (!StrUtils.isEmpty(mj.getjTitle())
					&& (!mj.getjTitle().equals("无标题"))) {
				holder.tvTitle.setText(mj.getjTitle());
				holder.tvTitle.setVisibility(View.VISIBLE);
			} else {
				holder.tvTitle.setVisibility(View.GONE);
			}

			holder.tvContent.setText(mj.getjContent());
			holder.imgPic.setOnClickListener(picClickListener);

			return convertView;
		}

	}

	private Dialog mDialog_detail; // 对话框（显示大图片的）
	private int window_width, window_height;// 屏幕的大小
	private int state_height;// 状态栏的高度
	private final int DIALOG_PADING = 0;// 对话框的边距（显示大图片的）

	OnClickListener picClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String url = (String) v.getTag();
			Log.i("url", url + "--");
			if (null != mDialog_detail) {
				if (mDialog_detail.isShowing()) {
					mDialog_detail.dismiss();
				} else {
					setDialogContentView(url);
					mDialog_detail.show();
				}
			} else {
				mDialog_detail = new Dialog(JokeListActivity.this,
						R.style.Transparent);
				setDialogWidth();
				setDialogContentView(url);
				mDialog_detail.show();
			}
		}
	};

	private void setDialogWidth() {
		/** 获取可見区域高度 **/
		WindowManager manager = (JokeListActivity.this).getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		// 获取状况栏高度
		Rect frame = new Rect();
		(JokeListActivity.this).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		state_height = frame.top;

		WindowManager.LayoutParams layoutParams = mDialog_detail.getWindow()
				.getAttributes();
		layoutParams.width = window_width;
		mDialog_detail.getWindow().setAttributes(layoutParams);
	}

	private void setDialogContentView(String url) {
		LinearLayout layout = new LinearLayout(JokeListActivity.this);
		ViewGroup.LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		layout.setPadding(DIALOG_PADING, DIALOG_PADING, DIALOG_PADING,
				DIALOG_PADING);
		LinearLayout layout2 = new LinearLayout(JokeListActivity.this);
		layout2.setBackgroundColor(Color.argb(255, 0, 0, 0));

		DragImageView dialogImageView = new DragImageView(JokeListActivity.this);
		dialogImageView.setScreen_H(window_height - state_height
				- DIALOG_PADING * 2);
		dialogImageView.setScreen_W(window_width - DIALOG_PADING * 2);
		dialogImageView.setmActivity((Activity) JokeListActivity.this);
		// asynImageLoader.showImageAsyn(dialogImageView,
		// url,R.drawable.guba_icon_default_pic);
		dialogImageView.setTag(url);
		imgloader.DisplayImage(url, JokeListActivity.this, dialogImageView);

		dialogImageView.setOnClickListener(JokeListActivity.this);

		layout2.addView(dialogImageView, params);
		layout.addView(layout2, params);
		mDialog_detail.setContentView(layout);
	}

	class Holder {
		ImageView imgPic;
		TextView tvTitle;
		TextView tvContent;

		public Holder(View view) {
			imgPic = (ImageView) view.findViewById(R.id.imgJokeImg);
			tvTitle = (TextView) view.findViewById(R.id.tvJokeTitle);
			tvContent = (TextView) view.findViewById(R.id.tvJokeContent);

			view.setTag(this);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String url = (String) v.getTag();
		Log.i("url = ", url);
		if (null != mDialog_detail) {
			if (mDialog_detail.isShowing()) {
				mDialog_detail.dismiss();
			} else {
				setDialogContentView(url);
				mDialog_detail.show();
			}
		} else {
			mDialog_detail = new Dialog(JokeListActivity.this,
					R.style.Transparent);
			setDialogWidth();
			setDialogContentView(url);
			mDialog_detail.show();
		}
	}

	private void saveListData(int TYPE, Object o) {
		// TODO Auto-generated method stub
		try {
			if (o == null)
				return;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			SharedPreferences mySharedPreferences = getSharedPreferences(
					"base64", Activity.MODE_PRIVATE);
			String productBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			// 将编码后的字符串写到base64.xml文件中
			editor.putString("JOKE_LIST_" + TYPE, productBase64);
			editor.commit();
			Log.i("saveListData:", " success" + productBase64);
		} catch (Exception e) {

		}
	}

	private void loadListData(int TYPE) {
		try {
			SharedPreferences mySharedPreferences = getSharedPreferences(
					"base64", Activity.MODE_PRIVATE);
			String productBase64 = mySharedPreferences.getString("JOKE_LIST_"
					+ TYPE, "");
			// 对Base64格式的字符串进行解码
			byte[] base64Bytes = Base64.decodeBase64(productBase64.getBytes());
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			ArrayList<joke> list = (ArrayList<joke>) ois.readObject();
			mArrayJoke.addAll(list);
			mAdapterJoke.notifyDataSetChanged();
			Log.i("loadListData:", "success " + "size = " + mArrayJoke.size()
					+ productBase64);
		} catch (Exception e) {

		}
	}

	public ArrayList<joke> getArrayJoke() {
		return mArrayJoke;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getData(0, PAGESIZE, JOKE_TYPE);
		mIsBottomRefresh = false;
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		getData(getLastID(), PAGESIZE, JOKE_TYPE);
		mIsBottomRefresh = true;
	}

	protected void onLoad() {
		myListJoke.stopRefresh();
		myListJoke.stopLoadMore();
		myListJoke.setRefreshTime("刚刚");
	}
}
