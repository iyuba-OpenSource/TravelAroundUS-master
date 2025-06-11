package com.iyuba.concept2.lil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.iyuba.core.common.util.ToastUtil;

/**
 * @title:
 * @date: 2023/6/26 14:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class HelpUtil {

    /**
     * 获取程序的版本号
     *
     * @param context
     * @param packname
     * @return
     */
    public static String getAppVersion(Context context, String packname) {
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packname;
    }

    /*******************手机品牌判断*****************/
    //是否是oppo旗下的手机
    public static boolean isBelongToOppoPhone(){
        String brand = Build.BRAND.toLowerCase();
        switch (brand){
            case "oppo"://oppo
            case "oneplus"://一加
                return true;
        }
        return false;
    }

    /***********************下载功能*********************/
    //使用系统浏览器下载文件
    public static void downloadFileBySystemBrowser(Context context,String fileUrl){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));
        if (intent.resolveActivity(context.getPackageManager())!=null){
            context.startActivity(Intent.createChooser(intent,"请选择浏览器"));
        }else {
            ToastUtil.showToast(context,"未查找到浏览器");
        }
    }
}
