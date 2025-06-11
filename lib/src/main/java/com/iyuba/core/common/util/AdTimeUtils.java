package com.iyuba.core.common.util;

import com.facebook.stetho.common.LogUtil;

public class AdTimeUtils {

    private static final long flagTime =1561289800 ;

    public static boolean isTime(){
        long time = System.currentTimeMillis() / 1000;
        //long flagTime = 1548484768;//1546922614 1547030614
        if (flagTime - time > 0) {
            long i = flagTime - time;
            LogUtil.e("时间还没到，剩余时间：" + i);
            return true;
        }
        return false;
    }
}
