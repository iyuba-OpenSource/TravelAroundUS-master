package com.iyuba.core.common.adapter;
/**
 * 对滑动页面的适配 可通用
 * @author 陈彤
 * @version 1.0
 */
import java.util.ArrayList;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {
	private ArrayList<View> mListViews;

	public ViewPagerAdapter(ArrayList<View> list) {
		mListViews = list;
	}

	public void changeListView(ArrayList<View> list) {
		mListViews = list;
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		((ViewPager) collection).addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView(mListViews.get(position));
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
}
