package com.iyuba.concept2.util;

import java.io.File;

import com.iyuba.configation.Constant;

/**
 * 清缓存
 * 
 * @author chentong
 * 
 */
public class ClearBuffer {
	private String filepath = Constant.envir + "/";

	public ClearBuffer(String type) {
		filepath = filepath + type;
	}

	public boolean Delete() {
		File file = new File(filepath);
		if (file.isFile()) {
			file.delete();
			return true;
		} else if (file.isDirectory()) {
			File files[] = file.listFiles();
			if (files.length == 0) {
				return false;
			} else {
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
				return true;
			}
		} else {
			return false;
		}
	}
}
