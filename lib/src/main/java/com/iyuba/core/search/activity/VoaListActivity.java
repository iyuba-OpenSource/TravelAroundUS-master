package com.iyuba.core.search.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.search.adapter.VoaListAdapter;
import com.iyuba.core.search.bean.SearchInfoBean;
import com.iyuba.core.search.request.SearchListRequest;
import com.iyuba.core.search.request.SearchListResponse;
import com.iyuba.lib.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Objects;

import butterknife.ButterKnife;

/**
 * 搜索文章列表
 * zh
 * 2018.12.19
 */
public class VoaListActivity extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView mRvVoaList;
    private SmartRefreshLayout srlRefreshLayout;
    private Context mContext;
    private String mKey;
    private SearchInfoBean searchInfoBean, moreVoaBean;
    private CustomDialog mWaitingDialog;
    private VoaListAdapter mAdapter;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voa_list);
        ButterKnife.bind(this);

        mContext = this;

        btnBack=findViewById(R.id.btn_back);
        mRvVoaList=findViewById(R.id.rv_voa_list);
        srlRefreshLayout=findViewById(R.id.srl_refresh_layout);
        getExtra();
        mWaitingDialog = WaittingDialog.showDialog(mContext);//加载过度
        initRecycleView();
        getData(mPage);
        initListener();
        initRefreshLayout();
    }

    public static void start(Context context, String key) {
        Intent intent = new Intent(context, VoaListActivity.class);
        intent.putExtra("key", key);
        context.startActivity(intent);
    }

    private void getExtra() {
        mKey = getIntent().getStringExtra("key");
    }

    private void getData(final int page) {
        Message msg=new Message();
        msg.what=404;
        handler.sendMessageDelayed(msg,3000);
        //handler.sendEmptyMessage(4);//加载过度
        int mPageNum = 10;
        ExeProtocol.exe(
                new SearchListRequest(mKey, page, mPageNum, 1,
                        UserInfoManager.getInstance().getUserId()),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        SearchListResponse response = (SearchListResponse) bhr;
                        String request = response.searchInfoBean.toString();
                        //ToastUtil.showToast(mContext, "请求结果：" + request.substring(10));
                        LogUtil.d(" 请求结果:" + request);
                        handler.sendEmptyMessage(3);//隐藏过度视图
                        if (page == 1) {
                            searchInfoBean = response.searchInfoBean;
                            mAdapter.setData(searchInfoBean);
                            srlRefreshLayout.finishRefresh(true);
                        } else {
                            moreVoaBean = response.searchInfoBean;
                            if (moreVoaBean.getTitleData()!=null&&moreVoaBean.getTitleData().size()>0) {
                                mAdapter.appendData(moreVoaBean);
                            srlRefreshLayout.finishLoadMore(true);
                            }else {
                                srlRefreshLayout.finishLoadMore(true);
                                handler.sendEmptyMessage(0);
                                //srlRefreshLayout.setLoadmoreFinished(true);//设置数据全部加载完成，将不能再次触发加载功能
                            }
                        }
                            handler.sendEmptyMessage(1);//更新适配器数据

                    }

                    @Override
                    public void error() {
                        ToastUtil.showToast(mContext, "请求失败！");
                        LogUtil.e("search: 请求失败");
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ToastUtil.showToast(mContext,"没有更多数据了");
                    break;
                case 1:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    if (mWaitingDialog.isShowing()) {
                        mWaitingDialog.dismiss();
                    }
                    break;
                case 4:
                    if (!mWaitingDialog.isShowing()) {
                        mWaitingDialog.show();
                    }
                    break;
                case 404:
                    if (searchInfoBean==null){
                        srlRefreshLayout.finishRefresh();
                        ToastUtil.showToast(mContext,"请求超时，请重试");
                    }
                    break;
            }
        }
    };

    private void initRecycleView() {
        //创建LinearLayoutManager 对象
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        //设置RecyclerView 布局
        mRvVoaList.setLayoutManager(mLayoutManager);
        mRvVoaList.addItemDecoration(new DividerItemDecoration(
                mContext, LinearLayoutManager.VERTICAL
        ));
        ((SimpleItemAnimator) Objects.requireNonNull(mRvVoaList.getItemAnimator())).setSupportsChangeAnimations(false);
        //设置Adapter
        mAdapter = new VoaListAdapter(mContext);
        mRvVoaList.setAdapter(mAdapter);
    }

    private void initRefreshLayout() {
        srlRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                //加载更多
                mPage++;
                getData(mPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //刷新
                mPage = 1;
                getData(mPage);
            }
        });
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
