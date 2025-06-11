package com.iyuba.concept2.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.KeyStructureExercise;

public class KeyStructureExerciseAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<KeyStructureExercise> mList = new ArrayList<KeyStructureExercise>();
	public boolean modeDelete = false;
	public ViewHolder viewHolder;

	public KeyStructureExerciseAdapter(Context context, ArrayList<KeyStructureExercise> list) {
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
		final KeyStructureExercise exercise = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = vi.inflate(R.layout.listitem_voa_annotation, null);
			
			viewHolder = new ViewHolder();
			viewHolder.descENText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.descCNText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.numberText = (TextView) convertView.findViewById(R.id.note);
			viewHolder.noteText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.typeText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.quesNumText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.answerText = (TextView) convertView.findViewById(R.id.anno_N);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.descENText.setText(exercise.describeEN);
		viewHolder.descCNText.setText(Html.fromHtml(exercise.describeCN));
		viewHolder.numberText.setText(exercise.number);
		viewHolder.noteText.setText(exercise.note);
		viewHolder.typeText.setText(exercise.type);
		viewHolder.quesNumText.setText(exercise.questionNumber);
		viewHolder.answerText.setText(exercise.answer);
		
		return convertView;
	}
	
	public class ViewHolder {
		TextView descENText;
		TextView descCNText;
		TextView numberText;
		TextView noteText;
		TextView typeText;
		TextView quesNumText;
		TextView answerText;
	}
}
