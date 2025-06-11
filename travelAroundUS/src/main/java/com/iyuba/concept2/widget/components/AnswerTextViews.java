package com.iyuba.concept2.widget.components;

import java.util.Map;

import com.iyuba.concept2.sqlite.mode.VoaDiffcultyExercise;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnswerTextViews extends LinearLayout {

	private Context mContext;
	private Map<Integer, VoaDiffcultyExercise> diffcultyExerciseMap;

	public AnswerTextViews(Context context) {
		super(context);
		initQuestionTextView(context);
	}

	public AnswerTextViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		initQuestionTextView(context);
	}

	public void initQuestionTextView(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		removeAllViews();
	}

	public void refAnswerTextView() {
		if (diffcultyExerciseMap != null && diffcultyExerciseMap.size() != 0) {
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			params.setMargins(0, 20, 0, 20);
			
			for(Map.Entry<Integer, VoaDiffcultyExercise> entry: diffcultyExerciseMap.entrySet()) {
				int index = entry.getKey();
				VoaDiffcultyExercise diffcultyExercise = entry.getValue();
				
				TextView textTemp = new TextView(mContext);
				String source = "<b>" + index + "</b>";
				textTemp.setText(Html.fromHtml(source + diffcultyExercise.answer));
				textTemp.setTextColor(0xFFFFFFFF);
				textTemp.setGravity(Gravity.CENTER_VERTICAL);
				
				addView(textTemp, params);
			}
		}
	}
	
	public void setQuestionMap(Map<Integer, VoaDiffcultyExercise> diffcultyExerciseMap) {
		this.diffcultyExerciseMap = diffcultyExerciseMap;
	}

}
