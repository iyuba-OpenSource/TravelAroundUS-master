package com.iyuba.concept2.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.efs.sdk.base.core.util.NetworkUtil;
import com.facebook.stetho.common.LogUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.gson.Gson;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.CommentListAdapter;
import com.iyuba.concept2.adapter.RankListAdapter;
import com.iyuba.concept2.adapter.ValReadAdapter;
import com.iyuba.concept2.adapter.ViewPagerAdapter;
import com.iyuba.concept2.api.ApiRetrofit;
import com.iyuba.concept2.api.ApiService;
import com.iyuba.concept2.api.RetrofitFactory;
import com.iyuba.concept2.api.data.EvaSendBean;
import com.iyuba.concept2.databinding.LayoutStudyTextBinding;
import com.iyuba.concept2.databinding.StudyBinding;
import com.iyuba.concept2.fragment.NewEvalCorrectPage;
import com.iyuba.concept2.lil.HelpUtil;
import com.iyuba.concept2.lil.event.RefreshUserInfoEvent;
import com.iyuba.concept2.lil.ui.ad.util.show.AdShowUtil;
import com.iyuba.concept2.lil.ui.ad.util.show.banner.AdBannerShowManager;
import com.iyuba.concept2.lil.ui.ad.util.show.banner.AdBannerViewBean;
import com.iyuba.concept2.lil.ui.ad.util.upload.AdUploadManager;
import com.iyuba.concept2.lil.ui.study.eval.EvalAdapter;
import com.iyuba.concept2.lil.ui.study.eval.FixShareUtil;
import com.iyuba.concept2.listener.RequestCallBack;
import com.iyuba.concept2.manager.RecordManager;
import com.iyuba.concept2.manager.SmallRecordManager;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.protocol.AddCreditsRequest;
import com.iyuba.concept2.protocol.CommentRequest;
import com.iyuba.concept2.protocol.CommentResponse;
import com.iyuba.concept2.protocol.DataCollectRequest;
import com.iyuba.concept2.protocol.DataCollectResponse;
import com.iyuba.concept2.protocol.ExpressionRequest;
import com.iyuba.concept2.protocol.FavorUpdateRequest;
import com.iyuba.concept2.protocol.FavorUpdateResponse;
import com.iyuba.concept2.protocol.GetRankInfoRequest;
import com.iyuba.concept2.protocol.GetRankInfoResponse;
import com.iyuba.concept2.sqlite.mode.Comment;
import com.iyuba.concept2.sqlite.mode.RankUser;
import com.iyuba.concept2.sqlite.mode.ReadVoiceComment;
import com.iyuba.concept2.sqlite.mode.StudyPcmFile;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.mode.VoaDetail;
import com.iyuba.concept2.sqlite.op.VoaDetailOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.concept2.util.AdUtil;
import com.iyuba.concept2.util.ConceptApplication;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.concept2.util.NextVideo;
import com.iyuba.concept2.util.RxTimer;
import com.iyuba.concept2.util.SP;
import com.iyuba.concept2.util.ScreenUtils;
import com.iyuba.concept2.util.UtilLightnessControl;
import com.iyuba.concept2.widget.CircleImageView;
import com.iyuba.concept2.widget.MyListView;
import com.iyuba.concept2.widget.RightMenuPopupWindow;
import com.iyuba.concept2.widget.WordCard;
import com.iyuba.concept2.widget.subtitle.SubtitleSum;
import com.iyuba.concept2.widget.subtitle.SubtitleSynView;
import com.iyuba.concept2.widget.subtitle.TextPageSelectTextCallBack;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.protocol.message.RequestSendMssage;
import com.iyuba.core.common.protocol.message.ResponseSendMsg;
import com.iyuba.core.common.service.bgPlayService.Background;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.PlayerNextEvent;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;
import com.iyuba.core.lil.manager.StudyContentManager;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.bean.VoaCollectEntity;
import com.iyuba.core.lil.model.local.bean.VoaListenEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.Chapter_collect;
import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.model.remote.bean.Mix_result;
import com.iyuba.core.lil.model.remote.bean.Publish_result;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_result;
import com.iyuba.core.lil.remote.bean.base.BaseBean_data;
import com.iyuba.core.lil.remote.service.LibAdService;
import com.iyuba.core.lil.ui.base.BaseStudyStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibDateUtil;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.lil.util.LibPermissionDialogUtil;
import com.iyuba.core.lil.util.LibRecordManager;
import com.iyuba.core.lil.util.LibRxTimer;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.core.lil.view.LoadingDialog;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
import com.iyuba.play.ExtendedPlayer;
import com.tencent.vasdolly.helper.ChannelReaderUtil;
import com.yd.saas.base.interfaces.AdViewBannerListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdBanner;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 学习界面内容
 *
 * 使用viewPager+view显示的，所有内容全在这个界面中
 * 评测界面已经使用新的界面处理了，旧的还在保存，等测试通过删除即可
 */
@RuntimePermissions
public class StudyActivity extends BaseStudyStackActivity implements ValReadAdapter.ValReadAdapterInteraction,//AppUpdateCallBack,
        OnHeaderRefreshListener, OnFooterRefreshListener {
    public static StudyActivity instance;
    private String curVoaId;

    private String shareUrl = "";
    // 通用界面
    private ContextMenu contextMenu;
    private ViewPager container = null;// 这个组件，注意这个组件是用来显示左右滑动的界面的，
    // 如果不加载xml布局文件，他是不会显示内容的。
    private ViewPagerAdapter viewPagerAdapter;
    private Context mContext;
    private ImageButton favorButton;
    private Button backButton, rightMenu;
    private boolean isSpeed = false;   //是否启用调速播放器
    private TextView Title, textButton, knowledgeButton, reReadButton, commentButton, today, week, month, note, userImageText, userName, myUsername, userInfo, title, cpReadWords,
            exerciseButton, rankButton, commentLoadMoreTextView, totalTime = null, curTime = null, textView_time;
    private TextView tvRankRefresh;

    private PullToRefreshView refreshView;//排名中的刷新控件

    private TextView textPlaySpeed_05, textPlaySpeed_10, textPlaySpeed_125, textPlaySpeed_15, textPlaySpeed_20;
    private View knowledge, exercise, comment, rank;
    private CircleImageView userImage, myImage;
    private ViewFlipper knowledgeContainer = null;
    private ImageView voaWord, voaAnno, voaImport, voaDifficult;
    private LinearLayout mLlVoaAnno, mLlDifficult;
    private int curSelectActivity = 1;//知识相关
    // 通用变量
    private int currentPage, lastPage = 0;
    private int voaId;
    private VoaOp voaOp;
    private Voa voaTemp;
    private List<VoaDetail> textDetailTemp;
    private VoaDetailOp textDetailOp;
    private SubtitleSum subtitleSum;
    private LayoutInflater inflater;
    public MediaPlayer mp = null;      //评测中使用的播放器
    // info
    private int mode = 0, source = 0;// 循环模式，来源（最新\本地）
    // 原文
    private boolean syncho, isShowChinese;
    private SubtitleSynView textCenter = null;
    private int currParagraph = 1;
    private int lastParegraph = 0;
    private SeekBar lightBar;
    private SeekBar seekBar = null;
    //播放按钮
    private ImageButton pause = null;
    private RelativeLayout functionButton, videoFormer, videoNext, re_CHN, abPlay, re_one_video;
    private ImageView showChineseButton, modeButton;
    private LinearLayout functionLayout;
    private int player_alltime;
    // 生词
    private WordCard card;
    // 跟读
    private double time, time0, time1;
    private double playTime, recordTime;
    private Player mPlayer;
    private boolean isRecord = false;
    private boolean isPlayRecord = false;
    private File soundFile;
    private ImageView smallVoiceValue;
    private SmallRecordManager smallRecordManager;
    private boolean scorllable = true;
    private boolean isPaused = false;
    // handler
    private final static int PROGRESS_CHANGED = 0;
    private final static int BUFFER_CHANGED = 2;
    private final static int NEXT_VIDEO = 3;
    // 练习
    private CustomDialog waittingDialog;
    private ImageView multipleChoice, voaStructure, voaDiffculty;
    private LinearLayout ll_multipleChoice, ll_voaStructure, ll_voaDiffculty;
    private TextView tv_multipleChoice, tv_voaStructure, tv_voaDiffculty;
    private ViewFlipper exerciseContainer = null;
    private int curExerciseActivity = 0;
    // 评论
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    private int curCommentPage = 1;
    private boolean isDownloadAll;
    private Button expressButton;
    private EditText expressEditText;
    private String expressWord;
    private CommentListAdapter commentAdapter;
    private View commentFooter;
    private boolean commentAll = false;
    private int aPositon, bPosition, abState = 0;// aPosition,bPosition分别存放
    // a，b的位置，abState显示现在是a还是b A-B播放
    private ListView listComment, rankListView;
    private ImageButton setModeButton;
    private Button testListenButton;//pressSpeakButton
    private View voiceView;
    private EditText commentEdit;
    private int currMode = 1;// 0是文字评论，1是语音评论
    private ImageView voiceValue;
    private RecordManager rManager;
    private MediaPlayer voiceMediaPlayer;
    private boolean isUploadVoice = false;
    private String beginTime, endTime;
    private String type;
    private String total;
    private String start;
    public String myImgSrc = "";
    public String myScores = "";
    public String myCount = "";
    public String myRanking = "";
    public String result = "";
    public String message = "";
    private RankUser champion;
    private RankListAdapter rankListAdapter;
    private Pattern p;
    private Matcher m;
    public List<RankUser> rankUsers = new ArrayList<RankUser>();

    //评测语音合成
    private TextView tv_read_mix, tv_read_share; //合成 分享
    private TextView imv_current_time, imv_total_time, tv_read_sore;
    private SeekBar imv_seekbar_player;
    private int mp3changTime = 0, totalScore; //变化时长,总得分
    private Boolean isSendSound = false, isMix = false;
    private String shuoshuoId;
    private long mp3TotalTime = 0;

    int index = 1;

    private String composeVoicePath;
    //睡眠模式
    private Handler mSleepHandler;

    RightMenuPopupWindow rightMenuPopupWindow;
    //菜单中的控件
    private TextView tvCollection;
    private TextView tvShare;
    private TextView tvDownPdf;

    //录音文件地址
    private ArrayList<String> web_path_list = new ArrayList<>();

    private List<StudyPcmFile> list_pcm_file_all = new ArrayList<>();

    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
            Log.e("onStartSend", "---");
        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
            Log.e("onStartRecv", "---");
        }

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
            Log.e("onStartConncet", "---");
        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
            Log.e("onSendFinish", "---");
        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {
            handler.sendEmptyMessage(4);
            rankHandler.sendEmptyMessage(7);
        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {
        }
    };

    TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {
        @Override
        public void selectTextEvent(String selectText) {
            if (selectText.matches("^[a-zA-Z]*")) {
                card.setVisibility(View.VISIBLE);
                card.searchWord(selectText, mContext, wordHandler);
                //这里要求点击显示的时候，停止原文（可以考虑下是不是关闭之后可以开启）
                originalSoundControlWrapper(false);
                isPaused = true;
            } else {
                CustomToast.showToast(mContext, R.string.play_please_take_the_word, 1000);
            }
        }

        @Override
        public void selectParagraph(int paragraph) {
            if (isSpeed)
                videoView.seekTo((int) (textDetailTemp.get(paragraph).startTime * 1000));
            else
                videoViewBP.seekTo((int) (textDetailTemp.get(paragraph).startTime * 1000));
            currParagraph = paragraph;
        }
    };

    private int words;

    /*************************************整理的数据**********************************/
    //代码太老，都写在一个界面了，太混乱了(注意以后的处理)
    //界面布局
    private StudyBinding binding;

    //界面集合
    private ArrayList<View> mList;

    /***************原文界面************/
    private View contentLayout;

    //广告控件
    private RelativeLayout adLayout;
    private RelativeLayout iyubaAdLayout;
    private RelativeLayout webAdLayout;
    private ImageView webAdImage;
    private TextView webAdTips;
    private ImageView webAdClose;

    //原文正常播放器
    public BackPlayer videoViewBP = null;
    //原文调速播放器
    public ExtendedPlayer videoView = null;

    /***************评测界面***********/
    private View evalLayout;
    private MyListView senListView;
    private ValReadAdapter valReadAdapter;

    //评测播放器
