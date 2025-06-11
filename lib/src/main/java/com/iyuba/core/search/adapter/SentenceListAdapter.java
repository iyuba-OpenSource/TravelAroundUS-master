package com.iyuba.core.search.adapter;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.me.pay.RequestCallBack;
import com.iyuba.core.search.activity.SentenceListActivity;
import com.iyuba.core.search.bean.EvaluationBeanNew;
import com.iyuba.core.search.bean.SearchInfoBean;
import com.iyuba.core.search.bean.SearchSentenceBean;
import com.iyuba.core.search.request.AddCreditsRequest;
import com.iyuba.core.search.util.ReadEvaluateManager;
import com.iyuba.core.search.util.ResultParse;
import com.iyuba.core.search.util.UtilPostFile;
import com.iyuba.lib.R;

import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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

/**
 * 句子列表 搜索结果
 */
public class SentenceListAdapter extends RecyclerView.Adapter {

    private SearchInfoBean searchInfoBean;
    private Context mContext;

    public MediaPlayer mediaPlayer;
    private int playPosition = -1;
    private ReadEvaluateManager manager;
    //private com.iyuba.concept2.widget.cdialog.CustomDialog soundDialog;
    private CustomDialog mWaitingDialog;
    private String playUrl;
    private OnClickListener mOnClickListener;
    private int playTime;
    private int maxTime;
    private boolean isFirst;


    public SentenceListAdapter(Context context) {
        mContext = context;
        mediaPlayer = new MediaPlayer();
    }

