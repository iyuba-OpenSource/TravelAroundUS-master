package com.iyuba.concept2.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.DownloadStateManager;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.op.DownloadInfoOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.concept2.util.FileDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表适配器
 */
public class VoaLocalAdapter extends BaseAdapter {
    private FileDownloader fileDownloader;
    private List<DownloadInfo> infoList;
    private Voa voa;
    private Context mContext;
    //private List<Voa> mList = new ArrayList<Voa>();
    private List<Voa> voaTitleList = new ArrayList<Voa>();
    public ViewHolder currViewHolder;
    public boolean modeDelete = false;
    private ViewHolder viewHolder;
    private VoaOp voaOp;
    private DownloadStateManager manager;
    private DownloadInfoOp downloadInfoOp;
    private Handler handler;

    public VoaLocalAdapter(Context context, List<Voa> list) {
        fileDownloader = FileDownloader.getInstance();
        manager = DownloadStateManager.getInstance();
        downloadInfoOp = manager.downloadInfoOp;
        infoList = manager.downloadList;
        mContext = context;
        //mList = list;
        voaTitleList = list;
        this.handler = manager.handler;
        init();
    }

    public VoaLocalAdapter(Context context) {
        fileDownloader = FileDownloader.getInstance();
        mContext = context;
        init();
    }

    public void addList(List<Voa> voasTemps) {
        voaTitleList.addAll(voasTemps);
    }

    private void init() {
        voaOp = new VoaOp(mContext);
    }

    public void clearList() {
        voaTitleList.clear();
    }

    @Override
    public int getCount() {
        return voaTitleList.size();
    }

    @Override
    public Voa getItem(int position) {
        return voaTitleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Voa curVoa = voaTitleList.get(position);
        int voaId1 = curVoa.voaId;

        final DownloadInfo info = getDownloadInfo(voaId1);//现在每一条的下载信息应该有3个。。。

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.listitem_local_voa, null);

            viewHolder = new ViewHolder();
            viewHolder.deleteBox = (ImageView) convertView
                    .findViewById(R.id.checkBox_is_delete);
            viewHolder.voa = (TextView) convertView
                    .findViewById(R.id.voa);
            viewHolder.voaN = (TextView) convertView
                    .findViewById(R.id.voaN);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.titleCn = (TextView) convertView
                    .findViewById(R.id.titleCn);

            //播放按钮，下载成功后才显示
//            viewHolder.downloadedImage = (ImageView) convertView
//                    .findViewById(R.id.image_downloaded);
            // 下载的滚动条，实际是不动的，下载按钮是背景
            viewHolder.mRlItem = convertView.findViewById(R.id.rl_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (modeDelete) {
            viewHolder.deleteBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.deleteBox.setVisibility(View.GONE);
        }

        if (voaTitleList.get(position).isDelete) {
            viewHolder.deleteBox.setImageResource(R.drawable.check_box_checked);
        } else {
            viewHolder.deleteBox.setImageResource(R.drawable.check_box);
        }
        final int voaId = curVoa.voaId;
        int index = curVoa.voaId % 1000;

        viewHolder.voa.setText("EPISODE");
        viewHolder.voaN.setText(curVoa.lesson + "");

        viewHolder.title.setText(curVoa.title);
        viewHolder.titleCn.setText(curVoa.titleCn);

//        viewHolder.mRlItem.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (voaTitleList.get(position).isSelect()) {
//                    voaTitleList.get(position).setSelect(false);
//                } else {
//                    for (int i = 0; i < voaTitleList.size(); i++) {
//                        if (voaTitleList.get(i).isSelect()) {
//                            voaTitleList.get(i).setSelect(false);
//                        }
//                    }
//                    voaTitleList.get(position).setSelect(true);
//                }
//                notifyDataSetChanged();
//            }
//        });
        currViewHolder = viewHolder;
        return convertView;
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
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }

    public void download() {
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

    public void download(DownloadInfo downloadInfo) {
        voa.isDownload = "1";
        voaOp.insertDataToDownload(voa.voaId);

        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public DownloadInfo createDownloadInfo() {
        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        downloadInfoOp.insert(downloadInfo);

        return downloadInfo;
    }

    public class ViewHolder {
        public ImageView deleteBox;
        public TextView voa;
        public TextView voaN;
        public TextView title;
        public TextView titleCn;
        //View downloadLayout;
       // public LinearLayout mLlSelect;//课程选择布局
        public RelativeLayout mRlItem;//课程列表视图
        //RoundProgressBar mCircleProgressBar;// 进度环
        //ImageView downloadedImage;// 下载完成的标示
    }

    public List<Voa> getList() {
        return voaTitleList;
    }

    public void setList(List<Voa> mList) {
        this.voaTitleList = mList;
    }

}
