package com.iyuba.core.common.service.bgPlayService;

/**
 * @title: 刷新数据
 * @date: 2023/4/27 15:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TravelBgPlayEvent {
    //退出app
    public static final String existApp = "existApp";
    //播放音频
    public static final String event_audio_play = "event_audio_play";
    //暂停音频
    public static final String event_audio_pause = "event_audio_pause";


    private String type;//刷新的数据类型
    private String msg;//传递的消息

    public TravelBgPlayEvent(String type) {
        this.type = type;
    }

    public TravelBgPlayEvent(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
