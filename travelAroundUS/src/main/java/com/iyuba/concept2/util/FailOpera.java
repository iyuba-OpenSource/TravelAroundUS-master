package com.iyuba.concept2.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

/**
 * 文件打开失败
 * 
 * @author chentong
 * 
 */
public class FailOpera {
	private static FailOpera instance;
	private static Context mContext;

	private FailOpera() {
	}

	public static synchronized FailOpera Instace(Context context) {
		mContext = context;
		if (instance == null) {
			instance = new FailOpera();
		}
		return instance;
	}

	public void openFile(String filePath) {
		File appFile = new File(filePath);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 或者
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			String type = FileUtil.getMIMEType(appFile.getName());
			intent.setDataAndType(FileProvider.getUriForFile(mContext, mContext.getPackageName(), appFile), type);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(filePath)),
					"application/vnd.android.package-archive");
		}
		mContext.startActivity(intent);
	}
}
