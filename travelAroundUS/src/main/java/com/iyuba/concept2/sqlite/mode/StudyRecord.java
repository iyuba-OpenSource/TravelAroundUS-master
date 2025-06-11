package com.iyuba.concept2.sqlite.mode;

public class StudyRecord {
	
	public int uid;        //爱语吧Id
	public String beginTime;
	public String endTime;
	public String voa;  //产品名：voa慢速英语,VOA常速英语,BBC六分钟英语,听歌学英语
	public String voaId;//文章ID
	public String endFlg;  //完成标志：0：只开始听；1：听力完成；2：做题完成.
	public String device;   //做题设备：android手机，iphone手机，firefox,ie等
	public String IP;       //客户端IP地址

}
