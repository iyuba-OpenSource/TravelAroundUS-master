package com.iyuba.core.common.service.bgPlayService;
/**
 * 后台播放服务
 *
 * @author 陈彤     zh修改2019.03.26  增加睡眠模式
 * @version 1.1
 * 更新内容 AudioManager管理与其他 播放器播放冲突问题
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.event.SleepEvent;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.lib.R;
import com.iyuba.play.ExtendedPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Background extends Service {
    private AudioManager mAm;
    private BackPlayer vvv;
    private ExtendedPlayer vv;
    private MediaPlayer mediaPlayer;
    private int voaid;
    private BroadcastReceiver mBroadcastReceiver;
    private MyBinder myBinder = new MyBinder();
    private MyOnAudioFocusChangeListener mListener;
    private boolean mPausedByTransientLossOfFocus;
    //public boolean isPlayerChange=false;//是否更换了播放器

    private Handler mSleepHandler;


    public class MyBinder extends Binder {
        public Background getService() {
            return Background.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        registerBoradcastReceiver();
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        unRegisterBoradcastReceiver();
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAm = (AudioManager) getApplicationContext().getSystemService(
                Context.AUDIO_SERVICE);
        mListener = new MyOnAudioFocusChangeListener();
        mSleepHandler = new Handler();
        EventBus.getDefault().register(this);
        registerPlayReceiver();

        //默认创建
        showNotification(true,false,null,null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegisterBoradcastReceiver();
        unregisterPlayReceiver();

        mAm.abandonAudioFocus(mListener);
        if (SettingConfig.Instance().isSpeedPlayer())
            try {
            if (vv!=null)
                vv.stopPlay();
            }catch (Exception e){
                LogUtils.e("播放器关闭失败！");
            }
        else{
            if(vvv!=null)
            vvv.stopPlayback();
        }
        mSleepHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    public void init(Context mContext) {
        vv = new ExtendedPlayer(mContext);
        vvv = new BackPlayer(mContext);
        mAm.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    public void putMediaPlayer(MediaPlayer mediaPlayer){
        this.mediaPlayer=mediaPlayer;
    }

    public  MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public ExtendedPlayer getPlayer() {
        return vv;
    }

    public BackPlayer getVvv() {
        return vvv;
    }

    public int getTag() {
        return voaid;
    }

    public void setTag(int id) {
        voaid = id;
    }

    public void next() {
    }

    public void before() {
    }

    public void registerBoradcastReceiver() {
        /*PhoneStateListener p = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (vv != null && vv.isPlaying()) {
                            vv.pause();
                        } else if (vvv != null && vvv.isPlaying()) {
                            vvv.pause();
                        }
                        break;
                    default:
                        break;
                }
            }

            ;
        };
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(p, PhoneStateListener.LISTEN_CALL_STATE);*/
    }

    private void unRegisterBoradcastReceiver() {
        // TODO Auto-generated method stub
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    private class MyOnAudioFocusChangeListener implements
            OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (vv != null && vv.isPlaying()) {
                        vv.pause();// 因为会长时间失去，所以直接暂停
                    } else if (vvv != null && vvv.isPlaying()) {
                        vvv.pause();
                    }
                    mPausedByTransientLossOfFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (vv != null && vv.isPlaying()) {
                        vv.pause();// 短暂失去焦点，先暂停。同时将标志位置成重新获得焦点后就开始播放
                    } else if (vvv != null && vvv.isPlaying()) {
                        vvv.pause();
                    }
                    mPausedByTransientLossOfFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 重新获得焦点，且符合播放条件，开始播放
//                    if (vv != null && !vv.isPlaying() && mPausedByTransientLossOfFocus) {
//                        mPausedByTransientLossOfFocus = false;
//                        vv.restart();
//                    } else if (vvv != null && !vvv.isPlaying() && mPausedByTransientLossOfFocus) {
//                        mPausedByTransientLossOfFocus = false;
//                        vvv.start();
//                    }
                    break;
            }
        }
    }


    @Subscribe
    public void onEvent(SleepEvent event) {
        if (event.isCancel) {
            mSleepHandler.removeCallbacksAndMessages(null);
        } else {
            mSleepHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (vv!=null&&vv.isPlaying()) {
                        vv.pause();
                        //vv.stopPlay();
                    }
                    if (vvv!=null&&vvv.isPlaying()){
                        vvv.pause();
                        //vvv.stopPlayback();
                    }
                }
            }, event.minute * 60 * 1000);
        }
    }


    /***********************************通知显示******************************/
    //app名称
    private static final String APP_NAME = Constant.APPName;
    //消息id
    public static final int NOTIFICATION_ID = Integer.parseInt(Constant.APPID);
    //消息类型
    private static final String NOTIFICATION_NAME = APP_NAME+"通知";

    public void showNotification(boolean isInit,boolean isPlay,String title,Class studyActivityClz){
        String showText = "";
        PendingIntent pendingIntent = null;

        if (isInit){
            title = Constant.APPName;
            showText = title+"正在运行";
            pendingIntent = null;
        }else {
            if (isPlay){
                showText = "正在播放";
            }else {
                showText = "暂停播放";
            }
            pendingIntent = getStartIntent(studyActivityClz);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(String.valueOf(NOTIFICATION_ID),NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setDescription(Constant.APPName+"的通知消息");
            channel.setSound(null,null);
            manager.createNotificationChannel(channel);

            builder = new Notification.Builder(this,String.valueOf(NOTIFICATION_ID));
        }else {
            builder = new Notification.Builder(this);
        }

        builder.setOngoing(true);
        builder.setContentTitle(title);
        builder.setContentText(showText);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.familyalbum_icon);
        builder.setTicker(showText);
        if (!isInit){
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"关闭",getPlayCloseIntent(false));
        }
        builder.build();
        startForeground(NOTIFICATION_ID,builder.build());
    }

    //跳转界面
    private PendingIntent getStartIntent(Class clz){
        Intent intent = new Intent(this,clz);
        return PendingIntent.getActivity(this, 0,intent,getFlag());
    }

    //获取flag
    private int getFlag(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            return PendingIntent.FLAG_IMMUTABLE;
        }else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }

    /********************接收器****************/
    private static final String action_close = "action_close";

    private TravelBgPlayCloseReceiver closeReceiver;

    private PendingIntent getPlayCloseIntent(boolean isPlay){
        String playStatus = TravelBgPlayEvent.event_audio_pause;
        if (isPlay){
            playStatus = TravelBgPlayEvent.event_audio_play;
        }

        Intent intent = new Intent(action_close);
        intent.putExtra(TravelBgPlayData.playStatus,playStatus);
        return PendingIntent.getBroadcast(this,0,intent,getFlag());
    }

    //注册接收器
    private void registerPlayReceiver(){
        IntentFilter closeFilter = new IntentFilter(action_close);
        closeReceiver = new TravelBgPlayCloseReceiver();
        registerReceiver(closeReceiver,closeFilter);
    }

    //解除接收器
    private void unregisterPlayReceiver(){
        try {
            unregisterReceiver(closeReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
