package com.hhp.commandroidproj.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commandroidproj.R;
import com.hhp.commandroidproj.utils.StrUtils;
 

/**
 * @author yaonan.hu
 * @time 2012-8-8 上午11:29:22
 */
public class ScoreView extends LinearLayout {
	
	public ScoreView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	private LayoutInflater mInflater;
	private TextView view1;
	private TextView view2;
	public ScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		
		mInflater.inflate(R.layout.score_view, this);
		view1 = (TextView) findViewById(R.id.score_view_1);
		view2 = (TextView) findViewById(R.id.score_view_2);
	}
	
	public void init(String arg1, String arg2) {
		view1.setText(arg1);
		view2.setText(arg2);
	}
	
	public void setText(String text) {
		if(StrUtils.isEmpty(text)) {
			view1.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
			return;
		}
		if(text.contains(".")) {
			int index = text.indexOf(".");
			view1.setText(text.substring(0, index)+" ");
			view2.setText(text.substring(index)+" ");
		} else {
			view1.setText(text+" ");
			view2.setText(".0 ");
		}
	}
	
	public void setText(String text, int size_1, int size_2){
		setText(text);
		if(size_1 > 0)
			view1.setTextSize(size_1);
		if(size_2 > 0)
			view2.setTextSize(size_2);
	}
	
	public void setTextColor(int color){
		view1.setTextColor(color);
		view2.setTextColor(color);
	}
}