//    private ExoPlayer evalAudioPlayer;
    private com.iyuba.concept2.util.Player cPlayer;


    /****************排行界面***********/

    /***************知识界面************/

    /**************练习界面*************/

    /*****************更多操作**************/
    //收藏文章操作
    private Disposable collectVoaDis;


    //新的跳转界面操作
    public static void start(Context context,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,StudyActivity.class);
        intent.putExtra("curVoaId",voaId);
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = StudyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        //部分数据操作
        mSleepHandler = new Handler();
        instance = this;
        Intent intent = getIntent();
        curVoaId = intent.getStringExtra("curVoaId");
        mContext = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);//可以设置该Activity中音量控制键控制的音频流
        CrashApplication.getInstance().addActivity(this);
        // 初始化原文信息
        voaOp = new VoaOp(mContext);
        textDetailOp = new VoaDetailOp(mContext);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        beginTime = df.format(new Date());
        subtitleSum = VoaDataManager.Instace().subtitleSum; // 文章mp3，文章中英文
        voaTemp = VoaDataManager.Instace().voaTemp;// 句子操作

        shareUrl = "http://familyusa.iyuba.cn/player_" + voaTemp.art + "_" + voaTemp.lesson + ".html";

        textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;// 句子list
        if (voaTemp == null) {
            voaId = 60;
        } else {
            int id = voaTemp.voaId;
            voaId = id;
        }

        getCounts(textDetailTemp);
        waittingDialog = WaittingDialog.showDialog(mContext);
        // 是否让屏幕保持不暗不关闭的
        if (SettingConfig.Instance().isLight()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // 是否获取是否播放时同步
        if (!SettingConfig.Instance().isSyncho()) {
            syncho = false;
        } else {
            syncho = true;
        }
        // 是否显示中文
        try {
            isShowChinese = ConfigManager.Instance().loadBoolean("showChinese");
        } catch (Exception e) {
            isShowChinese = true;
        }
        //
        try {
            mode = ConfigManager.Instance().loadInt("mode");
        } catch (Exception e) {
            mode = 1;
        }
        source = this.getIntent().getIntExtra("source", 0);

        initView();

        //加载新评测的相关内容
        initEvalView();
        initEvalPlayer();

        setVoaData();

        mList = new ArrayList<>();
        mList.add(contentLayout);
        //增加新的评测界面(更换为新的评测界面)
//        mList.add(evalLayout);
        mList.add(newEvalLayout);

        mList.add(rank);
        mList.add(knowledge);
        mList.add(exercise);
//        mList.add(comment);

        viewPagerAdapter = new ViewPagerAdapter(mList);
        ConfigManager.Instance().putString("cur_tab", "fristText");
        initListener();

        //判断是否需要加载广告
        if (!UserInfoManager.getInstance().isVip()
                && !AdBlocker.getInstance().shouldBlockAd()
                && NetworkUtil.isConnected(this)) {
            refreshAd();
        }

        //隐藏评论
        initNewData();
    }

    private void initView() {
        rightMenuPopupWindow = new RightMenuPopupWindow(this);
        tvCollection = rightMenuPopupWindow.getContentView().findViewById(R.id.collection_article);
        tvDownPdf = rightMenuPopupWindow.getContentView().findViewById(R.id.download_pdf);
        tvShare = rightMenuPopupWindow.getContentView().findViewById(R.id.share);
        // 初始化viewpager
        container = findViewById(R.id.mainBody);
        inflater = getLayoutInflater();
        knowledge = inflater.inflate(R.layout.voa_knowledge, null);
        //隐藏了底部的3个导航按钮
        exercise = inflater.inflate(R.layout.voa_exercise, null);
        rank = inflater.inflate(R.layout.rank, null);
        comment = inflater.inflate(R.layout.comment, null);
        commentFooter = inflater.inflate(R.layout.comment_footer, null);
        // 初始化头部
        Title = findViewById(R.id.study_text);
        backButton = findViewById(R.id.button_back);
        rightMenu = findViewById(R.id.right_menu);
        textButton = findViewById(R.id.voa_button_text);
        reReadButton = findViewById(R.id.voa_button_reRead);
        rankButton = findViewById(R.id.voa_rank);
        exerciseButton = findViewById(R.id.voa_button_exercise);
        knowledgeButton = findViewById(R.id.voa_button_knowledge);
        commentButton = findViewById(R.id.voa_button_remark);
        card = findViewById(R.id.word);
        card.setWordCard(mContext);
        card.setVisibility(View.GONE);

        /*************************整体框架**************************/


        /****************************原文界面**********************/
        contentLayout = LayoutStudyTextBinding.inflate(LayoutInflater.from(this)).getRoot();

        //广告控件
        adLayout = contentLayout.findViewById(R.id.adLayout);
        //爱语吧广告和web广告样式
        iyubaAdLayout = contentLayout.findViewById(R.id.iyubaSdkAdLayout);
        webAdLayout = contentLayout.findViewById(R.id.webAdLayout);
        webAdImage = contentLayout.findViewById(R.id.webAdImage);
        webAdTips = contentLayout.findViewById(R.id.webAdTips);
        webAdClose = contentLayout.findViewById(R.id.webAdClose);

        /*****************************评测界面*********************/
        evalLayout = inflater.inflate(R.layout.read, null);

        /****************************排行界面***********************/

        /*****************************知识界面***********************/

        /*****************************练习界面***********************/
    }

    private void initListener() {
        //上方的原文按钮
        textButton.setOnClickListener(v -> {
            ConfigManager.Instance().putString("cur_tab", "fristText");
            container.setCurrentItem(0);
        });
        //上方评测按钮
        reReadButton.setOnClickListener(v -> {
            ConfigManager.Instance().putString("cur_tab", "reRead");
            originalSoundControlWrapper(false);
            commitStudyRecordUnfinish();
            container.setCurrentItem(1);
        });
        //排行榜按钮
        rankButton.setOnClickListener(v -> {
            ConfigManager.Instance().putString("cur_tab", "rank");
            originalSoundControlWrapper(false);
            commitStudyRecordUnfinish();
            container.setCurrentItem(2);
        });
        //知识按钮
        knowledgeButton.setOnClickListener(v -> {
            ConfigManager.Instance().putString("cur_tab", "knowledge");
            originalSoundControlWrapper(false);
            commitStudyRecordUnfinish();
            container.setCurrentItem(3);
        });
        //练习按钮
        exerciseButton.setOnClickListener(v -> {
            ConfigManager.Instance().putString("cur_tab", "exercise");
            originalSoundControlWrapper(false);
            commitStudyRecordUnfinish();
            container.setCurrentItem(4);

            if (MultipleChoiceActivity.instance != null) {
                // TODO
                switch (curExerciseActivity) {
                    case 0:
                        MultipleChoiceActivity.instance.setMultiplechoice();
                        break;
                    case 1:
                    case 2:
                }
            }

        });
        //评论按钮
        commentButton.setOnClickListener(v -> {
            ConfigManager.Instance().putString("cur_tab", "comment");
            originalSoundControlWrapper(false);
            commitStudyRecordUnfinish();
            container.setCurrentItem(5);
        });
        //返回按钮
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                if ("knowledge".equals(ConfigManager.Instance().loadString(
                        "cur_tab"))
                        && curSelectActivity == 0) {
                    flag = VoaWordActivity.instance.changeWordState();
                }
                if (!flag) {
                    tofinish();

                }
            }
        });
        rightMenu.setOnClickListener((View v) -> {
            rightMenuPopupWindow.showAsDropDown(v);
        });
        //增加窗口关闭回调
        card.setOnWindowCloseListener(() -> {
//            originalSoundControlWrapper(true);
//            isPaused = false;
        });
        tvCollection.setOnClickListener(v -> {
            rightMenuPopupWindow.dismiss();
            if (UserInfoManager.getInstance().isLogin()) {
                //更换接口
                /*if (voaTemp.isCollect.equals("0") || voaTemp.isCollect.equals("")) {
                    tvCollection.setText("取消收藏");
                    voaTemp.isCollect = "1";
                    voaOp.insertDataToCollection(voaTemp.voaId);
                    voaOp.updateSynchro(voaTemp.voaId, 0);
                    handler.sendEmptyMessage(1);
                    handler.sendEmptyMessage(3);
                } else {
                    tvCollection.setText("收藏");
                    voaTemp.isCollect = "0";
                    voaOp.deleteDataInCollection(voaId);
                    voaOp.updateSynchro(voaTemp.voaId, 0);
                    handler.sendEmptyMessage(1);
                    handler.sendEmptyMessage(3);
                }*/

                //当前收藏信息
                VoaCollectEntity collectEntity = HelpDataManager.getInstance().getSingleVoaCollectData(UserInfoManager.getInstance().getUserId(), voaTemp.voaId);
                boolean isDel = collectEntity != null;
                //更换接口并进行单独处理
                LessonRemoteManager.collectVoaData(UserInfoManager.getInstance().getUserId(), voaTemp.voaId, isDel)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Chapter_collect>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                collectVoaDis = d;
                            }

                            @Override
                            public void onNext(Chapter_collect bean) {
                                if (bean != null && bean.result > 0) {
                                    //操作处理-收藏/取消收藏
                                    if (isDel) {
                                        //删除操作
                                        HelpDataManager.getInstance().deleteSingleVoaCollectData(UserInfoManager.getInstance().getUserId(), voaTemp.voaId);
                                        ToastUtil.showToast(StudyActivity.this, "取消收藏文章成功");
                                    } else {
                                        //插入操作
                                        VoaCollectEntity newCollectData = new VoaCollectEntity(voaTemp.voaId, UserInfoManager.getInstance().getUserId(), System.currentTimeMillis());
                                        HelpDataManager.getInstance().saveSingleVoaCollectData(newCollectData);
                                        ToastUtil.showToast(StudyActivity.this, "收藏文章成功");
                                    }

                                    //刷新显示
                                    refreshCollectView();
                                } else {
                                    if (isDel) {
                                        ToastUtil.showToast(StudyActivity.this, "取消收藏文章失败，请重试");
                                    } else {
                                        ToastUtil.showToast(StudyActivity.this, "收藏文章失败，请重试");
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (isDel) {
                                    ToastUtil.showToast(StudyActivity.this, "取消收藏文章异常，请重试");
                                } else {
                                    ToastUtil.showToast(StudyActivity.this, "收藏文章异常，请重试");
                                }
                            }

                            @Override
                            public void onComplete() {
                                LibRxUtil.unDisposable(collectVoaDis);
                            }
                        });
            } else {
                pauseReadPlayer();
                LoginUtil.startToLogin(mContext);
            }
        });

        tvShare.setOnClickListener(arg0 -> {
            rightMenuPopupWindow.dismiss();
            if (!NetworkUtil.isConnected(this))
                CustomToast.showToast(mContext,
                        R.string.category_check_network, 1000);
            else {
                try {
                    pauseReadPlayer();
                    showShare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvDownPdf.setOnClickListener(v -> {
            rightMenuPopupWindow.dismiss();
            pauseReadPlayer();

            if (!UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(this);
                return;
            }

            //设置新的操作
            List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "用于下载并保存pdf文件")));
            LibPermissionDialogUtil.getInstance().showMsgDialog(this, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
                @Override
                public void onGranted(boolean isSuccess) {
                    if (isSuccess) {

                        //显示弹窗
                        String[] pdfTypeArray = new String[]{"英文pdf", "双语pdf"};
                        new AlertDialog.Builder(StudyActivity.this)
                                .setTitle("导出类型")
                                .setItems(pdfTypeArray, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                //英文pdf
                                                getPdfDownUrl(true);
                                                break;
                                            case 1:
                                                //双语pdf
                                                getPdfDownUrl(false);
                                                break;
                                        }
                                    }
                                }).create().show();
                    }
                }
            });
        });
    }

    private boolean CheckNetWork() {
        if (NetWorkState.isConnectingToInternet() && NetWorkState.getAPNType() != 1) {
            return true;
        }
        return false;
    }

    public void getCounts(List<VoaDetail> details) {
        if (details != null) {
            words = 0;
            for (VoaDetail detail : details) {
                words += getWordCounts(detail.sentence);
            }
        }
    }

    public int getWordCounts(String content) {
        return content.split(" ").length;
    }

    /**
     * @param switchBl true: 原文播放
     *                 false: 原文播放暂停
     */
    private void originalSoundControlWrapper(boolean switchBl) {
        originalSoundControlBp(switchBl);
        originalSoundControl(switchBl);
    }

    /**
     * @param switchBl true: 原文播放
     *                 false: 原文播放暂停
     */
    private void originalSoundControlBp(boolean switchBl) {
        if (videoViewBP == null) {
            return;
        }
        if (switchBl) {
            videoViewBP.start();
            showPlayNotification(true);
        } else {
            if (videoViewBP.isPlaying()) {
                videoViewBP.pause();
                showPlayNotification(false);
            }
        }
    }

    /**
     * @param switchBl true: 原文播放
     *                 false: 原文播放暂停
     */
    private void originalSoundControl(boolean switchBl) {
        if (videoView == null || !videoView.isInitialized()) {
            return;
        }

        if (switchBl) {
            videoView.start();
            showPlayNotification(true);
        } else {
            if (videoView.isPlaying()) {
                videoView.pause();
                showPlayNotification(false);
            }
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private long bgtime;

    @Override
    protected void onResume() {
        super.onResume();
        Background bindService = BackgroundManager.Instance().bindService;
        isSpeed = SettingConfig.Instance().isSpeedPlayer();//获取播放状态
        if (isSpeed) {//是否播放声音 currentPage 这里不能判空！！！！！
            videoView = bindService.getPlayer();
            videoView.setPlaySpeed(ConfigManager.Instance().loadFloat("playSpeed", 1.0f));
        } else {
            if (bindService != null && bindService.getVvv() != null)
                videoViewBP = bindService.getVvv();
        }
        if (bindService == null || bindService.getMediaPlayer() == null) {//null,未知原因
            mp = new MediaPlayer();
            //CustomToast.showToast(mContext, "评测播放器不存在,创建", 3000);
        } else {
            mp = bindService.getMediaPlayer();//null???
        }

        initText();//原文内容
        initRead();//评测内容
        initRank();//排名内容
        initKnowledge();//知识内容
        initExercise();//练习内容
        initExpression();//评论内容
        initComment();//这个还是评论内容

        controlVideo();//音频播放控制,初始化控制，每次显示页面控制

        viewPagerAdapter.notifyDataSetChanged();
        container.setAdapter(viewPagerAdapter);
        container.setOffscreenPageLimit(1);//预加载
        container.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                /*if (valReadAdapter.videoView != null && valReadAdapter.videoView.isPlaying()) {
                    //valReadAdapter.stopAll();
                    valReadAdapter.videoView.pause();
                    showPlayNotification(false);
                }*/

                //不是第一个时，停止音频播放
                if (arg0 != 0) {
                    isPaused = true;
                    pause.setBackgroundResource(R.drawable.image_play);
                    try {
                        if (isSpeed) {
                            videoView.pause();
                        } else {
                            videoViewBP.pause();
                        }
                        showPlayNotification(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /*if (arg0 != 1) {
                    if (valReadAdapter.defaultData(voaTemp, true)) {
                        index = 1;
                    }
                    if (mp != null && mp.isPlaying()) {
                        mp.pause();
                    }
                } else {
                    valReadAdapter.defaultData(voaTemp, false);
                }*/

                //评测处理
                if (arg0!=1){
                    stopAllEvalAudio();
                }

                currentPage = container.getCurrentItem();
                setBackGround(currentPage);
                card.setVisibility(View.GONE);
                if (currentPage == 1) {
                    valReadAdapter.setClickPosition(0);
                    valReadAdapter.notifyDataSetChanged();
                } else {
                    if (cPlayer != null && cPlayer.isPlaying()) {
                        cPlayer.stopPlay();
                    }
                }

                lastPage = currentPage;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (arg0 == 4) {
                    voice_handler.removeMessages(0);
                    voice_handler.sendEmptyMessage(1);
                    if (rManager != null) {
                        rManager.stopRecord();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        container.setCurrentItem(currentPage);
        Title.setText(voaTemp.title);
        setBackGround(currentPage);
        new initExtendedPlayerOnly().execute();
        registerReceiver(rpl, new IntentFilter("toreply"));

        //广告设置
        if (UserInfoManager.getInstance().isVip() || AdBlocker.getInstance().shouldBlockAd()) {
            adLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void showShare() {
        ReadVoiceComment rvc = new ReadVoiceComment(voaTemp, true);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(rvc.getShareTitle());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(shareUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(rvc.getShareShortText());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(rvc.getShareImageUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APPName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用

        oks.setSiteUrl(shareUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {

            private int getPlatformSrid(Platform platform) {
                int srid = 19;
                String name = platform.getName();
                if (name.equals(QQ.NAME) || name.equals(Wechat.NAME)
                        || name.equals(WechatFavorite.NAME)) {
                    srid = 49;
                } else if (name.equals(QZone.NAME) || name.equals(WechatMoments.NAME)
                        || name.equals(SinaWeibo.NAME)) {
                    srid = 19;
                }
                return srid;
            }

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                LogUtil.e("okCallbackonError", "onError");
                LogUtil.e("--分享失败===", arg2.toString());
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");
                if (UserInfoManager.getInstance().isLogin()) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();
                    msg.what = getPlatformSrid(arg0);
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                LogUtil.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean flag = false;

        if (event.getAction() != KeyEvent.ACTION_UP) {
            if ("exercise".equals(ConfigManager.Instance()
                    .loadString("cur_tab"))) {
                switch (curExerciseActivity) {
                    case 1:
                        flag = VoaStructureExerciseActivity.instance
                                .dispatchKeyEvent(event);
                        break;
                    case 2:
                        flag = VoaDiffcultyExerciseActivity.instance
                                .dispatchKeyEvent(event);
                        break;
                }
            } else if ("knowledge".equals(ConfigManager.Instance().loadString(
                    "cur_tab"))
                    && curSelectActivity == 0) {
                flag = VoaWordActivity.instance.changeWordState();
            }

            if (!flag) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (functionLayout.getVisibility() == View.VISIBLE) {
                        functionLayout.setVisibility(View.GONE);
                    } else {
                        tofinish();
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onDestroy() {
        LogUtils.e("Tag--", "onDestroy");
        if (!isPaused) {
            new Thread(new UpdateStudyRecordunfinishThread()).start();
        }
        try {
            unregisterReceiver(rpl);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Glide.with(getApplicationContext()).onDestroy();

        //关闭广告
        AdBannerShowManager.getInstance().stopBannerAd();
        //关闭计时器
        stopAdTimer();

        if (valReadAdapter != null) {
            valReadAdapter.stopAll();
            if (valReadAdapter.videoView != null)
                valReadAdapter.videoView.seekTo(0);//进度到0
        }

        if (commentAdapter != null)
            commentAdapter.stopVoices();
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        commentHandler.removeCallbacksAndMessages(null);
        rankHandler.removeCallbacksAndMessages(null);
        wordHandler.removeCallbacksAndMessages(null);
        videoHandler.removeCallbacksAndMessages(null);
        handlerRead.removeCallbacksAndMessages(null);
        handlerText.removeCallbacksAndMessages(null);
        voice_handler.removeCallbacksAndMessages(null);

        if (cPlayer != null) {
            cPlayer.reset();
        }
        //mSleepHandler.removeCallbacksAndMessages(null);

        File file = new File(Constant.getsimRecordAddr() + "mix"
                + ".mp3");
        if (file != null && file.exists() && file.isFile()) {
            file.delete();
        }


        //这里如果是临时数据，则直接停止即可
        if (StudyContentManager.getInstance().getTempData()) {
            pauseReadPlayer();
        }

        LibRxUtil.unDisposable(collectVoaDis);
    }

    public static StudyActivity newInstance() {
        return instance;
    }

    //播放音频
    private void playVideo(boolean finish) {
        String url;
        if (UserInfoManager.getInstance().isVip()) {
            url = Constant.sound_vip() + voaTemp.voaId + Constant.append;
        } else {
            url = Constant.sound() + voaTemp.voaId + Constant.append;
        }
        int netType = NetWorkState.getAPNType();
        // 原文音频的存放路径
        String pathString = Constant.videoAddr + voaTemp.voaId + Constant.append;
        String pathString2 = ConfigManager.Instance().loadString("media_saving_path") +
                File.separator + voaTemp.voaId + Constant.append;

        File fileTemp = new File(pathString2);
        if (!fileTemp.exists()) {
            fileTemp = new File(pathString);
        } else {
            pathString = pathString2;
        }

        // TODO: 2022/12/15 添加首页进来之后同步播放，当点击的是同一个（true）直接获取播放器播放否则
        if (BackgroundManager.Instance().bindService.getTag() == voaId) {
            /*oldPlay();
            if (BackgroundManager.Instance().bindService != null) {
                if (isSpeed){
                    videoView = BackgroundManager.Instance().bindService.getPlayer();

                }else{
                    videoViewBP = BackgroundManager.Instance().bindService.getVvv();
                }
            }*/
        } else {
            BackgroundManager.Instance().bindService.setTag(voaId);
            if (fileTemp.exists()) {
                seekBar.setSecondaryProgress(seekBar.getMax());
                if (isSpeed) {
                    try {
                        videoView.initialize(pathString);
                        videoView.prepareAndPlay();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(mContext, "播放器异常！");
                        LogUtils.e("播放器异常！" + e);
                    }
                } else {
                    videoViewBP.setVideoPath(pathString);
                }
                try {
                    mp.reset();
                    mp.setDataSource(pathString);
                    mp.prepareAsync();
                } catch (Exception e) {
                    resetMediaPlayer(url, false);
                    LogUtils.e("==评测播放器异常4==", e + "俄");
                    e.printStackTrace();
                }

            } else if (netType == 0) {
                if (finish) {
                    CustomToast.showToast(mContext, R.string.study_info4, 1000);
                    source = 1;
                    voaId = voaOp.findDataByBook(1).get(0).voaId;
                    VoaDataManager.Instace().voaDetailsTemp = textDetailOp
                            .findDataByVoaId(voaId);
                    setVoaData();
                } else {
                    if (isSpeed) {
                        handler.sendEmptyMessage(5);
                        videoView.initialize(url);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertAndCancel(
                                        getResources().getString(R.string.alert),
                                        getResources().getString(R.string.study_info1));
                            }
                        });

                        try {
                            videoViewBP.setVideoPath(url);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(mContext, "播放器异常！");
                            LogUtils.e("播放器异常！" + e);
                        }

                    }
                    setVoaData();
                }
            } else if (netType == 1) {
                CustomToast.showToast(mContext, R.string.study_info2, 2000);

                if (isSpeed) {
                    try {
                        videoView.initialize(url);
                        videoView.prepareAndPlay();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(mContext, "播放器异常！");
                        LogUtils.e("播放器异常！" + e);
                    }
                } else {
                    videoViewBP.setVideoPath(url);
                }

                seekBar.setSecondaryProgress(0);
                try {
                    mp.reset();
                    mp.setDataSource(url);
                    mp.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                    resetMediaPlayer(url, false);
                    LogUtils.e("==评测播放器异常5==", e + "俄");
                }
            } else if (netType == 2) {
                if (isSpeed) {
                    try {
                        videoView.initialize(url);
                        videoView.prepareAndPlay();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(mContext, "播放器异常！");
                        LogUtils.e("播放器异常！" + e);
                    }
                    LogUtils.e("播放原文路径" + pathString);
                } else {
                    videoViewBP.setVideoPath(url);
                    LogUtils.e("播放原文路径" + pathString);
                }
                seekBar.setSecondaryProgress(0);

                try {
                    mp.reset();
                    mp.setDataSource(url);
                    mp.prepareAsync();

                } catch (Exception e) {
                    LogUtils.e("==评测播放器异常6==", e + "俄");
                    resetMediaPlayer(url, false);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 老的当重新进入当前页面和后台播放的音频一致的时候的执行方法
     */
    private void oldPlay() {
        /*if (fileTemp.exists()) {
            seekBar.setSecondaryProgress(seekBar.getMax());
            try {
                mp.reset();
                mp.setDataSource(pathString);
                mp.prepare();
            } catch (Exception e) {
                resetMediaPlayer(url, false);
                e.printStackTrace();
            }
            if (finish) {
                if (isSpeed) {
                    try {
                        videoView.initialize(pathString);
                        videoView.prepareAndPlay();//调速播放器
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(mContext, "播放器异常！");
                    }
                } else {
                    try {
                        videoViewBP.setVideoPath(pathString);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("播放器异常！" + e);
                    }
                }

            }
        } else {
            if (!isConnected) {
                LogUtils.e("网络连接", isConnected + "");
                return;
            }
            if (isSpeed) {
                try {
                    mp.reset();
                    mp.setDataSource(url);
                    mp.prepareAsync();
                } catch (Exception e) {
                    resetMediaPlayer(url, false);
                    LogUtils.e("==评测播放器异常2==", e + "俄");
                    e.printStackTrace();
                }

                if (finish || videoView.getCurrentPosition() == 0) {
                    try {
                        videoView.initialize(url);
                        videoView.prepareAndPlay();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(mContext, "播放器异常！");
                        LogUtils.e("播放器异常！" + e);
                    }
                }
            } else {

                try {
                    mp.reset();
                    mp.setDataSource(url);
                    mp.prepareAsync();
                } catch (Exception e) {
                    LogUtils.e("==评测播放器异常3==", e + "俄");
                    resetMediaPlayer(url, false);
                    e.printStackTrace();
                }

                if (finish || videoViewBP.getCurrentPosition() == 0) {
                    videoViewBP.setVideoPath(url);
                }
            }
        }*/
    }

    private void resetMediaPlayer(String url, boolean flag) {
        try {
            mp = null;
            mp = new MediaPlayer();
            mp.reset();
            mp.setDataSource(url);
            if (flag) {
                mp.prepare();
            } else {
                mp.prepareAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化原文
    private void initText() {
        // 初始化 控制信息
        totalTime = contentLayout.findViewById(R.id.total_time);
        curTime = contentLayout.findViewById(R.id.cur_time);
        textCenter = contentLayout.findViewById(R.id.text_center);
        textCenter.setSubtitleSum(subtitleSum);
        textCenter.setTpstcb(tp);
        videoFormer = contentLayout.findViewById(R.id.former_button);
        videoFormer.setOnClickListener(v -> {
            if (currParagraph != 0 && currParagraph != 1) {
                // 将videoView移动到指定的时间
                if (isSpeed)
                    videoView.seekTo((int) VoaDataManager.Instace().voaDetailsTemp
                            .get(currParagraph - 2).startTime * 1000);
                else
                    videoViewBP.seekTo((int) VoaDataManager.Instace().voaDetailsTemp
                            .get(currParagraph - 2).startTime * 1000);
                textCenter.unsnyParagraph();
            } else {
                CustomToast.showToast(mContext, R.string.study_first, 2000);
            }
        });
        videoNext = contentLayout.findViewById(R.id.next_button);
        videoNext.setOnClickListener(v -> {
            if (VoaDataManager.Instace().voaDetailsTemp != null &&
                    currParagraph < VoaDataManager.Instace().voaDetailsTemp.size()) {
                if (isSpeed)
                    videoView.seekTo((int) (VoaDataManager.Instace().voaDetailsTemp
                            .get(currParagraph).startTime * 1000));
                else
                    videoViewBP.seekTo((int) (VoaDataManager.Instace().voaDetailsTemp
                            .get(currParagraph).startTime * 1000));
                textCenter.unsnyParagraph();
            } else {
                CustomToast.showToast(mContext, R.string.study_last, 2000);
            }
        });
        lightBar = (SeekBar) contentLayout.findViewById(R.id.light_bar);
        lightBar.setProgress(UtilLightnessControl.GetLightness(StudyActivity.this));
        lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                UtilLightnessControl.setActivityBrightness(progress, StudyActivity.this);
            }
        });
        seekBar = contentLayout.findViewById(R.id.seek_bar);
        seekBar.getParent().requestDisallowInterceptTouchEvent(true);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    if (isSpeed) {
                        videoView.seekTo(progress);
                        currParagraph = subtitleSum.getParagraph(videoView
                                .getCurrentPosition() / 1000.0);
                    } else {
                        videoViewBP.seekTo(progress);
                        currParagraph = subtitleSum.getParagraph(videoViewBP
                                .getCurrentPosition() / 1000.0);
                    }
                    lastParegraph = currParagraph - 1;

                    if (currParagraph != 0) {
                        textCenter.snyParagraph(currParagraph);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        abPlay = (RelativeLayout) contentLayout.findViewById(R.id.abplay);// A-B循环播放
        abPlay.setOnClickListener(v -> {
            // abState最开始是0
            abState++;
            if (abState % 3 == 1) {
                if (isSpeed)
                    aPositon = videoView.getCurrentPosition();
                else
                    aPositon = videoViewBP.getCurrentPosition();
                CustomToast.showToast(mContext, R.string.study_ab_a, 2000);
            } else if (abState % 3 == 2) {
                if (isSpeed)
                    bPosition = videoView.getCurrentPosition();
                else
                    bPosition = videoViewBP.getCurrentPosition();
                handlerText.sendEmptyMessage(1);// 开始A-B循环
                CustomToast.showToast(mContext, R.string.study_ab_b, 1000);
            } else if (abState % 3 == 0) {
                handlerText.sendEmptyMessage(0);// 手动停止
            }
        });
        showChineseButton = (ImageView) contentLayout.findViewById(R.id.CHN);
        setShowChineseButton();
        re_CHN = contentLayout.findViewById(R.id.re_CHN);
        re_CHN.setOnClickListener(arg0 -> {
            isShowChinese = !isShowChinese;
            ConfigManager.Instance().putBoolean("showChinese",
                    isShowChinese);

            setShowChineseButton();
        });

        if ((videoView != null && !videoView.isPlaying()) || (videoViewBP != null && !videoViewBP.isPlaying())) {
            isPaused = true;
        }

        //播放按钮
        pause = (ImageButton) contentLayout.findViewById(R.id.video_play);
        pause.setOnClickListener(arg0 -> {
            isPaused = !isPaused;
            if (isPaused) {
                //暂停播放
                new Thread(new UpdateStudyRecordunfinishThread()).start();
                pause.setBackgroundResource(R.drawable.image_play);
                try {
                    if (isSpeed) {
                        videoView.pause();
                    } else {
                        videoViewBP.pause();
                    }
                    showPlayNotification(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //开始播放重置beginTime
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
                beginTime = df.format(new Date());

                //过度加载视图
                exerciseHandler.obtainMessage(1);
                pause.setBackgroundResource(R.drawable.image_pause);

                startSleep();
                if (isSpeed) {
                    try {
                        videoView.start();//第一次异常崩溃
                        showPlayNotification(true);
                    } catch (Exception e) {
                        LogUtils.e("播放器异常！崩溃 重新初始化");
                        resetVideoView();
                    }
                } else {
                    videoViewBP.start();
                    showPlayNotification(true);
                }

            }
        });
        //播放模式
        re_one_video = (RelativeLayout) contentLayout.findViewById(R.id.re_one_video);
        re_one_video.setOnClickListener(arg0 -> {
            mode = (mode + 1) % 3;
            ConfigManager.Instance().putInt("mode", mode);
            setModeBackground();

            if (mode == 0) {
                CustomToast.showToast(mContext, R.string.study_repeatone,
                        1000);
            } else if (mode == 1) {
                CustomToast
                        .showToast(mContext, R.string.study_follow, 1000);
            } else if (mode == 2) {
                CustomToast
                        .showToast(mContext, R.string.study_random, 1000);
            }
        });
        modeButton = (ImageView) contentLayout.findViewById(R.id.one_video);
        //功能设置
        functionLayout = (LinearLayout) contentLayout.findViewById(R.id.more_function_layout);
        functionButton = (RelativeLayout) contentLayout.findViewById(R.id.function_button);
        functionButton.setOnClickListener(arg0 -> {
            if (functionLayout.getVisibility() == View.VISIBLE) {
                functionLayout.setVisibility(View.GONE);
            } else {
                functionLayout.setVisibility(View.VISIBLE);
            }
        });

        OnClickListener onClickListener = v -> {
            float speed = 0.0f;
            switch (v.getId()) {
                case R.id.text_play_speed_05:
                    speed = 0.5f;
                    changeSpeed(speed, (TextView) v);
                    break;
                case R.id.text_play_speed_10:
                    speed = 1.0f;
                    changeSpeed(speed, (TextView) v);
                    break;
                case R.id.text_play_speed_125:
                    speed = 1.25f;
                    changeSpeed(speed, (TextView) v);
                    break;
                case R.id.text_play_speed_15:
                    speed = 1.5f;
                    changeSpeed(speed, (TextView) v);

                    break;
                case R.id.text_play_speed_20:
                    speed = 2.0f;
                    changeSpeed(speed, (TextView) v);
                    break;
            }
        };
        textPlaySpeed_05 = (TextView) contentLayout.findViewById(R.id.text_play_speed_05);
        textPlaySpeed_10 = (TextView) contentLayout.findViewById(R.id.text_play_speed_10);
        textPlaySpeed_125 = (TextView) contentLayout.findViewById(R.id.text_play_speed_125);
        textPlaySpeed_15 = (TextView) contentLayout.findViewById(R.id.text_play_speed_15);
        textPlaySpeed_20 = (TextView) contentLayout.findViewById(R.id.text_play_speed_20);

        textPlaySpeed_05.setOnClickListener(onClickListener);
        textPlaySpeed_10.setOnClickListener(onClickListener);
        textPlaySpeed_125.setOnClickListener(onClickListener);
        textPlaySpeed_15.setOnClickListener(onClickListener);
        textPlaySpeed_20.setOnClickListener(onClickListener);

        if (UserInfoManager.getInstance().isVip()) {
            if (isSpeed) {

                float currentSpeed = ConfigManager.Instance().loadFloat("playSpeed", 1.0f);
                switch (String.valueOf(currentSpeed)) {

                    case "0.5":
                        changeSpeed(currentSpeed, textPlaySpeed_05);
                        break;
                    case "1.0":
                        changeSpeed(currentSpeed, textPlaySpeed_10);
                        break;
                    case "1.25":
                        changeSpeed(currentSpeed, textPlaySpeed_125);
                        break;
                    case "1.5":
                        changeSpeed(currentSpeed, textPlaySpeed_15);
                        break;
                    case "2.0":
                        changeSpeed(currentSpeed, textPlaySpeed_20);
                        break;

                }


            }
        }
    }

    private void resetVideoView() {
        try {
            videoView = null;
            videoView = BackgroundManager.Instance().bindService.getPlayer();
            videoView.setPlaySpeed(ConfigManager.Instance().loadFloat("playSpeed", 1.0f));
            playVideo(true);//配置播放器
            videoView.start();

            showPlayNotification(true);
        } catch (Exception e) {
            LogUtils.e("播放器异常！" + e);
        }
    }

    private void changeSpeed(float speed, TextView textView) {
        if (UserInfoManager.getInstance().isVip()) {
            if (isSpeed) {
                setDefalt();
                float currentSpeed = speed;
                videoView.setPlaySpeed(currentSpeed);
                ConfigManager.Instance().putFloat("playSpeed", currentSpeed);

                textView.setBackgroundResource(R.drawable.btn_shape_speed_on);//background_speed_on
                textView.setTextColor(Color.parseColor("#ffffff"));
            } else {
                Toast.makeText(mContext, "请在应用设置中启用调速播放器！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "成为VIP用户即可调节播放速度！", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDefalt() {
        textPlaySpeed_05.setBackgroundResource(R.drawable.btn_shape_speed_off);//bacground_speed_off

        textPlaySpeed_10.setBackgroundResource(R.drawable.btn_shape_speed_off);
        textPlaySpeed_125.setBackgroundResource(R.drawable.btn_shape_speed_off);
        textPlaySpeed_15.setBackgroundResource(R.drawable.btn_shape_speed_off);
        textPlaySpeed_20.setBackgroundResource(R.drawable.btn_shape_speed_off);

        textPlaySpeed_05.setTextColor(Color.parseColor("#0077f9"));//#F6B476
        textPlaySpeed_10.setTextColor(Color.parseColor("#0077f9"));
        textPlaySpeed_125.setTextColor(Color.parseColor("#0077f9"));
        textPlaySpeed_15.setTextColor(Color.parseColor("#0077f9"));
        textPlaySpeed_20.setTextColor(Color.parseColor("#0077f9"));

    }

    // 初始化知识
    public void initKnowledge() {
        voaWord = knowledge.findViewById(R.id.voa_words);
        voaAnno = knowledge.findViewById(R.id.voa_annotations);
        mLlVoaAnno = knowledge.findViewById(R.id.ll_voa_annotations);
        voaImport = knowledge.findViewById(R.id.voa_important_sentences);
        voaDifficult = knowledge.findViewById(R.id.voa_diffculty);
        mLlDifficult = knowledge.findViewById(R.id.ll_voa_diffculty);

        OnClickListener ocl = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == voaWord) {
                    if (curSelectActivity != 0) {
                        curSelectActivity = 0;
                    } else {
                        Intent intent = new Intent("presstorefresh");
                        sendBroadcast(intent);
                        return;
                    }
                } else if (v == mLlVoaAnno) {
                    if (curSelectActivity != 1) {
                        curSelectActivity = 1;
                    } else {
                        return;
                    }
                } else if (v == voaImport) {
                    if (curSelectActivity != 2) {
                        curSelectActivity = 2;
                    } else {
                        return;
                    }

                } else if (v == mLlDifficult) {
                    if (curSelectActivity != 3) {
                        curSelectActivity = 3;
                    } else {
                        return;
                    }
                }

                clickTab();
            }
        };

        voaWord.setOnClickListener(ocl);
        voaImport.setOnClickListener(ocl);
        mLlVoaAnno.setOnClickListener(ocl);
        mLlDifficult.setOnClickListener(ocl);

        knowledgeContainer = (ViewFlipper) knowledge
                .findViewById(R.id.knowledgeBody);
        knowledgeContainer.setAnimateFirstView(true);

        clickTab();
    }

    //知识选项卡
    private void clickTab() {
        voaWord.setImageResource(R.drawable.voa_words_normal_new);
        voaAnno.setImageResource(R.drawable.voa_annotations_normal_new);
        voaImport.setImageResource(R.drawable.voa_improtant_sentences_normal_new);
        voaDifficult.setImageResource(R.drawable.voa_diffcult_normal_new);

        switch (curSelectActivity) {
            case 0:
//                setActivity(VoaWordActivity.class, curVoaId);
                voaWord.setImageResource(R.drawable.voa_words_press_new);
                break;
            case 1://课文注释
                VoaDataManager.Instace().showType = 0;
                setActivity(VoaAnnotationActivity.class, curVoaId);
                voaAnno.setImageResource(R.drawable.voa_annotations_press_new);
                break;
            case 2://关键句型
                //setActivity(VoaStructureActivity.class, curVoaId);//
                voaImport.setImageResource(R.drawable.voa_improtent_sentences_press_new);
                break;
            case 3://重点难点-->文化点滴
                VoaDataManager.Instace().showType = 1;
                setActivity(VoaAnnotationActivity.class, curVoaId);//VoaDiffcultyActivity
                voaDifficult.setImageResource(R.drawable.voa_diffcult_press_new);
                break;
        }
    }

    //启动Activity，包括练习下面的选择题
    public void setActivity(Class<?> cls, String voaId) {
        Intent intent = new Intent();
        intent.putExtra("curVoaId", voaId);//curVoaId
        intent.setClass(mContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final Window window = getLocalActivityManager().startActivity(String.valueOf(0), intent);
        final View view = window != null ? window.getDecorView() : null;
        if (view != null) {
            knowledgeContainer.removeAllViews();
            knowledgeContainer.addView(view);
            view.setFocusable(true);
            knowledgeContainer.showNext();
        }
    }

    private void initRank() {
        today = (TextView) rank.findViewById(R.id.rank_today);
        week = (TextView) rank.findViewById(R.id.rank_week);
        month = (TextView) rank.findViewById(R.id.rank_month);
        note = (TextView) rank.findViewById(R.id.rank_note);
        userImageText = (TextView) rank.findViewById(R.id.rank_user_image_text);
        userName = (TextView) rank.findViewById(R.id.rank_user_name);
        myUsername = (TextView) rank.findViewById(R.id.username);
        userImage = (CircleImageView) rank.findViewById(R.id.rank_user_image);
        userInfo = (TextView) rank.findViewById(R.id.rank_info);
        rankListView = (ListView) rank.findViewById(R.id.rank_list);
        myImage = (CircleImageView) rank.findViewById(R.id.my_image);
        refreshView = rank.findViewById(R.id.list_view_refresh);
        tvRankRefresh = rank.findViewById(R.id.tv_refresh);
        refreshView.setOnHeaderRefreshListener(this);
        refreshView.setOnFooterRefreshListener(this);

        cpReadWords = (TextView) rank.findViewById(R.id.champion_read_words);
        today.setSelected(true);
        today.setTextColor(0xffffffff);
        type = "D";
        total = "10";
        start = "0";
        rankListView.addFooterView(commentFooter);
        rankHandler.sendEmptyMessage(0);
        myImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UserInfoManager.getInstance().isLogin()) {
                    /*Intent intent = new Intent();
                    intent.putExtra("uid", String.valueOf(UserInfoManager.getInstance().getUserId()));
                    intent.putExtra("voaId", curVoaId);
                    intent.putExtra("userName", UserInfoManager.getInstance().getUserName());
                    intent.putExtra("userPic", UserInfoManager.getInstance().getUserPic());
                    intent.putExtra("type", "评测详情");
                    intent.setClass(mContext, CommentActivity.class);
                    startActivity(intent);*/

                    CommentActivity.start(
                            mContext,
                            String.valueOf(UserInfoManager.getInstance().getUserId()),
                            UserInfoManager.getInstance().getUserName(),
                            UserInfoManager.getInstance().getUserPic(),
                            curVoaId,
                            "评测详情");
                } else {
                    ToastUtil.showToast(mContext, "请先登录！");
                }

            }
        });
        rankListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            // 当comment不为空且comment.size()不为0且没有完全加载
                            if (scorllable)
                                rankHandler.sendEmptyMessage(0);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });

        rankListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("rankListPress", String.valueOf(position));
                if (position < rankListAdapter.getCount()) {
                    //跳出学习界面关闭音频
                    if (valReadAdapter.videoView.isPlaying()) {
                        valReadAdapter.stopAll();
                    }

                    RankUser rankUser = (RankUser) rankListAdapter.getItem(position);

                    /*Intent intent = new Intent();
                    intent.putExtra("uid", rankUser.getUid());
                    intent.putExtra("voaId", curVoaId);
                    intent.putExtra("userName", rankUser.getName());
                    intent.putExtra("userPic", rankUser.getImgSrc());
                    intent.putExtra("type", "评测详情");
                    intent.setClass(mContext, CommentActivity.class);
                    startActivity(intent);*/

                    CommentActivity.start(mContext,
                            rankUser.getUid(),
                            rankUser.getName(),
                            rankUser.getImgSrc(),
                            curVoaId,
                            "评测详情");
                }
            }
        });

        tvRankRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                waittingDialog.show();
                rankHandler.sendEmptyMessage(0);//加载数据
            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!today.isSelected()) {
                    rankUsers.clear();

                    today.setSelected(true);
                    today.setTextColor(0xffffffff);

                    week.setSelected(false);
                    week.setTextColor(0xffFFB151);

                    month.setSelected(false);
                    month.setTextColor(0xffFFB151);

                    type = "D";

                    note.setText("今日数据本日24:00清零");

                    rankHandler.sendEmptyMessage(0);
                }
            }
        });

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!week.isSelected()) {
                    rankUsers.clear();

                    today.setSelected(false);
                    today.setTextColor(0xffFFB151);

                    week.setSelected(true);
                    week.setTextColor(0xffffffff);

                    month.setSelected(false);
                    month.setTextColor(0xffFFB151);

                    type = "W";

                    note.setText("本周数据周日24:00清零");

                    rankHandler.sendEmptyMessage(0);
                }
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!month.isSelected()) {
                    rankUsers.clear();

                    today.setSelected(false);
                    today.setTextColor(0xffFFB151);

                    week.setSelected(false);
                    week.setTextColor(0xffFFB151);

                    month.setSelected(true);
                    month.setTextColor(0xffffffff);

                    type = "M";

                    note.setText("本月数据月末24:00清零");

                    rankHandler.sendEmptyMessage(0);
                }
            }
        });
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        rankHandler.sendEmptyMessage(0);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        if (UserInfoManager.getInstance().isLogin()) {
            start = "0";
            refreshView.setLastUpdated(ExeRefreshTime.lastRefreshTime("Word"));
            rankHandler.sendEmptyMessage(0);//刷新排名数据
        }
    }

    // 初始化跟读
    private void initRead() {
        // 初始化控制信息--测评
        tv_read_sore = (TextView) evalLayout.findViewById(R.id.tv_read_sore);
        tv_read_share = (TextView) evalLayout.findViewById(R.id.tv_read_share);
        tv_read_mix = (TextView) evalLayout.findViewById(R.id.tv_read_mix);
        cPlayer = new com.iyuba.concept2.util.Player(mContext, null);

        imv_current_time = (TextView) evalLayout.findViewById(R.id.imv_current_time);

        imv_seekbar_player = (SeekBar) evalLayout.findViewById(R.id.imv_seekbar_player);
        imv_seekbar_player.setEnabled(false);

        imv_total_time = (TextView) evalLayout.findViewById(R.id.imv_total_time);

        //合成语音
        tv_read_mix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里需要判断录音操作，还有停止音频播放
                if (valReadAdapter.isEvaluating || valReadAdapter.isRecording()) {
                    ToastUtil.showToast(StudyActivity.this, "正在录音评测中～");
                    return;
                }

                valReadAdapter.stopAll();

                if (tv_read_mix.getText().toString().equals("合成")) {
                    //pmc_file_list.clear();
                    web_path_list.clear();
                    totalScore = 0;
                    mp3TotalTime = 0l;

                    for (int i = 0; i < list_pcm_file_all.size(); i++) {

                        StudyPcmFile studyPcmFile = list_pcm_file_all.get(i);
                        web_path_list.add(studyPcmFile.getFilePath());
                        totalScore += studyPcmFile.getScore();
                        mp3TotalTime += studyPcmFile.getTotalTime();
                    }

                    if (web_path_list.size() <= 1) {
                        ToastUtil.showToast(mContext, "至少读两句方可合成！");
                        return;
                    }

//                   //合成录音
                    requestComposeVoice(web_path_list);
                    Log.e("时间", mp3TotalTime + "");
                } else if (tv_read_mix.getText().toString().equals("试听")) {

                    mp3changTime = 100;
                    playRecord2();
                    handler.sendEmptyMessageDelayed(6, 100);
                }
            }
        });

        tv_read_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里需要判断录音操作，还有停止音频播放
                if (valReadAdapter.isEvaluating || valReadAdapter.isRecording()) {
                    ToastUtil.showToast(StudyActivity.this, "正在录音评测中～");
                    return;
                }

                valReadAdapter.stopAll();

                if (tv_read_share.getText().equals("发布")) {
                    if (!isMix) {
                        ToastUtil.showToast(mContext, "请先合成后再发布");
                        return;
                    }

                    sendSound();
                    //这里不应该直接弄成分享，需要等发布成功之后再分享
//                    tv_read_share.setText("分享");
                } else {
                    showShareSound();
                }
            }
        });

        //评测跟读列表
        senListView = evalLayout.findViewById(R.id.sen_list);
        List<VoaDetail> mList = new ArrayList<VoaDetail>();
        //如果数据多于10条，就先加载10条
        if (textDetailTemp.size() >= 10) {
            for (int i = 0; i < 10; i++) {
                mList.add(textDetailTemp.get(i));
            }
        } else {
            mList.addAll(textDetailTemp);
        }
        mp.seekTo(0);
        mp.pause();
        valReadAdapter = new ValReadAdapter(mContext, mList, voaTemp, mp, videoView, videoViewBP, this);
        valReadAdapter.setOnClickWordListener(word -> {
            if (word.matches("^[a-zA-Z]*")) {
                card.setVisibility(View.VISIBLE);
                card.searchWord(word, mContext, wordHandler);
                //这里要求点击显示的时候，停止原文（可以考虑下是不是关闭之后可以开启）
                originalSoundControlWrapper(false);
                isPaused = true;
            } else {
                CustomToast.showToast(mContext,
                        R.string.play_please_take_the_word, 1000);
            }
        });
        valReadAdapter.setOnEvalClickListener(new ValReadAdapter.OnEvalClickListener() {
            @Override
            public void onStopPlay() {
                if (cPlayer != null && cPlayer.isPlaying()) {
                    cPlayer.stopPlay();
                }
                imv_seekbar_player.setProgress(0);
                imv_current_time.setText("00:00");
            }
        });

        senListView.setAdapter(valReadAdapter);
        senListView.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            if (valReadAdapter.manager.isRecording) {
                return;
            }

            if (valReadAdapter.isEvaluating) {
                ToastUtil.showToast(StudyActivity.this, "评测中");
                return;
            }

            //将上一个数据进行恢复
            valReadAdapter.refreshPreView(position);

            valReadAdapter.setClickPosition(position);
            if (valReadAdapter.videoView.isPlaying()) {
                valReadAdapter.videoView.pause();
                showPlayNotification(false);
            }
            valReadAdapter.notifyDataSetChanged();
        });
        senListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    List<VoaDetail> mList = new ArrayList<VoaDetail>();
                    int num = index * 10;
                    int length = num + 10;
                    if (num < textDetailTemp.size()) {
                        try {
                            for (int i = num; i < length; i++) {
                                mList.add(textDetailTemp.get(i));
                            }
                        } catch (IndexOutOfBoundsException e) {
                            for (int i = num; i < textDetailTemp.size(); i++) {
                                mList.add(textDetailTemp.get(i));
                            }
                        }
//                  // 滚动到最后一行了
                        valReadAdapter.addDate(mList, voaTemp);
                        //Toast.makeText(mContext, "滑到底了" + index, Toast.LENGTH_SHORT).show();
                        index++;
                    }
                }
            }
        });
    }

    /**
     * 合成录音请求
     */
    private void requestComposeVoice(ArrayList<String> pathList) {

        StringBuffer stringBuffer = new StringBuffer();

        for (String str : pathList) {
            stringBuffer.append(str).append(",");
        }
        String path = stringBuffer.toString();
        path = path.substring(0, path.length() - 1);
        Map<String, String> pathParams = new HashMap<String, String>();
        Map<String, String> typeParams = new HashMap<String, String>();
        LogUtils.e("合成原接口网络地址" + path);
        pathParams.put("audios", path);
        typeParams.put("type", "familyalbum");//familyalbum

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(15, TimeUnit.SECONDS).
                readTimeout(15, TimeUnit.SECONDS).
                writeTimeout(15, TimeUnit.SECONDS)
                .build();
        //文本类的
        MultipartBody.Builder urlBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (pathParams != null) {
            for (String key : pathParams.keySet()) {
                if (pathParams.get(key) != null) {
                    urlBuilder.addFormDataPart(key, pathParams.get(key));
                }
            }
        }

        if (typeParams != null) {
            for (String key : typeParams.keySet()) {
                if (typeParams.get(key) != null) {
                    urlBuilder.addFormDataPart(key, typeParams.get(key));
                }
            }
        }
        //String actionUrl = "http://speech." + Constant.IYBHttpHead + "/test/merge/";
//        String actionUrl = "http://ai." + Constant.IYBHttpHead() + "/test/merge/";
        String actionUrl = "http://iuserspeech." + Constant.IYBHttpHead() + ":9001/test/merge/";
        LogUtils.e("合成提交 " + "path:" + path + " familyalbum " + actionUrl);
        // 构造Request->call->执行
        final okhttp3.Request request = new okhttp3.Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
                .url(actionUrl).post(urlBuilder.build())//参数放在body体里
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtil.e("合成请求失败" + e);
                handler.sendEmptyMessage(8);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        LogUtil.e("合成请求返回数据：" + jsonObject);
                        String result = jsonObject.getString("result");
                        if (result.equals("1")) {
                            composeVoicePath = jsonObject.getString("URL");
                            String message = jsonObject.getString("message");
                            LogUtil.e("合成请求请求成功");//返回的试听地址拼接前缀:http://voa.iyuba.cn/voa/
                            handler.sendEmptyMessage(9);
                        } else {
                            handler.sendEmptyMessage(8);
                            LogUtil.e("合成请求请求失败0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("合成请求请求失败" + e);
                        handler.sendEmptyMessage(8);
                    }
                } else {
                    LogUtil.e("合成请求失败2");
                    handler.sendEmptyMessage(8);
                }
            }
        });
    }

    public void playRecord2() {
        if (cPlayer != null && !composeVoicePath.equals("")) {
            if (cPlayer.isIdle()) {

                String filepath = Constant.getsimRecordAddr() + "mix" + ".mp3";

                cPlayer.initialize("http://voa.iyuba.cn/voa/" + composeVoicePath);
                LogUtils.e("合成返回地址： " + "http://voa.iyuba.cn/voa/" + composeVoicePath);
                cPlayer.prepareAndPlay();
            } else if (cPlayer.isCompleted()) {
                cPlayer.start();
            } else if (cPlayer.isInitialized()) {
                cPlayer.prepareAndPlay();
            } else if (cPlayer.isPausing()) {
                cPlayer.start();
            }
        } else {
            ToastUtil.showToast(mContext, "还未合成成功！");
        }
    }

    // 以下评论
    private BroadcastReceiver rpl = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currMode == 1) {
                switchToText();
                expressEditText.findFocus();
                expressEditText.requestFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(expressEditText, 0);
                expressEditText.setText(getResources()
                        .getString(R.string.reply)
                        + intent.getExtras().getString("username") + ":");
                expressEditText.setSelection(expressEditText.length());
            } else if (currMode == 0) {
                switchToVoice();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }
    };


    //评论
    private void initComment() {//expressButton button_express
        voiceMediaPlayer = new MediaPlayer();
        voiceMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        voiceMediaPlayer.setOnPreparedListener(mp -> voiceMediaPlayer.start());

        contextMenu = findViewById(R.id.context_menu);
        setModeButton = comment.findViewById(R.id.setmode);//键盘 输入方式
        //pressSpeakButton =  comment.findViewById(R.id.press_speak); 语音
        testListenButton = comment.findViewById(R.id.test_listen);
        commentEdit = comment.findViewById(R.id.editText_express);
        voiceView = comment.findViewById(R.id.voice_view);
        //pressSpeakButton.setOnTouchListener(voice_otl);
        //setModeButton.setOnClickListener(voice_ocl);
        testListenButton.setOnClickListener(voice_ocl);
        voiceValue = comment.findViewById(R.id.mic_value);
        commentAdapter = new CommentListAdapter(mContext, comments, 0, voiceMediaPlayer);
        //comments.removeAll(comments);

        listComment = (ListView) comment.findViewById(R.id.list_comment);
        listComment.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            final boolean login = UserInfoManager.getInstance().isLogin();
            if (login) {
                final int position = arg2;
                contextMenu.setText(mContext.getResources().getStringArray(
                        R.array.context_menu_comment));
                contextMenu.setCallback(new ResultIntCallBack() {
                    @Override
                    public void setResult(int result) {
                        switch (result) {
                            case 0:
                                Intent intent = new Intent("toreply");
                                if (comments != null && comments.size() > 0 && position < comments.size() && null != comments
                                        .get(position)) {
                                    intent.putExtra("username",
                                            ("null".equals(comments.get(position).username) ?
                                                    comments.get(position).userId :
                                                    comments.get(position).username));
                                    mContext.sendBroadcast(intent);
                                }
                                break;
                            case 1:
                                if (login) {
                                    if (comments != null && comments.size() > 0 && position < comments.size() && null != comments
                                            .get(position)) {
                                        SocialDataManager.Instance().userid = comments
                                                .get(position).userId;
                                        mContext.startActivity(PersonalHomeActivity.buildIntent(mContext,
                                                Integer.parseInt(comments.get(position).userId),
                                                comments.get(position).username,
                                                0));
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
                contextMenu.show();
            } else {
//                startActivity(new Intent(mContext, Login.class));
                LoginUtil.startToLogin(mContext);
            }
        });
        //listComment.addFooterView(commentFooter);
        listComment.setAdapter(commentAdapter);
        listComment.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            //当comment不为空且comment.size()不为0且没有完全加载
                            if (comments != null && comments.size() != 0 && !commentAll) {
                                if (!isDownloadAll) {
                                    commentHandler.sendEmptyMessage(0);//加载
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });

        commentLoadMoreTextView = (TextView) commentFooter
                .findViewById(R.id.comment_loadmore_text);
        commentLoadMoreTextView.setVisibility(View.GONE);
        commentHandler.sendEmptyMessage(3);
    }

    private OnClickListener voice_ocl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setmode:
                    if (currMode == 1) {
                        comment.findViewById(R.id.voicebutton).setVisibility(
                                View.GONE);
                        comment.findViewById(R.id.edittext).setVisibility(
                                View.VISIBLE);
                        currMode = 0;
                        setModeButton
                                .setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
                    } else {
                        comment.findViewById(R.id.voicebutton).setVisibility(
                                View.VISIBLE);
                        comment.findViewById(R.id.edittext)
                                .setVisibility(View.GONE);
                        currMode = 1;
                        setModeButton
                                .setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
                    }
                    break;
                case R.id.test_listen:
                    if (voiceMediaPlayer.isPlaying()) {
                        voiceMediaPlayer.stop();
                    }
                    voiceMediaPlayer.reset();
                    try {
                        voiceMediaPlayer.setDataSource(Constant.voiceCommentAddr);
                        voiceMediaPlayer.prepareAsync();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private OnTouchListener voice_otl = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.press_speak) {

                StudyActivityPermissionsDispatcher.requestEvaluateWithPermissionCheck(StudyActivity.this);


                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO})) {

                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    voice_handler.sendEmptyMessageDelayed(0, 300);
                    // setTextPause(true);
                    // 录音
                    try {
                        File file = new File(Constant.voiceCommentAddr);
                        rManager = new RecordManager(file, voiceValue);
                        rManager.startRecord();
                    } catch (Exception e) {
                        Log.e("onTouch", e.getMessage());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    voice_handler.sendEmptyMessageDelayed(1, 300);
                    rManager.stopRecord();
                }
            } else {
                // comment.findViewById(R.id.voice_view)
                voiceView.setVisibility(View.VISIBLE);
                // comment.findViewById(R.id.voice_view)
                voiceView.setVisibility(View.GONE);
                testListenButton.setVisibility(View.VISIBLE);
                rManager.stopRecord();
            }

            return true;
        }
    };

    // 评论切换到文字输入
    private void switchToText() {
        findViewById(R.id.voicebutton).setVisibility(View.GONE);
        findViewById(R.id.edittext).setVisibility(View.VISIBLE);
        setModeButton
                .setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
        currMode = 0;
    }

    // 评论切换到语音输入
    private void switchToVoice() {
        findViewById(R.id.voicebutton).setVisibility(View.VISIBLE);
        findViewById(R.id.edittext).setVisibility(View.GONE);
        setModeButton.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
        currMode = 1;
    }

    @Override
    public void setDefault() {
        tv_read_mix.setText("合成");
    }

    @Override
    public void getIndex(int position, int score, long adpterTime, String filepath) {
        StudyPcmFile studyPcmFile = new StudyPcmFile();
        studyPcmFile.setCurrIndex(position);
        studyPcmFile.setFilePath(filepath);//现在是网络路径！
        studyPcmFile.setScore(score);
        studyPcmFile.setTotalTime(adpterTime);

        boolean addFlag = true;

        if (list_pcm_file_all.size() > 0) {

            for (int i = 0; i < list_pcm_file_all.size(); i++) {

                if (studyPcmFile.getCurrIndex() == list_pcm_file_all.get(i).getCurrIndex()) {//studyPcmFile.getFilePath().equals(list_pcm_file_all.get(i).getFilePath())
                    list_pcm_file_all.remove(i);
                    list_pcm_file_all.add(i, studyPcmFile);
                    addFlag = false;
                }
            }

            if (addFlag) {
                list_pcm_file_all.add(studyPcmFile);
            }

        } else {
            list_pcm_file_all.add(studyPcmFile);
        }

        //排序
        Collections.sort(list_pcm_file_all, new Comparator<StudyPcmFile>() {
            @Override
            public int compare(StudyPcmFile o1, StudyPcmFile o2) {

                int i = o1.getCurrIndex() - o2.getCurrIndex();
                return i;
            }
        });
    }

    // 获取评论
    class commentThread implements Runnable {
        @Override
        public void run() {
            ClientSession.Instace().asynGetResponse(
                    new CommentRequest(String.valueOf(voaId),
                            String.valueOf(curCommentPage)),
                    new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            CommentResponse commentResponse = (CommentResponse) response;
                            if (commentResponse.resultCode.equals("511")) {
                                comments.clear();
                                comments.addAll(commentResponse.Comments);
                                commentHandler.sendEmptyMessage(5);
                            } else if (commentResponse.resultCode.equals("510")) {
                                commentHandler.sendEmptyMessage(8);
                            } else {
                                commentHandler.sendEmptyMessage(9);
                            }
                        }
                    }, null, mNetStateReceiver);
        }
    }

    public Handler commentHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    commentFooter.setVisibility(View.VISIBLE);
                    commentLoadMoreTextView.setTextSize(Constant.textSize);
                    commentLoadMoreTextView.setTextColor(Constant.normalColor);
                    commentLoadMoreTextView.setText(getResources().getString(
                            R.string.study_on_load));
                    new Thread(new commentThread()).start();
                    break;
                case 1:// 发表评论
                    expressEditText.setText("");
                    String userId = String.valueOf(UserInfoManager.getInstance().getUserId());
                    String username = UserInfoManager.getInstance().getUserName();
                    ClientSession.Instace().asynGetResponse(
                            new ExpressionRequest(userId, String.valueOf(voaId),
                                    expressWord, username),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(BaseHttpResponse response,
                                                       BaseHttpRequest request, int rspCookie) {
                                    waittingDialog.dismiss();
                                    commentHandler.sendEmptyMessageDelayed(4, 1000);
                                }
                            }, null, mNetStateReceiver);
                    break;
                case 3:
                    commentAll = false;
                    commentHandler.sendEmptyMessage(0);
                    break;
                case 4:
                    if (waittingDialog.isShowing()) {
                        waittingDialog.dismiss();
                    }
                    curCommentPage = 1;
                    commentEdit.setText("");
                    comments.clear();
                    commentHandler.sendEmptyMessage(3);
                    break;
                case 5:
                    commentAdapter.notifyDataSetChanged();
                    break;
                case 10:
                    String addscore = String.valueOf(msg.arg1);
                    if (addscore.equals("5")) {
                        String mg = "语音成功发送至口语圈，恭喜您获得了" + addscore + "分";
                        CustomToast.showToast(mContext, mg, 3000);
                    } else {
                        String mg = "语音成功发送至口语圈";
                        CustomToast.showToast(mContext, mg, 3000);
                    }
                    valReadAdapter.notifyDataSetChanged();
                    break;
                case 11:
                    CustomToast
                            .showToast(
                                    mContext,
                                    "请录音后，再发送。",
                                    3000);
                    if (waittingDialog.isShowing())
                        waittingDialog.dismiss();
                    break;
                case 12:
                    if (waittingDialog.isShowing())
                        waittingDialog.dismiss();
                    ToastUtil.showToast(mContext, "发送失败！");
                    break;
                default:
                    break;
            }
        }
    };

    //练习模块
    private void initExercise() {

        ll_multipleChoice = (LinearLayout) exercise.findViewById(R.id.ll_multiple_choice);
        ll_voaStructure = (LinearLayout) exercise.findViewById(R.id.ll_voa_structure);
        ll_voaDiffculty = (LinearLayout) exercise.findViewById(R.id.ll_voa_diffculty);

        tv_multipleChoice = (TextView) exercise.findViewById(R.id.tv_multiple_choice);
        tv_voaStructure = (TextView) exercise.findViewById(R.id.tv_voa_structure);
        tv_voaDiffculty = (TextView) exercise.findViewById(R.id.tv_voa_diffculty);

        multipleChoice = (ImageView) exercise.findViewById(R.id.multiple_choice);
        voaStructure = (ImageView) exercise.findViewById(R.id.voa_structure);
        voaDiffculty = (ImageView) exercise.findViewById(R.id.voa_diffculty);

        OnClickListener exerciseListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == ll_multipleChoice) {
                    if (curExerciseActivity != 0) {
                        curExerciseActivity = 0;
                    } else {
                        return;
                    }
                } else if (v == ll_voaStructure) {
                    if (curExerciseActivity != 1) {
                        curExerciseActivity = 1;
                    } else {
                        return;
                    }
                } else if (v == ll_voaDiffculty) {
                    if (curExerciseActivity != 2) {
                        curExerciseActivity = 2;
                    } else {
                        return;
                    }
                }

                clickExerciseTab();
            }
        };

        ll_multipleChoice.setOnClickListener(exerciseListener);
        ll_voaStructure.setOnClickListener(exerciseListener);
        ll_voaDiffculty.setOnClickListener(exerciseListener);
        exerciseContainer = (ViewFlipper) exercise.findViewById(R.id.exerciseBody);
        exerciseContainer.setAnimateFirstView(true);
        clickExerciseTab();
    }

    private void clickExerciseTab() {
        multipleChoice.setImageResource(R.drawable.multiple_choice_normal_new);
        voaStructure.setImageResource(R.drawable.voa_improtant_sentences_normal_new);
        voaDiffculty.setImageResource(R.drawable.voa_diffcult_normal_new);
        tv_multipleChoice.setTextColor(Color.parseColor("#333333"));
        tv_voaStructure.setTextColor(Color.parseColor("#333333"));
        tv_voaDiffculty.setTextColor(Color.parseColor("#333333"));

        switch (curExerciseActivity) {
            case 0:
                exerciseHandler.obtainMessage(1);
                setExerciseActivity(MultipleChoiceActivity.class, curVoaId);//单项选择题  异常？
                multipleChoice.setImageResource(R.drawable.multiple_choice_press_new);
                tv_multipleChoice.setTextColor(Color.parseColor("#F6B476"));
                exerciseHandler.obtainMessage(0);
                break;
            case 1:
                exerciseHandler.obtainMessage(1);
                setExerciseActivity(VoaStructureExerciseActivity.class, curVoaId);
                voaStructure.setImageResource(R.drawable.voa_improtent_sentences_press_new);
                tv_voaStructure.setTextColor(Color.parseColor("#F6B476"));
                break;
            case 2:
                exerciseHandler.obtainMessage(1);
                setExerciseActivity(VoaDiffcultyExerciseActivity.class, curVoaId);
                voaDiffculty.setImageResource(R.drawable.voa_diffcult_press_new);
                tv_voaDiffculty.setTextColor(Color.parseColor("#F6B476"));
                break;
        }
    }

    public Handler exerciseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    waittingDialog.dismiss();
                    break;
                case 1:
                    waittingDialog.show();
                    break;
            }
        }
    };

    public void setExerciseActivity(Class<?> cls, String voaId) {
        Intent intent = new Intent();
        intent.putExtra("curVoaId", curVoaId);
        intent.setClass(mContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final Window window = getLocalActivityManager().startActivity(String.valueOf(0), intent);
        final View view = window != null ? window.getDecorView() : null;
        if (view != null) {
            exerciseContainer.removeAllViews();
            exerciseContainer.addView(view);
            view.setFocusable(true);
            exerciseContainer.showNext();
        }
    }

    // 评论
    private void initExpression() {
        expressButton = comment.findViewById(R.id.button_express);
        expressEditText = comment.findViewById(R.id.editText_express);

        if (UserInfoManager.getInstance().isLogin()) {
            expressEditText.setHint(getResources().getString(R.string.hint1));
            expressButton.setText(getResources().getString(R.string.send));
            expressEditText.setFocusableInTouchMode(true);

            expressButton.setOnClickListener(v -> {
                if (CheckNetWork()) {
                    waittingDialog.show();
                    if (currMode == 0) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                expressEditText.getWindowToken(), 0);
                        String expressionInput = expressEditText.getText().toString();
                        if (expressionInput.toString().equals("")) {
                            CustomToast.showToast(mContext, R.string.study_input_comment, 1000);
                        } else {
                            expressWord = expressionInput;
                            commentHandler.sendEmptyMessage(1);
                        }
                    } else {
                        if (isUploadVoice) {
                            CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
                        } else {
                            sendMessage();
//                                        Map<String, String> textParams = new HashMap<String, String>();
//                                        Map<String, File> fileParams = new HashMap<String, File>();
//                                        File file = new File( Constant.voiceCommentAddr);
//                                        fileParams.put("content.acc", file);
                        }
                    }
                } else {
                    Toast.makeText(mContext, "请在有网的状态下发送。", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            expressEditText.setHint("请先登录！");
            expressEditText.setFocusableInTouchMode(false);
            expressButton.setText(getResources().getString(R.string.login));
            expressButton.setOnClickListener(v -> {
//                Intent intent = new Intent();
//                intent.setClass(mContext, Login.class);
//                startActivity(intent);
                LoginUtil.startToLogin(mContext);
            });
        }
    }

    private void sendMessage() {
        if (UserInfoManager.getInstance().isLogin()) {
            try {
                String content1 = commentEdit.getText().toString();
                ClientSession.Instace()
                        .asynGetResponse(
                                new RequestSendMssage(
                                        String.valueOf(UserInfoManager.getInstance().getUserId()), voaTemp.voaId, content1),
                                (response, request, rspCookie) -> {
                                    ResponseSendMsg rs = (ResponseSendMsg) response;
                                    if (rs.isSuccess) {
                                        commentHandler.sendEmptyMessage(4);
                                    } else {
                                        commentHandler.sendEmptyMessage(12);
                                    }
                                });
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(mContext, "发送错误！");
            }
        } else {
            ToastUtil.showToast(mContext, "请先登录");
        }
    }

    // 执行resume之后的设置
    private void setBackGround(int item) {
        textButton.setSelected(false);
        textButton.setTextColor(0xff8C8C8C);
        textButton.setTextSize(15);
        rankButton.setSelected(false);
        rankButton.setTextColor(0xff8C8C8C);
        rankButton.setTextSize(15);
        knowledgeButton.setSelected(false);
        knowledgeButton.setTextColor(0xff8C8C8C);
        knowledgeButton.setTextSize(15);
        exerciseButton.setSelected(false);
        exerciseButton.setTextColor(0xff8C8C8C);
        exerciseButton.setTextSize(15);
        reReadButton.setSelected(false);
        reReadButton.setTextColor(0xff8C8C8C);
        reReadButton.setTextSize(15);
        commentButton.setSelected(false);
        commentButton.setTextColor(0xff8C8C8C);
        commentButton.setTextSize(15);

        switch (item) {
            case 0:
                textButton.setSelected(true);
                textButton.setTextColor(Constant.whiteColor);
                textButton.setTextSize(17);
                break;
            case 1:
                reReadButton.setSelected(true);
                reReadButton.setTextColor(Constant.whiteColor);
                reReadButton.setTextSize(17);
                break;
            case 2:
                rankButton.setSelected(true);
                rankButton.setTextColor(Constant.whiteColor);
                rankButton.setTextSize(17);
                break;
            case 3:
                knowledgeButton.setSelected(true);
                knowledgeButton.setTextColor(Constant.whiteColor);
                knowledgeButton.setTextSize(17);
                break;
            case 4:
                exerciseButton.setSelected(true);
                exerciseButton.setTextColor(Constant.whiteColor);
                exerciseButton.setTextSize(17);
                break;
            case 5:
                commentButton.setSelected(true);
                commentButton.setTextColor(Constant.whiteColor);
                commentButton.setTextSize(17);
                break;
        }
    }

    // 设置text的内容
    private void setVoaData() {
        voaTemp = voaOp.findDataById(voaId);
        VoaDataManager.Instace().voaTemp = voaTemp;
        VoaDataManager.Instace().setSubtitleSum(voaTemp, VoaDataManager.Instace().voaDetailsTemp);
        videoHandler.sendEmptyMessage(NEXT_VIDEO);

        //这里将新的数据放在需要首页显示后台播放的地方
        ConfigManager.Instance().putString("bg_title_cn", voaTemp.titleCn);
        ConfigManager.Instance().putString("bg_title_en", voaTemp.title);

        //这里刷新收藏数据显示
        refreshCollectView();

        //这里保存阅读数据
        if (UserInfoManager.getInstance().isLogin()) {
            VoaListenEntity listenEntity = new VoaListenEntity(voaTemp.voaId, UserInfoManager.getInstance().getUserId(), System.currentTimeMillis());
            HelpDataManager.getInstance().saveSingleVoaListenData(listenEntity);
        }

        //刷新排行榜显示
        refreshRankData();

        //刷新评测显示
        refreshEvalData();
    }

    private void RefreshData() {

        subtitleSum = VoaDataManager.Instace().subtitleSum;
        textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;
        getCounts(textDetailTemp);
        currParagraph = 1;
        VoaDataManager.Instace().changeLanguage(!isShowChinese);
        textCenter.setSubtitleSum(subtitleSum);

        if (valReadAdapter != null) {
            valReadAdapter.setAdapter(textDetailTemp, voaTemp);
        }

        comments.removeAll(comments);
        commentHandler.sendEmptyMessage(3);
        commentAdapter.notifyDataSetChanged();
        playVideo(true);
        Title.setText(voaTemp.title);
    }

    private void setSeekbar() {
        int i = 0;
        if (isSpeed)
            i = videoView.getDuration();
        else
            i = videoViewBP.getDuration();
        seekBar.setMax(i);
        i /= 1000;
        int minute = i / 60;
        int second = i % 60;
        minute %= 60;
        totalTime.setText(String.format("%02d:%02d", minute, second));

        player_alltime = i;
    }

    private void setModeBackground() {
        if (mode == 0) {
            modeButton.setImageDrawable(getResources().getDrawable(R.drawable.play_this));
        } else if (mode == 1) {
            modeButton.setImageDrawable(getResources().getDrawable(R.drawable.play_next));
        } else if (mode == 2) {
            modeButton.setImageDrawable(getResources().getDrawable(R.drawable.play_random));
        }
    }

    private void setShowChineseButton() {
        if (isShowChinese) {
            showChineseButton
                    .setImageResource(R.drawable.show_chinese_selected);
            VoaDataManager.Instace().changeLanguage(false);
            textCenter.updateSubtitleView();
        } else {
            showChineseButton.setImageResource(R.drawable.show_chinese);
            VoaDataManager.Instace().changeLanguage(true);
            textCenter.updateSubtitleView();
        }
    }

    private void setLockButton() {
        Handler voice_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        // comment.findViewById(R.id.voice_view)
                        voiceView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

        };
        textCenter.syncho = syncho;
    }

    private void controlVideo() {
        if (isSpeed) {
            //播放器进度监听
            videoView.setOnBufferingUpdateListener((mp, percent) -> {
                Message msg = Message.obtain();
                msg.arg1 = percent;
                msg.what = BUFFER_CHANGED;
                videoHandler.sendMessage(msg);
            });
            //播放器监听 播放完成！
            videoView.setOnCompletionListener(mp -> {
                new Thread(new UpdateStudyRecordThread()).start();
                if (mode == 0) {
                    playVideo(true);//配置播放器
                    textCenter.setSubtitleSum(subtitleSum);
                    // setReadText();

                    //设置重新播放
                    originalSoundControlWrapper(true);
                } else {
                    if (mode == 1) {
                        voaId = new NextVideo(voaId, source, mContext).following();
                    } else if (mode == 2) {
                        voaId = new NextVideo(voaId, source, mContext).nextVideo();
                    }

                    VoaDataManager.Instace().voaDetailsTemp = textDetailOp.findDataByVoaId(voaId);
                    if (VoaDataManager.Instace().voaDetailsTemp != null
                            && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                        setVoaData();
                    }
                }

                setReadLesson(voaId);

                Title.setText(voaTemp.title);
                updateNotification();
                //自动播放下一章节
                EventBus.getDefault().post(new PlayerNextEvent(voaTemp.title, voaTemp.titleCn, voaTemp.lesson, voaTemp.art));
            });
            videoView.setOnPreparedListener(mp -> {
                setSeekbar();
                if (currentPage == 0) {
                    startSleep();
                    try {
                        videoView.start();
                        showPlayNotification(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(mContext, "播放异常！");
                    }
                    //关闭过度加载视图
                    exerciseHandler.obtainMessage(0);
                } else {
                    if (isSpeed) {
                        videoView.pause();
                    } else {
                        videoViewBP.pause();
                    }
                    showPlayNotification(false);
                }
                videoHandler.sendEmptyMessage(PROGRESS_CHANGED);
            });
        } else {
            if (videoViewBP == null) {
                videoViewBP = BackgroundManager.bindService.getVvv();
            }
            if (videoViewBP == null) {
                ToastUtil.showToast(mContext, "播放器错误！");
                return;
            }
            videoViewBP.setOnPreparedListener(arg0 -> {
                setSeekbar();
                if (currentPage == 0) {
                    startSleep();
                    videoViewBP.start();
                    showPlayNotification(true);
                    //关闭过度加载视图
                    exerciseHandler.obtainMessage(0);
                }

                videoHandler.sendEmptyMessage(PROGRESS_CHANGED);
            });

            videoViewBP.setOnBufferingUpdateListener((mp, percent) -> {
                Message msg = Message.obtain();
                msg.arg1 = percent;
                msg.what = BUFFER_CHANGED;
                videoHandler.sendMessage(msg);
            });

            videoViewBP.setOnCompletionListener(arg0 -> {
                new Thread(new UpdateStudyRecordThread()).start();

                if (mode == 0) {
                    playVideo(true);
                    textCenter.setSubtitleSum(subtitleSum);
                    // setReadText();

                    //重新从头播放下
                    originalSoundControlWrapper(true);
                } else {
                    if (mode == 1) {
                        voaId = new NextVideo(voaId, source, mContext)
                                .following();
                    } else if (mode == 2) {
                        voaId = new NextVideo(voaId, source, mContext)
                                .nextVideo();
                    }

                    VoaDataManager.Instace().voaDetailsTemp = textDetailOp
                            .findDataByVoaId(voaId);
                    if (VoaDataManager.Instace().voaDetailsTemp != null
                            && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                        setVoaData();
                    }
                }
                setReadLesson(voaId);
                Title.setText(voaTemp.title);
                EventBus.getDefault().post(new PlayerNextEvent(voaTemp.title, voaTemp.titleCn, voaTemp.lesson, voaTemp.art));
                updateNotification();
            });
        }

    }

    public void setReadLesson(int voaId) {
        voaOp.updateIsRead(voaId);
        voaOp.updateReadCount(voaId);
        ConfigManager.Instance().putInt("voaIdSave", voaId);
    }

    private void showAlertAndCancel(String title, String msg) {
        if (!isDestroyed()) {
            final AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle(title);
            alert.setMessage(msg);
            alert.setIcon(android.R.drawable.ic_dialog_alert);
            alert.setButton(AlertDialog.BUTTON_POSITIVE,
                    getResources().getString(R.string.alert_btn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert.dismiss();
                        }
                    });
            alert.show();
        }
    }

    // 结构化时间
    private void Reciprocal(int time) {
        int i = time;
        int minute = i / 60;
        int second = i % 60;
        textView_time.setText(String.format("%02d:%02d", minute, second));
    }

    private void updateNotification() {
        currParagraph = 0;
        lastParegraph = 1;
    }

    @SuppressLint("MissingPermission")
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) StudyActivity.newInstance().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public void tofinish() {

        this.finish();
    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }

    @Override
    protected void onPause() {
        if (valReadAdapter.videoView != null) {
            valReadAdapter.videoView.pause();
            showPlayNotification(false);
            valReadAdapter.handler.removeMessages(0);
            BackgroundManager.Instance().bindService.putMediaPlayer(valReadAdapter.videoView);
        }

        // TODO
        // updateNotification();
        handler_read.removeMessages(0);
        handlerText.removeMessages(1);// 手动停止A-B播放

        if (currParagraph != 0) {
            textCenter.snyParagraph(currParagraph);
        }

        handler_read.removeMessages(1);
        commentHandler.removeMessages(0);
        videoHandler.removeMessages(PROGRESS_CHANGED);
        videoHandler.removeMessages(BUFFER_CHANGED);

//        if (mAdView != null) {
////            谷歌广告
//            mAdView.pause();
//        }
        super.onPause();
        // MobclickAgent.onPause(this);
    }

    /**
     * 提交学习记录
     */

    class UpdateStudyRecordThread implements Runnable {
        @Override
        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            endTime = df.format(new Date());

            String endFlag = "1";
            String lesson = Constant.APPType;
//            String lesson = "NewConcept"; // lesson应该上传用户使用的哪一款app的名称
            String lessonId = String.valueOf(voaId);
            String testNumber = "0";
            String testWords = "0";
            final String testMode = "1";
            String userAnswer = "";
            String score = "0";
            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
            String sign = UserInfoManager.getInstance().getUserId() + beginTime
                    + dft.format(System.currentTimeMillis());
            String deviceId = getLocalMacAddress();

            if (NetWorkState.isConnectingToInternet()) {
                try {
                    ClientSession.Instace().asynGetResponse(
                            new DataCollectRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
                                    beginTime, endTime,
                                    lesson, lessonId, testNumber, words + "",
                                    testMode, userAnswer, score, endFlag,
                                    deviceId, sign), new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    DataCollectResponse tr = (DataCollectResponse) response;

                                    if (tr != null && tr.result.equals("1")
                                            && testMode.equals("1")) {
                                        Looper.prepare();
                                        if (tr.score.equals("0")) {
                                            // Toast.makeText(mContext,
                                            // "数据提交成功!", 1500).show();
                                        } else
                                            Toast.makeText(
                                                    mContext,
                                                    "数据提交成功，恭喜您获得了" + tr.score
                                                            + "分", Toast.LENGTH_SHORT).show();

                                        //handler.sendEmptyMessage(8);
                                        Looper.loop();
                                    } else if (tr.result.equals("0")) {
                                        Looper.prepare();
//                                        Toast.makeText(mContext, "数据提交出错", Toast.LENGTH_SHORT)
//                                                .show();
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(mContext, "数据提交异常", Toast.LENGTH_SHORT)
                                                .show();
                                        Looper.loop();
                                    }
                                }
                            }, null, mNetStateReceiver);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //重置学习开始时间,不能和上一篇的末尾时间重叠，需要延时一秒
            timeHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            beginTime = df.format(new Date());
        }
    };

    private float stepIncrease(float speed) {
        int temp = (int) (speed * 10);
        temp = (temp >= 20) ? 6 : temp + 2;
        return (float) temp / 10.0f;
    }

    private String buildSpeedString(float speed) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(speed)).append("x");
        return sb.toString();
    }

    private class initExtendedPlayerOnly extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            /**
             *花了四五个小时边改边看终于从屎山中找到了
             * 初始化播放器并开始播放的代码
             */
            playVideo(false);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setSeekbar();
            setModeBackground();
            setShowChineseButton();
            setLockButton();
            videoHandler.sendEmptyMessage(PROGRESS_CHANGED);
