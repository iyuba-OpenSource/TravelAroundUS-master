package com.iyuba.core.common.base;

/**
 * 程序崩溃后操作
 *
 * @version 1.0
 * @author 陈彤
 * 修改日期    2014.3.29
 */

import android.app.Activity;
import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.configation.RuntimeManager;

import java.util.LinkedList;
import java.util.List;

public class CrashApplication extends Application {
    private static CrashApplication mInstance = null;
    private RequestQueue queue;


    private List<Activity> activityList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        RuntimeManager.setApplicationContext(getApplicationContext());
        RuntimeManager.setApplication(this);
        queue = Volley.newRequestQueue(this);
        mInstance = this;

    }

    // 程序加入运行列表
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 程序退出
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static CrashApplication getInstance() {
        return mInstance;
    }

    // 全局volley请求队列队列
    public RequestQueue getQueue() {
        return queue;
    }


}