package com.iyuba.concept2.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.ise.result.Result;
import com.iflytek.ise.result.xml.XmlResultParser;
import com.iyuba.concept2.util.AmrEncoder;
import com.iyuba.concept2.util.FileCopyUtil;
import com.iyuba.concept2.util.RecordUtil;
import com.iyuba.concept2.util.ResultParse;
import com.iyuba.concept2.util.WavWriter;
import com.iyuba.configation.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//进行语音对比打分
public class IseManager {
    private static final String TAG = IseManager.class.getSimpleName();

    private static IseManager instance;
    private static Handler handler;

    private SpeechEvaluator mSpeechEvaluator;

    private String LANGUAGE = "en_us";
    private String ISE_CATEGORY = "read_sentence";
    private String RESULT_LEVEL = "complete";
    private String VAD_BOS = "5000";
    private String VAD_EOS = "1800";
    private String KEY_SPEECH_TIMEOUT = "-1";

    private String resultStr;
    private Result resultEva; //

    private String pcmFileName;

    private int time;// 录音时间
    private int senIndex;
    private String sentence;
    private String endTime;
    private Context mContext;
    private String amrPath;
    private RecordManager rManager;
    private thread recordThread;

    private long startDate, endDate;

    private List<File> list_file = new ArrayList<>();

    public IseManager(Context context) {
        this.mContext = context;
        mSpeechEvaluator = SpeechEvaluator.createEvaluator(context, null);
    }

    public static IseManager getInstance(Context context, Handler h) {
        if (instance == null) {
            instance = new IseManager(context);
        }
        handler = h;
        return instance;
    }

