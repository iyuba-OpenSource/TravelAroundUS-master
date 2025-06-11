package com.iyuba.core.common.util;

import android.os.Build;
import android.text.TextUtils;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.QQSupportApi;
import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
import com.iyuba.core.common.retrofitapi.result.QQGroupSupportResult;
import com.iyuba.core.common.retrofitapi.result.QQSupportResult;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * BrandUtil
 *
 * @author wayne
 * @date 2017/11/18
 */
public class BrandUtil {

    private static final String BRAND_HUAWEI = "huawei";
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String BRAND_MEIZU = "meizu";
    private static final String BRAND_VIVO = "vivo";
    private static final String BRAND_OPPO = "oppo";
    private static final String BRAND_SAMSUNG = "samsung";
    private static final String BRAND_GIONEE = "gionee";
    private static final String BRAND_360 = "360";
    private static final String BRAND_OTHER = "android";

    private static String brandName;

    private static String getBrandName() {
        if (TextUtils.isEmpty(brandName)) {
            brandName = setBrandName();
        }
        return brandName;
    }

    public static String getBrandChinese() {
        String brand = getBrandName();
        switch (brand) {
            case BRAND_HUAWEI:
                return "华为";
            case BRAND_VIVO:
                return "Vivo";
            case BRAND_OPPO:
                return "Oppo";
            case BRAND_XIAOMI:
                return "小米";
            case BRAND_SAMSUNG:
                return "三星";
            case BRAND_GIONEE:
                return "金立";
            case BRAND_MEIZU:
                return "魅族";
            case BRAND_360:
                return "360";
            default:
                return "安卓";
        }
    }

    private static String setBrandName() {
        String brand = Build.MANUFACTURER.trim().toLowerCase();
//        if (brand.contains("huawei") || brand.contains("honor")
//                || brand.contains("nova") || brand.contains("mate")) {
//            return BRAND_HUAWEI;
//        }
//        if (brand.contains("xiaomi")) {
//            return BRAND_XIAOMI;
//        }
//        if (brand.contains("vivo")) {
//            return BRAND_VIVO;
//        }
//        if (brand.contains("oppo")) {
//            return BRAND_OPPO;
//        }
//        if (brand.contains("samsung")) {
//            return BRAND_SAMSUNG;
//        }
//        if (brand.contains("meizu")) {
//            return BRAND_MEIZU;
//        }
//        // 金立
//        if (brand.contains("gionee")) {
//            return BRAND_GIONEE;
//        }
//        if (brand.contains("360") || brand.contains("qiku")
//                || brand.contains("qiho") || brand.contains("qihu")) {
//            return BRAND_360;
//        }
        return brand;
    }

    private static String getQQKey(String brandName) {
        switch (brandName) {
            case "华为":
                return "X_XYfsL0_-ewHkXpIUhlwpbxvQcxEWLb";
            case "Vivo":
                return "CFBROmhoDx_440-ukjYYugIf61SSujRC";
            case "Oppo":
                return "Yuhyc18Q34Lmy0b6W1HeXuDG3TdferpX";
            case "小米":
                return "9UmuKvpLjV-ib9W-bDSgEok_KyvAZYQ-";
            case "三星":
                return "4LU-47yf_P510zgmdp98miJtDx366Ty5";
            case "金立":
                return "pb42xTKokAQVzo1buzX95skntd5UOLUQ";
            case "魅族":
                return "zS82Y-4zaPVChkpun-HLOnNpKcf_h2_3";
            case "360":
                return "0yHQOAWPGPOPacORm2BXdOblJZvlzeLw";
            default:
                return "DEbZdKF9fjFpsxAzdcEQ5rhzHz9WWCFW";
        }
    }

    private static String getQQNumber(String brand) {
        switch (brand) {
            case "华为":
                return "705250027";
            case "Vivo":
                return "433075910";
            case "Oppo":
                return "334687859";
            case "小米":
                return "499939472";
            case "三星":
                return "639727892";
            case "金立":
                return "621392974";
            case "魅族":
                return "745011534";
            case "360":
                return "625355797";
            default:
                return "482516431";
        }
    }

    private static String getQQGroupNumber(String brand) {
        switch (brand) {
            case BRAND_HUAWEI:
                return "553959124";
            case BRAND_VIVO:
                return "433075910";
            case BRAND_OPPO:
                return "334687859";
            case BRAND_XIAOMI:
                return "493470842";
            case BRAND_SAMSUNG:
                return "639727892";
            case BRAND_GIONEE:
                return "621392974";
            case BRAND_MEIZU:
                return "625401994";
            case BRAND_360:
                return "625355797";
            default:
                return "540297996";
        }
    }

    private static String getQQGroupKey(String brand) {
        switch (brand) {
            case BRAND_HUAWEI:
                return "1ez-53Qiy0Q5086C6uwxMdms2AQ2ZmK6";
            case BRAND_VIVO:
                return "Clsc8LITvyP0SdaPub3F1MWHpfoXIJPw";
            case BRAND_OPPO:
                return "5XjVMCHCKOkoeAnjRpRAeqIjXjoRdHhw";
            case BRAND_XIAOMI:
                return "GDgYOnG50MCYk4vldiNqC5BB5uy5pCMd";
            case BRAND_SAMSUNG:
                return "4LU-47yf_P510zgmdp98miJtDx366Ty5";
            case BRAND_GIONEE:
                return "pb42xTKokAQVzo1buzX95skntd5UOLUQ";
            case BRAND_MEIZU:
                return "Tg2nNWD9wFOqu_RTX6FKcfy1Ay4TkGcD";
            case BRAND_360:
                return "0yHQOAWPGPOPacORm2BXdOblJZvlzeLw";
            default:
                return "N69aGfm78SVXbgCrpHalYlHxRMPh7LHz";
        }
    }

    //qq客服支持
    public static void requestQQSupport() {
        ApiRequestFactory.getQQSupport().getQQSupport(QQSupportApi.BASE_QQ_SUPPORT_URL, Constant.APPID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<QQSupportResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(QQSupportResult bean) {
                        if ((bean != null) && (bean.result == 200)) {
                            if ((bean.data != null) && (bean.data.size() > 0)) {
                                ConfigManager.Instance().setQQEditor(bean.data.get(0).editor);
                                ConfigManager.Instance().setQQTechnician(bean.data.get(0).technician);
                                ConfigManager.Instance().setQQManager(bean.data.get(0).manager);
                            }
                        }
                    }
                });
    }

    //qq群组支持
    public static void requestQQGroup(String uid) {
        ApiRequestFactory.getQQSupport().getQQGroup(QQSupportApi.BASE_QQ_GROUP_URL, getBrandName(), uid, Constant.APPID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<QQGroupSupportResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(QQGroupSupportResult bean) {
                        if ("true".equals(bean.message)) {
                            ConfigManager.Instance().setQQGroupNumber(bean.QQ);
                            ConfigManager.Instance().setQQGroupKey(bean.key);
                        }
                    }
                });
    }
}
