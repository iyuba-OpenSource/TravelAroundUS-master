package com.iyuba.concept2.sqlite.mode;

/**
 * 每一个voa的基本信息
 */
public class StudyPcmFile {


	private int currIndex;
	private  int score;
	private  long totalTime;
	private String filePath;


	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getCurrIndex() {
		return currIndex;
	}

	public void setCurrIndex(int currIndex) {
		this.currIndex = currIndex;
	}
}
