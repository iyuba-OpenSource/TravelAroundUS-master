package com.iyuba.core.search.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.search.adapter.SentenceListAdapter;
import com.iyuba.core.search.bean.EvaluationBeanNew;
import com.iyuba.core.search.bean.SearchInfoBean;
import com.iyuba.core.search.request.SearchListRequest;
import com.iyuba.core.search.request.SearchListResponse;
import com.iyuba.lib.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * 搜索句子列表类
 * zh
 * 2018.12.19
 */

@RuntimePermissions
public class SentenceListActivity extends AppCompatActivity {

    private Button btnBack;
    private TextView mTvTitle;
    private TextView mTvTest;
    private RecyclerView mRvVoaList;
    private SmartRefreshLayout srlRefreshLayout;
    private Context mContext;
    private String mKey;
    private SearchInfoBean searchInfoBean, moreVoaBean;
    private CustomDialog mWaitingDialog;
    private SentenceListAdapter mAdapter;
    private int mPage = 1;
    private ArrayList<EvaluationBeanNew> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_list);
        ButterKnife.bind(this);

        mContext = this;

        btnBack = findViewById(R.id.btn_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTest = findViewById(R.id.tv_test);
        mRvVoaList = findViewById(R.id.rv_voa_list);
        srlRefreshLayout = findViewById(R.id.srl_refresh_layout);
        getExtra();
        closeOtherPlay();
        mTvTitle.setText(MessageFormat.format("{0} 相关语句", mKey));
        mWaitingDialog = WaittingDialog.showDialog(mContext);//加载过度
        initRecycleView();
        getData(mPage);
        initListener();
        initRefreshLayout();
    }


    //    public static void start(Context context, String key, ArrayList<EvaluatBean> arrayList) {
//        Intent intent = new Intent(context, SentenceListActivity.class);
//        intent.putExtra("key", key);
//        intent.putExtra("arrayList", arrayList);
//        context.startActivity(intent);
//    }
    public static void start(Context context, String key, ArrayList<EvaluationBeanNew> arrayList) {
        Intent intent = new Intent(context, SentenceListActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("arrayList", arrayList);
        context.startActivity(intent);
    }

    private void getExtra() {
        mKey = getIntent().getStringExtra("key");
        if (getIntent().getSerializableExtra("arrayList") != null) {
            arrayList = (ArrayList<EvaluationBeanNew>) getIntent().getSerializableExtra("arrayList");
        }
    }

    private void closeOtherPlay() {
        if (BackgroundManager.Instance().bindService.getPlayer() != null && BackgroundManager.Instance().bindService.getPlayer().isPlaying()) {//是否播放声音 currentPage 这里不能判空！！！！！
            BackgroundManager.Instance().bindService.getPlayer().pause();
        }
        if (BackgroundManager.Instance().bindService.getVvv() != null && BackgroundManager.Instance().bindService.getVvv().isPlaying()) {
            BackgroundManager.Instance().bindService.getVvv().pause();
        }
    }

    private void getData(final int page) {

        //handler.sendEmptyMessage(4);//加载过度
        int mPageNum = 12;
        ExeProtocol.exe(
                new SearchListRequest(mKey, page, mPageNum, 2,
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
                            //传递评测结果
                            for (int i = 0; i < searchInfoBean.getTextData().size(); i++) {
//                                for (EvaluatBean evaluatBean : arrayList) {
//                                    if (searchInfoBean.getTextData().get(i).getVoaId().equals(evaluatBean.getVoaId())
//                                            && searchInfoBean.getTextData().get(i).getParaId().equals(evaluatBean.getParaId())
//                                            && searchInfoBean.getTextData().get(i).getIdIndex().equals(evaluatBean.getIdIndex())) {
//                                        searchInfoBean.getTextData().get(i).setEvaluatBean(evaluatBean);
//                                        searchInfoBean.getTextData().get(i).setTest(true);
//                                        LogUtil.e("替换" + i + " " + searchInfoBean.getTextData().get(i).getVoaId());
//                                    }
//                                }
                                for (EvaluationBeanNew evaluatBean : arrayList) {
                                    if (searchInfoBean.getTextData().get(i).getVoaId().equals(evaluatBean.getVoaId())
                                            && searchInfoBean.getTextData().get(i).getParaId().equals(evaluatBean.getParaId())
                                            && searchInfoBean.getTextData().get(i).getIdIndex().equals(evaluatBean.getIdIndex())) {
                                        searchInfoBean.getTextData().get(i).setEvaluatBean(evaluatBean);
                                        searchInfoBean.getTextData().get(i).setTest(true);
                                        LogUtil.e("替换" + i + " " + searchInfoBean.getTextData().get(i).getVoaId());
                                    }
                                }
                            }
                            mAdapter.setData(searchInfoBean);
                            srlRefreshLayout.finishRefresh(true);
                        } else {
                            moreVoaBean = response.searchInfoBean;
                            if (moreVoaBean.getTextData() != null && moreVoaBean.getTextData().size() > 0) {
                                mAdapter.appendData(moreVoaBean);
                                srlRefreshLayout.finishLoadMore(true);
                            } else {
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
                    ToastUtil.showToast(mContext, "没有更多数据了");
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
        mAdapter = new SentenceListAdapter(mContext);
        mRvVoaList.setAdapter(mAdapter);
//        mRvVoaList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (mAdapter.mediaPlayer!=null&&mAdapter.mediaPlayer.isPlaying()){
//                    mAdapter.mediaPlayer.pause();
//                    mAdapter.mediaPlayer.seekTo(0);
//                }
//            }
//        });
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
        mAdapter.onSetClick(new SentenceListAdapter.OnClickListener() {
            @Override
            public void onSenPlayClick(String path, int position) {
                try {
                    mAdapter.mediaPlayer.setDataSource(searchInfoBean.getTextData().get(position).getSoundText());
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e("异常" + e);
                }
            }
        });
        mTvTest.setVisibility(View.GONE);
        mTvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.reset();
                String playUrl = "http://staticvip." + Constant.IYBHttpHead() + "/sounds/voa/sentence/201812/7440/7440_1_2.wav";
                //String playUrl = "http://static."+Constant.IYBHttpHead+"/sounds/voa/sentence/201812/7440/7440_3_1.wav";

                try {
                    mediaPlayer.setDataSource(playUrl);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    LogUtil.e("异常！！！！" + e);
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter.mediaPlayer != null) {
            if (mAdapter.mediaPlayer.isPlaying()) {
                mAdapter.mediaPlayer.pause();
            }
            mAdapter.mediaPlayer.stop();
            mAdapter.mediaPlayer.release();//释放资源
            mAdapter.mediaPlayer = null;
        }
    }

    @NeedsPermission(android.Manifest.permission.RECORD_AUDIO)
    public void requestEvaluate() {

    }

    @OnPermissionDenied(android.Manifest.permission.RECORD_AUDIO)
    public void requestEvaluateDenied() {
        ToastUtil.showToast(SentenceListActivity.this, "申请权限失败,此功能无法正常使用!");
        return;

    }

    public void checkStudyPermission() {
        SentenceListActivityPermissionsDispatcher.requestEvaluateWithPermissionCheck(SentenceListActivity.this);
    }
}
