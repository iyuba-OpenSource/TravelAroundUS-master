package com.iyuba.core.lil.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * @title: 文件路径
 * @date: 2023/11/23 10:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LibFilePathUtil {

    //根据版本获取不同的路径
    public static String getFileDirPath(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getExternalFilesDir(null).getPath();
        }else {
            return Environment.getExternalStorageDirectory().getPath()+"/iyuba";
        }
    }
}
