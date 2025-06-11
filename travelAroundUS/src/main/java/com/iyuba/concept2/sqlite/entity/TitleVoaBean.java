package com.iyuba.concept2.sqlite.entity;

/**
 * 每一个voa的基本信息
 */
public class TitleVoaBean {
    public int voaId; // 课 ID
    public String title = ""; // 课名
    public String titleCn = ""; // 课名
    public String sound=""; // 声音
    public String url="";
    public String readCount = ""; // 阅读量
    public String isCollect = "0";
    public String isRead="0";
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
}
