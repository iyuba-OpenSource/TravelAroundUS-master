package com.iyuba.concept2.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.iyuba.core.lil.user.util.LoginUtil;

/**
 * Created by iyuba on 2017/8/22.
 */

public class DialogUtil {




    private DialogUtil(){

    }

    public static void showLoginDialog(final Activity mContext,String title,String content){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(com.iyuba.lib.R.drawable.iyubi_icon);
        normalDialog.setTitle(title);
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("登录",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        Intent intent = new Intent();
//                        intent.setClass(mContext, Login.class);
//                        mContext.startActivity(intent);
                        LoginUtil.startToLogin(mContext);
                    }
                });
        normalDialog.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        normalDialog.show();
    }
}
