package com.iyuba.concept2.sqlite.mode;

import java.util.List;

/**
 * voa标题数据集
 */
public class VoaTitleBean {
    private int mVoaId;
    public boolean isRead=false;
    private String mTitleEn;
    private String mTitleCn;
    private List<Voa> voaList;
    private boolean isSelect=false;

    public int getmVoaId() {
        return mVoaId;
    }

    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }
    public void setmVoaId(int mVoaId) {
        this.mVoaId = mVoaId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getmTitleEn() {
        return mTitleEn;
    }

    public void setmTitleEn(String mTitleEn) {
        this.mTitleEn = mTitleEn;
    }

    public String getmTitleCn() {
        return mTitleCn;
    }

    public void setmTitleCn(String mTitleCn) {
        this.mTitleCn = mTitleCn;
    }

    public List<Voa> getVoaList() {
        return voaList;
    }

    public void setVoaList(List<Voa> voaList) {
        this.voaList = voaList;
    }
}
