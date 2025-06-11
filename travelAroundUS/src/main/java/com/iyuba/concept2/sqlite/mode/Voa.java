package com.iyuba.concept2.sqlite.mode;

/**
 * 每一个voa的基本信息
 */
public class Voa {
	public int voaId; // 课 ID
	public int lesson;// 单元ID
	public int art; //每单元课ID
	public String title = ""; // 课名
	public String titleCn = ""; // 课名
	public String sound=""; // 声音
	public String url="";
	public String readCount = ""; // 阅读量
	public String isCollect = "0"; 
	public String isRead="0";
	public boolean isReadArt=false;
	public String isDownload = "0"; 
	public String isSynchro; 
	public String pic = ""; // 图片
	public int titleFind;
	public int textFind;
	
	public boolean isDelete;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public boolean isSelect=false;
	
	public int downLoadPercentage; // 下载百分比
	
	public int doMultiple;
	public int doBlank;
	public int doThinking;

	public boolean isReadArt() {
		return isReadArt;
	}

	public void setReadArt(boolean readArt) {
		isReadArt = readArt;
	}

	@Override
	public String toString() {
		return "Voa{" +
				"voaId=" + voaId +
				", lesson=" + lesson +
				", art=" + art +
				", title='" + title + '\'' +
				", titleCn='" + titleCn + '\'' +
				", sound='" + sound + '\'' +
				", url='" + url + '\'' +
				", readCount='" + readCount + '\'' +
				", isCollect='" + isCollect + '\'' +
				", isRead='" + isRead + '\'' +
				", isReadArt=" + isReadArt +
				", isDownload='" + isDownload + '\'' +
				", isSynchro='" + isSynchro + '\'' +
				", pic='" + pic + '\'' +
				", titleFind=" + titleFind +
				", textFind=" + textFind +
				", isDelete=" + isDelete +
				", isSelect=" + isSelect +
				", downLoadPercentage=" + downLoadPercentage +
				", doMultiple=" + doMultiple +
				", doBlank=" + doBlank +
				", doThinking=" + doThinking +
				'}';
	}
}
