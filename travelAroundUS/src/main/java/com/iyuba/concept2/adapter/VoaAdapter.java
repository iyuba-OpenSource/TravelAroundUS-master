package com.iyuba.concept2.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.DownloadStateManager;
import com.iyuba.concept2.push.LogUtil;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.mode.VoaTitleBean;
import com.iyuba.concept2.sqlite.op.DownloadInfoOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.concept2.util.FileDownloader;
import com.iyuba.concept2.widget.cdialog.CustomToast;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.lil.model.local.bean.VoaListenEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibPermissionDialogUtil;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表适配器
 */
public class VoaAdapter extends BaseAdapter {
    private FileDownloader fileDownloader;
    private List<DownloadInfo> infoList;
    private Voa voa;
    private Context mContext;
    //private List<Voa> mList = new ArrayList<Voa>();
    private List<VoaTitleBean> voaTitleList = new ArrayList<VoaTitleBean>();
    public ViewHolder currViewHolder;
    public boolean modeDelete = false;
    private ViewHolder viewHolder;
    private VoaOp voaOp;
    private DownloadStateManager manager;
    private DownloadInfoOp downloadInfoOp;
    private Handler handler;
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private int image[];

    public VoaAdapter(Context context, List<VoaTitleBean> list) {
        fileDownloader = FileDownloader.getInstance();//启懂下载线程
        manager = DownloadStateManager.getInstance();
        downloadInfoOp = manager.downloadInfoOp;
        infoList = manager.downloadList;
        mContext = context;
        //mList = list;
        voaTitleList = list;
        this.handler = manager.handler;
        init();


        @SuppressLint("Recycle")
        TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.test_list_icon);

        image = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            image[i] = typedArray.getResourceId(i, 0);
        }
    }

