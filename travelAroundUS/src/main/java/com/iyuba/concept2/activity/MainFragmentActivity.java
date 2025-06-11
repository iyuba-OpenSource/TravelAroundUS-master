package com.iyuba.concept2.activity;

import static com.iyuba.concept2.push.TestModeUtil.TYPE_LOG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.RankFragmentAdapter;
import com.iyuba.concept2.fragment.HomeFragment;
import com.iyuba.concept2.fragment.SmallVideoFragment;
import com.iyuba.concept2.fragment.VideoFragment;
import com.iyuba.concept2.lil.MocShowFragment;
import com.iyuba.concept2.lil.bottom.MainBottomAdapter;
import com.iyuba.concept2.lil.bottom.MainBottomBean;
import com.iyuba.concept2.lil.event.RefreshUserInfoEvent;
import com.iyuba.concept2.lil.manager.AbilityControlManager;
import com.iyuba.concept2.lil.ui.me.NewMeFragment;
import com.iyuba.concept2.lil.ui.search.NewSearchActivity;
import com.iyuba.concept2.listener.AppUpdateCallBack;
import com.iyuba.concept2.manager.VersionManager;
import com.iyuba.concept2.push.TestModeUtil;
import com.iyuba.concept2.sqlite.ImportDatabase;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.service.bgPlayService.Background;
import com.iyuba.core.common.service.bgPlayService.TravelBgPlayEvent;
import com.iyuba.core.common.sqlite.ImportLibDatabase;
import com.iyuba.core.common.util.ChangeVideoEvnet;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.event.SearchWordEvent;
import com.iyuba.core.lil.manager.TempDataManager;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.data.LoginType;
import com.iyuba.core.lil.util.LibResUtil;
import com.iyuba.core.lil.util.LibSPUtil;
import com.iyuba.core.lil.util.LibStackUtil;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.favor.data.model.BasicFavorPart;
import com.iyuba.module.favor.event.FavorItemEvent;
import com.iyuba.module.movies.ui.series.SeriesActivity;
import com.iyuba.play.ExtendedPlayer;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.PersonalType;
import personal.iyuba.personalhomelibrary.data.model.HeadlineTopCategory;
import personal.iyuba.personalhomelibrary.data.model.Voa;
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent;


//主界面的类
@RuntimePermissions
public class MainFragmentActivity extends BasisActivity implements AppUpdateCallBack,TestModeUtil.UpdateTestMode  {
    private FragmentManager fragmentManager;
    private HomeFragment homeFragmentNew;
    private Context mContext;
    private String version_code;
    private boolean isExit = false;// 是否点过退出
    public static final String SYSTEM_EXIT = "com.example.exitsystem.system_exit";
    //private MyReceiver receiver;

    private ViewPager viewPager;
    private RankFragmentAdapter fragmentAdapter;

    /***********底部菜单重置*********/
    private RecyclerView bottomView;
    private MainBottomAdapter bottomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //注册eventbus
        EventBus.getDefault().register(this);

        setContentView(R.layout.main);

        //这里使用秒验登录
        mobPreLogin();

        //获取用户数据并设置到本地
        setUserInfo();

        //共通模块处理
        setCommonModule();

        //设置引导版本数据
        ConfigManager.Instance().putInt("version",Constant.GUIDE_VERSION);

        RuntimeManager.setApplication(getApplication());
        RuntimeManager.setApplicationContext(getApplicationContext());
        RuntimeManager.setDisplayMetrics(this);
        mContext = this;
        fragmentManager = this.getSupportFragmentManager();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=562f4a3d");
        Constant.videoAddr = ConfigManager.Instance().loadString("media_saving_path");

//        setCommonData();
        initSet();
        bindService();
        initView();

        IntentFilter filter = new IntentFilter(); //注册广播，用于退出程序
        filter.addAction(SYSTEM_EXIT);

