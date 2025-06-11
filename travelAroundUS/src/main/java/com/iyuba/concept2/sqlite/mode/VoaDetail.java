package com.iyuba.concept2.sqlite.mode;

import android.text.SpannableStringBuilder;

import com.iyuba.core.search.bean.EvaluatBean;

/**
 * 
 */
public class VoaDetail {
	public int voaId;
	public String paraId=""; // 段落ID
	public String lineN=""; // 句子行数
	public double startTime; // 开始时间
	public double endTime; // 结束时间
	public double timing; // 所用时间
	public String sentence=""; // 句子内容
	public String imgPath=""; // 图片
	public String sentenceCn=""; // 中文句子内容
	public String imgwords = "";
	public String chosnWords="";

	public boolean isRead = false;
	public boolean isListen = false;
	public boolean isCommit = false;
	public SpannableStringBuilder readResult;  //拼接之后的数据
	private int readScore = 0;

	public void setReadScore(int score) {
		this.readScore = score;
	}

	public int getReadScore() {
		return readScore;
	}
}
