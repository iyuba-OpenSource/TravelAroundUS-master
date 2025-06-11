package com.iyuba.concept2.sqlite.mode;

public class ExerciseRecord {
	public int uid;
	public int voaId;
	public int TestNumber;   //题号
	public String BeginTime;
	public String UserAnswer = "";    //用户答案
	public String RightAnswer ;    //正确答案
	public int AnswerResut = 2;    //正确与否：0：错误；1：正确  2 未提交
	public String TestTime;    //测试时间
}
