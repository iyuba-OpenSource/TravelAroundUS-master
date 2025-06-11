package com.iyuba.concept2.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.util.ReadBitmap;
import com.iyuba.configation.RuntimeManager;

public class TestFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	private int mContent;

	public static TestFragment newInstance(int content) {
		TestFragment fragment = new TestFragment();
		fragment.mContent = content;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getInt(KEY_CONTENT);
		}
		
		View root = inflater
				.inflate(R.layout.fragment_layout, container, false);
		ImageView iv = (ImageView) root.findViewById(R.id.iv);
		iv.setImageBitmap(ReadBitmap.readBitmap(RuntimeManager.getContext(), mContent));
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, mContent);
	}
}

