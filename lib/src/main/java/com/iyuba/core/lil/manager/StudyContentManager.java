package com.iyuba.core.lil.manager;

/**
 * @title: 学习界面-原文界面处理
 * @date: 2023/12/19 18:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyContentManager {
    private static StudyContentManager instance;

    public static StudyContentManager getInstance(){
        if (instance==null){
            synchronized (StudyContentManager.class){
                if (instance==null){
                    instance = new StudyContentManager();
                }
            }
        }
        return instance;
    }

    //是否为临时数据
    private boolean isTempData = false;

    public void setTempData(boolean isTemp){
        this.isTempData = isTemp;
    }

    public boolean getTempData(){
        return isTempData;
    }
}
