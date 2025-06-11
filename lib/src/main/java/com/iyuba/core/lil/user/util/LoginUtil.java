package com.iyuba.core.lil.user.util;

import android.content.Context;

import com.iyuba.core.lil.user.data.LoginType;
import com.iyuba.core.lil.user.ui.NewLoginActivity;

/**
 * @title:
 * @date: 2023/8/25 18:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LoginUtil {

    //跳转类型
    public static void startToLogin(Context context){
        NewLoginActivity.start(context, LoginType.getInstance().getCurLoginType());
    }
}
