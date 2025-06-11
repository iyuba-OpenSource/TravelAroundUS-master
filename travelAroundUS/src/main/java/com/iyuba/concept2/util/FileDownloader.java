package com.iyuba.concept2.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.concept2.manager.DownloadStateManager;
import com.iyuba.concept2.sqlite.mode.Book;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FileDownloader extends Thread {
    private DownloadStateManager downloadStateManager;

    private int fileLen;

    private int downPercent;
    public DownloadInfo curInfo;
    public List<DownloadInfo> infoList;
    public List<Book> bookList;
    private String urlStr = null;
    private String dirPath = null;
    private String savePath = null;
    private File file = null;
    public int downloadNum = -1;
    public int downloadState = 0;

    public Handler handler;
    private static final String TAG = "FileDownloader";

    private static FileDownloader instance;
    public static FileDownloader getInstance() {
        if (instance == null) {
            synchronized (FileDownloader.class){
                if (instance==null){
                    instance = new FileDownloader();
                }
            }
        }

        return instance;
    }

    public int getDownloadState() {
        for (Book book : bookList) {
            Log.e(TAG, "book.downloadState"+book.downloadState + "");
            if (book.downloadState == 1) {
                return (downloadState = 1);
            }
        }

        return (downloadState = 0);
    }


    public FileDownloader() {
        downloadStateManager = DownloadStateManager.getInstance();
        infoList = downloadStateManager.downloadList;
        bookList = downloadStateManager.bookList;
        handler = downloadStateManager.handler;
        makedir();
    }

    public void makedir() {
        dirPath = ConfigManager.Instance().loadString("media_saving_path");
        file = new File(dirPath);
        if (file != null) {
            file.mkdirs();
        }
    }

    public void run() {
        for (int i = 0; ; i++) {
//            if (infoList != null) {
                try {
                    if (i == infoList.size()) {
                        if (downloadNum == 0 || infoList.size() == 0) {
                            synchronized (bookList) {//线程同步，保证只有一个在下载
                                try {
                                    setBookDownloadState(0);//设置书的下载状态
                                    bookList.wait();
                                    downloadState = 0;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        i = 0;
                        downloadNum = 0;
                    }

                    curInfo = infoList.get(i);

                    Log.e("下载状态"+curInfo.voaId , curInfo.downloadedState + "");

                    savePath = dirPath + "/" + curInfo.voaId;
                    file = new File(savePath + Constant.append);
                    // 当文件存在时，继续下载下一个文件
                    if (file.exists()) {
                        if (curInfo.downloadedState != 2) {
                            curInfo.downloadedState = 2;
                            downloadStateManager.updateDownloadInfo(curInfo);
                        }
                        continue;
                    }

                    // 当当前文件的状态为等待时，设置下载状态为正在下载
                    if (curInfo.downloadedState == -2 || curInfo.downloadedState == 1) {
                        curInfo.downloadedState = 1;
                        // 设置一册书的下载状态
                        setBookDownloadState(curInfo.voaId / 1000);
                        downloadNum++;
                        downloadState = 1;

                        download();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        //}
    }

    public void setBookDownloadState() {
        for (Book tempBook : bookList) {
            if (downloadNum == 0) {
                if (tempBook.downloadState == 1 || tempBook.downloadState == -2) {
                    tempBook.downloadState = -1;
                }
            }
        }
    }

    public void setBookDownloadState(int bookId) {
        bookId=0;
        for (Book tempBook : bookList) {
            if (tempBook.bookId == bookId) {
                tempBook.downloadState = 1;
            } else if (tempBook.downloadState == 1) {
                tempBook.downloadState = -2;
            }
        }
    }

    //下载方法
    public void download() {
        if (UserInfoManager.getInstance().isVip()
                && (SettingConfig.Instance().isHighSpeed())) {
            urlStr = Constant.sound_vip() + curInfo.url;
        } else {
            urlStr = Constant.sound() + curInfo.url;
        }

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Net");

            // 设置超时时间
            conn.setConnectTimeout(10000);
            if (conn.getResponseCode() == 200) {
                fileLen = conn.getContentLength();
                file = new File(savePath);
                RandomAccessFile raf = new RandomAccessFile(file, "rws");
                raf.setLength(fileLen);
                raf.close();

                download(url, file, fileLen);
                Log.e("下载中",urlStr);
            } else {
                throw new IllegalArgumentException("404 path: " + urlStr);
            }
        } catch (Exception e) {
            curInfo.downloadedState = 0;
            Log.e("下载失败download", e.getMessage());
            Log.e("进入下载url", urlStr);
            handler.sendEmptyMessage(0);
        }
    }

    public void download(URL url, File file, int fileLen) {
        long start = curInfo.downloadedBytes; // 开始位置 += 已下载量
        long end = fileLen - 1;

        if (start > end) {
            file.delete();
            file = new File(savePath);
            curInfo.downloadedBytes = 0;
            start = 0;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            // 获取指定位置的数据，Range范围如果超出服务器上数据范围, 会以服务器数据末尾为准 + end
            conn.setRequestProperty("RANGE", "bytes=" + start + "-" + end);
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(start);
            // 开始读写数据
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[1024 * 10];
            int len;
            while ((len = in.read(buf)) != -1) {
                raf.write(buf, 0, len);
                curInfo.downloadedBytes += len;
                curInfo.totalBytes = fileLen;
                downPercent = (int) (curInfo.downloadedBytes * 100 / fileLen);
                curInfo.downloadPer = downPercent;
                // 记录每个线程已下载的数据量
                downloadStateManager.updateDownloadInfo(curInfo);

                if (curInfo.downloadedState == -1) {
                    break;
                }
            }
            in.close();
            raf.close();

            if (curInfo.downloadedBytes == curInfo.totalBytes) {
                curInfo.downloadedState = 2;
                downloadStateManager.updateDownloadInfo(curInfo);

                int bookId = 0;//curInfo.voaId / 1000 * 1000;
                downloadStateManager.updateBook(bookId);
                Book tempBook = bookList.get(0);//bookId / 1000 - 1
                tempBook.downloadNum++;
                if (tempBook.downloadNum > tempBook.totalNum) {
                    tempBook.downloadNum = tempBook.totalNum;
                }

                if (getBookDownloadNum(tempBook.bookId) <= 0) {
                    tempBook.downloadState = 0;
                }

                reNameFile(savePath, savePath + Constant.append);
                file = new File(savePath);
                file.delete();

                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.arg1 = curInfo.voaId;
                //msg.arg2= curInfo.l
                //下载成功
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            curInfo.downloadedState = -2;
            Log.e("download 1，下载失败", e.getMessage());
            handler.sendEmptyMessage(0);
        }
    }

    public int getBookDownloadNum(int bookId) {
        int downloadNum = 0;
        int bookIndex = bookId ;// / 1000;

        for (DownloadInfo info : infoList) {
            if (info.voaId== bookIndex) {
                if (info.downloadedState == -1 || info.downloadedState == -2) {
                    downloadNum++;
                }
            }
        }

        return downloadNum;
    }

    public boolean reNameFile(String oldFilePath, String newFilePath) {
        File source = new File(oldFilePath);
        File dest = new File(newFilePath);
        return source.renameTo(dest);
    }

    //更新下载信息，添加下载？
    public void updateInfoList(DownloadInfo downloadInfo) {
        infoList.add(downloadInfo);

        // 恢复所有线程
        synchronized (bookList) {
            bookList.notifyAll();
        }
    }

    public void updateInfoList(List<DownloadInfo> downloadInfoList) {
        for (DownloadInfo downloadInfo : downloadInfoList) {
            infoList.add(downloadInfo);
        }

        // 恢复所有线程
        synchronized (bookList) {
            bookList.notifyAll();
        }
    }

    public void updateInfoList() {
        // 恢复所有线程
        synchronized (bookList) {
            bookList.notifyAll();
        }
    }

}
