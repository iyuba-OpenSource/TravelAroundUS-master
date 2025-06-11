package com.iyuba.concept2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.concept2.R;
import com.iyuba.concept2.activity.MultipleChoiceActivity;
import com.iyuba.concept2.activity.StudyActivity;
import com.iyuba.concept2.activity.SummaryActivity;
import com.iyuba.concept2.adapter.NewVoaAdapter;
import com.iyuba.concept2.adapter.VoaAdapter;
import com.iyuba.concept2.lil.ui.ad.util.show.template.AdTemplateShowManager;
import com.iyuba.concept2.lil.ui.ad.util.show.template.AdTemplateViewBean;
import com.iyuba.concept2.lil.ui.ad.util.show.template.OnAdTemplateShowListener;
import com.iyuba.concept2.lil.ui.search.NewSearchActivity;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.mode.VoaTitleBean;
import com.iyuba.concept2.sqlite.op.VoaDetailOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.PlayerNextEvent;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.manager.StudyContentManager;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.play.ExtendedPlayer;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页课程列表界面
 */
public class HomeFragment extends Fragment{
    private Context mContext;
    private View root;

    private RecyclerView recyclerView;
    private NewVoaAdapter newVoaAdapter;

//    private ListView voaListView;
//    private VoaAdapter voaAdapter;

    private TextView title;

    private ImageButton mSearch;
    private List<Voa> voaList = new ArrayList<>();
    private List<VoaTitleBean> voaTitleList = new ArrayList<VoaTitleBean>();

    private VoaOp voaOp;
    private int curVoaId;

    private ImageButton showBookButton;
    private CustomDialog waittingDialog;
    private Button buttonLately;


    private RelativeLayout mPlayLayout;//后台播放布局
    private TextView mPlayTitle, mPlayTitleCn;//后台播放布局
    private ImageButton mPlayBtn;//后台播放布局

    private Voa curVoa;
    VoaDetailOp voaDetailOp;
    private String bellVoaId;

    private int index = 0;
    private static int OFFSET = 10;
    private int curBook;
    private String uid;
    private BackPlayer videoViewBP = null;    //播放原文正常播放器
    private ExtendedPlayer videoView = null; //播放文章用的调速播放器
    private boolean isInitialize;

    private Handler handlerDownload = new Handler();

