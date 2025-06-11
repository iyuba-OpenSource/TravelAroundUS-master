package com.iyuba.concept2.listener;

public interface DownLoadFailCallBack {
	public void downLoadSuccess(String localFilPath);

	public void downLoadFaild(String errorInfo);
}
