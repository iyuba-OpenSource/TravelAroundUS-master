package com.iyuba.concept2.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.BookDownloadAdapter;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;

public class BookDownloadActivity extends BasisActivity {
	private Context mContext;

	private GridView bookGridView;
	private BookDownloadAdapter bookAdapter;
	private ImageView backButton;

	private View backView;

	private Handler handlerDownload = new Handler();

	Runnable runnable = new Runnable() {
		public void run() {
			if (bookAdapter != null)
				bookAdapter.notifyDataSetChanged();
			handlerDownload.postDelayed(this, 500);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.easy_download);
		
		CrashApplication.getInstance().addActivity(this);

		mContext = this;

		backView = findViewById(R.id.backlayout);
		backView.setBackgroundColor(Color.WHITE);

		backButton = (ImageView) findViewById(R.id.button_back);
		bookGridView = (GridView) findViewById(R.id.book_list);

		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	public void init() {
		bookAdapter = new BookDownloadAdapter(mContext);
		bookGridView.setAdapter(bookAdapter);
		
		handlerDownload.postDelayed(runnable, 500);
	}
	
	protected void onResume() {
		super.onResume();
		
		init();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
