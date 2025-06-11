package com.iyuba.concept2.lil.ui.search.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efs.sdk.base.core.util.NetworkUtil;
import com.iyuba.concept2.R;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.Word_search;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 新的搜索-文章界面
 * @date: 2023/12/26 09:31
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchArticleActivity extends BaseStackActivity {

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
    private NewSearchArticleAdapter articleAdapter;

    //查询内容
    private String searchWord = "";

    //数据
    private Disposable searchWordDis;

    public static void start(Context context,String keyWord){
        Intent intent = new Intent();
        intent.setClass(context,NewSearchArticleActivity.class);
        intent.putExtra(TAG_word,keyWord);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_search_new_more);

        searchWord = getIntent().getStringExtra(TAG_word);

        initView();
        initToolbar();
        initList();

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

        String showTitle = TextUtils.isEmpty(searchWord)?"更多文章":searchWord+" 的更多文章";
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
                if (!NetworkUtil.isConnected(NewSearchArticleActivity.this)){
                    refreshLayout.finishLoadMore();
                    ToastUtil.showToast(NewSearchArticleActivity.this,"请链接网络后重试");
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(NewSearchArticleActivity.this)){
                    refreshLayout.finishRefresh();
                    ToastUtil.showToast(NewSearchArticleActivity.this,"请链接网络后重试");
                    return;
                }

                pageIndex = 1;
                refreshData();
            }
        });

        articleAdapter = new NewSearchArticleAdapter(this,new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(articleAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        articleAdapter.setOnArticleClickListener(new NewSearchArticleAdapter.OnArticleClickListener() {
            @Override
            public void onItemClick(Word_search.TitleDataBean dataBean) {
                if (dataBean==null){
                    ToastUtil.showToast(NewSearchArticleActivity.this,"课程内容位空，请重试~");
                    return;
                }

                Intent intent = AudioContentActivity.getIntent2Me(
                        NewSearchArticleActivity.this,
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

                        if (bean!=null&&bean.getTitleData()!=null&&bean.getTitleData().size()>0){
                            //刷新数据显示
                            articleAdapter.refreshData(bean.getTitleData());

                            pageIndex++;
                        }else {
                            ToastUtil.showToast(NewSearchArticleActivity.this,"暂无更多数据");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopRefreshAndMore(true);
                        ToastUtil.showToast(NewSearchArticleActivity.this,"获取文章数据异常，请重试～");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(searchWordDis);
                    }
                });
    }

    /********************************其他功能**************************/
    //关闭刷新和加载
    private void stopRefreshAndMore(boolean isSuccess){
        refreshLayout.finishRefresh(isSuccess);
        refreshLayout.finishLoadMore(isSuccess);
    }
}
