package com.iyuba.concept2.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UtilFile {
	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		boolean isok = true;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				BufferedInputStream bif = new BufferedInputStream(inStream);
				BufferedOutputStream bof = new BufferedOutputStream(fs);
				
				byte[] buffer = new byte[1024];
				int length;
				
				while ((byteread = bif.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					bof.write(buffer, 0, byteread);
				}
				if (fs != null) {
					fs.flush();
					fs.close();
				}

				if (inStream != null) {
					inStream.close();
				}

				if (bif != null) {
					bif.close();
				}
			} else {
				isok = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isok = false;
		}
		return isok;
	}

	/**
	 * 
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * 
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * 
	 * @return boolean
	 */
	public static boolean copyFolder(String oldPath, String newPath) {
		boolean isok = true;
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					if (!copyFile(temp.getPath(),
							newPath + "/" + (temp.getName()).toString())) {
						isok = false;
					} else {
						// 移动成功，删除原来的
						temp.delete();
					}

				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		}

		catch (Exception e) {
			isok = false;
		}
		return isok;
	}

}
