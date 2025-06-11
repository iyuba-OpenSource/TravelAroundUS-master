package com.iyuba.concept2.lil.ui.search;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.efs.sdk.base.core.util.NetworkUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.iyuba.concept2.R;
import com.iyuba.concept2.fragment.NewEvalCorrectPage;
import com.iyuba.concept2.lil.ui.search.article.NewSearchArticleActivity;
import com.iyuba.concept2.lil.ui.search.article.NewSearchArticleAdapter;
import com.iyuba.concept2.lil.ui.search.sentence.NewSearchSentenceActivity;
import com.iyuba.concept2.lil.ui.search.sentence.NewSearchSentenceAdapter;
import com.iyuba.concept2.lil.ui.study.eval.FixShareUtil;
import com.iyuba.concept2.sqlite.mode.NewWord;
import com.iyuba.concept2.sqlite.mode.VoaDetail;
import com.iyuba.concept2.sqlite.op.NewWordOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.model.remote.bean.Publish_result;
import com.iyuba.core.lil.model.remote.bean.Word_collect;
import com.iyuba.core.lil.model.remote.bean.Word_search;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibDateUtil;
import com.iyuba.core.lil.util.LibEncodeUtil;
import com.iyuba.core.lil.util.LibPermissionDialogUtil;
import com.iyuba.core.lil.util.LibRecordManager;
import com.iyuba.core.lil.util.LibRxTimer;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.core.lil.view.LoadingDialog;
import com.iyuba.core.lil.view.NoScrollLinearLayoutManager;
import com.iyuba.core.me.pay.RequestCallBack;
import com.iyuba.core.search.request.AddCreditsRequest;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 新搜索界面
 * @date: 2023/12/25 16:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchActivity extends BaseStackActivity {

    //参数
    private static final String TAG_word = "word";

    //默认参数(这里数据不动)
    private static final int TAG_pageIndex = 1;
    private static final int TAG_showCount = 3;

    //控件
    private Button btnBack;
    private EditText etInput;
    private ImageView ivDelete;

    private NestedScrollView dataLayout;
    private TextView tvWord;
    private LinearLayout llPlayUS;
    private TextView tvPronUS;
    private LinearLayout llPlayEN;
    private TextView tvPronEN;
    private TextView tvDef;
    private ImageView ivCollect;

    private LinearLayout articleLayout;
    private ImageView ivArticleMore;
    private RecyclerView rvArticle;

    private LinearLayout sentenceLayout;
    private ImageView ivSentenceMore;
    private RecyclerView rvSentence;


    private LinearLayout loadingLayout;
    private ImageView loadingImage;
    private ProgressBar loadingBar;
    private TextView loadingMsg;

    //适配器
    private NewSearchArticleAdapter articleAdapter;
    private NewSearchSentenceAdapter sentenceAdapter;

    //查询内容
    private String searchWord = "";
    //查询出来的内容
    private Word_search searchResultBean;
    //查询单词线程
    private Disposable searchWordDis;
    //收藏单词线程
    private Disposable collectWordDis;

    //单词播放器
    private ExoPlayer wordPlayer;

    /*****************评测相关****************/
    //原音播放器
    private ExoPlayer audioPlayer;
    //原音计时器
    private static final String TAG_audioTimer = "audioTimer";

    //评测播放器
    private ExoPlayer evalPlayer;

    //录音器
    private LibRecordManager recordManager;
    //录音计时器
    private static final String TAG_recordTimer = "recordTimer";
    //是否正在录音评测中
    private boolean isRecordEvaling = false;
    //录音的路径
    private String recordSavePath = null;

    //加载弹窗
    private LoadingDialog loadingDialog;

    public static void start(Context context,String keyWord){
        Intent intent = new Intent();
        intent.setClass(context,NewSearchActivity.class);
        intent.putExtra(TAG_word,keyWord);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_search_new);

        initView();
        initClick();
        initList();
        initPlayer();

        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseWord();
        pauseAudio();
        pauseEval();
        stopRecord();


        LibRxUtil.unDisposable(searchWordDis);
        LibRxUtil.unDisposable(collectWordDis);
        LibRxUtil.unDisposable(submitSentenceEvalDis);
        LibRxUtil.unDisposable(publishSingleEvalDis);
    }

    /*******************************初始化**************************/
    private void initView(){
        btnBack = findViewById(R.id.btnBack);
        etInput = findViewById(R.id.etInput);
        ivDelete = findViewById(R.id.ivDelete);
        
        loadingLayout = findViewById(R.id.loadingLayout);
        loadingImage = findViewById(R.id.loadingImage);
        loadingBar = findViewById(R.id.loadingBar);
        loadingMsg = findViewById(R.id.loadingMsg);

        dataLayout = findViewById(R.id.dataLayout);
        tvWord = findViewById(R.id.word);
        llPlayUS = findViewById(R.id.play_us);
        tvPronUS = findViewById(R.id.pron_us);
        llPlayEN = findViewById(R.id.play_en);
        tvPronEN = findViewById(R.id.pron_en);
        tvDef = findViewById(R.id.def);
        ivCollect = findViewById(R.id.collect);

        articleLayout = findViewById(R.id.articleLayout);
        ivArticleMore = findViewById(R.id.more_article);
        rvArticle = findViewById(R.id.recyclerView_article);

        sentenceLayout = findViewById(R.id.sentenceLayout);
        ivSentenceMore = findViewById(R.id.more_sentence);
        rvSentence = findViewById(R.id.recyclerView_sentence);
    }

    private void initClick(){
        btnBack.setOnClickListener(v->{
            //退出
            finish();
        });
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //单词查询
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchWord = etInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchWord)){
                        searchWord(searchWord);
                    }else {
                        updateUi(false,"查询内容不能为空");
                    }
                    return true;
                }
                return false;
            }
        });
        ivDelete.setOnClickListener(v->{
            //删除
            searchWord = null;
            etInput.setText("");
            updateUi(false,"还没有任何搜索呦～");
        });
        ivCollect.setOnClickListener(v->{
            //收藏单词
            if (TextUtils.isEmpty(searchWord)||searchResultBean==null){
                ToastUtil.showToast(this,"未查询到单词数据");
                return;
            }

            NewWord newWord = new NewWordOp(this).findWordDataByWord(searchWord,UserInfoManager.getInstance().getUserId());
            if (newWord!=null){
                collectWord(searchWord,false);
            }else {
                collectWord(searchWord,true);
            }
        });
        ivArticleMore.setOnClickListener(v->{
            //文章更多
            NewSearchArticleActivity.start(this,searchWord);
        });
        ivSentenceMore.setOnClickListener(v->{
            //句子更多
            NewSearchSentenceActivity.start(this,searchWord);
        });

        llPlayUS.setOnClickListener(v->{
            //单词播放
            if (wordPlayer==null){
                ToastUtil.showToast(this,"单词播放器未初始化");
                return;
            }

            if (wordPlayer.isPlaying()){
                pauseWord();
            }else {
                playWord(searchResultBean.getPh_am_mp3());
            }
        });
        llPlayEN.setOnClickListener(v->{
            //单词播放
            if (wordPlayer==null){
                ToastUtil.showToast(this,"单词播放器未初始化");
                return;
            }

            if (wordPlayer.isPlaying()){
                pauseWord();
            }else {
                playWord(searchResultBean.getPh_en_mp3());
            }
        });
    }

    private void initList(){
        articleAdapter = new NewSearchArticleAdapter(this,new ArrayList<>());
        rvArticle.setLayoutManager(new NoScrollLinearLayoutManager(this,false));
        rvArticle.setAdapter(articleAdapter);
        rvArticle.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        articleAdapter.setOnArticleClickListener(new NewSearchArticleAdapter.OnArticleClickListener() {
            @Override
            public void onItemClick(Word_search.TitleDataBean dataBean) {
                Intent intent = AudioContentActivityNew.buildIntent(
                        NewSearchActivity.this,
                        dataBean.getCategory(),
                        dataBean.getTitle(),
                        dataBean.getTitle_Cn(),
                        dataBean.getPic(),
                        "voa",
                        dataBean.getVoaId(),
                        dataBean.getSound()
                );
                startActivity(intent);
            }
        });

        sentenceAdapter = new NewSearchSentenceAdapter(this,new ArrayList<>());
        rvSentence.setLayoutManager(new NoScrollLinearLayoutManager(this,false));
        rvSentence.setAdapter(sentenceAdapter);
        rvSentence.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        sentenceAdapter.setOnEvalItemClickListener(new NewSearchSentenceAdapter.OnEvalItemClickListener() {
            @Override
            public void onItemSwitch(int position) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中～");
                    return;
                }

                //停止播放
                pauseWord();
                pauseAudio();
                pauseEval();

                sentenceAdapter.refreshItem(position);
            }

            @Override
            public void onPlayAudio(String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中～");
                    return;
                }

                //停止其他音频播放
                pauseWord();
                pauseEval();

                if (!NetworkUtil.isConnected(NewSearchActivity.this)){
                    ToastUtil.showToast(NewSearchActivity.this,"请链接网络后使用");
                    return;
                }

                //判断停止还是显示
                if (audioPlayer!=null){
                    if (audioPlayer.isPlaying()){
                        pauseAudio();
                    }else {
                        playAudio(audioUrl);
                    }
                }else {
                    ToastUtil.showToast(NewSearchActivity.this,"音频播放器未初始化");
                }
            }

            @Override
            public void onRecord(int voaId, String paraId, String lineN, String sentence, long totalTime) {
                //停止其他音频播放
                pauseWord();
                pauseAudio();
                pauseEval();

                if (!NetworkUtil.isConnected(NewSearchActivity.this)){
                    ToastUtil.showToast(NewSearchActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(NewSearchActivity.this);
                    return;
                }

                //检查是否为vip或者超过5个
                /*List<EvalSentenceEntity> evalList = HelpDataManager.getInstance().getMultiEvalData(voaId,UserInfoManager.getInstance().getUserId());
                boolean isCountLimit = evalList!=null&&evalList.size()>=5;
                boolean isVipLimit = !UserInfoManager.getInstance().isVip();
                boolean isEvalLimit = HelpDataManager.getInstance().getSingleEvalData(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN)==null;
                if (isCountLimit&&isVipLimit&&isEvalLimit){
                    new AlertDialog.Builder(NewSearchActivity.this)
                            .setMessage("评测限制")
                            .setMessage("非会员限制评测5个，非会员无限制，是否开通会员使用？")
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    VipCenterGoldActivity.start(NewSearchActivity.this,VipCenterGoldActivity.VIP_APP);
                                }
                            }).setNegativeButton("暂不开通",null)
                            .setCancelable(false)
                            .create().show();
                    return;
                }*/

                //判断录音评测
                if (!isRecordEvaling){
                    List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                    pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO, new Pair<>("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")));
                    pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "保存评测的音频文件，用于评测打分使用")));
                    LibPermissionDialogUtil.getInstance().showMsgDialog(NewSearchActivity.this, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
                        @Override
                        public void onGranted(boolean isSuccess) {
                            if (isSuccess){
                                //这里有个特殊要求，原来的时间不太够，需要设置为原来的1.7倍
                                long recordTime = (long) (totalTime*1.7f);
                                startRecord(voaId,paraId,lineN,sentence,recordTime);
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
            public void onEvalPlay(int voaId, String paraId, String lineN, String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中～");
                    return;
                }

                //停止音频播放
                pauseWord();
                pauseAudio();

                if (!NetworkUtil.isConnected(NewSearchActivity.this)){
                    ToastUtil.showToast(NewSearchActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(NewSearchActivity.this);
                    return;
                }

                if (evalPlayer!=null){
                    if (evalPlayer.isPlaying()){
                        pauseEval();
                    }else {
                        //获取播放路径
                        String playUrl = getEvalPlayUrl(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN,audioUrl);
                        playEval(playUrl);
                    }
                }else {
                    ToastUtil.showToast(NewSearchActivity.this,"评测播放器初始化失败");
                }
            }

            @Override
            public void onPublish(int voaId, String paraId, String lineN, String score, String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中～");
                    return;
                }

                pauseWord();
                pauseAudio();
                pauseEval();

                if (!NetworkUtil.isConnected(NewSearchActivity.this)){
                    ToastUtil.showToast(NewSearchActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(NewSearchActivity.this);
                    return;
                }

                submitSinglePublish(voaId,paraId,lineN,score,audioUrl);
            }

            @Override
            public void onShare(int voaId, String sentence,String score, String audioUrl, int shareId) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中～");
                    return;
                }

                pauseWord();
                pauseAudio();
                pauseEval();

                if (!NetworkUtil.isConnected(NewSearchActivity.this)){
                    ToastUtil.showToast(NewSearchActivity.this,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(NewSearchActivity.this);
                    return;
                }

                String evalAudioUrl = audioUrl;
                shareSingleEval(voaId,sentence,score,shareId,evalAudioUrl);
            }

            @Override
            public void checkWord(Word_search.TextDataBean dataBean, EvalSentenceEntity sentenceEntity) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(NewSearchActivity.this,"正在录音评测中～");
                    return;
                }

                //停止其他音频播放
                pauseWord();
                pauseAudio();
                pauseEval();

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(NewSearchActivity.this);
                    return;
                }

                VoaDetail detail = new VoaDetail();
                detail.voaId = Integer.parseInt(dataBean.getVoaId());
                detail.paraId = dataBean.getParaId();
                detail.lineN = dataBean.getIdIndex();
                detail.sentence = dataBean.getSentence();
                detail.sentenceCn = dataBean.getSentence_cn();

                NewEvalCorrectPage correctPage = new NewEvalCorrectPage();
                //这里将数据保存下载并且传递过去
                correctPage.setUid(UserInfoManager.getInstance().getUserId());
                correctPage.setVoaDetail(detail);
                correctPage.setEvaluateBean(sentenceEntity);
                correctPage.show((NewSearchActivity.this).getFragmentManager(), "EvalFragmentPage");
            }
        });
    }

    private void initPlayer(){
        wordPlayer = new ExoPlayer.Builder(this).build();
        wordPlayer.setPlayWhenReady(false);
        wordPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playWord(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(NewSearchActivity.this,"单词播放器初始化失败");
            }
        });

        audioPlayer = new ExoPlayer.Builder(this).build();
        audioPlayer.setPlayWhenReady(false);
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(NewSearchActivity.this,"原音播放器初始化失败");
            }
        });

        evalPlayer = new ExoPlayer.Builder(this).build();
        evalPlayer.setPlayWhenReady(false);
        evalPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playEval(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseEval();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(NewSearchActivity.this,"评测播放器初始化失败");
            }
        });
    }

    /********************************刷新数据**********************/
    private void refreshData(){
        searchWord = getIntent().getStringExtra(TAG_word);
        if (TextUtils.isEmpty(searchWord)){
            updateUi(false,"还没有任何搜索呦～");
        }else {
            etInput.setText(searchWord);
            searchWord(searchWord);
        }
    }

    //查询单词接口
    private void searchWord(String keyWord){
        updateUi(true,null);

        LessonRemoteManager.searchWordData(keyWord, UserInfoManager.getInstance().getUserId(), TAG_pageIndex,TAG_showCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_search>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        searchWordDis = d;
                    }

                    @Override
                    public void onNext(Word_search bean) {
                        if (bean!=null){
                            updateUi(false,null);

                            searchResultBean = bean;

                            //显示数据
                            showWordData(bean);
                            showArticleData(bean.getTitleData());
                            showSentenceData(bean.getTextData());
                        }else {
                            updateUi(false,"查询单词失败，请重试~");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateUi(false,"查询单词异常，请重试~");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(searchWordDis);
                    }
                });
    }

    //收藏/取消收藏单词接口
    private void collectWord(String keyWord,boolean isCollect){
        String mode = "insert";
        if (!isCollect){
            mode = "del";
        }

        LessonRemoteManager.collectWordData(UserInfoManager.getInstance().getUserId(), mode,keyWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_collect>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(Word_collect bean) {
                        String showStr = "收藏单词";
                        if (!isCollect){
                            showStr = "取消收藏单词";
                        }

                        if (bean!=null&&bean.result==1){
                            if (isCollect){
                                NewWord newWord = new NewWord();
                                newWord.id = String.valueOf(UserInfoManager.getInstance().getUserId());
                                newWord.word = keyWord;
                                newWord.pron = searchResultBean.getPh_am();
                                newWord.def = searchResultBean.getDef();
                                newWord.audio = searchResultBean.getPh_am_mp3();
                                newWord.createDate = LibDateUtil.toDateStr(System.currentTimeMillis(),LibDateUtil.YMDHMSS);
                                new NewWordOp(NewSearchActivity.this).saveData(newWord);

                                ivCollect.setImageResource(R.drawable.ic_study_collected);
                            }else {
                                new NewWordOp(NewSearchActivity.this).deleteWordDataByWord(keyWord,UserInfoManager.getInstance().getUserId());

                                ivCollect.setImageResource(R.drawable.ic_study_collect);
                            }

                            ToastUtil.showToast(NewSearchActivity.this,showStr+"成功");
                        }else {
                            ToastUtil.showToast(NewSearchActivity.this,showStr+"失败("+bean.result+")，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String showStr = "收藏单词";
                        if (!isCollect){
                            showStr = "取消收藏单词";
                        }
                        ToastUtil.showToast(NewSearchActivity.this,showStr+"异常("+e.getMessage()+")");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(collectWordDis);
                    }
                });
    }
    /********************************单词数据操作********************/
    private void showWordData(Word_search bean){
        if (TextUtils.isEmpty(bean.getWord())){
            updateUi(false,"暂无当前单词数据");
            return;
        }

        tvWord.setText(bean.getWord());

        NewWord newWord = new NewWordOp(this).findWordDataByWord(bean.getWord(),UserInfoManager.getInstance().getUserId());
        if (newWord!=null){
            ivCollect.setImageResource(R.drawable.ic_study_collected);
        }else {
            ivCollect.setImageResource(R.drawable.ic_study_collect);
        }

        String usPron = LibEncodeUtil.decode(bean.getPh_am());
        if (TextUtils.isEmpty(usPron)){
            tvPronUS.setText("");
        }else {
            tvPronUS.setText("美音 /"+usPron+"/");
        }

        String enPron = LibEncodeUtil.decode(bean.getPh_en());
        if (TextUtils.isEmpty(enPron)){
            tvPronEN.setText("");
        }else {
            tvPronEN.setText("英音 /"+enPron+"/");
        }
        tvDef.setText(bean.getWordCn());
    }

    //播放音频
    private void playWord(String audioUrl){
        if (!TextUtils.isEmpty(audioUrl)){
            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            wordPlayer.setMediaItem(mediaItem);
            wordPlayer.prepare();
        }else {
            wordPlayer.play();
        }
    }

    //暂停音频
    private void pauseWord(){
        if (wordPlayer!=null&&wordPlayer.isPlaying()){
            wordPlayer.pause();
        }
    }
    /*********************************文章数据操作*******************/
    private void showArticleData(List<Word_search.TitleDataBean> list){
        if (list!=null&&list.size()>0){
            articleLayout.setVisibility(View.VISIBLE);

            articleAdapter.refreshData(list);
        }else {
            articleLayout.setVisibility(View.GONE);
        }
    }

    /*********************************句子数据操作********************/
    private void showSentenceData(List<Word_search.TextDataBean> list){
        if (list!=null&&list.size()>0){
            sentenceLayout.setVisibility(View.VISIBLE);

            sentenceAdapter.refreshData(list);
            sentenceAdapter.refreshItem(0);
        }else {
            sentenceLayout.setVisibility(View.GONE);
        }
    }


    /******原音******/
    private void playAudio(String audioUrl){
        if (!TextUtils.isEmpty(audioUrl)){
            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            audioPlayer.setMediaItem(mediaItem);
            audioPlayer.prepare();
        }else {
            //计算时间显示
            audioPlayer.play();

            startAudioTimer(0,audioPlayer.getDuration());
        }
    }

    private void pauseAudio(){
        if (audioPlayer!=null&&audioPlayer.isPlaying()){
            audioPlayer.pause();
        }
        stopAudioTimer();
        sentenceAdapter.refreshPlayView(0,0,false);
    }

    private void startAudioTimer(long startTime,long endTime){
        LibRxTimer.getInstance().multiTimerInMain(TAG_audioTimer, 0, 100L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long progressTime = audioPlayer.getCurrentPosition()-startTime;
                long totalTime = endTime-startTime;
                sentenceAdapter.refreshPlayView(progressTime,totalTime,true);

                if (progressTime>=totalTime){
                    pauseAudio();
                }
            }
        });
    }

    private void stopAudioTimer(){
        LibRxTimer.getInstance().cancelTimer(TAG_audioTimer);
    }

    /******评测音频******/
    private void playEval(String urlOrPath){
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

            sentenceAdapter.refreshEvalView(0,0,true);
        }
    }

    private void pauseEval(){
        if (evalPlayer != null && evalPlayer.isPlaying()) {
            evalPlayer.pause();
        }

        sentenceAdapter.refreshEvalView(0,0,false);
    }

    //获取评测的音频文件路径
    private String getEvalPlayUrl(int voaId,int userId,String paraId,String lineN,String audioUrl){
        String localPath = getRecordFilePath(voaId,userId,paraId,lineN,false);
        File file = new File(localPath);
        if (file.exists()){
            return localPath;
        }

        //http://iuserspeech.iyuba.cn:9001/voa/wav7/202312/familyalbum/20231225/17034689874207236.mp3
        String urlPre = "http://iuserspeech."+ Constant.IYBHttpHead()+":9001/voa/";
        return urlPre+audioUrl;
    }

    /******录音******/
    private void startRecord(int voaId,String paraId,String lineN,String sentence,long recordTime){
        recordSavePath = getRecordFilePath(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN,true);
        if (recordSavePath==null){
            ToastUtil.showToast(this,"录音文件初始化失败");
            return;
        }

        isRecordEvaling = true;
        recordManager = new LibRecordManager(new File(recordSavePath));
        recordManager.startRecord();

        startRecordTimer(voaId,paraId,lineN,sentence,recordTime);
    }

    private void stopRecord(){
        if (recordManager!=null){
            recordManager.stopRecord();
        }

        sentenceAdapter.refreshRecordView(0,0,false);
        stopRecordTimer();
    }

    private void startRecordTimer(int voaId,String paraId,String lineN,String sentence,long totalTime){
        LibRxTimer.getInstance().multiTimerInMain(TAG_recordTimer, 0, 100L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                int volume = (int) recordManager.getVolume();
                int max = 100;
                sentenceAdapter.refreshRecordView(volume,max,true);

                long recordTime = number*100L;
                if (recordTime>=totalTime){
                    stopRecord();
                    submitEval(voaId,paraId,lineN,sentence,recordSavePath);
                }
            }
        });
    }

    private void stopRecordTimer(){
        LibRxTimer.getInstance().cancelTimer(TAG_recordTimer);
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
        showLoading("正在提交句子评测。。。");

        LessonRemoteManager.submitEvalSentenceData(voaId,UserInfoManager.getInstance().getUserId(), paraId,lineN,sentence,recordPath)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<com.iyuba.core.lil.remote.bean.base.BaseBean_data<Eval_result>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitSentenceEvalDis = d;
                    }

                    @Override
                    public void onNext(com.iyuba.core.lil.remote.bean.base.BaseBean_data<Eval_result> bean) {
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
                            sentenceAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.showToast(NewSearchActivity.this,"提交评测失败，请重试("+bean.getResult()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        isRecordEvaling = false;
                        ToastUtil.showToast(NewSearchActivity.this,"提交评测异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(submitSentenceEvalDis);
                    }
                });
    }

    /*********************提交单句评测****************/
    private Disposable publishSingleEvalDis;
    public void submitSinglePublish(int voaId,String paraId,String lineN,String score,String audioUrl){
        showLoading("正在发布评测。。。");

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
                            ToastUtil.showToast(NewSearchActivity.this,"发布单句成功");
                            sentenceAdapter.saveShareId(voaId,paraId,lineN,bean.getShuoshuoId());
                            sentenceAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.showToast(NewSearchActivity.this,"发布单句评测失败，请重试("+bean.getResultCode()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(NewSearchActivity.this,"发布单句评测异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(publishSingleEvalDis);
                    }
                });
    }

    /*********************分享单句评测**********************/
    private void shareSingleEval(int voaId,String sentence,String score,int shareId,String evalAudioUrl){
        PlatformActionListener pal = new PlatformActionListener() {

            @Override
            public void onError(Platform platform, int arg1, Throwable arg2) {
                CustomToast.showToast(NewSearchActivity.this, "分享失败", 1000);
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
                    com.iyuba.core.me.pay.RequestCallBack rc = new RequestCallBack() {

                        @Override
                        public void requestResult(Request result) {
                            com.iyuba.core.search.request.AddCreditsRequest rq = (com.iyuba.core.search.request.AddCreditsRequest) result;
                            if (rq.isShareFirstlySuccess()) {
                                String msg = "分享成功，增加了"
                                        + rq.addCredit + "积分，共有"
                                        + rq.totalCredit + "积分";
                                CustomToast.showToast(NewSearchActivity.this, msg, 3000);
                            } else if (rq.isShareRepeatlySuccess()) {
                                CustomToast.showToast(NewSearchActivity.this, "分享成功", 3000);
                            }
                        }
                    };
                    int uid = UserInfoManager.getInstance().getUserId();
                    com.iyuba.core.search.request.AddCreditsRequest rq = new AddCreditsRequest(
                            uid,
                            voaId,
                            srid, rc);
                    RequestQueue queue = Volley.newRequestQueue(NewSearchActivity.this);
                    queue.add(rq);
                }
            }

            @Override
            public void onCancel(Platform platform, int arg1) {
                CustomToast.showToast(NewSearchActivity.this, "分享已取消", 1000);
            }
        };
        String name = Constant.APPName;
        String shareTitle = "[" + UserInfoManager.getInstance().getUserName() + "]"
                + "在" + name + "中获得了"
                + score
                + "分";
        String shortText = sentence;

        //这里可能需要替换，先准备下
        String ArticleShareUrl = "http://voa." + Constant.IYBHttpHead() + "/voa/play.jsp?id=" + shareId;

        //分享操作
        FixShareUtil.showShare(NewSearchActivity.this, shareTitle, shortText, ArticleShareUrl,
                Constant.AppIcon(), "很不错的应用，大家快来使用呀！", Constant.APPName,
                ArticleShareUrl, pal);
    }

    /***********************************其他操作**********************/
    //刷新ui显示
    private void updateUi(boolean isLoading,String showMsg){
        if (isLoading){
            etInput.setEnabled(false);
            ivDelete.setVisibility(View.INVISIBLE);

            loadingLayout.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);

            loadingImage.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);
            loadingMsg.setText("正在查询单词数据~");
        }else {
            etInput.setEnabled(true);
            ivDelete.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(showMsg)){
                loadingLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);

                loadingImage.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                loadingMsg.setText(showMsg);
            }else {
                dataLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    //显示加载弹窗
    private void showLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
