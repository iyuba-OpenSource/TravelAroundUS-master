package com.iyuba.core.common.util;

import android.content.Context;
import android.view.WindowManager;

import com.iyuba.lib.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Liuzhenli on 2016/9/19.
 */
public class CommonUtils {

    /**
     * 获取屏幕的宽
     *
     * @param context 上下文
     * @return 屏幕的高度
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 用户协议
     */
    public static String getUserAgreementUrl(Context context) {
        String url = "";
        context = context.getApplicationContext();
        try {
            url = "https://ai.iyuba.cn/api/protocoluse666.jsp?apptype=" +
                    URLEncoder.encode(context.getString(R.string.app_name), "UTF-8") + "&company=北京爱语吧";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 隐私政策
     * 1：北京爱语吧
     * 2：画笙智能
     * 3：爱语言
     * <p>
     * 隐私协议：
     * company:1爱语吧  2上海画笙   3爱语言
     * https://ai.iyuba.cn/api/protocolpri.jsp?apptype=Name&company=1
     */
    public static String getPrivacyPolicyUrl(Context context) {
        String url = "";
        context = context.getApplicationContext();
        try {

            url = "https://ai.iyuba.cn/api/protocolpri.jsp?apptype="
                    + URLEncoder.encode(context.getString(R.string.app_name), "UTF-8") + "&company=1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