//    public VoaAdapter(Context context) {
//        fileDownloader = FileDownloader.instance();
//        mContext = context;
//        init();
//    }

    public void addList(List<VoaTitleBean> voasTemps) {
        voaTitleList.addAll(voasTemps);
    }

    private void init() {
        voaOp = new VoaOp(mContext);
    }

    public void clearList() {
        voaTitleList.clear();
    }

    public List<VoaTitleBean> getData(){
       return voaTitleList;
    }

    public void reSetData( List<VoaTitleBean> list){
        voaTitleList=list;
    }

    @Override
    public int getCount() {
        return voaTitleList.size();
    }

    @Override
    public VoaTitleBean getItem(int position) {
        return voaTitleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final VoaTitleBean curVoa = voaTitleList.get(position);
        int voaId1 = curVoa.getVoaList().get(0).voaId;
        int voaId2 = curVoa.getVoaList().get(1).voaId;
        int voaId3 = curVoa.getVoaList().get(2).voaId;

        final DownloadInfo info = getDownloadInfo(voaId1);//现在每一条的下载信息应该有3个。。。
        final DownloadInfo info1 = getDownloadInfo(voaId2);//现在每一条的下载信息应该有3个。。。
        final DownloadInfo info2 = getDownloadInfo(voaId3);//现在每一条的下载信息应该有3个。。。

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.listitem_voa, null);

            viewHolder = new ViewHolder();
            viewHolder.voaN = (TextView) convertView
                    .findViewById(R.id.voaN);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.titleCn = (TextView) convertView
                    .findViewById(R.id.titleCn);

            viewHolder.ivRound = convertView.findViewById(R.id.iv_around);

            //播放按钮，下载成功后才显示
            // viewHolder.downloadedImage = (ImageView) convertView.findViewById(R.id.image_downloaded);
            // 下载的滚动条，实际是不动的，下载按钮是背景
            //viewHolder.mLlSelect = (LinearLayout) convertView.findViewById(R.id.ll_course_select);
            viewHolder.mRlItem = convertView.findViewById(R.id.rl_item);
            viewHolder.mTvAct1 = convertView.findViewById(R.id.tv_act1);
            viewHolder.mTvAct2 = convertView.findViewById(R.id.tv_act2);
            viewHolder.mTvAct3 = convertView.findViewById(R.id.tv_act3);
            viewHolder.mTvSummary = convertView.findViewById(R.id.tv_summary);

            viewHolder.mLlArt1 = convertView.findViewById(R.id.ll_art1);
            viewHolder.mLlArt2 = convertView.findViewById(R.id.ll_art2);
            viewHolder.mLlArt3 = convertView.findViewById(R.id.ll_art3);

            viewHolder.mIvStartPlayAct1 = convertView.findViewById(R.id.iv_download_act1);
            viewHolder.mIvStartPlayAct2 = convertView.findViewById(R.id.iv_download_act2);
            viewHolder.mIvStartPlayAct3 = convertView.findViewById(R.id.iv_download_act3);

            viewHolder.mRpbArt1 = convertView.findViewById(R.id.roundBar1_act1);
            viewHolder.mRpbArt2 = convertView.findViewById(R.id.roundBar1_act2);
            viewHolder.mRpbArt3 = convertView.findViewById(R.id.roundBar1_act3);

            viewHolder.mRLDownloadArt1 = convertView.findViewById(R.id.download_layout_act1);
            viewHolder.mRLDownloadArt2 = convertView.findViewById(R.id.download_layout_act2);
            viewHolder.mRLDownloadArt3 = convertView.findViewById(R.id.download_layout_act3);

            //viewHolder.mRpbArt1.setProgress(0);
            //viewHolder.mRpbArt2.setProgress(0);
            //viewHolder.mRpbArt3.setProgress(0);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.voaN.setText(curVoa.getmVoaId() + ".");

        //这里修改下逻辑，根据数据库内容设置下样式(获取当前item的阅读状态，然后设置下)
        /*if (curVoa.isRead){
            viewHolder.voaN.setTextColor(Constant.readColor);
            viewHolder.title.setTextColor(Constant.readColor);
            viewHolder.titleCn.setTextColor(Constant.readColor);

            if (curVoa.getVoaList().get(0).isReadArt) {
                viewHolder.mTvAct1.setTextColor(Constant.readColor);
            }else {
                viewHolder.mTvAct1.setTextColor(Constant.unreadCnColor);
            }

            if (curVoa.getVoaList().get(1).isReadArt) {
                viewHolder.mTvAct2.setTextColor(Constant.readColor);
            }else {
                viewHolder.mTvAct2.setTextColor(Constant.unreadCnColor);
            }

            if (curVoa.getVoaList().get(2).isReadArt) {
                viewHolder.mTvAct3.setTextColor(Constant.readColor);
            }else {
                viewHolder.mTvAct3.setTextColor(Constant.unreadCnColor);
            }

        }else {
            viewHolder.voaN.setTextColor(Constant.unreadCnColor);
            viewHolder.title.setTextColor(Constant.unreadCnColor);
            viewHolder.titleCn.setTextColor(Constant.unreadCnColor);
            viewHolder.mTvAct1.setTextColor(Constant.unreadCnColor);
            viewHolder.mTvAct2.setTextColor(Constant.unreadCnColor);
            viewHolder.mTvAct3.setTextColor(Constant.unreadCnColor);
        }*/
        VoaListenEntity listenEntityOne = HelpDataManager.getInstance().getSingleVoaListenData(UserInfoManager.getInstance().getUserId(), curVoa.getVoaList().get(0).voaId);
        VoaListenEntity listenEntityTwo = HelpDataManager.getInstance().getSingleVoaListenData(UserInfoManager.getInstance().getUserId(), curVoa.getVoaList().get(1).voaId);
        VoaListenEntity listenEntityThree = HelpDataManager.getInstance().getSingleVoaListenData(UserInfoManager.getInstance().getUserId(), curVoa.getVoaList().get(2).voaId);

        if (listenEntityOne!=null||listenEntityTwo!=null||listenEntityThree!=null){
            viewHolder.voaN.setTextColor(Constant.readColor);
            viewHolder.title.setTextColor(Constant.readColor);
            viewHolder.titleCn.setTextColor(Constant.readColor);

            if (listenEntityOne!=null){
                viewHolder.mTvAct1.setTextColor(Constant.readColor);
            }else {
                viewHolder.mTvAct1.setTextColor(Constant.unreadCnColor);
            }

            if (listenEntityTwo!=null){
                viewHolder.mTvAct2.setTextColor(Constant.readColor);
            }else {
                viewHolder.mTvAct2.setTextColor(Constant.unreadCnColor);
            }

            if (listenEntityThree!=null){
                viewHolder.mTvAct3.setTextColor(Constant.readColor);
            }else {
                viewHolder.mTvAct3.setTextColor(Constant.unreadCnColor);
            }
        }else {
            viewHolder.voaN.setTextColor(Constant.unreadCnColor);
            viewHolder.title.setTextColor(Constant.unreadCnColor);
            viewHolder.titleCn.setTextColor(Constant.unreadCnColor);
            viewHolder.mTvAct1.setTextColor(Constant.unreadCnColor);
            viewHolder.mTvAct2.setTextColor(Constant.unreadCnColor);
            viewHolder.mTvAct3.setTextColor(Constant.unreadCnColor);
        }

        String enTitle = curVoa.getmTitleEn().replace("ACT I", "");
        enTitle = enTitle.replace("ACT II", "");
        enTitle = enTitle.replace("ACT III", "");
        if (position < 10) {
            enTitle = enTitle.substring(4);
        } else {
            enTitle = enTitle.substring(5);
        }

        if (position < image.length) {
            viewHolder.ivRound.setImageResource(image[position]);
        } else {
            viewHolder.ivRound.setImageResource(image[position % 7]);
        }

        viewHolder.title.setText(enTitle);
        viewHolder.titleCn.setText(curVoa.getmTitleCn().substring(0, curVoa.getmTitleCn().length() - 6));

        //根据文件是否存在改变状态
        checkDownload(curVoa,0,viewHolder.mIvStartPlayAct1);
        checkDownload(curVoa,1,viewHolder.mIvStartPlayAct2);
        checkDownload(curVoa,2,viewHolder.mIvStartPlayAct3);

        setLoadingUI(info, viewHolder.mRpbArt1);
        setLoadingUI(info1, viewHolder.mRpbArt2);
        setLoadingUI(info2, viewHolder.mRpbArt3);

        //如果是正在下载中或将要下载才走这个方法
