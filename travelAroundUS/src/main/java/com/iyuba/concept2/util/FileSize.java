package com.iyuba.concept2.util;

import java.io.File;
import java.text.DecimalFormat;
import java.io.FileInputStream;

public class FileSize {

	private static FileSize instance;

	public FileSize() {
	}

	public static FileSize getInstance() {
		if (instance == null) {
			instance = new FileSize();
		}
		return instance;
	}
	public String getFormatFileSize(File f){
		try {
			return FormetFileSize(getFileSize(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getFormatFolderSize(File f){
		try {
			return FormetFileSize(getFolderSize(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/*** 获取文件大小 ***/
	private long getFileSize(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			
		} else {
			f.createNewFile();
		}
		return s;
	}

	/*** 获取文件夹大小 ***/
	private long getFolderSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		if (flist!=null) {
			for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFolderSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		}
		return size;
	}
	//专门获取音频大小
	public String getFormatFolderSizeAudio(File f){
		try {
			return FormetFileSize(getFolderSizeAudio(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/*** 获取音频文件大小 ***/
	private long getFolderSizeAudio(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		if (flist!=null) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					
				} else if (flist[i].getName().startsWith("temp_audio_")) {
					size = size + flist[i].length();
				}
			}
		}
		return size;
	}

	/*** 转换文件大小单位(b/kb/mb/gb) ***/
	private String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS == 0) {
			fileSizeString = "0B";
		} else if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/*** 获取文件个数 ***/
	public long getlist(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;

	}
}
