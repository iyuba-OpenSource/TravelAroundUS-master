package com.iyuba.concept2.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.VoaLocalAdapter;
import com.iyuba.concept2.manager.DownloadStateManager;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.protocol.FavorUpdateRequest;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.op.VoaDetailOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.PlayerNextEvent;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.manager.StudyContentManager;
import com.iyuba.core.lil.model.local.bean.VoaCollectEntity;
import com.iyuba.core.lil.model.local.bean.VoaListenEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.model.remote.bean.Chapter_collect;
import com.iyuba.core.lil.model.remote.bean.Chapter_collect_show;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibDateUtil;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 收藏至本地
 */

public class LocalNews extends BasisActivity {
    public static final String tag_localType = "localType";

    private Context mContext;
    private int localType;// 0 local ; 1 love ; 2 heard
    private TextView titleText;
    private List<Voa> voaList;
    private ListView voaListView;
    private VoaLocalAdapter voaAdapter;
    private Button backButton, buttonEdit, buttonSyncho;
    private boolean isDelStart = false;
    private Voa voa;
    private VoaOp voaOp;
    private VoaDetailOp voaDetailOp;
    private CustomDialog waittingDialog;

    //获取收藏数据
    private Disposable getCollectDataDis;

    public static void start(Context context,int localType){
        Intent intent = new Intent();
        intent.setClass(context,LocalNews.class);
        intent.putExtra(tag_localType,localType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_list);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mContext = this;
        localType = getIntent().getIntExtra(tag_localType, localType);

        init();
        initWidget();

        handler.postDelayed(runnable, 1000);// 每兩秒執行一次runnable.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LibRxUtil.unDisposable(getCollectDataDis);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (voaAdapter != null) {
                handler.sendEmptyMessage(7);
            }
            handler.postDelayed(this, 1000);//2秒刷新
        }
    };

    public void initWidget() {
        waittingDialog = WaittingDialog.showDialog(mContext);

        titleText = (TextView) findViewById(R.id.title);

        backButton = (Button) findViewById(R.id.button_back);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //编辑删除按钮
        buttonEdit = (Button) findViewById(R.id.button_edit);
        buttonEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDelStart) {
                    //这里因为接口的问题，需要循环删除才行
                    if (localType==1){
                        //收藏数据删除
                        deleteCollectDataRecycle();
                    }else if (localType==2){
                        //试听数据删除
                        deleteMultiListenData();
                    }else {
                        Iterator<Voa> iteratorVoa = voaList.iterator();
                        while (iteratorVoa.hasNext()) {
                            Voa voaTemp = iteratorVoa.next();

                            if (voaTemp.isDelete) {
                                deleteData(localType, voaTemp);
                                iteratorVoa.remove();

                            /*if (localType == 1) {
                                voaOp.updateSynchro(voaTemp.voaId, 0);
                                handler.sendEmptyMessage(3);
                            }*/
                            }
                        }

                        handler.sendEmptyMessage(7);
                        isDelStart = false;
                        buttonEdit.setBackgroundResource(R.drawable.button_edit);
                        changeItemDeleteStart(false);
                    }
                } else {
                    buttonEdit.setBackgroundResource(R.drawable.button_edit_finished);
                    if (voaAdapter != null) {
                        isDelStart = true;
                        changeItemDeleteStart(true);
                    }
                }
            }
        });

        buttonSyncho = (Button) findViewById(R.id.button_syncho);
        buttonSyncho.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().isLogin()) {
                    handler.sendEmptyMessage(2);
                } else {
                    LoginUtil.startToLogin(mContext);
                }
            }
        });
        if (localType != 1) {
            buttonSyncho.setVisibility(View.GONE);
        }

        //下面都是两个LIST相关的event，动画等等。
        voaListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                VoaDataManager.Instace().voaTemp = voaList.get(arg2);
                if (isDelStart) {
                    boolean isDelete = voaList.get(arg2).isDelete;
                    voaList.get(arg2).isDelete = !isDelete;
                    voaAdapter.notifyDataSetChanged();
                } else {
                    waittingDialog.show();
                    voa = voaList.get(arg2);
                    handler.sendEmptyMessage(6);
                    handler.sendEmptyMessage(7);
                }
            }
        });

        setTop(localType);
    }

    public void setTop(int type) {
        switch (type) {
            case 0:
                titleText.setText(R.string.local_title);
                break;
            case 1:
                titleText.setText(R.string.favor_title);
                break;
            case 2:
                titleText.setText(R.string.read_title);
                break;
        }
    }

    public void init() {
        voaOp = new VoaOp(mContext);
        voaDetailOp = new VoaDetailOp(mContext);
        voaList = getData(localType);
        voaAdapter = new VoaLocalAdapter(mContext, voaList);
        voaListView = findViewById(R.id.voa_list);
        voaListView.setAdapter(voaAdapter);
    }

    public List<Voa> getData(int type) {
        List<Voa> voaList = new ArrayList<>();
        switch (type) {
            case 0:
                voaList = voaOp.findDataFromDownload();
                break;
            case 1:
//                voaList = voaOp.findDataFromCollection();

                //这里根据辅助数据内容获取数据，并且处理显示
                List<VoaCollectEntity> collectList = HelpDataManager.getInstance().getVoaCollectByUserId(UserInfoManager.getInstance().getUserId());
                if (collectList!=null&&collectList.size()>0){
                    for (int i = 0; i < collectList.size(); i++) {
                        //逐个数据查询
                        Voa tempVoa = voaOp.findDataById(collectList.get(i).voaId);
                        tempVoa.isCollect = "1";
                        voaList.add(tempVoa);
                    }
                }
                break;
            case 2:
//                voaList = voaOp.findDataFromRead();

                //这里使用辅助数据表获取数据并展示
                List<VoaListenEntity> listenList = HelpDataManager.getInstance().getVoaListenByUserId(UserInfoManager.getInstance().getUserId());
                if (listenList!=null&&listenList.size()>0){
                    for (int i = 0; i < listenList.size(); i++) {
                        //逐个数据查询并设置
                        Voa tempVoa = voaOp.findDataById(listenList.get(i).voaId);
                        tempVoa.isRead = "1";
                        voaList.add(tempVoa);
                    }
                }
                break;
        }

        return voaList;
    }

    public void deleteData(int type, Voa voa) {
        switch (type) {
            case 0:
                DownloadStateManager.getInstance().delete(voa.voaId);
                voaOp.deleteDataInDownload(voa.voaId);

                File file= new File(Constant.videoAddr +voa.voaId + Constant.append);
                if(file.exists()){
                    file.delete();
                }
                break;
            case 1:
                /*for (Voa voaTemp : VoaDataManager.Instace().voasTemp) {
                    if (voaTemp.voaId == voa.voaId) {
                        voaTemp.isCollect = "0";
                    }
                }
                voaOp.deleteDataInCollection(voa.voaId);*/

                //这里使用新的数据处理
                //这里直接删除本地数据(数据处理真是666，功能无解，完蛋了，毁灭吧)
                HelpDataManager.getInstance().deleteSingleVoaCollectData(UserInfoManager.getInstance().getUserId(), voa.voaId);
                break;
            case 2:
                /*for (Voa voaTemp : VoaDataManager.Instace().voasTemp) {
                    if (voaTemp.voaId == voa.voaId) {
                        voaTemp.isRead = "0";
                    }
                }
                voaOp.deleteDataInRead(voa.voaId);*/

                //相同操作-删除新数据库中的数据
                HelpDataManager.getInstance().deleteSingleVoaListenData(UserInfoManager.getInstance().getUserId(), voa.voaId);
                break;
        }
    }

    //	以上均为两个个list切换及相关按键逻辑
    public void changeItemDeleteStart(boolean isDelete) {
        if (voaAdapter != null) {
            voaAdapter.modeDelete = isDelete;//是否显示删除按钮
            voaAdapter.notifyDataSetChanged();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    waittingDialog.dismiss();
                    break;
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    handler.sendEmptyMessage(1);
                    List<Voa> tempVoaList = voaOp.findUnSynchroData();
                    if (tempVoaList != null && localType == 1) {
                        Message message = null;
                        for (Voa tempVoa : tempVoaList) {
                            message = handler.obtainMessage();
                            message.what = 3;
                            message.arg1 = tempVoa.voaId;
                            if (tempVoa.isCollect.equals(""))tempVoa.isCollect="0";
                            message.arg2 = Integer.parseInt(tempVoa.isCollect);//valueOf 是转换成 Integer
                            handler.sendMessageDelayed(message, 1500);
                        }
                    }

                    handler.sendEmptyMessage(0);

                    /*ExeProtocol.exe(
                            new FavorSynRequest(String.valueOf(UserInfoManager.getInstance().getUserId())),
                            new ProtocolResponse() {
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    FavorSynResponse response = (FavorSynResponse) bhr;
                                    if (response.list != null
                                            && response.list.size() != 0) {
                                        for (int voaid : response.list) {
                                            voaOp.updateSynchro(voaid, 1);
                                            voaOp.insertDataToCollection(voaid);
                                        }
                                        handler.sendEmptyMessage(9);
                                        handler.sendEmptyMessageDelayed(0, 1000);
                                    } else {
                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(5);
                                        handler.sendEmptyMessage(7);
                                    }
                                }

                                @Override
                                public void error() {
                                    handler.sendEmptyMessage(0);
                                    handler.sendEmptyMessage(4);
                                }
                            });*/

                    //更换接口
                    LessonRemoteManager.getCollectVoaData(UserInfoManager.getInstance().getUserId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Chapter_collect_show>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    getCollectDataDis = d;
                                }

                                @Override
                                public void onNext(Chapter_collect_show bean) {
                                    if (bean!=null&&bean.result==1){
                                        //保存在辅助数据库中
                                        if (bean.list!=null&&bean.list.size()>0){
                                            for (int i = 0; i < bean.list.size(); i++) {
                                                int saveVoaId = bean.list.get(i).voaid;
                                                long saveCreateTime = LibDateUtil.toDateLong(bean.list.get(i).createDate,LibDateUtil.YMDHMSS);

                                                VoaCollectEntity collectEntity = new VoaCollectEntity(saveVoaId,UserInfoManager.getInstance().getUserId(), saveCreateTime);
                                                HelpDataManager.getInstance().saveSingleVoaCollectData(collectEntity);
                                            }
                                        }

                                        //刷新数据显示
                                        voaList = getData(localType);
                                        voaAdapter.setList(voaList);
                                        voaAdapter.notifyDataSetChanged();
                                    }else {
                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(5);
                                        handler.sendEmptyMessage(7);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    handler.sendEmptyMessage(0);
                                    handler.sendEmptyMessage(4);
                                }

                                @Override
                                public void onComplete() {
                                    LibRxUtil.unDisposable(getCollectDataDis);
                                }
                            });
                    break;
                case 3:
                    final int voaid = msg.arg1;
                    final int typeId = msg.arg2;
                    String type = (typeId == 1) ? "insert" : "del";
                    ExeProtocol.exe(
                            new FavorUpdateRequest(
                                    String.valueOf(UserInfoManager.getInstance().getUserId()), voaid, type),
                            new ProtocolResponse() {
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    voaOp.updateSynchro(voaid, 1);
                                    handler.sendEmptyMessage(7);
                                }

                                @Override
                                public void error() {
                                }
                            });
                    break;
                case 4:
