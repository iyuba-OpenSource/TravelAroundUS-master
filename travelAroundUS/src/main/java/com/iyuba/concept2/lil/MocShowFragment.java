package com.iyuba.concept2.lil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.American.constant.AdTestKeyData;
import com.iyuba.concept2.BuildConfig;
import com.iyuba.concept2.R;
import com.iyuba.concept2.lil.ui.ad.util.show.AdShowUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibScreenUtil;
import com.iyuba.core.me.activity.BuyIyubiActivity;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.ImoocManager;
import com.iyuba.imooclib.event.ImoocBuyIyubiEvent;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.event.ImoocPayCourseEvent;
import com.iyuba.imooclib.ui.mobclass.MobClassFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * @title:
 * @date: 2023/6/26 14:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class MocShowFragment extends Fragment {

    private Context context;

    /**
     * 标记已加载完成，保证懒加载只能加载一次
     */
    private boolean hasLoaded = false;
    /**
     * 标记Fragment是否已经onCreate
     */
    private boolean isCreated = false;
    /**
     * 界面对于用户是否可见
     */
    private boolean isVisibleToUser = false;


    public static MocShowFragment getInstance(){
        MocShowFragment fragment = new MocShowFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(context).inflate(R.layout.layout_container,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lazyLoad();
        isCreated = true;
    }

    /**
     * 监听界面是否展示给用户，实现懒加载
     * 这个方法也是网上的一些方法用的最多的一个，我的思路也是这个，不过把整体思路完善了一下
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //注：关键步骤
        this.isVisibleToUser = isVisibleToUser;
        lazyLoad();
    }

    /**
     * 懒加载方法，获取数据什么的放到这边来使用，在切换到这个界面时才进行网络请求
     */
    private void lazyLoad() {

        //如果该界面不对用户显示、已经加载、fragment还没有创建，
        //三种情况任意一种，不获取数据
        if (!isVisibleToUser || hasLoaded || !isCreated) {
            return;
        }
        initViewVideo();
        //注：关键步骤，确保数据只加载一次
        hasLoaded = true;
    }

    public void initViewVideo() {
        //设置appId
        ImoocManager.appId = String.valueOf(Constant.APPID);
        //根据要求显示有道广告
        IMooc.setAdAppId(String.valueOf(AdShowUtil.NetParam.AppId));
        IMooc.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IMooc.setYoudaoId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IMooc.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);
        //设置广告自适应
        int adWidth = LibScreenUtil.getScreenW(getActivity());
        IMooc.setYdsdkTemplateAdWidthHeight(adWidth,0);

        //显示课程
        ArrayList<Integer> typeIdFilter = new ArrayList<>();
        typeIdFilter.add(-2);//全部
        typeIdFilter.add(-1);//最新
        typeIdFilter.add(2);//四级
        typeIdFilter.add(3);//VOA
        typeIdFilter.add(4);//六级
        typeIdFilter.add(7);//托福
        typeIdFilter.add(8);//考研
        typeIdFilter.add(9);//BBC
        typeIdFilter.add(21);//新概念
        typeIdFilter.add(22);//走遍美国
        //typeIdFilter.add(28);//学位
        typeIdFilter.add(52);//考研二
        typeIdFilter.add(61);//雅思
        typeIdFilter.add(91);//中职
        MobClassFragment mobClassFragment = MobClassFragment.newInstance(MobClassFragment.buildArguments(22,false,typeIdFilter));

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container,mobClassFragment).show(mobClassFragment).commitNow();
    }

    /*********************************相关回调*******************************/
    //微课会员购买
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyVIPEvent dlEvent) {
        if (!UserInfoManager.getInstance().isLogin()){
            LoginUtil.startToLogin(getActivity());
            return;
        }

        VipCenterGoldActivity.start(getActivity(),VipCenterGoldActivity.VIP_GOLD);
    }

    //微课爱语币购买
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyIyubiEvent event){
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
            return;
        }

        Intent intent = new Intent();
        intent.setClass(getActivity(), BuyIyubiActivity.class);
        intent.putExtra("title", "爱语币充值");
        startActivity(intent);
    }

    //微课直购
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPayCourseEvent event){
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
            return;
        }

        String desc = event.body;
        String info = "花费"+event.price+"元购买微课("+event.body+")";
        String subject = "微课直购";
        int type = 0;//无作用
        Intent intent = PayOrderActivity.buildIntent(getActivity(),event.price,type,subject,desc,getOutTradeNo(),info,String.valueOf(event.productId),String.valueOf(event.courseId));
        startActivity(intent);
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }
}
