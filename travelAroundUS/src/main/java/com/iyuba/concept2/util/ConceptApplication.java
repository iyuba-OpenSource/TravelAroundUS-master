package com.iyuba.concept2.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.concept2.activity.WelcomeActivity;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.lil.util.LibResUtil;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.dl.BasicDL;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.movies.IMovies;
import com.iyuba.widget.unipicker.IUniversityPicker;
import com.mob.MobSDK;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.config.utils.OaidUtils;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import java.io.File;
import java.util.Date;

import personal.iyuba.personalhomelibrary.PersonalHome;

/**
 *
 */
public class ConceptApplication extends CrashApplication {

    private static ConceptApplication mInstance;
    private long startTime, endTime;
    private int mFinalCount;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext=getApplicationContext();
        //预加载oaid操作
        System.loadLibrary("msaoaidsec");
        //个人中心初始化
        PersonalHome.init(this);
        PersonalHome.setEnableShare(true);
        IUniversityPicker.init(this);
        //设置上下文
        LibResUtil.getInstance().setApplication(this);
    }

    public static ConceptApplication getInstance() {
        return mInstance;
    }

    public void init() {
        // UMSDK预初始化函数
        // preInit预初始化函数耗时极少，不会影响App首次冷启动用户体验
        String channel = ChannelReaderUtil.getChannel(this);
        UMConfigure.preInit(getApplicationContext(),Constant.UM_KEY,channel);

        //把之前的 异常处理去掉，似乎会导致 看不到日志
        /*CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());*/
        initImageLoader();
        //包名可能不对，暂时没有影响
        MobSDK.init(this, "19a1773990a19", "e05c1fa7afaff36759e5728b6157f78b");

        RuntimeManager.setApplication(this);
        RuntimeManager.setApplicationContext(getApplicationContext());

        /**
         * 通用模块
         */
        //微课
        DLManager.init(this, 6);
        IMooc.init(getApplicationContext(), Constant.APPID, Constant.APPType);

        //看一看
        IMovies.init(getApplicationContext(), Constant.APPID, Constant.APPType);

        //收藏相关
        BasicDL.init(getApplicationContext());
        BasicFavor.init(getApplicationContext(), Constant.APPID);

        //头条初始化
//        IHeadline.setDebug(BuildConfig.DEBUG);
        IHeadline.init(getApplicationContext(), Constant.APPID, Constant.APPType);
        //开启分享功能
        IHeadline.setEnableShare(true);
        //关闭口语圈
        IHeadline.setEnableGoStore(false);
        //开启配音
        IHeadline.setEnableSmallVideoTalk(true);

        BasicDLDBManager.init(getApplicationContext());
        BasicFavorDBManager.init(getApplicationContext());
        HLDBManager.init(getApplicationContext());

        try{
            /**
             * oaid
             */
            // TODO: 2025/1/13 因为有道sdk的问题，这里不使用手动处理的方式，但是上边的system需要保留
//            OAIDHelper.getInstance().init(getApplicationContext());
//            new MsaHelper(appIdsUpdater).getDeviceIds(getApplicationContext());

            /**
             * 有道设置
             */
            YouDaoAd.getYouDaoOptions().setSdkOpenOtherApkEnabled(false);
            YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
            YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
            YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
            YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
            YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
            YouDaoAd.getYouDaoOptions().setWifiEnabled(false);
            YouDaoAd.getYouDaoOptions().setCanObtainAndroidId(false);
            YoudaoSDK.init(getApplicationContext());
        }catch (Exception e){
            Log.e("TAG", "init: "+e.getMessage());
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e("onActivityStarted", mFinalCount + "");
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    endTime = new Date().getTime();
                    Log.e("onActivityStarted", new Date().toString() + "");

                    long timeload = endTime - startTime;
                    Log.e("onActivityStarted", timeload + "--" + startTime);
                    if (startTime != 0 && timeload / 1000 >= 180) {
                        Log.e("onActivityStarted", "===============" + activity.toString());

                        Intent intent = new Intent(activity, WelcomeActivity.class);
                        intent.putExtra("onActivityStarted", true);
                        activity.startActivity(intent);
                    }

                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台
                Log.e("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台

                    startTime = new Date().getTime();
                    Log.e("onActivityStopped", new Date().toString() + "");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        try {
            //这里初始化广告时间为3天
            //测试使用
            Date compileDate = TimeUtil.GLOBAL_SDF.parse("2022-07-01 13:20:20");
            //正式使用
//            Date compileDate = TimeUtil.GLOBAL_SDF.parse(BuildConfig.COMPILE_DATETIME);
            //这里根据当前情况，扩展成3天
            Date adBlockDate = new Date(compileDate.getTime()+3*24*60*60*1000);
            AdBlocker.getInstance().setBlockStartDate(adBlockDate);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(com.iyuba.lib.R.drawable.nearby_no_icon) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(com.iyuba.lib.R.drawable.nearby_no_icon) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(com.iyuba.lib.R.drawable.nearby_no_icon) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        Log.e("cache", cacheDir.toString());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)//线程池内加载的数量
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
    }
    public static Context getContext() {
        return mContext;
    }

//    private MsaHelper.AppIdsUpdater appIdsUpdater = new MsaHelper.AppIdsUpdater() {
//        @Override
//        public void onIdData(boolean isSupported, boolean isLimited, String oaid, String vaid, String aaid) {
//            if (isSupported && !isLimited){
//                OAIDHelper.getInstance().setOAID(oaid);
//            }
//        }
//    };

}