        //receiver = new MyReceiver(); 广播接收器
        //this.registerReceiver(receiver, filter);
        //myreceiverregist = true;
    }

    private void initViewPager() {
        //界面数据
        List<Fragment> fragmentList = new ArrayList<>();
        //底部数据
        List<MainBottomBean> bottomList = new ArrayList<>();

        //首页界面
        homeFragmentNew = new HomeFragment();
        fragmentList.add(homeFragmentNew);
        bottomList.add(new MainBottomBean(R.drawable.main_home_layer_list,R.drawable.main_home_press_layer_list,"首页"));

        //微课界面
        MocShowFragment mocShowFragment = MocShowFragment.getInstance();
        if (!AbilityControlManager.getInstance().isLimitMoc()){
            fragmentList.add(mocShowFragment);
            bottomList.add(new MainBottomBean(R.drawable.main_study_layer_list,R.drawable.main_study_press_layer_list,"微课"));
        }

        if (!AbilityControlManager.getInstance().isLimitVideo()){
            //视频界面
            VideoFragment videoFragmentNew = VideoFragment.getInstance();
            fragmentList.add(videoFragmentNew);
            bottomList.add(new MainBottomBean(R.drawable.main_video_layer_list,R.drawable.main_video_press_layer_list,"视频"));

            //小视频界面
            SmallVideoFragment smallVideoFragment = SmallVideoFragment.getInstance();
            fragmentList.add(smallVideoFragment);
            bottomList.add(new MainBottomBean(R.drawable.main_small_layer_list,R.drawable.main_small_press_layer_list,"小视频"));
        }


        //我的界面
        NewMeFragment newMeFragment = NewMeFragment.getInstance();
        fragmentList.add(newMeFragment);
        bottomList.add(new MainBottomBean(R.drawable.main_me_layer_list,R.drawable.main_me_press_layer_list,"我"));

        //配置适配器
        FragmentManager fm = getSupportFragmentManager();
        fragmentAdapter = new RankFragmentAdapter(fm, fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.setCurrentItem(0,false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomAdapter.setIndex(position);
                //这里根据点击操作停止播放
                MainBottomBean bottomBean = bottomList.get(position);
                if (!bottomBean.getText().equals("首页")){
                    if (homeFragmentNew!=null){
                        homeFragmentNew.stopPlayer();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //配置底部适配器
        //底部处理(前任真是个人才啊，前人挖坑，后人骂娘)
        //有时间尽量做成recyclerview的样式，把数据动态设置，然后进行显示
        bottomAdapter = new MainBottomAdapter(this,bottomList);
        GridLayoutManager manager = new GridLayoutManager(this,bottomList.size());
        bottomView.setLayoutManager(manager);
        bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setOnClickListener(new MainBottomAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                viewPager.setCurrentItem(position);
            }
        });

        //只有一个则隐藏即可
        if (bottomList.size()<=1){
            bottomView.setVisibility(View.GONE);
        }
    }

    public void initView() {
        bottomView = findViewById(R.id.l1);
        viewPager = findViewById(R.id.view_pager);

        initViewPager();
    }

    /**
     * 检查新版本
     */
    public void checkAppUpdate() {
        VersionManager.Instace(this).checkNewVersion(VersionManager.version, this);
    }

    public void initSet() {
        // 睡眠模式
//        msleepReceiver = new sleepReceiver();
//        IntentFilter mmIntentFilter = new IntentFilter("gotosleep");
//        registerReceiver(msleepReceiver, mmIntentFilter);
        int netState = NetWorkState.getAPNType();
        if (netState == 0) {
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle(R.string.alert_title);
            alert.setMessage(getResources().getString(
                    R.string.alert_net_content));
            alert.setIcon(android.R.drawable.ic_dialog_alert);
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources()
                            .getString(R.string.setting_title),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_SETTINGS);
                            startActivityForResult(intent, 1);
                        }
                    });
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getResources()
                            .getString(R.string.alert_btn_cancel),
                    (dialog, which) -> {
                    });
            alert.show();
        }
        boolean isFirst = ConfigManager.Instance().loadBoolean("firstuse");
        LoadFix();
    }

    private void bindService() {
        Intent intent = new Intent(mContext, Background.class);
        BackgroundManager.mContext = mContext;
        bindService(intent, BackgroundManager.Instance().conn, Context.BIND_AUTO_CREATE);
    }

    class sleepReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CustomToast.showToast(context, R.string.good_night, 2000);
            CrashApplication.getInstance().exit();
        }
    }

    private void LoadFix() {
        Constant.mode = ConfigManager.Instance().loadInt("mode");
        Constant.type = ConfigManager.Instance().loadInt("type");
        Constant.download = ConfigManager.Instance().loadInt("download");
    }

    /*private void saveFix() {
        if (AccountManager.Instance(mContext).userInfo != null) {
            new UserInfoOp(mContext)
                    .saveData(AccountManager.Instance(mContext).userInfo);
        }
        ConfigManager.Instance().putInt("mode", Constant.mode);
        ConfigManager.Instance().putInt("type", Constant.type);
        ConfigManager.Instance().putInt("download", Constant.download);
    }*/

    private void exit() {
//        saveFix();
        //unregisterReceiver(msleepReceiver);
        new Thread() {
            @Override
            public void run() {
                super.run();
                ImportDatabase.mdbhelper.close();
                ImportLibDatabase.mdbhelper.close();
                CrashApplication.getInstance().exit();
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                exit();
                break;
        }
        return false;
    }

    private void showAlertAndCancel(String msg,
                                    DialogInterface.OnClickListener ocl) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(msg);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getString(R.string.alert_btn_ok), ocl);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alert.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showAlertAndCancel(
                            getString(R.string.about_update_alert_1)
                                    + version_code
                                    + getString(R.string.about_update_alert_2),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent();
                                    intent.setClass(mContext, AboutActivity.class);
                                    startActivity(intent);
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        pressAgainExit();
    }

    private void pressAgainExit() {
        int isdowning = ConfigManager.Instance().loadInt("isdowning");
        if (isdowning <= 0) {
            if (isExit) {
                ConfigManager.Instance().putInt("isdowning", 0);
                exit();
                Intent intent = new Intent();
                intent.setAction(MainFragmentActivity.SYSTEM_EXIT);
                sendBroadcast(intent);
            } else {
                CustomToast.showToast(getApplicationContext(),
                        "再按一次退出程序", 1000);
                doExitInOneSecond();
            }
        } else {
            Dialog dialog = new AlertDialog.Builder(mContext)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_exit_content)
                    .setPositiveButton(R.string.alert_btn_exit,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    finish();
                                }
                            })
                    .setNeutralButton(R.string.alert_btn_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).create();
            dialog.show();
        }
    }

    private void doExitInOneSecond() {
        isExit = true;
        HandlerThread thread = new HandlerThread("doTask");
        thread.start();
        new Handler(thread.getLooper()).postDelayed(task, 1500);// 1.5秒内再点有效
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            isExit = false;
        }
    };

    private void getUid(int userId) {
        if (UserInfoManager.getInstance().isLogin()){
            UserInfoManager.getInstance().initUserInfo();
            //之后获取接口数据
            UserInfoManager.getInstance().getRemoteUserInfo(userId, null);
        }else {
            UserInfoManager.getInstance().clearUserInfo();
        }
    }

    public static long getTimeDate() {
        Date date = new Date();
        long unixTimestamp = date.getTime() / 1000 + 3600 * 8; //东八区;
        long days = unixTimestamp / 86400;
        return days;
    }

    private void stopPlay() {
        ExtendedPlayer extendedPlayer = BackgroundManager.Instance().bindService.getPlayer();
        if (extendedPlayer != null && extendedPlayer.isPlaying()) {
            extendedPlayer.pause();
        }

        BackPlayer videoViewBP = BackgroundManager.Instance().bindService.getVvv();
        if (videoViewBP != null && videoViewBP.isPlaying()) {
            videoViewBP.pause();
        }

    }

    /*private void setCommonData() {
        //这边根据登录状态存储数据
        if (UserInfoManager.getInstance().isLogin()){
            com.iyuba.module.user.User user = new com.iyuba.module.user.User();
            AccountManager managerLib = AccountManager.Instance(mContext);
            if (managerLib.checkUserLogin()) {
                user.vipStatus = String.valueOf(managerLib.getVipStatus());
                user.uid = managerLib.getUserId();
                user.name = managerLib.userName;
            } else {
                user.vipStatus = "0";
                user.uid = 0;
                user.name = "";
            }
            if (AdTimeUtils.isTime())
                user.vipStatus = "1";
            IyuUserManager.getInstance().setCurrentUser(user);
        }
    }*/

    @Override
    public void appUpdateSave(String version_code, String newAppNetworkUrl) {
        this.version_code = version_code;
        handler.sendEmptyMessage(0);
    }

    @Override
    public void appUpdateFaild() {
    }

    @Override
    public void appUpdateBegin(String newAppNetworkUrl) {

    }


