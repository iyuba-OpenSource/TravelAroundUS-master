package com.iyuba.concept2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.MyPagerAdapter;
import com.iyuba.concept2.fragment.LanguageFragment;
import com.iyuba.concept2.fragment.SummaryFragment;
import com.iyuba.concept2.listener.RequestCallBack;
import com.iyuba.concept2.protocol.AddCreditsRequest;
import com.iyuba.concept2.sqlite.mode.ReadVoiceComment;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * create 2018-11-13
 * by zhao hao 赵皓
 */
public class SummaryActivity extends AppCompatActivity {
    private int artId;
    private TextView mToolbarTitle;
    private Button btnBack, btnShare;
    private ViewPager mViewPager;
    private ImageView mIvlangeuage, mIvSummary;
    private LanguageFragment langeuageFragment;
    private SummaryFragment summaryFragment;
    private LinearLayout mLlLangeuage, mLlSummary;
    private String[] mTitles = {"实用语言", "本章小结"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private boolean isConnected;
    private Context mContext;
    private Voa voa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        mContext=this;
        // 判断是否连接网络
        isConnected = NetWorkState.isConnectingToInternet();
        VoaOp voaOp=new VoaOp(mContext);
        initIntentExtras();
        voa=voaOp.findDataById(artId);
        initView();
        initViewPage();
    }

    private void initView() {
        mToolbarTitle = findViewById(R.id.summary_title);
        btnBack = findViewById(R.id.button_back);
        btnShare = findViewById(R.id.btn_share);
        mViewPager = findViewById(R.id.vp_summary);
        mIvlangeuage = findViewById(R.id.iv_language);
        mIvSummary = findViewById(R.id.voa_summary);
        mLlLangeuage = findViewById(R.id.ll_language);
        mLlSummary = findViewById(R.id.ll_summary);

        mToolbarTitle.setText("EPISODE " + artId + " Summary");

        mLlLangeuage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImg();
                mIvlangeuage.setImageResource(R.drawable.voa_words_press_new);
                mViewPager.setCurrentItem(0);
            }
        });
        mLlSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImg();
                mIvSummary.setImageResource(R.drawable.voa_improtent_sentences_press_new);
                mViewPager.setCurrentItem(1);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i==0){
                    resetImg();
                    mIvlangeuage.setImageResource(R.drawable.voa_words_press_new);
                }else {
                    resetImg();
                    mIvSummary.setImageResource(R.drawable.voa_improtent_sentences_press_new);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected)
                    CustomToast.showToast(SummaryActivity.this,
                            R.string.category_check_network, 1000);
                else {
                    try {
                        showShare();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("异常", e.toString());
                    }
                }
            }
        });
    }

    public static void start(Context context, int artId) {
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtra("artId", artId);
        context.startActivity(intent);
    }

    private void initIntentExtras() {
        Intent intent = getIntent();
        artId = intent.getIntExtra("artId", 0) + 1;
    }

    /*
     * 初始化initViewPage
     */
    private void initViewPage() {
        for (int i = 0; i < mTitles.length; i++) {
            switch (i) {
                case 0:
                    langeuageFragment = LanguageFragment.newInstance(artId);
                    mFragments.add(langeuageFragment);
                    break;
                case 1:
                    summaryFragment = SummaryFragment.newInstance(artId);
                    mFragments.add(summaryFragment);
                    break;
                default:
            }
        }
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);
    }

    /*
     * 把所有图片变暗
     */
    private void resetImg() {
        mIvSummary.setImageResource(R.drawable.voa_important_sentences);
        mIvlangeuage.setImageResource(R.drawable.voa_words);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 19:
                case 41:
                    if (UserInfoManager.getInstance().isLogin()) {
                        final ReadVoiceComment rvc = new ReadVoiceComment(voa);
                        RequestCallBack rc = new RequestCallBack() {

                            @Override
                            public void requestResult(
                                    Request result) {
                                AddCreditsRequest rq = (AddCreditsRequest) result;
                                if (rq.isShareFirstlySuccess()) {
                                    String msg = "分享成功，增加了"
                                            + rq.addCredit
                                            + "积分，共有"
                                            + rq.totalCredit
                                            + "积分";
                                    CustomToast.showToast(
                                            mContext, msg,
                                            3000);
                                } else if (rq
                                        .isShareRepeatlySuccess()) {
                                    CustomToast.showToast(
                                            mContext,
                                            "分享成功", 3000);
                                }
                            }
                        };
                        int uid = UserInfoManager.getInstance().getUserId();
                        AddCreditsRequest rq = new AddCreditsRequest(
                                uid, rvc.getVoaRef().voaId,
                                msg.what, rc);
                        RequestQueue queue = Volley
                                .newRequestQueue(mContext);
                        queue.add(rq);
                    }
                    break;
                case 13:
                    Toast.makeText(mContext, "分享成功！未获取到用户ID", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void showShare() {

       /* ？？？？？？？？？？？？？？？？？？？？？偶尔会崩溃，初始化ShareSDK 出错？？？？？？？？？？？？？？？*/
//        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//        weibo.removeAccount(true);
//        ShareSDK.removeCookieOnAuthorize(true);
        ReadVoiceComment rvc = new ReadVoiceComment(voa, true);
        PlatformActionListener listener =new PlatformActionListener() {

            private int getPlatformSrid(Platform platform) {
                int srid = 19;
                String name = platform.getName();
                if (name.equals(QQ.NAME) || name.equals(Wechat.NAME)
                        || name.equals(WechatFavorite.NAME)) {
                    srid = 41;
                } else if (name.equals(QZone.NAME) || name.equals(WechatMoments.NAME)
                        || name.equals(SinaWeibo.NAME)) {
                    srid = 19;
                }
                return srid;
            }

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                LogUtil.e("okCallbackonError", "onError");
                LogUtil.e("--分享失败===", arg2.toString());
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                LogUtil.e("okCallbackonComplete", "onComplete");
                if (UserInfoManager.getInstance().isLogin()) {
                    int srId = getPlatformSrid(arg0);

                    Message msg = new Message();
                    msg.obj = arg0.getName();
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
                    msg.what=srId;
                    handler.sendMessage(msg);

                } else {
                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                LogUtil.e("okCallback onCancel", "onCancel");
            }
        };

       showShare(this, rvc.getShareTitle(), rvc.getShareShortText(), rvc.getArticleShareUrl(),
               rvc.getShareImageUrl(), "很不错的应用，大家快来使用呀！", Constant.APPName,
               rvc.getArticleShareUrl(), listener);

    }
    private void showShare(Context context, String title, String content, String shareurl,
                          String imageUrl, String comment, String site, String titleurl,
                          PlatformActionListener actionListener) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(titleurl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(imageUrl);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(site);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareurl);
        if (actionListener != null) oks.setCallback(actionListener);
        // 启动分享GUI
        oks.show(context);
    }
}
