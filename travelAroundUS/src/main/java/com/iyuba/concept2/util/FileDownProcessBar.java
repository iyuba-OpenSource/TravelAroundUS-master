package com.iyuba.concept2.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

import com.iyuba.concept2.R;
import com.iyuba.concept2.listener.DownLoadFailCallBack;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownProcessBar {

	private ProgressBar progressBar;
	private Context mContext;
	private long FileLength;
	private long DownedFileLength = 0;
	private InputStream inputStream;
	private String saveName;
	private String fileUrl;
	private BufferedInputStream bufferedInputStream;
	private int downPercent;
	private final static String FILE_DOWNLOAD_APPUPDATE_PATH = "/appUpdate";
	private DownLoadFailCallBack dlfcb; //
	private int downLoadType = 0; // 0=MP3,1=APK
	private int isdowning;
	private String savePathString;
	private int voaid;
	private RandomAccessFile oSavedFile;
	private HttpURLConnection connection;

	public FileDownProcessBar(Context context) {
		this.mContext = context;
	}

	public FileDownProcessBar(Context context, ProgressBar progressBar) {
		this.mContext = context;
		this.progressBar = progressBar;
	}

	public FileDownProcessBar(Context context, int voaid) {
		this.mContext = context;
		this.voaid = voaid;
	}

	/**
	 * 
	 * @param filePath
	 */
	public void downLoadAudioFile(final int voaId, final String saveName) {
		downLoadType = 0;
		this.saveName = saveName;
		isdowning = ConfigManager.Instance().loadInt("isdowning");

		if (SDCard.hasSDCard()) {
			DownedFileLength = 0; // 下载媒体

			// 在下载集合里
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						Looper.prepare();
						// 里面用到了accountmanager，accountmanager里有handler，所以此线程要起一个looper
						DownFile(String.valueOf(voaId), saveName);
						Looper.loop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			thread.start();

			isdowning++;
			ConfigManager.Instance().putInt("isdowning", isdowning);
		} else {
			CustomToast.showToast(mContext, "下载失败！请检查SD卡是否存在！", 1000);
		}
	}

	/**
	 * 
	 * @param filePath
	 */
	public void downLoadApkFile(final String newWorkFileUrl,
			final String saveName, DownLoadFailCallBack dlfcb) {
		downLoadType = 1; // 下载APK
		this.dlfcb = dlfcb;
		this.saveName = saveName;
		if (SDCard.hasSDCard()) {
			DownedFileLength = 0;
			Thread thread = new Thread() {
				public void run() {
					try {
						Looper.prepare();
						DownFile(newWorkFileUrl, saveName);
						Looper.loop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
			ConfigManager.Instance().putInt("isdowning", isdowning);
		} else {
			CustomToast.showToast(mContext, "下载失败！请检查SD卡是否存在！", 1000);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					if (progressBar != null) {
						progressBar.setMax(100);
					}
					break;
				case 1:
					if (downLoadType == 1) {
						if (progressBar != null) {
							progressBar.setProgress(downPercent);
						}
					}
					break;
				case 2:
					// Log.i("下载完成----------->", progressBar.getMax() + "");
					if (downLoadType == 0) {
						ConfigManager.Instance().putInt(
								String.valueOf(saveName), 100);
						reNameFile(savePathString, savePathString
								+ Constant.append);

					} else if (downLoadType == 1) {
						if (reNameFile(savePathString, savePathString + ".apk")) {
							if (dlfcb != null) {
								dlfcb.downLoadSuccess(savePathString + ".apk");
							}
						} else {
							dlfcb.downLoadFaild("error");
						}
					}
					handler.removeMessages(1);
					break;
				case 3: // 下载失败
					ConfigManager.Instance().putInt(saveName, 0);
					new ClearBuffer("audio/temp_audio_" + saveName).Delete();
					int isdowning = ConfigManager.Instance().loadInt(
							"isdowning");
					isdowning--;
					ConfigManager.Instance().putInt("isdowning", isdowning);
					CustomToast.showToast(mContext, "下载失败", 1000);
					break;
				case 4:

					break;
				default:
					break;
				}
			}
		}
	};

	public void DownFile(String urlString, String saveName) {
		this.saveName = saveName;

		// 设置下载的url和文件的存储路径
		if (downLoadType == 0) {
			String url;
			int voaId = Integer.valueOf(urlString);

			if (UserInfoManager.getInstance().isVip()
					&& (SettingConfig.Instance().isHighSpeed())) {
				url = Constant.sound_vip() + (voaId / 1000) + "_"
						+ (voaId % 1000) + Constant.append;
				CustomToast.showToast(mContext, R.string.study_vipdownload,
						1000);
			} else {
				url = Constant.sound() + (voaId / 1000) + "_"
						+ (voaId % 1000) + Constant.append;
			}

			this.fileUrl = url;

			savePathString = ConfigManager.Instance().loadString(
					"media_saving_path");

			File file = new File(savePathString);
			file.mkdirs();

			if (file == null || !(file.isDirectory()) ) {//增加双斜杠判断处理
				CustomToast.showToast(mContext, "下载路径出错，请在‘设置’界面重新设置", 1000);
				return;
			} else {
				savePathString = savePathString + "/" + this.saveName;
			}
			// savePathString = Constant.videoAddr + this.saveName;

		} else if (downLoadType == 1) {
			this.fileUrl = urlString;
			//这里的路径怎么还有双斜杠
			savePathString = Constant.envir + Constant.appfile
					+ FILE_DOWNLOAD_APPUPDATE_PATH + "/" + this.saveName;
		}

		// 如果文件夹里有这个完整的文件，显示下载完成
		File fileTemp = new File(savePathString + Constant.append);

		if (fileTemp.exists()) {
			isdowning--;
			ConfigManager.Instance().putInt("isdowning", isdowning);

			handler.sendEmptyMessage(2);
		} else {
			String savePAth = "";

			// 设置存储的文件夹的路径
			if (downLoadType == 0) {
				savePAth = ConfigManager.Instance().loadString(
						"media_saving_path");
			} else if (downLoadType == 1) {
				//这里已经在后面的常量中定义了斜杠，怎么还加一个斜杠呢，去掉吧
//				savePAth = Constant.envir + Constant.appfile + "/"
//						+ FILE_DOWNLOAD_APPUPDATE_PATH;
				savePAth = Constant.envir + Constant.appfile
						+ FILE_DOWNLOAD_APPUPDATE_PATH;
			}

			// 如果文件夹不存在，新建
			File file1 = new File(savePAth);

			if (!file1.exists()) {
				file1.mkdirs();
			}

			// 如果没有，进行下载，开始连接并打开url，拿输入流
			try {
				URL url = new URL(fileUrl);

				connection = (HttpURLConnection) url.openConnection();

				if (downLoadType == 0) {
					if (connection.getReadTimeout() == 5) {

					}
				}

				inputStream = connection.getInputStream();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			/*
			 * 向SD卡中写入文件,用Handle传递线程
			 */
			// Message message = new Message();

			try {
				oSavedFile = new RandomAccessFile(savePathString, "rw");
				bufferedInputStream = new BufferedInputStream(inputStream);

				if (downLoadType == 0) {
					
				} else {
					FileLength = connection.getContentLength();
				}

				byte[] buffer = new byte[1024 * 8];
				int length;

				handler.sendMessage(handler.obtainMessage(0));

				while (DownedFileLength < FileLength) {
					if (downLoadType == 0) {
						return;
					}

					length = bufferedInputStream.read(buffer);

					// 每次读入的字节数加到计数里面
					DownedFileLength += length;
					oSavedFile.write(buffer, 0, length);

					// 写的挺好
					handler.sendMessage(handler.obtainMessage(1));
					downPercent = (int) (DownedFileLength * 100 / FileLength);

					if (downLoadType == 0) {
						ConfigManager.Instance().putInt(saveName, downPercent);// 保存进度，用于列表识别进度条
					}

				}

				ConfigManager.Instance().putInt(saveName, 100);

				handler.sendEmptyMessage(2);
			} catch (FileNotFoundException e) {
				handler.sendEmptyMessage(3);
				e.printStackTrace();
			} catch (IOException e) {
				handler.sendEmptyMessage(3);
				e.printStackTrace();
			} finally {
				try {
					oSavedFile.close();
					inputStream.close();
					bufferedInputStream.close();
					connection.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件更名，从缓冲状态转换到完成状态
	 * 
	 * @param oldFilePath
	 * @param newFilePath
	 * @return
	 */
	public boolean reNameFile(String oldFilePath, String newFilePath) {
		File source = new File(oldFilePath);
		File dest = new File(newFilePath);
		isdowning = ConfigManager.Instance().loadInt("isdowning");
		isdowning--;
		ConfigManager.Instance().putInt("isdowning", isdowning);
		return source.renameTo(dest);
	}

}
