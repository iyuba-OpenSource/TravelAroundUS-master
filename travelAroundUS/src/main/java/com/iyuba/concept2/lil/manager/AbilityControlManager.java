package com.iyuba.concept2.lil.manager;

/**
 * @desction: 功能控制管理类
 * @date: 2023/3/11 23:29
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class AbilityControlManager {
    private static final String TAG = "AbilityControlManager";

    private static AbilityControlManager instance;

    public static AbilityControlManager getInstance(){
        if (instance==null){
            synchronized (AbilityControlManager.class){
                if (instance==null){
                    instance = new AbilityControlManager();
                }
            }
        }
        return instance;
    }

    //人教版屏蔽(屏蔽选书的人教版选项)
    //(这里在启动页获取接口，在选择课程界面获取接口)
    //(启动页获取接口为不限制，则其他接口不启动；如果为限制，则其他接口二次查询)
    private boolean limitPep = false;

    public boolean isLimitPep() {
        return limitPep;
    }

    public void setLimitPep(boolean isLimit) {
        this.limitPep = isLimit;
    }


    //微课屏蔽(单词接口调用，默认不显示)
    private boolean limitMoc = false;

    public boolean isLimitMoc(){
        return limitMoc;
    }

    public void setLimitMoc(boolean limitMoc) {
        this.limitMoc = limitMoc;
    }

    //视频屏蔽(单词接口调用，默认不显示)
    private boolean limitVideo = false;

    public boolean isLimitVideo(){
        return limitVideo;
    }

    public void setLimitVideo(boolean limitVideo) {
        this.limitVideo = limitVideo;
    }


    //控制青少版选书的数据
    private boolean limitJunior = false;

    public boolean isLimitJunior(){
        return limitJunior;
    }

    public void setLimitJunior(boolean limitJunior) {
        this.limitJunior = limitJunior;
    }

    //控制课文界面的数据
    private boolean limitLesson = false;

    public boolean isLimitLesson(){
        return limitLesson;
    }

    public void setLimitLesson(boolean limitLesson) {
        this.limitLesson = limitLesson;
    }
}