//    private class MyReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            context.unregisterReceiver(this);
//            finish();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ChangeVideoEvnet(true));//刷新视频状态
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainFragmentActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE})
    public void initLocation() {
        getUid(UserInfoManager.getInstance().getUserId());
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE})
    public void locationDenied() {
        CustomToast.showToast(MainFragmentActivity.this, "权限开通才可以正常使用app，请到系统设置中开启", 3000);
        getUid(UserInfoManager.getInstance().getUserId());
    }
    private void initData() {
        String appId="549140";
        String appKey="5BaBaU49lp4w8sOs0wGwc8s4W";
        String appSecret="3F69B49fd835385a14D6c5E867f5E37A";
        //初始化push，调用注册接口
        addLog("AppId", appId);
        addLog("appKey",appKey);
        addLog("appSecret", appSecret);
        if(getPackageName() != null){
            addLog("pkgName", getPackageName());
        }
        TestModeUtil.setUpdateTestMode(this);
        onLogUpdate(TYPE_LOG);
        onLogUpdate(TestModeUtil.TYPE_STATUS);

        try {
        } catch (Exception e) {
            e.printStackTrace();
            TestModeUtil.addLogString(e.getLocalizedMessage());
        }
    }

    protected void addLog(String tag, String info) {
        TestModeUtil.addLogString(tag, info);
    }

    @Override
    public void onLogUpdate(int type) {
        //这里貌似没用
        /*if (type == TYPE_LOG) {
            logInfoTextView.post(new Runnable() {
                @Override
                public void run() {
                    String info = TestModeUtil.getLastLog();
                    if (info == null) return;
                    while (info != null) {
                        logInfoTextView.append(info + "\n");
                        info = TestModeUtil.getLastLog();
                    }
                    //滚动log到底部
                    //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        } else if (type == TestModeUtil.TYPE_STATUS) {
            LogUtil.w("LOG TYPE_STATUS");
        }*/
    }

    //个人中心中文章点击跳转
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ArtDataSkipEvent event) {
        Voa voa = event.voa;
        //文章跳转
        switch (event.type) {
            case "news":
                HeadlineTopCategory topCategory = event.headline;
                startActivity(TextContentActivity.getIntent2Me(mContext,
                        topCategory.id, topCategory.Title, topCategory.TitleCn, topCategory.type
                        , topCategory.Category, topCategory.CreatTime, topCategory.getPic(), topCategory.Source));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivityNew.getIntent2Me(mContext,
                        voa.categoryString, voa.title, voa.title_cn,
                        voa.getPic(),event.type, String.valueOf(voa.voaid), voa.sound));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(mContext,
                        voa.categoryString, voa.title, voa.title_cn, voa.getPic(),
                        event.type, String.valueOf(voa.voaid), voa.sound));//voa.getVipAudioUrl()
                break;
        }
    }

    //视频模块中点击现在升级操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlineGoVIPEvent headlineGoVIPEvent) {
        if (UserInfoManager.getInstance().isLogin()){
            Intent intent = new Intent(mContext, VipCenterGoldActivity.class);
            startActivity(intent);
        }else {
            ToastUtil.showToast(mContext,"请先登录");
        }
    }

    //收藏的视频中点击跳转
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FavorItemEvent fEvent) {
        //收藏页面点击
        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
        goFavorItem(fPart);
    }

    private void goFavorItem(BasicFavorPart part) {

        switch (part.getType()) {
            case "news":
                startActivity(TextContentActivity.getIntent2Me(mContext, part.getId(), part.getTitle(), part.getTitleCn(), part.getType()
                        , part.getCategoryName(), part.getCreateTime(), part.getPic(), part.getSource()));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(),
                        part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(),
                        part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(),
                        part.getType(), part.getId(), part.getSound()));
                break;
            case "series":
                Intent intent = SeriesActivity.buildIntent(mContext, part.getSeriesId(), part.getId());
                startActivity(intent);
                break;
            case HeadlineType.SMALLVIDEO:
                int code=1;
                int pageCount=1;
                int dataPage=0;
                Intent forOne = VideoMiniContentActivity.buildIntentForOne(this, part.getId(), dataPage, pageCount, code);
                startActivity(forOne);
                break;
        }
    }

    //下载后的视频中点击跳转
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        //视频下载后点击
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);
        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(this,dlPart.getCategoryName(),
                        dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(this,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
        }

    }

    /**************************后期的一些数据操作**************************/
    //初始化用户信息
    private void setUserInfo(){
        if (UserInfoManager.getInstance().isLogin()) {
            getUid(UserInfoManager.getInstance().getUserId());
        }else {
            //这里处理下，如果另一个缓存中可以查到uid，则使用新的uid进行处理
            SharedPreferences tempPreference = LibSPUtil.getPreferences(LibResUtil.getInstance().getContext(), "config");
            String userIdStr = tempPreference.getString("userId", "0");
            if (!TextUtils.isEmpty(userIdStr)&&!userIdStr.equals("0")){
                int userId = Integer.parseInt(userIdStr);
                getUid(userId);

                //将原来的数据处理掉
                tempPreference.edit().clear().apply();
                return;
            }
        }
    }

    //mob预登录操作
    private void mobPreLogin(){
        LoginType.getInstance().setCurLoginType(LoginType.loginByVerify);
        if (LoginType.getInstance().getCurLoginType().equals(LoginType.loginByVerify)){
            // 建议提前调用预登录接口，可以加快免密登录过程，提高用户的体验。
            SecVerify.preVerify(new PreVerifyCallback() {
                @Override
                public void onComplete(Void unused) {
                    TempDataManager.getInstance().setMobVerify(true);
                }

                @Override
                public void onFailure(VerifyException e) {
                    TempDataManager.getInstance().setMobVerify(false);
                }
            });
        }
    }

    //共通模块处理
    private void setCommonModule(){
        PersonalHome.setMainPath("com.iyuba.concept2.activity.MainFragmentActivity");
        PersonalHome.setCategoryType(PersonalType.VOA);
        PersonalHome.setAppInfo(Constant.APPID,Constant.APPType);
    }

    //回调操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayHelpEvent(TravelBgPlayEvent event){
        if (event.getType().equals(TravelBgPlayEvent.existApp)){
            LibStackUtil.getInstance().finishAll();
            System.exit(0);
        }
    }

    //查询单词
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWord(SearchWordEvent event){
        if (event.getType().equals(SearchWordEvent.to_searchWord)){
            NewSearchActivity.start(this,event.getKeyWord());
        }
    }

    //刷新用户信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshUserInfo(RefreshUserInfoEvent event){
        if (UserInfoManager.getInstance().isLogin()){
            UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
        }
    }
}
