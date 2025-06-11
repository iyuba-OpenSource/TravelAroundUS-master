package com.iyuba.concept2.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.concept2.R;
import com.iyuba.concept2.databinding.WelcomeBinding;
import com.iyuba.concept2.lil.manager.AbilityControlManager;
import com.iyuba.concept2.lil.manager.VerifyManager;
import com.iyuba.concept2.lil.ui.ad.util.AdLogUtil;
import com.iyuba.concept2.lil.ui.ad.util.show.AdShowUtil;
import com.iyuba.concept2.lil.ui.ad.util.show.spread.AdSpreadShowManager;
import com.iyuba.concept2.lil.ui.ad.util.show.spread.AdSpreadViewBean;
import com.iyuba.concept2.lil.ui.ad.util.upload.AdUploadManager;
import com.iyuba.concept2.service.AutoNoticeService;
import com.iyuba.concept2.sqlite.ImportDatabase;
import com.iyuba.concept2.util.AdUtil;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.concept2.util.RxTimer;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.ImportLibDatabase;
import com.iyuba.core.common.util.ShareInit;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_result;
import com.iyuba.core.lil.remote.service.LibAdService;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibFilePathUtil;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.lil.util.LibRxUtil;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 起始界面Activity
 */
public class WelcomeActivity extends BasisActivity implements OnRequestPermissionsResultCallback {
    private Context mContext;
    private int lastVersion, currentVersion;

    private WelcomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//指定应用窗体无标题
        setVolumeControlStream(AudioManager.STREAM_MUSIC);//多媒体音量控制  /音乐回放即媒体音量/
        RuntimeManager.setDisplayMetrics(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        binding = WelcomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());//设置页面布局

        CrashApplication.getInstance().addActivity(this);

        //share init
        ShareInit initShare = new ShareInit();
        initShare.initShare(mContext);

        try {
//            currentVersion = getPackageManager().getPackageInfo(
//                    mContext.getPackageName(), 0).versionCode;
            currentVersion = Constant.GUIDE_VERSION;
            lastVersion = ConfigManager.Instance().loadInt("version");
        } catch (Exception e) {
            lastVersion = 0;
            e.printStackTrace();
        }

        //设置广告屏蔽
        if (!AdBlocker.getInstance().shouldBlockAd() && CheckNetWork()) {
//            getSplashAD();
            showSpreadAd();
        } else {
            setTimer();
        }

        Intent intent = new Intent(this, AutoNoticeService.class);
        stopService(intent);//停止已经启动的通知服务（但闹钟还在，所以定时依然会再次启动服务）

        //配置默认参数
        setDefaultConfig();

        //保存app启动次数，一定次数后弹出好评送书弹框
        int newInt = ConfigManager.Instance().loadInt("firstSendBookFlag") + 1;
        ConfigManager.Instance().putInt("firstSendBookFlag", newInt);

        //请求微课审核接口
        if (Constant.mocVerifyCheck) {
            VerifyManager.getInstance().verifyMoc();
        } else {
            AbilityControlManager.getInstance().setLimitMoc(false);
        }

