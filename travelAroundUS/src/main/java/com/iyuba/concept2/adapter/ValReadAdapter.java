package com.iyuba.concept2.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.common.LogUtil;
import com.hjq.permissions.XXPermissions;
import com.iyuba.concept2.R;
import com.iyuba.concept2.activity.StudyActivity;
import com.iyuba.concept2.fragment.EvalCorrectPage;
import com.iyuba.concept2.listener.RequestCallBack;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.protocol.AddCreditsRequest;
import com.iyuba.concept2.protocol.DataCollectRequest;
import com.iyuba.concept2.protocol.DataCollectResponse;
import com.iyuba.concept2.sqlite.mode.ReadVoiceComment;
import com.iyuba.concept2.sqlite.mode.Shareable;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.mode.VoaDetail;
import com.iyuba.concept2.sqlite.mode.VoaSound;
import com.iyuba.concept2.sqlite.op.VoaSoundOp;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.concept2.util.Player;
import com.iyuba.concept2.util.UtilPostFile;
import com.iyuba.concept2.widget.DictionEditText;
import com.iyuba.concept2.widget.RoundProgressBar;
import com.iyuba.concept2.widget.cdialog.CustomToast;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.DensityUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibPermissionDialogUtil;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
import com.iyuba.core.search.bean.EvaluationBeanNew;
import com.iyuba.core.search.util.ReadEvaluateManager;
import com.iyuba.core.search.util.ResultParse;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.play.ExtendedPlayer;

import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.drakeet.materialdialog.MaterialDialog;

public class ValReadAdapter extends BaseAdapter {

    private static final String TAG = ValReadAdapter.class.getSimpleName();
    private List<VoaDetail> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    static public int clickPosition = -1;
    private int senIndex;
    private VoaDetail clickDetail;
    static private ViewHolder clickViewHolder;
    private double time;
    private double playTime;
    public MediaPlayer videoView = null;
    private int currParagraph = 1;
    private int lastParegraph = 0;
    private Voa voaTemp;
    //private IseManager iseManager;
    private Player mPlayer;
    private VoaDetail tempDetail = new VoaDetail();
    private boolean isUploadVoice = false;
    private CustomDialog waittingDialog;
    //private com.iyuba.concept2.widget.cdialog.CustomDialog soundDialog;
    private HashMap<Integer, ReadVoiceComment> mMap;
    private LinkedHashMap<String, String> unknownWord;
    private SpannableStringBuilder style[];
    private SpannableStringBuilder tip;
    private SpannableStringBuilder words;
    private int mHeaderViewHeight;
    private static String beginTime; // 听力开始学习时间(听力和语音测试都用的这两个变量记录时间）
    private static String endTime;// 听力开始学习结束时间
    //private Result rs;
    private static Bundle bundle = new Bundle();
    private ExtendedPlayer ep;
    private BackPlayer bp;
    private boolean isChange = true;
    public ReadEvaluateManager manager;
    //替换类型
//    private EvaluatBean evaluatBean;
    private EvaluationBeanNew evaluatBean;
    private String shuoshuoId = "";

    //错误单词分数标准线
    private double errorWordScore = com.iyuba.concept2.util.ResultParse.errorWordScore;
    //正确单词分数标准线
    private double rightWordScore = com.iyuba.concept2.util.ResultParse.rightWordScore;

    //这里在录音评测的时候会出现一个问题
    //问题1：录音的时候你选择条目1，然后点击停止录音后，快速点击另一个条目的目录，返回的结果就会显示在另一个条目上
    //问题2：在录音时未处理其他操作的问题
    public boolean isEvaluating = false;

    public void setOnClickWordListener(OnClickWordListener onClickWordListener) {
        this.onClickWordListener = onClickWordListener;
    }

    //判断是否正在录音
    public boolean isRecording(){
        if (manager!=null&&manager.isRecording){
            return true;
        }
        return false;
    }

    private OnClickWordListener onClickWordListener;

