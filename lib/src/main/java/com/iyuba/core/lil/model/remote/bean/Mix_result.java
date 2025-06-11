package com.iyuba.core.lil.model.remote.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @title: 合成评测句子的回调
 * @date: 2023/12/21 15:14
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Mix_result implements Serializable {

    /**
     * result : 1
     * message : merge success
     * URL : wav6/202312/familyalbum/20231221/17031428131210406.mp3
     */

    private String result;
    private String message;
    @SerializedName("URL")
    private String url;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