    public void setData(SearchInfoBean searchInfoBean) {
        this.searchInfoBean = searchInfoBean;
        isFirst=true;
        playPosition = -1;//设置没有播放的
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            //mediaPlayer.stop(); 谨慎使用
        }
    }

    public void appendData(SearchInfoBean searchInfoBean) {
        if (this.searchInfoBean != null) {
            // playPosition = -1;//设置没有播放的
            for (SearchSentenceBean bean : searchInfoBean.getTextData()) {
                this.searchInfoBean.getTextData().add(bean);
            }
            if (mediaPlayer.isPlaying()) {
                // mediaPlayer.pause();
                //mediaPlayer.seekTo(0);
                //mediaPlayer.stop();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        View view = mInflater.inflate(R.layout.search_sentence_item, viewGroup, false);
        return new SenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SenViewHolder) {
            ((SenViewHolder) viewHolder).setData(position, position);
            ((SenViewHolder) viewHolder).setListener(position, position);
        }
    }

    @Override
    public int getItemCount() {
        if (searchInfoBean != null) {
            return searchInfoBean.getTextData().size();
        }
        return 0;
    }

    public class SenViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSenIndex;
        private TextView tvSenEn;
        private TextView tvSenCh;
        private TextView chosnWord;
        private Button wordCommit;
        private LinearLayout frontView;
        private RoundProgressBar rpbSenPlay;
        private RoundProgressBar rpbSenIRead;
        private ImageView ivSenReadPlay;
        private RoundProgressBar senReadPlaying;
        private FrameLayout senReadButton;
        private ImageView ivSenReadSend;
        private ImageView ivSenReadCollect;
        private TextView senReadResult;
        private LinearLayout llChoose;

        private int positions;
        private int indexBean;
        private boolean isSen;
        private boolean isUploadVoice = false;//是否正在发送评测录音
        private String suosuoId;
        private boolean isSening;


        public SenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenIndex=itemView.findViewById(R.id.tv_sen_index);
            tvSenEn=itemView.findViewById(R.id.tv_sen_en);
            tvSenCh=itemView.findViewById(R.id.tv_sen_zh);
            chosnWord=itemView.findViewById(R.id.chosn_word);
            wordCommit=itemView.findViewById(R.id.word_commit);
            frontView=itemView.findViewById(R.id.front_view);
            rpbSenPlay=itemView.findViewById(R.id.rpb_sen_play);
            rpbSenIRead=itemView.findViewById(R.id.rpb_sen_i_read);
            ivSenReadPlay=itemView.findViewById(R.id.iv_sen_read_play);
            senReadPlaying=itemView.findViewById(R.id.sen_read_playing);
            senReadButton=itemView.findViewById(R.id.sen_sound_button);
            ivSenReadSend=itemView.findViewById(R.id.iv_sen_read_send);
            ivSenReadCollect=itemView.findViewById(R.id.iv_sen_read_collect);
            senReadResult=itemView.findViewById(R.id.sen_read_result);
            llChoose=itemView.findViewById(R.id.ll_choose);
        }

        public void setData(int position, int index) {
            positions = position;
            if (playPosition == -1) {
                playTime = 0;
            }
            mWaitingDialog = WaittingDialog.showDialog(mContext);//加载过度
            tvSenEn.setText(searchInfoBean.getTextData().get(position).getSentence());
            tvSenCh.setText(searchInfoBean.getTextData().get(position).getSentence_cn());
            tvSenIndex.setText(String.valueOf(position + 1));
            if (position==0&&isFirst){
                searchInfoBean.getTextData().get(position).setClick(true);
                isFirst=false;
            }
            if (searchInfoBean.getTextData().get(position).isClick()) {
                llChoose.setVisibility(View.VISIBLE);
            } else {
                llChoose.setVisibility(View.GONE);
            }

            if (searchInfoBean.getTextData().get(index).isTest()) {
                if (!isSen) {//如果录音没有在播放
                    ivSenReadPlay.setVisibility(View.VISIBLE);//播放录音
                }
                ivSenReadSend.setVisibility(View.VISIBLE);//发送口语圈
                senReadResult.setVisibility(View.VISIBLE);//得分
//                EvaluatBean evaluatBean = searchInfoBean.getTextData().get(index).getEvaluateBean();
                EvaluationBeanNew evaluatBean = searchInfoBean.getTextData().get(index).getEvaluatBean();
                //写入得分
                setReadScoreViewContent((int) (Double.valueOf(evaluatBean.getTotal_score()) * 20));
                //字体变色   //拼接之后的数据
                String[] floats = new String[evaluatBean.getWords().size()];
                for (int i = 0; i < evaluatBean.getWords().size(); i++) {
                    floats[i] = String.valueOf(evaluatBean.getWords().get(i).getScore());
                }
                SpannableStringBuilder readResult = ResultParse.getSenResultLocal(floats, searchInfoBean.getTextData().get(index).getSentence());

                tvSenEn.setText(readResult);//变色写入
                if (searchInfoBean.getTextData().get(index).isSendOut()) {
                    ivSenReadCollect.setVisibility(View.VISIBLE);//分享
                } else {
                    ivSenReadCollect.setVisibility(View.GONE);//分享
                }

            } else {
                ivSenReadPlay.setVisibility(View.GONE);//播放录音
                ivSenReadSend.setVisibility(View.GONE);//发送口语圈
                senReadResult.setVisibility(View.GONE);//得分
                ivSenReadCollect.setVisibility(View.GONE);//分享
            }
            //如果当前index的项不在播放就关掉播放，并且停止进度条
            LogUtil.e("item" + index + "是否在播放" + searchInfoBean.getTextData().get(index).isPlay());
            if (!searchInfoBean.getTextData().get(index).isPlay()) {
                //mediaPlayer.pause();
                //mediaPlayer.seekTo(0);
                rpbSenPlay.setProgress(0);
                rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
            } else {
                if (mediaPlayer.isPlaying()) {//未知的原因引起暂停，继续播放
                   // mediaPlayer.start();
                    //rpbSenPlay.setProgress(playTime);
                    rpbSenPlay.setBackgroundResource(R.drawable.sen_stop);
                    rpbSenPlay.setCricleProgressColor(0xff66a6e8);
                    //handler.sendEmptyMessage(0);//进度恢复
                }
            }

        }

        public void setListener(final int index, final int position) {
            final SearchSentenceBean bean = searchInfoBean.getTextData().get(index);
            //评测按钮
            rpbSenIRead.setOnClickListener(v -> {
                //bean.setTest(true);//标记为已经评测
                ((SentenceListActivity) mContext).checkStudyPermission();
                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext,
                        new String[]{Manifest.permission.RECORD_AUDIO})) {

                    return;
                }
                rpbSenIRead.setMax(10000);
                if (!isSening) {
                    isSening = true;
                    manager = ReadEvaluateManager.getInstance(mContext);

                    //关闭播放
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }
                    rpbSenPlay.setProgress(0);
                    rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
                    senReadPlaying.setProgress(0);
                    senReadPlaying.setVisibility(View.INVISIBLE);
                    ivSenReadPlay.setVisibility(View.VISIBLE);
                    playTime = 0;
                    notifyItemChanged(position);

                    if (!manager.isRecording) {
                        try {
                            //soundDialog = SoundDialog.showDialog(mContext, SentenceListAdapter.this, "录音中");
                            //开始录音
                            Map<String, String> textParams = new HashMap<String, String>();
                            textParams.put("type", "familyalbum");
                            textParams.put("userId", String.valueOf(UserInfoManager.getInstance().getUserId()));
                            textParams.put("newsId", bean.getVoaId() + "");
                            textParams.put("paraId", bean.getParaId() + "");
                            textParams.put("IdIndex", bean.getIdIndex());
                            String sentence = URLEncoder.encode(bean.getSentence(), "UTF-8").replace("+", "%20");
                            textParams.put("sentence", sentence);
                            textParams.put("appId", Constant.APPID);
                            textParams.put("flg", "0");
                            textParams.put("wordId", "0");
                            manager.startEvaluate(position, bean.getSentence(), Integer.valueOf(bean.getVoaId()), textParams, handler);
                            //handler.sendEmptyMessage(104);
                            indexBean = index;
                            rpbSenIRead.setBackgroundResource(R.drawable.sen_i_stop);
                            //有时间为0的情况，不能延时关闭
                            int time = (int) (Double.valueOf(bean.getEndTiming()) - Double.valueOf(bean.getTiming()));
                            LogUtil.e("录音 持续时间：" + time * 1.7 * 1000 + "原始时间" + time);
                            if (time<1){
                                time=20000;
                                LogUtil.e("录音 持续时间：异常，修改为"+time);
                            }
                            //有的时间太短
                            handler.sendEmptyMessageDelayed(11, (int)(time * 1.7 * 1000));//延时1.7倍关闭·
                        } catch (ParseException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        manager.stopEvaluate();
                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);
                    }
                }else {
                    isSening = false;
                    handler.sendEmptyMessage(10);
                }
            });
            //句子原始音频播放
            rpbSenPlay.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    if (isSen) {
                        //关闭录音播放
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(0);
                        }
                        senReadPlaying.setProgress(0);
                        senReadPlaying.setVisibility(View.INVISIBLE);
                        ivSenReadPlay.setVisibility(View.VISIBLE);
                        playTime = 0;
                        playPosition=-1;
                        isSen=false;
                    }
                    if (playPosition == position) {
                        mediaPlayer.pause();//声音暂停
                        //进度暂停
                    } else {
                        startPlay(position, true);//本条初始播放
                        //handler.sendEmptyMessage(1);//其他条进度复原
                    }
                } else {
                    if (playPosition == position) {
                        if (searchInfoBean.getTextData().get(index).isPlay()) {
                            mediaPlayer.start();//声音恢复
                            handler.sendEmptyMessage(0);//进度恢复
                        } else {
                            //点击了其他的item造成的暂停，需要重新播放
                            startPlay(position, true);//最初始播放
                        }
                    } else {
                        startPlay(position, true);//最初始播放
                    }
                }
            });
            //句子录音播放
            ivSenReadPlay.setOnClickListener(v -> {
                if (searchInfoBean.getTextData().get(index).getEvaluatBean() != null) {
                    isSen = true;

                    //关闭播放
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }
                    rpbSenPlay.setProgress(0);
                    rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
                    senReadPlaying.setProgress(0);
                    senReadPlaying.setVisibility(View.INVISIBLE);
                    ivSenReadPlay.setVisibility(View.VISIBLE);
                    playTime = 0;
                    startPlay(position, false);
                }
            });

            //单句评测发送至排名
            ivSenReadSend.setOnClickListener(v -> {
                if (UserInfoManager.getInstance().isLogin()) {
                    if (isUploadVoice) {
                        CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
                    } else {
                        mWaitingDialog.show();
                        Thread threadsend = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    isUploadVoice = true;
                                    String response = "";
                                    response = UtilPostFile  //单句语音发送
                                            .post("http://voa."+Constant.IYBHttpHead()+"/voa/UnicomApi?topic=familyalbum"
                                                    + "&platform=android&format=json&protocol=60003"
                                                    + "&userid="
                                                    + UserInfoManager.getInstance().getUserId()
                                                    + "&voaid="
                                                    + searchInfoBean.getTextData().get(index).getVoaId()
                                                    + "&idIndex=" + searchInfoBean.getTextData().get(index).getIdIndex()
                                                    + "&score="
                                                    //+ searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()
//                                                        + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()) * 20)
                                                    + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluatBean().getTotal_score()) * 20)
                                                    + "&shuoshuotype=2"
                                                    + "&content="
//                                                        + searchInfoBean.getTextData().get(index).getEvaluateBean().getURL());
                                                    + searchInfoBean.getTextData().get(index).getEvaluatBean().getUrl());
                                    LogUtil.e("sendRank" + response);
                                    LogUtil.e("sendRank：内容" + "http://voa."+Constant.IYBHttpHead()+"/voa/UnicomApi?topic=familyalbum"//
                                            + "&platform=android&format=json&protocol=60003"
                                            + "&userid="
                                            + UserInfoManager.getInstance().getUserId()
                                            + "&voaid="
                                            + searchInfoBean.getTextData().get(index).getVoaId()
                                            + "&idIndex=" + searchInfoBean.getTextData().get(index).getIdIndex()
                                            + "&score="
                                            //+ searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()
//                                                + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()) * 20)
                                            + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluatBean().getTotal_score()) * 20)
                                            + "&shuoshuotype=2"
                                            + "&content="
//                                                + searchInfoBean.getTextData().get(index).getEvaluateBean().getURL());
                                            + searchInfoBean.getTextData().get(index).getEvaluatBean().getUrl());

                                    isUploadVoice = false;

                                    JSONObject jsonObjectRoot;
                                    jsonObjectRoot = new JSONObject(
                                            response);
                                    String result = jsonObjectRoot
                                            .getInt("ResultCode") + "";
                                    String addscore = jsonObjectRoot
                                            .getString("AddScore");
                                    suosuoId = String.valueOf(jsonObjectRoot.getInt("ShuoshuoId"));

                                    if (result.equals("501") || result.equals("1")) {
                                        mWaitingDialog.dismiss();
                                        Message msg = handler
                                                .obtainMessage();
                                        msg.what = 16;
                                        msg.arg1 = Integer
                                                .parseInt(addscore);
                                        handler.sendMessage(msg);
                                    }
                                } catch (Exception e) {
                                    isUploadVoice = false;
                                    LogUtil.e("sendRank 发布异常" + e);
                                    e.printStackTrace();

                                    //增加处理样式
                                    Message message = handler.obtainMessage();
                                    message.what = 17;
                                    handler.sendMessage(message);
                                }
                            }
                        });
                        threadsend.start();
                    }
                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, Login.class);