    private VoaSoundOp voaSoundOp;
    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {
            // TODO Auto-generated method stub

        }
    };


    public ValReadAdapter(Context mContext, List<VoaDetail> mList, Voa voaTemp,
                          MediaPlayer videoView, ExtendedPlayer ep, BackPlayer bp,
                          ValReadAdapterInteraction valReadAdapterInteraction) {
        this.mContext = mContext;
        this.mList = mList;
        this.voaTemp = voaTemp;
        if (mList.size() > 0)
            this.clickDetail = mList.get(0);
        this.ep = ep;
        this.bp = bp;
        this.valReadAdapterInteraction = valReadAdapterInteraction;
        waittingDialog = WaittingDialog.showDialog(mContext);

        //soundDialog = SoundDialog.showDialog(mContext, this, "录音中");
        mMap = new HashMap<Integer, ReadVoiceComment>();
        unknownWord = new LinkedHashMap<String, String>();
        mInflater = LayoutInflater.from(mContext);

        voaSoundOp = new VoaSoundOp(mContext);
        //iseManager = IseManager.getInstance(mContext, handler);//评测管理
        manager = ReadEvaluateManager.getInstance(mContext);
        // url = Constant.sound + voaTemp.voaId / 1000 + "_" + voaTemp.voaId
        // % 1000 + Constant.append;
        // netType = NetWorkState.getAPNType();
        this.videoView = videoView;
        tempDetail = mList.get(currParagraph - 1);
        mPlayer = new Player(mContext, null);
        style = new SpannableStringBuilder[mList.size()];
        for (int i = 0; i < mList.size(); i++) {
            // allEn[i] = mList.get(i).sentence;
            style[i] = new SpannableStringBuilder(mList.get(i).sentence);
        }
        tip = new SpannableStringBuilder("请先听原文，然后选择未听懂的单词");
        tip.setSpan(new ForegroundColorSpan(0xff639cfe), 0, tip.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void setAdapter(List<VoaDetail> mList, Voa voaTemp) {
        this.mList.clear();
        this.mList.addAll(mList);

        style = new SpannableStringBuilder[mList.size()];
        for (int i = 0; i < mList.size(); i++) {
            // allEn[i] = mList.get(i).sentence;
            style[i] = new SpannableStringBuilder(mList.get(i).sentence);
        }
        this.voaTemp = voaTemp;
        notifyDataSetChanged();
    }

    //加载下20条数据
    public void addDate(List<VoaDetail> list, Voa voaTemp) {
        mList.addAll(list);
        style = new SpannableStringBuilder[mList.size()];

        for (int i = 0; i < mList.size(); i++) {
            // allEn[i] = mList.get(i).sentence;
            style[i] = new SpannableStringBuilder(mList.get(i).sentence);
        }
//        this.mList = mList;
        this.voaTemp = voaTemp;
        notifyDataSetChanged();
    }

    //初始化数据为10条，做了避免多次初始化处理
    public boolean defaultData(Voa voaTemp, boolean change) {
        if (!change) {
            isChange = true;
        }
        if (isChange && change) {
            mList = mList.subList(0, 10);
            style = new SpannableStringBuilder[mList.size()];

            for (int i = 0; i < mList.size(); i++) {
                style[i] = new SpannableStringBuilder(mList.get(i).sentence);
            }
            this.voaTemp = voaTemp;
            notifyDataSetChanged();
            isChange = false;
            return true;
        }
        return false;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /**
     * 反转字符串
     *
     * @param s
     * @return
     */
    public String reverseStr(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    /**
     * 字符串找词的index
     */
    public int findWord(String sub, SpannableStringBuilder ssb, int index,
                        int start) {
        int i = 0;
        for (int j = start; j < ssb.length(); j++) {
            i = ssb.toString().indexOf(sub, j);
            if (i + sub.length() < index + start)
                j = i + sub.length();
            else
                break;
        }
        return i;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final VoaDetail curDetail = mList.get(position);
        // String[] allEn = new String [mList.size()];
        final int curPosition = position;
        ValReadAdapter.ViewHolder showHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_read, null);

            showHolder = new ViewHolder();
            showHolder.senIndex = (TextView) convertView
                    .findViewById(R.id.sen_index);
            showHolder.senEn = convertView
                    .findViewById(R.id.sen_en);
            showHolder.senZh = (TextView) convertView
                    .findViewById(R.id.sen_zh);
            showHolder.wordChosn = (TextView) convertView
                    .findViewById(R.id.chosn_word);
            showHolder.senPlay = (RoundProgressBar) convertView
                    .findViewById(R.id.sen_play);
            showHolder.senIRead = (RoundProgressBar) convertView
                    .findViewById(R.id.sen_i_read);
            showHolder.senReadPlayButton = convertView
                    .findViewById(R.id.sen_sound_button);
            showHolder.senReadPlay = (ImageView) convertView
                    .findViewById(R.id.sen_read_play);
            showHolder.senReadPlaying = (RoundProgressBar) convertView
                    .findViewById(R.id.sen_read_playing);
            showHolder.senReadSend = (ImageView) convertView
                    .findViewById(R.id.sen_read_send);
            showHolder.senReadCollect = (ImageView) convertView
                    .findViewById(R.id.sen_read_collect);
            // curViewHolder.senReadCollect.setVisibility(View.GONE);
            showHolder.senReadResult = (TextView) convertView
                    .findViewById(R.id.sen_read_result);
            showHolder.sepLine = (ImageView) convertView
                    .findViewById(R.id.sep_line);
            showHolder.bottomView = convertView
                    .findViewById(R.id.bottom_view);
            showHolder.frontView = convertView.findViewById(R.id.front_view);
            showHolder.wordCommit = (Button) convertView
                    .findViewById(R.id.word_commit);

            //纠音
            showHolder.llWordCorrect = convertView.findViewById(R.id.word_correct);
            showHolder.llWordCorrect.setVisibility(View.GONE);
            showHolder.tvWordCorrect = convertView.findViewById(R.id.word_correct_msg);
            showHolder.btnWordCorrect = convertView.findViewById(R.id.word_correct_commit);

            convertView.setTag(showHolder);
        } else {
            showHolder = (ViewHolder) convertView.getTag();
        }

        final VoaSound voaSound = voaSoundOp.findDataById(Integer.parseInt(curDetail.voaId + "" + position));

        if (voaSound != null) {
            String filepath2 = voaSound.evaluateWebPath;
            if (filepath2 != null && !filepath2.equals("")) {
                String[] floats = voaSound.wordScore.split(",");//分数列表
                curDetail.setReadScore(voaSound.totalScore);
                curDetail.readResult = ResultParse.getSenResultLocal(floats, curDetail.sentence);
                curDetail.isRead = true;
                //将数据加入到合成列表中
                valReadAdapterInteraction.getIndex(position, voaSound.totalScore, Long.parseLong(voaSound.time), voaSound.evaluateWebPath);
            }

        }
        showHolder.senIndex.setText((position + 1) + "");

        if (curDetail.isListen && curDetail.isRead == false) {
            words = new SpannableStringBuilder(curDetail.chosnWords);
            words.setSpan(new ForegroundColorSpan(0xffff0000), 0,
                    words.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //curViewHolder.senZh.setVisibility(View.INVISIBLE);
            //curViewHolder.wordChosn.setVisibility(View.VISIBLE);
            if (curDetail.isCommit == false) {
                // curViewHolder.wordCommit.setVisibility(View.VISIBLE);
            } else
                showHolder.wordCommit.setVisibility(View.INVISIBLE);
        } else {
            showHolder.senZh.setVisibility(View.VISIBLE);
            showHolder.wordChosn.setVisibility(View.INVISIBLE);
            showHolder.wordCommit.setVisibility(View.INVISIBLE);
        }

        showHolder.senZh.setText(curDetail.sentenceCn);
        // curViewHolder.senEn.setText(curDetail.sentence);
        showHolder.senEn.setText(style[position]);
        showHolder.bottomView.getParent().requestDisallowInterceptTouchEvent(true);

        if (curDetail.isRead) {
            if (curDetail.readResult == null) {
                showHolder.senEn.setText("readResult不存在");
            } else {
                showHolder.senEn.setText(curDetail.readResult);
            }
            showHolder.senZh.setText(curDetail.sentenceCn);
            setReadScoreViewContent(showHolder, curDetail.getReadScore());//得分
        } else {
            // curViewHolder.senEn.setText(curDetail.sentence);
            if (curDetail.isListen) {
                showHolder.senEn.setText(style[position]);
            } else if (SettingConfig.Instance().isSpcCn()) {
                showHolder.senZh.setText(tip);
            } else {
                showHolder.senEn.setText(style[position]);
            }
        }

        if (curPosition == clickPosition) {
            clickViewHolder = showHolder;
            clickDetail = curDetail;
        }

        // curViewHolder.senReadCollect.setVisibility(View.GONE);
        if (mMap.containsKey(curPosition)) {
            showHolder.senReadCollect.setVisibility(View.VISIBLE);
        } else {
            showHolder.senReadCollect.setVisibility(View.GONE);
        }

        //Log.d("mdg", "it is my dog,hahahaha");
        if (curPosition != clickPosition) {
            showHolder.sepLine.setVisibility(View.GONE);
            showHolder.bottomView.setVisibility(View.GONE);
        } else {
            showHolder.sepLine.setVisibility(View.VISIBLE);
            showHolder.bottomView.setVisibility(View.VISIBLE);
            if (curDetail.isRead) {
                showHolder.senReadPlayButton.setVisibility(View.VISIBLE);
                showHolder.senReadSend.setVisibility(View.VISIBLE);
                showHolder.senReadResult.setVisibility(View.VISIBLE);
            } else {
                showHolder.senReadPlayButton.setVisibility(View.INVISIBLE);
                showHolder.senReadSend.setVisibility(View.INVISIBLE);
                showHolder.senReadResult.setVisibility(View.INVISIBLE);
            }
        }

        if (clickDetail.readResult != null) {
            clickDetail.readResult.toString();
        }

        //纠音显示和隐藏
        if (curPosition == clickPosition) {
            if (voaSound != null && !TextUtils.isEmpty(voaSound.words)) {
                EvaluationBeanNew evaluation = GsonUtils.toObject(voaSound.words, EvaluationBeanNew.class);
                showCorrectionView(evaluation.getWords());
            } else {
                showHolder.llWordCorrect.setVisibility(View.GONE);
            }
        } else {
            showHolder.llWordCorrect.setVisibility(View.GONE);
        }

        // 提交按钮 。未听懂的单词  功能取消,按钮被隐藏
        showHolder.wordCommit.setOnClickListener(arg0 -> {
            if (onEvalClickListener!=null){
                onEvalClickListener.onStopPlay();
            }

            VoaDetail en = mList.get(clickPosition);
            int length;
            String[] split = en.sentence.split(" ");
            String userWord = en.chosnWords;
            userWord = userWord.replace(" ", ",");
            String[] splitt = en.chosnWords.split(" ");

            if ("".equals(en.chosnWords))
                length = 0;
            else
                length = splitt.length;

            int score = 100 - length * 100 / split.length;

            Message msg = new Message();
            msg.what = 1;
            bundle.putString("endFlag", "1");
            bundle.putString("lessonId", String.valueOf(voaTemp.voaId));
            bundle.putString("lesson", voaTemp.title);
            bundle.putString("testNumber", String.valueOf(currParagraph));
            bundle.putString("testWords", String.valueOf(split.length));
            bundle.putString("testMode", "1");
            bundle.putString("userAnswer", userWord);
            bundle.putString("score", String.valueOf(score));
            msg.setData(bundle);

            if (UserInfoManager.getInstance().isLogin()) {
                if (userWord.equals(""))
                    Toast.makeText(mContext, "您没有选择任何单词", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(mContext, "正在上传...", Toast.LENGTH_SHORT).show();
                    videoHandle.sendMessage(msg); // 向服务器发送数据 .发送数据
                }
            } else {
                LoginUtil.startToLogin(mContext);
            }
        });
        //播放按钮，每一句播放，地址 ,没有地址，手动切割时间？
        showHolder.senPlay.setOnClickListener(view -> {
            if (manager.isRecording || isEvaluating) {
                ToastUtil.showToast(mContext, "正在评测中");
                return;
            }

            int endtime;
            int currentPosition = videoView.getCurrentPosition();//播放的进度
            if (currParagraph == mList.size()) {
                endtime = videoView.getDuration();
            } else {
                endtime = (int) mList.get(currParagraph).startTime * 1000;
            }

            if (manager.isRecording) {//语音识别
                manager.cancelEvaluate(true);
                handler.sendEmptyMessage(3);
            }

            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
            if (ep != null && ep.isPlaying()) {
                ep.pause();
            }
            if (bp != null && bp.isPlaying()) {
                bp.pause();
            }
            if (videoView.isPlaying()) {
                //如果videoView
                videoView.pause();
                handler.removeMessages(0);
                //这里延迟100毫秒操作
//                handler.sendEmptyMessageDelayed(0,100);
                clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
                time = 0;
                clickViewHolder.senPlay.setProgress(0);
                updateReadThread.run();
            } else if (currentPosition > tempDetail.startTime * 1000.0
                    && currentPosition < endtime) {
                clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_stop);
                handler.sendEmptyMessage(0);

                //跳转到指定位置播放
                videoView.seekTo((int) (tempDetail.startTime * 1000L));
                videoView.start();
            } else {
                setReadTime(position);//重新播放
            }

            if (currParagraph - lastParegraph == 2) {
                currParagraph--;
            }

        });
        //测评按钮
        showHolder.senIRead.setOnClickListener(view -> {
            if (manager.isRecording) {
                handler.sendEmptyMessage(11);//按钮变色
                //manager.stopEvaluating();
                LogUtil.e("录音，手动停止录音");
                return;
            }

            if (!NetWorkState.isConnectingToInternet()) {
                CustomToast.showToast(mContext, R.string.alert_net_content, 1000);
                return;
            }

            if (onEvalClickListener!=null){
                onEvalClickListener.onStopPlay();
            }

            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
            if (ep != null && ep.isPlaying()) {
                ep.pause();
            }
            if (bp != null && bp.isPlaying()) {
                bp.pause();
            }

            if (videoView.isPlaying()) {
                videoView.pause();
                handler.removeMessages(0);
                handler.sendEmptyMessage(1);
            }

//            ((StudyActivity) mContext).checkStudyPermission();
//            LogUtil.e("录音，开始录音");
//            if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})) {
//                return;
//            }

            //权限弹窗显示
            if (!XXPermissions.isGranted(mContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO, new Pair<>("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")));
                pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "保存评测的音频文件，用于评测打分使用")));

                LibPermissionDialogUtil.getInstance().showMsgDialog(mContext, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
                    @Override
                    public void onGranted(boolean isSuccess) {
                        if (isSuccess) {
                            ToastUtil.showToast(mContext, "已获取授权，请开始录音评测");
                        }
                    }
                });
                return;
            }

            //登录判断
            if (!UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(mContext);
                return;
            }

            ArrayList<VoaSound> voaSoundArrayList = voaSoundOp.findDataByvoaId(tempDetail.voaId);

            if (!curDetail.isRead && voaSoundArrayList.size() >= 5) {
                if (!UserInfoManager.getInstance().isVip()) {
                    final MaterialDialog materialDialog = new MaterialDialog(mContext);
                    materialDialog.setTitle("提醒");
                    materialDialog.setMessage("本篇你已评测5句！成为vip后可评测更多");

                    materialDialog.setPositiveButton("确定", view12 -> {
                        Intent intent = new Intent(mContext, VipCenterGoldActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        materialDialog.dismiss();
                    });

                    materialDialog.setNegativeButton("取消", view1 -> materialDialog.dismiss());
                    materialDialog.show();
                    return;
                }
            }

            if (!manager.isRecording) {//语音识别
                if (clickDetail.sentence.length() > 200)
                    CustomToast.showToast(mContext, "句子过长，暂时不支持语音评测哦~",
                            3000);
                else {
                    senIndex = clickPosition;
                    //设置按钮样式
                    clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_stop);
                    if (clickPosition == 0) {
                        notifyDataSetChanged();
                    }
                    if (!manager.isRecording) {
                        try {
                            //新的句子评测接口所需要的数据
                            Map<String, String> textParams = new HashMap<String, String>();
                            textParams.put("type", "familyalbum");
                            textParams.put("userId", String.valueOf(UserInfoManager.getInstance().getUserId()));
                            textParams.put("newsId", clickDetail.voaId + "");
                            textParams.put("paraId", clickDetail.paraId + "");
                            textParams.put("IdIndex", clickDetail.lineN);
                            String sentence = URLEncoder.encode(clickDetail.sentence, "UTF-8").replace("+", "%20");
                            textParams.put("sentence", sentence);
                            textParams.put("appId", Constant.APPID);
                            textParams.put("flg", "0");
                            textParams.put("wordId", "0");

                            manager.startEvaluate(clickPosition, clickDetail.sentence, clickDetail.voaId, textParams, handler);
                            //handler.sendEmptyMessage(104);
                            int time = (int) (clickDetail.timing * 2 * 1000);
                            LogUtil.e("录音 持续时间：" + time + "原始时间" + clickDetail.timing);
                            if (time < 8000) {
                                time = 8000;
                                LogUtil.e("录音 持续时间修改为" + time);
                            }
                            handler.sendEmptyMessage(9);//录音中
                            handler.sendEmptyMessageDelayed(11, time);//延时2倍关闭·
                        } catch (ParseException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        manager.stopEvaluate();
                    }

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
                    beginTime = df.format(new Date());// new Date()为获取当前系统时间
                }
            } else {
                manager.cancelEvaluate(true);
                clickViewHolder.senIRead
                        .setBackgroundResource(R.drawable.sen_i_read);
                handler.sendEmptyMessageDelayed(7, 100);
                handler.sendEmptyMessage(3);
            }
        });
        //播放录音
        showHolder.senReadPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manager.isRecording || isEvaluating) {
                    ToastUtil.showToast(mContext, "正在评测中");
                    return;
                }

                if (onEvalClickListener!=null){
                    onEvalClickListener.onStopPlay();
                }

                if (videoView.isPlaying()) {
                    videoView.pause();
                    handler.removeMessages(0);
                    handler.sendEmptyMessage(1);
                }

                if (manager.isRecording) {
                    manager.stopEvaluating();
                    handler.sendEmptyMessage(3);
                }

                if (isStoppedAndCouldPlay()) {
                    clickViewHolder.senReadPlay.setVisibility(View.GONE);
                    clickViewHolder.senReadPlaying.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    playRecord2(Integer.parseInt(voaTemp.voaId + "" + clickPosition));
                    Log.e("音频", mPlayer.getDuration() + "");
                    handler.sendEmptyMessage(4);
                } else if (mPlayer.isPlaying()) {
                    clickViewHolder.senReadPlaying.setVisibility(View.GONE);
                    clickViewHolder.senReadPlay.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    mPlayer.pause();
                    handler.removeMessages(4);
                } else if (mPlayer.isPausing()) {
                    clickViewHolder.senReadPlay.setVisibility(View.GONE);
                    clickViewHolder.senReadPlaying.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    mPlayer.restart();
                    handler.sendEmptyMessage(4);
                }
            }
        });
        //分享按钮
        showHolder.senReadCollect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manager.isRecording || isEvaluating) {
                    ToastUtil.showToast(mContext, "正在评测中");
                    return;
                }

                if (onEvalClickListener!=null){
                    onEvalClickListener.onStopPlay();
                }

                if (!mMap.containsKey(curPosition))
                    return;
                PlatformActionListener pal = new PlatformActionListener() {

                    @Override
                    public void onError(Platform platform, int arg1,
                                        Throwable arg2) {
                        CustomToast.showToast(mContext, "分享失败", 1000);
                    }

                    @Override
                    public void onComplete(Platform platform, int arg1,
                                           HashMap<String, Object> arg2) {
                        int srid = 46;
                        String name = platform.getName();
                        if (name.equals(QQ.NAME)
                                || name.equals(Wechat.NAME)
                                || name.equals(WechatFavorite.NAME)) {
                            srid = 45;
                        } else if (name.equals(QZone.NAME)
                                || name.equals(WechatMoments.NAME)
                                || name.equals(SinaWeibo.NAME)) {
                            srid = 46;
                        }
                        if (UserInfoManager.getInstance().isLogin()) {
                            RequestCallBack rc = new RequestCallBack() {

                                @Override
                                public void requestResult(Request result) {
                                    AddCreditsRequest rq = (AddCreditsRequest) result;
                                    if (rq.isShareFirstlySuccess()) {
                                        String msg = "分享成功，增加了"
                                                + rq.addCredit + "积分，共有"
                                                + rq.totalCredit + "积分";
                                        CustomToast.showToast(mContext,
                                                msg, 3000);
                                    } else if (rq.isShareRepeatlySuccess()) {
                                        CustomToast.showToast(mContext,
                                                "分享成功", 3000);
                                    }
                                }
                            };
                            int uid = UserInfoManager.getInstance().getUserId();
                            AddCreditsRequest rq = new AddCreditsRequest(
                                    uid,
                                    mMap.get(curPosition).getVoaRef().voaId,
                                    srid, rc);
                            RequestQueue queue = Volley
                                    .newRequestQueue(mContext);
                            queue.add(rq);
                        }
                    }

                    @Override
                    public void onCancel(Platform platform, int arg1) {
                        CustomToast.showToast(mContext, "分享已取消", 1000);
                    }
                };
                Shareable stuff = mMap.get(curPosition);
                String ArticleShareUrl = "http://voa." + Constant.IYBHttpHead() + "/voa/play.jsp?id=" + shuoshuoId
                        + "&apptype=" + Constant.TOPICID + "&addr=" + voaSound.evaluateWebPath;

                //stuff.getArticleShareUrl()
                showShare(mContext, stuff.getShareTitle(), stuff.getShareShortText(), ArticleShareUrl,
                        stuff.getShareImageUrl(), "很不错的应用，大家快来使用呀！", Constant.APPName,
                        ArticleShareUrl, pal);
                LogUtil.e("stuff" + stuff.getShareTitle());
                LogUtil.e("stuff" + stuff.getShareShortText());
                LogUtil.e("stuff" + stuff.getArticleShareUrl());
                LogUtil.e("stuff" + stuff.getShareImageUrl());
            }
        });
        //发布单句按钮
        showHolder.senReadSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onEvalClickListener!=null){
                    onEvalClickListener.onStopPlay();
                }

                if (UserInfoManager.getInstance().isLogin()) {
                    if (manager.isRecording || isEvaluating) {
                        ToastUtil.showToast(mContext, "正在评测中");
                        return;
                    }

                    if (isUploadVoice) {
                        CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
                    } else {
                        waittingDialog.show();
                        Thread threadsend = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    isUploadVoice = true;
                                    String response = "";
                                    if (voaSound == null || voaSound.evaluateWebPath == null || voaSound.evaluateWebPath.equals("")) {
                                        //evaluatBean.getPosition() != position
                                        Map<String, String> textParams = new HashMap<String, String>();
                                        Map<String, File> fileParams = new HashMap<String, File>();
                                        File file = new File(Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".mp3");

                                        textParams.put("topic", "concept");
                                        textParams.put("IdIndex", tempDetail.lineN);
                                        fileParams.put("content.acc", file);

                                        response = UtilPostFile  //单句语音发送
                                                .post("http://voa." + Constant.IYBHttpHead() + "/voa/UnicomApi?topic=familyalbum"
                                                                + "&platform=android&format=json&protocol=60003"
                                                                + "&userid="
                                                                + UserInfoManager.getInstance().getUserId()
                                                                + "&username="
                                                                + UserInfoManager.getInstance().getUserName()
                                                                + "&voaid="
                                                                + voaTemp.voaId
                                                                + "&paraid="
                                                                + voaTemp.voaId
                                                                + "&idIndex="
                                                                + tempDetail.lineN
                                                                + "&score="
                                                                + clickDetail.getReadScore()
                                                                + "&shuoshuotype=2",
                                                        textParams, fileParams);
                                        LogUtil.e("sendRank 本地旧" + response);
                                    } else {//  新接口，使用网络地址
                                        response = UtilPostFile  //单句语音发送
                                                .post("http://voa." + Constant.IYBHttpHead() + "/voa/UnicomApi?topic=familyalbum"
                                                        + "&platform=android&format=json&protocol=60003"
                                                        + "&userid="
                                                        + UserInfoManager.getInstance().getUserId()
                                                        + "&username="
                                                        + UserInfoManager.getInstance().getUserName()
                                                        + "&voaid="
                                                        + voaTemp.voaId
                                                        + "&idIndex=" + tempDetail.lineN
                                                        + "&score="
                                                        + clickDetail
                                                        .getReadScore()
                                                        + "&shuoshuotype=2"
                                                        + "&content="
                                                        + voaSound.evaluateWebPath);//evaluatBean.getURL()
                                        LogUtil.e("sendRank" + response);
                                        LogUtil.e("sendRank：内容新" + "http://voa." + Constant.IYBHttpHead() + "/voa/UnicomApi?topic=familyalbum"//
                                                + "&platform=android&format=json&protocol=60003"
                                                + "&userid="
                                                + UserInfoManager.getInstance().getUserId()
                                                + "&username="
                                                + UserInfoManager.getInstance().getUserName()
                                                + "&voaid="
                                                + voaTemp.voaId
                                                + "&idIndex=" + tempDetail.lineN
                                                + "&score="
                                                + clickDetail
                                                .getReadScore()
                                                + "&shuoshuotype=2"
                                                + "&content="
                                                + voaSound.evaluateWebPath);//evaluatBean.getURL()
                                    }
                                    isUploadVoice = false;

                                    JSONObject jsonObjectRoot;
                                    jsonObjectRoot = new JSONObject(
                                            response);
                                    String result = jsonObjectRoot
                                            .getInt("ResultCode") + "";
                                    ReadVoiceComment rvc = new ReadVoiceComment(
                                            VoaDataManager.getInstance().voaTemp,
                                            mList.get(curPosition));
                                    //新旧接口返回的说说ID是不一样的
                                    if (voaSound == null || voaSound.evaluateWebPath == null || voaSound.evaluateWebPath.equals("")) {
                                        shuoshuoId = jsonObjectRoot.getInt("ShuoShuoId") + "";
                                        rvc.id = jsonObjectRoot.getInt("ShuoShuoId") + "";
                                        rvc.shuoshuo = jsonObjectRoot.getString("FilePath");
                                    } else {
                                        shuoshuoId = jsonObjectRoot.getInt("ShuoshuoId") + "";
                                        rvc.id = jsonObjectRoot.getInt("ShuoshuoId") + "";
                                        rvc.shuoshuo = voaSound.evaluateWebPath;//evaluatBean.getURL()
                                    }
                                    String addscore = jsonObjectRoot
                                            .getString("AddScore");
                                    mMap.put(curPosition, rvc);

                                    if (result.equals("501") || result.equals("1")) {
                                        waittingDialog.dismiss();
                                        Message msg = handler.obtainMessage();

                                        msg.what = 10;
                                        msg.arg1 = Integer
                                                .parseInt(addscore);
                                        StudyActivity.newInstance().commentHandler
                                                .sendMessage(msg);
                                        StudyActivity.newInstance().commentHandler
                                                .sendEmptyMessage(4);
                                        StudyActivity.newInstance().rankHandler.sendEmptyMessage(0);
                                    } else {
                                        handler.sendEmptyMessage(14);//发送失败
                                    }
                                } catch (Exception e) {
                                    isUploadVoice = false;
                                    LogUtil.e("发送失败" + e);
                                    handler.sendEmptyMessage(14);//发送失败
                                    e.printStackTrace();
                                }
                            }
                        });
                        threadsend.start();
                    }
                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    StudyActivity.newInstance().startActivity(intent);
                    LoginUtil.startToLogin(mContext);
                }
            }
        });
        //纠音按钮
        showHolder.btnWordCorrect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.isRecording || isEvaluating) {
                    ToastUtil.showToast(mContext, "正在评测中");
                    return;
                }

                if (onEvalClickListener!=null){
                    onEvalClickListener.onStopPlay();
                }

                if (TextUtils.isEmpty(voaSound.words)) {
                    ToastUtil.showToast(mContext, "未获取到评测数据，请重新评测");
                    return;
                }

                EvalCorrectPage correctPage = new EvalCorrectPage();
                //这里将数据保存下载并且传递过去
                correctPage.setUid(UserInfoManager.getInstance().getUserId());
                correctPage.setVoaDetail(clickDetail);
                correctPage.setEvaluateBean(GsonUtils.toObject(voaSound.words, EvaluationBeanNew.class));
                correctPage.show(((StudyActivity) mContext).getFragmentManager(), "EvalFragmentPage");

                //关闭声音
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                }
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                if (ep != null && ep.isPlaying()) {
                    ep.pause();
                }
                if (bp != null && bp.isPlaying()) {
                    bp.pause();
                }

                if (manager != null && manager.isRecording) {
                    manager.stopEvaluate();
                }
            }
        });

        if (ConfigManager.Instance().loadInt("eva_select_word") == 1) {
            showHolder.senEn.setOnSelectListener(word -> {
                onClickWordListener.clickWord(word);
            });
        } else {
            showHolder.senEn.setClickable(false);
        }


        return convertView;
    }

    private void showShare(Context context, String title, String content, String shareurl,
                           String imageUrl, String comment, String site, String titleurl,
                           PlatformActionListener actionListener) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(titleurl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(imageUrl);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(site);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareurl);
        if (actionListener != null) oks.setCallback(actionListener);
        // 启动分享GUI
        oks.show(context);
    }


    public void setClickPosition(int clickPosition) {
        stopAll();
        this.clickPosition = clickPosition;
        this.currParagraph = clickPosition + 1;
        this.lastParegraph = clickPosition;
        if (mList.size() == 0)
            tempDetail = new VoaDetail();
        else
            tempDetail = mList.get(currParagraph - 1);
        mPlayer.reset();
    }

    public void stopAll() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            //videoView.stop(); 用stop将导致不能再次播放
            // lisNotCmplMsg();
            //videoView.ON();//释放播放器
        }
        if (manager.isRecording) {
            manager.cancelEvaluate(true);
            handler.sendEmptyMessageDelayed(7, 50);//停止播放原文
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if (clickViewHolder != null && clickDetail != null) {
            handler.removeMessages(0);
            handler.sendEmptyMessage(1);
            handler.sendEmptyMessage(3);
            handler.sendEmptyMessage(5);
        }

    }

    private void setReadScoreViewContent(ValReadAdapter.ViewHolder holder, int score) {
        if (score < 50) {
            holder.senReadResult
                    .setBackgroundResource(R.drawable.sen_score_lower60);
            holder.senReadResult.setText("");

        } else if (score > 80) {
            holder.senReadResult.setText(String.valueOf(score));
            holder.senReadResult
                    .setBackgroundResource(R.drawable.sen_score_higher_80);
        } else {
            holder.senReadResult.setText(String.valueOf(score));
            holder.senReadResult
                    .setBackgroundResource(R.drawable.sen_score_60_80);
        }
    }

    private void setReadTime(int dex) {
        time = 0;
        List<VoaDetail> textDetailTemp = mList;

        if (currParagraph != 0) {//item 位置 position
            videoView.seekTo((int) (tempDetail.startTime * 1000.0));

            if (currParagraph == textDetailTemp.size()) {
                playTime = videoView.getDuration() / 1000
                        - (tempDetail.startTime);
            } else {
                playTime = textDetailTemp.get(currParagraph).startTime
                        - tempDetail.startTime - 0.5;
            }
        }
        if (dex == 0) {
            playTime = playTime / 2;
        }

        handler.removeMessages(0);
        handler.sendEmptyMessage(0);
        videoView.start();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        beginTime = df.format(new Date());// new Date()为获取当前系统时间
    }

    public boolean isStoppedAndCouldPlay() {
        if (mPlayer != null) {
            return mPlayer.isIdle() || mPlayer.isCompleted()
                    || mPlayer.isInitialized();
        }
        return false;
    }

    //录音播放
    public void playRecord2(int senIndex) {
        if (mPlayer != null) {
            if (mPlayer.isIdle()) {
                String filepath = Constant.getsimRecordAddr() + senIndex + ".mp3";
                //String filepath = manager.mMp3Path;
                LogUtil.e("filepath", filepath);
                try {
                    mPlayer.initialize(filepath);
                    mPlayer.prepareAndPlay();//???
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        mPlayer = null;
                        mPlayer = new Player(mContext, null);
                        mPlayer.initialize(filepath);
                        mPlayer.prepareAndPlay();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        ToastUtil.showToast(mContext, "播放失败");
                    }

                }

            } else if (mPlayer.isCompleted()) {
                mPlayer.start();
            } else if (mPlayer.isInitialized()) {
                try {
                    mPlayer.prepareAndPlay();//异常！
                } catch (Exception e) {
                    e.printStackTrace();
                    String filepath = Constant.getsimRecordAddr() + senIndex + ".mp3";
                    LogUtil.e("filepath", filepath);
                    mPlayer.initialize(filepath);
                    mPlayer.prepareAndPlay();
                }
            }
        }
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) StudyActivity.newInstance().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // 将要执行的操作写在线程对象的run方法当中
    Runnable updateReadThread = new Runnable() {
        public void run() {
            // 播放音频时，计时
            if (time <= playTime) {
                time = time + 0.05;
                handler.sendEmptyMessageDelayed(0, 50);
            } else {
                videoView.pause();

                videoView.seekTo((int) (tempDetail.startTime * 1000.0));
                clickDetail.isListen = true;
                handler.sendEmptyMessage(1);

                SimpleDateFormat df = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");// 设置日期格式
                endTime = df.format(new Date());// new Date()为获取当前系统时间

            }
        }
    };

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            RoundProgressBar tempBar = null;
            switch (msg.what) {
                case 0:// to change the progress bar of play button with handler 改变进度条
                    tempBar = clickViewHolder.senPlay;
                    if (videoView.isPlaying()) {
                        tempBar.setBackgroundResource(R.drawable.sen_stop);
                    } else {
                        tempBar.setBackgroundResource(R.drawable.sen_play);
                    }

                    tempBar.setCricleProgressColor(0xff66a6e8);
                    if (playTime * 100 < 0) {//IllegalArgumentException: max not less than 0
                        tempBar.setMax(0);
                    } else {
                        tempBar.setMax((int) (playTime * 100));
                    }
                    tempBar.setProgress((int) (time * 100));

                    if (videoView.isPlaying()) {
                        updateReadThread.run();
                    }
                    break;
                case 1:// reset the progress bar of play button when the playing is
                    // over
                    clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
                    tempBar = clickViewHolder.senPlay;
                    tempBar.setCricleProgressColor(0xff66a6e8);
                    tempBar.setMax(100);
                    tempBar.setProgress(0);
                    notifyDataSetChanged();
                    break;
                case 2:// set the read button progress bar with value of voice
                    // volume
                    try {
                        tempBar = clickViewHolder.senIRead;
                        int db = msg.arg1;
                        // Log.e(TAG, "sound DB value: " + db);
                        tempBar.setCricleProgressColor(0xff87c973);
                        tempBar.setMax(100);
                        if (db > 0)
                            tempBar.setProgress(db);
                    } catch (Exception e) {
                        Log.e("val", "handler.case2");
                    }
                    break;
                case 3: // reset the read button progress bar
                    try {
                        clickViewHolder.senIRead
                                .setBackgroundResource(R.drawable.sen_i_read);
                        tempBar = clickViewHolder.senIRead;
                        tempBar.setCricleProgressColor(0xff87c973);
                        tempBar.setMax(100);
                        tempBar.setProgress(0);
                    } catch (Exception e) {
                        Log.e("val", "handler.case3");
                    }
                    break;
                case 4:// 播放自己录音时的progressbar
                    tempBar = clickViewHolder.senReadPlaying;
                    tempBar.setCricleProgressColor(0xff66a6e8);
                    if (mPlayer.getDuration() - 250 >= 0) {
                        tempBar.setMax(mPlayer.getDuration() - 250);
                    } else {
                        tempBar.setMax(200);//!!!判断有错误
                    }
                    if (mPlayer.getCurrentTime() > 0)
                        tempBar.setProgress(mPlayer.getCurrentTime());
                    if (mPlayer.isPlaying()) {
                        handler.sendEmptyMessageDelayed(4, 100);
                    } else {
                        handler.sendEmptyMessage(5);
                    }
                    break;
                case 5:// 重置播放录音的progressbar
                    clickViewHolder.senReadPlay.setVisibility(View.VISIBLE);
                    clickViewHolder.senReadPlaying.setVisibility(View.GONE);
                    tempBar = clickViewHolder.senReadPlaying;
                    tempBar.setCricleProgressColor(0xff66a6e8);
                    tempBar.setMax(100);
                    tempBar.setProgress(0);
                    notifyDataSetChanged();
                    break;
                case 6:
                    int score = msg.arg1;
                    int voaID = msg.arg2;
                    evaluatBean = (EvaluationBeanNew) msg.obj;
                    int timpClickPosition = clickPosition;
                    String userwords = "";
                    boolean is_rejected = false;//(Boolean) msg.obj;
                    if (is_rejected) {
                        CustomToast.showToast(mContext, "语音异常，请重新录入!", 1500);

                        clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read);

                        String filepath = Constant.getsimRecordAddr() + Integer.parseInt(voaTemp.voaId
                                + "" + timpClickPosition) + Constant.getrecordTag();
                        File file = new File(filepath);

                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }

                    } else {
                        isEvaluating = false;
                        mMap.remove(voaID);
                        handler.sendEmptyMessage(3);
                        mList.get(timpClickPosition).isRead = true;
                        mList.get(timpClickPosition).setReadScore(score);
                        CustomToast.showToast(mContext, "评测成功", 1800);

                        String wordScore = "";
                        //rs 被取代  evaluatBean相当于Sentence
                        if (evaluatBean != null) {
                            if (evaluatBean.getWords() != null && evaluatBean.getWords().size() != 0) {
                                List<EvaluationBeanNew.WordsBean> words = evaluatBean.getWords();
                                for (int j = 0; j < words.size(); j++) {
                                    EvaluationBeanNew.WordsBean wdd = words.get(j);

                                    wordScore = wordScore + wdd.getScore() + ",";
                                    if (Double.parseDouble(wdd.getScore()) < 3.0
                                            && Double.parseDouble(wdd.getScore()) > 0.0) {

                                        if (userwords.equals(""))
                                            userwords = wdd.getContent();
                                        else
                                            userwords = userwords + ","
                                                    + wdd.getContent();
                                    }
                                }
                            }

                            if (userwords.equals("")) {//单词拼接字符串
                                VoaDetail en = mList.get(timpClickPosition);
                                String[] split = en.sentence.split(" ");
                                endTime = manager.getEndTime();

                                Message mg = new Message();
                                mg.what = 1;
                                Bundle bundle = new Bundle();
                                bundle.putString("endFlag", "0");
                                bundle.putString("lessonId",
                                        String.valueOf(voaTemp.voaId));
                                bundle.putString("lesson", voaTemp.title);
                                bundle.putString("testNumber",
                                        String.valueOf(currParagraph));
                                bundle.putString("testWords",
                                        String.valueOf(split.length));
                                bundle.putString("testMode", "2");
                                bundle.putString("userAnswer", userwords);
                                bundle.putString("score", String.valueOf(score));
                                mg.setData(bundle);
                                videoHandle.sendMessage(mg);//发送评测
                            }
                            LogUtil.e("时间" + beginTime + "----" + endTime + "---" + manager.getStartDate()
                                    + "---" + manager.getEndDate());
                            valReadAdapterInteraction.getIndex(senIndex, score,
                                    manager.getEndDate() - manager.getStartDate(),
                                    evaluatBean.getUrl());//Constant.getsimRecordAddr() + tempDetail.voaId + clickPosition + ".mp3"

                            valReadAdapterInteraction.setDefault();

                            //这里更换保存数据的方法
                            voaSoundOp.updateEvalData(wordScore, score, tempDetail.voaId,
                                    Constant.getsimRecordAddr() + tempDetail.voaId + timpClickPosition + ".mp3",//manager.mMp3Path,//
                                    manager.getEndDate() - manager.getStartDate() + "",
                                    Integer.parseInt(tempDetail.voaId + "" + timpClickPosition),
                                    evaluatBean.getUrl(),
                                    GsonUtils.toJson(evaluatBean));

                        }

                        notifyDataSetChanged();
                    }
                    break;
                case 7:
                    if (ep != null)
                        ep.pause();

                    if (bp != null)
                        bp.pause();
                    break;
                case 8:
                    clickDetail.isCommit = true;
                    notifyDataSetChanged();
                    break;
                case 9:
                    ToastUtil.showToast(mContext, "录音中");
                    if (clickViewHolder.llWordCorrect.getVisibility() == View.VISIBLE) {
                        clickViewHolder.llWordCorrect.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 10:
                    ToastUtil.showToast(mContext, "评测中");
                    isEvaluating = true;
                    break;
                case 11:
                    if (manager.isRecording) {
                        dismissDia();//关闭录音
                        tempBar = clickViewHolder.senIRead;
                        tempBar.setProgress(0);
                    }
                    break;
                case 12:
                    isEvaluating = false;
                    if (msg.arg1 == 404) {
                        CustomToast.showToast(mContext, "评测失败\n未连接到服务器，请检查网络！", 2000);
                    } else {
                        CustomToast.showToast(mContext, "评测失败", 1000);
                    }
                    LogUtil.e("评测失败");
                    break;
                case 13:
                    //录音结束，进入评测
                    // SoundDialog.setText("评测中");
                    // handler.sendEmptyMessageDelayed(14,6000);//6s还在评测中，就是异常
                    break;
                case 14:
                    ToastUtil.showToast(mContext, "发布失败");
                    if (waittingDialog.isShowing()) {
                        waittingDialog.dismiss();
                    }
                    break;
                case 104:
                    //录音音量动画
                    //curViewHolder.senIRead.setProgress(msg.arg1);
                    //senIReadProgress(msg.arg1);
                    if (!manager.isRecording) {
                        return;
                    }
                    tempBar = clickViewHolder.senIRead;
                    tempBar.setMax(10000);
                    tempBar.setCricleProgressColor(Color.GREEN);
                    if (msg.arg1 > 0)
                        tempBar.setProgress(msg.arg1);
                    LogUtil.e("录音104 ，音量" + msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    Handler videoHandle = new Handler() {
        public void handleMessage(Message msg) {
            // Looper.prepare();
            switch (msg.what) {
                case 1: // 向服务器发送数据

                    String endFlag = msg.getData().getString("endFlag");
                    // String lesson = msg.getData().getString("lesson");
                    String lesson = "familyalbum"; // lesson应该上传用户使用的哪一款app的名称 familyalbum NewConcept
                    String lessonId = msg.getData().getString("lessonId");
                    String testNumber = msg.getData().getString("testNumber");
                    String testWords = msg.getData().getString("testWords");
                    final String testMode = msg.getData().getString("testMode");
                    String userAnswer = msg.getData().getString("userAnswer");
                    String score = msg.getData().getString("score");
                    String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
                    SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                    String sign = uid + beginTime
                            + dft.format(System.currentTimeMillis());
                    String deviceId = getLocalMacAddress();

                    LogUtil.e("endtime", endTime);
                    if (NetWorkState.isConnectingToInternet()) {
                        try {
                            ClientSession.Instace().asynGetResponse(
                                    new DataCollectRequest(uid, beginTime, endTime,
                                            lesson, lessonId, testNumber,
                                            testWords, testMode, userAnswer, score,
                                            endFlag, deviceId, sign),
                                    new IResponseReceiver() {
                                        @Override
                                        public void onResponse(
                                                BaseHttpResponse response,
                                                BaseHttpRequest request,
                                                int rspCookie) {
                                            DataCollectResponse tr = (DataCollectResponse) response;

                                            if (tr != null && tr.result.equals("1")
                                                    && testMode.equals("1")) {
                                                Looper.prepare();
                                                if (tr.score.equals("0"))
                                                    Toast.makeText(mContext,
                                                            "数据提交成功!", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(
                                                            mContext,
                                                            "数据提交成功，恭喜您获得了"
                                                                    + tr.score
                                                                    + "分", Toast.LENGTH_SHORT)
                                                            .show();
                                                handler.sendEmptyMessage(8);
                                                Looper.loop();
                                            } else if (tr.result.equals("0")) {
                                                Looper.prepare();
                                                Toast.makeText(mContext, "数据提交出错",
                                                        Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            } else {
                                                Looper.prepare();
                                                Looper.loop();
                                            }
                                        }
                                    }, null, mNetStateReceiver);
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };


    //关闭 录音
    public void dismissDia() {
        //soundDialog.dismiss();
        manager.cancelEvaluate(true);
        clickViewHolder.senIRead.setBackgroundResource(R.drawable.sen_i_read);
        handler.sendEmptyMessageDelayed(7, 100);//播放器关闭
        handler.sendEmptyMessage(3);//播放进度归零
        manager.stopEvaluate();//关闭录音
    }

    public static class ViewHolder {

        TextView senIndex;
        DictionEditText senEn;
        TextView senZh;
        TextView wordChosn;
        RoundProgressBar senPlay;
        View senReadPlayButton;
        RoundProgressBar senIRead;
        RoundProgressBar senReadPlaying;
        ImageView senReadPlay;
        ImageView senReadSend;
        ImageView senReadCollect;
        TextView senReadResult;
        ImageView sepLine;
        View bottomView;
        View frontView;
        Button wordCommit;

        // TODO: 2022/6/28 纠音
        LinearLayout llWordCorrect;
        TextView tvWordCorrect;
        Button btnWordCorrect;
    }

    // 1 定义了所有activity必须实现的接口方法
    //这里方法是用于将语音保存下来，进行后面的语音合成的
    //以后请把注释写清楚了
    public interface ValReadAdapterInteraction {
        void getIndex(int position, int score, long time, String filepath);

        void setDefault();
    }

    private ValReadAdapterInteraction valReadAdapterInteraction;


    //获取保存的单词拆分数据，并处理是否显示
    private void showCorrectionView(List<EvaluationBeanNew.WordsBean> wordsBeans) {
        if (wordsBeans == null || wordsBeans.size() == 0) {
            clickViewHolder.llWordCorrect.setVisibility(View.GONE);
            return;
        }

        int flag = 0;
        StringBuilder builder = new StringBuilder(16);
        for (int i = 0; i < wordsBeans.size(); i++) {
            double scoreInt = Double.parseDouble(wordsBeans.get(i).getScore());
            String wordStr = wordsBeans.get(i).getContent();

            if (!TextUtils.isEmpty(wordStr)
                    && !("-").equals(wordStr)
                    && scoreInt < errorWordScore) {
                flag++;
                if (flag < 2) {
                    if ("Mr.".equals(wordStr)
                            || "Mrs.".equals(wordStr)
                            || "Ms.".equals(wordStr)) {
                        builder.append(wordStr).append(" ");
                    } else {
                        builder.append(wordStr.replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1")).append(" ");
                    }
                }
            }
        }

        if (flag > 0) {
            clickViewHolder.llWordCorrect.setVisibility(View.VISIBLE);
            clickViewHolder.tvWordCorrect.setText(builder + "单词发音有误");
            ViewGroup.LayoutParams params = clickViewHolder.llWordCorrect.getLayoutParams();
            params.height = DensityUtil.dip2px(mContext, 42);
            if (flag == 1) {
                if (builder.length() < 4) {
                    params.width = DensityUtil.dip2px(mContext, 180);
                } else if (builder.length() < 8) {
                    params.width = DensityUtil.dip2px(mContext, 200);
                } else if (builder.length() < 12) {
                    params.width = DensityUtil.dip2px(mContext, 220);
                } else {
                    params.width = DensityUtil.dip2px(mContext, 240);
                }
            } else {
                if (builder.length() < 4) {
                    params.width = DensityUtil.dip2px(mContext, 190);
                } else if (builder.length() < 8) {
                    params.width = DensityUtil.dip2px(mContext, 210);
                } else if (builder.length() < 12) {
                    params.width = DensityUtil.dip2px(mContext, 230);
                } else {
                    params.width = DensityUtil.dip2px(mContext, 250);
                }
            }
            clickViewHolder.llWordCorrect.setLayoutParams(params);
            clickViewHolder.llWordCorrect.setVisibility(View.VISIBLE);
        } else {
            clickViewHolder.llWordCorrect.setVisibility(View.GONE);
        }
    }

    public interface OnClickWordListener {
        void clickWord(String word);
    }

    //刷新上一个数据样式
    public void refreshPreView(int position) {
        if (clickPosition == position) {
            return;
        }

        time = 0;
        clickViewHolder.senPlay.setProgress(0);
        clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
    }

    /*******************************新的样式操作*********************************/
    //总的回调接口
    private OnEvalClickListener onEvalClickListener;

    public interface OnEvalClickListener {
        //切换item
//        void onItemSwitch(int position);
        //播放原音
//        void onPlayAudio(String audio, double startTime, double endTime);
        //录音
        //播放评测
        //发布到排行榜
        //分享
        //纠音

        //临时操作-停止音频播放
        void onStopPlay();
    }

    public void setOnEvalClickListener(OnEvalClickListener onEvalClickListener) {
        this.onEvalClickListener = onEvalClickListener;
    }

    //刷新原文播放的样式
    public void refreshAudioView(long total, long progress, boolean isPlay) {
        if (isPlay) {
            clickViewHolder.senPlay.setMax((int) total);
            clickViewHolder.senPlay.setProgress((int) progress);
            clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_stop);
        } else {
            clickViewHolder.senPlay.setMax(0);
            clickViewHolder.senPlay.setProgress(0);
            clickViewHolder.senPlay.setBackgroundResource(R.drawable.sen_play);
        }
    }
}