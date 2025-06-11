package com.iyuba.core.lil.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;

import com.iyuba.lib.R;

import java.util.List;
import java.util.PrimitiveIterator;

public class LibSpanUtil {

	//低成绩标志
	public static final double tag_low_score = 2.5f;
	//高成绩标志
	public static final double tag_high_score = 4.0f;


	//设置句子样式显示
	public static SpannableStringBuilder showSpan(List<Pair<String,Double>> wordPair,String sentence){
		SpannableStringBuilder builder = new SpannableStringBuilder(sentence);
		builder.setSpan(new ForegroundColorSpan(Color.BLACK),0,sentence.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		//用空格切分显示
		sentence = sentence.trim();
		String[] sentenceArray = sentence.split(" ");
		if (sentenceArray.length!=wordPair.size()){
			return builder;
		}

		//当前位置
		int curIndex = 0;
		//循环判断
		for (int i = 0; i < sentenceArray.length; i++) {
			//句子中的长度
			String showStr = sentenceArray[i];
			//单词中的长度
			String wordStr = wordPair.get(i).first;

			double showScore = wordPair.get(i).second;
			if (showScore<=tag_low_score){
				builder.setSpan(new ForegroundColorSpan(Color.RED),curIndex,curIndex+wordStr.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			/*else if (showScore>=tag_high_score){
				builder.setSpan(new ForegroundColorSpan(Color.GREEN),curIndex,curIndex+wordStr.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}*/

			curIndex+=showStr.length()+1;
		}

		return builder;
	}
}
