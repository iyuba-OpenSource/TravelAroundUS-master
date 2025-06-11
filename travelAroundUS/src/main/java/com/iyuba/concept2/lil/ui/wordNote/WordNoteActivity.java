package com.iyuba.concept2.lil.ui.wordNote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efs.sdk.base.core.util.NetworkUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.concept2.R;
import com.iyuba.concept2.lil.ui.search.NewSearchActivity;
import com.iyuba.concept2.sqlite.mode.NewWord;
import com.iyuba.concept2.sqlite.op.NewWordOp;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.Word_collect;
import com.iyuba.core.lil.remote.bean.Word_note;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 生词本功能
 * @date: 2023/12/18 09:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordNoteActivity extends BaseStackActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvMore;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    //当前页码
    private int pageIndex = 1;
    //显示数量
    private int showCount = 30;

    //播放器
    private ExoPlayer exoPlayer;
    //适配器
    private WordNoteAdapter wordNoteAdapter;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,WordNoteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_word_note);

        initView();
        initToolbar();
        initList();
        initPlayer();

        getLocalData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LibRxUtil.unDisposable(wordNoteDis);
        LibRxUtil.unDisposable(collectWordDis);
    }

    /*************************初始化**************************/
    private void initView(){
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvMore = findViewById(R.id.tv_more);

        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initToolbar(){
        tvTitle.setText("生词本");
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText("编辑");
        tvMore.setOnClickListener(v->{
            String showText = tvMore.getText().toString().trim();
            if (showText.equals("编辑")){
                wordNoteAdapter.setStatus(true);
                tvMore.setText("取消");
            }else if (showText.equals("删除")){
                //删除单词
                deleteCollectWord();
            }else if (showText.equals("取消")){
                wordNoteAdapter.setStatus(false);
                tvMore.setText("编辑");
            }
        });
        ivBack.setOnClickListener(v->{
            finish();
        });
    }

    private void initList(){
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WordNoteActivity.this)){
                    refreshLayout.finishRefresh();
                    ToastUtil.showToast(WordNoteActivity.this,"请链接网络后使用");
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WordNoteActivity.this)){
                    refreshLayout.finishRefresh();
                    ToastUtil.showToast(WordNoteActivity.this,"请链接网络后使用");
                    return;
                }

                pageIndex = 1;
                refreshData();
            }
        });

        wordNoteAdapter = new WordNoteAdapter(this,new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(wordNoteAdapter);
        wordNoteAdapter.setOnItemClickListener(new WordNoteAdapter.OnItemClickListener() {
            @Override
            public void onPlay(String audioUrl) {
                if (!NetworkUtil.isConnected(WordNoteActivity.this)){
                    ToastUtil.showToast(WordNoteActivity.this,"当前网络不可用");
                    return;
                }

                if (TextUtils.isEmpty(audioUrl)){
                    ToastUtil.showToast(WordNoteActivity.this,"音频链接不存在");
                    return;
                }

                if (exoPlayer!=null){
                    if (exoPlayer.isPlaying()){
                        pauseAudio();
                    }else {
                        playAudio(audioUrl);
                    }
                }else {
                    ToastUtil.showToast(WordNoteActivity.this,"播放器未初始化");
                }
            }

            @Override
            public void onSelect(int allSelectCount) {
                if (allSelectCount>0){
                    tvMore.setText("删除");
                }else {
                    tvMore.setText("取消");
                }
            }

            @Override
            public void onSearch(String keyWord) {
                if (!NetworkUtil.isConnected(WordNoteActivity.this)){
                    ToastUtil.showToast(WordNoteActivity.this,"当前网络不可用");
                    return;
                }

                pauseAudio();

                NewSearchActivity.start(WordNoteActivity.this,keyWord);
            }
        });
    }

    /***************************刷新数据**************************/
    private Disposable wordNoteDis;
    private void refreshData(){
        LibRxUtil.unDisposable(wordNoteDis);
        LessonRemoteManager.getWordNoteData(UserInfoManager.getInstance().getUserId(), pageIndex,showCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_note>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        wordNoteDis = d;
                    }

                    @Override
                    public void onNext(Word_note bean) {
                        refreshLayout.finishRefresh(true);
                        refreshLayout.finishLoadMore(true);

                        //这里有点开玩笑了，数据库中竟然没有主键，这里只能核对数据进行处理了
                        saveNetWordData(bean.list);

                        //判断如何显示数据
                        if (bean!=null&&bean.list!=null&&bean.list.size()>0){
                            //查询本地数据并刷新
                            List<NewWord> localList = new NewWordOp(WordNoteActivity.this).findAllData(String.valueOf(UserInfoManager.getInstance().getUserId()));
                            wordNoteAdapter.refreshData(localList);

                            pageIndex++;
                        }else {
                            if (pageIndex==1){
                                ToastUtil.showToast(WordNoteActivity.this,"暂无生词数据");
                            }else {
                                ToastUtil.showToast(WordNoteActivity.this,"暂无更多生词数据");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.finishRefresh(false);
                        refreshLayout.finishLoadMore(false);

                        //显示错误
                        ToastUtil.showToast(WordNoteActivity.this,"查询生词数据异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(wordNoteDis);
                    }
                });
    }

    private void getLocalData(){
        //这里如果网络可用，则加载数据；反之则从本地获取数据
        if (NetworkUtil.isConnected(this)){
            refreshLayout.autoRefresh();
            return;
        }

        List<NewWord> localList = new NewWordOp(this).findAllData(String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (localList!=null&&localList.size()>0){
            wordNoteAdapter.refreshData(localList);
        }else {
            ToastUtil.showToast(this,"请链接网络后获取生词数据");
        }
    }

    /******************************音频***************************/
    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(WordNoteActivity.this,"播放器加载失败");
            }
        });
    }

    private void playAudio(String audioUrl){
        if (!TextUtils.isEmpty(audioUrl)){
            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }else {
            exoPlayer.play();
        }
    }

    private void pauseAudio(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    /********************************收藏/取消收藏接口************************/
    private Disposable collectWordDis;
    private void deleteCollectWord(){
        //需要删除的数据
        List<NewWord> deleteLit = wordNoteAdapter.getSelectData();
        if (deleteLit==null||deleteLit.size()==0){
            ToastUtil.showToast(this,"不存在需要删除的生词数据");
            return;
        }

        showLoading("正在删除生词数据...");

        //获取选中的数据
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < deleteLit.size(); i++) {
            NewWord newWord = deleteLit.get(i);
            buffer.append(newWord.word);

            if (i!=deleteLit.size()-1){
                buffer.append(",");
            }
        }

        //调用接口删除
        LessonRemoteManager.collectWordData(UserInfoManager.getInstance().getUserId(), "delete",buffer.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_collect>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(Word_collect word_collect) {
                        stopLoading();

                        //删除数据
                        for (int i = 0; i < deleteLit.size(); i++) {
                            NewWord temp = deleteLit.get(i);
                            new  NewWordOp(WordNoteActivity.this).deleteWordDataByWord(temp.word,UserInfoManager.getInstance().getUserId());
                        }

                        //刷新界面显示
                        tvMore.setText("编辑");
                        wordNoteAdapter.setStatus(false);
                        //刷新数据显示
                        refreshData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(WordNoteActivity.this,"删除生词数据异常("+e.getMessage()+")");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(collectWordDis);
                    }
                });
    }

    /***********************************其他方法****************************/
    //将单词数据保存在本地
    private void saveNetWordData(List<Word_note.TempWord> list){
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Word_note.TempWord tempWord = list.get(i);

                //从数据库中看下是否存在数据
                NewWord saveData = new NewWordOp(this).findWordDataByWord(tempWord.Word,UserInfoManager.getInstance().getUserId());
                if (saveData!=null){
                    continue;
                }

                NewWord newWord = new NewWord();
                newWord.word = tempWord.Word;
                newWord.audio = tempWord.Audio;
                newWord.pron = tempWord.Pron;
                newWord.def = tempWord.Def;
                newWord.createDate = tempWord.createDate;
                newWord.id = String.valueOf(UserInfoManager.getInstance().getUserId());

                new  NewWordOp(this).saveData(newWord);
            }
        }
    }

    //加载弹窗
    private LoadingDialog loadingDialog;

    private void showLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
