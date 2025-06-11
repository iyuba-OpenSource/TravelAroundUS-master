package com.iyuba.concept2.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.TestResultDetail;

public class TestDetailAdapter extends BaseAdapter {

	private List<TestResultDetail> mList = new ArrayList<TestResultDetail>();
	private Context mContext;
	private LayoutInflater mInflater;
	private ViewHolder curViewHolder;
	private SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	String today;

	public TestDetailAdapter(Context mContext, List<TestResultDetail> mList) {
		this.mContext = mContext;
		this.mList = mList;
		today = dft.format(System.currentTimeMillis());
		mInflater = LayoutInflater.from(mContext);
	}

	public TestDetailAdapter(Context mContext) {
		this.mContext = mContext;
		today = dft.format(System.currentTimeMillis());
		mInflater = LayoutInflater.from(mContext);
	}

	public void addList(ArrayList<TestResultDetail> lwdList) {
		mList.addAll(lwdList);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final TestResultDetail curDetail = mList.get(position);

		final int curPosition = position;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.test_detail, null);

			curViewHolder = new ViewHolder();
			curViewHolder.time = (TextView) convertView.findViewById(R.id.time);
			curViewHolder.lessonId = (TextView) convertView
					.findViewById(R.id.lessonid);
			curViewHolder.quesNum = (TextView) convertView
					.findViewById(R.id.ques_num);
			curViewHolder.choices = (TextView) convertView
					.findViewById(R.id.choices);
			curViewHolder.ans = (TextView) convertView.findViewById(R.id.ans);
			curViewHolder.grade = (TextView) convertView
					.findViewById(R.id.grade);

			convertView.setTag(curViewHolder);
		} else {
			curViewHolder = (ViewHolder) convertView.getTag();
		}
		String ymd = curDetail.testTime.substring(0,
				curDetail.testTime.indexOf(" "));
		String hms = curDetail.testTime.substring(curDetail.testTime.indexOf(" "),curDetail.testTime.length());

		if (ymd.equals(today))
			curViewHolder.time.setText(hms);
		else
			curViewHolder.time.setText(curDetail.testTime);
		curViewHolder.lessonId.setText(curDetail.lessonId);
		curViewHolder.quesNum.setText(curDetail.testNum);
		curViewHolder.choices.setText(curDetail.userAnswer);
//		curViewHolder.choices.setMovementMethod(ScrollingMovementMethod.getInstance());  //实现textview竖直滑动
		curViewHolder.ans.setText(curDetail.rightAnswer);
		curViewHolder.grade.setText(curDetail.score);

		return convertView;
	}

	public static class ViewHolder {
		TextView time;
		TextView lessonId;
		TextView quesNum;
		TextView choices;
		TextView ans;
		TextView grade;
	}
}
