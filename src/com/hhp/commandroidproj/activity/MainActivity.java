package com.hhp.commandroidproj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.commandroidproj.R;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		TextView tvSection0 = (TextView) findViewById(R.id.tvSection0);
		TextView tvSection1 = (TextView) findViewById(R.id.tvSection1);
		tvSection0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(MainActivity.this, JokeListActivity.class);
				startActivity(i);
			}
		});
		tvSection1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(MainActivity.this, GewaraActivity.class);
				startActivity(i);
			}
		});
	}

}