    private void setParams(int senIndex) {
        //录音原始文件

        pcmFileName = Constant.getsimRecordAddr() + senIndex
                + ".pcm";

//        pcmFileName = Environment.getExternalStorageDirectory()
//                .getAbsolutePath()
//                + "/msc/"
//                + LANGUAGE
//                + "_"
//                + ISE_CATEGORY
//                + "_" + senIndex + ".pcm";
        LogUtil.e("pcmFileName"+ pcmFileName);
        // 仅作测试
        // pcmFileName
        // =Environment.getExternalStorageDirectory().getAbsolutePath()
        // +"/msc "+"/test.pcm";
        // 测试部分
        mSpeechEvaluator.setParameter(SpeechConstant.LANGUAGE, LANGUAGE);
        mSpeechEvaluator
                .setParameter(SpeechConstant.ISE_CATEGORY, ISE_CATEGORY);
        mSpeechEvaluator.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mSpeechEvaluator.setParameter(SpeechConstant.SAMPLE_RATE, 16000 + "");
        mSpeechEvaluator.setParameter(SpeechConstant.VAD_BOS, VAD_BOS);
        mSpeechEvaluator.setParameter(SpeechConstant.VAD_EOS, VAD_EOS);
        mSpeechEvaluator.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT,
                KEY_SPEECH_TIMEOUT);
        mSpeechEvaluator
                .setParameter(SpeechConstant.RESULT_LEVEL, RESULT_LEVEL);
        mSpeechEvaluator.setParameter(SpeechConstant.ISE_AUDIO_PATH,
                pcmFileName);
    }

    // 评测监听接口
    private EvaluatorListener mEvaluatorListener = new EvaluatorListener() {

        @Override
        public void onResult(EvaluatorResult result, boolean isLast) {
            Log.e(TAG, result.toString());
            handler.sendEmptyMessage(7); // 不加这个音频会自动启动，别问我为什么

            if (isLast) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
                endTime = df.format(new Date());// new Date()为获取当前系统时间
                Log.e("时间", endTime + "===");
                StringBuilder builder = new StringBuilder();
                builder.append(result.getResultString());
                resultStr = builder.toString();
                resultParse();

                Message msg = handler.obtainMessage();
                msg.what = 6;
                msg.arg1 = (int) (resultEva.total_score * 20);//分数
                msg.arg2 = senIndex;
                msg.obj = resultEva.is_rejected;

                handler.sendMessage(msg);
                transformPcmToAmr();
//                new TransferThread(mContext, Constant.getsimRecordAddr() + senIndex
//                        + Constant.getrecordTag()).start();
            }
        }

        @Override
        public void onError(SpeechError error) {
//            if (rManager != null) {
//                rManager.stopRecord();
//            }
            if (error != null) {
                // showTip("error:" + error.getErrorCode() + ","
                // + error.getErrorDescription());
            } else {
                Log.e(TAG, "evaluator over");
            }
        }

        @Override
        public void onBeginOfSpeech() {
            Log.e(TAG, "evaluator begin");

            startDate = new Date().getTime();// new Date()为获取当前系统时间
            //开始讲话
        }

        @Override
        public void onEndOfSpeech() {

            endDate = new Date().getTime();
            //结束讲话
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onVolumeChanged(int arg0, byte[] arg1) {
            // TODO Auto-generated method stub
            noticeVolume(arg0);
        }

    };

    public void noticeVolume(int volume) {
        Message msg = handler.obtainMessage();
        msg.what = 2;
        msg.arg1 = volume;
        handler.sendMessage(msg);
    }

    //开始评测
    public void startEvaluate(String sen, int senIndex) {
        if (mSpeechEvaluator != null) {
            setParams(senIndex);

            amrPath = Constant.getsimRecordAddr() + senIndex + Constant.getrecordTag();
            mSpeechEvaluator.startEvaluating(sen, null, mEvaluatorListener);//评测监听接口 开始录音？？？
            this.senIndex = senIndex;
            this.sentence = sen;
            time = 0;
        }
    }

    public boolean isEvaluating() {
        return mSpeechEvaluator.isEvaluating();
    }

    public void stopEvaluating() {
        if (mSpeechEvaluator.isEvaluating()) {
            mSpeechEvaluator.stopEvaluating();
        }
    }

    public void cancelEvaluate(boolean cancel) {
        mSpeechEvaluator.cancel();
    }

    public void resultParse() {
        if (!TextUtils.isEmpty(resultStr)) {
            XmlResultParser resultParser = new XmlResultParser();
            resultEva = resultParser.parse(resultStr.toString());
        }
    }

    public String getResult() {
        return resultStr;
    }

    public Result getResultEva() {
        return resultEva;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getTime() {
        return time;
    }


    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {

        return endDate;
    }

    //保存录音

    public void transformPcmToAmr() {
        File amrFile = new File(Constant.getsimRecordAddr() + senIndex
                + Constant.getrecordTag());
        File pcmFile = new File(pcmFileName);
        FileCopyUtil.fileChannelCopy(pcmFile, amrFile);
        WavWriter myWavWriter = null;
        try {
            myWavWriter = new WavWriter(amrFile, 16000);
            myWavWriter.writeHeader();
            myWavWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecordUtil ru = new RecordUtil();

        ru.pcm2mp3(pcmFileName, Constant.getsimRecordAddr()  + senIndex
                + ".mp3");

//        pcmFile.delete();
    }

    public class TransferThread extends Thread {
        private String amrPath;
        private Context context;

        public TransferThread(Context context, String amrPath) {
            this.context = context;
            this.amrPath = amrPath;
        }

        @Override
        public void run() {
            transfer();
        }

        private void transfer() {
            String rootPath = Environment.getExternalStorageDirectory().getPath();
            try {
                File pcmFile = new File(pcmFileName);
                InputStream pcmStream = new FileInputStream(pcmFile);
                AmrEncoder.pcm2Amr(pcmStream, amrPath);
                pcmFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class thread extends Thread {
        @Override
        public void run() {
            super.run();
            File file = new File(amrPath);
            rManager = new RecordManager(file, null);
            rManager.startRecord();
        }

        public void stopRecord() {
            if (rManager != null)
                rManager.stopRecord();
        }
    }
//	public static void pcm2Amr(String pcmPath , String amrPath) {
//		try {
//			FileInputStream fis = new FileInputStream(pcmPath);
//			AmrInputStream ais = new AmrInputStream(fis);
//			OutputStream out = new FileOutputStream(amrPath);
//			byte[] buf = new byte[4096];
//			int len = -1;
//			/*
//			 * 下面的amr的文件头
//			 * 缺少这几个字节是不行的
//			 */
//			out.write(0x23);
//			out.write(0x21);
//			out.write(0x41);
//			out.write(0x4D);
//			out.write(0x52);
//			out.write(0x0A);
//			while((len = ais.read(buf)) >0){
//				out.write(buf,0,len);
//			}
//			out.close();
//			ais.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

    public SpannableStringBuilder getSenStyle() {
        Log.i(TAG, String.valueOf(resultEva.is_rejected));
        Log.i(TAG, resultEva.toString());
        return ResultParse.getSenResult(resultEva, sentence);
    }
}
