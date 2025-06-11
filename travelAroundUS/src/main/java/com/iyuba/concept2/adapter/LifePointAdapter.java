package com.iyuba.concept2.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.VoaLifePointBean;

import java.util.ArrayList;

public class LifePointAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VoaLifePointBean> mList = new ArrayList<VoaLifePointBean>();
	public boolean modeDelete = false;
	public ViewHolder viewHolder;

	public LifePointAdapter(Context context, ArrayList<VoaLifePointBean> list) {
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
		final VoaLifePointBean anno = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = vi.inflate(R.layout.listitem_voa_annotation, null);
			
			viewHolder = new ViewHolder();
			viewHolder.annoN = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.annoN.setTextColor(Constant.normalColor);
			viewHolder.annoN.setTextSize(Constant.textSize);
			viewHolder.note = (TextView) convertView.findViewById(R.id.note);
			viewHolder.note.setTextColor(Constant.normalColor);
			viewHolder.note.setTextSize(Constant.textSize);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.annoN.setText("" + anno.number);
		viewHolder.note.setText(Html.fromHtml(anno.note));
		
		return convertView;
	}

	public class ViewHolder {
		TextView annoN;
		TextView note;
	}

}