//        if (curVoa.getVoaList().get(0).isDownload.equals("1")) {
//            downloadUI(info, viewHolder.mIvStartPlayAct1);
//        }
//        if (curVoa.getVoaList().get(1).isDownload.equals("1")) {
//            downloadUI(info1, viewHolder.mIvStartPlayAct2);
//        }
//        if (curVoa.getVoaList().get(2).isDownload.equals("1")) {
//            downloadUI(info2, viewHolder.mIvStartPlayAct3);
//        }

        viewHolder.mRlItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voaTitleList.get(position).isSelect()) {
                    voaTitleList.get(position).setSelect(false);
                } else {
                    for (int i = 0; i < voaTitleList.size(); i++) {
                        if (voaTitleList.get(i).isSelect()) {
                            voaTitleList.get(i).setSelect(false);
                        }
                    }
                    voaTitleList.get(position).setSelect(true);
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.mLlArt1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onActClick(position, 0, curVoa.getVoaList().get(0));
                }

                setFalse();
                curVoa.setRead(true);

                curVoa.getVoaList().get(0).isReadArt=true;
                curVoa.getVoaList().get(0).setReadArt(true);
            }
        });
        viewHolder.mLlArt2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onActClick(position, 1, curVoa.getVoaList().get(1));
                }
                setFalse();
                curVoa.setRead(true);
                curVoa.getVoaList().get(1).isReadArt=true;
                curVoa.getVoaList().get(1).setReadArt(true);
            }
        });
        viewHolder.mLlArt3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onActClick(position, 2, curVoa.getVoaList().get(2));
                }
                setFalse();
                curVoa.setRead(true);
                curVoa.getVoaList().get(2).isReadArt=true;
                curVoa.getVoaList().get(2).setReadArt(true);
            }
        });
        viewHolder.mTvSummary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onSummaryClick(position);
                }
            }
        });

        //文件下载操作
        viewHolder.mRLDownloadArt1.setOnClickListener(v->{
            showPermissionShowDialog(position,0);
        });
        viewHolder.mRLDownloadArt2.setOnClickListener(v -> {
            showPermissionShowDialog(position,1);
        });
        viewHolder.mRLDownloadArt3.setOnClickListener(v -> {
            showPermissionShowDialog(position,2);
        });
        currViewHolder = viewHolder;
        return convertView;
    }

    private void setDownload(VoaTitleBean curVoa,int num,int position){
        setFalse();
        curVoa.setRead(true);
        curVoa.getVoaList().get(num).isReadArt=true;
        curVoa.getVoaList().get(num).setReadArt(true);
    }

    private void setFalse(){
        for (VoaTitleBean bean : voaTitleList) {
            bean.setRead(false);
            for (Voa voa:bean.getVoaList()){
                voa.isReadArt = false;
            }
        }
    }
    private void checkDownload(VoaTitleBean curVoa,int i,ImageView view){
        // 如果本地存在,删除的不彻底
        File file=new File(ConfigManager.Instance().loadString("media_saving_path")
                + "/" + curVoa.getVoaList().get(i).voaId + Constant.append);
        File file1= new File(Constant.videoAddr + curVoa.getVoaList().get(i).voaId
                + Constant.append);
        if (file.exists() ||file1.exists()) {
            view.setImageResource(R.drawable.ic_download_over);
        } else {
            view.setImageResource(R.drawable.ic_download_blue);
        }
    }

    private void downloadUI(DownloadInfo info,ImageView view){
        if (info != null) {
            switch (info.downloadedState) {
                case -2:
                    view.setImageResource(R.drawable.wait_download);
                    break;
                case -1:
                    view.setImageResource(R.drawable.ic_download_blue);
                    break;
                case 0:
                    view.setImageResource(R.drawable.ic_download_blue);
                    break;
                case 1:
                    view.setImageResource(R.drawable.ic_download_loading);//ic_downloading
                    break;
                case 2:
                    view.setImageResource(R.drawable.ic_download_over);//ic_start_playe
                    break;
            }
        } else {
            view.setImageResource(R.drawable.ic_download_blue);
        }
    }

    private void setLoadingUI(DownloadInfo info,RoundProgressBar bar){
        // 如果下载列表中存在
        if (info != null && info.downloadedState == 1) {
            // 画进度条
            bar.setCricleProgressColor(0xff00AEFF);
            bar.setProgress(info.downloadPer);
            bar.setMax(100);
        } else {
            bar.setCricleProgressColor(0xff00AEFF);
            bar.setProgress(0);
            bar.setMax(100);
        }
    }

    private void getPermissions() {
        notifyDataSetChanged();
        //使用前必须开启-存储权限--
        if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
            if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                CustomToast.showToast(mContext, "存储权限开通才可以正常下载，请到系统设置中开启", 3000);
                return;
            }
        }
    }

    private void ShowVipDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.alert))
                .setMessage(mContext.getResources().getString(R.string.nladapter_notvip))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(mContext.getResources().getString(R.string.alert_btn_buy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (UserInfoManager.getInstance().isLogin()) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, VipCenterGoldActivity.class);
                            intent.putExtra("username", UserInfoManager.getInstance().getUserName());
                            mContext.startActivity(intent);
                        } else {
                            LoginUtil.startToLogin(mContext);
                        }
                    }
                })
                .setNegativeButton(mContext.getResources().getString(R.string.alert_btn_cancel),null)
                .setCancelable(false)
                .create().show();
    }

    public int getDownloadNum(/*int bookId*/) {
        //int bookIndex = bookId / 1000;
        int downloadNum = 0;
        for (DownloadInfo info : infoList) {
            if (info.downloadedState != 0) {//		//downloadList=null;
                downloadNum++;
            }
        }

        return downloadNum;
    }

    public DownloadInfo getDownloadInfo(int voaId) {
        if (infoList != null) {
            for (DownloadInfo tempInfo : infoList) {
                if (tempInfo.voaId == voaId) {
                    return tempInfo;
                }
            }
        }

        return null;
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();//null
        }

        return false;
    }

    public void download(Voa voa) {
        handler.sendEmptyMessage(1);

        voa.isDownload = "1";
        //添加下载
        voaOp.insertDataToDownload(voa.voaId);

        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void download(DownloadInfo downloadInfo, Voa voa) {
        voa.isDownload = "1";
        voaOp.insertDataToDownload(voa.voaId);//修改本地数据库中状态为已下载

        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);//插入下载信息
        fileDownloader.updateInfoList(downloadInfo);
    }

    public DownloadInfo createDownloadInfo() {
        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        downloadInfoOp.insert(downloadInfo);
        return downloadInfo;
    }

    public class ViewHolder {
        public TextView voaN;
        public TextView title;
        public TextView titleCn;
        public ImageView ivRound;
        public RelativeLayout mRlItem;//课程列表视图
        public TextView mTvAct1;
        public TextView mTvAct2;
        public TextView mTvAct3;
        public TextView mTvSummary;

        public ImageView mIvStartPlayAct1, mIvStartPlayAct2, mIvStartPlayAct3;//开始播放按钮，下载完成的标示
        public RelativeLayout mRLDownloadArt1, mRLDownloadArt2, mRLDownloadArt3;//下载选择的布局
        public RoundProgressBar mRpbArt1, mRpbArt2, mRpbArt3; //进度环

        public RelativeLayout mLlArt1, mLlArt2, mLlArt3;
    }

    public List<VoaTitleBean> getList() {
        return voaTitleList;
    }

    public void setList(List<VoaTitleBean> mList) {
        this.voaTitleList = mList;
    }

    /**
     * item点击事件需要的方法
     */
    public void itemSetOnclick(OnItemButtonClickListener onItemButtonClickListener) {
        this.mOnItemButtonClickListener = onItemButtonClickListener;
    }

    /**
     * 按钮点击事件对应的接口
     */
    public interface OnItemButtonClickListener {
        //课程点击事件
        void onActClick(int position, int art, Voa voa);
        //总结点击事件
        void onSummaryClick(int position);
    }

    //显示权限说明弹窗
    private void showPermissionShowDialog(int position,int downloadIndex){
        List<Pair<String,Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","用于保存下载的文件")));

        LibPermissionDialogUtil.getInstance().showMsgDialog(mContext, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
            @Override
            public void onGranted(boolean isSuccess) {
                if (isSuccess){
                    downloadFile(position, downloadIndex);
                }
            }
        });
    }

    //下载文件
    private void downloadFile(int position,int downloadIndex){
        VoaTitleBean curVoaTitleData = voaTitleList.get(position);
        //当前课程
        Voa curVoa = curVoaTitleData.getVoaList().get(downloadIndex);
        //下载信息
        DownloadInfo info = getDownloadInfo(curVoa.voaId);
        //获取路径
        File file=new File(Constant.videoAddr + curVoa.voaId + Constant.append);
        if (!file.exists()) {//如果文件不存在，进行现在操作
            if (!isOpenNetwork()) {
                // 请检查网络
                CustomToast.showToast(mContext, R.string.category_check_network, 1000);
            } else {
                int downloadNum = getDownloadNum();//voa.voaId / 1000 * 1000

                if (UserInfoManager.getInstance().isVip() || downloadNum < 10) {
                    if (info == null) {//文件下载状态的控制
                        download(curVoa);
                    } else if (info.downloadedState == 0) {
                        download(info, curVoa);
                    } else if (info.downloadedState == -1) {
                        download(info, curVoa);
                    } else if (info.downloadedState == 1 || info.downloadedState == -2) {
                        info.downloadedState = -1;
                        notifyDataSetChanged();
                    }
                } else {
                    ShowVipDialog();
                }
            }
        } else {
            if (info != null){
                info.downloadedState = 2;
            }
            voaOp.insertDataToDownload(curVoa.voaId);
            //setDownload(curVoa,0,position);
            notifyDataSetChanged(); //视图复用了！
            ToastUtil.showToast(mContext,"已经下载！");
            LogUtil.e("已经下载："+curVoa.voaId + Constant.append);
        }
    }
}
