package com.iyuba.concept2.widget.indicator;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.concept2.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ExercisePageIndicator extends LinearLayout {

	private Context mContext;
	private List<ImageView> indicators = new ArrayList<ImageView>();
	private int currPage = 0;

	public ExercisePageIndicator(Context context) {
		super(context);
		initPageIndicator(context);
	}

	public ExercisePageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPageIndicator(context);
	}

	public void initPageIndicator(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		refPageIndicator();

	}

	public void refPageIndicator() {
		if (indicators != null && indicators.size() != 0) {
			for (int i = 0; i < indicators.size(); i++) {
				ImageView ivTemp = indicators.get(i);
				addView(ivTemp);
			}
		} else {
			ImageView ivTemp = new ImageView(mContext);
			ivTemp.setPadding(8, 5, 8, 5);
			ivTemp.setImageResource(R.drawable.point_blue);
			indicators.add(ivTemp);
			addView(ivTemp);
		}
	}

	public void setIndicator(int pageNum) {
		indicators.clear();
		removeAllViews();
		for (int i = 0; i < pageNum; i++) {
			ImageView ivTemp = new ImageView(mContext);
			ivTemp.setPadding(8, 5, 8, 5);
			ivTemp.setImageResource(R.drawable.point_gray);
			indicators.add(ivTemp);
		}
		refPageIndicator();
		setCurrIndicator(currPage);
	}

	public void setCurrIndicator(int currPage) {
		this.currPage = currPage;
		for (int i = 0; i < indicators.size(); i++) {
			ImageView ivTemp = indicators.get(i);
			if (i == this.currPage) {
				ivTemp.setImageResource(R.drawable.point_blue);

			} else {
				ivTemp.setImageResource(R.drawable.point_gray);
			}
		}
	}

}
