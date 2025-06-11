//package com.iyuba.concept2.manager;
//
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.channels.FileChannel;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//
//public class MergeHelper {
//
//    public static void merge(File targetFile, File... sourceFiles) throws IOException {
//        merge(targetFile, Arrays.asList(sourceFiles));
//    }
//
//    public static void merge(File targetFile, List<File> sourceFiles) throws IOException {
//        if (sourceFiles.size() == 0) throw new IOException("no source files!");
//        if (targetFile.exists()) targetFile.delete();
//        FileChannel in;
//        FileChannel out;
//        out = new FileOutputStream(targetFile).getChannel();
//        for (File pcmFile : sourceFiles) {
//            in = new FileInputStream(pcmFile).getChannel();
//            in.transferTo(0, in.size(), out);
//            in.close();
//        }
//        out.close();
//    }
//
//
//
//
//    public static String getRingDuring(String mUri) {
//        String duration = null;
//        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
//        try {
//            if (mUri != null) {
//                HashMap<String, String> headers = null;
//                if (headers == null) {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
//                mmr.setDataSource(mUri, headers);
//            }
//            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
//        } catch (Exception ex) {
//        } finally {
//            mmr.release();
//        }
//        Log.e("ryan", "duration " + duration);
//        return duration;
//    }
//
//}
