package com.iyuba.concept2.fragment;

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
import com.iyuba.core.lil.util.LibScreenUtil;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.imooclib.IMooc;

public class SmallVideoFragment extends Fragment {

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

    public static SmallVideoFragment getInstance(){
        SmallVideoFragment fragment = new SmallVideoFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.layout_container,null);
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
        //设置小视频信息
        IHeadlineManager.appId = String.valueOf(Constant.APPID);
        IHeadlineManager.appName = Constant.APPType;
        //设置广告类型
        IHeadline.setAdAppId(String.valueOf(AdShowUtil.NetParam.AppId));
        IHeadline.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj, AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);
        //设置广告自适应
        int adWidth = LibScreenUtil.getScreenW(getActivity());
        IMooc.setYdsdkTemplateAdWidthHeight(adWidth,0);
        //设置小视频内容
        String[] types = new String[]{
                HeadlineType.SMALLVIDEO
        };

        Bundle videoBundle = DropdownTitleFragmentNew.buildArguments(10,types,false);
        DropdownTitleFragmentNew mFragment = DropdownTitleFragmentNew.newInstance(videoBundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container,mFragment).show(mFragment).commitNow();
    }
}
