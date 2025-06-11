package com.iyuba.concept2.widget.components;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.ExerciseRecord;
import com.iyuba.configation.Constant;

public class AnswerEditViews extends LinearLayout {

	private Context mContext;
	private int blankNum;
	private List<Map<Integer, ExerciseRecord>> recordMapList;
	private int curQuestionId;
	private int exerciseStatus;
	private int tag; //tag=1 表示VoaStrcutureExercise , tag=2表示VoaDiffcultyExercise
	private final static int BASE_INDEX= 1000;

	public AnswerEditViews(Context context) {
		super(context);
		initAnswerEditView(context);
	}

	public AnswerEditViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAnswerEditView(context);
	}

	public void initAnswerEditView(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		removeAllViews();
	}

	public void refAnswerEditView() {
		long startTime = System.currentTimeMillis();
		
		removeAllViews();
		
		if (blankNum != 0) {
			LayoutParams params0 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT); 
			params0.setMargins(0, 10, 0, 10);
			
			LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f); 
			params1.setMargins(0, 0, 40, 0);
			
			LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			params2.setMargins(0, 0, 15, 0);
			
			LayoutParams params3 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
			
			Map<Integer, ExerciseRecord> recordMap = null;
			if(recordMapList.size() > curQuestionId) {
				recordMap = recordMapList.get(curQuestionId);
			}
			
			LinearLayout linearLayout0 = null;
			LinearLayout linearLayout =null;
			TextView textTemp = null;
			EditText editTemp = null;
			ExerciseRecord record = null;
			String index = null;
			
			for(int i = 0; i < blankNum; ) {
				linearLayout0 = new LinearLayout(mContext);
				linearLayout0.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout0.setGravity(Gravity.CENTER_VERTICAL);
				addView(linearLayout0, params0);
					
				linearLayout = new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.setGravity(Gravity.CENTER_VERTICAL);
				
				textTemp = new TextView(mContext);
				index = "<b>" + (i + 1) + "</b>";
				textTemp.setText(Html.fromHtml(index));
				textTemp.setTextColor(Constant.normalColor);
				textTemp.setTextSize(Constant.textSize);
				textTemp.setGravity(Gravity.CENTER_VERTICAL);
				linearLayout.addView(textTemp, params2);
				
				editTemp = new EditText(mContext);
				editTemp.setBackgroundResource(R.drawable.gray_item);
				editTemp.setTextColor(Constant.normalColor);
				editTemp.setTextSize(Constant.textSize);
				editTemp.setGravity(Gravity.CENTER_VERTICAL);
				editTemp.setSingleLine(true);
				editTemp.setMaxLines(1);
				editTemp.setId(tag * BASE_INDEX + i + 1);
				
				if(recordMap != null) {
					record = recordMap.get(i + 1);
					
					if(record != null) {
						String text = record.UserAnswer.trim();
						if(text != null) {
							editTemp.setText(text);
						}
						
						if(exerciseStatus == 1) {
							String rightAnswer = record.RightAnswer.trim();
							if(rightAnswer.equals(text)) {
								editTemp.setBackgroundResource(R.drawable.green_item);
							} else {
								editTemp.setBackgroundResource(R.drawable.red_item);
							}
						}
					}
				}
				
				linearLayout.addView(editTemp, params3);
				linearLayout0.addView(linearLayout, params1);
				
				i++;
				
				linearLayout = new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.setGravity(Gravity.CENTER_VERTICAL);
				
				textTemp = new TextView(mContext);
				index = "<b>" + (i + 1) + "</b>";
				textTemp.setText(Html.fromHtml(index));
				textTemp.setTextColor(Constant.normalColor);
				textTemp.setTextSize(Constant.textSize);
				textTemp.setGravity(Gravity.CENTER_VERTICAL);
				linearLayout.addView(textTemp, params2);
				
				editTemp = new EditText(mContext);
				editTemp.setBackgroundResource(R.drawable.gray_item);
				editTemp.setTextColor(Constant.normalColor);
				editTemp.setTextSize(Constant.textSize);
				editTemp.setGravity(Gravity.CENTER_VERTICAL);
				editTemp.setMaxLines(1);
				editTemp.setSingleLine(true);
				editTemp.setId(tag * BASE_INDEX + i + 1);
				
				if(recordMap != null) {
					if(i >= blankNum) {
						record = recordMap.get(blankNum - 1);
					} else {
						record = recordMap.get(i + 1);
					}
					
					if(record != null) {
						String text = record.UserAnswer;
						if(text != null) {
							editTemp.setText(text);
						}
						
						if(exerciseStatus == 1) {
							String rightAnswer = record.RightAnswer.trim();
							if(rightAnswer.equals(text)) {
								editTemp.setBackgroundResource(R.drawable.green_item);
							} else {
								editTemp.setBackgroundResource(R.drawable.red_item);
							}
						}
					}
				}
				
				linearLayout.addView(editTemp, params3);
				linearLayout0.addView(linearLayout, params1);
				
				i++;
				
				if(i == blankNum + 1) {
					linearLayout.setVisibility(View.INVISIBLE);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
	}
	
	public void setRecordMapList(List<Map<Integer, ExerciseRecord>> recordMapList) {
		this.recordMapList = recordMapList;
	}
	
	public void setBlankNum(int blankNum) {
		this.blankNum = blankNum;
	}
	
	public void setCurQuestionId(int curQuestionId) {
		this.curQuestionId = curQuestionId;
	}

	public void setExerciseStatus(int exerciseStatus) {
		this.exerciseStatus = exerciseStatus;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
}