//                    CustomToast.showToast(mContext, R.string.please_check_network, 1000);
                    break;
                case 5:
                    CustomToast.showToast(mContext, R.string.newslist_synchro_success, 1000);
                    break;
                case 6:
                    getTextDetail(voa);
                    break;
                case 7:
                    voaAdapter.notifyDataSetChanged();
                    break;
                case 8:
                    CustomToast.showToast(mContext, "删除成功", 1000);
                    break;
                /*case 9:
                    voaList.clear();
                    voaList.addAll(voaOp.findDataFromCollection());
                    handler.sendEmptyMessage(7);
                    break;*/
                default:
                    break;
            }
        }
    };

    public void setSyschro() {
        if (localType == 1) {
            buttonSyncho.setVisibility(View.VISIBLE);
        } else {
            buttonSyncho.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //这里进行标志处理-临时数据
        StudyContentManager.getInstance().setTempData(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        handler.sendEmptyMessage(0);
    }

    public void getTextDetail(final Voa voa) {
        handler.sendEmptyMessage(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从本地数据库中查找
                VoaDataManager.Instace().voaTemp = voa;
                VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(voa.voaId);
                if (VoaDataManager.Instace().voaDetailsTemp != null
                        && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                    ConfigManager.Instance().putInt("voaIdSave", voa.voaId);
                    ConfigManager.Instance().putString("bg_title_cn", voa.titleCn);
                    ConfigManager.Instance().putString("bg_title_en", voa.title);
                    Log.e("TAG", "voa: "+voa.toString() );
                    VoaDataManager.Instace().setSubtitleSum(voa, VoaDataManager.Instace().voaDetailsTemp);

                    //换成新的跳转操作
                    /*Intent intent = new Intent();
                    intent.setClass(mContext, StudyActivity.class);
                    startActivity(intent);*/
                    StudyActivity.start(mContext,String.valueOf(voa.voaId));

                    EventBus.getDefault().post(new PlayerNextEvent(voa.title, voa.titleCn, voa.lesson, voa.art));
                    overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                    //handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (isDelStart) {
            buttonEdit.setBackgroundResource(R.drawable.button_edit_finished);
            if (voaAdapter != null) {
                isDelStart = true;
                changeItemDeleteStart(true);
            }
        } else {
            finish();
        }
    }

    /********************************其他方法***************************/
    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //循环删除数据
    private int collectRecycleCount = 0;
    private void deleteCollectDataRecycle(){
        //先获取需要删除的数据
        List<Voa> deleteList = new ArrayList<>();
        if (voaList!=null&&voaList.size()>0){
            for (int i = 0; i < voaList.size(); i++) {
                Voa tempVoa = voaList.get(i);
                if (tempVoa.isDelete){
                    deleteList.add(tempVoa);
                }
            }
        }

        if (deleteList.size()>0){
            startLoading("正在删除收藏数据～");

            deleteSingleCollectData(deleteList);
        }else {
            collectRecycleCount = 0;
            handler.sendEmptyMessage(7);
            isDelStart = false;
            buttonEdit.setBackgroundResource(R.drawable.button_edit);
            changeItemDeleteStart(false);
        }
    }

    //删除单个收藏数据
    private void deleteSingleCollectData(List<Voa> deleteList){
        if (collectRecycleCount>=deleteList.size()){
            stopLoading();
            ToastUtil.showToast(this,"收藏数据删除完成");

            //获取数据并刷新显示
            voaList = getData(localType);
            voaAdapter.setList(voaList);
            voaAdapter.notifyDataSetChanged();

            collectRecycleCount = 0;
            isDelStart = false;
            buttonEdit.setBackgroundResource(R.drawable.button_edit);
            changeItemDeleteStart(false);
            return;
        }

        Voa tempVoa = deleteList.get(collectRecycleCount);
        Log.d("当前计数器", collectRecycleCount+"--"+deleteList.size());
        LessonRemoteManager.collectVoaData(UserInfoManager.getInstance().getUserId(), tempVoa.voaId,true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Chapter_collect>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Chapter_collect bean) {
                        if (bean!=null&&bean.result>0){
                            //删除数据
                            HelpDataManager.getInstance().deleteSingleVoaCollectData(UserInfoManager.getInstance().getUserId(), tempVoa.voaId);
                        }
                        //下一个
                        collectRecycleCount+=1;
                        deleteSingleCollectData(deleteList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //下一个
                        collectRecycleCount+=1;
                        deleteSingleCollectData(deleteList);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //删除试听的数据
    private void deleteMultiListenData(){
        List<Voa> listenList = new ArrayList<>();
        if (voaList!=null&&voaList.size()>0){
            for (int i = 0; i < voaList.size(); i++) {
                Voa tempVoa = voaList.get(i);
                if (tempVoa.isRead.equals("1")){
                    listenList.add(tempVoa);
                }
            }
        }

        if (listenList!=null&&listenList.size()>0){
            for (int i = 0; i < listenList.size(); i++) {
                Voa tempVoa = listenList.get(i);
                HelpDataManager.getInstance().deleteSingleVoaListenData(UserInfoManager.getInstance().getUserId(), tempVoa.voaId);
            }

            //刷新数据
            //获取数据并刷新显示
            voaList = getData(localType);
            voaAdapter.setList(voaList);
            voaAdapter.notifyDataSetChanged();
        }else {
            handler.sendEmptyMessage(7);
        }
        isDelStart = false;
        buttonEdit.setBackgroundResource(R.drawable.button_edit);
        changeItemDeleteStart(false);
    }
}
