package com.iyuba.concept2.lil.ui.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.iyuba.concept2.R;
import com.iyuba.concept2.activity.BookMarketActivity;
import com.iyuba.concept2.activity.ColloquialCircleActivity;
import com.iyuba.concept2.activity.LocalNews;
import com.iyuba.concept2.activity.RankActivity;
import com.iyuba.concept2.activity.SetActivity;
import com.iyuba.concept2.activity.SignActivity;
import com.iyuba.concept2.lil.ui.wallet.WalletListActivity;
import com.iyuba.concept2.lil.ui.wordNote.WordNoteActivity;
import com.iyuba.concept2.protocol.SignRequest;
import com.iyuba.concept2.protocol.SignResponse;
import com.iyuba.concept2.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.concept2.util.ConceptApplication;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibDateUtil;
import com.iyuba.core.me.activity.AttentionCenter;
import com.iyuba.core.me.activity.FansCenter;
import com.iyuba.core.me.activity.NoticeCenter;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.imooclib.ui.record.PurchaseRecordActivity;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.ui.BasicFavorActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import personal.iyuba.personalhomelibrary.data.model.GetGroup;
import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity;
import personal.iyuba.personalhomelibrary.ui.groupChat.getGroupInfo.AppGroupInfo;
import personal.iyuba.personalhomelibrary.ui.groupChat.getGroupInfo.GetGroupCallBack;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.home.SimpleListActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import personal.iyuba.personalhomelibrary.ui.my.MySpeechActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 新的我的界面
 * @date: 2023/11/23 13:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewMeFragment extends Fragment {

    private TextView clockInView, exitView;
    private ConstraintLayout userLayout;
    private Button btnLogin;
    private ConstraintLayout topLayout;
    private ImageView userPicView, userMoreView;
    private TextView userNameView, userWalletView, userVipTimeView;
    private LinearLayout attentionLayout, fansLayout, notificationLayout, integralLayout;
    private TextView attentionView, fansView, notificationView, integralView;
    private TextView officialGroupView;
    private RelativeLayout vipCenterLayout,
            wordNoteLayout,videoCollectLayout, videoDubbingLayout, localArticleLayout, favorArticleLayout, listenArticleLayout,
            payMarkLayout, studyReportLayout, walletHistoryLayout,messageCenterLayout, speakCircleLayout,
            officialGroupLayout, integralExchangeLayout, netBookLayout, studyRankLayout,
            settingLayout;

    //官方群暂存信息
    private Pair<Integer, String> groupInfoPair;

    public static NewMeFragment getInstance() {
        NewMeFragment fragment = new NewMeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me_new, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClick();
        checkData();
        //获取官方群信息
        getIyubaGroupInfo();
    }

    @Override
    public void onResume() {
        super.onResume();

        checkData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /******************************初始化数据*************************/
    private void initView(View rootView) {
        clockInView = (TextView) rootView.findViewById(R.id.clockInView);
        exitView = (TextView) rootView.findViewById(R.id.exitView);

        userLayout = rootView.findViewById(R.id.userLayout);
        topLayout = rootView.findViewById(R.id.topLayout);
        btnLogin = rootView.findViewById(R.id.btnLogin);
        userPicView = (ImageView) rootView.findViewById(R.id.userPic);
        userNameView = (TextView) rootView.findViewById(R.id.userName);
        userWalletView = (TextView) rootView.findViewById(R.id.userWallet);
        userVipTimeView = (TextView) rootView.findViewById(R.id.userVipTime);
        userMoreView = (ImageView) rootView.findViewById(R.id.userMore);

        attentionLayout = rootView.findViewById(R.id.attentionLayout);
        attentionView = (TextView) rootView.findViewById(R.id.attentionText);
        fansLayout = (LinearLayout) rootView.findViewById(R.id.fansLayout);
        fansView = (TextView) rootView.findViewById(R.id.fansText);
        notificationLayout = (LinearLayout) rootView.findViewById(R.id.notificationLayout);
        notificationView = (TextView) rootView.findViewById(R.id.notificationText);
        integralLayout = (LinearLayout) rootView.findViewById(R.id.integralLayout);
        integralView = (TextView) rootView.findViewById(R.id.integralText);

        vipCenterLayout = (RelativeLayout) rootView.findViewById(R.id.vipCenter);
        wordNoteLayout = rootView.findViewById(R.id.wordNote);
        walletHistoryLayout = rootView.findViewById(R.id.walletHistory);
        videoCollectLayout = (RelativeLayout) rootView.findViewById(R.id.videoCollect);
        videoDubbingLayout = (RelativeLayout) rootView.findViewById(R.id.videoDubbing);
        localArticleLayout = (RelativeLayout) rootView.findViewById(R.id.localArticle);
        favorArticleLayout = (RelativeLayout) rootView.findViewById(R.id.favorArticle);
        listenArticleLayout = (RelativeLayout) rootView.findViewById(R.id.listenArticle);
        payMarkLayout = (RelativeLayout) rootView.findViewById(R.id.payMark);
        studyReportLayout = (RelativeLayout) rootView.findViewById(R.id.studyReport);
        messageCenterLayout = (RelativeLayout) rootView.findViewById(R.id.messageCenter);
        speakCircleLayout = (RelativeLayout) rootView.findViewById(R.id.speakCircle);
        officialGroupView = rootView.findViewById(R.id.tv_officialGroup);
        officialGroupLayout = (RelativeLayout) rootView.findViewById(R.id.officialGroup);
        integralExchangeLayout = (RelativeLayout) rootView.findViewById(R.id.integralExchange);
        netBookLayout = (RelativeLayout) rootView.findViewById(R.id.netBook);
        studyRankLayout = (RelativeLayout) rootView.findViewById(R.id.studyRank);
        settingLayout = (RelativeLayout) rootView.findViewById(R.id.setting);
    }

    private void initClick() {
        btnLogin.setOnClickListener(onClickListener);
        clockInView.setOnClickListener(onClickListener);
        exitView.setOnClickListener(onClickListener);

        /*userPicView.setOnClickListener(onClickListener);
        userNameView.setOnClickListener(onClickListener);
        userVipTimeView.setOnClickListener(onClickListener);
        userMoreView.setOnClickListener(onClickListener);
        userWalletView.setOnClickListener(onClickListener);*/
        topLayout.setOnClickListener(onClickListener);

        attentionLayout.setOnClickListener(onClickListener);
        fansLayout.setOnClickListener(onClickListener);
        notificationLayout.setOnClickListener(onClickListener);
        integralLayout.setOnClickListener(onClickListener);

        vipCenterLayout.setOnClickListener(onClickListener);
        wordNoteLayout.setOnClickListener(onClickListener);
        walletHistoryLayout.setOnClickListener(onClickListener);
        videoCollectLayout.setOnClickListener(onClickListener);
        videoDubbingLayout.setOnClickListener(onClickListener);
        localArticleLayout.setOnClickListener(onClickListener);
        favorArticleLayout.setOnClickListener(onClickListener);
        listenArticleLayout.setOnClickListener(onClickListener);

        payMarkLayout.setOnClickListener(onClickListener);
        studyReportLayout.setOnClickListener(onClickListener);
        messageCenterLayout.setOnClickListener(onClickListener);
        speakCircleLayout.setOnClickListener(onClickListener);

        officialGroupLayout.setOnClickListener(onClickListener);
        integralExchangeLayout.setOnClickListener(onClickListener);
        netBookLayout.setOnClickListener(onClickListener);
        studyRankLayout.setOnClickListener(onClickListener);

        settingLayout.setOnClickListener(onClickListener);
    }

    /********************************数据显示*************************/
    private void checkData() {
        GitHubImageLoader.Instace(getActivity()).setCirclePic(String.valueOf(UserInfoManager.getInstance().getUserId()), userPicView);

        if (UserInfoManager.getInstance().isLogin()) {
            updateLoginView(true);

            userNameView.setText(UserInfoManager.getInstance().getUserName());
            userWalletView.setText("钱包：" + UserInfoManager.getInstance().getMoney());
            //其他数据
            attentionView.setText(String.valueOf(UserInfoManager.getInstance().getOtherData().getFollowing()));
            fansView.setText(String.valueOf(UserInfoManager.getInstance().getOtherData().getFollower()));
            notificationView.setText("0");
            integralView.setText(String.valueOf(UserInfoManager.getInstance().getJiFen()));

            if (UserInfoManager.getInstance().isVip()) {
                Drawable vipDrawable = getResources().getDrawable(R.drawable.vip);
                vipDrawable.setBounds(0, 0, vipDrawable.getMinimumWidth(), vipDrawable.getMinimumHeight());
                userVipTimeView.setCompoundDrawables(vipDrawable, null, null, null);
                userVipTimeView.setText("到期时间：" + LibDateUtil.toDateStr(UserInfoManager.getInstance().getVipTime(), LibDateUtil.YMD));
            } else {
                userVipTimeView.setCompoundDrawables(null, null, null, null);
                userVipTimeView.setText("普通用户");
            }
        } else {
            updateLoginView(false);
        }
    }

    /********************************其他功能**************************/
    //切换登录样式
    private void updateLoginView(boolean isLogin) {
        if (isLogin) {
            btnLogin.setVisibility(View.GONE);
            userLayout.setVisibility(View.VISIBLE);

            clockInView.setVisibility(View.VISIBLE);
            exitView.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            userLayout.setVisibility(View.INVISIBLE);

            clockInView.setVisibility(View.INVISIBLE);
            exitView.setVisibility(View.INVISIBLE);
        }
    }

    //接口回调
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clockInView:
                    //打卡
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        getSignInfo();
                    }
                    break;
                case R.id.btnLogin:
                    //登录
                    LoginUtil.startToLogin(getActivity());
                    break;
                case R.id.exitView:
                    //退出
                    new AlertDialog.Builder(getActivity())
                            .setMessage("提示")
                            .setMessage(getResources().getString(R.string.logout_alert))
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    UserInfoManager.getInstance().clearUserInfo();
                                    checkData();
                                }
                            }).setNegativeButton("取消", null)
                            .create().show();
                    break;
                /*case R.id.userPic:
                case R.id.userName:
                case R.id.userVipTime:
                case R.id.userMore:*/
                case R.id.topLayout:
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        startActivity(PersonalHomeActivity.buildIntent(getActivity(),
                                UserInfoManager.getInstance().getUserId(),
                                UserInfoManager.getInstance().getUserName(),
                                0));
                    }
                    break;
                case R.id.wordNote:
                    //生词本
                    if (!UserInfoManager.getInstance().isLogin()){
                        LoginUtil.startToLogin(getActivity());
                    }else {
                        WordNoteActivity.start(getActivity());
                    }
                    break;
                case R.id.walletHistory:
                    //钱包
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        WalletListActivity.start(getActivity());
                    }
                    break;
                case R.id.attentionLayout:
                    //关注
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        //更换为共同模块中的内容
                        //AttentionCenter.start(getActivity(),UserInfoManager.getInstance().getUserId());
                        startActivity(SimpleListActivity.buildIntent(getActivity(),1,UserInfoManager.getInstance().getUserId()));
                    }
                    break;
                case R.id.fansLayout:
                    //粉丝
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        //更换为共同模块中的内容
                        //FansCenter.start(getActivity(),UserInfoManager.getInstance().getUserId());
                        startActivity(SimpleListActivity.buildIntent(getActivity(),0,UserInfoManager.getInstance().getUserId()));
                    }
                    break;
                case R.id.notificationLayout:
                    //通知
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        NoticeCenter.start(getActivity(),UserInfoManager.getInstance().getUserId());
                    }
                    break;
                case R.id.integralLayout:
                    //积分
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        String url = "http://api." + Constant.IYBHttpHead() + "/credits/useractionrecordmobileList1.jsp?uid=" + UserInfoManager.getInstance().getUserId();
                        Web.start(getActivity(), url, "积分明细");
                    }
                    break;
                case R.id.vipCenter:
                    //会员中心
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        VipCenterGoldActivity.start(getActivity(), VipCenterGoldActivity.VIP_APP);
                    }
                    break;
                case R.id.videoCollect:
                    //视频收藏
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        //设置视频类型过滤
                        List<String> typeFilter = new ArrayList<>();
                        typeFilter.add(HeadlineType.VOAVIDEO);
                        typeFilter.add(HeadlineType.MEIYU);
                        typeFilter.add(HeadlineType.TED);
                        typeFilter.add(HeadlineType.BBCWORDVIDEO);
                        typeFilter.add(HeadlineType.TOPVIDEOS);
                        typeFilter.add(HeadlineType.SMALLVIDEO);
                        BasicFavor.setTypeFilter(typeFilter);

                        startActivity(BasicFavorActivity.buildIntent(getActivity()));
                    }
                    break;
                case R.id.videoDubbing:
                    //视频配音
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        ArrayList<String> types = new ArrayList<>();
                        types.add(HeadlineType.SMALLVIDEO);
                        startActivity(com.iyuba.module.headlinetalk.ui.mytalk.MyTalkActivity.buildIntent(getActivity(), types));
                    }
                    break;
                case R.id.localArticle:
                    //本地篇目
                    LocalNews.start(getActivity(), 0);
                    break;
                case R.id.favorArticle:
                    //最爱篇目
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        LocalNews.start(getActivity(), 1);
                    }
                    break;
                case R.id.listenArticle:
                    //试听篇目
                    if (!UserInfoManager.getInstance().isLogin()){
                        LoginUtil.startToLogin(getActivity());
                    }else {
                        LocalNews.start(getActivity(), 2);
                    }
                    break;
                case R.id.payMark:
                    //购买记录
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        startActivity(PurchaseRecordActivity.buildIntent(getActivity()));
                    }
                    break;
                case R.id.studyReport:
                    //学习报告
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        String[] types = {
                                SummaryType.LISTEN,
                                SummaryType.EVALUATE
                                //这里暂时屏蔽掉，因为显示的首页数据和详情数据对不起来，首页数据包含部分听读写的内容
//                                SummaryType.TEST
                        };
                        startActivity(SummaryActivity.buildIntent(getActivity(), Constant.APPType, types, 1, "D"));
                    }
                    break;
                case R.id.messageCenter:
                    //消息中心
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        startActivity(new Intent(getContext(), MessageActivity.class));
                    }
                    break;
                case R.id.speakCircle:
                    //口语圈
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        // TODO: 2023/12/27 更换为共同模块中的内容
//                        ColloquialCircleActivity.start(getActivity());
                        startActivity(MySpeechActivity.buildIntent(getActivity()));
                    }
                    break;
                case R.id.officialGroup:
                    //官方群
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        GroupChatManageActivity.start(getActivity(), groupInfoPair.first, groupInfoPair.second, true);
                    }
                    break;
                case R.id.integralExchange:
                    //积分兑换
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        String url = "http://m." + Constant.IYBHttpHead() + "/mall/index.jsp?"
                                + "&uid=" + UserInfoManager.getInstance().getUserId()
                                + "&sign=" + MD5.getMD5ofStr("iyuba" + UserInfoManager.getInstance().getUserId() + "camstory")
                                + "&username=" + UserInfoManager.getInstance().getUserName()
                                + "&platform=android&appid="
                                + Constant.APPID
                                + "&username=" + TextAttr.encode(UserInfoManager.getInstance().getUserName());
                        Web.start(getActivity(), url, getResources().getString(R.string.discover_exgift));
                    }
                    break;
                case R.id.netBook:
                    //全媒体图书
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        BookMarketActivity.start(getActivity());
                    }
                    break;
                case R.id.studyRank:
                    //学习排行
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        RankActivity.start(getActivity());
                    }
                    break;
                case R.id.setting:
                    //应用设置
                    SetActivity.start(getActivity());
                    break;
                default:
