package com.iyuba.concept2.sqlite.mode;

import com.iyuba.concept2.R;

/** 
 * 
 * 文件浏览器相关类
 * 
 */
public class FileInfo {
	public String Name;
	public String Path;
	public long Size;
	public boolean IsDirectory = false;
	public int FileCount = 0;
	public int FolderCount = 0;

	public int getIconResourceId() {
		if (IsDirectory) {
			return R.drawable.folder;
		}
		return R.drawable.doc;
	}
}