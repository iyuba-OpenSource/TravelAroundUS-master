package com.iyuba.concept2.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iyuba.concept2.R;

public class TestFragmentAdapter extends FragmentPagerAdapter {
	protected static final int[] CONTENT = new int[] {
			R.drawable.american_help1_0725,
			R.drawable.american_help2_0725,
			R.drawable.american_help3_0725,
			R.drawable.american_help4_0725,//help1
			R.drawable.american_help5_0725};

	public TestFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return TestFragment.newInstance(CONTENT[position]);
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}
}

