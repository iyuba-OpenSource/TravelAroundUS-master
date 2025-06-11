///*
// * 文件名
// * 包含类名列表
// * 版本信息，版本号
// * 创建日期
// * 版权声明
// */
//package com.iyuba.concept2.fragment;
//
//import static com.youdao.sdk.common.YoudaoSDK.getApplicationContext;
//
//import android.app.AlertDialog;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.facebook.stetho.common.LogUtil;
//import com.google.gson.Gson;
//import com.iyuba.concept2.R;
//import com.iyuba.concept2.activity.AboutActivity;
//import com.iyuba.concept2.activity.BookMarketActivity;
//import com.iyuba.concept2.activity.ColloquialCircleActivity;
//import com.iyuba.concept2.activity.FeedbackActivity;
//import com.iyuba.concept2.activity.OrderDetailActivity;
//import com.iyuba.concept2.activity.RankActivity;
//import com.iyuba.concept2.activity.SetActivity;
//import com.iyuba.concept2.activity.SignActivity;
//import com.iyuba.concept2.lil.manager.AbilityControlManager;
//import com.iyuba.concept2.protocol.SignRequest;
//import com.iyuba.concept2.protocol.SignResponse;
//import com.iyuba.concept2.sqlite.mode.StudyTimeBeanNew;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.GradeRequest;
//import com.iyuba.core.common.protocol.base.GradeResponse;
//import com.iyuba.core.common.protocol.message.RequestBasicUserInfo;
//import com.iyuba.core.common.protocol.message.RequestNewInfo;
//import com.iyuba.core.common.protocol.message.ResponseBasicUserInfo;
//import com.iyuba.core.common.protocol.message.ResponseNewInfo;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.sqlite.db.Emotion;
//import com.iyuba.core.common.sqlite.mode.UserInfo;
//import com.iyuba.core.common.thread.GitHubImageLoader;
//import com.iyuba.core.common.util.AdTimeUtils;
//import com.iyuba.core.common.util.BrandUtil;
//import com.iyuba.core.common.util.ChangeVideoEvnet;
//import com.iyuba.core.common.util.CheckGrade;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.util.Expression;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.MD5;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.util.LoginUtil;
//import com.iyuba.core.me.activity.AttentionCenter;
//import com.iyuba.core.me.activity.FansCenter;
//import com.iyuba.core.me.activity.NoticeCenter;
//import com.iyuba.core.me.activity.WriteState;
//import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
//import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
//import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
//import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
//import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity;
//import com.iyuba.module.favor.data.model.BasicFavorPart;
//import com.iyuba.module.favor.event.FavorItemEvent;
//import com.iyuba.module.favor.ui.BasicFavorActivity;
//import com.iyuba.module.movies.IMovies;
//import com.iyuba.module.movies.IMoviesManager;
//import com.iyuba.module.movies.ui.series.SeriesActivity;
//import com.iyuba.module.user.IyuUserManager;
//import com.umeng.analytics.MobclickAgent;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.text.DecimalFormat;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Locale;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import personal.iyuba.personalhomelibrary.data.model.GetGroup;
//import personal.iyuba.personalhomelibrary.event.UserPhotoChangeEvent;
//import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity;
//import personal.iyuba.personalhomelibrary.ui.groupChat.getGroupInfo.AppGroupInfo;
//import personal.iyuba.personalhomelibrary.ui.groupChat.getGroupInfo.GetGroupCallBack;
//import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
//import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
//import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
//import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;
//import personal.iyuba.personalhomelibrary.utils.ToastFactory;
///**
// * 类名
// *
// * @author 作者 <br/>
// * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。  现在使用中
// */
//public class MeFragment extends Fragment {
//    private View noLogin, login; // 登录提示面板
//    private Button loginBtn, logout, btndk;
//    private Context mContext;
//    private ImageView photo;
//    private TextView name, state, fans, attention, notification, listem_time,
//            integral, position, lv ,tvGroupTitle;
//    private View person;
//    private View stateView, messageView, vipView, messageGroupView;
//    private View local, favor, read, back;
//    private View attentionView, fansView, notificationView, integralView,
//            discover_iyubaset, media_book, me_order;
//    private UserInfo userInfo;
//    private View root;
//
//    private RelativeLayout meRank,mGift,circleLayout,meDiscover,learningReportLayout;
//    private ImageView view_send;
//    ImageView imageView;
//    private TextView mTvWallet;
//    private String moneyStr;
//    private RelativeLayout mRlShare, mRlAbout, mRlFeedback;
//
//    private int mGroupId;
//    private String mGroupTitle;
//
//    //视频模块
//    private RelativeLayout videoCollectLayout,videoDubbingLayout;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        root = inflater.inflate(R.layout.me_main, container, false);
//        noLogin = root.findViewById(R.id.noLogin);
//        login = root.findViewById(R.id.login);
//        btndk = root.findViewById(R.id.dk);
//        logout = root.findViewById(R.id.logout);
//        back = root.findViewById(R.id.button_back);
//        back.setVisibility(View.GONE);
//        AppGroupInfo.Instance().getGroupInfo(callBack,"TravelUS");
//
//        init();
//        initLocal();
//        return root;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        //根据要求，隐藏发现功能
//        meDiscover.setVisibility(View.GONE);
//
//        ImageView discoverLine = root.findViewById(R.id.me_discover_line);
//        discoverLine.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//        mContext = getActivity();
//
//        //获取qq客服支持
//        BrandUtil.requestQQSupport();
//        String uid = AccountManager.Instance(getActivity()).userId;
//        BrandUtil.requestQQGroup(TextUtils.isEmpty(uid)?"0":uid);
//    }
//
//    @Override
//    public void setArguments(Bundle args) {
//        super.setArguments(args);
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (getUserVisibleHint() && isVisibleToUser) {
//            checkLogin();
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(mContext);
//        viewChange();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(mContext);
//    }
//
//    private void viewChange() {
//        if (checkLogin()) {
//            userInfo = AccountManager.Instance(mContext).userInfo;
//            ClientSession.Instace()
//                    .asynGetResponse(
//                            new RequestNewInfo(
//                                    AccountManager.Instance(mContext).userId),
//                            new IResponseReceiver() {
//                                @Override
//                                public void onResponse(
//                                        BaseHttpResponse response,
//                                        BaseHttpRequest request, int rspCookie) {
//                                    ResponseNewInfo rs = (ResponseNewInfo) response;
//                                    if (rs.letter > 0) {
//                                        handler.sendEmptyMessage(2);
//                                    } else {
//                                        handler.sendEmptyMessage(5);
//                                    }
//                                }
//                            });
//            loadData();
//        }
//    }
//
//    private boolean checkLogin() {
//        userInfo = AccountManager.getInstance().userInfo;
//
//        boolean isLogin=AccountManager.Instance(mContext).isLogin();
//        if (!isLogin) {
//            try{
//                noLogin.setVisibility(View.VISIBLE);
//                login.setVisibility(View.GONE);
//                btndk.setVisibility(View.GONE);
//            }catch (Exception e){
//                Log.e("TAG", "checkLogin: "+e.getMessage());
//            }
//
//            loginBtn = root.findViewById(R.id.button_to_login);
//            loginBtn.setOnClickListener(v -> {
////                startActivity(new Intent(mContext, Login.class));
//                LoginUtil.startToLogin(mContext);
//            });
//            logout.setVisibility(View.GONE);
//            return false;
//        } else {
//            try {
//                noLogin.setVisibility(View.GONE);
//                login.setVisibility(View.VISIBLE);
//                btndk.setVisibility(View.VISIBLE);
//                logout.setVisibility(View.VISIBLE);
//            }catch (Exception e){
//                Log.e("TAG", "checkLogin: "+e.getMessage());
//            }
//
//            if (AccountManager.Instance(mContext).islinshi) {
//                logout.setText(R.string.login_login);
//
//            } else {
//                logout.setText(R.string.exitUser);
//            }
//            logout.setOnClickListener(arg0 -> {
//                if (AccountManager.Instance(mContext).islinshi) {
////                    Intent intent = new Intent();
////                    intent.setClass(mContext, Login.class);
////                    startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                } else {
//                    AlertDialog dialog = new AlertDialog.Builder(mContext)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setTitle(
//                                    getResources().getString(
//                                            R.string.alert_title))
//                            .setMessage(
//                                    getResources().getString(
//                                            R.string.logout_alert))
//                            .setPositiveButton(
//                                    getResources().getString(
//                                            R.string.alert_btn_ok),
//                                    (dialog1, whichButton) -> handler.sendEmptyMessage(4))
//                            .setNeutralButton(
//                                    getResources().getString(
//                                            R.string.alert_btn_cancel),
//                                    (dialog12, which) -> {
//                                    }).create();
//                    dialog.show();
//                }
//            });
//            btndk.setOnClickListener(ocl);
//            return true;
//        }
//    }
//
//    private void loadData() {
//        final String id = AccountManager.Instance(mContext).userId;
////        init();
//        ExeProtocol.exe(new RequestBasicUserInfo(id, id),
//                new ProtocolResponse() {
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//                        ResponseBasicUserInfo response = (ResponseBasicUserInfo) bhr;
//                        userInfo = response.userInfo;
//                        AccountManager.Instance(mContext).userInfo = userInfo;
//                        Looper.prepare();
//                        ExeProtocol.exe(new GradeRequest(id),
//                                new ProtocolResponse() {
//                                    @Override
//                                    public void finish(BaseHttpResponse bhr) {
//                                        GradeResponse response = (GradeResponse) bhr;
//                                        userInfo.studytime = Integer
//                                                .parseInt(response.totalTime);
//                                        userInfo.position = response.positionByTime;
//                                        handler.sendEmptyMessage(3);
//                                    }
//
//                                    @Override
//                                    public void error() {
//                                        handler.sendEmptyMessage(0);
//                                    }
//                                });
//                        Looper.loop();
//                    }
//
//                    @Override
//                    public void error() {
//                    }
//                });
//    }
//
//    private void init() {
//        videoCollectLayout = root.findViewById(R.id.video_collect_layout);
//        videoDubbingLayout = root.findViewById(R.id.video_dubbing_layout);
//        if (AbilityControlManager.getInstance().isLimitVideo()){
//            videoCollectLayout.setVisibility(View.GONE);
//            videoDubbingLayout.setVisibility(View.GONE);
//        }
//        person = root.findViewById(R.id.personalhome);
//        photo = root.findViewById(R.id.me_pic);
//        name = root.findViewById(R.id.me_name);
//        state = root.findViewById(R.id.me_state);
//        attention = root.findViewById(R.id.me_attention);
//        listem_time = root.findViewById(R.id.me_listem_time);
//        position = root.findViewById(R.id.me_position);
//        lv = root.findViewById(R.id.lv);
//        fans = root.findViewById(R.id.me_fans);
//        notification = root.findViewById(R.id.me_notification);
//
//        integral = root.findViewById(R.id.Integral_notification);
//        stateView = root.findViewById(R.id.me_state_change);
//        messageView = root.findViewById(R.id.me_message);
//        messageGroupView = root.findViewById(R.id.me_message_group);
//        vipView = root.findViewById(R.id.me_vip);
//        attentionView = root.findViewById(R.id.attention_area);
//        fansView = root.findViewById(R.id.fans_area);
//
//        notificationView = root.findViewById(R.id.notification_area);
//        integralView = root.findViewById(R.id.Integral);
//        tvGroupTitle =root.findViewById(R.id.tv_group_title);
//        setViewClick();
//        if (userInfo != null) {
//            setTextViewContent();
//        }
//    }
//
//    private void initLocal() {
////        init();
//        local = root.findViewById(R.id.me_local);
//        favor = root.findViewById(R.id.me_love);
//        read = root.findViewById(R.id.me_read);
//        meRank = root.findViewById(R.id.me_rank);
//        imageView = root.findViewById(R.id.newletter);
//
//        view_send = root.findViewById(R.id.view_send);
//        mTvWallet = root.findViewById(R.id.tv_wallet);
//        circleLayout = root.findViewById(R.id.me_colloquial_circle);
//        meDiscover = root.findViewById(R.id.me_discover);
//        learningReportLayout = root.findViewById(R.id.me_learning_report);
//
//        mGift =root.findViewById(R.id.discover_exchange_gift);
//        mGift.setOnClickListener(ocl);
//
//        discover_iyubaset = root.findViewById(R.id.discover_iyubaset);
//
//        media_book = root.findViewById(R.id.me_media_book);
//        me_order = root.findViewById(R.id.me_order_detail);
//        local.setOnClickListener(ocl);
//        favor.setOnClickListener(ocl);
//        read.setOnClickListener(ocl);
//        discover_iyubaset.setOnClickListener(ocl);
//        media_book.setOnClickListener(ocl);
//        me_order.setOnClickListener(ocl);
//        meRank.setOnClickListener(ocl);
//
//        mTvWallet.setOnClickListener(ocl);
//        circleLayout.setOnClickListener(ocl);
//        meDiscover.setOnClickListener(ocl);
//        learningReportLayout.setOnClickListener(ocl);
//
////        AppGroupInfo.Instance().getGroupInfo(callBack);
//
//        mRlShare = root.findViewById(R.id.rl_share);
//        mRlShare.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showShare();
//            }
//        });
//
//        mRlAbout = root.findViewById(R.id.rl_about);
//        mRlAbout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), AboutActivity.class));
//            }
//        });
//        mRlFeedback = root.findViewById(R.id.rl_feedback);
//        mRlFeedback.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), FeedbackActivity.class));
//            }
//        });
//
//    }
//
//    private GetGroupCallBack callBack =new GetGroupCallBack() {
//        @Override
//        public void getSuccess(GetGroup group) {
//            mGroupId=group.groupId;
//            mGroupTitle=group.gptitle;
//            tvGroupTitle.setText(group.gptitle);
//        }
//
//        @Override
//        public void getError() {
//            ToastFactory.showShort(mContext,"获取群信息失败");
//        }
//    };
//
//    private void setViewClick() {
//        person.setOnClickListener(ocl);
//        vipView.setOnClickListener(ocl);
//        stateView.setOnClickListener(ocl);
//        messageView.setOnClickListener(ocl);
//        messageGroupView.setOnClickListener(ocl);
//        attentionView.setOnClickListener(ocl);
//        fansView.setOnClickListener(ocl);
//        notificationView.setOnClickListener(ocl);
//        integralView.setOnClickListener(ocl);
//
//        //视频
//        videoCollectLayout.setOnClickListener(ocl);
//        videoDubbingLayout.setOnClickListener(ocl);
//    }
//
//    private void setTextViewContent() {
//        GitHubImageLoader.Instace(mContext).setCirclePic(
//                AccountManager.Instance(mContext).userId, photo);
//        String name1=AccountManager.Instance(mContext).userInfo.username;
//        // name.setText(AccountManager.Instance(mContext).userName);
//        LogUtils.e("用户名："+name1+"!!");
//        name.setText(AccountManager.Instance(mContext).userInfo.username);
//        if (ConfigManager.Instance().loadInt("isvip") >= 1) {
//            Drawable img = mContext.getResources().getDrawable(R.drawable.vip);
//            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//            name.setCompoundDrawables(null, null, img, null);
//        } else {
//            Drawable img = mContext.getResources().getDrawable(R.drawable.no_vip);
//            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//            name.setCompoundDrawables(null, null, img, null);
//        }
//        if (userInfo.text == null) {
//            state.setText(R.string.social_default_state);
//        } else {
//            String zhengze = "image[0-9]{2}|image[0-9]";
//            Emotion emotion = new Emotion();
//            userInfo.text = emotion.replace(userInfo.text);
//            SpannableString spannableString = Expression.getExpressionString(mContext, userInfo.text, zhengze);
//            state.setText(spannableString);
//        }
//        attention.setText(userInfo.following);
//        fans.setText(userInfo.follower);
//        listem_time.setText(exeStudyTime());
//        position.setText(exePosition());
//        lv.setText(mContext.getString(R.string.me_lv) + CheckGrade.CheckLevelName(userInfo.icoins));
//        mTvWallet.setText(userMoney());
//        notification.setText(userInfo.notification);
//        integral.setText(userInfo.icoins);
//
//
//        /*******************新的用户信息********************/
////        GitHubImageLoader.Instace(mContext).setCirclePic(String.valueOf(UserInfoManager.getInstance().getUserId()), photo);
////        name.setText(UserInfoManager.getInstance().getUserName());
//    }
//
//    public void setShortTimeName(String lName){
//         if (TextUtils.isEmpty(name.getText().toString()) && lName!=null){
//             name.setText(lName);
//         }
//    }
//
//    private String exeStudyTime() {
//        StringBuffer sb = new StringBuffer(
//                mContext.getString(R.string.me_study_time));
//        int time = userInfo.studytime;
//        int minus = time % 60;
//        int minute = time / 60 % 60;
//        int hour = time / 60 / 60;
//        if (hour > 0) {
//            sb.append(hour).append(mContext.getString(R.string.me_hour));
//        } else if (minute > 0) {
//            sb.append(minute).append(mContext.getString(R.string.me_minute));
//        } else {
//            sb.append(minus).append(mContext.getString(R.string.me_minus));
//        }
//        return sb.toString();
//    }
//
//    private String exePosition() {
//        StringBuffer sb = new StringBuffer(
//                mContext.getString(R.string.me_study_position));
//        int position = Integer.parseInt(userInfo.position);
//        if (position < 10000) {
//            sb.append(position);
//        } else {
//            sb.append(position / 10000 * 10000).append("+");
//        }
//        return sb.toString();
//    }
//
//    private String userMoney() {
//        String money;
//        if (AccountManager.Instance(mContext).userId != null &&
//                !"".equals(AccountManager.Instance(mContext).userId) &&
//                !AccountManager.Instance(mContext).userId.equals("0")
//                && Integer.parseInt(AccountManager.Instance(mContext).userId) < 50000000) {
//            mTvWallet.setVisibility(View.VISIBLE);
//            float moneyThisTime = 0;
//            if (AccountManager.Instance(getActivity()).getMONEY() != null && !AccountManager.Instance(getActivity()).getMONEY().equals("")) {
//                LogUtil.e("断网崩溃" + AccountManager.Instance(getActivity()).getMONEY());
//                moneyThisTime = Float.parseFloat(AccountManager.Instance(getActivity()).getMONEY());
//            }
//            moneyStr = floatToString(moneyThisTime);
//            money = "钱包:" + floatToString(moneyThisTime) + "元";
//        } else {
//            //临时账户 没有钱包
//            money = "";
//            mTvWallet.setVisibility(View.GONE);
//        }
//        return money;
//    }
//
//    private OnClickListener ocl = new OnClickListener() {
//
//        @Override
//        public void onClick(View arg0) {
//            Intent intent;
//            int id = arg0.getId();
//            String packageName = mContext.getPackageName();
//            if (id == R.id.personalhome) {
//
//                int userid = Integer.valueOf(AccountManager.getInstance().userId);
//                startActivity(PersonalHomeActivity.buildIntent(mContext,
//                        userid,AccountManager.getInstance().userName,
//                        0));
//
//            } else if (id == R.id.dk) {
//                if (AccountManager.Instance(mContext).userId != null &&
//                        !"".equals(AccountManager.Instance(mContext).userId) &&
//                        !AccountManager.Instance(mContext).userId.equals("0")
//                        && Integer.parseInt(AccountManager.Instance(mContext).userId) < 50000000) {
//                    //跳转打卡页面
//                    initData();
//
//                } else {
//                    Toast.makeText(mContext, "临时用户无法打卡。", Toast.LENGTH_SHORT).show();
//                }
//            } else if (id == R.id.me_state_change) {
//                if (AccountManager.Instance(mContext).islinshi) {
//                    showNormalDialog("临时用户无法修改个性签名。");
//                } else {
//                    intent = new Intent(mContext, WriteState.class);
//                    startActivity(intent);
//                }
//            } else if (id == R.id.me_vip) {//VIP
//                if (!AccountManager.getInstance().checkUserLogin()){
//                    ToastUtil.showToast(mContext,"请先登录");
////                    startActivity(new Intent(mContext, Login.class));
//                    LoginUtil.startToLogin(mContext);
//                    return;
//                }
//
//                intent = new Intent(mContext, VipCenterGoldActivity.class);
//                startActivity(intent);
//            } else if (id == R.id.me_message) {//消息中心
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    startActivity(new Intent(getContext(), MessageActivity.class));//需要登录
//                } else {
//                    ToastUtil.showToast(mContext,"请先登录");
////                    startActivity(new Intent(mContext, Login.class));
//                    LoginUtil.startToLogin(mContext);
//                }
//            } else if (id == R.id.me_message_group) {//爱语吧官方群
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//                    //GroupChatManageActivity.start(mContext,10108,"爱语吧官方群",true);//10016 VOA慢速英语 10016
//                    GroupChatManageActivity.start(mContext,mGroupId,mGroupTitle,true);
//                }
//            }else if (id == R.id.me_local) {
//                intent = new Intent();
//                intent.setComponent(new ComponentName(packageName,
//                        "com.iyuba.concept2.activity.LocalNews"));
//                intent.putExtra("localType", 0);
//                startActivity(intent);
//            } else if (id == R.id.me_love) {
//                intent = new Intent();
//                intent.setComponent(new ComponentName(packageName,
//                        "com.iyuba.concept2.activity.LocalNews"));
//                intent.putExtra("localType", 1);
//                startActivity(intent);
//            } else if (id == R.id.me_read) {
//                intent = new Intent();
//                intent.setComponent(new ComponentName(packageName,
//                        "com.iyuba.concept2.activity.LocalNews"));
//                intent.putExtra("localType", 2);
//                startActivity(intent);
//            } else if (id == R.id.attention_area) {
//                //替换关注跳转
//                int uid =Integer.valueOf(AccountManager.getInstance().userId);
//                AccountManager manager = AccountManager.getInstance();
//                //个人中心模块部分功能隐藏
////                if (manager.checkUserLogin() && !manager.islinshi) {
////                    startActivity(SimpleListActivity.buildIntent(mContext, FOLLOWING, uid));
////                } else {
////                    ToastFactory.showShort(getContext(), getString(R.string.please_login));
////                }
//                intent = new Intent(mContext, AttentionCenter.class);
//                intent.putExtra("userId",
//                        AccountManager.Instance(mContext).userId);
//                startActivity(intent);
//            } else if (id == R.id.fans_area) {
//                //替换粉丝跳转
//                int uid =Integer.valueOf(AccountManager.getInstance().userId);
//                AccountManager manager = AccountManager.getInstance();
//                //个人中心模块部分功能隐藏
////                if (manager.checkUserLogin() && !manager.islinshi) {
////                    startActivity(SimpleListActivity.buildIntent(mContext, FOLLOWER, uid));
////                } else {
////                    ToastFactory.showShort(getContext(), getString(R.string.please_login));
////                }
//                intent = new Intent(mContext, FansCenter.class);
//                intent.putExtra("userId",
//                        AccountManager.Instance(mContext).userId);
//                startActivity(intent);
//            } else if (id == R.id.notification_area) {
//                intent = new Intent(mContext, NoticeCenter.class);
//                intent.putExtra("userId",
//                        AccountManager.Instance(mContext).userId);
//                startActivity(intent);
//            } else if (id == R.id.Integral) {
//                if (!AccountManager.getInstance().checkUserLogin()){
//                    ToastUtil.showToast(mContext,"请先登录");
////                    startActivity(new Intent(mContext, Login.class));
//                    LoginUtil.startToLogin(mContext);
//                    return;
//                }
//
//                intent = new Intent(mContext, Web.class);
//                intent.putExtra("title", "积分明细");
//                intent.putExtra("url",
//                        "http://api." + Constant.IYBHttpHead() + "/credits/useractionrecordmobileList1.jsp?uid="
//                                + AccountManager.Instance(mContext).userId);
//                startActivity(intent);
//            } else if (id == R.id.discover_iyubaset) {
//                intent = new Intent();
//                intent.setClass(mContext, SetActivity.class);
//                // intent.setClass(mContext, IntelLearningActivity.class);
//                startActivity(intent);
//            } else if (id == R.id.me_media_book) {
//                startActivity(new Intent(mContext, BookMarketActivity.class));
//            } else if (id == R.id.me_order_detail) {
//                startActivity(new Intent(mContext, OrderDetailActivity.class));
//            } else if (id == R.id.video_collect_layout) {
//                // 收藏页面
//                if (AccountManager.Instance(mContext).checkUserLogin()) {
//
//                    int uid;
//                    try {
//                        uid = Integer.parseInt(AccountManager.Instance(mContext).userId);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        uid = 0;
//                    }
//
//                    Intent intents = BasicFavorActivity.buildIntent(mContext);
//                    startActivity(intents);
//                } else {
////                    startActivity(new Intent(mContext, Login.class));
//                    LoginUtil.startToLogin(mContext);
//                }
//            } else if (id == R.id.me_rank) {
//                startActivity(new Intent(mContext, RankActivity.class));
//            }else if (id == R.id.tv_wallet) {
//                new androidx.appcompat.app.AlertDialog.Builder(getActivity())
//                        .setTitle(R.string.alert)
//                        .setMessage("当前钱包金额:" + moneyStr + "元,满10元可在[爱语吧]微信公众号提现(关注绑定爱语吧账号),每天坚持打卡分享,获得更多红包吧!")
//                        .setPositiveButton(R.string.alert_btn_ok, (dialog, whichButton) -> dialog.dismiss())
//                        .create()
//                        .show();
//            }if (id == R.id.discover_exchange_gift) {//积分商城
//                if (AccountManager.Instance(mContext).checkUserLogin() && !AccountManager.Instance(mContext).islinshi) {
//                    intent = new Intent();
//                    intent.setClass(mContext, Web.class);
//                    intent.putExtra("url", "http://m." + Constant.IYBHttpHead() + "/mall/index.jsp?"
//                            + "&uid=" + AccountManager.Instance(mContext).userId
//                            + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManager.Instance(mContext).userId + "camstory")
//                            + "&username=" + AccountManager.Instance(mContext).userName
//                            + "&platform=android&appid="
//                            + Constant.APPID
//                            + "&username=" + TextAttr.encode(AccountManager.Instance(mContext).userName)
//                    );
//                    intent.putExtra("title",
//                            mContext.getString(R.string.discover_exgift));
//                    startActivity(intent);
//
//                } else {
////                    intent = new Intent();
////                    intent.setClass(mContext, Login.class);
////                    startActivity(intent);
//                    LoginUtil.startToLogin(mContext);
//                }
//            }else if(id == R.id.me_discover){
//                //发现功能根据要求关闭
////                startActivity(new Intent(requireActivity(), DiscoverActivity.class));
//            }else if(id == R.id.me_colloquial_circle){
//                intent = new Intent(mContext, ColloquialCircleActivity.class);
//                startActivity(intent);
//            }else if(id == R.id.me_learning_report){
//                if (!AccountManager.getInstance().checkUserLogin()){
//                    ToastUtil.showToast(mContext,"请先登录");
////                    startActivity(new Intent(mContext, Login.class));
//                    LoginUtil.startToLogin(mContext);
//                    return;
//                }
//
//                String[] types = {
//                        SummaryType.LISTEN,
//                        SummaryType.EVALUATE,
//                        SummaryType.TEST
//                };
//                startActivity(
//                        SummaryActivity.buildIntent(
//                                getActivity(),
//                                Constant.APPType,
//                                types,
//                                1,
//                                "D"
//                        )
//                ); //10 PersonalType.NEWS
//            }
//        }
//    };
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(final Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    CustomToast.showToast(mContext, R.string.check_network);
//                    break;
//                case 1:
//                    CustomToast.showToast(mContext, R.string.action_fail);
//                    break;
//                case 2:
//                    try {
//                        if(imageView != null){
//                            imageView.setVisibility(View.VISIBLE);
//                        }
//                    }catch (Exception e){
//                        Log.e("TAG", "handleMessage: "+e.getMessage());
//                    }
//
//
//                    break;
//                case 3:
//                    setTextViewContent();
//                    break;
//                case 4:
//                    AccountManager.Instance(mContext).loginOut();
//                    CustomToast.showToast(mContext, R.string.exitUserSuccess);
//                    SettingConfig.Instance().setHighSpeed(false);
//
//                    //手动写一下
//                    ConfigManager.Instance().putString("userId", "0");
//                    ConfigManager.Instance().putString("userName", "");
//                    ConfigManager.Instance().putInt("isvip", 0);
//                    //推出账号
//                    IyuUserManager.getInstance().logout();
//                    //退出登录刷新状态
//                    EventBus.getDefault().post(new ChangeVideoEvnet(true));//event发布
//                    onResume();
//                    break;
//            }
//        }
//    };
//
//
//    private void showNormalDialog(String content) {
//        /* @setIcon 设置对话框图标
//         * @setTitle 设置对话框标题
//         * @setMessage 设置对话框消息提示
//         * setXXX方法返回Dialog对象，因此可以链式设置属性
//         */
//        final AlertDialog.Builder normalDialog =
//                new AlertDialog.Builder(mContext);
//        normalDialog.setIcon(com.iyuba.lib.R.drawable.iyubi_icon);
//        normalDialog.setTitle("提示");
//        normalDialog.setMessage(content);
//        normalDialog.setPositiveButton("登录",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //...To-do
////                        Intent intent = new Intent();
////                        intent.setClass(mContext, Login.class);
////                        startActivity(intent);
//                        LoginUtil.startToLogin(mContext);
//                    }
//                });
//        normalDialog.setNegativeButton("确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //...To-do
//
//                    }
//                });
//        // 显示
//        normalDialog.show();
//    }
//
//
//    CustomDialog mWaittingDialog;
//    private int signStudyTime = 3 * 60;
//    private String loadFiledHint = "打卡加载失败";
//
//    //打卡
//    private void initData() {
//
//        mWaittingDialog = WaittingDialog.showDialog(getActivity());
//        mWaittingDialog.setTitle("请稍后");
//        mWaittingDialog.show();
//
//        String uid = AccountManager.Instance(mContext).userId;
//
//
//        final String url = String.format(Locale.CHINA,
//                "http://daxue." + Constant.IYBHttpHead() + "/ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());
//        Log.d("dddd", url);
//
//
//        ExeProtocol.exe(
//                new SignRequest(AccountManager.Instance(mContext).userId),
//                new ProtocolResponse() {
//
//                    @Override
//                    public void finish(BaseHttpResponse bhr) {
//
//
//                        SignResponse response = (SignResponse) bhr;
//
//                        try {
//                            if (null != mWaittingDialog) {
//                                if (mWaittingDialog.isShowing()) {
//                                    mWaittingDialog.dismiss();
//                                }
//                            }
//
//                            final StudyTimeBeanNew bean = new Gson().fromJson(response.jsonObjectRoot.toString(), StudyTimeBeanNew.class);
//                            Log.d("dddd", response.jsonObjectRoot.toString());
//                            if ("1".equals(bean.getResult())) {
//                                final int time = Integer.parseInt(bean.getTotalTime());
//
//                                if (time < signStudyTime) {
//
//                                    toast(String.format(Locale.CHINA, "当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60));
//
//                                } else {
//
//                                    //跳转打卡页面
//                                    Intent intent1 = new Intent(getActivity(), SignActivity.class);
//                                    startActivity(intent1);
//                                }
//                            } else {
//                                toast(loadFiledHint + bean.getResult());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            toast(loadFiledHint + "！！");
//                        }
//
//                    }
//
//                    @Override
//                    public void error() {
//
//                    }
//                });
//
//    }
//
//    private void toast(String msg) {
//
//        Looper.prepare();
//        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
//        Looper.loop();
//
//    }
//
//    private long getDays() {
//        //东八区;
//        Calendar cal = Calendar.getInstance(Locale.CHINA);
//        cal.set(1970, 0, 1, 0, 0, 0);
//        Calendar now = Calendar.getInstance(Locale.CHINA);
//        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
//        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
//        return xcts;
//    }
//
//
//    private String floatToString(float fNumber) {
//
//        fNumber = (float) (fNumber * 0.01);
//
//        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
//        String str = myformat.format(fNumber);
//        return str;
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(FavorItemEvent fEvent) {
//        //收藏页面点击
//        //搜一搜
//        //这边根据登录状态存储数据
//        if (AccountManager.getInstance().checkUserLogin()){
//            com.iyuba.module.user.User user = new com.iyuba.module.user.User();
//            AccountManager managerLib =AccountManager.Instance(mContext);
//            if (managerLib.checkUserLogin()) {
//                user.vipStatus = String.valueOf(managerLib.getVipStatus());
//                user.uid = managerLib.getUserId();
//                user.name = managerLib.userName;
//            } else {
//                user.vipStatus = "0";
//                user.uid = 0;
//                user.name ="";
//            }
//            if (AdTimeUtils.isTime())
//                user.vipStatus = "1";
//            IyuUserManager.getInstance().setCurrentUser(user);
//        }
//        //看一看
//        IMovies.init(getApplicationContext(),Constant.APPID,Constant.APPType);
//        IMoviesManager.appId = Constant.APPID;
//        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
//        goFavorItem(fPart);
//
//    }
//
//    private void goFavorItem(BasicFavorPart part) {
//        String userIdStr = AccountManager.Instance(mContext).userId;
//
//        int userId;
//        boolean isVip;
//        try {
//            userId = Integer.parseInt(userIdStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//            userId = 0;
//        }
//
//        try {
//            int vip = ConfigManager.Instance().loadInt("isvip");
//            if (vip > 0) {
//                isVip = true;
//            } else {
//
//                isVip = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            isVip = false;
//        }
//        Log.e("TAG", "goFavorItem: "+part.getType());
//        switch (part.getType()) {
//            case "news":
//                startActivity(TextContentActivity.getIntent2Me(mContext,
//                        part.getId(), part.getTitle(), part.getTitleCn(), part.getType()
//                        , part.getCategoryName(), part.getCreateTime(), part.getPic(), part.getSource()));
//                break;
//            case "voa":
//            case "csvoa":
//            case "bbc":
//            case "song":
//                startActivity(AudioContentActivity.getIntent2Me(mContext,  part.getCategoryName(),
//                        part.getTitle(), part.getTitleCn(),
//                        part.getPic(), part.getType(), part.getId(), part.getSound()));
//                break;
//            case "voavideo":
//            case "meiyu":
//            case "ted":
//            case "bbcwordvideo":
//            case "topvideos":
//            case "japanvideos":
//                startActivity(VideoContentActivity.getIntent2Me(mContext,
//                        part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(),
//                        part.getType(), part.getId()));
//                break;
//            case "series":
//                Intent intent = SeriesActivity.buildIntent(mContext, part.getSeriesId(), part.getId());
//                startActivity(intent);
//                break;
//            case "smallvideo":
//                startActivity(VideoMiniContentActivity.buildIntentForOne(requireContext(),part.getId(),0,1,1));
//
//        }
//    }
//
//
//    private void showShare() {
//        String siteUrl= Constant.shareUrl;
//        String text = "讲真、你与学霸只有一个" + Constant.APPName + "的距离";
//        String imageUrl = "http://app."+Constant.IYBHttpHead()+"/android/images/FamilyAlbum/FamilyAlbum.png";
//        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//        weibo.removeAccount(true);
//        ShareSDK.removeCookieOnAuthorize(true);
//        OnekeyShare oks = new OnekeyShare();
//        // 关闭sso授权datatable( )
//        oks.disableSSOWhenAuthorize();
//        // 分享时Notification的图标和文字
//        // oks.setNotification(R.drawable.ic_launcher,
//        // getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(Constant.APPName);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(siteUrl);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(text);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        // oks.setImagePath("/sdcard/test.jpg");
//        // imageUrl是Web图片路径，sina需要开通权限
//        oks.setImageUrl(imageUrl);
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(siteUrl);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(Constant.APPName);
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(siteUrl);
//        // oks.setDialogMode();
//        // oks.setSilent(false);
//        oks.setCallback(new PlatformActionListener() {
//
//            @Override
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                Log.e("okCallbackonError", "onError");
//                Log.e("--分享失败===", arg2.toString());
//
//            }
//
//            @Override
//            public void onComplete(Platform arg0, int arg1,
//                                   HashMap<String, Object> arg2) {
//                Log.e("okCallbackonComplete", "onComplete");
//                if (AccountManager.Instance(mContext).userId != null) {
//                    Message msg = new Message();
//                    msg.obj = arg0.getName();
//                    if (arg0.getName().equals("QQ")
//                            || arg0.getName().equals("Wechat")
//                            || arg0.getName().equals("WechatFavorite")) {
//                        msg.what = 49;
//                    } else if (arg0.getName().equals("QZone")
//                            || arg0.getName().equals("WechatMoments")
//                            || arg0.getName().equals("SinaWeibo")
//                            || arg0.getName().equals("TencentWeibo")) {
//                        msg.what = 19;
//                    }
//                    handler.sendMessage(msg);
//                } else {
//                    handler.sendEmptyMessage(13);
//                }
//            }
//
//            @Override
//            public void onCancel(Platform arg0, int arg1) {
//                Log.e("okCallbackonCancel", "onCancel");
//            }
//        });
//        // 启动分享GUI
//        oks.show(getActivity());
//    }
//
//    //用户更换头像的回调
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(UserPhotoChangeEvent event){
//        //先清除缓存
//        GitHubImageLoader.Instace(mContext).clearCache();
//        GitHubImageLoader.Instace(mContext).setCirclePic(
//                AccountManager.Instance(mContext).userId, photo);
//    }
//
//
//    /*******************************新的显示********************************/
//}