        //请求视频审核接口
        if (Constant.videoVerifyCheck) {
            VerifyManager.getInstance().verifyVideo();
        } else {
            AbilityControlManager.getInstance().setLimitVideo(false);
        }
    }

    private boolean CheckNetWork() {
        if (NetWorkState.isConnectingToInternet() && NetWorkState.getAPNType() != 1) {
            return true;
        }
        return false;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //点击广告返回后直接跳转
        if (isClickAd){
            isClickAd = false;
            toMain();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mContext = null;
        super.onDestroy();

        //关闭广告
        AdSpreadShowManager.getInstance().stopSpreadAd();
        //关闭倒计时
        RxTimer.getInstance().cancelTimer(downTimerTag);
    }

    //配置部分默认参数
    private void setDefaultConfig(){
        ConfigManager.Instance().putInt("isdowning", 0);
        LogUtil.e("软件内部版本" + currentVersion + "旧的" + lastVersion);
        if (currentVersion > lastVersion) { // 首次使用设置默认界面背景常亮
            ImportDatabase db = new ImportDatabase(mContext);
            db.openDatabase(ImportDatabase.DB_PATH + "/" + ImportDatabase.DB_NAME);
            ImportLibDatabase db2 = new ImportLibDatabase(mContext);
            db2.setPackageName(mContext.getPackageName());
            int lastVersionDb = 0, currentVersionDb = 1;
            db2.setVersion(lastVersionDb, currentVersionDb);// 有需要数据库更改使用 lib_database.sqlite
            db2.openDatabase(db2.getDBPath());
            ConfigManager.Instance().putInt("mode", 1);
            ConfigManager.Instance().putInt("isvip", 0);
            ConfigManager.Instance().putBoolean("saying", true);
            SettingConfig.Instance().setHighSpeed(false);
            SettingConfig.Instance().setSyncho(true);
            SettingConfig.Instance().setLight(true);
            SettingConfig.Instance().setAutoPlay(false);
            SettingConfig.Instance().setAutoStop(true);
            ConfigManager.Instance().putInt("version", currentVersion);
            ConfigManager.Instance().putBoolean("showChinese", true);
            ConfigManager.Instance().putBoolean("firstuse", true);
            ConfigManager.Instance().putInt("mode", 1);
            ConfigManager.Instance().putBoolean("autoplay", false);
            ConfigManager.Instance().putBoolean("autostop", true);
            ConfigManager.Instance().putString("applanguage", "zh");
            ConfigManager.Instance().putString("media_saving_path", LibFilePathUtil.getFileDirPath(this) + "/travel/audio/");
            ConfigManager.Instance().putInt("curBook", 0);
            ConfigManager.Instance().putInt("lately_one", 1001);
            ConfigManager.Instance().putInt("lately_two", 2001);
            ConfigManager.Instance().putInt("lately_three", 3001);
            ConfigManager.Instance().putInt("lately_four", 4001);
            ConfigManager.Instance().putBoolean("is_exercising", false);
            ConfigManager.Instance().putString("cur_tab", "text");
            ConfigManager.Instance().putString("updateAD", "1970-01-01");
            ConfigManager.Instance().putString("media_saving_path", LibFilePathUtil.getFileDirPath(this) + "/travel/audio/");
            ConfigManager.Instance().putInt("quesAppType", 115);
            String startTime = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = sdf.format(new Date());
            ConfigManager.Instance().putString("study_start_time", startTime);

            Intent intent2 = new Intent(WelcomeActivity.this, HelpUseActivity.class);
            intent2.putExtra("isFirstInfo", 0);
            startActivity(intent2);

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /********************************开屏时间计时器**********************************/
    //设置倒计时
    private static final String downTimerTag = "downTimerTag";
    private static final long defaultTime = 5000L;

    private void setTimer() {
        binding.adSkip.setText("跳过(" + defaultTime / 1000 + "s)");
        binding.adSkip.setOnClickListener(v -> {
            RxTimer.getInstance().cancelTimer(downTimerTag);
            toMain();
        });

        RxTimer.getInstance().multiTimerInMain(downTimerTag, 0, 1000L, new RxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                int showTime = (int) ((defaultTime - number * 1000L) / 1000);
                binding.adSkip.setText("跳过(" + showTime + "s)");

                if (showTime <= 0) {
                    RxTimer.getInstance().cancelTimer(downTimerTag);
                    toMain();
                }
            }
        });
    }

    //跳转到主界面
    private void toMain() {
        Intent intent3 = new Intent(WelcomeActivity.this,
                MainFragmentActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("isFirstInfo", 0);
        startActivity(intent3);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /*****************************新的开屏广告操作************************/
    //开屏广告接口是否完成
    private boolean isSplashAdLoaded = false;
    //是否已经点击了广告
    private boolean isClickAd = false;
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //广告倒计时时间
    private static final int AdDownTime = 5;
    //操作倒计时时间
    private static final int OperateTime = 5;
    //界面数据
    private AdSpreadViewBean spreadViewBean = null;

    //展示广告
    private void showSpreadAd(){
        if (spreadViewBean==null){
            spreadViewBean = new AdSpreadViewBean(binding.adImage, binding.adSkip, binding.adTips, binding.adLayout, new AdSpreadShowManager.OnAdSpreadShowListener() {
                @Override
                public void onLoadFinishAd() {
                    isSplashAdLoaded = true;
                    AdSpreadShowManager.getInstance().stopOperateTimer();
                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            ToastUtil.showToast(WelcomeActivity.this, "暂无内容");
                            return;
                        }

                        //设置点击
                        isClickAd = true;
                        //关闭计时器
                        AdSpreadShowManager.getInstance().stopAdTimer();
                        //跳转界面
                        Intent intent = new Intent();
                        intent.setClass(mContext, WebActivity.class);
                        intent.putExtra("url", jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;


                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_spread;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                //直接显示信息即可
                                ToastUtil.showToast(WelcomeActivity.this, showMsg);

                                if (isSuccess && UserInfoManager.getInstance().isLogin()) {
                                    UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
//                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    //关闭广告
                    AdSpreadShowManager.getInstance().stopSpreadAd();
                    //跳出
                    toMain();
                }

                @Override
                public void onAdError(String adType) {

                }

                @Override
                public void onAdShowTime(boolean isEnd, int lastTime) {
                    if (isEnd){
                        //跳转
                        toMain();
                    }else {
                        //开启广告计时器
                        binding.adSkip.setText("跳过("+lastTime+"s)");
                    }
                }

                @Override
                public void onOperateTime(boolean isEnd, int lastTime) {
                    if (isEnd){
                        //跳转到下一个
                        toMain();
                        return;
                    }

                    if (isSplashAdLoaded){
                        AdSpreadShowManager.getInstance().stopOperateTimer();
                        return;
                    }

                    AdLogUtil.showDebug(AdSpreadShowManager.TAG,"操作定时器时间--"+lastTime);
                }
            },AdDownTime,OperateTime);
            AdSpreadShowManager.getInstance().setShowData(this,spreadViewBean);
        }
        AdSpreadShowManager.getInstance().showSpreadAd();
    }
}
