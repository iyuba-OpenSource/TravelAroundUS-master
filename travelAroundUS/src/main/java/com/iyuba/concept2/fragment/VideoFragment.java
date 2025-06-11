package com.iyuba.concept2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.American.constant.AdTestKeyData;
import com.iyuba.concept2.BuildConfig;
import com.iyuba.concept2.R;
import com.iyuba.concept2.lil.ui.ad.util.show.AdShowUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.util.ChangeVideoEvnet;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibScreenUtil;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.event.HeadlinePlayEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.event.ImoocPlayEvent;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.movies.event.IMoviePlayEvent;
import com.iyuba.play.ExtendedPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by iyuba on 2017/7/27.
 */

public class VideoFragment extends Fragment {

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

    public static VideoFragment getInstance(){
        VideoFragment fragment = new VideoFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(context).inflate(R.layout.layout_container,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lazyLoad();
        isCreated = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        IHeadlineManager.appId = Constant.APPID;
        IHeadlineManager.appName = Constant.APPType;
        //设置广告类型
        IHeadline.setAdAppId(String.valueOf(AdShowUtil.NetParam.AppId));
        IHeadline.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);
        //设置广告自适应
        int adWidth = LibScreenUtil.getScreenW(getActivity());
        IMooc.setYdsdkTemplateAdWidthHeight(adWidth,0);
        //设置显示内容
        String[] types = new String[]{
                HeadlineType.VOAVIDEO,
                HeadlineType.MEIYU,
                HeadlineType.TED,
                HeadlineType.BBCWORDVIDEO,
                HeadlineType.TOPVIDEOS
        };


        Bundle bundle = DropdownTitleFragmentNew.buildArguments( 10, HolderType.SMALL, types, false);
        DropdownTitleFragmentNew videoFragment = DropdownTitleFragmentNew.newInstance(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container,videoFragment).show(videoFragment).commitNow();

        EventBus.getDefault().post(new ChangeVideoEvnet(true));//刷新视频状态
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeVideoEvnet videoEvnet) {
        //使用新的20001接口处理
        if (UserInfoManager.getInstance().isLogin()){
            UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPlayEvent event) {
        LogUtils.e("event 开始播放");
        stopPlay();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlinePlayEvent event) {
        LogUtils.e("event 开始播放");
        stopPlay();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMoviePlayEvent event) {
        LogUtils.e("event 开始播放");
        stopPlay();
    }

    private void stopPlay() {
        ExtendedPlayer extendedPlayer = BackgroundManager.Instance().bindService.getPlayer();
        if (extendedPlayer != null && extendedPlayer.isPlaying()) {
            extendedPlayer.pause();
        }

        BackPlayer videoViewBP = BackgroundManager.Instance().bindService.getVvv();
        if (videoViewBP != null && videoViewBP.isPlaying()) {
            videoViewBP.pause();
        }

    }


    private boolean isTime() {
        long time = System.currentTimeMillis() / 1000;
        long flagTime = 1548484768;//1546922614 1547030614
        if (flagTime - time > 0) {
            long i = flagTime - time;
            LogUtil.e("时间还没到，剩余时间：" + i);
            return true;
        }
        return false;
    }
}