    Runnable runnable = new Runnable() {
        public void run() {
//            if (voaAdapter != null){
//                voaAdapter.notifyDataSetChanged();
//            }
            if (newVoaAdapter!=null){
                newVoaAdapter.notifyDataSetChanged();
            }
            handlerDownload.postDelayed(this, 500);
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.voa_list, container, false);
        uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        init();

        if (!com.iyuba.concept2.manager.ConfigManager.Instance(getActivity()).loadBoolean("firstSendbook")
                && ConfigManager.Instance().loadInt("firstSendBookFlag") == 20
                && getActivity().getPackageName().equals("com.iyuba.concept2")) {
            //sendBookPop = new SendBookPop(getActivity(), root);
            com.iyuba.concept2.manager.ConfigManager.Instance(getActivity()).putInt("firstSendBookFlag", 0);
        }
//        sendBookPop = new SendBookPop(getActivity(), root);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);

        if (BackgroundManager.Instance().bindService != null) {
            if (videoView == null)
                videoView = BackgroundManager.Instance().bindService.getPlayer();
            if (videoViewBP == null)
                videoViewBP = BackgroundManager.Instance().bindService.getVvv();
        }

        if ((videoView != null && videoView.isPlaying()) ||
                (videoViewBP != null && videoViewBP.isPlaying())) {
                isInitialize=true;
        }else{
            isInitialize = false;
        }

        if (isInitialize) {
            mPlayLayout.setVisibility(View.VISIBLE);
            String titleCn = ConfigManager.Instance().loadString("bg_title_cn");
            String titleEn = ConfigManager.Instance().loadString("bg_title_en");
            if(titleCn != null){
                mPlayTitle.setText(titleEn);
                mPlayTitleCn.setText(titleCn);
            }

            mPlayBtn.setBackgroundResource(R.drawable.image_pause);
            showPlayNotification(true);
        }else {
//            mPlayLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //关闭广告
        AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
    }

    public void init() {
        waittingDialog = WaittingDialog.showDialog(mContext);
        voaOp = new VoaOp(mContext);
        voaDetailOp = new VoaDetailOp(mContext);
        title = root.findViewById(R.id.title);
        title.setText("走遍美国");//学英语
        mSearch=root.findViewById(R.id.button_show_searchLayout);
        mSearch.setOnClickListener(v -> {
            /*Intent intent = new Intent();
            intent.setClass(mContext, SearchActivity.class);
            startActivity(intent);*/
            //停止音频播放
            stopPlayer();

            //判断登录
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            NewSearchActivity.start(mContext,null);
        });
        curBook = ConfigManager.Instance().loadInt("curBook");
        try {
            bellVoaId = ConfigManager.Instance().loadString("bellvoaid");
        } catch (Exception e) {
            e.printStackTrace();
        }

        showBookButton = root.findViewById(R.id.button_show_category);
        //隐藏 or 显示
        if (!getActivity().getPackageName().equals("com.iyuba.concept2")) {
            showBookButton.setVisibility(View.GONE);
        }
        showBookButton.setOnClickListener(v -> {
            /*Intent intent = new Intent();
            intent.setClass(mContext, SearchActivity.class);
            startActivity(intent);*/
            //停止音频播放
            stopPlayer();

            //判断登录
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            NewSearchActivity.start(mContext,null);
        });

        mPlayLayout = root.findViewById(R.id.rl_bg_play);
        mPlayBtn = root.findViewById(R.id.ib_play_btn);
        mPlayTitle = root.findViewById(R.id.tv_play_title);
        mPlayTitleCn = root.findViewById(R.id.tv_play_title_cn);

        mPlayBtn.setOnClickListener(v -> {
            //先获取播放器状态
            boolean isSpeed = SettingConfig.Instance().isSpeedPlayer();//获取播放状态

            if (isSpeed&&videoView!=null){
                if (videoView.isPlaying()){
                    videoView.pause();
                    mPlayBtn.setBackgroundResource(R.drawable.image_play);//?这个有问题！
                    showPlayNotification(false);
                }else {
                    try {
                        videoView.start();
                        mPlayBtn.setBackgroundResource(R.drawable.image_pause);
                        showPlayNotification(true);
                    }catch (Exception e){
                        LogUtils.e("播放器异常");
                        e.printStackTrace();
                    }
                }
            }

            if (!isSpeed&&videoViewBP!=null){
                if (videoViewBP.isPlaying()){
                    videoViewBP.pause();
                    mPlayBtn.setBackgroundResource(R.drawable.image_play);//?
                    showPlayNotification(false);
                }else {
                    try {
                        videoViewBP.start();
                        mPlayBtn.setBackgroundResource(R.drawable.image_pause);
                        showPlayNotification(true);
                    }catch (Exception e){
                        LogUtils.e("播放器异常");
                        e.printStackTrace();
                    }
                }
            }
        });

        mPlayLayout.setOnClickListener(v -> intent());

        handlerDownload.postDelayed(runnable, 1000);

        //设置列表显示
        getVoaList();

        buttonLately = (Button) root.findViewById(R.id.button_lately);
        buttonLately.setOnClickListener(arg0 -> intent());
    }

    private void intent(){
        int voaId = 0;
        if (ConfigManager.Instance().loadInt("voaIdSave") == 0) {
            voaId = 1;
        } else {
            voaId = ConfigManager.Instance().loadInt("voaIdSave");
        }
        curVoa = voaOp.findDataById(voaId);
        getVoaDetail();
    }

    Handler handlerWaiting = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    if (waittingDialog.isShowing())
                        waittingDialog.dismiss();
                    break;
                case 1:
                    /*if (!waittingDialog.isShowing()) {
                        waittingDialog.show();////????
                    }*/
                    break;
                case 2:
                    //这里不是临时数据
                    StudyContentManager.getInstance().setTempData(false);

                    //换成新的跳转操作
                    /*Intent intent = new Intent();
                    intent.setClass(mContext, StudyActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    intent.putExtra("curVoaId", curVoaId + "");
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("跳转异常！");
                    }*/
//                    ((Activity) mContext).overridePendingTransition(
//                            R.anim.slide_in_right, android.R.anim.fade_out);
                    StudyActivity.start(mContext,String.valueOf(curVoaId));
                    break;
            }
        }
    };

    public void getVoaDetail() {
        handlerWaiting.sendEmptyMessage(1);

        new Thread(() -> {
            // 从本地数据库中查找
            VoaDataManager.Instace().voaTemp = curVoa;
            VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(curVoa.voaId);

            if (VoaDataManager.Instace().voaDetailsTemp != null
                    && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                VoaDataManager.Instace().setSubtitleSum(curVoa,
                        VoaDataManager.Instace().voaDetailsTemp);
                handlerWaiting.sendEmptyMessage(2);//跳转不要在子线程中！！！！
            }
        }).start();
    }


    public void getVoaList() {
        curBook = ConfigManager.Instance().loadInt("curBook");
        VoaDataManager.Instace().voasTemp = voaOp.findDataByPage(curBook, OFFSET, index);
        voaList = VoaDataManager.Instace().voasTemp;
        for (int i = 0; i < voaList.size(); i = i + 3) {
            VoaTitleBean bean = new VoaTitleBean();
            bean.setmTitleCn(voaList.get(i).titleCn);
            bean.setmTitleEn(voaList.get(i).title);
            bean.setmVoaId(voaList.get(i).lesson);

            List<Voa> voa = new ArrayList<Voa>();
            voa.add(voaList.get(i));
            voa.add(voaList.get(i + 1));
            voa.add(voaList.get(i + 2));
            bean.setVoaList(voa);
            voaTitleList.add(bean);
            //voa.clear();
        }

        //新的界面显示
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newVoaAdapter = new NewVoaAdapter(getActivity(),voaTitleList);
        recyclerView.setAdapter(newVoaAdapter);
        newVoaAdapter.setOnItemButtonClickListener(new NewVoaAdapter.OnItemButtonClickListener() {
            @Override
            public void onActClick(int position, int art, Voa voa) {
                handlerWaiting.sendEmptyMessage(1);//加载过度

                VoaDataManager.Instace().voaTemp = voaList.get(position);
                curVoa = voaTitleList.get(position).getVoaList().get(art);
                curVoaId = curVoa.voaId;
                ConfigManager.Instance().putInt("voaIdSave", curVoaId);
                getVoaDetail();// 准备数据

                mPlayTitle.setText("Episode  "+curVoa.title);
                mPlayTitleCn.setText(curVoa.titleCn);
                ConfigManager.Instance().putString("bg_title_cn",curVoa.titleCn);
                ConfigManager.Instance().putString("bg_title_en","Episode  "+curVoa.title);
                mPlayBtn.setBackgroundResource(R.drawable.image_pause);
                showPlayNotification(true);

                voaOp.updateIsRead(curVoaId);
                voaOp.updateReadCount(curVoaId);
                voaTitleList.get(position).setRead(true);
                MultipleChoiceActivity.instance = null;
            }

            @Override
            public void onSummaryClick(int position) {
                SummaryActivity.start(mContext, position);
            }
        });

        //刷新广告显示
        refreshTemplateAd();

        /*voaListView = root.findViewById(R.id.voa_list);
        voaListView.setOnScrollListener(onScrollListener);
        voaAdapter = new VoaAdapter(mContext, voaTitleList);
        voaListView.setAdapter(voaAdapter);//自定义接口
        voaAdapter.itemSetOnclick(new VoaAdapter.OnItemButtonClickListener() {
            @Override
            public void onActClick(int position, int art, Voa voa) {
                handlerWaiting.sendEmptyMessage(1);//加载过度

                VoaDataManager.Instace().voaTemp = voaList.get(position);
                curVoa = voaTitleList.get(position).getVoaList().get(art);
                curVoaId = curVoa.voaId;
                ConfigManager.Instance().putInt("voaIdSave", curVoaId);
                getVoaDetail();// 准备数据

                mPlayTitle.setText("Episode  "+curVoa.title);
                mPlayTitleCn.setText(curVoa.titleCn);
                ConfigManager.Instance().putString("bg_title_cn",curVoa.titleCn);
                ConfigManager.Instance().putString("bg_title_en","Episode  "+curVoa.title);
                mPlayBtn.setBackgroundResource(R.drawable.image_pause);
                showPlayNotification(true);

                voaOp.updateIsRead(curVoaId);
                voaOp.updateReadCount(curVoaId);
                voaTitleList.get(position).setRead(true);
                MultipleChoiceActivity.instance = null;
            }

            @Override
            public void onSummaryClick(int position) {
                SummaryActivity.start(mContext, position);
            }
        });*/
    }

    /*public void refresh() {
        curBook = ConfigManager.Instance().loadInt("curBook");
        index = 0;
        VoaDataManager.Instace().voasTemp = voaOp.findDataByPage(curBook,
                OFFSET, index);
        voaList = VoaDataManager.Instace().voasTemp;
        voaTitleList.clear();
        //刷新
        List<Voa> voa = new ArrayList<Voa>();
        for (int i = 0; i < voaList.size(); i = i + 3) {
            VoaTitleBean bean = new VoaTitleBean();
            bean.setmTitleCn(voaList.get(i).titleCn);
            bean.setmTitleEn(voaList.get(i).title);
            bean.setmVoaId(voaList.get(i).lesson);
            voa.add(voaList.get(i));
            voa.add(voaList.get(i + 1));
            voa.add(voaList.get(i + 2));
            bean.setVoaList(voa);
            voa.clear();
            voaTitleList.add(bean);
        }

        voaAdapter.setList(voaTitleList);
        voaAdapter.notifyDataSetChanged();
//        newVoaAdapter.refreshData(voaTitleList);
    }*/


    /*private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                    // 判断滚动到底部
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        index = index + OFFSET;
                        List<Voa> voaListTemp = voaOp.findDataByPage(curBook, OFFSET, index);

                        if (voaListTemp != null) {
                            voaList.addAll(voaListTemp);
//                        voaAdapter.notifyDataSetChanged();
                            newVoaAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };*/

    @Override
    public void onPause() {
        super.onPause();
        Log.e("HomeFragment", "onPause");
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onStop() {
        super.onStop();
        handlerWaiting.sendEmptyMessage(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerNextEvent event) {
        Log.e("TAG", "onEvent: 下一个"+event.toString());
        mPlayTitle.setText(event.sentence);
        mPlayTitleCn.setText(event.sentenceCn);
//        List<VoaTitleBean> dataList= voaAdapter.getData();
        List<VoaTitleBean> dataList = newVoaAdapter.getData();
        for (VoaTitleBean bean:dataList){
            bean.isRead=false;
            for (Voa voa:bean.getVoaList()){
                voa.isReadArt=false;
            }
        }
        dataList.get(event.position-1).isRead=true;
        dataList.get(event.position-1).getVoaList().get(event.artPosition-1).isReadArt=true;

//        voaAdapter.reSetData(dataList);
//        voaAdapter.notifyDataSetChanged();
        newVoaAdapter.refreshData(dataList);
        //刷新广告显示
        refreshTemplateAd();
    }

    /*********************************二次数据处理*********************************/
    //播放状态通知
    private void showPlayNotification(boolean isPlay){
        BackgroundManager.Instance().bindService.showNotification(false,isPlay,mPlayTitle.getText().toString().trim(),StudyActivity.class);
    }

    //停止音频播放
    public void stopPlayer(){
        //获取是否倍速播放器
        boolean isSpeed = SettingConfig.Instance().isSpeedPlayer();
        if (isSpeed&&videoView!=null){
            if (videoView.isPlaying()){
                videoView.pause();
                mPlayBtn.setBackgroundResource(R.drawable.image_play);//?这个有问题！
                showPlayNotification(false);
            }
        }else {
            if (videoViewBP!=null){
                if (videoViewBP.isPlaying()){
                    videoViewBP.pause();
                    mPlayBtn.setBackgroundResource(R.drawable.image_play);//?
                    showPlayNotification(false);
                }
            }
        }
    }

    /*****************************设置新的信息流广告************************/
    //当前信息流广告的key
    private String adTemplateKey = HomeFragment.class.getName();
    //模版广告数据
    private AdTemplateViewBean templateViewBean = null;
    //显示广告
    private void showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = new AdTemplateViewBean(R.layout.item_ad_mix, R.id.template_container, R.id.ad_whole_body, R.id.native_main_image, R.id.native_title, recyclerView, newVoaAdapter, new OnAdTemplateShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String showAdMsg) {

                }

                @Override
                public void onAdClick() {

                }
            });
            AdTemplateShowManager.getInstance().setShowData(adTemplateKey, templateViewBean);
        }
        AdTemplateShowManager.getInstance().showTemplateAd(adTemplateKey,getActivity());
    }

    //刷新广告操作[根据类型判断刷新还是隐藏]
    private void refreshTemplateAd(){
        if (!AdBlocker.getInstance().shouldBlockAd() && !UserInfoManager.getInstance().isVip()) {
            //先删除广告
            AdTemplateShowManager.getInstance().clearAd(adTemplateKey);
            //在加载广告
            showTemplateAd();
        } else {
            AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
        }
    }
}