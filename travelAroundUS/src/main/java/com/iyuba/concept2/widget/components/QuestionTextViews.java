package com.iyuba.concept2.widget.components;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.concept2.sqlite.mode.VoaDiffcultyExercise;
import com.iyuba.configation.Constant;

public class QuestionTextViews extends LinearLayout {

	private Context mContext;
	private Map<Integer, VoaDiffcultyExercise> diffcultyExerciseMap;

	public QuestionTextViews(Context context) {
		super(context);
		initQuestionTextView(context);
	}

	public QuestionTextViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		initQuestionTextView(context);
	}

	public void initQuestionTextView(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		removeAllViews();
	}

	public void refQuestionTextView() {
		if (diffcultyExerciseMap != null && diffcultyExerciseMap.size() != 0) {
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT); 
			params.setMargins(0, 10, 0, 10);
			
			for(int index = 1; index <= diffcultyExerciseMap.size(); index++) {
				VoaDiffcultyExercise diffcultyExercise = diffcultyExerciseMap.get(index);
				int number = diffcultyExercise.number;
				int type = diffcultyExercise.type;
				String note = diffcultyExercise.note;
				
				if(number != 0) {
					if(type == 0) {
						note.replaceFirst("+++", "<i>");
						note.replaceFirst("+++", "</i>");
					}
				}
				
				TextView textTemp = new TextView(mContext);
				
				textTemp.setText(number + "." + note);
				textTemp.setTextColor(Constant.normalColor);
				textTemp.setTextSize(Constant.textSize);
				textTemp.setGravity(Gravity.CENTER_VERTICAL);
				
				addView(textTemp, params);
			}
		}
	}

	public void setAnswer() {
		
	}
	
	public void setQuestionMap(Map<Integer, VoaDiffcultyExercise> diffcultyExerciseMap) {
		this.diffcultyExerciseMap = diffcultyExerciseMap;
	}

}
