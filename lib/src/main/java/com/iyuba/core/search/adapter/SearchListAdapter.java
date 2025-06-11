//package com.iyuba.core.search.adapter;
//
//import static com.youdao.sdk.common.YoudaoSDK.getApplicationContext;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.media.MediaPlayer;
//import android.os.Handler;
//import android.os.Message;
//import android.text.SpannableStringBuilder;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
//import com.facebook.stetho.common.LogUtil;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.Player;
//import com.iyuba.core.common.widget.RoundProgressBar;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.discover.activity.WordContent;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.lil.user.util.LoginUtil;
//import com.iyuba.core.me.pay.RequestCallBack;
//import com.iyuba.core.search.activity.SearchActivity;
//import com.iyuba.core.search.activity.SentenceListActivity;
//import com.iyuba.core.search.activity.VoaListActivity;
//import com.iyuba.core.search.bean.EvaluationBeanNew;
//import com.iyuba.core.search.bean.SearchArticleBean;
//import com.iyuba.core.search.bean.SearchInfoBean;
//import com.iyuba.core.search.bean.SearchSentenceBean;
//import com.iyuba.core.search.request.AddCreditsRequest;
//import com.iyuba.core.search.util.ReadEvaluateManager;
//import com.iyuba.core.search.util.ResultParse;
//import com.iyuba.core.search.util.UtilPostFile;
//import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
//import com.iyuba.lib.R;
//import com.iyuba.play.IJKPlayer;
//
//import org.apache.commons.cli.ParseException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.wechat.favorite.WechatFavorite;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.sharesdk.wechat.moments.WechatMoments;
//
//
///**
// * zh
// * 2018-12-18
// * 搜索列表多布局适配器
// */
//public class SearchListAdapter extends RecyclerView.Adapter {
//    private static final int WORD = 0; //单词
//    private static final int ARTICLE = 1; //文章
//    private static final int SENTENCE = 2; //句子
//    private static final int MORE = 3; //更多
//
//    private SearchInfoBean searchInfoBean;
//    private String key;
//    private Context mContext;
//    private int indexSen = 0;
//    private int indexArt = 0;
//    private OnClickListener mOnClickListener;
//    public Player mPlayer;
//    public MediaPlayer mediaPlayer;
//    //public IJKPlayer mediaPlayer;
//    private int playPosition = -1;
//    private ReadEvaluateManager manager;
//    //private com.iyuba.concept2.widget.cdialog.CustomDialog soundDialog;
//    private CustomDialog mWaitingDialog;
//    private boolean isFrist;
//    //    private ArrayList<EvaluatBean> arrayList;
//    private ArrayList<EvaluationBeanNew> arrayList;
//    private int maxTime;
//
//    public SearchListAdapter(Context context) {
//        mContext = context;
//        //mediaPlayer = new MediaPlayer();
//        IJKPlayer.initNative();
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                LogUtil.e("播放器播放失败：what" + what + "extra" + extra);
//                ToastUtil.showToast(mContext, "音频无法播放");
//                return false;
//            }
//        });
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                maxTime = (int) mp.getDuration();//长度
//            }
//        });
//    }
//
//    public void setData(SearchInfoBean searchInfoBean, String key) {
//        this.searchInfoBean = searchInfoBean;
//        this.key = key;
//        isFrist = true;
//        indexSen = 0;
//        indexArt = 0;
//        playPosition = -1;//设置没有播放的
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (searchInfoBean.getWord() != null && position == 0) {
//            return WORD;
//        } else if (searchInfoBean.getTitleData().size() != 0) {
//            if (searchInfoBean.getTitleToal() != 0 && searchInfoBean.getWord() != null && position == 1) {
//                return MORE;
//            } else if (searchInfoBean.getTitleToal() != 0 && searchInfoBean.getWord() == null && position == 0) {
//                return MORE;
//            }
//            if (searchInfoBean.getWord() != null && position <= searchInfoBean.getTitleData().size() + 1) {
//                return ARTICLE;//文章
//            } else if (searchInfoBean.getWord() == null && position <= searchInfoBean.getTitleData().size() - 1 + 1) {
//                return ARTICLE;
//            }
//            // return ARTICLE;
//        }
//        if (searchInfoBean.getTextData() != null) {
//            if (searchInfoBean.getWord() != null && searchInfoBean.getTitleData() != null && searchInfoBean.getTitleData().size() > 0) {
//                if (position == searchInfoBean.getTitleData().size() + 2) {
//                    return MORE;
//                }
//                return SENTENCE;
//            } else if (searchInfoBean.getWord() != null && searchInfoBean.getTitleToal() == 0) {
//                if (position == 1) {
//                    return MORE;
//                }
//                return SENTENCE;
//            } else if (searchInfoBean.getWord() == null && searchInfoBean.getTitleToal() != 0) {
//                if (position == searchInfoBean.getTitleData().size() + 1) {
//                    return MORE;
//                } else {
//                    return SENTENCE;
//                }
//            } else if (searchInfoBean.getWord() == null && searchInfoBean.getTitleToal() == 0) {
//                if (position == 0) {
//                    return MORE;
//                } else {
//                    return SENTENCE;
//                }
//            }
//            return SENTENCE;
//        }
//        return WORD;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater mInflater = LayoutInflater.from(mContext);
//        switch (viewType) {
//            case WORD:
//                View word = mInflater.inflate(R.layout.search_word_item, parent, false);
//                return new SearchWordViewHolder(word);
//            case SENTENCE:
//                View sen = mInflater.inflate(R.layout.search_sentence_item, parent, false);
//                return new SearchSentenceViewHolder(sen);
//            case ARTICLE://文章
//                View art = mInflater.inflate(R.layout.search_article_item, parent, false);
//                return new SearchArticleViewHolder(art);
//            case MORE:
//                View more = mInflater.inflate(R.layout.search_more_item, parent, false);
//                return new SearchMoreViewHolder(more);
//            default:
//                break;
//        }
//        return new SearchWordViewHolder(mInflater.inflate(R.layout.search_word_item, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        if (holder instanceof SearchWordViewHolder) {
//            ((SearchWordViewHolder) holder).setData();
//            ((SearchWordViewHolder) holder).setListener(position);
//        } else if (holder instanceof SearchSentenceViewHolder) {//句子
//            if (searchInfoBean.getWord() != null) {
//                if (searchInfoBean.getTitleData() != null && searchInfoBean.getTitleToal() != 0) {
//                    indexSen = position - searchInfoBean.getTitleToal() - 1 - 1 - 1;
//                } else {
//                    indexSen = position - 1 - 1;//减去单词和句子头部
//                }
//
//            } else {
//                if (searchInfoBean.getTitleToal() != 0) {
//                    indexSen = position - searchInfoBean.getTitleToal() - 1 - 1;
//                } else {
//                    indexSen = position - 1;
//                }
//            }
//            if (indexSen < 0) {
//                indexSen = 0;
//            }
//            if (indexSen >= 3) {
//                indexSen = 2;
//            }
//            ((SearchSentenceViewHolder) holder).setData(indexSen, position);
//            ((SearchSentenceViewHolder) holder).setListener(indexSen, position);
//
//
//        } else if (holder instanceof SearchArticleViewHolder) {//文章
//            if (searchInfoBean.getWord() != null) {
//                indexArt = position - 1 - 1;
//            } else {
//                indexArt = position - 1;
//            }
//            ((SearchArticleViewHolder) holder).setData(indexArt);
//            ((SearchArticleViewHolder) holder).setListener(indexArt);
//
//        } else if (holder instanceof SearchMoreViewHolder) {
//            ((SearchMoreViewHolder) holder).setData(position);
//            ((SearchMoreViewHolder) holder).setListener(position);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (searchInfoBean != null) {
//            if (searchInfoBean.getWord() != null) {
//                if (searchInfoBean.getTextToal() != 0 && searchInfoBean.getTitleToal() != 0) {
//                    return 1 + searchInfoBean.getTextToal() + searchInfoBean.getTitleToal() + 2;
//                }
//                if (searchInfoBean.getTextToal() == 0 && searchInfoBean.getTitleToal() != 0) {
//                    return 1 + searchInfoBean.getTitleToal() + 1;
//                }
//                if (searchInfoBean.getTextToal() != 0 && searchInfoBean.getTitleToal() == 0) {
//                    return 1 + searchInfoBean.getTextToal() + 1;
//                }
//                if (searchInfoBean.getTextToal() == 0 && searchInfoBean.getTitleToal() == 0) {
//                    return 1;
//                }
//            } else {
//                if (searchInfoBean.getTextToal() != 0 && searchInfoBean.getTitleToal() != 0) {
//                    return searchInfoBean.getTextToal() + searchInfoBean.getTitleToal() + 2;
//                }
//                if (searchInfoBean.getTextToal() == 0 && searchInfoBean.getTitleToal() != 0) {
//                    return searchInfoBean.getTitleToal() + 1;
//                }
//                if (searchInfoBean.getTextToal() != 0 && searchInfoBean.getTitleToal() == 0) {
//                    return searchInfoBean.getTextToal() + 1;
//                }
//                if (searchInfoBean.getTextToal() == 0 && searchInfoBean.getTitleToal() == 0) {
//                    return 0;
//                }
//            }
//        }
//        return 0;
//    }
//
//    public class SearchWordViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView tvWord;
//        private ImageButton ibCollect;
//        private ImageButton ibEnPlay;
//        private TextView tvEnPhoneticSymbol;
//        private ImageButton ibUsPlay;
//        private TextView tvUsPhoneticSymbol;
//        private TextView tvExplain;
//
//        public SearchWordViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvWord = itemView.findViewById(R.id.tv_word);
//            ibCollect = itemView.findViewById(R.id.ib_collect);
//            ibEnPlay = itemView.findViewById(R.id.ib_en_play);
//            tvEnPhoneticSymbol = itemView.findViewById(R.id.tv_en_Phonetic_symbol);
//            ibUsPlay = itemView.findViewById(R.id.ib_us_play);
//            tvUsPhoneticSymbol = itemView.findViewById(R.id.tv_us_Phonetic_symbol);
//            tvExplain = itemView.findViewById(R.id.tv_explain);
//        }
//
//        public void setData() {
//            tvWord.setText(searchInfoBean.getWord());
//            if (searchInfoBean.getPh_en() != null && !searchInfoBean.getPh_en().trim().equals("")) {
//                tvEnPhoneticSymbol.setVisibility(View.VISIBLE);
//                tvEnPhoneticSymbol.setText("英/" + TextAttr.decode(searchInfoBean.getPh_en()) + "/");
//            } else {
//                tvEnPhoneticSymbol.setText("英/");
//            }
//            if (searchInfoBean.getPh_am() != null && !searchInfoBean.getPh_am().trim().equals("")) {
//                tvUsPhoneticSymbol.setVisibility(View.VISIBLE);
//                tvUsPhoneticSymbol.setText("美/" + TextAttr.decode(searchInfoBean.getPh_am()) + "/");
//            } else {
//                tvUsPhoneticSymbol.setText("美/");
//            }
//            if (searchInfoBean.getPh_en_mp3() == null || searchInfoBean.getPh_en_mp3().trim().equals("")) {
//                ibEnPlay.setVisibility(View.INVISIBLE);
//            } else {
//                ibEnPlay.setVisibility(View.VISIBLE);
//            }
//            if (searchInfoBean.getPh_am_mp3() == null || searchInfoBean.getPh_am_mp3().trim().equals("")) {
//                ibUsPlay.setVisibility(View.INVISIBLE);
//            } else {
//                ibUsPlay.setVisibility(View.VISIBLE);
//            }
//            tvExplain.setText(searchInfoBean.getWordCn().trim());
//            if (searchInfoBean.isCollectWord()) {
//                ibCollect.setImageResource(R.drawable.headline_collect);
//            } else {
//                ibCollect.setImageResource(R.drawable.headline_collect_not);
//            }
//            mPlayer = new Player(mContext, null);
//
//        }
//
//        public void setListener(final int position) {
//            ibCollect.setOnClickListener(v -> {
//                if (mOnClickListener != null) {
//                    mOnClickListener.onWordCollectClick(searchInfoBean.getWord(), position);
//                }
//            });
//            itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, WordContent.class);
//                intent.putExtra("word", searchInfoBean.getWord());
//                intent.putExtra("isCollect", false);
//                mContext.startActivity(intent);
//            });
//            ibEnPlay.setOnClickListener(v -> {
//                if (mPlayer.isPlaying()) {
//                    mPlayer.pause();
//                }
//                mPlayer.playUrl(searchInfoBean.getPh_en_mp3());
//            });
//            ibUsPlay.setOnClickListener(v -> {
//                if (mPlayer.isPlaying()) {
//                    mPlayer.pause();
//                }
//                mPlayer.playUrl(searchInfoBean.getPh_am_mp3());
//            });
//        }
//    }
//
//    public class SearchSentenceViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView tvSenIndex;
//        private TextView tvSenEn;
//        private TextView tvSenCh;
//        private TextView chosnWord;
//        private Button wordCommit;
//        private LinearLayout frontView;
//        private RoundProgressBar rpbSenPlay;
//        private RoundProgressBar rpbSenIRead;
//        private ImageView ivSenReadPlay;
//        private RoundProgressBar senReadPlaying;
//        private ImageView ivSenReadSend;
//        private ImageView ivSenReadCollect;
//        private TextView senReadResult;
//        private LinearLayout llChoose;
//
//        private int playTime;
//        private int positions;
//        private int indexBean;
//        private boolean isSen;//录音是否在播放
//        private boolean isUploadVoice = false;//是否正在发送评测录音
//        private String suosuoId;
//        private long beginTime;
//        private boolean isSening;
//
//        public SearchSentenceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvSenIndex = itemView.findViewById(R.id.tv_sen_index);
//            tvSenEn = itemView.findViewById(R.id.tv_sen_en);
//            tvSenCh = itemView.findViewById(R.id.tv_sen_zh);
//            chosnWord = itemView.findViewById(R.id.chosn_word);
//            wordCommit = itemView.findViewById(R.id.word_commit);
//            frontView = itemView.findViewById(R.id.front_view);
//            rpbSenPlay = itemView.findViewById(R.id.rpb_sen_play);
//            rpbSenIRead = itemView.findViewById(R.id.rpb_sen_i_read);//录音 进度
//            ivSenReadPlay = itemView.findViewById(R.id.iv_sen_read_play);//播放录音按钮图片，
//            senReadPlaying = itemView.findViewById(R.id.sen_read_playing);//播放录音进度
//            ivSenReadSend = itemView.findViewById(R.id.iv_sen_read_send);
//            ivSenReadCollect = itemView.findViewById(R.id.iv_sen_read_collect);
//            senReadResult = itemView.findViewById(R.id.sen_read_result);
//            llChoose = itemView.findViewById(R.id.ll_choose);
//        }
//
//        public void setData(int index, int position) {
//            positions = position;
//            if (playPosition == -1) {
//                playTime = 0;
//            }
//            mWaitingDialog = WaittingDialog.showDialog(mContext);//加载过度
//            tvSenEn.setText(searchInfoBean.getTextData().get(index).getSentence());
//            tvSenCh.setText(searchInfoBean.getTextData().get(index).getSentence_cn());
//            tvSenIndex.setText(String.valueOf(index + 1));
//            if (index == 0 && isFrist) {
//                searchInfoBean.getTextData().get(index).setClick(true);
//                isFrist = false;
//            }
//            if (searchInfoBean.getTextData().get(index).isClick()) {
//                llChoose.setVisibility(View.VISIBLE);
//            } else {
//                llChoose.setVisibility(View.GONE);
//            }
//            if (searchInfoBean.getTextData().get(index).isTest()) {//如果是评测过的
//                if (!isSen) {//如果录音没有在播放
//                    ivSenReadPlay.setVisibility(View.VISIBLE);//播放录音
//                }
//                ivSenReadSend.setVisibility(View.VISIBLE);//发送口语圈
//                senReadResult.setVisibility(View.VISIBLE);//得分
////                EvaluatBean evaluatBean = searchInfoBean.getTextData().get(index).getEvaluateBean();
//                EvaluationBeanNew evaluatBean = searchInfoBean.getTextData().get(index).getEvaluatBean();
//                //写入得分
//                setReadScoreViewContent((int) (Double.valueOf(evaluatBean.getTotal_score()) * 20));
//                //字体变色   //拼接之后的数据
//                String[] floats = new String[evaluatBean.getWords().size()];
//                for (int i = 0; i < evaluatBean.getWords().size(); i++) {
//                    floats[i] = String.valueOf(evaluatBean.getWords().get(i).getScore());
//                    LogUtil.e("======" + floats[i]);
//                }
//                LogUtil.e("======" + evaluatBean.getSentence());
//                SpannableStringBuilder readResult = ResultParse.getSenResultLocal(floats, searchInfoBean.getTextData().get(index).getSentence());
//
//                tvSenEn.setText(readResult);//变色写入
//                if (searchInfoBean.getTextData().get(index).isSendOut()) {
//                    ivSenReadCollect.setVisibility(View.VISIBLE);//分享
//                } else {
//                    ivSenReadCollect.setVisibility(View.GONE);//分享
//                }
//
//            } else {
//                ivSenReadPlay.setVisibility(View.GONE);//播放录音
//                ivSenReadSend.setVisibility(View.GONE);//发送口语圈
//                senReadResult.setVisibility(View.GONE);//得分
//                ivSenReadCollect.setVisibility(View.GONE);//分享
//            }
//            if (!searchInfoBean.getTextData().get(index).isPlay()) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                    mediaPlayer.seekTo(0);
//                }
//                rpbSenPlay.setProgress(0);
//                rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
//            }
//        }
//
//        public void setListener(final int index, final int position) {
//            final SearchSentenceBean bean = searchInfoBean.getTextData().get(index);
//            //评测按钮
//            rpbSenIRead.setOnClickListener(v -> {
//                ((SearchActivity) mContext).checkStudyPermission();
//                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext,
//                        new String[]{Manifest.permission.RECORD_AUDIO})) {
//
//                    return;
//                }
//                //bean.setTest(true);//标记为已经评测
//                rpbSenIRead.setMax(10000);
//                if (!isSening) {
//                    isSening = true;
//                    manager = ReadEvaluateManager.getInstance(mContext);
//
//                    //关闭播放
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        mediaPlayer.seekTo(0);
//                    }
//                    rpbSenPlay.setProgress(0);
//                    rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
//                    senReadPlaying.setProgress(0);
//                    senReadPlaying.setVisibility(View.INVISIBLE);
//                    //ivSenReadPlay.setVisibility(View.VISIBLE);
//                    playTime = 0;
//                    //notifyItemChanged(position);
//
//                    if (!manager.isRecording) {
//                        try {
//                            //soundDialog = SoundDialog.showDialog(mContext, SearchListAdapter.this, "录音中");
//                            beginTime = System.currentTimeMillis() / 1000;
//
//                            //新的句子评测接口所需要的数据
//                            Map<String, String> textParams = new HashMap<String, String>();
//                            textParams.put("type", "familyalbum");
//                            textParams.put("userId", String.valueOf(UserInfoManager.getInstance().getUserId()));
//                            textParams.put("newsId", bean.getVoaId() + "");
//                            textParams.put("paraId", bean.getParaId() + "");
//                            textParams.put("IdIndex", bean.getIdIndex());
//                            String sentence = URLEncoder.encode(bean.getSentence(), "UTF-8").replace("+", "%20");
//                            textParams.put("sentence", sentence);
//                            textParams.put("appId", Constant.APPID);
//                            textParams.put("flg", "0");
//                            textParams.put("wordId", "0");
//
//                            manager.startEvaluate(position, bean.getSentence(), Integer.valueOf(bean.getVoaId()), textParams, handler);
//                            //handler.sendEmptyMessage(104);
//                            indexBean = index;
//                            rpbSenIRead.setBackgroundResource(R.drawable.sen_i_stop);
//                            int time = (int) (Double.valueOf(bean.getEndTiming()) - Double.valueOf(bean.getTiming()));
//                            LogUtil.e("录音 持续时间：" + (int) (time * 1.7 * 1000) + "原始时间" + time);
//                            if (time < 0) {
//                                time = 20000;
//                                LogUtil.e("录音 持续时间：异常，修改为" + time);
//                            }
//                            //有的时间太短
//                            //这里是将原文句子的时间*1.7进行处理
//                            handler.sendEmptyMessageDelayed(11, (int) (time * 1.7 * 1000));//延时1.5倍关闭·
//
//                        } catch (ParseException | UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        manager.stopEvaluate();
//                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);
//                    }
//                } else {
//                    isSening = false;
//                    handler.sendEmptyMessage(10);
//                }
//            });
//            //句子原始音频播放
//            rpbSenPlay.setOnClickListener(v -> {
//                if (manager != null && manager.isRecording) {
//                    ToastUtil.showToast(mContext, "正在录音");
//                    return;
//                }
//                if (mediaPlayer.isPlaying()) {
//                    if (isSen) {
//                        //关闭播放
//                        if (mediaPlayer.isPlaying()) {
//                            mediaPlayer.pause();
//                            mediaPlayer.seekTo(0);
//                        }
//                        senReadPlaying.setProgress(0);
//                        senReadPlaying.setVisibility(View.INVISIBLE);
//                        ivSenReadPlay.setVisibility(View.VISIBLE);
//                        playTime = 0;
//                        playPosition = -1;
//                        isSen = false;
//                    }
//                    if (playPosition == position) {
//                        LogUtil.e("播放器暂停");
//                        mediaPlayer.pause();//声音暂停
//                        //进度暂停
//                    } else {
//                        startPlay(position, index, true);//本条初始播放
//                        //handler.sendEmptyMessage(1);//其他条进度复原
//                    }
//
//                } else {
//                    if (playPosition == position) {
//                        if (searchInfoBean.getTextData().get(index).isPlay()) {
//                            LogUtil.e("播放器恢复播放");
//                            mediaPlayer.start();//声音恢复
//                            handler.sendEmptyMessage(0);//进度恢复
//                        } else {
//                            //点击了其他的item造成的暂停，需要重新播放
//                            startPlay(position, index, true);//最初始播放
//                        }
//                    } else {
//                        startPlay(position, index, true);//最初始播放
//                    }
//                }
//            });
//            //句子录音播放
//            ivSenReadPlay.setOnClickListener(v -> {
//                if (manager != null && manager.isRecording) {
//                    ToastUtil.showToast(mContext, "正在录音");
//                    return;
//                }
//                if (searchInfoBean.getTextData().get(index).getEvaluatBean() != null) {
//                    isSen = true;
//                    //关闭播放
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        mediaPlayer.seekTo(0);
//                    }
//                    rpbSenPlay.setProgress(0);
//                    rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
//                    senReadPlaying.setProgress(0);
//                    senReadPlaying.setVisibility(View.INVISIBLE);
//                    ivSenReadPlay.setVisibility(View.VISIBLE);
//                    playTime = 0;
//                    startPlay(position, index, false);
//                }else{
//                    ToastUtil.showToast(mContext,"评测实体为空");
//                }
//            });
//
//            //单句评测发送至排名
//            ivSenReadSend.setOnClickListener(v -> {
//                if (manager != null && manager.isRecording) {
//                    ToastUtil.showToast(mContext, "正在录音");
//                    return;
//                }
//                if (UserInfoManager.getInstance().isLogin()) {
//                    if (isUploadVoice) {
//                        CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
//                    } else {
//                        mWaitingDialog.show();
//                        Thread threadsend = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    isUploadVoice = true;
//                                    String response = "";
//                                    response = UtilPostFile  //单句语音发送
//                                            .post("http://voa." + Constant.IYBHttpHead() + "/voa/UnicomApi?topic=familyalbum"
//                                                    + "&platform=android&format=json&protocol=60003"
//                                                    + "&userid="
//                                                    + UserInfoManager.getInstance().getUserId()
//                                                    + "&voaid="
//                                                    + searchInfoBean.getTextData().get(index).getVoaId()
//                                                    + "&idIndex="
//                                                    + searchInfoBean.getTextData().get(index).getIdIndex()
//                                                    + "&score="
//                                                    // + searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()
//                                                    + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluatBean().getTotal_score()) * 20)
//                                                    + "&shuoshuotype=2"
//                                                    + "&content="
////                                                        + searchInfoBean.getTextData().get(index).getEvaluateBean().getURL());
//                                                    + searchInfoBean.getTextData().get(index).getEvaluatBean().getUrl());
//                                    LogUtil.e("sendRank" + response);
//                                    LogUtil.e("sendRank：内容" + "http://voa." + Constant.IYBHttpHead() + "/voa/UnicomApi?topic=familyalbum"//
//                                            + "&platform=android&format=json&protocol=60003"
//                                            + "&userid="
//                                            + UserInfoManager.getInstance().getUserId()
//                                            + "&voaid="
//                                            + searchInfoBean.getTextData().get(index).getVoaId()
//                                            + "&idIndex=" + searchInfoBean.getTextData().get(index).getIdIndex()
//                                            + "&score="
////                                                + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()) * 20)
//                                            + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluatBean().getTotal_score()) * 20)
//                                            //+ searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()
//                                            + "&shuoshuotype=2"
//                                            + "&content="
////                                                + searchInfoBean.getTextData().get(index).getEvaluateBean().getURL());
//                                            + searchInfoBean.getTextData().get(index).getEvaluatBean().getUrl());
//
//                                    isUploadVoice = false;
//
//                                    JSONObject jsonObjectRoot;
//                                    jsonObjectRoot = new JSONObject(
//                                            response);
//                                    String result = jsonObjectRoot
//                                            .getInt("ResultCode") + "";
//                                    String addscore = jsonObjectRoot
//                                            .getString("AddScore");
//                                    suosuoId = String.valueOf(jsonObjectRoot.getInt("ShuoshuoId"));
////                                        ReadVoiceComment rvc = new ReadVoiceComment(
////                                                VoaDataManager.getInstance().voaTemp,
////                                                mList.get(curPosition));
////
////                                            rvc.id = jsonObjectRoot.getInt("ShuoshuoId") + "";
////                                            rvc.shuoshuo = searchInfoBean.getTextData().get(index).getEvaluateBean().getURL();
////
////                                        mMap.put(curPosition, rvc);
//
//                                    // TODO
//                                    if (result.equals("501") || result.equals("1")) {
//                                        mWaitingDialog.dismiss();
//                                        Message msg = handler
//                                                .obtainMessage();
//                                        msg.what = 16;
//                                        msg.arg1 = Integer
//                                                .parseInt(addscore);
//                                        handler.sendMessage(msg);
//                                    }
//                                } catch (Exception e) {
//                                    isUploadVoice = false;
//                                    LogUtil.e("sendRank 发布异常" + e);
//                                    e.printStackTrace();
//
//                                    //增加处理样式
//                                    Message message = handler.obtainMessage();
//                                    message.what = 17;
//                                    handler.sendMessage(message);
//                                }
//                            }
//                        });
//                        threadsend.start();
//                    }
//                } else {
////                    Intent intent = new Intent();
////                    intent.setClass(mContext, Login.class);
////                    mContext.startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                }
//            });
//
//            //单句分享按钮
//            ivSenReadCollect.setOnClickListener(v -> {
//                PlatformActionListener pal = new PlatformActionListener() {
//
//                    @Override
//                    public void onError(Platform platform, int arg1,
//                                        Throwable arg2) {
//                        CustomToast.showToast(mContext, "分享失败", 1000);
//                    }
//
//                    @Override
//                    public void onComplete(Platform platform, int arg1,
//                                           HashMap<String, Object> arg2) {
//                        int srid = 46;
//                        String name = platform.getName();
//                        if (name.equals(QQ.NAME)
//                                || name.equals(Wechat.NAME)
//                                || name.equals(WechatFavorite.NAME)) {
//                            srid = 45;
//                        } else if (name.equals(QZone.NAME)
//                                || name.equals(WechatMoments.NAME)
//                                || name.equals(SinaWeibo.NAME)) {
//                            srid = 46;
//                        }
//                        if (UserInfoManager.getInstance().isLogin()) {
//                            RequestCallBack rc = new RequestCallBack() {
//
//                                @Override
//                                public void requestResult(Request result) {
//                                    AddCreditsRequest rq = (AddCreditsRequest) result;
//                                    if (rq.isShareFirstlySuccess()) {
//                                        String msg = "分享成功，增加了"
//                                                + rq.addCredit + "积分，共有"
//                                                + rq.totalCredit + "积分";
//                                        CustomToast.showToast(mContext,
//                                                msg, 3000);
//                                    } else if (rq.isShareRepeatlySuccess()) {
//                                        CustomToast.showToast(mContext,
//                                                "分享成功", 3000);
//                                    }
//                                }
//                            };
//                            int uid = UserInfoManager.getInstance().getUserId();
//                            AddCreditsRequest rq = new AddCreditsRequest(
//                                    uid,
//                                    //mMap.get(curPosition).getVoaRef().voaId,
//                                    Integer.valueOf(searchInfoBean.getTextData().get(index).getVoaId()),
//                                    srid, rc);
//                            RequestQueue queue = Volley
//                                    .newRequestQueue(mContext);
//                            queue.add(rq);
//                        }
//                    }
//
//                    @Override
//                    public void onCancel(Platform platform, int arg1) {
//                        CustomToast.showToast(mContext, "分享已取消", 1000);
//                    }
//                };
//                String name = Constant.APPName;
//                String shareTitle = "[" + UserInfoManager.getInstance().getUserName() + "]"
//                        + "在" + name + "中获得了"
////                            + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluateBean().getTotal_score()) * 20)
//                        + (int) (Double.valueOf(searchInfoBean.getTextData().get(index).getEvaluatBean().getTotal_score()) * 20)
//                        + "分";
//                String shortText = searchInfoBean.getTextData().get(index).getSentence();
//
//                //这里可能需要替换，先准备下
//                String ArticleShareUrl = "http://voa." + Constant.IYBHttpHead() + "/voa/play.jsp?id=" + suosuoId;
////                    String ArticleShareUrl = "http://voa." + Constant.IYBHttpHead() + "/voa/play.jsp?id=" + suosuoId
////                            + "&apptype=" + Constant.TOPICID + "&addr=" + searchInfoBean.getTextData().get(index).getEvaluateBean().getURL();
//
//                showShare(mContext, shareTitle, shortText, ArticleShareUrl,
//                        Constant.AppIcon(), "很不错的应用，大家快来使用呀！", Constant.APPName,
//                        ArticleShareUrl, pal);
//                LogUtil.e("shareShow" + shareTitle);
//                LogUtil.e("shareShow" + shortText);
//                LogUtil.e("shareShow" + ArticleShareUrl);
//            });
//
//            itemView.setOnClickListener(v -> {
//                if (!bean.isClick()) {
//                    bean.setClick(true);
//                    for (int i = 0; i < searchInfoBean.getTextData().size(); i++) {
//                        if (i != index) {
//                            searchInfoBean.getTextData().get(i).setClick(false);
//                            searchInfoBean.getTextData().get(i).setPlay(false);
//                        }
//                    }
//                    //setSource(index);//写入播放器资源
//                    notifyDataSetChanged();//更新全部数据了
//                }
//            });
//        }
//
//        // 将要执行的操作写在线程对象的run方法当中
//        Runnable updateProgressThread = new Runnable() {
//            public void run() {
//                // 播放音频时，计时
//                if (playTime <= maxTime) {
//                    int d = (int) (maxTime / 150);
//                    playTime = playTime + d;
//                    //LogUtil.e("进度" + playTime+"全部时间"+maxTime);
//                    handler.sendEmptyMessageDelayed(0, maxTime / 150);
//                }
//            }
//        };
//
//        Runnable updateProgressThreadSen = new Runnable() {
//            public void run() {
//                // 播放音频时，计时
//                if (playTime <= maxTime) {
//                    int d = (int) (maxTime / 150);
//                    playTime = playTime + d;
//                    //LogUtil.e("进度" + playTime + " 总时间" + maxTime);
//                    handler.sendEmptyMessageDelayed(3, (int) (maxTime / 150));
//                }
//            }
//        };
//
//        //重新初始化播放
//        private void startPlay(final int position, final int index, final boolean isRpbSen) {
//            try {
//                mediaPlayer.reset();
//                if (isRpbSen) {
//                    mediaPlayer.setDataSource(searchInfoBean.getTextData().get(index).getSoundText());
//                } else {
//                    //这里不知道数据是否正确，需要测试下
////                    mediaPlayer.setDataSource(searchInfoBean.getTextData().get(index).getEvaluatBean().getFilepath());
//                    Log.e("TAG", "startPlay: "+"http://voa."+Constant.IYBHttpHead()+"/voa/" + searchInfoBean.getTextData().get(index).getEvaluatBean().getUrl());
//                    mediaPlayer.setDataSource("http://voa."+Constant.IYBHttpHead()+"/voa/" + searchInfoBean.getTextData().get(index).getEvaluatBean().getUrl());
//                }
//                mediaPlayer.prepareAsync();
//            } catch (IOException e) {
//                e.printStackTrace();
//                ToastUtil.showToast(mContext,"播放器初始化错误，无法正常播放");
//                LogUtil.e("播放器初始化异常" + e);
//            }
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    LogUtil.e("播放器成功播放" + mediaPlayer.getDuration());
//                    mediaPlayer.start();
//                    playPosition = position;
//
//                    for (SearchSentenceBean sentenceBean : searchInfoBean.getTextData()) {
//                        sentenceBean.setPlay(false);
//                    }
//                    searchInfoBean.getTextData().get(index).setPlay(true);
//
//                    if (isRpbSen) {
//                        rpbSenPlay.setBackgroundResource(R.drawable.sen_stop);
//                        rpbSenPlay.setCricleProgressColor(0xff66a6e8);
//                        handler.sendEmptyMessage(0);
//                    } else {
//                        ivSenReadPlay.setVisibility(View.GONE);
//                        senReadPlaying.setVisibility(View.VISIBLE);
//                        senReadPlaying.setBackgroundResource(R.drawable.sen_stop);
//                        senReadPlaying.setCricleProgressColor(0xff66a6e8);
//                        maxTime = (int) mediaPlayer.getDuration();
//                        senReadPlaying.setMax((int) (maxTime * 0.8));
//                        handler.sendEmptyMessage(3);
//                    }
//                }
//            });
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {//MediaPlayer
//                    playTime = 0;
//                    playPosition = -1;
//                    if (isRpbSen) {
//                        //播放完毕
//                        handler.sendEmptyMessage(1);
//                        searchInfoBean.getTextData().get(index).setPlay(false);
//                    } else {
//                        handler.sendEmptyMessage(2);
//                        handler.sendEmptyMessage(15);
//                        searchInfoBean.getTextData().get(index).setPlay(false);
//                        isSen = false;
//                    }
//                }
//            });
//        }
//
//        public Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 0:
//                        if (mediaPlayer != null && mediaPlayer.isPlaying() && !isSen) {//且录音没有在播放
//                            maxTime = (int) mediaPlayer.getDuration();
//                            rpbSenPlay.setMax(maxTime);
//                            rpbSenPlay.setProgress(playTime);
//                            notifyItemChanged(positions);
//                            updateProgressThread.run();
//                        }
//                        break;
//                    case 1:
//                        rpbSenPlay.setProgress(0);
//                        rpbSenPlay.setBackgroundResource(R.drawable.sen_play);
//                        playTime = 0;
//                        notifyItemChanged(positions);
//                        break;
//                    case 2:
//                        senReadPlaying.setProgress(0);
//                        //senReadPlaying.setBackgroundResource(R.drawable.sen_play);
//                        senReadPlaying.setVisibility(View.INVISIBLE);
//                        ivSenReadPlay.setVisibility(View.VISIBLE);
//                        playTime = 0;
//                        notifyItemChanged(positions);
//                        break;
//                    case 3:
//                        //录音音频播放
//                        //LogUtil.e("播放" + mediaPlayer.isPlaying());
//                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                            senReadPlaying.setProgress(playTime);
//                            notifyItemChanged(positions);
//                            updateProgressThreadSen.run();
//                        }
//                        break;
//                    case 104:
//                        //录音音量动画
//                        if (!manager.isRecording) {
//                            return;
//                        }
//                        rpbSenIRead.setMax(10000);
//                        rpbSenIRead.setCricleProgressColor(Color.GREEN);
//                        rpbSenIRead.setProgress(msg.arg1);
//                        LogUtil.e("录音104 ，音量" + msg.arg1);
//
//                        notifyItemChanged(positions);
//                        break;
//                    case 6:
//                        //searchInfoBean.getTextData().get(index);
//                        CustomToast.showToast(mContext, "评测成功", 1000);
//                        LogUtil.e("评测成功" + msg.obj);
//                        ivSenReadPlay.setVisibility(View.VISIBLE);
//
//                        EvaluationBeanNew evaluatBean = (EvaluationBeanNew) msg.obj;
//
////                        evaluatBean.setTime((int) (System.currentTimeMillis() / 1000 - beginTime));
//                        searchInfoBean.getTextData().get(indexBean).setEvaluatBean(evaluatBean);
//                        //评测成功后的操作 字体变色 分数显示
//                        searchInfoBean.getTextData().get(indexBean).setTest(true);
//
//                        notifyItemChanged(positions);
//                        break;
//                    case 10:
//                        if (manager.isRecording) {
////                            ToastUtil.showToast(mContext,"评测中");
//
//                            //这里是第二次点击录音，则停止录音并且重置
//                            //之前的操作是上方，但是点击第二次的时候显示存在问题，所以修改下
//                            ToastUtil.showToast(mContext, "正在评测中");
//                            handler.sendEmptyMessage(11);
//                        }
//                        break;
//                    case 11:
//                        //录音终止 进入评测
//                        if (manager.isRecording) {
//                            dismissDia();//关闭录音
//                        }
//                        break;
//                    case 12:
//                        if (msg.arg1 == 404) {
//                            CustomToast.showToast(mContext, "评测失败\n未连接到服务器，请检查网络！", 2000);
//                        } else {
//                            CustomToast.showToast(mContext, "评测失败", 1000);
//                        }
//                        LogUtil.e("评测失败");
//                        break;
//
//                    case 13:
//                        //录音结束，进入评测
//                        //SoundDialog.setText("评测中");
//                        //handler.sendEmptyMessageDelayed(14,6000);//6s还在评测中，就是异常
//                        break;
//                    case 14:
//                        rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);//录音按钮恢复初始蓝色
//                        rpbSenIRead.setProgress(0);
//                        break;
//                    case 15:
//                        if (manager.isRecording) {
//                            dismissDia();//关闭录音
//                            rpbSenIRead.setBackgroundResource(R.drawable.sen_i_read);//录音按钮恢复初始蓝色
//                        }
//                        break;
//                    case 16:
//                        String addScore = String.valueOf(msg.arg1);
//                        if (addScore.equals("5")) {
//                            String mg = "语音成功发送至口语圈，恭喜您获得了" + addScore + "分";
//                            CustomToast.showToast(mContext, mg, 3000);
//                        } else {
//                            String mg = "语音成功发送至口语圈";
//                            CustomToast.showToast(mContext, mg, 3000);
//                        }
//                        ivSenReadCollect.setVisibility(View.VISIBLE);//分享
//                        //notifyItemChanged(positions);
//                        break;
//                    case 17:
//                        //增加发布失败后的处理操作
//                        if (mWaitingDialog != null
//                                && mWaitingDialog.isShowing()) {
//                            mWaitingDialog.dismiss();
//                        }
//                        ToastUtil.showToast(mContext, "发布出现异常");
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//
//        //得分
//        private void setReadScoreViewContent(int score) {
//            if (score < 50) {
//                senReadResult.setBackgroundResource(R.drawable.sen_score_lower60);
//                senReadResult.setText("");
//            } else if (score > 80) {
//                senReadResult.setText(String.valueOf(score));
//                senReadResult.setBackgroundResource(R.drawable.sen_score_higher_80);
//            } else {
//                senReadResult.setText(String.valueOf(score));
//                senReadResult.setBackgroundResource(R.drawable.sen_score_60_80);
//            }
//        }
//
//        private void showShare(Context context, String title, String content, String shareurl,
//                               String imageUrl, String comment, String site, String titleurl,
//                               PlatformActionListener actionListener) {
//            OnekeyShare oks = new OnekeyShare();
//            //关闭sso授权
//            oks.disableSSOWhenAuthorize();
//            // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//            //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//            oks.setTitle(title);
//            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//            oks.setTitleUrl(titleurl);
//            // text是分享文本，所有平台都需要这个字段
//            oks.setText(content);
//            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//            oks.setImageUrl(imageUrl);//确保SDcard下面存在此张图片
//            // url仅在微信（包括好友和朋友圈）中使用
//            oks.setUrl(shareurl);
//            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//            oks.setComment(comment);
//            // site是分享此内容的网站名称，仅在QQ空间使用
//            oks.setSite(site);
//            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//            oks.setSiteUrl(shareurl);
//            if (actionListener != null) oks.setCallback(actionListener);
//            // 启动分享GUI
//            oks.show(context);
//        }
//
//    }
//
//    //关闭 录音
//    public void dismissDia() {
//        //soundDialog.dismiss();
//        manager.cancelEvaluate(true);
//        manager.stopEvaluate();//关闭录音
//        manager.mHandler.sendEmptyMessage(14);
//    }
//
//    public class SearchArticleViewHolder extends RecyclerView.ViewHolder {
//
//        private ImageView ivArticleCover;
//        private TextView tvEnTitle;
//        private TextView tvCnTitle;
//        private TextView tvReadNum;
//
//        public SearchArticleViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            ivArticleCover = itemView.findViewById(R.id.iv_article_cover);
//            tvEnTitle = itemView.findViewById(R.id.tv_en_title);
//            tvCnTitle = itemView.findViewById(R.id.tv_cn_title);
//            tvReadNum = itemView.findViewById(R.id.tv_read_num);
//        }
//
//        //文章
//        public void setData(int position) {
//
//            if (searchInfoBean.getWord() != null) {
//
//            }
//            tvEnTitle.setText(searchInfoBean.getTitleData().get(position).getTitle());
//            tvCnTitle.setText(searchInfoBean.getTitleData().get(position).getTitle_Cn());
//            tvReadNum.setText(searchInfoBean.getTitleData().get(position).getReadCount() + "次阅读");
//            Glide.with(getApplicationContext()).load(searchInfoBean.getTitleData().get(position).getPic()).into(ivArticleCover);
//        }
//
//        public void setListener(final int position) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SearchArticleBean articleBean = searchInfoBean.getTitleData().get(position);
//                    //跳转到文章视频通用模块
//                    Intent intent = AudioContentActivity.getIntent2Me(mContext,
//                            articleBean.getCategory(), articleBean.getTitle(),
//                            articleBean.getTitle_Cn(), articleBean.getPic(), "voa",
//                            articleBean.getVoaId(), articleBean.getSound());
//                    mContext.startActivity(intent);
//                }
//            });
//        }
//    }
//
//    public class SearchMoreViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView mTvMore;
//        private ImageButton mIbMoreClick;
//
//        public SearchMoreViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mTvMore = itemView.findViewById(R.id.tv_more);
//            mIbMoreClick = itemView.findViewById(R.id.ib_more_click);
//        }
//
//        public void setData(int position) {
//            if (searchInfoBean.getTitleToal() > 0 && (position == 0 || position == 1)) {
//                mTvMore.setText("精彩文章");
//                if (searchInfoBean.getTitleToal() >= 3) {
//                    mIbMoreClick.setVisibility(View.VISIBLE);
//                } else {
//                    mIbMoreClick.setVisibility(View.GONE);
//                }
//            } else {
//                mTvMore.setText("精彩句子");
//                if (searchInfoBean.getTextToal() >= 3) {
//                    mIbMoreClick.setVisibility(View.VISIBLE);
//                } else {
//                    mIbMoreClick.setVisibility(View.GONE);
//                }
//            }
//
//        }
//
//        public void setListener(final int position) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (searchInfoBean.getTitleToal() > 0 && (position == 0 || position == 1)) {
//                        if (mIbMoreClick.getVisibility() == View.VISIBLE) {
//                            goVoa();
//                        }
//                    } else {
//                        if (mIbMoreClick.getVisibility() == View.VISIBLE) {
//                            arrayList = new ArrayList<EvaluationBeanNew>();
//                            //保证评测数据能够传递
//                            for (SearchSentenceBean bean : searchInfoBean.getTextData()) {
//                                if (bean.isTest()) {
//                                    //替换数据
//                                    bean.getEvaluatBean().setParaId(bean.getParaId());
//                                    bean.getEvaluatBean().setIdIndex(bean.getIdIndex());
//                                    bean.getEvaluatBean().setVoaId(bean.getVoaId());
//                                    arrayList.add(bean.getEvaluatBean());
//                                }
//                            }
//                            goSentence();
//                        }
//                    }
//                }
//            });
//            //跟上面一样没啥用
//            mIbMoreClick.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (searchInfoBean.getTitleToal() > 0 && (position == 0 || position == 1)) {
//                        if (mIbMoreClick.getVisibility() == View.VISIBLE) {
//                            goVoa();
//                        }
//                    } else {
//                        if (mIbMoreClick.getVisibility() == View.VISIBLE) {
//                            arrayList = new ArrayList<EvaluationBeanNew>();
//                            for (SearchSentenceBean bean : searchInfoBean.getTextData()) {
//                                if (bean.isTest()) {
//                                    //替换数据
//                                    bean.getEvaluatBean().setParaId(bean.getParaId());
//                                    bean.getEvaluatBean().setIdIndex(bean.getIdIndex());
//                                    bean.getEvaluatBean().setVoaId(bean.getVoaId());
//                                    arrayList.add(bean.getEvaluatBean());
//                                }
//                            }
//                            goSentence();
//                        }
//                    }
//                }
//            });
//        }
//
//        private void goSentence() {
//            String keys = "";
//            if (searchInfoBean.getWord() != null) {
//                keys = searchInfoBean.getWord();
//            } else {
//                keys = key;
//            }
//            SentenceListActivity.start(mContext, keys, arrayList);
//            arrayList.clear();
//        }
//
//        private void goVoa() {
//            String keys = "";
//            if (searchInfoBean.getWord() != null) {
//                keys = searchInfoBean.getWord();
//            } else {
//                keys = key;
//            }
//            VoaListActivity.start(mContext, keys);
//        }
//    }
//
//    public interface OnClickListener {
//
//        void onWordCollectClick(String wordKey, int position);
//    }
//
//    public void onSetClick(OnClickListener onClickListener) {
//
//        this.mOnClickListener = onClickListener;
//    }
//
//}
