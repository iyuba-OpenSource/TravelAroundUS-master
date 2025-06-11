package com.iyuba.concept2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.VoaSummaryLanguageBean;

import java.util.ArrayList;

public class SummaryLanguageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VoaSummaryLanguageBean> mList = new ArrayList<VoaSummaryLanguageBean>();
	public boolean modeDelete = false;
	public ViewHolder viewHolder;

	public SummaryLanguageAdapter(Context context, ArrayList<VoaSummaryLanguageBean> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final VoaSummaryLanguageBean anno = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = vi.inflate(R.layout.listitem_summary_language, null);
			
			viewHolder = new ViewHolder();
			viewHolder.annoN = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.annoN.setTextColor(Constant.normalColor);
			viewHolder.annoN.setTextSize(Constant.textSize);
			viewHolder.note = (TextView) convertView.findViewById(R.id.note);
			viewHolder.noteEn = (TextView) convertView.findViewById(R.id.note_en);
			viewHolder.noteCH = (TextView) convertView.findViewById(R.id.note_ch);
			viewHolder.note.setTextColor(Constant.normalColor);
			viewHolder.note.setTextSize(Constant.textSize);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		int num=position+1;
		viewHolder.annoN.setText("" +num);
		viewHolder.noteEn.setText(anno.noteEn);
		viewHolder.noteCH.setText(anno.notoCh);
		viewHolder.note.setText(anno.sentence);
		
		return convertView;
	}

	public class ViewHolder {
		TextView annoN;
		TextView note;
		TextView noteCH;
		TextView noteEn;

	}

}
