package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.DownloadStateManager;
import com.iyuba.concept2.protocol.ShareRequest;
import com.iyuba.concept2.sqlite.mode.Book;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.op.BookOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.concept2.util.SP;
import com.iyuba.concept2.util.UtilFile;
import com.iyuba.concept2.widget.SleepDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.event.SleepEvent;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
import com.iyuba.core.common.retrofitapi.result.HostUpdateResult;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.CommonUtils;
import com.iyuba.core.common.util.FileSize;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.microclass.activity.DelCourseDataActivity;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.module.commonvar.CommonVars;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SetActivity extends BaseStackActivity {

    private Context mContext;

    private CheckBox CheckBoxHighSpeedDwonload, checkBoxPushMessage,
            CheckBoxScreenLit, CheckBoxSyncho, checkSpcEvaCn, checkSpeedPlayer,checkBoxSelect;
    private Button backBtn;
    private View btnHighSpeedDownload, btnScreenLit, btnPushMessage, btnSyncho,btnSelectWord,
            sleepButton, pathButton, btnClearPic, btnClearVideo,
            recommendButton, aboutBtn, feedbackBtn, clearClassButton,
            btnSpcEvaCn;
    private TextView picSize, soundSize,mPrivacyAndUserinfoTxt;
    private static int hour, minute, totaltime, volume;// total用于计算时间，volume用于调整音量,睡眠模式用到的
    private static boolean isSleep = false;// 睡眠模式是否开启
    private String lastSavingPath, nowSavingPath;
    private ArrayList<Voa> voaList = new ArrayList<Voa>();

    private RelativeLayout mRlSleep,mLogoutUser,mPrivacyAndUserinfo;
    private TextView mTvSleepTime;
    private ImageView mCbCheckSleep;

    //更新服务
    private View btnHostUpdate;
    private ImageView ivLoading;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,SetActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        mContext = this;

        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        initProtocol();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.sendEmptyMessage(6);
        handler.sendEmptyMessage(7);
        handler.sendEmptyMessage(8);
        ((TextView) findViewById(R.id.savingpath_path)).setText(ConfigManager
                .Instance().loadString("media_saving_path"));// 显示路径

        MobclickAgent.onResume(mContext);
    }

    public void initCheckBox() {
        checkBoxSelect = findViewById(R.id.CheckBox_eva_select_word);
        if(ConfigManager.Instance().loadInt("eva_select_word") == 0){
            checkBoxSelect.setChecked(false);
        }else{
            checkBoxSelect.setChecked(true);
        }
        CheckBoxHighSpeedDwonload = (CheckBox) findViewById(R.id.CheckBox_high_speed_download);
        CheckBoxHighSpeedDwonload.setChecked(SettingConfig.Instance()
                .isHighSpeed());
        CheckBoxHighSpeedDwonload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().isVip()) {
                    if (SettingConfig.Instance().isHighSpeed()) {
                        SettingConfig.Instance().setHighSpeed(false);
                    } else {
                        SettingConfig.Instance().setHighSpeed(true);
                    }
                    CheckBoxHighSpeedDwonload.setChecked(SettingConfig
                            .Instance().isHighSpeed());
                } else {
                    CheckBoxHighSpeedDwonload.setChecked(false);
                    AlertDialog.Builder builder = new Builder(mContext);
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", null);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setMessage(getResources().getString(
                            R.string.high_speed_download_toast));
                    builder.show();
                }
            }
        });
        checkBoxPushMessage = (CheckBox) findViewById(R.id.CheckBox_PushMessage);
        checkBoxPushMessage.setChecked(SettingConfig.Instance().isPush());
        checkBoxPushMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        setPush();
                    }
                });
        CheckBoxScreenLit = (CheckBox) findViewById(R.id.CheckBox_ScreenLit);
        CheckBoxScreenLit.setChecked(SettingConfig.Instance().isLight());
        CheckBoxScreenLit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        SettingConfig.Instance().setLight(isChecked);
                    }
                });
        CheckBoxSyncho = (CheckBox) findViewById(R.id.CheckBox_auto_syncho);
        CheckBoxSyncho.setChecked(SettingConfig.Instance().isSyncho());
        CheckBoxSyncho.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        SettingConfig.Instance().setSyncho(isChecked);
                    }
                });
        checkSpcEvaCn = (CheckBox) findViewById(R.id.CheckBox_speecheva_cnshow);
        checkSpcEvaCn.setChecked(SettingConfig.Instance().isSpcCn());
        checkSpcEvaCn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        SettingConfig.Instance().setSpcCn(isChecked);
                    }
                });
        checkSpeedPlayer = (CheckBox) findViewById(R.id.CheckBox_speed_player);
        checkSpeedPlayer.setChecked(SettingConfig.Instance().isSpeedPlayer());
        checkSpeedPlayer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SettingConfig.Instance().setSpeedPlayer(isChecked);
                if (UserInfoManager.getInstance().isVip()) {
                    if (SettingConfig.Instance().isSpeedPlayer()) {
                        SettingConfig.Instance().setSpeedPlayer(false);
                    } else {
                        SettingConfig.Instance().setSpeedPlayer(true);
                    }
                    //如果后台有播放就暂停播放
                    if(BackgroundManager.Instance().bindService.getPlayer().isPlaying()){
                        BackgroundManager.Instance().bindService.getPlayer().pause();
                    }
                    if (BackgroundManager.Instance().bindService.getVvv().isPlaying()){
                        BackgroundManager.Instance().bindService.getVvv().pause();
                    }

                    //BackgroundManager.Instance().bindService.isPlayerChange=true;

                    checkSpeedPlayer.setChecked(SettingConfig
                            .Instance().isSpeedPlayer());
                } else {
                    checkSpeedPlayer.setChecked(false);
                    AlertDialog.Builder builder = new Builder(mContext);
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", null);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setMessage("您好，只有vip用户，才能拥有调速权限！");
                    builder.show();
                }
            }
        });


    }

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private String showadtime = "2018-02-13 12:00:00";

    public void initWidget() {
        btnHighSpeedDownload = findViewById(R.id.btn_high_speed_download);
        btnScreenLit = findViewById(R.id.btn_screen_lit);
        btnSelectWord = findViewById(R.id.btn_eva_select_word);
        btnPushMessage = findViewById(R.id.btn_push_message);
        btnSyncho = findViewById(R.id.btn_auto_syncho);
        sleepButton = findViewById(R.id.sleep_mod);
        pathButton = findViewById(R.id.setting_saving_path);
        btnClearPic = findViewById(R.id.clear_pic);
        btnClearVideo = findViewById(R.id.clear_video);

        btnHostUpdate = findViewById(R.id.btn_host_update);
        ivLoading = findViewById(R.id.iv_loading);
        ivLoading.setVisibility(View.GONE);

        recommendButton = findViewById(R.id.recommend_btn);
        aboutBtn = findViewById(R.id.about_btn);
 
        mRlSleep =findViewById(R.id.rl_btn_sleep_time);
        mTvSleepTime =findViewById(R.id.tv_sleep_time);
        mCbCheckSleep =findViewById(R.id.CheckBox_sleep_player);
        mLogoutUser=findViewById(R.id.logout_user);
        mPrivacyAndUserinfo=findViewById(R.id.privacyAndUserinfo);
        mCbCheckSleep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int sleepTime = Integer.parseInt(String.valueOf(SP.get(SetActivity.this, "sp_play_sleep_time", 0)));
                if (sleepTime==0){
                    Intent intent = new Intent(mContext, SleepDialog.class);
                    startActivityForResult(intent, 23);
                }else {
                    mTvSleepTime.setText("睡眠模式关闭");
                    mCbCheckSleep.setSelected(SettingConfig.Instance().isPush());
                    EventBus.getDefault().post(new SleepEvent(true, 0));
                    SP.put(mContext, "sp_play_sleep_time", 0);
                }
            }
        });

        int timePlay =Integer.parseInt(String.valueOf(SP.get(this, "sp_play_sleep_time", 0)));
        if (timePlay==0){
            mTvSleepTime.setText("睡眠模式关闭");
            mCbCheckSleep.setSelected(SettingConfig.Instance().isPush());
        }else {
            mTvSleepTime.setText(timePlay+"分钟后停止播放");
            minute=timePlay;
            mCbCheckSleep.setSelected(SettingConfig.Instance().isLight());
        }

        long time = System.currentTimeMillis();
        Log.e("Tag", "--bgtime--" + time);
        Date date = null;
        try {
            date = sdf2.parse(showadtime);
            long thetime = date.getTime();
            Log.e("show-time", date.getTime() + "");
            if (time < thetime) {
                aboutBtn.setVisibility(View.GONE);
            } else {
                aboutBtn.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            aboutBtn.setVisibility(View.VISIBLE);
        }
        feedbackBtn = findViewById(R.id.feedback_btn);
        clearClassButton = findViewById(R.id.clear_microclass);

        initCacheSize();
        initListener();
    }


    private void initProtocol() {
        mPrivacyAndUserinfoTxt =findViewById(R.id.privacyAndUserinfoTxt);

        String privacy1 = "使用协议";
        String privacy2 = "及";
        String privacy3 = "隐私政策";

        ClickableSpan userAgreementClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SetActivity.this, com.iyuba.core.common.activity.Web.class);
                intent.putExtra("url", CommonUtils.getUserAgreementUrl(SetActivity.this));
                intent.putExtra("title", "用户协议");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        };
        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SetActivity.this, Web.class);
                intent.putExtra("url", CommonUtils.getPrivacyPolicyUrl(SetActivity.this));
                intent.putExtra("title", "隐私政策");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        };


        int userAgreementStart =  0;
        int userAgreementEnd = userAgreementStart+privacy1.length();
        int privacyStart = privacy1.length() + privacy2.length();
        int privacyEnd = privacyStart + privacy3.length();

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(privacy1);
        strBuilder.append(privacy2);
        strBuilder.append(privacy3);
        strBuilder.setSpan(userAgreementClickableSpan, userAgreementStart, userAgreementEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(privacyClickableSpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mPrivacyAndUserinfoTxt.setText(strBuilder);
        mPrivacyAndUserinfoTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        mPrivacyAndUserinfoTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void initCacheSize() {
        picSize = (TextView) findViewById(R.id.picSize);
        soundSize = (TextView) findViewById(R.id.soundSize);
        new Thread(new Runnable() {
            // 获取图片大小
            @Override
            public void run() {
                String strings;
                strings = getSize(0);
                handler.obtainMessage(0, strings).sendToTarget();

            }
        }).start();

        new Thread(new Runnable() {
            // 获取音频大小
            @Override
            public void run() {
                String strings;
                strings = getSize(1);
                handler.obtainMessage(1, strings).sendToTarget();

            }
        }).start();
    }

    public void initListener() {
        btnHighSpeedDownload.setOnClickListener(ocl);
        btnScreenLit.setOnClickListener(ocl);
        btnSelectWord.setOnClickListener(ocl);
        btnPushMessage.setOnClickListener(ocl);
        sleepButton.setOnClickListener(ocl);
//        pathButton.setOnClickListener(ocl);
        btnSyncho.setOnClickListener(ocl);
        btnClearPic.setOnClickListener(ocl);
        btnClearVideo.setOnClickListener(ocl);
        recommendButton.setOnClickListener(ocl);
        aboutBtn.setOnClickListener(ocl);
        feedbackBtn.setOnClickListener(ocl);
        clearClassButton.setOnClickListener(ocl);
        mLogoutUser.setOnClickListener(ocl);

        //暂时关闭这里的按钮操作
        /*mRlSleep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SleepDialog.class);
                startActivityForResult(intent, 23);
            }
        });*/

        btnHostUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ivLoading.setVisibility(View.VISIBLE);
                LibGlide3Util.loadGif(mContext,R.drawable.ic_loading,0,ivLoading);

                //加载更新服务
                ApiRequestFactory.getHostUpdate().getHostUpdate(
                        Constant.APPID,
                        ConfigManager.Instance().getDomainShort(),
                        ConfigManager.Instance().getDomainLong())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<HostUpdateResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(mContext,"更新服务失败");
                                ivLoading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(HostUpdateResult hostUpdate) {
                                ivLoading.setVisibility(View.GONE);

                                if ((hostUpdate.getResult() == 201 && hostUpdate.getUpdateflg()==1)
                                        ||(hostUpdate.getResult() == 200 && hostUpdate.getUpdateflg() == 0)){
                                    if (!TextUtils.isEmpty(hostUpdate.getShort1())
                                            && !hostUpdate.getShort1().equals(ConfigManager.Instance().getDomainShort())){
                                        ConfigManager.Instance().setDomainShort(hostUpdate.getShort1());
                                        CommonVars.domain = hostUpdate.getShort1();
                                    }

                                    if (!TextUtils.isEmpty(hostUpdate.getShort2())
                                            && !hostUpdate.getShort2().equals(ConfigManager.Instance().getDomainLong())){
                                        ConfigManager.Instance().setDomainLong(hostUpdate.getShort2());
                                        CommonVars.domainLong = hostUpdate.getShort2();
                                    }

                                    ToastUtil.showToast(mContext,"更新服务成功");

                                    //更新共通模块
                                    IHeadline.resetMseUrl();
                                    String extraUrl = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/test/ai/";
                                    IHeadline.setExtraMseUrl(extraUrl);
                                    String extraMergeUrl = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/test/merge/";
                                    IHeadline.setExtraMergeAudioUrl(extraMergeUrl);
                                }else {
                                    ToastUtil.showToast(mContext,"更新服务失败");
                                }
                            }
                        });
            }
        });
    }

    OnClickListener ocl = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent = null;

            switch (arg0.getId()) {
                case R.id.btn_high_speed_download:
                    setHighSpeedDownload();
                    break;
                case R.id.btn_screen_lit:
                    setScreenLit();
                    break;
                case R.id.btn_eva_select_word:
                    setSelectWord();
                    break;
                case R.id.btn_push_message:
                    setPush();
                    break;
                case R.id.btn_auto_syncho:
                    setAutoSyncho();
                    break;
                case R.id.sleep_mod://睡眠模式
                    intent = new Intent(mContext, SleepDialog.class);
                    startActivityForResult(intent, 23);// 第二个参数requestcode随便写的，应该定义个static比较好
                    break;
                case R.id.setting_saving_path:
                    lastSavingPath = ConfigManager.Instance().loadString(
                            "media_saving_path");
                    intent = new Intent(mContext, FileBrowserActivity.class);
                    startActivityForResult(intent, 25);
                    break;
                case R.id.clear_pic:
                    if (picSize.equals("0B")){
                        ToastUtil.showToast(SetActivity.this,"暂无缓存数据");
                        return;
                    }

                    CustomToast.showToast(mContext, R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
                    new CleanBufferAsyncTask("image").execute();
                    break;
                case R.id.clear_video:
                    if (soundSize.equals("0B")){
                        ToastUtil.showToast(SetActivity.this,"暂无缓存数据");
                        return;
                    }

                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(
                                    getResources().getString(R.string.alert_title))
                            .setMessage(
                                    getResources()
                                            .getString(R.string.setting_alert))
                            .setPositiveButton(
                                    getResources().getString(R.string.alert_btn_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            CustomToast.showToast(
                                                            mContext,
                                                            R.string.setting_deleting,
                                                            2000);// 这里可以改为引用资源文件
                                            new CleanBufferAsyncTask("video").execute();
                                        }
                                    })
                            .setNeutralButton(
                                    getResources().getString(
                                            R.string.alert_btn_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    }).create();
                    dialog.show();
                    break;
                case R.id.recommend_btn:
                    Log.e("分享应用", "开始");
                    prepareMessage();
                    break;
                case R.id.about_btn:
                    intent = new Intent();
                    intent.setClass(mContext, AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.feedback_btn:
                    intent = new Intent();
                    intent.setClass(mContext, FeedbackActivity.class);
                    startActivity(intent);
                    break;
                case R.id.clear_microclass:
                    intent = new Intent();
                    intent.setClass(mContext, DelCourseDataActivity.class);
                    startActivity(intent);
                    break;
                case R.id.logout_user:
                    //跳转到注销账户页面
                    intent = new Intent();
                    intent.setClass(mContext, LogoutUserActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    public void setHighSpeedDownload() {
        if (UserInfoManager.getInstance().isVip()) {
            if (SettingConfig.Instance().isHighSpeed()) {
                SettingConfig.Instance().setHighSpeed(false);
            } else {
                SettingConfig.Instance().setHighSpeed(true);
            }

            CheckBoxHighSpeedDwonload.setChecked(SettingConfig.Instance()
                    .isHighSpeed());
        } else {
            AlertDialog.Builder builder = new Builder(mContext);
            builder.setTitle("提示");
            builder.setPositiveButton("确定", null);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(getResources().getString(
                    R.string.high_speed_download_toast));
            builder.show();
        }
    }

    public void setScreenLit() {
        if (SettingConfig.Instance().isLight()) {
            SettingConfig.Instance().setLight(false);
        } else {
            SettingConfig.Instance().setLight(true);
        }
        CheckBoxScreenLit.setChecked(SettingConfig.Instance().isLight());
    }

    public void setSelectWord() {
        if(checkBoxSelect.isChecked()){
            checkBoxSelect.setChecked(false);
            ConfigManager.Instance().putInt("eva_select_word",0);
        }else{
            checkBoxSelect.setChecked(true);
            ConfigManager.Instance().putInt("eva_select_word",1);
        }

    }

    private void setPush() {
        if (checkBoxPushMessage.isChecked()) {
            SettingConfig.Instance().setPush(true);
        } else {
            SettingConfig.Instance().setPush(false);
        }
    }

    public void setAutoSyncho() {
        if (SettingConfig.Instance().isSyncho()) {
            SettingConfig.Instance().setSyncho(false);
        } else {
            SettingConfig.Instance().setSyncho(true);
        }
        CheckBoxSyncho.setChecked(SettingConfig.Instance().isSyncho());
    }

    public void initSleep() {
        if (!isSleep) {
            ((TextView) findViewById(R.id.sleep_state))
                    .setText(R.string.setting_sleep_state_off);
        } else {
            ((TextView) findViewById(R.id.sleep_state)).setText(String.format(
                    "%02d:%02d", hour, minute));
        }
    }

    Handler sleepHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int count = 0;
            AudioManager am = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (hour + minute != 0) {// 时间没结束
                        count++;
                        if (count % 10 == 0) {
                            if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > 2) {
                                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_LOWER, 0);// 第三参数为0代表不弹出提示。
                            }
                        }
                        totaltime--;
                        ((TextView) findViewById(R.id.sleep_state)).setText(String
                                .format("%02d:%02d", hour, minute));
                        hour = totaltime / 60;
                        minute = totaltime % 60;
                        sleepHandler.sendEmptyMessageDelayed(0, 60000);
                    } else {// 到结束时间
                        isSleep = false;
                        ((TextView) findViewById(R.id.sleep_state))
                                .setText(R.string.setting_sleep_state_off);
                        Intent intent = new Intent();
                        intent.setAction("gotosleep");
                        mContext.sendBroadcast(intent);
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    String strings = (String) msg.obj;
                    picSize.setText(strings);
                    break;
                case 1:
                    String string = (String) msg.obj;
                    soundSize.setText(string);
                    break;
                case 2:
                    CustomToast.showToast(mContext,
                            R.string.file_path_move_success, 1000);
                    break;
                case 3:
                    CustomToast.showToast(mContext,
                            R.string.file_path_move_exception, 1000);
                    break;
                case 6:
                    initWidget();
                    break;
                case 7:
                    initCheckBox();
                    break;
                case 8:
                    initSleep();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23 && resultCode == 1) {// 睡眠模式设置的返回结果
            hour = data.getExtras().getInt("hour");
            minute = data.getExtras().getInt("minute");
            if (minute==0){
                mCbCheckSleep.setSelected(SettingConfig.Instance().isPush());
                //取消睡眠模式
                EventBus.getDefault().post(new SleepEvent(true, 0));
                mTvSleepTime.setText("睡眠模式关闭");
                SP.put(this, "sp_play_sleep_time", 0);
            }else {
                mCbCheckSleep.setSelected(SettingConfig.Instance().isLight());
                //睡眠模式开启
                EventBus.getDefault().post(new SleepEvent(false, minute));
                mTvSleepTime.setText(minute+"分钟后停止播放");
                SP.put(this, "sp_play_sleep_time", minute);
            }

        } else if (requestCode == 25 && resultCode == 2) {// 改变存储路径的结果
            nowSavingPath = data.getExtras().getString("nowSavingPath");
            if (!nowSavingPath.equals(lastSavingPath)) {
                Dialog dialog = new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(
                                getResources().getString(R.string.alert_title))
                        .setMessage(
                                getResources().getString(
                                        R.string.setting_file_path_ask))
                        .setPositiveButton(
                                getResources().getString(R.string.alert_btn_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        if (lastSavingPath == null
                                                || nowSavingPath == null) {
                                            return;
                                        }
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (movePath(lastSavingPath,
                                                        nowSavingPath)) {
                                                    handler.sendEmptyMessage(2);
                                                } else {
                                                    handler.sendEmptyMessage(3);
                                                }

                                            }
                                        }).start();
                                        CustomToast
                                                .showToast(
                                                        mContext,
                                                        R.string.setting_file_path_moving,
                                                        2000);// 这里可以改为引用资源文件
                                    }
                                })
                        .setNeutralButton(
                                getResources().getString(
                                        R.string.alert_btn_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();
                dialog.show();

            }
        }
    }

    ;

    private boolean movePath(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
//				if (temp.isFile() && temp.getName().startsWith("temp_audio_")) {
                boolean aa = temp.isFile();
                String bb = temp.getName().substring(temp.getName().indexOf("."));
                if (temp.isFile() && temp.getName().substring(temp.getName().indexOf(".")).equals(".mp3")) {
                    if (!UtilFile.copyFile(temp.getPath(), newPath + "/"
                            + (temp.getName()).toString())) {
                        isok = false;
                    } else {
                        // 移动成功，删除原来的
                        if (temp.delete()) {
                            Log.d("shanchu", "chenggong");
                        } else {
                            Log.d("shanchu", "shibai");
                        }
                    }

                }
                if (temp.isDirectory()) {// 如果是子文件夹
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

    private void prepareMessage() {

        showShare();

    }

    private void showShare() {
        //String siteUrl = "http://app."+Constant.IYBHttpHead+"/android/androidDetail.jsp?id=232";
        String siteUrl = Constant.shareUrl;
        String text = "讲真、你与学霸只有一个" + Constant.APPName + "的距离";
        String imageUrl = "http://app."+Constant.IYBHttpHead()+"/android/images/FamilyAlbum/FamilyAlbum.png";
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(Constant.APPName);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APPName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", arg2.toString());

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");
                if (UserInfoManager.getInstance().isLogin()) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();
                    if (arg0.getName().equals("QQ")
                            || arg0.getName().equals("Wechat")
                            || arg0.getName().equals("WechatFavorite")) {
                        msg.what = 49;
                    } else if (arg0.getName().equals("QZone")
                            || arg0.getName().equals("WechatMoments")
                            || arg0.getName().equals("SinaWeibo")
                            || arg0.getName().equals("TencentWeibo")) {
                        msg.what = 19;
                    }
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(this);
    }


    private void getIyubi(String to) {
        String sigMd5 = UserInfoManager.getInstance().getUserId()
                + Constant.APPID + "android" + to + "20001" + 0 + "iyuba"; // 认证码Md5(userId+appId+from+to+titleId+type+’iyuba’)
        sigMd5 = MD5.getMD5ofStr(sigMd5);
        ClientSession.Instace().asynGetResponse(
                new ShareRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
                        "20001", to, sigMd5), new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response,
                                           BaseHttpRequest request, int rspCookie) {
                    }
                }, null, null);
    }

    class CleanBufferAsyncTask extends AsyncTask<Void, Void, Void> {
        private String filepath = Constant.picAddr;
        public String result;
        private String cachetype;
        private VoaOp voaOp;
        private BookOp bookOp;
        private List<Book> bookList;
        private List<DownloadInfo> infoList;

        public CleanBufferAsyncTask(String type) {
            this.cachetype = type;
            if (type.equals("image")) {
                filepath = Constant.picAddr;// 此处在voa常速英语中改为filepath=Constant.Instance().getPicPos();Constant文件不一致
            } else if (type.equals("video")) {
                DownloadStateManager manager = DownloadStateManager.getInstance();
                bookOp = manager.bookOp;
                infoList = manager.downloadList;
                bookList = manager.bookList;
                filepath = ConfigManager.Instance().loadString(
                        "media_saving_path");
            }
        }

        public boolean Delete() {
            File file = new File(filepath);
            if (file.isFile()) {
                file.delete();
                return true;
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                if (files != null && files.length == 0) {
                    return false;
                } else {
                    for (int i = 0; i < files.length; i++) {
                        files[i].delete();
                    }
                    return true;
                }
            } else {
                return false;
            }
        }

        public DownloadInfo getDownloadInfo(int voaId) {
            for (DownloadInfo info : infoList) {
                if (info.voaId == voaId) {
                    return info;
                }
            }

            return null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Delete()) {
                if (cachetype.equals("image")) {
                    picSize.post(new Runnable() {
                        @Override
                        public void run() {
                            picSize.setText("0B");
                        }
                    });
                } else if (cachetype.equals("video")) {
                    for (Book book : bookList) {
                        book.downloadNum = 0;
                        book.downloadState = 0;
                        bookOp.updateDownloadNum(book);
                    }

                    voaOp = new VoaOp(mContext);
                    voaList = (ArrayList<Voa>) voaOp.findDataFromDownload();
                    if (voaList != null) {
                        Iterator<Voa> iteratorVoa = voaList.iterator();
                        while (iteratorVoa.hasNext()) {
                            Voa voaTemp = iteratorVoa.next();
                            voaOp.deleteDataInDownload(voaTemp.voaId);
                            DownloadStateManager.getInstance().delete(
                                    voaTemp.voaId);
                        }
                        soundSize.post(new Runnable() {
                            @Override
                            public void run() {
                                soundSize.setText("0B");
                            }
                        });
                    }
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //这里直接显示删除完成就可以了
                        //CustomToast.showToast(mContext, R.string.setting_del_fail, 1000);
                        CustomToast.showToast(mContext, "删除完成", 1000);
                    }
                });
            }
            return null;
        }
    }

    private String getSize(int type) {
        if (type == 0) {
            return FileSize.getInstance().getFormatFolderSize(
                    new File(Constant.envir + "/image"));
        } else {
            return FileSize.getInstance().getFormatFolderSize(
                    new File(ConfigManager.Instance().loadString(
                            "media_saving_path")));
        }
    }

}
