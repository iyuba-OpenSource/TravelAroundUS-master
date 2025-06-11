package com.iyuba.concept2.util;

import java.util.Comparator;

import com.iyuba.concept2.sqlite.mode.FileInfo;

/**
 * 
 * 文件浏览器相关类
 */
public class FileComparator implements Comparator<FileInfo> {

	public int compare(FileInfo file1, FileInfo file2) {
		if (file1.IsDirectory && !file2.IsDirectory) {
			return -1000;
		} else if (!file1.IsDirectory && file2.IsDirectory) {
			return 1000;
		}
		return file1.Name.compareTo(file2.Name);
	}
}