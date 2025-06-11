package com.iyuba.concept2.widget;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iyuba.concept2.R;
import com.iyuba.core.common.base.CrashApplication;

public class StopBellWindow extends Activity {
	private ImageView imageView;
	private Button stopButton;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.stopbell);
		CrashApplication.getInstance().addActivity(this);
		mContext=this;
		imageView=(ImageView)findViewById(R.id.stopbellimage);
		stopButton=(Button)findViewById(R.id.randombutton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    switch (keyCode) {
	        case KeyEvent.KEYCODE_BACK:
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		DisplayMetrics dm = new DisplayMetrics();
		   this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		   int width = dm.widthPixels;
		   int height = dm.heightPixels;
		   int width1;
		   int height1;
		   Random random=new Random();
		   width1=random.nextInt(width-40)+40;
		   height1=random.nextInt(height-40)+40;
		   RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(stopButton.getLayoutParams()); 
		  params.setMargins(width1, height1, 0, 0);
		   stopButton.setLayoutParams(params);
		   
		
	}



	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent=new Intent("killbell");
		sendBroadcast(intent);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
	}
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	

}
