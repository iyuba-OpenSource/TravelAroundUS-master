package com.iyuba.concept2.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;

import android.os.Environment;

import com.iyuba.concept2.sqlite.mode.FileInfo;

/**
 * 文件浏览器相关类
 */
public class FileUtil {

    public static String getSDPath() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return "/";
        }
        return "/";
    }

    public static FileInfo getFileInfo(File f) {
        FileInfo info = new FileInfo();
        info.Name = f.getName();
        info.IsDirectory = f.isDirectory();
        calcFileContent(info, f);
        return info;
    }

    private static void calcFileContent(FileInfo info, File f) {
        if (f.isFile()) {
            info.Size += f.length();
        }
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    File tmp = files[i];
                    if (tmp.isDirectory()) {
                        info.FolderCount++;
                    } else if (tmp.isFile()) {
                        info.FileCount++;
                    }
                    if (info.FileCount + info.FolderCount >= 10000) { // ����һ�򲻼���
                        break;
                    }
                    calcFileContent(info, tmp);
                }
            }
        }
    }

    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = fileS + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " G";
        }
        return fileSizeString;
    }

    public static String combinPath(String path, String fileName) {
        return path + (path.endsWith(File.separator) ? "" : File.separator) + fileName;
    }

    public static boolean copyFile(File src, File tar) throws Exception {
        if (src.isFile()) {
            InputStream is = new FileInputStream(src);
            OutputStream op = new FileOutputStream(tar);
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(op);
            byte[] bt = new byte[1024 * 8];
            int len = bis.read(bt);
            while (len != -1) {
                bos.write(bt, 0, len);
                len = bis.read(bt);
            }
            bis.close();
            bos.close();
        }
        if (src.isDirectory()) {
            File[] f = src.listFiles();
            tar.mkdir();
            for (int i = 0; i < f.length; i++) {
                copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile() + File.separator
                        + f[i].getName()));
            }
        }
        return true;
    }

    public static boolean moveFile(File src, File tar) throws Exception {
        if (copyFile(src, tar)) {
            deleteFile(src);
            return true;
        }
        return false;
    }

    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    deleteFile(files[i]);
                }
            }
        }
        f.delete();
    }

    public static String getMIMEType(String name) {
        String type = "";
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("apk")) {
            return "application/vnd.android.package-archive";
        } else if (end.equals("mp4") || end.equals("avi") || end.equals("3gp")
                || end.equals("rmvb")) {
            type = "video";
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf")
                || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("txt") || end.equals("log")) {
            type = "text";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }


    public static String getTextFromFile(String filePath) {
        String question = "";
        try {
            String encoding = "utf-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    question += lineTxt;
                }
                read.close();
            } else {
                question = "找不到指定的文件.";
            }
        } catch (Exception e) {
            question = "抱歉!读取文件内容出错.";
            e.printStackTrace();
        }
        return question;
    }
}
