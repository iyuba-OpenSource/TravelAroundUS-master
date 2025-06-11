package com.iyuba.core.lil.util;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

/**
 * @desction: 堆栈管理
 * @date: 2023/3/16 00:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class LibStackUtil {

    private static LibStackUtil instance;
    private static Stack<Activity> activityStack = new Stack<>();

    public static LibStackUtil getInstance(){
        if (instance == null){
            synchronized (LibStackUtil.class){
                if (instance == null){
                    instance = new LibStackUtil();
                }
            }
        }
        return instance;
    }

    //添加到堆栈
    public void add(Activity activity){
        if (activity!=null){
            activityStack.add(activity);
        }
    }

    //从堆栈中移除(不退出)
    public void remove(Activity activity){
        if (activityStack==null||activityStack.size()==0){
            return;
        }

        if (activity!=null){
            activityStack.remove(activity);
        }
    }

    //从堆栈中退出(移除)
    public void finish(Activity activity){
        Log.d("堆栈数据", "数据集合--"+activityStack.toString()+"--"+activity.getClass().getSimpleName());

        if (activityStack==null||activityStack.size()==0){
            return;
        }

        if (activity!=null){
            activityStack.remove(activity);

            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    //从堆栈中退出(移除)
    public void finish(Class<?> clz){
        Log.d("堆栈数据", "数据集合0011--1"+activityStack.toString()+"--"+clz.getSimpleName());

        if (activityStack==null||activityStack.size()==0){
            return;
        }

        if (clz!=null){
            for (int i = 0; i < activityStack.size(); i++) {
                Activity curAty = activityStack.get(i);
                if (curAty.getClass().equals(clz)){
                    activityStack.remove(curAty);
                    curAty.finish();
                    return;
                }
            }
        }
    }

    //从堆栈中退出其他界面(移除)
    public void finishOther(AppCompatActivity activity){
        if (activityStack==null||activityStack.size()==0){
            return;
        }

        if (activity!=null){
            for (int i = 0; i < activityStack.size(); i++) {
                Activity curAty = activityStack.get(i);
                if (!curAty.equals(activity)){
                    finish(curAty);
                }
            }
        }
    }

    //从堆栈中退出其他界面(移除)
    public void finishOther(Class<?> clz){
        if (activityStack==null||activityStack.size()==0){
            return;
        }

        if (clz!=null){
            for (int i = 0; i < activityStack.size(); i++) {
                Activity curAty = activityStack.get(i);
                if (!curAty.getClass().equals(clz)){
                    finish(curAty);
                }
            }
        }
    }

    //从堆栈中退出当前界面(移除)
    public void finishCur(){
        if (activityStack==null||activityStack.size()==0){
            return;
        }

        Log.d("堆栈数据", "数据集合--"+activityStack.toString()+"--1111");
        finish(activityStack.lastElement());
    }

    //从堆栈中退出所有界面
    public void finishAll(){
        if (activityStack==null||activityStack.size()==0){
            return;
        }

        try {
            Log.d("堆栈数据", "数据集合--"+activityStack.toString()+"--2222");
            for (int i = activityStack.size()-1; i >=0; i--) {
                Activity activity = activityStack.get(i);
                finish(activity);
            }
            activityStack.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //退出app
    public void existApp(){
        try {
            finishAll();
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //是否存在当前界面
    public boolean isExistActivity(Class clz){
        if (activityStack==null||activityStack.size()==0){
            return false;
        }

        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i).getClass().equals(clz)){
                return true;
            }
        }
        return false;
    }
}
