package com.iyuba.concept2.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.iyuba.concept2.sqlite.mode.Book;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;
import com.iyuba.concept2.sqlite.op.BookOp;
import com.iyuba.concept2.sqlite.op.DownloadInfoOp;
import com.iyuba.concept2.util.ClearBuffer;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.widget.dialog.CustomToast;

import java.io.File;
import java.util.List;

// 下载状态管理
public class DownloadStateManager {
	private Context mContext;
	public DownloadInfoOp downloadInfoOp;
	public BookOp bookOp;
	
	public List<DownloadInfo> downloadList;
	public List<Book> bookList;
	
	public DownloadStateManager() {
		mContext = RuntimeManager.getContext();
		downloadInfoOp = new DownloadInfoOp(mContext);
		bookOp = new BookOp(mContext);
		downloadList=downloadInfoOp.query();
		bookList = bookOp.findData();  //书的状态牵扯太多，想办法去除
		//bookList=new ArrayList<Book>();
	}

	private static DownloadStateManager instance;
	public static DownloadStateManager getInstance() {
		if(instance == null) {
			synchronized (DownloadStateManager.class){
				if (instance==null){
					instance = new DownloadStateManager();
				}
			}
		}
		
		return instance;
	}
	
	public void updateDownloadInfo(DownloadInfo info) {
		downloadInfoOp.update(info);
	}

	//更新书下载数据
	public void updateBook(int bookId) {
		bookOp.updateDownloadNum(bookId);
		bookList.get(bookId);//bookId / 1000 - 1
	}
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				CustomToast.showToast(RuntimeManager.getContext(), "网络问题或服务器端错误", 2000);
				break;
			case 1:
				CustomToast.showToast(mContext, "正在下载", 2000);
				break;
			case 2:
				int voaId = msg.arg1;
				CustomToast.showToast(mContext, "第" + (voaId) + "课下载完成", 2000);
				break;
			default:
				break;
			}
		}
	};
	
	public void delete(int voaId) {
		for(DownloadInfo info : downloadList) {
			if(info.voaId == voaId) {
				downloadInfoOp.delete(voaId);
				info.downloadedState = 0;
				info.downloadPer = 0;
				info.downloadedBytes = 0;
				new ClearBuffer("audio/temp_audio_"
						+ info.voaId
						+ Constant.append).Delete();
				File file=new File(ConfigManager.Instance().loadString("media_saving_path")
						+ "/" + voaId
						+ Constant.append);
				if (file.isFile()) {
					file.delete();
				}
				break;
			}
		}
	}
}