//                    mContext.startActivity(intent);
                    LoginUtil.startToLogin(mContext);
                }
            });

            //单句分享按钮
            ivSenReadCollect.setOnClickListener(v -> {
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
                            RequestCallBack rc = result -> {
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
                            };
                            int uid = UserInfoManager.getInstance().getUserId();
                            AddCreditsRequest rq = new AddCreditsRequest(
                                    uid,
                                    Integer.valueOf(searchInfoBean.getTextData().get(index).getVoaId()),
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

                String name = Constant.APPName;

                String shareTitle = "[" + UserInfoManager.getInstance().getUserName() + "]"
                        + "在" + name + "中获得了"
                        + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluatBean().getTotal_score()) * 20)
                        + "分";
                String shortText = searchInfoBean.getTextData().get(index).getSentence();
                String ArticleShareUrl = "http://voa."+Constant.IYBHttpHead()+"/voa/play.jsp?id=" + suosuoId;
                showShare(mContext, shareTitle, shortText, ArticleShareUrl,
                        Constant.AppIcon(), "很不错的应用，大家快来使用呀！", Constant.APPName,
                        ArticleShareUrl, pal);
            });

            itemView.setOnClickListener(v -> {
                if (!bean.isClick()) {
                    bean.setClick(true);
                    for (int i = 0; i < searchInfoBean.getTextData().size(); i++) {
                        if (i != index) {
                            searchInfoBean.getTextData().get(i).setClick(false);
                            searchInfoBean.getTextData().get(i).setPlay(false);

                            if (mediaPlayer.isPlaying()){
                                mediaPlayer.pause();
                                playPosition=position;
                                maxTime=0;
                                playTime=0;
                            }
                        }
                    }
                    notifyDataSetChanged();//更新全部数据了
                }
            });
        }

        // 将要执行的操作写在线程对象的run方法当中
        Runnable updateProgressThread = new Runnable() {
            public void run() {
                // 播放音频时，计时
                if (playTime <= maxTime) {
                    int d = (int) (maxTime / 150);
                    playTime = playTime + d;
                    LogUtil.e("进度" + playTime + " 总时间" + maxTime);
                    handler.sendEmptyMessageDelayed(0, maxTime / 150);
                }
            }
        };

        Runnable updateProgressThreadSen = new Runnable() {
            public void run() {
                // 播放音频时，计时
                if (playTime <= maxTime) {
                    int d = (int) (maxTime / 150);
                    playTime = playTime + d;
                    LogUtil.e("进度" + playTime + " 总时间" + maxTime);
                    handler.sendEmptyMessageDelayed(3, (int) (maxTime / 150));
                }
            }
        };

        //重新初始化播放
        private void startPlay(final int position, final boolean isRpbSen) {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()&&!searchInfoBean.getTextData().get(position).isPlay()) {
                try {
                    mediaPlayer.reset();
                    if (isRpbSen) {
                        playUrl = searchInfoBean.getTextData().get(position).getSoundText();
                        mediaPlayer.setDataSource(playUrl);
                    } else {
                        //替换
                        mediaPlayer.setDataSource("http://voa."+Constant.IYBHttpHead()+"/voa/" + searchInfoBean.getTextData().get(position).getEvaluatBean().getUrl());
                    }
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e("播放器初始化异常" + e);
                }
                //完成播放监听
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playTime = 0;
                        playPosition = -1;
                        if (isRpbSen) {
                            //播放完毕
                            handler.sendEmptyMessage(1);
                            searchInfoBean.getTextData().get(position).setPlay(false);
                        } else {
                            handler.sendEmptyMessage(2);
                            handler.sendEmptyMessage(15);
                            searchInfoBean.getTextData().get(position).setPlay(false);
                            isSen = false;
                        }
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        LogUtil.e("播放错误" + what + "extra" + extra);
                       // mediaPlayer.stop();
                        return false;
                    }
                });
                //异步播放监听
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        LogUtil.e("播放器成功播放"+mediaPlayer.getDuration());
                        mediaPlayer.start();
                        playPosition = position;
                        for (SearchSentenceBean sentenceBean : searchInfoBean.getTextData()) {
                            sentenceBean.setPlay(false);
                        }
                        searchInfoBean.getTextData().get(position).setPlay(true);
                        if (isRpbSen) {
                            rpbSenPlay.setBackgroundResource(R.drawable.sen_stop);
                            rpbSenPlay.setCricleProgressColor(0xff66a6e8);
                            handler.sendEmptyMessage(0);
                        } else {
                            ivSenReadPlay.setVisibility(View.GONE);
                            senReadPlaying.setVisibility(View.VISIBLE);
                            senReadPlaying.setBackgroundResource(R.drawable.sen_stop);
                            senReadPlaying.setCricleProgressColor(0xff66a6e8);
                            maxTime = (int)mediaPlayer.getDuration();
                            senReadPlaying.setMax((int) (maxTime * 0.8));
                            handler.sendEmptyMessage(3);
                        }
                    }
                });
            }
        }

        public Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //LogUtil.e("0是否播放中"+mediaPlayer.isPlaying());
                        LogUtil.e("进度更新position" + positions);
                        if (mediaPlayer != null && mediaPlayer.isPlaying()&&!isSen) {
                            maxTime = (int)mediaPlayer.getDuration();
                            rpbSenPlay.setMax(maxTime);
                            rpbSenPlay.setProgress(playTime);
                            notifyItemChanged(positions);
                            updateProgressThread.run();
                        }
                        break;
                    case 1:
                        rpbSenPlay.setProgress(0);
                        rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
                        playTime = 0;
                        searchInfoBean.getTextData().get(positions).setPlay(false);
                        notifyItemChanged(positions);
                        break;
                    case 2:
                        senReadPlaying.setProgress(0);
                        senReadPlaying.setVisibility(View.INVISIBLE);
                        ivSenReadPlay.setVisibility(View.VISIBLE);
                        playTime = 0;
                        notifyItemChanged(positions);
                        break;
                    case 3:
                        //录音音频播放
                        //LogUtil.e("播放" + mediaPlayer.isPlaying());
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            senReadPlaying.setProgress(playTime);
                            updateProgressThreadSen.run();
                        }
                        break;
                    case 104:
                        //录音音量动画
                        if (!manager.isRecording){
                            return;
                        }
                        rpbSenIRead.setMax(10000);
                        rpbSenIRead.setCricleProgressColor(Color.GREEN);
                        rpbSenIRead.setProgress(msg.arg1);
                        LogUtil.e("录音104 ，音量"+msg.arg1);

                        notifyItemChanged(positions);
                        break;
                    case 6:
                        CustomToast.showToast(mContext, "评测成功", 1000);
                        LogUtil.e("评测成功" + msg.obj);
                        //替换
