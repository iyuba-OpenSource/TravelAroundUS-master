//package com.iyuba.core.search.activity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.SimpleItemAnimator;
//
//import com.facebook.stetho.common.LogUtil;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.manager.BackgroundManager;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.news.WordUpdateRequest;
//import com.iyuba.core.common.sqlite.mode.Word;
//import com.iyuba.core.common.sqlite.op.WordDBOp;
//import com.iyuba.core.common.sqlite.op.WordOp;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.manager.StudyContentManager;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.lil.user.util.LoginUtil;
//import com.iyuba.core.search.adapter.SearchListAdapter;
//import com.iyuba.core.search.bean.SearchInfoBean;
//import com.iyuba.core.search.request.SearchListRequest;
//import com.iyuba.core.search.request.SearchListResponse;
//import com.iyuba.lib.R;
//
//import java.util.Objects;
//
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.OnPermissionDenied;
//import permissions.dispatcher.RuntimePermissions;
//
///**
// * 搜索 多布局列表
// * 赵皓
// * 2018.12.15
// */
//
//@RuntimePermissions
//public class SearchActivity extends AppCompatActivity {
//
//    private AppCompatButton mBack;
//    private ImageButton mDelInput;
//    private EditText mEdKey;
//    private TextView mTvNullBg;
//    private RecyclerView mRvList;
//    private int mPageNum;
//    private Context mContext;
//    private SearchListAdapter mAdapter;
//    private CustomDialog mWaitingDialog;
//    private Word curWord;
//    private SearchInfoBean searchInfoBean;
//    private String mUid;
//    private WordOp wo;
//    private String mSearchKey;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        mContext = this;
//        mUid = String.valueOf(UserInfoManager.getInstance().getUserId());
//        wo = new WordOp(mContext);
//        initView();
//        closeOtherPlay();
//        initRecycleView();
//        initListener();
//        intentData();
//
//        updateUi(false,"还没有任何搜索哟～");
//    }
//
//    private void initView() {
//        mBack = findViewById(R.id.btn_back);
//        mDelInput = findViewById(R.id.ib_delete_edit);
//        mEdKey = findViewById(R.id.et_input);
//        mRvList = findViewById(R.id.rv_search_list);
//        mTvNullBg = findViewById(R.id.tv_null_bg);
//        mRvList.setVisibility(View.GONE);
//        mWaitingDialog = WaittingDialog.showDialog(mContext);//加载过度
//    }
//
//    private void closeOtherPlay(){
//        if (BackgroundManager.Instance().bindService!=null
//                &&BackgroundManager.Instance().bindService.getPlayer()!=null
//                &&BackgroundManager.Instance().bindService.getPlayer().isPlaying()) {//是否播放声音 currentPage 这里不能判空！！！！！
//            BackgroundManager.Instance().bindService.getPlayer().pause();
//        }
//        if (BackgroundManager.Instance().bindService!=null&&
//                BackgroundManager.Instance().bindService.getVvv()!=null
//                &&BackgroundManager.Instance().bindService.getVvv().isPlaying()){
//            BackgroundManager.Instance().bindService.getVvv().pause();
//        }
//    }
//
//    private void initListener() {
//        mDelInput.setOnClickListener(v -> {
//            mEdKey.setText("");
//            updateUi(false,"还没有任何搜索哟～");
//        });
//        mBack.setOnClickListener(v -> finish());
//        mEdKey.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                if (mEdKey.getText().toString().trim().equals("")) {
//                    ToastUtil.showToast(mContext, "请输入关键词");
//                    return true;
//                } else {
//                    handler.sendEmptyMessage(4);//展示过度视图
//                    initData();
//                    return false;
//                }
//            }
//            return false;
//        });
//
//        mAdapter.onSetClick((wordKey, position) -> saveNewWords(wordKey, position));
//    }
//
//    private void initData() {
//        mPageNum = 3;
//        if (mEdKey.getText().toString().trim().equals("")) {
//            //单词本跳转情况
//            mEdKey.setText(mSearchKey);
//            mSearchKey = mSearchKey.replace(" ", "%20");
//            mEdKey.clearFocus();
//            mEdKey.setSelected(false);
//        } else {
//            mSearchKey = mEdKey.getText().toString().replace(" ", "%20");
//        }
//        ExeProtocol.exe(
//                new SearchListRequest(mSearchKey, 1, mPageNum, 0,Integer.valueOf(mUid)),
//                new ProtocolResponse() {
//
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//                        SearchListResponse response = (SearchListResponse) bhr;
//                        String request = response.searchInfoBean.toString();
//                        //ToastUtil.showToast(mContext, "请求结果：" + request.substring(10));
//                        LogUtil.d(" 请求结果:" + request);
//                        if (response.searchInfoBean.getWord() == null && response.searchInfoBean.getTitleToal() == 0 && response.searchInfoBean.getTextToal() == 0) {
//                            handler.sendEmptyMessage(2);//提示无内容
//                            handler.sendEmptyMessage(3);//隐藏过度视图
//                        } else {
//                            handler.sendEmptyMessage(3);//隐藏过度视图
//                            searchInfoBean = response.searchInfoBean;
//                            handler.sendEmptyMessage(0);//检测单词是否被收藏
//
//                            mAdapter.setData(searchInfoBean,mSearchKey);
//                            handler.sendEmptyMessage(1);
//                        }
//                    }
//
//                    @Override
//                    public void error() {
//                        ToastUtil.showToast(mContext, "请求失败！");
//                        LogUtil.e("search: 请求失败");
//                    }
//                });
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0://检测搜索到的单词是否被收藏（本地检测）
//                    if (searchInfoBean.getWord() != null) {
//                        if (wo.findDataByUser(mUid+"", searchInfoBean.getWord())) {
//                            searchInfoBean.setCollectWord(true);
//                            LogUtil.e("单词：已经被收藏过");
//                        } else {
//                            searchInfoBean.setCollectWord(false);
//                            LogUtil.e("单词：没有被收藏过");
//                        }
//                    }
//                    break;
//                case 1:
//                    updateUi(true,null);
//                    mAdapter.notifyDataSetChanged();
//                    break;
//                case 2:
//                    updateUi(false,"未查询到当前关键词内容");
//                    break;
//                case 3:
//                    if (mWaitingDialog.isShowing()) {
//                        mWaitingDialog.dismiss();
//                    }
//                    break;
//                case 4:
//                    if (!mWaitingDialog.isShowing()) {
//                        mWaitingDialog.show();
//                    }
//                    Message message = new Message();
//                    message.what = 404;
//                    handler.sendMessageDelayed(message, 5000);
//                    break;
//                case 5:
//                    ToastUtil.showToast(mContext, "操作失败，请重试！" + "\n" + "网络异常或服务器忙，请稍后重试");
//                    break;
//                case 6:
//                    if (wo.findDataByUser(mUid+"", searchInfoBean.getWord())) {
//                        LogUtil.e("单词：删除失败");
//                    } else {
//                        LogUtil.e("单词：删除成功");
//                    }
//                    break;
//                case 404:
//                    if (mWaitingDialog.isShowing()) {
//                        mWaitingDialog.dismiss();
//                        ToastUtil.showToast(mContext, "网络请求超时，请重试");
//                    }
//                    break;
//            }
//        }
//    };
//
//    private void initRecycleView() {
//        //创建LinearLayoutManager 对象
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        //设置RecyclerView 布局
//        mRvList.setLayoutManager(mLayoutManager);
//        mRvList.addItemDecoration(new DividerItemDecoration(
//                mContext, LinearLayoutManager.VERTICAL
//        ));
//        ((SimpleItemAnimator) Objects.requireNonNull(mRvList.getItemAnimator())).setSupportsChangeAnimations(false);
//        //设置Adapter
//        mAdapter = new SearchListAdapter(mContext);
//        mRvList.setAdapter(mAdapter);
//    }
//
//    private void intentData() {
//        mSearchKey = getIntent().getStringExtra("key");
//        if (mSearchKey != null && !mSearchKey.equals("")) {
//            initData();
//            mRvList.setVisibility(View.VISIBLE);
//            mTvNullBg.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * 点击收藏判断
//     */
//    private void saveNewWords(String word, int position) {
//        if (!UserInfoManager.getInstance().isLogin()) {
//            LoginUtil.startToLogin(mContext);
//        } else {
//            if (wo.findDataByUser(mUid, word)) {
//                delNetWord(word);//网络删除
//                StringBuffer ids = new StringBuffer("");
//                ids.append(",").append("\'" + word + "\'");
//                String a = ids.toString().substring(1);
//                //如果已经收藏了此单词就删除  删除单词需要加引号！！！！坑！！！
//                boolean del = wo.tryToDeleteItemWord(ids.toString().substring(1), mUid);//本地删除收藏
//                LogUtil.e("单词 尝试设置删除" + del);
//                if (del) {
//                    wo.deleteKeyWord(word, mUid);//真正删除
//                    ToastUtil.showToast(mContext, "单词已从单词本中删除");
//                    searchInfoBean.setCollectWord(false);//设置数据源为未收藏
//                    mAdapter.notifyItemChanged(position);
//                } else {
//                    ToastUtil.showToast(mContext, "取消收藏失败");
//                }
//
//            } else {
//                curWord = new WordDBOp(mContext).findDataByKey(word);
//                if (curWord!=null) {
//                    curWord.userId = mUid;
//                    wo.saveData(curWord);
//                    addNetWord(curWord.key);//网络添加
//                    ToastUtil.showToast(mContext, "成功加入单词本！");
//                    searchInfoBean.setCollectWord(true);//设置数据源为已收藏
//                    mAdapter.notifyItemChanged(position);
//                }
//            }
//        }
//    }
//
//    /**
//     * 同步网络生词库
//     */
//    private void addNetWord(String wordTemp) {
//        com.iyuba.core.common.util.ExeProtocol.exe(new WordUpdateRequest(
//                        String.valueOf(UserInfoManager.getInstance().getUserId()),
//                        WordUpdateRequest.MODE_INSERT, wordTemp),
//                new ProtocolResponse() {
//
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//                        onBackPressed();
//                    }
//
//                    @Override
//                    public void error() {
//                        handler.sendEmptyMessage(5);
//                    }
//                });
//    }
//
//    //网络删除
//    private void delNetWord(String key) {
//        if (key != null) {
//            ClientSession.Instace().asynGetResponse(
//                    new WordUpdateRequest(mUid,
//                            WordUpdateRequest.MODE_DELETE,
//                            key), new IResponseReceiver() {
//                        @Override
//                        public void onResponse(BaseHttpResponse response,
//                                               BaseHttpRequest request, int rspCookie) {
//                            //wo.deleteItemWord(mUid);
//                            handler.sendEmptyMessage(6);
//                        }
//                    });
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //设置临时数据
//        StudyContentManager.getInstance().setTempData(true);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mAdapter.mediaPlayer != null && mAdapter.mediaPlayer.isPlaying()) {
//            mAdapter.mediaPlayer.pause();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mAdapter.mPlayer != null) {
//            mAdapter.mPlayer.stop();
//            mAdapter.mPlayer = null;
//        }
//        if (mAdapter.mediaPlayer != null) {
//            if (mAdapter.mediaPlayer.isPlaying()) {
//                mAdapter.mediaPlayer.pause();
//            }
//            mAdapter.mediaPlayer.stop();
//            mAdapter.mediaPlayer.release();//释放资源
//            mAdapter.mediaPlayer = null;
//        }
//    }
//
//    @NeedsPermission(android.Manifest.permission.RECORD_AUDIO)
//    public void requestEvaluate() {
//
//
//    }
//
//    @OnPermissionDenied(android.Manifest.permission.RECORD_AUDIO)
//    public void requestEvaluateDenied() {
//        ToastUtil.showToast(SearchActivity.this, "申请权限失败,此功能无法正常使用!");
//        return;
//    }
//
//    public void checkStudyPermission() {
//        SearchActivityPermissionsDispatcher.requestEvaluateWithPermissionCheck(SearchActivity.this);
//    }
//
//
//    //更新界面显示
//    private void updateUi(boolean isSuccess,String showMsg){
//        if (isSuccess){
//            mTvNullBg.setVisibility(View.GONE);
//            mRvList.setVisibility(View.VISIBLE);
//        }else {
//            mTvNullBg.setVisibility(View.VISIBLE);
//            mRvList.setVisibility(View.GONE);
//
//            if (TextUtils.isEmpty(showMsg)){
//                mTvNullBg.setText("还没有任何搜索哟～");
//            }else {
//                mTvNullBg.setText(showMsg);
//            }
//        }
//    }
//}
