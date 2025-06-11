package com.iyuba.core.lil.event;

/**
 * @title: 刷新事件
 * @date: 2023/11/23 09:57
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RefreshEvent {

    //用户信息
    public static final String USER_INFO = "user_info";

    private String type;//类型

    public RefreshEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
