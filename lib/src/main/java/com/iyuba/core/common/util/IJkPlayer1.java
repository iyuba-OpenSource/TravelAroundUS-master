//package com.iyuba.core.common.util;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.os.Handler;
//import android.os.Message;
//import android.widget.SeekBar;
//
//import com.facebook.stetho.common.LogUtil;
//
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//
//
//public class IJkPlayer1 implements IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener,
//        IMediaPlayer.OnPreparedListener {
//    public IjkMediaPlayer ijkPlayer;
//    private SeekBar skbProgress;
//    private Context mContext;
//    private Timer mTimer = new Timer();
//    private OnIJKPlayStateChangedListener opscl;
//    private String audioUrl;
//
//
//    public static void initNative() {
//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//    }
//
//    public static void endNative() {
//        IjkMediaPlayer.native_profileEnd();
//    }
//
//    public OnIJKPlayStateChangedListener getOpscl() {
//        return opscl;
//    }
//
//    public void setOpscl(OnIJKPlayStateChangedListener opscl) {
//        this.opscl = opscl;
//    }
//
//    public IJkPlayer1(Context context) {
//        this.mContext = context;
//        try {
//            ijkPlayer = new IjkMediaPlayer();
//            ijkPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            ijkPlayer.setOnBufferingUpdateListener(this);
//            ijkPlayer.setOnPreparedListener(this);
//            ijkPlayer.setOnCompletionListener(this);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public IJkPlayer1(Context context, OnIJKPlayStateChangedListener opscl, SeekBar skbProgress) {
//        this.skbProgress = skbProgress;
//        skbProgress.setEnabled(false);
//        this.mContext = context;
//        this.opscl = opscl;
//        try {
//            ijkPlayer = new IjkMediaPlayer();
//            ijkPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            ijkPlayer.setOnBufferingUpdateListener(this);
//            ijkPlayer.setOnPreparedListener(this);
//            ijkPlayer.setOnCompletionListener(this);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Log.e("ijkPlayer", "error", e);
//        }
//
//        // mTimer.schedule(mTimerTask, 0, 1000);
//        // mTimer.cancel();
//    }
//
//    /*******************************************************
//     * 通过定时器和Handler来更新进度条
//     ******************************************************/
//    TimerTask mTimerTask = new TimerTask() {
//        @Override
//        public void run() {
//            // Log.e("dingshiqi", "!!!!!!!!!!!");
//            try {
//                if (ijkPlayer == null) return;
//                if (ijkPlayer.isPlaying() && skbProgress.isPressed() == false) {
//                    handleProgress.sendEmptyMessage(0);
//                }
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
//    };
//
//    Handler handleProgress = new Handler() {
//        public void handleMessage(Message msg) {
//            if (ijkPlayer != null && ijkPlayer.isPlaying()) {
//                long position = ijkPlayer.getCurrentPosition();
//                long duration = ijkPlayer.getDuration();
//                if (duration > 0) {
//                    //��ȡ��ǰseekbar��Ӧ��λ��
//                    long pos = skbProgress.getMax() * position / duration;
//                    skbProgress.setProgress((int) pos);
//                }
//
//                if (opscl != null) {
//                    opscl.setPlayTime(getAudioCurrTime(), getAudioAllTime());
//                }
//            }
//        }
//
//        ;
//    };
//
//    /*
//     * ���ֲ������ĸ��ֲ���
//     *
//     *///������Ƶʱ���ǰ���ʱ��
//    public static final int SEEK_NEXT = 5000;
//
//    public void play() {
//        ijkPlayer.start();
//        if (opscl != null) {
//            opscl.playResume();
//        }
//    }
//
//    public void playUrl(final String videoUrl) {
//        this.audioUrl = videoUrl;
//        handler.sendEmptyMessage(1);
//    }
//
//    public void playAnother(final String videoUrl) {
//        if (ijkPlayer != null) {
//            ijkPlayer.reset();
//            try {
//                ijkPlayer.setDataSource(videoUrl);
//                ijkPlayer.prepareAsync();
//                play();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//
//    public void pause() {
//        ijkPlayer.pause();
//        if (opscl != null) {
//            opscl.playPause();
//        }
//
//    }
//
//    public void stop() {
//        if (ijkPlayer != null) {
//            ijkPlayer.stop();
//            ijkPlayer.release();
//            ijkPlayer = null;
//            LogUtil.e("ijkPlayer", "为空");
//        }
//        if (skbProgress != null) {
//            mTimer.cancel();
//            mTimerTask.cancel();
//            skbProgress.setEnabled(false);
//            skbProgress = null;
//            handleProgress.removeMessages(0);
//            handleProgress.removeCallbacksAndMessages(null);
//            handler.removeCallbacksAndMessages(null);
//            LogUtil.e("skbProgress", "为空");
//        }
//        if (opscl != null) {
//            opscl.playStop();
//            opscl = null;
//            LogUtil.e("opscl", "为空");
//        }
//        audioUrl = null;
//    }
//
//
//    public void nextSpeed() {
//        long seekNextTime = getCurrentPosition() + 5000;
//        if (seekNextTime > getDur()) {
//            seekNextTime = getDur();
//        }
//        seekTo(seekNextTime);
//
//    }
//
//    public void preSpeed() {
//        long seekPreTime = getCurrentPosition() + 5000;
//        if (seekPreTime < 0) {
//            seekPreTime = 0;
//        }
//        seekTo(seekPreTime);
//    }
//
//    public boolean isPlaying() {
//        if (ijkPlayer == null) {
//            return false;
//        }
//        return ijkPlayer.isPlaying();
//    }
//
//    public long getDur() {
//        if (ijkPlayer != null) {
//            return ijkPlayer.getDuration();
//        }
//        return 0;
//    }
//
//    public long getCurrentPosition() {
//        if (ijkPlayer != null) {
//            return ijkPlayer.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    private static final String TAG = "Player";
//
//    public void seekTo(long msec) {
//        LogUtil.e(TAG, "msec  :  " + msec);
//        if (ijkPlayer != null && !ijkPlayer.isPlaying()) {
//            LogUtil.e(TAG, "play");
//            play();
//        } else if (ijkPlayer != null) {
//            LogUtil.e(TAG, "seekto:    ");
//            ijkPlayer.seekTo(msec);
//        }
//    }
//
//
//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    try {
//                        if (ijkPlayer.isPlaying()) {
//                            if (opscl != null) {
//                                opscl.playFaild();
//                            }
//                        }
//                        ijkPlayer.reset();
//                        ijkPlayer.setDataSource(audioUrl);
//                        new Thread() {
//
//                            @Override
//                            public void run() {
//
//                                super.run();
//                                try {
//                                    ijkPlayer.prepareAsync();
//                                    if (opscl != null) {
//                                        opscl.setPlayTime(getAudioCurrTime(), getAudioAllTime());
//                                    }
//                                    if (skbProgress != null) {
//                                        handler.sendEmptyMessage(2);
//                                    }
//                                    // }
//                                } catch (Exception e) {
//
//                                    e.printStackTrace();
//                                }
//                            }
//                        }.start();
//
//                    } catch (IllegalArgumentException e) {
//
//                        if (opscl != null) {
//                            opscl.playFaild();
//                        }
//                        e.printStackTrace();
//                    } catch (IllegalStateException e) {
//
//                        if (opscl != null) {
//                            opscl.playFaild();
//                        }
//                        e.printStackTrace();
//                    } catch (IOException e) {
//
//                        if (opscl != null) {
//                            opscl.playFaild();
//                        }
//                        e.printStackTrace();
//                    }
//                    break;
//                case 2:
//                    mTimer.cancel();
//                    mTimer = new Timer();
//                    if (mTimerTask != null) {
//                        mTimerTask.cancel();
//                    }
//                    mTimerTask = new TimerTask() {
//
//                        @Override
//                        public void run() {
//                            // Log.e("dingshiqi", "!!!!!!!!!!!");
//                            try {
//                                if (ijkPlayer == null) return;
//                                if (ijkPlayer.isPlaying() && skbProgress.isPressed() == false) {
//                                    handleProgress.sendEmptyMessage(0);
//                                }
//                            } catch (Exception e) {
//
//                                e.printStackTrace();
//                            }
//                        }
//
//                    };
//                    mTimer.schedule(mTimerTask, 0, 1000);
//                    skbProgress.setEnabled(true);
//                    break;
//            }
//        }
//    };
//
//    public void release() {
//
//    }
//
//    /**
//     * 获取音频总长
//     *
//     * @return
//     */
//    public String getAudioAllTime() {
//
//        LogUtil.e("获取音频总长=======", ijkPlayer.getDuration() + "");
//        StringBuffer timeBuffer = new StringBuffer("");
//        if (ijkPlayer != null) {
//            long musicTime = ijkPlayer.getDuration() / 1000;// �?
//            String minit = "00";// �?
//            String second = "00";// �?
//            if ((musicTime / 60) < 10)// �?
//            {
//                minit = "0" + String.valueOf(musicTime / 60);
//                // timeBuffer.append("0").append(musicTime / 60).append(":")
//                // .append(musicTime % 60);
//            } else {
//                minit = String.valueOf(musicTime / 60);
//            }
//            if ((musicTime % 60) < 10)// �?
//            {
//                second = "0" + String.valueOf(musicTime % 60);
//            } else {
//                second = String.valueOf(musicTime % 60);
//            }
//            timeBuffer.append(minit).append(":").append(second);
//
//        }
//        return timeBuffer.toString();
//    }
//
//    /**
//     * 获取音频当前播放进度时间
//     *
//     * @return
//     */
//    public String getAudioCurrTime() {
//        StringBuffer timeBuffer = new StringBuffer("");
//        if (ijkPlayer != null) {
//            long musicTime = ijkPlayer.getCurrentPosition() / 1000;
//            String minit = "00";// �?
//            String second = "00";// �?
//            if ((musicTime / 60) < 10)// �?
//            {
//                minit = "0" + String.valueOf(musicTime / 60);
//                // timeBuffer.append("0").append(musicTime / 60).append(":")
//                // .append(musicTime % 60);
//            } else {
//                minit = String.valueOf(musicTime / 60);
//            }
//            if ((musicTime % 60) < 10)// �?
//            {
//                second = "0" + String.valueOf(musicTime % 60);
//            } else {
//                second = String.valueOf(musicTime % 60);
//            }
//            timeBuffer.append(minit).append(":").append(second);
//        }
//        return timeBuffer.toString();
//    }
//
//    /**
//     * 设置循环播放----
//     */
//    public void resetPlay() {
//
//        ijkPlayer.start();
////        ijkPlayer.setLooping(true);
//    }
//
//    /**
//     * 设置播放速度
//     */
//    public void setSpeed(float speed) {
//        ijkPlayer.setSpeed(speed);
//
//    }
//
//    /**
//     * 设置播放速度
//     */
//    public float getSpeed() {
//        return  ijkPlayer.getSpeed(0.5f);
//    }
//
//
//    @Override
//    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
//        if (skbProgress != null) {
//            skbProgress.setSecondaryProgress(percent);
//        }
//    }
//
//    @Override
//    public void onCompletion(IMediaPlayer mp) {
//        if (skbProgress != null) {
//            skbProgress.setProgress(0);
//        }
//        if (ijkPlayer != null && opscl != null) {
//            opscl.playCompletion();
//
//        }
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer mp) {
//        mp.start();
//        if (opscl != null) {
//            opscl.playSuccess();
//        }
//    }
//}
//
