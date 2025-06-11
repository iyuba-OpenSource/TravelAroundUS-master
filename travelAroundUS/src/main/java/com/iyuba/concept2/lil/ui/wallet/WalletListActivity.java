package com.iyuba.concept2.lil.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efs.sdk.base.core.util.NetworkUtil;
import com.iyuba.concept2.R;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.remote.bean.Reward_history;
import com.iyuba.core.lil.remote.bean.base.BaseBean_data;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibResUtil;
import com.iyuba.core.lil.util.LibRxUtil;
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
 * @title: 钱包列表界面
 * @date: 2023/8/22 18:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WalletListActivity extends AppCompatActivity {

    private WalletListAdapter listAdapter;

    //起始数据
    private int pages = 1;
    //每页的数量
    private int pageCount = 20;
    //是否刷新状态
    private boolean isRefresh = true;

    //控件
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;

    private Button backView;
    private TextView titleView;
    private ImageView tipsView;

    private TextView typeView;
    private TextView rewardView;
    private TextView timeView;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,WalletListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_list);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initView();
        initToolbar();
        initList();

        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*********************初始化*******************/
    private void initView(){
        backView = findViewById(R.id.backView);
        titleView = findViewById(R.id.titleView);
        tipsView = findViewById(R.id.tipsView);

        typeView = findViewById(R.id.type);
        rewardView = findViewById(R.id.reward);
        timeView = findViewById(R.id.time);

        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.refreshLayout);
    }

    private void initToolbar(){
        titleView.setText("奖励历史记录");
        backView.setVisibility(View.VISIBLE);
        backView.setBackgroundResource(R.drawable.back_button);
        backView.setOnClickListener(v->{
            finish();
        });
        tipsView.setVisibility(View.VISIBLE);
        tipsView.setBackgroundResource(0);
        tipsView.setImageResource(R.drawable.ic_tips);
        tipsView.setOnClickListener(v->{
            String showMsg = "当前钱包金额:" + UserInfoManager.getInstance().getMoney() + "元,满10元可在[爱语吧]微信公众号提现(关注绑定爱语吧账号)";
            new AlertDialog.Builder(this)
                    .setTitle("奖励说明")
                    .setMessage(showMsg)
                    .show();
        });

        typeView.setText("类型");
        typeView.setTextColor(LibResUtil.getInstance().getColor(R.color.colorPrimary));
        rewardView.setText("金额(元)");
        rewardView.setTextColor(LibResUtil.getInstance().getColor(R.color.colorPrimary));
        timeView.setText("时间");
        timeView.setTextColor(LibResUtil.getInstance().getColor(R.color.colorPrimary));
    }

    private void initList(){
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WalletListActivity.this)){
                    stopRefreshAndMore(false);
                    ToastUtil.showToast(WalletListActivity.this,"请链接网络后重试~");
                    return;
                }

                isRefresh = false;
                getWalletHistory(pages,pageCount);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WalletListActivity.this)){
                    stopRefreshAndMore(false);
                    ToastUtil.showToast(WalletListActivity.this,"请链接网络后重试~");
                    return;
                }

                pages = 1;
                isRefresh = true;
                getWalletHistory(pages,pageCount);
            }
        });

        listAdapter = new WalletListAdapter(this,new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(listAdapter);
    }

    /**********************刷新数据*****************/
    private void refreshData(){
        refreshLayout.autoRefresh();
    }

    private void stopRefreshAndMore(boolean isFinish){
        refreshLayout.finishRefresh(isFinish);
        refreshLayout.finishLoadMore(isFinish);
    }

    /********************回调数据********************/
    public void showRewardHistory(List<Reward_history> list) {
        if (list==null){
            stopRefreshAndMore(false);
            ToastUtil.showToast(this,"查询奖励的历史记录失败~");
            return;
        }

        stopRefreshAndMore(true);
        if (list.size()==0){
            if (isRefresh){
                ToastUtil.showToast(this,"当前账号暂无奖励记录~");
            }else {
                ToastUtil.showToast(this,"当前账号暂无更多奖励记录~");
            }
            return;
        }

        if (list.size()>0){
            pages++;
        }

        if (isRefresh){
            listAdapter.refreshData(list,false);
        }else {
            listAdapter.refreshData(list,true);
        }
    }

    /***********************************接口数据*******************************/
    private Disposable walletHistoryDis;
    //获取钱包历史记录
    private void getWalletHistory(int pages,int pageCount){
        LibRxUtil.unDisposable(walletHistoryDis);
        LessonRemoteManager.getWalletHistory(UserInfoManager.getInstance().getUserId(), pages,pageCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Reward_history>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        walletHistoryDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Reward_history>> bean) {
                        if (bean!=null&&bean.getResult().equals("200")){
                            showRewardHistory(bean.getData());
                        }else {
                            showRewardHistory(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showRewardHistory(null);
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(walletHistoryDis);
                    }
                });
    }
}
