package com.iyuba.concept2.sqlite.mode;

import com.iyuba.configation.Constant;

public class DownloadInfo {
	public int voaId;
	public String url;
	public long downloadedBytes;
	public int downloadedState;
	public long totalBytes;
	public int downloadPer;
	public String savePath;

	public DownloadInfo() {
		super();
	}

	public DownloadInfo(int voaId) {
		super();
		this.voaId = voaId;
		//this.url = (voaId / 1000) + "_"+ (voaId % 1000) + Constant.append;
		this.url = voaId+ Constant.append;
		this.downloadedBytes = 0;
		this.downloadedState = -2;
	}
}
