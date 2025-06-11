package com.iyuba.concept2.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.iyuba.configation.Constant;

public class ImageOperation {
	private final static String APP_PATH = "/voa";
	private static String IMAGE_DOWNLOAD_PATH = APP_PATH + "/image";
	private final static String IMAGE_DOWNLOAD_TAG = "temp_image_";
	private final static String IMAGE_DOWNLOAD_BIG_TAG = "temp_big_image_";
	private static boolean de_weight;

	/**
	 * 加载图片小图，默认从缓存中取图片，如缓存中没有图片，则从网络获取
	 * 
	 * @param imgUrl
	 * @return
	 */
	private static Bitmap loadimage(String imgUrl, Bitmap bitmap, String name) {
		// Log.e("1111111111", imgUrl);
		if (bitmap == null) {
			if (checkImageForSDCard(name) && !de_weight) { // 命中缓存图片
				StringBuffer imageFilePath = new StringBuffer();
				imageFilePath = imageFilePath
						.append(Environment.getExternalStorageDirectory())
						.append(IMAGE_DOWNLOAD_PATH).append("/")
						.append(IMAGE_DOWNLOAD_TAG).append(name)
						.append(".jpeg");
				try {
					bitmap = BitmapFactory.decodeFile(imageFilePath.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // 网络获取图片
				URL fileURL = null;
				try {
					fileURL = new URL(imgUrl);
				} catch (MalformedURLException err) {
					err.printStackTrace();
				}
				try {
					HttpURLConnection conn = (HttpURLConnection) fileURL
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					int length = (int) conn.getContentLength();
					if (length != -1) {
						byte[] imgData = new byte[length];
						byte[] buffer = new byte[1024 * 4];
						int readLen = 0;
						int destPos = 0;
						OutputStream outputStream = null;
						StringBuffer bigImageFilePath = new StringBuffer();
						bigImageFilePath = bigImageFilePath
								.append(Environment
										.getExternalStorageDirectory())
								.append(IMAGE_DOWNLOAD_PATH).append("/")
								.append(IMAGE_DOWNLOAD_BIG_TAG).append(name)
								.append(".jpeg");

						File dirFile = new File(
								Environment.getExternalStorageDirectory()
										+ IMAGE_DOWNLOAD_PATH);
						if (!dirFile.exists()) {
							dirFile.mkdirs();
						}
						File fileTemp = new File(bigImageFilePath.toString());
						if (!fileTemp.exists()) {
							try {
								fileTemp.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						outputStream = new FileOutputStream(fileTemp);
						while ((readLen = is.read(buffer)) > 0) { // 度数据
							System.arraycopy(buffer, 0, imgData, destPos,
									readLen);
							destPos += readLen;
							// outputStream.write(buffer,0,readLen);
						}
						outputStream.write(imgData, 0, imgData.length);
						// -----------------加载缩略图并保存
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inSampleSize = 4;
						bitmap = BitmapFactory.decodeByteArray(imgData, 0,
								imgData.length, opts);
						saveImage(bitmap, name);

						is.close();
						outputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
			}
		}
		return bitmap;
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	private static void saveImage(Bitmap bm, String fileName)
			throws IOException {
		File dirFile = new File(Environment.getExternalStorageDirectory()
				+ IMAGE_DOWNLOAD_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		StringBuffer imageFilePath = new StringBuffer();
		imageFilePath = imageFilePath
				.append(Environment.getExternalStorageDirectory())
				.append(IMAGE_DOWNLOAD_PATH).append("/")
				.append(IMAGE_DOWNLOAD_TAG).append(fileName).append(".jpeg");
		File imgFile = new File(imageFilePath.toString());

		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(imgFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}

	public static Bitmap getBigImageBitmap(String name) {
		StringBuffer bigImageFilePath = new StringBuffer();
		bigImageFilePath = bigImageFilePath
				.append(Environment.getExternalStorageDirectory())
				.append(IMAGE_DOWNLOAD_PATH).append("/")
				.append(IMAGE_DOWNLOAD_BIG_TAG).append(name).append(".jpeg");

		try {
			return BitmapFactory.decodeStream(new FileInputStream(
					bigImageFilePath.toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getBigImagePath(String name) {
		StringBuffer bigImageFilePath = new StringBuffer();
		bigImageFilePath = bigImageFilePath
				.append(Environment.getExternalStorageDirectory())
				.append(IMAGE_DOWNLOAD_PATH).append("/")
				.append(IMAGE_DOWNLOAD_BIG_TAG).append(name).append(".jpeg");
		return bigImageFilePath.toString();
	}

	/**
	 * 检查图片文件在SD卡中是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean checkImageForSDCard(String fileName) {
		StringBuffer imageFilePath = new StringBuffer();
		imageFilePath = imageFilePath
				.append(Environment.getExternalStorageDirectory())
				.append(IMAGE_DOWNLOAD_PATH).append("/")
				.append(IMAGE_DOWNLOAD_TAG).append(fileName).append(".jpeg");
		File imgFile = new File(imageFilePath.toString());
		if (imgFile.exists()) {
			return true;
		}
		return false;
	}

	public static Bitmap loadUserImage(String path, String imgUrl,
			Bitmap bitmap, String name) {
		IMAGE_DOWNLOAD_PATH = APP_PATH + "/" + path;
		de_weight = true;
		loadimage(imgUrl, bitmap, name);
		return getBigImageBitmap(name);
	}

	public static Bitmap loadNewsImage(String path, String imgUrl,
			Bitmap bitmap, String name) {
		IMAGE_DOWNLOAD_PATH = APP_PATH + "/" + path;
		de_weight = false;
		return loadimage(imgUrl, bitmap, name);
	}
	public static int CopySdcardFile(String fromFile, String toFile)
	{
	try 
	{
	InputStream fosfrom = new FileInputStream(fromFile);
	OutputStream fosto = new FileOutputStream(toFile);
	byte bt[] = new byte[1024];
	int c;
	while ((c = fosfrom.read(bt)) > 0) 
	{
	fosto.write(bt, 0, c);
	}
	fosfrom.close();
	fosto.close();
	return 0;

	} catch (Exception ex) 
	{
	return -1;
	}
	}

	public static String getSharePicPath(String name) {
		IMAGE_DOWNLOAD_PATH = APP_PATH + "/image";
		File file=new File(Constant.picAddr+"/"+name+".jpg.cach");
		CopySdcardFile(Constant.picAddr+"/"+name+".jpg.cach", Constant.picAddr+"/"+name+".jpg");
		return Constant.picAddr+"/"+name+".jpg";
	}
}
