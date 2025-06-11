package com.iyuba.concept2.widget.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.activity.MultipleChoiceActivity;
import com.iyuba.concept2.sqlite.mode.ExerciseRecord;

import java.util.Map;

public class ResultTextViews extends LinearLayout {

	private Context mContext;
	private Map<Integer, ExerciseRecord> exerciseRecordMap;

	public ResultTextViews(Context context) {
		super(context);
		initResultTextView(context);
	}

	public ResultTextViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		initResultTextView(context);
	}

	public void initResultTextView(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		removeAllViews();
	}

	public void refResultTextView() {
		removeAllViews();
		
		if (exerciseRecordMap != null && exerciseRecordMap.size() != 0) {
			LinearLayout linearLayout = null;
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT); 
			
			LayoutParams params1 = new LayoutParams(130, 60); 
			params1.setMargins(5, 5, 5, 5);
			
			for(Map.Entry<Integer, ExerciseRecord> entry: exerciseRecordMap.entrySet()) {
				int index = entry.getKey();
				
				if(index % 4 == 0) {
					if(linearLayout != null) {
						addView(linearLayout, params);
					}
					
					linearLayout = new LinearLayout(mContext);
					linearLayout.setGravity(Gravity.CENTER);
				}
				
				ExerciseRecord exerciseRecord = entry.getValue();
				
				TextView textTemp = new TextView(mContext);
				textTemp.setText((index + 1) + "");
				textTemp.setTextColor(0xFFFFFFFF);
				textTemp.setGravity(Gravity.CENTER);
				
				if(exerciseRecord.RightAnswer.equals(exerciseRecord.UserAnswer)) {
					textTemp.setBackgroundResource(R.drawable.right_item);
				} else {
					textTemp.setBackgroundResource(R.drawable.wrong_item);
				}
				
				textTemp.setOnClickListener(olc);
				
				linearLayout.addView(textTemp, params1);
			}
			
			addView(linearLayout, params);
		}
	}
	
	OnClickListener olc = new OnClickListener() {
		public void onClick(View v) {
			TextView textView = (TextView) v;
			int index = Integer.valueOf(String.valueOf(textView.getText()));
			
			MultipleChoiceActivity.instance.setQuestion(index - 1);
 		}
    };
	
	public void setResultMap(Map<Integer, ExerciseRecord> exerciseRecordMap) {
		this.exerciseRecordMap = exerciseRecordMap;
	}

}
