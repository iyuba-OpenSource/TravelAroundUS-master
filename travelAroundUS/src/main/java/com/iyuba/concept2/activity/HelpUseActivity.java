package com.iyuba.concept2.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.TestFragmentAdapter;
import com.iyuba.concept2.adapter.ViewPagerAdapter;
import com.iyuba.concept2.widget.indicator.PageIndicator;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;

import java.util.ArrayList;

/**
 * 使用说明Activity
 */
public class HelpUseActivity extends BasisActivity {
	private ViewPager viewPager;
	private ArrayList<View> mListViews;

	private PageIndicator pi;
	private ViewPagerAdapter viewPagerAdapter;
	private TestFragmentAdapter testFragmentAdapter;
	private int lastIntoCount;
	private int goInfo = 0;// 0=第一次使用程序 1=从设置界面进入

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.help_use);

		
		CrashApplication.getInstance().addActivity(this);
		goInfo = this.getIntent().getIntExtra("isFirstInfo", 0);
		
		pi = (PageIndicator) findViewById(R.id.pageIndicator);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		testFragmentAdapter = new TestFragmentAdapter(fragmentManager);
		
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				pi.setCurrIndicator(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				switch (arg0) {
				case 0: // 停止变更
					if (viewPager.getCurrentItem() == testFragmentAdapter.getCount() - 1) {
						lastIntoCount = lastIntoCount + 1;
					}
					break;
				case 1:
					break;
				case 2: // 已经变更
					lastIntoCount = 0;
					break;
				}
				if (lastIntoCount > 1) {
					if (arg0 == 0) {
						if (goInfo == 0) {
								Intent intent = new Intent();
								intent.setClass(HelpUseActivity.this,
										MainFragmentActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("isFirstInfo", 0);
								ConfigManager.Instance().putInt("curBook", 1);
								startActivity(intent);
								finish();
						}
						finish();
					}
				}
			}
		});
		viewPager.setAdapter(testFragmentAdapter);
		pi.setIndicator(testFragmentAdapter.getCount());
		pi.setCurrIndicator(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


}