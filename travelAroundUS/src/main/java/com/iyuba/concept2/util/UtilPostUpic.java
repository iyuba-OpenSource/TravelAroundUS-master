//package com.iyuba.concept2.util;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import android.os.Environment;
//import android.util.Log;
//
//import com.iyuba.configation.RuntimeManager;
//import com.iyuba.core.common.manager.AccountManager;
//
//public class UtilPostUpic {
//	private static String newName;
//	private static String success;
//	private static String uploadFile;
//	private static String uid;
//	public static String post(String actionUrl,File files) throws IOException {
//		String end = "\r\n";
//		String twoHyphens = "--";
//		String boundary = "*****";
//		try {
//			if (uid==null||uid.equals("")) {
//				uid=AccountManager.Instance(RuntimeManager.getContext()).userId;
//			}
//			URL url = new URL(actionUrl);
//			Log.d("??????????",actionUrl+"");
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			/* 允许Input、Output，不使用Cache */
//			con.setDoInput(true);
//			con.setDoOutput(true);
//			con.setUseCaches(false);
//			/* 设定传送的method=POST */
//			con.setRequestMethod("POST");
//			con.setDoInput(true);
//			con.setDoOutput(true);
//			/* setRequestProperty */
//			con.setRequestProperty("Connection", "Keep-Alive");
//			con.setRequestProperty("Charset", "UTF-8");
//			con.setRequestProperty("Content-Type",
//					"multipart/form-data;boundary=" + boundary);
//			/* 设定DataOutputStream */
//			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//			/* // 首先组拼文本类型的参数
//		    for (Map.Entry<String, String> entry : params.entrySet()) {
//		    	ds.writeBytes(twoHyphens+boundary+end);
//		    	ds.writeBytes("Content-Disposition: form-data; name=\""
//		                + entry.getKey() + "\"" + end+end);
//		    	ds.writeBytes(entry.getValue());
//		    	ds.writeBytes(end);
//		    	Log.d("??????????","参数");
//		    }
//			if (files != null)
//		        for (Map.Entry<String, File> file : files.entrySet()) {  */
//			ds.writeBytes(twoHyphens + boundary + end);
//			ds.writeBytes("Content-Disposition: form-data; "
//					+ "name=\""+files+"\""+";filename=\"" + System.currentTimeMillis() + ".jpg\"" + end);
//			Log.d("??????????","Content-Disposition: form-data; "
//					+ "name=\""+files+"\""+";filename=\"" + System.currentTimeMillis() + ".jpg\"" + end);
//			ds.writeBytes(end);
//			/* 取得文件的FileInputStream */
//			/*if (uploadFile==null) {
//				handler.sendEmptyMessage(2);
//				return;
//			}*/
//			FileInputStream fStream = new FileInputStream(Environment.getExternalStorageDirectory()
//					+ "/temp.jpg");
//			/* 设定每次写入1024bytes */
//			int bufferSize = 1024;
//			byte[] buffer = new byte[bufferSize];
//			int length = -1;
//			/* 从文件读取数据到缓冲区 */
//			while ((length = fStream.read(buffer)) != -1) { /* 将数据写入DataOutputStream中 */
//				Log.d("??????????",buffer.length+"");
//				ds.write(buffer, 0, length);
//			}
//			ds.writeBytes(end);
//			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
//			/* close streams */
//			fStream.close();
//
//			ds.flush();
//			/* 取得Response内容 */
//			InputStream is = con.getInputStream();
//			int ch;
//			StringBuffer b = new StringBuffer();
//			while ((ch = is.read()) != -1) {
//				b.append((char) ch);
//			}
//			ds.close();
//			/* 将Response显示于Dialog */
//			success = b.toString().trim();
//		} catch (Exception e) {
//			e.printStackTrace();
//			//failure = e.getMessage();
//			//handler.sendEmptyMessage(7);
//		}
//		return success;
//	}
//
//}
