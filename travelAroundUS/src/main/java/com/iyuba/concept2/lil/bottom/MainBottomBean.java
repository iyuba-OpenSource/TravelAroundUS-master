package com.iyuba.concept2.lil.bottom;

/**
 * @desction: 主页面底部动态设置
 * @date: 2023/3/23 18:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class MainBottomBean {

    private int oldResId;
    private int newResId;
    private String text;

    public MainBottomBean(int oldResId, int newResId, String text) {
        this.oldResId = oldResId;
        this.newResId = newResId;
        this.text = text;
    }

    public int getOldResId() {
        return oldResId;
    }

    public int getNewResId() {
        return newResId;
    }

    public String getText() {
        return text;
    }
}
