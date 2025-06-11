package com.iyuba.concept2.lil.ui.ad.util;

import android.util.Log;

import com.iyuba.core.lil.util.LibDateUtil;


public class AdLogUtil {
    private static final String TAG = "AdLogUtil";

    //当前时间
    private static String showCurrentTime(){
        return LibDateUtil.toDateStr(System.currentTimeMillis(),LibDateUtil.YMDHMSS);
    }

    //当前线程
    private static String showCurrentThread(){
        return Thread.currentThread().getName();
    }

    public static void showDebug(String tag,String showMsg){
        String timeMsg = "当前时间："+showCurrentTime();
        String threadMsg = "当前线程："+showCurrentThread();
        Log.d(tag, timeMsg+"----"+threadMsg);
        Log.d(tag, showMsg);
    }
}
