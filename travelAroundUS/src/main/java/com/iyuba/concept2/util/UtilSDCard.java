package com.iyuba.concept2.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class UtilSDCard {
	
	public static String getExternalSDPath() {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			String mount = new String();
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line.contains("secure")) continue;
				if (line.contains("asec")) continue;
				Log.d("line",line);
				if (line.contains("fat")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1&&columns[3].startsWith("rw")) {
						//mount = mount.concat(columns[1]);
						mount=columns[1];
						return mount;
					}
				} else if (line.contains("fuse")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1&&columns[3].startsWith("rw")) {
						//mount = mount.concat(columns[1]);
						mount=columns[1];
					}
				}
			}
			return mount;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
		
	}
	  

}
