package com.iyuba.concept2.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.iyuba.concept2.listener.DownLoadCallBack;
import com.iyuba.concept2.util.ImageLoder;
import com.iyuba.concept2.util.SDCard;

public class DownLoadManager {
	private static Context mContext;
	private static DownLoadManager instance;
	private ExecutorService pool;

	private DownLoadManager() {
		pool = Executors.newFixedThreadPool(1); // 创建线程池并设定池中线程数量
	};

	public static synchronized DownLoadManager Instace(Context context) {
		mContext = context;
		if (instance == null) {
			instance = new DownLoadManager();
		}
		return instance;
	}

	/**
	 * 下载图片，独立线程
	 * 
	 * @param urlPath
	 * @param downloadCallBack
	 */
	public void downLoadImage(final String urlPath, final String reName,
			final DownLoadCallBack downloadCallBack, final boolean thumbnail) {
		if (SDCard.hasSDCard()) {
			Thread downLoadT = new Thread() {
				@Override
				public void run() {
					super.run();
					Bitmap image = ImageLoder.Instance().getBitmap(urlPath);
					if (downloadCallBack != null) {
						downloadCallBack.downLoadSuccess(image);
					}
				}

			};
			pool.execute(downLoadT);
		} else {
			Toast.makeText(mContext, "SD卡不存在！", Toast.LENGTH_SHORT).show();
		}
	}

}
