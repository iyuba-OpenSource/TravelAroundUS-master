package com.iyuba.concept2.adapter;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
	private ArrayList<View> mListViews;

	public ViewPagerAdapter(ArrayList<View> list) {
		mListViews = list;
	}

	public void changeListView(ArrayList<View> list) {
		mListViews = list;
	}

	public int getCount() {
		return mListViews.size();
	}

	public Object instantiateItem(ViewGroup collection, int position) {
		((ViewPager) collection).addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}

	public void destroyItem(ViewGroup collection, int position, Object view) {
		((ViewPager) collection).removeView(mListViews.get(position));
	}

	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	public void finishUpdate(ViewGroup arg0) {
	}

	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	public Parcelable saveState() {
		return null;
	}

	public void startUpdate(ViewGroup arg0) {
	}
}
