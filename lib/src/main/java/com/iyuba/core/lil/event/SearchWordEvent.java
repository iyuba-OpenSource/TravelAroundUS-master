package com.iyuba.core.lil.event;

/**
 * @title: 跳转到查询单词界面
 * @date: 2023/12/26 15:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchWordEvent {
    public static final String to_searchWord = "to_searchWord";

    private String type;
    private String keyWord;

    public SearchWordEvent(String type, String keyWord) {
        this.type = type;
        this.keyWord = keyWord;
    }

    public String getType() {
        return type;
    }

    public String getKeyWord() {
        return keyWord;
    }
}