//            waittingDialog.dismiss();
        }
    }

    // 将要执行的操作写在线程对象的run方法当中
    Runnable updateReadThread = new Runnable() {
        public void run() {
            // 播放音频时，计时
            if (time <= playTime) {
                Reciprocal((int) (playTime - time));

                time = time + 0.1;
                handlerRead.sendEmptyMessageDelayed(0, 100);
            } else {
                if (isSpeed) {
                    videoView.pause();
                } else {
                    videoViewBP.pause();
                }
                showPlayNotification(false);
            }
        }
    };

    Runnable updatePlayRecordThread = new Runnable() {
        public void run() {
            if (time1 < recordTime) {// 播放录音
                if (!isPlayRecord) {
                    isPlayRecord = !isPlayRecord;
                    if (soundFile != null && soundFile.exists()) {
//                        if (mediaRecorder != null) {
//                            mediaRecorder.reset();
//                        }

                        mPlayer = new Player(mContext, null);
                        mPlayer.playUrl(Constant.recordAddr);

                        Reciprocal((int) (recordTime));
                    }
                    CustomToast.showToast(mContext, R.string.study_play_record,
                            1000);
                }

                Reciprocal((int) (recordTime - time1));
                time1 = time1 + 0.1;
                handlerRead.sendEmptyMessageDelayed(1, 100);
            }
        }
    };

    Runnable updateRecordThread = new Runnable() {
        public void run() {
            // 录音
            if (!isRecord) {
                isRecord = !isRecord;

                textView_time.setText("00:00");
                //small_voice_handler.sendEmptyMessage(0);
                CustomToast.showToast(mContext, R.string.study_recording, 1000);

                try {
                    soundFile = new File(Constant.recordAddr);
                    smallRecordManager = new SmallRecordManager(soundFile,
                            smallVoiceValue);
                    smallRecordManager.startRecord();
                    CustomToast.showToast(mContext, R.string.study_recording,
                            1000);
                } catch (Exception e) {

                }
            }

            Reciprocal((int) (time0));
            time0 = time0 + 0.1;
            handlerRead.sendEmptyMessageDelayed(2, 100);
        }
    };

    Handler voice_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    voiceView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    voiceView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    Handler handler_read = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateReadThread.run();
                    break;
                case 1:
                    updateRecordThread.run();
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handlerText = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, "区间播放已取消", 1000);
                    handlerText.removeMessages(1);// 手动停止A-B播放
                    break;
                case 1:
                    if (isSpeed)
                        videoView.seekTo(aPositon);// A-B播放
                    else
                        videoViewBP.seekTo(aPositon);// A-B播放
                    handlerText.sendEmptyMessageDelayed(1, bPosition - aPositon
                            + 300);
                    break;
                default:
                    break;

            }
        }
    };

    private Handler handlerRead = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    updateReadThread.run();
                    break;
                case 1:
                    updatePlayRecordThread.run();
                    break;
                case 2:
                    updateRecordThread.run();
                    break;
                default:
                    break;

            }
        }
    };

    private Handler videoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS_CHANGED:
                    int i = 0;
                    if (isSpeed)
                        i = videoView.getCurrentPosition();
                    else
                        i = videoViewBP.getCurrentPosition();
                    seekBar.setProgress(i);
                    i /= 1000;
                    int minute = i / 60;
                    int second = i % 60;
                    minute %= 60;
                    // playedTime = i;
                    curTime.setText(String.format("%02d:%02d", minute, second));
                    try {
                        if (isSpeed) {
                            if (videoView.isPlaying()) {
                                Log.e("speedplayer", "" + videoView
                                        .getCurrentPosition() / 1000.0);
                                currParagraph = subtitleSum.getParagraph(videoView
                                        .getCurrentPosition() / 1000.0);

                                if (currParagraph != 0) {
                                    textCenter.snyParagraph(currParagraph);
                                }
                                isPaused = false;
                                //过度加载视图
                                //exerciseHandler.obtainMessage(1);
                                pause.setBackgroundResource(R.drawable.image_pause);
                            } else if (textCenter != null) {
                                pause.setBackgroundResource(R.drawable.image_play);
                                textCenter.unsnyParagraph();
                            }
                        } else {
                            if (videoViewBP.isPlaying()) {
                                currParagraph = subtitleSum.getParagraph(videoViewBP
                                        .getCurrentPosition() / 1000.0);
                                if (currParagraph != 0) {
                                    textCenter.snyParagraph(currParagraph);
                                }
                                isPaused = false;
                                //过度加载视图
                                //exerciseHandler.obtainMessage(1);
                                pause.setBackgroundResource(R.drawable.image_pause);
                            } else if (textCenter != null) {
                                pause.setBackgroundResource(R.drawable.image_play);

                                textCenter.unsnyParagraph();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
                    }
                    break;
                case BUFFER_CHANGED:
                    seekBar.setSecondaryProgress(msg.arg1 * seekBar.getMax() / 100);
                    break;
                case NEXT_VIDEO:
                    RefreshData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public Handler wordHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    card.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public Handler rankHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        ClientSession.Instace().asynGetResponse(//concept
                                new GetRankInfoRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), "familyalbum", String.valueOf(voaTemp.voaId), start, total),
                                new IResponseReceiver() {
                                    @Override
                                    public void onResponse(BaseHttpResponse response,
                                                           BaseHttpRequest request, int rspCookie) {
                                        GetRankInfoResponse tr = (GetRankInfoResponse) response;
                                        if (tr != null && tr.message.equals("Success")) {
                                            //请求成功
                                            rankHandler.sendEmptyMessage(6);
                                        } else {
                                            rankHandler.sendEmptyMessage(7);
                                            LogUtils.e("排名请求失败" + tr.message);
                                        }
                                        myImgSrc = tr.myImgSrc;
                                        myScores = tr.myScores;
                                        myCount = tr.myCount;
                                        myRanking = tr.myRanking;
                                        rankUsers = tr.rankUsers;
                                        if (rankUsers.size() == 0) {
                                            champion = new RankUser();
                                        } else {
                                            champion = rankUsers.get(0);
                                        }

                                        if (rankUsers.size() < 10) {
                                            runOnUiThread(new Runnable() {//子线程修改UI
                                                @Override
                                                public void run() {
                                                    rankListView.removeFooterView(commentFooter);
                                                }
                                            });
                                            scorllable = false;
                                        } else {
                                            commentFooter.setVisibility(View.VISIBLE);
                                        }
                                        rankHandler.sendEmptyMessage(1);
                                        if (champion.getRanking().equals("1")) {
                                            rankHandler.sendEmptyMessage(2);
                                        }

                                    }
                                }, null, mNetStateReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                        rankHandler.sendEmptyMessage(7);
                    }
                    refreshView.onFooterRefreshComplete();//完成加载
                    break;
                case 1:
                    if (rankListAdapter == null) {
                        rankListAdapter = new RankListAdapter(mContext, rankUsers);
                        rankListView.setAdapter(rankListAdapter);
                    } else if (champion.getRanking().equals("1")) {
                        rankListAdapter.resetList(rankUsers);
                    } else {
                        rankListAdapter.addList(rankUsers);
                    }
                    start = String.valueOf(rankListAdapter.getCount());


                    if (UserInfoManager.getInstance().isLogin()) {
                        myUsername.setText(UserInfoManager.getInstance().getUserName());
                        GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage,
                                R.drawable.noavatar_small);
                    } else {
                        myUsername.setText("未登录");
                        myImage.setImageResource(R.drawable.userimg);
                    }
                    userInfo.setText("句子数:" + myCount + ",总分:" + myScores + ",排名:" + myRanking);

                    refreshView.onHeaderRefreshComplete();//完成刷新
                    refreshView.onFooterRefreshComplete();//完成加载
                    break;
                case 2:
                    String firstChar = getFirstChar(champion.getName());
                    if (champion.getImgSrc().equals("http://static1." + Constant.IYBHttpHead() + "/uc_server/images/noavatar_middle.jpg")) {
                        userImage.setVisibility(View.INVISIBLE);
                        userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            userImageText.setBackgroundResource(R.drawable.rank_blue);
                            userImageText.setText(firstChar);

                            if (champion.getName() != null && !"".equals(champion.getName()) && !"null".equals(champion.getName())

                                    && !"none".equals(champion.getName())) {
                                userName.setText(champion.getName());
                            } else {
                                userName.setText(champion.getUid());
                            }


                            cpReadWords.setText("总分：" + champion.getScores());
                        } else {
                            userImageText.setBackgroundResource(R.drawable.rank_green);
                            userImageText.setText(firstChar);
                            if (champion.getName() != null && !"".equals(champion.getName()) && !"null".equals(champion.getName())

                                    && !"none".equals(champion.getName())) {
                                userName.setText(champion.getName());
                            } else {
                                userName.setText(champion.getUid());
                            }
                            cpReadWords.setText("总分：" + champion.getScores());
                        }
                    } else {
                        userImageText.setVisibility(View.INVISIBLE);
                        userImage.setVisibility(View.VISIBLE);
                        GitHubImageLoader.Instace(mContext).setRawPic(champion.getImgSrc(), userImage,
                                R.drawable.noavatar_small);

                        if (champion.getName() != null && !"".equals(champion.getName()) && !"null".equals(champion.getName())

                                && !"none".equals(champion.getName())) {
                            userName.setText(champion.getName());
                        } else {
                            userName.setText(champion.getUid());
                        }


                        cpReadWords.setText("总分：" + champion.getScores());
                    }
                    refreshView.onHeaderRefreshComplete();//完成刷新
                    refreshView.onFooterRefreshComplete();//完成加载
                    break;
                case 3:
                    //ToastUtil.showToast(mContext,"发布成功");
                    tv_read_share.setText("分享");
                    break;
                case 4:
                    ToastUtil.showToast(mContext, "发布失败");
                    tv_read_share.setText("发布");
                    break;
                case 5:
                    ToastUtil.showToast(mContext, "发布失败，合成数据为空");
                    tv_read_mix.setText("合成");
                    tv_read_share.setText("发布");
                    break;
                case 6:
                    LogUtils.e("排名请求成功");
                    refreshView.setVisibility(View.VISIBLE);
                    tvRankRefresh.setVisibility(View.GONE);
                    if (waittingDialog.isShowing()) {
                        waittingDialog.dismiss();
                    }
                    break;
                case 7:
                    LogUtils.e("排名请求失败");
                    start = "0";
                    refreshView.setVisibility(View.GONE);
                    tvRankRefresh.setVisibility(View.VISIBLE);
                    if (waittingDialog.isShowing()) {
                        waittingDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /*case 0x111:
                    if (!AdBlocker.getInstance().shouldBlockAd()){
                        initAdsetting();
                        initaiyubaAd();
                    }
                    break;*/
                case 1:
                    String type = (voaTemp.isCollect.equals("1")) ? "insert" : "del";
                    ExeProtocol.exe(
                            new FavorUpdateRequest(
                                    String.valueOf(UserInfoManager.getInstance().getUserId()),
                                    voaTemp.voaId, type), new ProtocolResponse() {
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    FavorUpdateResponse reponse = (FavorUpdateResponse) bhr;
                                    if (reponse.result == 1 || reponse.result == 2 || reponse.result == 0) {
                                        voaOp.updateSynchro(voaTemp.voaId, 1);
                                        //handler.sendEmptyMessage(3);//不等网络请求了
                                    } else {
                                        //handler.sendEmptyMessage(10);
                                        LogUtils.e("收藏错误：" + reponse.result + " " + reponse.word);
                                    }
                                }

                                @Override
                                public void error() {
                                }
                            });
                    break;
                case 3:
                    String tip = (voaTemp.isCollect.equals("1")) ? "收藏成功" : "取消收藏成功";
                    CustomToast.showToast(mContext, tip, 2000);
                    break;
                case 4:
