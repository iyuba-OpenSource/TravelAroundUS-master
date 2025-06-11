package com.iyuba.concept2.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.databinding.ItemVoaListBinding;
import com.iyuba.concept2.databinding.ListitemVoaBinding;
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

public class NewVoaAdapter extends RecyclerView.Adapter<NewVoaAdapter.NewVoaHolder> {

    private Context context;
    private List<VoaTitleBean> list;

    //下载信息集合
    private List<DownloadInfo> infoList;
    //头像图片数组
    private List<Integer> picImageList;
    //下载功能数据库
    private DownloadInfoOp downloadInfoOp;
    //下载管理类
    private FileDownloader fileDownloader;
    //下载的线程
    private Handler fileHandler;
    //选中的样式
    private NewVoaHolder selectHolder;
    //课程的数据库
    private VoaOp voaOp;

    public NewVoaAdapter(Context context, List<VoaTitleBean> list) {
        this.context = context;
        this.list = list;

        //设置课程数据库
        voaOp = new VoaOp(context);

        //设置下载信息集合
        fileDownloader = FileDownloader.getInstance();
        downloadInfoOp = DownloadStateManager.getInstance().downloadInfoOp;
        infoList = DownloadStateManager.getInstance().downloadList;
        fileHandler = DownloadStateManager.getInstance().handler;

        //设置头像数组
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.test_list_icon);
        picImageList = new ArrayList<>();
        for (int i = 0; i < typedArray.length(); i++) {
            picImageList.add(typedArray.getResourceId(i,0));
        }
    }

    @NonNull
    @Override
    public NewVoaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoaListBinding binding = ItemVoaListBinding.inflate(LayoutInflater.from(context),parent,false);
        return new NewVoaHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewVoaHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        VoaTitleBean titleBean = list.get(position);
        //下载信息
        DownloadInfo downInfo1 = getDownloadInfo(titleBean.getVoaList().get(0).voaId);
        DownloadInfo downInfo2 = getDownloadInfo(titleBean.getVoaList().get(1).voaId);
        DownloadInfo downInfo3 = getDownloadInfo(titleBean.getVoaList().get(2).voaId);

        holder.index.setText(String.valueOf(titleBean.getmVoaId()));

        //显示的播放信息
        VoaListenEntity listenEntityOne = HelpDataManager.getInstance().getSingleVoaListenData(UserInfoManager.getInstance().getUserId(), titleBean.getVoaList().get(0).voaId);
        VoaListenEntity listenEntityTwo = HelpDataManager.getInstance().getSingleVoaListenData(UserInfoManager.getInstance().getUserId(), titleBean.getVoaList().get(1).voaId);
        VoaListenEntity listenEntityThree = HelpDataManager.getInstance().getSingleVoaListenData(UserInfoManager.getInstance().getUserId(), titleBean.getVoaList().get(2).voaId);

        if (listenEntityOne!=null||listenEntityTwo!=null||listenEntityThree!=null){
            holder.index.setTextColor(Constant.readColor);
            holder.title.setTextColor(Constant.readColor);
            holder.titleCn.setTextColor(Constant.readColor);

            if (listenEntityOne!=null){
                holder.tvAct1.setTextColor(Constant.readColor);
            }else {
                holder.tvAct1.setTextColor(Constant.unreadCnColor);
            }

            if (listenEntityTwo!=null){
                holder.tvAct2.setTextColor(Constant.readColor);
            }else {
                holder.tvAct2.setTextColor(Constant.unreadCnColor);
            }

            if (listenEntityThree!=null){
                holder.tvAct3.setTextColor(Constant.readColor);
            }else {
                holder.tvAct3.setTextColor(Constant.unreadCnColor);
            }
        }else {
            holder.index.setTextColor(Constant.unreadCnColor);
            holder.title.setTextColor(Constant.unreadCnColor);
            holder.titleCn.setTextColor(Constant.unreadCnColor);
            holder.tvAct1.setTextColor(Constant.unreadCnColor);
            holder.tvAct2.setTextColor(Constant.unreadCnColor);
            holder.tvAct3.setTextColor(Constant.unreadCnColor);
        }

        String enTitle = titleBean.getmTitleEn().replace("ACT I", "");
        enTitle = enTitle.replace("ACT II", "");
        enTitle = enTitle.replace("ACT III", "");
        if (position < 10) {
            enTitle = enTitle.substring(4);
        } else {
            enTitle = enTitle.substring(5);
        }

        if (position < picImageList.size()) {
            holder.pic.setImageResource(picImageList.get(position));
        } else {
            holder.pic.setImageResource(picImageList.get(position % 7));
        }

        holder.title.setText(enTitle);
        holder.titleCn.setText(titleBean.getmTitleCn().substring(0, titleBean.getmTitleCn().length() - 6));

        //根据文件是否存在改变状态
        checkDownload(titleBean,0,holder.ivAct1);
        checkDownload(titleBean,1,holder.ivAct2);
        checkDownload(titleBean,2,holder.ivAct3);

        setLoadingUI(downInfo1, holder.rpAct1);
        setLoadingUI(downInfo2, holder.rpAct2);
        setLoadingUI(downInfo3, holder.rpAct3);

        //设置点击操作
        holder.itemLayout.setOnClickListener(v -> {
            if (list.get(position).isSelect()) {
                list.get(position).setSelect(false);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelect()) {
                        list.get(i).setSelect(false);
                    }
                }
                list.get(position).setSelect(true);
            }
            notifyDataSetChanged();
        });
        holder.rlAct1.setOnClickListener(v -> {
            if (onItemButtonClickListener != null) {
                onItemButtonClickListener.onActClick(position, 0, titleBean.getVoaList().get(0));
            }

            setFalse();
            titleBean.setRead(true);
            titleBean.getVoaList().get(0).isReadArt=true;
            titleBean.getVoaList().get(0).setReadArt(true);
        });
        holder.rlAct2.setOnClickListener(v -> {
            if (onItemButtonClickListener != null) {
                onItemButtonClickListener.onActClick(position, 1, titleBean.getVoaList().get(1));
            }

            setFalse();
            titleBean.setRead(true);
            titleBean.getVoaList().get(1).isReadArt=true;
            titleBean.getVoaList().get(1).setReadArt(true);
        });
        holder.rlAct3.setOnClickListener(v -> {
            if (onItemButtonClickListener != null) {
                onItemButtonClickListener.onActClick(position, 2, titleBean.getVoaList().get(2));
            }

            setFalse();
            titleBean.setRead(true);
            titleBean.getVoaList().get(2).isReadArt=true;
            titleBean.getVoaList().get(2).setReadArt(true);
        });
        holder.summary.setOnClickListener(v -> {
            if (onItemButtonClickListener != null) {
                onItemButtonClickListener.onSummaryClick(position);
            }
        });

        //文件下载操作
        holder.rlDownloadAct1.setOnClickListener(v->{
            showPermissionShowDialog(position,0);
        });
        holder.rlDownloadAct2.setOnClickListener(v -> {
            showPermissionShowDialog(position,1);
        });
        holder.rlDownloadAct3.setOnClickListener(v -> {
            showPermissionShowDialog(position,2);
        });
        selectHolder = holder;
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NewVoaHolder extends RecyclerView.ViewHolder{
        private RelativeLayout itemLayout;

        private ImageView pic;
        private TextView index;
        private TextView title;
        private TextView titleCn;
        public TextView summary;

        private RelativeLayout rlAct1;
        private TextView tvAct1;
        private RelativeLayout rlDownloadAct1;
        private ImageView ivAct1;
        private RoundProgressBar rpAct1;

        private RelativeLayout rlAct2;
        private TextView tvAct2;
        private RelativeLayout rlDownloadAct2;
        private ImageView ivAct2;
        private RoundProgressBar rpAct2;

        private RelativeLayout rlAct3;
        private TextView tvAct3;
        private RelativeLayout rlDownloadAct3;
        private ImageView ivAct3;
        private RoundProgressBar rpAct3;

        public NewVoaHolder(ItemVoaListBinding binding){
            super(binding.getRoot());

            itemLayout = binding.itemLayout;

            pic = binding.ivPic;
            index = binding.tvIndex;
            title = binding.tvTitle;
            titleCn = binding.tvTitleCn;
            summary = binding.tvSummary;

            rlAct1 = binding.rlAct1;
            tvAct1 = binding.tvAct1;
            rlDownloadAct1 = binding.rlDownloadAct1;
            ivAct1 = binding.ivDownloadAct1;
            rpAct1 = binding.rpDownloadAct1;

            rlAct2 = binding.rlAct2;
            tvAct2 = binding.tvAct2;
            rlDownloadAct2 = binding.rlDownloadAct2;
            ivAct2 = binding.ivDownloadAct2;
            rpAct2 = binding.rpDownloadAct2;

            rlAct3 = binding.rlAct3;
            tvAct3 = binding.tvAct3;
            rlDownloadAct3 = binding.rlDownloadAct3;
            ivAct3 = binding.ivDownloadAct3;
            rpAct3 = binding.rpDownloadAct3;
        }
    }

    /***********************************方法********************************/
    //获取下载信息
    private DownloadInfo getDownloadInfo(int voaId) {
        if (infoList != null) {
            for (DownloadInfo tempInfo : infoList) {
                if (tempInfo.voaId == voaId) {
                    return tempInfo;
                }
            }
        }
        return null;
    }

    //检查下载
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

    //设置加载样式
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

    //显示权限说明弹窗
    private void showPermissionShowDialog(int position,int downloadIndex){
        List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","用于保存下载的文件")));

        LibPermissionDialogUtil.getInstance().showMsgDialog(context, pairList, new LibPermissionDialogUtil.OnPermissionResultListener() {
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
        VoaTitleBean curVoaTitleData = list.get(position);
        //当前课程
        Voa curVoa = curVoaTitleData.getVoaList().get(downloadIndex);
        //下载信息
        DownloadInfo info = getDownloadInfo(curVoa.voaId);
        //获取路径
        File file=new File(Constant.videoAddr + curVoa.voaId + Constant.append);
        if (!file.exists()) {//如果文件不存在，进行现在操作
            if (!isOpenNetwork()) {
                // 请检查网络
                CustomToast.showToast(context, R.string.category_check_network, 1000);
            } else {
                int downloadNum = getDownloadNum();//voa.voaId / 1000 * 1000

                if (UserInfoManager.getInstance().isVip() || downloadNum < 10) {
                    if (info == null) {//文件下载状态的控制
                        setDownloadInfo(curVoa);
                    } else if (info.downloadedState == 0) {
                        setDownloadInfo(info, curVoa);
                    } else if (info.downloadedState == -1) {
                        setDownloadInfo(info, curVoa);
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
            ToastUtil.showToast(context,"已经下载！");
        }
    }

    //设置下载信息
    public void setDownloadInfo(Voa voa) {
        fileHandler.sendEmptyMessage(1);

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

    //设置下载信息
    public void setDownloadInfo(DownloadInfo downloadInfo, Voa voa) {
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

    //是否开启了网络
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();//null
        }

        return false;
    }

    //显示vip弹窗
    private void ShowVipDialog() {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert))
                .setMessage(context.getResources().getString(R.string.nladapter_notvip))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(context.getResources().getString(R.string.alert_btn_buy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (UserInfoManager.getInstance().isLogin()) {
                            Intent intent = new Intent();
                            intent.setClass(context, VipCenterGoldActivity.class);
                            intent.putExtra("username", UserInfoManager.getInstance().getUserName());
                            context.startActivity(intent);
                        } else {
                            LoginUtil.startToLogin(context);
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.alert_btn_cancel),null)
                .setCancelable(false)
                .create().show();
    }

    //获取下载的次数
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

    //设置已读为false
    private void setFalse(){
        for (VoaTitleBean bean : list) {
            bean.setRead(false);
            for (Voa voa:bean.getVoaList()){
                voa.isReadArt = false;
            }
        }
    }

    //刷新数据
    public void refreshData(List<VoaTitleBean> rfereshList){
        this.list = rfereshList;
        notifyDataSetChanged();
    }

    //获取当前的集合数据
    public List<VoaTitleBean> getData(){
        return  list;
    }

    /**********************************接口*********************************/
    //按钮点击事件对应的接口
    private OnItemButtonClickListener onItemButtonClickListener;

    public interface OnItemButtonClickListener {
        //课程点击事件
        void onActClick(int position, int art, Voa voa);
        //总结点击事件
        void onSummaryClick(int position);
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener onItemButtonClickListener) {
        this.onItemButtonClickListener = onItemButtonClickListener;
    }
}