//                        EvaluatBean evaluatBean =(EvaluatBean) msg.obj;
//                        searchInfoBean.getTextData().get(indexBean).setEvaluatBean(evaluatBean);
                        EvaluationBeanNew evaluatBean =(EvaluationBeanNew) msg.obj;
                        searchInfoBean.getTextData().get(indexBean).setEvaluatBean(evaluatBean);

                        //评测成功后的操作 字体变色 分数显示
                        searchInfoBean.getTextData().get(indexBean).setTest(true);
                        notifyItemChanged(positions);
                        break;
                    case 12:
                        if (msg.arg1==404){
                            CustomToast.showToast(mContext, "评测失败\n未连接到服务器，请检查网络！", 2000);
                        }else {
                            CustomToast.showToast(mContext, "评测失败", 1000);
                        }
                        LogUtil.e("评测失败");
                        break;
                    case 10:
                        if (manager.isRecording) {
//                            ToastUtil.showToast(mContext,"评测中");

                            //这里是第二次点击录音，则停止录音并且重置
                            //之前的操作是上方，但是点击第二次的时候显示存在问题，所以修改下
                            ToastUtil.showToast(mContext,"正在评测中");
                            handler.sendEmptyMessage(11);
                        }
                        break;
                    case 11:
                        if (manager.isRecording) {
                            dismissDia();//关闭录音
                        }
                        break;
                    case 13:
                        //录音结束，进入评测
                        //SoundDialog.setText("评测中");
                        // handler.sendEmptyMessageDelayed(14,6000);//6s还在评测中，就是异常
                        break;
                    case 14:
                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);//录音按钮恢复初始蓝色
                        rpbSenIRead.setProgress(0);
                        break;
                    case 15:
                        if (manager!=null&&manager.isRecording) {
                            dismissDia();//关闭录音
                            rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);//录音按钮恢复初始蓝色
                        }
                        break;
                    case 16:
                        String addScore = String.valueOf(msg.arg1);
                        if (addScore.equals("5")) {
                            String mg = "语音成功发送至口语圈，恭喜您获得了" + addScore + "分";
                            com.iyuba.core.common.widget.dialog.CustomToast.showToast(mContext, mg, 3000);
                        } else {
                            String mg = "语音成功发送至口语圈";
                            com.iyuba.core.common.widget.dialog.CustomToast.showToast(mContext, mg, 3000);
                        }
                        ivSenReadCollect.setVisibility(View.VISIBLE);//分享
                        break;
                    case 17:
                        //增加发布失败后的处理操作
                        if (mWaitingDialog!=null
                                &&mWaitingDialog.isShowing()){
                            mWaitingDialog.dismiss();
                        }
                        ToastUtil.showToast(mContext,"发布出现异常");
                        break;
                    default:
                }
            }
        };

        //得分
        private void setReadScoreViewContent(int score) {
            if (score < 50) {
                senReadResult.setBackgroundResource(R.drawable.sen_score_lower60);
                senReadResult.setText("");
            } else if (score > 80) {
                senReadResult.setText(String.valueOf(score));
                senReadResult.setBackgroundResource(R.drawable.sen_score_higher_80);
            } else {
                senReadResult.setText(String.valueOf(score));
                senReadResult.setBackgroundResource(R.drawable.sen_score_60_80);
            }
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

    }

    //关闭 录音
    public void dismissDia() {
        manager.cancelEvaluate(true);
        manager.stopEvaluate();//关闭录音
        manager.mHandler.sendEmptyMessage(14);//恢复录音按钮
    }
    public interface OnClickListener {

        void onSenPlayClick(String path, int position);
    }

    public void onSetClick(OnClickListener onClickListener) {

        this.mOnClickListener = onClickListener;
    }

}
