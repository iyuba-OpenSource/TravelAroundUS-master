package com.iyuba.concept2.sqlite.mode;

public class TestRecord {
    public String uid;
    public String LessonId;//课程的Id
    public String BeginTime;//测试的开始时间
    public int TestNumber;     //题号
    public String UserAnswer = "";    //用户答案
    public String RightAnswer;    //正确答案
    public int AnswerResult;    //正确与否�0：错误；1：正确
    public String TestTime;    //测试时间
    public boolean IsUpload;

    public String deviceId;//设备Id
    public String appId;//app在爱语吧平台的Id
    public String testMode;//测试模式
    public int index;//测试模式
    public String category;
}
