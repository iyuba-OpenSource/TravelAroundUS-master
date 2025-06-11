package com.iyuba.concept2.sqlite.mode;

public class NewWord {
	public String id;
	public String word = ""; // 关键字
	public String audio = ""; // 多媒体网络路径
	public String pron = ""; // 音标
	public String def = ""; // 解释
	public String examples = "";
	public String viewCount = ""; // 例句，多条以“,”分割
	public String createDate = ""; // 创建时间
	public boolean isDelete;
}
