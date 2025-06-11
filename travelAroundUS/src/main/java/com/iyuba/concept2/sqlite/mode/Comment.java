package com.iyuba.concept2.sqlite.mode;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;

public class Comment {
	public String id;
	public String imgsrc="";
	public String userId; // 用户ID
	public SoftReference<Bitmap> picbitmap;
	public int agreeCount;
	public int againstCount;
	public String shuoshuo;
	public int shuoshuoType;
	public String username="";
	public String createdate;
	public String score;
	public String indexId;
}