//                    CustomToast.showToast(mContext, R.string.play_check_network, 1000);
                    break;
                case 10:
                    CustomToast.showToast(mContext, "收藏失败！", 1000);
                    break;
                case 5:
                    showAlertAndCancel(
                            getResources().getString(R.string.alert),
                            getResources().getString(R.string.study_info1));
                    break;
                case 13:
                    Toast.makeText(mContext, "分享成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 19:
                case 49:
                    if (UserInfoManager.getInstance().isLogin()) {
                        final ReadVoiceComment rvc = new ReadVoiceComment(voaTemp);
                        RequestCallBack rc = new RequestCallBack() {

                            @Override
                            public void requestResult(
                                    Request result) {
                                AddCreditsRequest rq = (AddCreditsRequest) result;
                                if (rq.isShareFirstlySuccess()) {
                                    String msg = "分享成功，增加了"
                                            + rq.addCredit
                                            + "积分，共有"
                                            + rq.totalCredit
                                            + "积分";
                                    CustomToast.showToast(
                                            mContext, msg,
                                            3000);
                                } else if (rq
                                        .isShareRepeatlySuccess()) {
                                    CustomToast.showToast(
                                            mContext,
                                            "分享成功", 3000);
                                }
                            }
                        };
                        int uid = Integer.parseInt(String.valueOf(UserInfoManager.getInstance().getUserId()));
                        AddCreditsRequest rq = new AddCreditsRequest(
                                uid, rvc.getVoaRef().voaId,
                                msg.what, rc);
                        RequestQueue queue = Volley
                                .newRequestQueue(mContext);
                        queue.add(rq);
                    }
                    break;

                case 6:

                    if (cPlayer.isPlaying()) {
                        imv_total_time.setText(getDurationInFormat());
                        imv_seekbar_player.setMax((int) mp3TotalTime);
                        imv_seekbar_player.setProgress(cPlayer.getCurrentTime());
                        if (cPlayer.getCurrentTime() < mp3TotalTime) {

                            mp3changTime = mp3changTime + 100;
                            imv_current_time.setText(cPlayer.getCurrentTimeInFormat());
                            handler.sendEmptyMessageDelayed(6, 100);
                        } else {

                            handler.sendEmptyMessage(7);
                        }
                    } else {
                        handler.sendEmptyMessage(7);
                    }
                    break;
                case 7:
                    imv_seekbar_player.setMax(100);
                    imv_seekbar_player.setProgress(100);
                    imv_current_time.setText(getDurationInFormat());
                    tv_read_mix.setEnabled(true);
                    break;
                case 8:
                    ToastUtil.showToast(mContext, "合成失败！");
                    // TODO: 2022/6/30 将文字重新修改成合成
                    tv_read_mix.setText("合成");
                    break;
                case 9:
                    ToastUtil.showToast(mContext, "合成成功！");
                    tv_read_sore.setVisibility(View.VISIBLE);
                    tv_read_sore.setText(totalScore / web_path_list.size() + "");
                    tv_read_mix.setText("试听");
                    isMix = true;

                    // TODO: 2022/6/30 合成成功后，这里将分享转成发布
                    tv_read_share.setText("发布");
                    break;
                default:
                    break;

            }
        }
    };

    //合成语音上发至服务器 发布  不能使用旧版本的post请求
    private void sendSound() {
        if (UserInfoManager.getInstance().isLogin()) {

            if (isSendSound) {
                com.iyuba.concept2.widget.cdialog.CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
            } else {
                waittingDialog.show();
                if (composeVoicePath != null && !composeVoicePath.equals("")) {
                    //新的网络请求！！！
                    ApiService service = ApiRetrofit.getInstance().getApiService();
                    String head = "http://voa." + Constant.IYBHttpHead() + "/voa/UnicomApi";
                    service.audioSendApi(head, "familyalbum", "android",
                            "json", "60003", String.valueOf(UserInfoManager.getInstance().getUserId()),
                            UserInfoManager.getInstance().getUserName(),
                            String.valueOf(voaTemp.voaId), String.valueOf(totalScore / web_path_list.size()),
                            "4", composeVoicePath).enqueue(new Callback<EvaSendBean>() {
                        @Override
                        public void onResponse(Call<EvaSendBean> call, Response<EvaSendBean> response) {
                            String result = response.body().getResultCode();


                            shuoshuoId = String.valueOf(response.body().getShuoshuoId());
                            String addscore = String.valueOf(response.body().getAddScore());
                            if (result.equals("501") || result.equals("1")) {
                                waittingDialog.dismiss();
                                Message msg = handler
                                        .obtainMessage();

                                msg.what = 10;
                                msg.arg1 = Integer.parseInt(addscore);
                                commentHandler.sendMessage(msg);
                                rankHandler.sendEmptyMessage(0);
                                rankHandler.sendEmptyMessage(3);
                                LogUtils.e("发布成功");
                            } else {
                                LogUtils.e("发布失败1" + response.body().getMessage());
                                rankHandler.sendEmptyMessage(4);
                            }
                        }

                        @Override
                        public void onFailure(Call<EvaSendBean> call, Throwable e) {
                            LogUtils.e("发布失败" + e);
                            rankHandler.sendEmptyMessage(4);
                        }
                    });
                } else {
                    LogUtils.e("发布失败，合成数据为空");
                    rankHandler.sendEmptyMessage(5);
                }
            }
        } else {
//            Intent intent = new Intent();
//            intent.setClass(mContext, Login.class);
//            StudyActivity.newInstance().startActivity(intent);
            LoginUtil.startToLogin(mContext);
        }
    }

    private void showShareSound() {
        String siteUrl = "http://voa." + Constant.IYBHttpHead() + "/voa/play.jsp?id="
                + shuoshuoId + "&addr=" + composeVoicePath + "&apptype=" + Constant.TOPICID;

        String text = "我在爱语吧语音评测中获得了" + (totalScore / web_path_list.size()) + "分";
        String imageUrl = "http://app." + Constant.IYBHttpHead() + "/android/images/FamilyAlbum/FamilyAlbum.png";
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
        oks.setTitle(text);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(voaTemp.title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//         oks.setImagePath("/sdcard/test.jpg");
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

    /**
     * 时间格式转化为00：00
     *
     * @return
     */
    private String getDurationInFormat() {
        StringBuffer sb = new StringBuffer("");
        int musicTime = (int) (mp3TotalTime / 1000);

        String minu = String.valueOf(musicTime / 60);
        if (minu.length() == 1) {
            minu = "0" + minu;
        }
        String sec = String.valueOf(musicTime % 60);
        if (sec.length() == 1) {
            sec = "0" + sec;
        }

        sb.append(minu).append(":").append(sec);
        return sb.toString();
    }

    /**
     * list 去重 保留最新元素
     *
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    /**
     * 提交学习记录，原文点击暂停按钮，或者切换到评测，排行等页面时强制暂停，或者点击返回按钮时提交不完成阅读文章记录
     */
    private void commitStudyRecordUnfinish() {
        isPaused = true;
        if (isSpeed) {
            if (videoView.isPlaying()) {
                new Thread(new UpdateStudyRecordunfinishThread()).start();
            }
        } else {
            if (videoViewBP.isPlaying()) {
                new Thread(new UpdateStudyRecordunfinishThread()).start();
            }
        }
    }

    /**
     * 提交学习记录
     */
    class UpdateStudyRecordunfinishThread implements Runnable {
        @Override
        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            endTime = df.format(new Date());
            //Log.e("url", beginTime + "===" + endTime + "单词数" + wordsCount());
            String endFlag = "0";  //未完成播放
            String lesson = Constant.APPType;
//            String lesson = "NewConcept"; // lesson应该上传用户使用的哪一款app的名称
            String lessonId = String.valueOf(voaId);
            String testNumber = "0";
            String testWords = "0";
            final String testMode = "1";
            String userAnswer = "";
            String score = "0";
            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
            String sign = UserInfoManager.getInstance().getUserId() + beginTime
                    + dft.format(System.currentTimeMillis());
            String deviceId = getLocalMacAddress();
            if (NetWorkState.isConnectingToInternet()) {
                try {
                    ClientSession.Instace().asynGetResponse(//有内存溢出的情况
                            new DataCollectRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), beginTime, endTime,
                                    lesson, lessonId, testNumber, wordsCount() + "",
                                    testMode, userAnswer, score, endFlag,
                                    deviceId, sign), new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    DataCollectResponse tr = (DataCollectResponse) response;

                                    if (tr != null && tr.result.equals("1") && testMode.equals("1")) {
                                        Looper.prepare();
                                        if (tr.score.equals("0")) {
                                            // Toast.makeText(mContext,
                                            // "数据提交成功!", 1500).show();
                                        } else
                                            Toast.makeText(
                                                    mContext,
                                                    "数据提交成功，恭喜您获得了" + tr.score
                                                            + "分", Toast.LENGTH_SHORT).show();

                                        //handler.sendEmptyMessage(8);
                                        Looper.loop();
                                    } else if (tr.result.equals("0")) {
                                        Looper.prepare();
//                                        Toast.makeText(mContext, "数据提交出错", Toast.LENGTH_SHORT)
//                                                .show();
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(mContext, "数据提交异常", Toast.LENGTH_SHORT)
                                                .show();
                                        Looper.loop();
                                    }
                                }
                            }, null, mNetStateReceiver);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    /**
     * 计算提交时间段的单词数
     */
    private int wordsCount() {
        int wordNum = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

        try {

            int timeAll = (int) ((df.parse(endTime).getTime() - df.parse(beginTime).getTime()) / 1000);


            wordNum = timeAll * words / player_alltime;

            Log.e("url", timeAll + "==" + player_alltime + "==" + words + "====" + wordNum);
            if (wordNum > words) {
                wordNum = words;
            }
        } catch (Exception e) {
            e.printStackTrace();
            wordNum = 0;
        }

        return wordNum;
    }

    @NeedsPermission({android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void requestEvaluate() {
    }

    @OnPermissionDenied({android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void requestEvaluateDenied() {
        ToastUtil.showToast(StudyActivity.this, "申请权限失败,此功能无法正常使用!");
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StudyActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode, grantResults);
    }

    public void checkStudyPermission() {
        StudyActivityPermissionsDispatcher.requestEvaluateWithPermissionCheck(StudyActivity.this);
    }

    public void startSleep() {
        int time = (int) SP.get(mContext, "sp_play_sleep_time", 0);
        if (time > 0) {
            //开启睡眠模式
            mSleepHandler.removeCallbacksAndMessages(null);

            mSleepHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (videoView != null && videoView.isPlaying()) {
                        videoView.pause();
                    }
                    if (videoViewBP != null && videoViewBP.isPlaying()) {
                        videoViewBP.pause();
                    }

                    showPlayNotification(false);
                }
            }, time * 60 * 1000);
            LogUtils.e("睡眠开始time:" + time);
        }
    }

    private void getPdfDownUrl(boolean isEn) {
        String language = "cn";
        if (isEn) {
            language = "en";
        }
        String finalLanguage = language;
        Map map = new HashMap<String, String>() {{
            put("voaid", voaId + "");
            put("language", finalLanguage);
        }};
        RetrofitFactory.getAppsApi().getPdf(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "onError: 获取pdf下载地址出错" + e.getMessage());
                        ToastUtil.showToast(StudyActivity.this, "获取下载链接失败");
                    }

                    @Override
                    public void onNext(ResponseBody result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.string());
                            String url = jsonObject.getString("path");
                            if (url != null && !url.isEmpty()) {
                                downloadPdf("http://familyusa.iyuba.cn" + url);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(StudyActivity.this, "获取下载链接失败");
                        }
                    }
                });
    }

    private void downloadPdf(String url) {
//        ToastUtil.showToast(this, "开始下载请到任务栏查看");
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setTitle("pdf下载");
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        String dirPath = Environment.DIRECTORY_DOWNLOADS;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            dirPath = getExternalFilesDir(null).getPath();
//        }
//        request.setDestinationInExternalFilesDir(this, dirPath, "走遍美国_" + voaId + ".pdf");
//
//        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        //将下载任务加入队列
//        long id = manager.enqueue(request);

        //这里修改为跳转到系统浏览器中下载
        HelpUtil.downloadFileBySystemBrowser(this, url);
    }

    /***************************广告计时器***************************/
    //广告定时器
    private static final String timer_ad = "timer_ad";
    //广告间隔时间
    private static final long adScaleTime = 20*1000L;
    //开始计时
    private void startAdTimer() {
        stopAdTimer();
        LibRxTimer.getInstance().multiTimerInMain(timer_ad, 0, adScaleTime, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                showBannerAd();
            }
        });
    }
    //停止计时
    private void stopAdTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_ad);
    }

    /*******************************新的banner广告显示**********************/
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //显示的界面模型
    private AdBannerViewBean bannerViewBean = null;
    //显示banner广告
    private void showBannerAd(){
        //请求广告
        if (bannerViewBean==null){
            bannerViewBean = new AdBannerViewBean(iyubaAdLayout, webAdLayout, webAdImage, webAdClose,webAdTips, new AdBannerShowManager.OnAdBannerShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {
                    adLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    pauseReadPlayer();

                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            ToastUtil.showToast(StudyActivity.this,"暂无内容");
                            return;
                        }

                        Intent intent = new Intent();
                        intent.setClass(StudyActivity.this, WebActivity.class);
                        intent.putExtra("url", jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;

                        //获取奖励
                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_banner;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                //直接显示信息即可
                                ToastUtil.showToast(ConceptApplication.getContext(),showMsg);

                                if (isSuccess){
                                    EventBus.getDefault().post(new RefreshUserInfoEvent());
                                }
                            }
                        });
                        //点击广告提交数据
                        /*List<AdLocalMarkBean> localAdList = new ArrayList<>();
                        localAdList.add(new AdLocalMarkBean(
                                fixAdType,
                                fixShowType,
                                AdShowUtil.NetParam.AdOperation.operation_click,
                                System.currentTimeMillis()/1000L
                        ));
                        AdUploadManager.getInstance().submitAdMsgData(getActivity().getPackageName(), localAdList, new AdUploadManager.OnAdSubmitCallbackListener() {
                            @Override
                            public void showSubmitAdResult(boolean isSuccess, String showMsg) {
                                //不进行处理
                            }
                        });*/
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    //关闭界面
                    adLayout.setVisibility(View.GONE);
                    //关闭计时器
                    stopAdTimer();
                    //关闭广告
                    AdBannerShowManager.getInstance().stopBannerAd();
                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdBannerShowManager.getInstance().setShowData(this,bannerViewBean);
        }

        AdBannerShowManager.getInstance().showBannerAd();
        //重置数据
        isGetRewardByClickAd = false;
    }
    //配置广告显示
    private void refreshAd(){
        // TODO: 2024/11/29 根据要求，华为渠道暂时关闭banner广告，后续在处理吧
        String channel = ChannelReaderUtil.getChannel(this);
        if (channel.equals("huawei")){
            return;
        }

        if (!UserInfoManager.getInstance().isVip() && !AdBlocker.getInstance().shouldBlockAd()) {
            startAdTimer();
        }else {
            adLayout.setVisibility(View.GONE);
            stopAdTimer();
            AdBannerShowManager.getInstance().stopBannerAd();
        }
    }

    //暂停原文音频播放
    private void pauseReadPlayer() {
        pause.setBackgroundResource(R.drawable.image_play);
        try {
            if (isSpeed) {
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                }
            } else {
                if (videoViewBP != null && videoViewBP.isPlaying()) {
                    videoViewBP.pause();
                }
            }
            showPlayNotification(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************二次数据处理***************************/
    private void initNewData() {
        commentButton.setVisibility(View.GONE);
    }

    /*********************原文相关操作****************/
    //原文通知栏播放状态
    private void showPlayNotification(boolean isPlay) {
        BackgroundManager.Instance().bindService.showNotification(false, isPlay, voaTemp.title, StudyActivity.class);
    }

    /*********************评测相关操作****************/
    private View newEvalLayout;
    private RecyclerView evalRecyclerView;
    private TextView tvEvalPlayTime;
    private SeekBar seekMixProgress;
    private TextView tvEvalTotalTime;
    private TextView tvEvalMix;
    private TextView tvEvalScore;
    private TextView tvEvalPublish;

    private EvalAdapter evalAdapter;
    //评测-原文播放器
    private ExoPlayer evalAudioPlayer;
    //是否加载完成
    private boolean isPrepare = false;

    //评测-评测音频播放器
    private ExoPlayer evalPlayer;

    //录音器
    private LibRecordManager recordManager;
    //是否正在录音评测
    private boolean isRecordEvaling = false;
    //录音文件保存的路径
    private String recordSavePath = null;

    //评测-合成音频播放器
    private ExoPlayer mixPlayer;
    //合成的评测音频链接
    private String mixAudioUrl = null;

    //初始化评测的界面
    private void initEvalView() {
        newEvalLayout = inflater.inflate(R.layout.layout_eval, null);
        evalRecyclerView = newEvalLayout.findViewById(R.id.recyclerView);
        tvEvalPlayTime = newEvalLayout.findViewById(R.id.tv_playTime);
        seekMixProgress = newEvalLayout.findViewById(R.id.seekBar_progress);
        seekMixProgress.setEnabled(false);
        tvEvalTotalTime = newEvalLayout.findViewById(R.id.tv_totalTime);
        tvEvalMix = newEvalLayout.findViewById(R.id.tv_mix);
        tvEvalScore = newEvalLayout.findViewById(R.id.tv_score);
        tvEvalPublish = newEvalLayout.findViewById(R.id.tv_publish);

        tvEvalMix.setOnClickListener(v->{
            if (isRecordEvaling){
                ToastUtil.showToast(this,"正在录音评测中~");
                return;
            }

            pauseAudio();
            pauseEvalAudio();

            //根据文本判断当前操作
            String showText = tvEvalMix.getText().toString().trim();
            if (showText.equals("合成")){
                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(StudyActivity.this);
                    return;
                }

                //这里判断是否存在两个及以上的数据
                List<EvalSentenceEntity> evalList = HelpDataManager.getInstance().getMultiEvalData(voaTemp.voaId,UserInfoManager.getInstance().getUserId());
                if (evalList.size()<2){
                    ToastUtil.showToast(this,"请至少评测两句后再合成");
                    return;
                }

                //开启合成
                submitAudioMix();
            }else if (showText.equals("试听")){
                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                if (TextUtils.isEmpty(mixAudioUrl)){
                    ToastUtil.showToast(this,"未找到合成音频");
                    return;
                }

                String playUrl = "http://voa."+Constant.IYBHttpHead()+"/voa/"+mixAudioUrl;
                playMixAudio(playUrl);
            }else if (showText.equals("停止")){
                tvEvalMix.setText("试听");
                pauseMixAudio();
            }
        });
        tvEvalPublish.setOnClickListener(v->{
            if (!NetworkUtil.isConnected(StudyActivity.this)){
                ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                return;
            }

            pauseAudio();
            pauseEvalAudio();
            pauseMixAudio();

            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(StudyActivity.this);
                return;
            }

            if (TextUtils.isEmpty(mixAudioUrl)){
                ToastUtil.showToast(this,"未查询到合成音频数据");
                return;
            }

            submitMixAudioPublish(voaTemp.voaId,tvEvalScore.getText().toString().trim(),mixAudioUrl);
        });

        evalAdapter = new EvalAdapter(this, new ArrayList<>());
        evalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        evalRecyclerView.setAdapter(evalAdapter);
        evalAdapter.setOnEvalItemClickListener(new EvalAdapter.OnEvalItemClickListener() {
            @Override
            public void onItemSwitch(int position) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(StudyActivity.this,"正在录音评测中～");
                    return;
                }

                //停止播放
                pauseAudio();
                pauseEvalAudio();
                pauseMixAudio();

                evalAdapter.refreshItem(position);
            }

            @Override
            public void onPlayAudio(long startTime, long endTime) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(StudyActivity.this,"正在录音评测中～");
                    return;
                }

                //停止其他音频播放
                pauseEvalAudio();
                pauseMixAudio();

                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                //判断停止还是显示
                if (evalAudioPlayer!=null){
                    if (evalAudioPlayer.isPlaying()){
                        pauseAudio();
                    }else {
                        playAudio(startTime, endTime);
                    }
                }else {
                    ToastUtil.showToast(StudyActivity.this,"音频播放器未初始化");
                }
            }

            @Override
            public void onRecord(int voaId, String paraId, String lineN, String sentence,long totalTime) {
                //停止其他音频播放
                pauseEvalAudio();
                pauseMixAudio();
                pauseAudio();

                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(StudyActivity.this);
                    return;
                }

                //检查是否为vip或者超过5个
                List<EvalSentenceEntity> evalList = HelpDataManager.getInstance().getMultiEvalData(voaId,UserInfoManager.getInstance().getUserId());
                boolean isCountLimit = evalList!=null&&evalList.size()>=5;
                boolean isVipLimit = !UserInfoManager.getInstance().isVip();
                boolean isEvalLimit = HelpDataManager.getInstance().getSingleEvalData(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN)==null;
                if (isCountLimit&&isVipLimit&&isEvalLimit){
                    new AlertDialog.Builder(StudyActivity.this)
                            .setMessage("评测限制")
                            .setMessage("非会员限制评测5个，非会员无限制，是否开通会员使用？")
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    VipCenterGoldActivity.start(StudyActivity.this,VipCenterGoldActivity.VIP_APP);
                                }
                            }).setNegativeButton("暂不开通",null)
                            .setCancelable(false)
                            .create().show();
                    return;
                }

                //判断录音评测
                if (!isRecordEvaling){
                    List<Pair<String,Pair<String,String>>> pairList = new ArrayList<>();
                    pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO, new Pair<>("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")));
                    pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "保存评测的音频文件，用于评测打分使用")));
                    LibPermissionDialogUtil.getInstance().showMsgDialog(StudyActivity.this, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
                        @Override
                        public void onGranted(boolean isSuccess) {
                            if (isSuccess){
                                startRecord(voaId,paraId,lineN,sentence,totalTime);
                            }
                        }
                    });
                }else {
                    stopRecord();
                    //提交评测
                    submitEval(voaId,paraId,lineN,sentence,recordSavePath);
                }
            }

            @Override
            public void onEvalPlay(int voaId,String paraId,String lineN,String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(StudyActivity.this,"正在录音评测中～");
                    return;
                }

                //停止音频播放
                pauseAudio();
                pauseMixAudio();

                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(StudyActivity.this);
                    return;
                }

                if (evalPlayer!=null){
                    if (evalPlayer.isPlaying()){
                        pauseEvalAudio();
                    }else {
                        //获取播放路径
                        String playUrl = getEvalPlayUrl(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN,audioUrl);
                        playEvalAudio(playUrl);
                    }
                }else {
                    ToastUtil.showToast(StudyActivity.this,"评测播放器初始化失败");
                }
            }

            @Override
            public void onPublish(int voaId, String paraId,String lineN, String score,String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(StudyActivity.this,"正在录音评测中～");
                    return;
                }

                pauseAudio();
                pauseEvalAudio();
                pauseMixAudio();

                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(StudyActivity.this);
                    return;
                }

                submitSinglePublish(voaId,paraId,lineN,score,audioUrl);
            }

            @Override
            public void onShare(int voaId,String score,String audioUrl,int shareId) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(StudyActivity.this,"正在录音评测中～");
                    return;
                }

                pauseAudio();
                pauseEvalAudio();
                pauseMixAudio();

                if (!NetworkUtil.isConnected(StudyActivity.this)){
                    ToastUtil.showToast(StudyActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(StudyActivity.this);
                    return;
                }

                String urlPre = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/voa/";
                String evalAudioUrl = audioUrl;
                shareSingleEval(voaId,score,shareId,evalAudioUrl);
            }

            @Override
            public void checkWord(VoaDetail voaDetail,EvalSentenceEntity sentenceEntity) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(StudyActivity.this,"正在录音评测中～");
                    return;
                }

                //停止其他音频播放
                pauseEvalAudio();
                pauseMixAudio();
                pauseAudio();

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(StudyActivity.this);
                    return;
                }

                NewEvalCorrectPage correctPage = new NewEvalCorrectPage();
                //这里将数据保存下载并且传递过去
                correctPage.setUid(UserInfoManager.getInstance().getUserId());
                correctPage.setVoaDetail(voaDetail);
                correctPage.setEvaluateBean(sentenceEntity);
                correctPage.show(((StudyActivity) mContext).getFragmentManager(), "EvalFragmentPage");
            }
        });
    }

    //初始化评测的播放器
    private void initEvalPlayer() {
        evalAudioPlayer = new ExoPlayer.Builder(this).build();
        evalAudioPlayer.setPlayWhenReady(false);
        evalAudioPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case com.google.android.exoplayer2.Player.STATE_READY:
                        isPrepare = true;
                        break;
                    case com.google.android.exoplayer2.Player.STATE_ENDED:
                        evalAdapter.refreshPlayView(0,0,false);
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                isPrepare = false;
            }
        });

        evalPlayer = new ExoPlayer.Builder(this).build();
        evalPlayer.setPlayWhenReady(false);
        evalPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case com.google.android.exoplayer2.Player.STATE_READY:
                        //加载完成，直接播放
                        playEvalAudio(null);
                        break;
                    case com.google.android.exoplayer2.Player.STATE_ENDED:
                        //播放完成，回调数据-刷新适配器中的显示
                        pauseEvalAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(StudyActivity.this, "播放器初始化失败");
            }
        });

        mixPlayer = new ExoPlayer.Builder(this).build();
        mixPlayer.setPlayWhenReady(false);
        mixPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case com.google.android.exoplayer2.Player.STATE_READY:
                        //加载完成，直接播放
                        playMixAudio(null);
                        break;
                    case com.google.android.exoplayer2.Player.STATE_ENDED:
                        //播放完成，回调数据-刷新适配器中的显示
                        pauseMixAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(StudyActivity.this, "合成播放器初始化失败");
            }
        });

        //预加载原文的音频
        preInitAudio(getReadPlayUrl());
    }

    //将时间数据写入数据
    private List<VoaDetail> writeTimeToList(List<VoaDetail> oldList){
        Log.d("播放时间111", oldList.size()+"");

        List<VoaDetail> newList = new ArrayList<>();
        if (oldList!=null&&oldList.size()>0){
            for (int i = 0; i < oldList.size(); i++) {
                VoaDetail temp = oldList.get(i);

                //如果不是最后一个，则设置为下一个的开始时间；最后一个设置为播放器的最后时间
                double endTime = 0;
                if (i < oldList.size()-1){
                    endTime = (long) (oldList.get(i+1).startTime*1.0f);
                }else {
                    //这里特意-200，因为担心计时器出现问题，播放异常
                    /*endTime = (getPlayAudioTime(getReadPlayUrl())-100L)/1000.0f;
                    endTime = LibBigDecimalUtil.trans2Double(endTime);*/

                    //这里设置成-1，表示需要持续到结束
                    endTime = -1;
                }

                temp.startTime = temp.startTime*1.0f;
                // TODO: 2023/12/27 这里因为时间不准确的问题，暂时将开始时间提前450ms
                if (temp.startTime>1.0f){
                    temp.startTime -= 0.45f;
                }

                temp.endTime = endTime;
                Log.d("播放时间222", temp.startTime+"---"+temp.endTime+"--"+i);

                newList.add(temp);
            }
        }
        return newList;
    }

    //获取当前音频的长度
    private long getPlayAudioTime(String playUrl){
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            if (playUrl.startsWith("http://")||playUrl.startsWith("https://")){
                mediaPlayer.setDataSource(this,Uri.parse(playUrl));
            }else {
                mediaPlayer.setDataSource(playUrl);
            }
            mediaPlayer.prepare();

            return mediaPlayer.getDuration();
        }catch (Exception e){
            return 0;
        }
    }

    //停止任何评测的音频和录音操作
    private void stopAllEvalAudio(){
        pauseAudio();
        pauseEvalAudio();
        pauseMixAudio();
        stopRecord();
    }

    /**********************录音*******************/
    //开始录音
    private void startRecord(int voaId,String paraId,String lineN,String sentence,long totalTime){
        recordSavePath = getRecordFilePath(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN,true);
        if (recordSavePath==null){
            ToastUtil.showToast(this,"录音文件初始化失败");
            return;
        }

        isRecordEvaling = true;
        recordManager = new LibRecordManager(new File(recordSavePath));
        recordManager.startRecord();

        startRecordTimer(voaId,paraId,lineN,sentence,totalTime);
    }

    //结束录音
    private void stopRecord(){
        if (recordManager!=null){
            recordManager.stopRecord();
        }

        evalAdapter.refreshRecordView(0,0,false);
        stopRecordTimer();
    }

    //录音计时器
     private static final String tag_recordTimer = "recordTimer";

    private void startRecordTimer(int voaId,String paraId,String lineN,String sentence,long totalTime){
        LibRxTimer.getInstance().multiTimerInMain(tag_recordTimer, 0, 100L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                int volume = (int) recordManager.getVolume();
                int max = 100;
                evalAdapter.refreshRecordView(volume,max,true);

                long recordTime = number*100L;
                if (recordTime>=totalTime){
                    stopRecord();
                    submitEval(voaId,paraId,lineN,sentence,recordSavePath);
                }
            }
        });
    }

    private void stopRecordTimer(){
        LibRxTimer.getInstance().cancelTimer(tag_recordTimer);
    }

    //录音文件保存路径
    private String getRecordFilePath(int voaId,int userId,String paraId,String lineN,boolean isCreateFile){
        String dirPath = ConfigManager.Instance().loadString("media_saving_path");
        String fileName = voaId+"_"+paraId+"_"+lineN+"_"+userId+".mp3";

        String savePath = dirPath+fileName;

        //如果需要创建文件，则进行创建操作
        if (isCreateFile){
            File saveFile = new File(savePath);
            if (saveFile.exists()){
                saveFile.delete();
            }else {
                if (!saveFile.getParentFile().exists()){
                    saveFile.getParentFile().mkdirs();
                }
            }

            try {
                saveFile.createNewFile();
            }catch (Exception e){
                savePath = null;
            }
        }

        return savePath;
    }

    //提交评测
    private Disposable submitSentenceEvalDis;
    private void submitEval(int voaId,String paraId,String lineN,String sentence,String recordPath){
        showLoading("正在提交句子评测~");

        LessonRemoteManager.submitEvalSentenceData(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN,sentence,recordPath)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<Eval_result>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitSentenceEvalDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<Eval_result> bean) {
                        stopLoading();
                        isRecordEvaling = false;

                        if (bean!=null&&bean.getResult().equals("1")){
                            //将数据保存在本地
                            Eval_result resultBean = bean.getData();
                            //单词数据
                            String evalWordStr = new Gson().toJson(resultBean.getWords());
                            EvalSentenceEntity sentenceEntity = new EvalSentenceEntity(
                                    voaId,
                                    paraId,
                                    lineN,
                                    UserInfoManager.getInstance().getUserId(),
                                    sentence,
                                    resultBean.getUrl(),
                                    recordPath,
                                    resultBean.getTotal_score(),
                                    resultBean.getScores(),
                                    evalWordStr);

                            HelpDataManager.getInstance().saveSingleEvalData(sentenceEntity);

                            //刷新数据显示
                            evalAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.showToast(StudyActivity.this,"提交评测失败，请重试("+bean.getResult()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        isRecordEvaling = false;
                        ToastUtil.showToast(StudyActivity.this,"提交评测异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(submitSentenceEvalDis);
                    }
                });
    }

    /*****************原文音频**************/
    //预加载评测原文音频
    private void preInitAudio(String urlOrPath){
        if (TextUtils.isEmpty(urlOrPath)){
            return;
        }

        Uri playUri = null;
        if (urlOrPath.startsWith("http://") || urlOrPath.startsWith("https://")) {
            playUri = Uri.parse(urlOrPath);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                playUri = FileProvider.getUriForFile(this, getResources().getString(R.string.file_provider_name_personal), new File(urlOrPath));
            } else {
                playUri = Uri.fromFile(new File(urlOrPath));
            }
        }
        MediaItem mediaItem = MediaItem.fromUri(playUri);
        evalAudioPlayer.setMediaItem(mediaItem);
        evalAudioPlayer.prepare();
    }

    //播放评测的原文音频
    private void playAudio(long startTime,long endTime) {
        if (!NetworkUtil.isConnected(this)){
            ToastUtil.showToast(this,"请链接网络后使用");
            return;
        }

        if (!isPrepare){
            ToastUtil.showToast(this,"加载音频失败");
            return;
        }

        evalAudioPlayer.seekTo(startTime);
        evalAudioPlayer.play();

        startAudioTimer(startTime,endTime);
    }

    //暂停评测的原文音频
    private void pauseAudio() {
        if (evalAudioPlayer != null && evalAudioPlayer.isPlaying()) {
            evalAudioPlayer.pause();
        }

        //停止动画
        stopAudioTimer();
    }

    //评测的原文计时器
    private static final String tag_audioTimer = "audioTimer";

    private void startAudioTimer(long startTime,long endTime){
        LibRxTimer.getInstance().multiTimerInMain(tag_audioTimer, 0, 100L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long totalTime = 0;
                if (endTime<0){
                    totalTime = evalAudioPlayer.getDuration()-startTime;
                }else {
                    totalTime = endTime-startTime;
                }


                long progressTime = evalAudioPlayer.getCurrentPosition()-startTime;
                Log.d("播放时间", progressTime+"---"+totalTime);
                evalAdapter.refreshPlayView(progressTime,totalTime,true);

                if (progressTime>=totalTime){
                    pauseAudio();
                    stopAudioTimer();
                    evalAdapter.refreshPlayView(0,0,false);
                }
            }
        });
    }

    private void stopAudioTimer(){
        LibRxTimer.getInstance().cancelTimer(tag_audioTimer);
        evalAdapter.refreshPlayView(0,0,false);
    }

    //获取原文的音频数据
    private String getReadPlayUrl(){
        String playUrl = null;
        if (UserInfoManager.getInstance().isVip()) {
            playUrl = Constant.sound_vip() + voaTemp.voaId + Constant.append;
        } else {
            playUrl = Constant.sound() + voaTemp.voaId + Constant.append;
        }
        // 原文音频的存放路径
        String pathString = Constant.videoAddr + voaTemp.voaId + Constant.append;
        String pathString2 = ConfigManager.Instance().loadString("media_saving_path") + File.separator + voaTemp.voaId + Constant.append;

        File audioFileOne = new File(pathString);
        if (audioFileOne.exists()){
            playUrl = pathString;
        }

        File audioFileTwo = new File(pathString2);
        if (audioFileTwo.exists()){
            playUrl = pathString2;
        }

        return playUrl;
    }

    /******************评测音频*****************/
    //播放评测的评测音频
    private void playEvalAudio(String urlOrPath) {
        if (!TextUtils.isEmpty(urlOrPath)) {
            Uri playUri = null;
            if (urlOrPath.startsWith("http://") || urlOrPath.startsWith("https://")) {
                playUri = Uri.parse(urlOrPath);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    playUri = FileProvider.getUriForFile(this, getResources().getString(R.string.file_provider_name_personal), new File(urlOrPath));
                } else {
                    playUri = Uri.fromFile(new File(urlOrPath));
                }
            }
            MediaItem mediaItem = MediaItem.fromUri(playUri);
            evalPlayer.setMediaItem(mediaItem);
            evalPlayer.prepare();
        } else {
            evalPlayer.play();

            evalAdapter.refreshEvalView(0,0,true);
        }
    }

    //暂停评测的评测音频
    private void pauseEvalAudio() {
        if (evalPlayer != null && evalPlayer.isPlaying()) {
            evalPlayer.pause();
        }

        evalAdapter.refreshEvalView(0,0,false);
    }

    //获取评测的音频文件路径
    private String getEvalPlayUrl(int voaId,int userId,String paraId,String lineN,String audioUrl){
        String localPath = getRecordFilePath(voaId,userId,paraId,lineN,false);
        File file = new File(localPath);
        if (file.exists()){
            return localPath;
        }

        //http://iuserspeech.iyuba.cn:9001/voa/wav7/202312/familyalbum/20231225/17034689874207236.mp3
        String urlPre = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/voa/";
        return urlPre+audioUrl;
    }

    //评测播放的定时器
    private static final String tag_evalPlayTimer = "evalPlayTimer";

    /*private void startEvalTimer(){
        LibRxTimer.getInstance().multiTimerInMain(tag_evalPlayTimer, 0, 100L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long progressTime = evalPlayer.getCurrentPosition();
                long maxTime = evalPlayer.getDuration();

                //刷新显示
                evalAdapter.refreshEvalView(progressTime,maxTime,true);
            }
        });
    }

    private void stopEvalPlayer(){
        LibRxTimer.getInstance().cancelTimer(tag_evalPlayTimer);
    }*/

    /*********************提交单句评测****************/
    private Disposable publishSingleEvalDis;
    public void submitSinglePublish(int voaId,String paraId,String lineN,String score,String audioUrl){
        showLoading("正在发布评测中~");

        LessonRemoteManager.publishEvalSentence(voaId,UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), lineN,audioUrl,score)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<Publish_result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        publishSingleEvalDis = d;
                    }

                    @Override
                    public void onNext(Publish_result bean) {
                        stopLoading();
                        if (bean!=null&&bean.getResultCode().equals("501")){
                            //设置分享id，刷新界面显示
                            ToastUtil.showToast(StudyActivity.this,"发布单句成功");
                            evalAdapter.saveShareId(voaId,paraId,lineN,bean.getShuoshuoId());
                            evalAdapter.notifyDataSetChanged();
                            //设置为重新合成
                            tvEvalMix.setText("合成");
                            mixAudioUrl = null;
                            tvEvalScore.setVisibility(View.GONE);
                            //刷新排行榜数据
                            refreshRankData();
                        }else {
                            ToastUtil.showToast(StudyActivity.this,"发布单句评测失败，请重试("+bean.getResultCode()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(StudyActivity.this,"发布单句评测异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(publishSingleEvalDis);
                    }
                });
    }

    /*********************分享单句评测**********************/
    private void shareSingleEval(int voaId,String score,int shareId,String evalAudioUrl){
        PlatformActionListener pal = new PlatformActionListener() {

            @Override
            public void onError(Platform platform, int arg1,
                                Throwable arg2) {
                com.iyuba.concept2.widget.cdialog.CustomToast.showToast(mContext, "分享失败", 1000);
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
                                com.iyuba.concept2.widget.cdialog.CustomToast.showToast(mContext,
                                        msg, 3000);
                            } else if (rq.isShareRepeatlySuccess()) {
                                com.iyuba.concept2.widget.cdialog.CustomToast.showToast(mContext,
                                        "分享成功", 3000);
                            }
                        }
                    };
                    int uid = UserInfoManager.getInstance().getUserId();
                    AddCreditsRequest rq = new AddCreditsRequest(
                            uid,
                            voaId,
                            srid, rc);
                    RequestQueue queue = Volley
                            .newRequestQueue(mContext);
                    queue.add(rq);
                }
            }

            @Override
            public void onCancel(Platform platform, int arg1) {
                com.iyuba.concept2.widget.cdialog.CustomToast.showToast(mContext, "分享已取消", 1000);
            }
        };

        String shareTitle = voaTemp.title;
        String shareMsg = "["+UserInfoManager.getInstance().getUserName()+"]在"+getResources().getString(R.string.app_name_main)+"评测中获得了"+score+"分";
        String shareUrl = "http://voa." + Constant.IYBHttpHead() + "/voa/play.jsp?id=" + shareId
                + "&apptype=" + Constant.TOPICID + "&addr=" + evalAudioUrl;
        String shareImageUrl = Constant.AppIcon();

        FixShareUtil.showShare(mContext, shareMsg, shareTitle, shareUrl,
                shareImageUrl, "很不错的应用，大家快来使用呀！", Constant.APPName,
                shareUrl, pal);
    }

    /*******************合成音频*****************/
    //播放评测的合成音频
    private void playMixAudio(String audioUrl) {
        if (!TextUtils.isEmpty(audioUrl)) {
            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            mixPlayer.setMediaItem(mediaItem);
            mixPlayer.prepare();
        } else {
            tvEvalTotalTime.setText(LibDateUtil.transPlayFormat(LibDateUtil.MINUTE,mixPlayer.getDuration()));
            tvEvalPlayTime.setText(LibDateUtil.transPlayFormat(LibDateUtil.MINUTE,0));

            mixPlayer.play();

            tvEvalMix.setText("停止");
            startMixTimer();
        }
    }

    //暂停评测的合成音频
    private void pauseMixAudio() {
        if (mixPlayer != null && mixPlayer.isPlaying()) {
            mixPlayer.pause();
        }

        if (TextUtils.isEmpty(mixAudioUrl)){
            tvEvalMix.setText("合成");
        }else {
            tvEvalMix.setText("试听");
        }
        seekMixProgress.setProgress(0);
        tvEvalPlayTime.setText(LibDateUtil.transPlayFormat(LibDateUtil.MINUTE,0));

        stopMixPlayer();
    }

    //评测合成的计时器
    private static final String tag_mixTimer = "tag_mixTimer";
    private void startMixTimer(){
        LibRxTimer.getInstance().multiTimerInMain(tag_mixTimer, 0, 100L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long progressTime = mixPlayer.getCurrentPosition();
                long totalTime = mixPlayer.getDuration();

                if (progressTime>=totalTime){
                    progressTime = totalTime;
                }

                seekMixProgress.setProgress((int) progressTime);
                seekMixProgress.setMax((int) totalTime);
                tvEvalPlayTime.setText(LibDateUtil.transPlayFormat(LibDateUtil.MINUTE,progressTime));
            }
        });
    }

    private void stopMixPlayer(){
        LibRxTimer.getInstance().cancelTimer(tag_mixTimer);
    }

    //提交音频合成
    private Disposable mixAudioDis;
    private void submitAudioMix(){
        showLoading("正在合成音频中~");

        LessonRemoteManager.mixEvalSentence(voaTemp.voaId,UserInfoManager.getInstance().getUserId())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<Mix_result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mixAudioDis = d;
                    }

                    @Override
                    public void onNext(Mix_result bean) {
                        stopLoading();
                        if (bean!=null&&bean.getResult().equals("1")){
                            mixAudioUrl = bean.getUrl();
                            tvEvalMix.setText("试听");

                            //合并数据并展示分数
                            List<EvalSentenceEntity> evalList = HelpDataManager.getInstance().getMultiEvalData(voaTemp.voaId,UserInfoManager.getInstance().getUserId());
                            long showScore = 0;
                            for (int i = 0; i < evalList.size(); i++) {
                                EvalSentenceEntity sentenceEntity = evalList.get(i);
                                showScore+=sentenceEntity.showScore;
                            }
                            tvEvalScore.setVisibility(View.VISIBLE);
                            tvEvalScore.setText(String.valueOf(showScore/evalList.size()));

                            ToastUtil.showToast(StudyActivity.this,"合成音频成功");
                        }else {
                            ToastUtil.showToast(StudyActivity.this,"合成音频失败("+bean.getResult()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(StudyActivity.this,"合成音频异常，请重试~");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(mixAudioDis);
                    }
                });
    }

    /*************************发布合成音频*********************/
    private Disposable publishMixAudioDis;
    private void submitMixAudioPublish(int voaId,String score,String mixAudioUrl){
        showLoading("正在将合成音频发布到排行榜");

        LessonRemoteManager.publishMixEvalSentence(voaId,UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), score,mixAudioUrl)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<Publish_result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        publishMixAudioDis = d;
                    }

                    @Override
                    public void onNext(Publish_result bean) {
                        stopLoading();

                        if (bean!=null&&bean.getResultCode().equals("501")){
                            ToastUtil.showToast(StudyActivity.this,"发布合成音频成功，请在排行榜中查看");
                            //刷新排行榜数据
                            refreshRankData();

                            //显示价格
                            /*int showReward = TextUtils.isEmpty(bean.getReward())?0:Integer.parseInt(bean.getReward());
                            if (showReward>0){

                            }*/
                        }else {
                            ToastUtil.showToast(StudyActivity.this,"发布合成音频到排行榜失败("+bean.getResultCode()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(StudyActivity.this,"发布合成音频到排行榜异常");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(publishMixAudioDis);
                    }
                });
    }


    /****************************刷新数据******************/
    //刷新评测数据
    private void refreshEvalData(){
        //这里顺便把数据给刷新上去(这里需要重新设置下时间，然后刷新数据)
        evalAdapter.refreshData(writeTimeToList(textDetailTemp));

        //重置是播放器
        initEvalPlayer();

        //取消各种数据
        mixAudioUrl = null;
        recordSavePath = null;
        isRecordEvaling = false;
        isPrepare = false;
    }

    //刷新排行榜数据
    private void refreshRankData(){
        start = "0";
        rankHandler.sendEmptyMessage(0);
    }

    /*********************加载弹窗*******************/
    private LoadingDialog loadingDialog;

    private void showLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /****************************收藏操作*********************/
    //刷新收藏样式
    private void refreshCollectView() {
        VoaCollectEntity collectEntity = HelpDataManager.getInstance().getSingleVoaCollectData(UserInfoManager.getInstance().getUserId(), voaTemp.voaId);
        if (collectEntity != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_study_collected);
            tvCollection.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            tvCollection.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_study_collect);
            tvCollection.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            tvCollection.setTextColor(Color.parseColor("#8a8a8a"));
        }
    }


    //刷新登录回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshUserInfo(RefreshUserInfoEvent event){
        refreshAd();
    }
}