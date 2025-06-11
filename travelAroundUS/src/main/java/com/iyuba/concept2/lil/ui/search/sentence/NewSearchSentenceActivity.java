package com.iyuba.concept2.lil.ui.search.sentence;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.iyuba.concept2.lil.ui.study.eval.FixShareUtil;
import com.iyuba.concept2.sqlite.mode.VoaDetail;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.model.remote.bean.Publish_result;
import com.iyuba.core.lil.model.remote.bean.Word_search;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibPermissionDialogUtil;
import com.iyuba.core.lil.util.LibRecordManager;
import com.iyuba.core.lil.util.LibRxTimer;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.core.lil.view.LoadingDialog;
import com.iyuba.core.me.pay.RequestCallBack;
import com.iyuba.core.search.request.AddCreditsRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

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
 * @title: 新的搜索-句子界面
 * @date: 2023/12/26 09:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchSentenceActivity extends BaseStackActivity {

    //参数
    private static final String TAG_word = "word";

    //默认参数
    private int pageIndex = 1;
    private static final int TAG_showCount = 10;

    //控件
    private Button btnBack;
    private TextView tvTitle;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    //适配器
    private NewSearchSentenceAdapter sentenceAdapter;

    //查询内容
    private String searchWord = "";

    //数据
    private Disposable searchWordDis;
    private Context context;

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

    public static void start(Context context, String keyWord){
        Intent intent = new Intent();
        intent.setClass(context, NewSearchSentenceActivity.class);
        intent.putExtra(TAG_word,keyWord);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_search_new_more);

        context = this;
        searchWord = getIntent().getStringExtra(TAG_word);

        initView();
        initToolbar();
        initList();
        initPlayer();

        refreshLayout.autoRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LibRxUtil.unDisposable(searchWordDis);
    }

    /***************************初始化界面************************/
    private void initToolbar(){
        btnBack.setOnClickListener(v->{
            finish();
        });

        String showTitle = TextUtils.isEmpty(searchWord)?"更多句子":searchWord+" 的更多句子";
        tvTitle.setText(showTitle);
    }

    private void initView(){
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList(){
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(NewSearchSentenceActivity.this)){
                    refreshLayout.finishLoadMore();
                    ToastUtil.showToast(NewSearchSentenceActivity.this,"请链接网络后重试");
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(NewSearchSentenceActivity.this)){
                    refreshLayout.finishRefresh();
                    ToastUtil.showToast(NewSearchSentenceActivity.this,"请链接网络后重试");
                    return;
                }

                pageIndex = 1;
                refreshData();
            }
        });

        sentenceAdapter = new NewSearchSentenceAdapter(this,new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sentenceAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        sentenceAdapter.setOnEvalItemClickListener(new NewSearchSentenceAdapter.OnEvalItemClickListener() {
            @Override
            public void onItemSwitch(int position) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(context,"正在录音评测中～");
                    return;
                }

                //停止播放
                pauseAudio();
                pauseEval();

                sentenceAdapter.refreshItem(position);
            }

            @Override
            public void onPlayAudio(String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(context,"正在录音评测中～");
                    return;
                }

                //停止其他音频播放
                pauseEval();

                if (!NetworkUtil.isConnected(context)){
                    ToastUtil.showToast(context,"请链接网络后使用");
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
                    ToastUtil.showToast(context,"音频播放器未初始化");
                }
            }

            @Override
            public void onRecord(int voaId, String paraId, String lineN, String sentence, long totalTime) {
                //停止其他音频播放
                pauseAudio();
                pauseEval();

                if (!NetworkUtil.isConnected(context)){
                    ToastUtil.showToast(context,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(context);
                    return;
                }

                //这里不判断是否超过数量限制

                //判断录音评测
                if (!isRecordEvaling){
                    List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                    pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO, new Pair<>("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")));
                    pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "保存评测的音频文件，用于评测打分使用")));
                    LibPermissionDialogUtil.getInstance().showMsgDialog(context, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
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
                    ToastUtil.showToast(context,"正在录音评测中～");
                    return;
                }

                //停止音频播放
                pauseAudio();

                if (!NetworkUtil.isConnected(context)){
                    ToastUtil.showToast(context,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(context);
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
                    ToastUtil.showToast(context,"评测播放器初始化失败");
                }
            }

            @Override
            public void onPublish(int voaId, String paraId, String lineN, String score, String audioUrl) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(context,"正在录音评测中～");
                    return;
                }

                pauseAudio();
                pauseEval();

                if (!NetworkUtil.isConnected(context)){
                    ToastUtil.showToast(context,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(context);
                    return;
                }

                submitSinglePublish(voaId,paraId,lineN,score,audioUrl);
            }

            @Override
            public void onShare(int voaId, String sentence, String score, String audioUrl, int shareId) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(context,"正在录音评测中～");
                    return;
                }

                pauseAudio();
                pauseEval();

                if (!NetworkUtil.isConnected(context)){
                    ToastUtil.showToast(context,"请链接网络后使用");
                    return;
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(context);
                    return;
                }

                String evalAudioUrl = audioUrl;
                shareSingleEval(voaId,sentence,score,shareId,evalAudioUrl);
            }

            @Override
            public void checkWord(Word_search.TextDataBean dataBean, EvalSentenceEntity sentenceEntity) {
                //检测录音
                if (isRecordEvaling){
                    ToastUtil.showToast(context,"正在录音评测中～");
                    return;
                }

                //停止其他音频播放
                pauseAudio();
                pauseEval();

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(context);
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
                correctPage.show(getFragmentManager(), "EvalFragmentPage");
            }
        });
    }

    private void initPlayer(){
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
                ToastUtil.showToast(context,"原音播放器初始化失败");
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
                ToastUtil.showToast(context,"评测播放器初始化失败");
            }
        });
    }

    /*****************************刷新数据************************/
    private void refreshData(){
        if (TextUtils.isEmpty(searchWord)){
            stopRefreshAndMore(false);
            ToastUtil.showToast(this,"未查询到单词数据");
            return;
        }

        LessonRemoteManager.searchWordData(searchWord, UserInfoManager.getInstance().getUserId(), pageIndex,TAG_showCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_search>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        searchWordDis = d;
                    }

                    @Override
                    public void onNext(Word_search bean) {
                        stopRefreshAndMore(true);

                        if (bean!=null&&bean.getTextData()!=null&&bean.getTextData().size()>0){
                            //刷新数据显示
                            sentenceAdapter.refreshData(bean.getTextData());
                            if (pageIndex==1){
                                sentenceAdapter.refreshItem(0);
                            }

                            pageIndex++;
                        }else {
                            ToastUtil.showToast(NewSearchSentenceActivity.this,"暂无更多数据");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopRefreshAndMore(true);
                        ToastUtil.showToast(NewSearchSentenceActivity.this,"获取句子数据异常，请重试～");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(searchWordDis);
                    }
                });
    }

    /*******************************原文音频**********************/
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

    /*******************************录音**********************/
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
                            ToastUtil.showToast(context,"提交评测失败，请重试("+bean.getResult()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        isRecordEvaling = false;
                        ToastUtil.showToast(context,"提交评测异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(submitSentenceEvalDis);
                    }
                });
    }

    /*******************************评测音频**********************/
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

    /*******************************发布单句评测**********************/
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
                            ToastUtil.showToast(context,"发布单句成功");
                            sentenceAdapter.saveShareId(voaId,paraId,lineN,bean.getShuoshuoId());
                            sentenceAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.showToast(context,"发布单句评测失败，请重试("+bean.getResultCode()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(context,"发布单句评测异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(publishSingleEvalDis);
                    }
                });
    }

    /*******************************分享单句评测**********************/
    private void shareSingleEval(int voaId,String sentence,String score,int shareId,String evalAudioUrl){
        PlatformActionListener pal = new PlatformActionListener() {

            @Override
            public void onError(Platform platform, int arg1, Throwable arg2) {
                CustomToast.showToast(context, "分享失败", 1000);
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
                                CustomToast.showToast(context, msg, 3000);
                            } else if (rq.isShareRepeatlySuccess()) {
                                CustomToast.showToast(context, "分享成功", 3000);
                            }
                        }
                    };
                    int uid = UserInfoManager.getInstance().getUserId();
                    com.iyuba.core.search.request.AddCreditsRequest rq = new AddCreditsRequest(
                            uid,
                            voaId,
                            srid, rc);
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(rq);
                }
            }

            @Override
            public void onCancel(Platform platform, int arg1) {
                CustomToast.showToast(context, "分享已取消", 1000);
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
        FixShareUtil.showShare(context, shareTitle, shortText, ArticleShareUrl,
                Constant.AppIcon(), "很不错的应用，大家快来使用呀！", Constant.APPName,
                ArticleShareUrl, pal);
    }

    /********************************其他功能**************************/
    //关闭刷新和加载
    private void stopRefreshAndMore(boolean isSuccess){
        refreshLayout.finishRefresh(isSuccess);
        refreshLayout.finishLoadMore(isSuccess);
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