//                    ToastUtil.showToast(getActivity(), "未设置点击操作");
                    break;
            }
        }
    };

    //获取官方群数据
    private void getIyubaGroupInfo() {
        String type = "TravelUS";
        AppGroupInfo.Instance().getGroupInfo(new GetGroupCallBack() {
            @Override
            public void getSuccess(GetGroup getGroup) {
                groupInfoPair = new Pair<>(getGroup.groupId, getGroup.gptitle);
                officialGroupView.setText(getGroup.gptitle);
            }

            @Override
            public void getError() {
                groupInfoPair = null;
            }
        }, type);
    }

    //获取打卡信息
    private int signStudyTime = 3 * 60;

    private void getSignInfo() {
        showDialog();

        ExeProtocol.exe(
                new SignRequest(String.valueOf(UserInfoManager.getInstance().getUserId())),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        SignResponse response = (SignResponse) bhr;

                        try {
                            stopDialog();

                            final StudyTimeBeanNew bean = new Gson().fromJson(response.jsonObjectRoot.toString(), StudyTimeBeanNew.class);
                            if ("1".equals(bean.getResult())) {
                                final int time = Integer.parseInt(bean.getTotalTime());

                                if (time < signStudyTime) {
                                    String showMsg = "当前已学习" + time + "秒，\n满" + (signStudyTime / 60) + "分钟可打卡";
                                    toast(showMsg);
                                } else {
                                    //跳转打卡页面
                                    Intent intent1 = new Intent(getActivity(), SignActivity.class);
                                    startActivity(intent1);
                                }
                            } else {
                                toast("打卡加载失败(" + bean.getResult() + ")");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast("打卡加载失败!");
                        }
                    }

                    @Override
                    public void error() {
                        toast("打卡加载异常");
                    }
                });
    }

    //toast信息显示
    private void toast(String msg) {
        Looper.prepare();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    //加载弹窗
    private CustomDialog waittingDialog = null;

    private void showDialog() {
        waittingDialog = WaittingDialog.showDialog(getActivity());
        waittingDialog.setTitle("请稍后");
        waittingDialog.show();
    }

    private void stopDialog() {
        if (waittingDialog != null) {
            waittingDialog.dismiss();
        }
    }
}
