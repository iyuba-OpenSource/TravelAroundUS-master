package com.iyuba.concept2.manager;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.iyuba.concept2.R;

/** 
 * amr音频处理 
 *  
 * @author hongfa.yy 
 * @version 创建时间2012-11-21 下午4:33:28 
 * 
 * 来源网络  
 * http://shuimuqinghua77.iteye.com/blog/1739128
 */  
public class SmallRecordManager {
    private final String TAG = "RecordManager";  
    private MediaRecorder mMediaRecorder;  
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;  
    private File file;  
  
    public SmallRecordManager(File file,ImageView view) {  
        this.file = file;  
        this.view=view;  
    }  
    public SmallRecordManager(File file) {  
        this.file = file;  
    }  
  
    private long startTime;  
    private long endTime;  
  
    /** 
     * 开始录音 使用amr格式 
     *  
     * @param mRecAudioFile 
     *            录音文件 
     * @return 
     */  
    public void startRecord() {  
        // 开始录音  
        /* ①Initial：实例化MediaRecorder对象 */  
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();  
        } else {
        	mMediaRecorder.reset();  
        }
        try {  
            /* ②setAudioSource/setVedioSource */  
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风  
            /* 
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式 
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB) 
             */  
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */  
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
            /* ③准备 */  
            mMediaRecorder.setOutputFile(file.getAbsolutePath());  
            mMediaRecorder.setMaxDuration(MAX_LENGTH);  
            mMediaRecorder.prepare();;  
            /* ④开始 */  
            mMediaRecorder.start();  
            // AudioRecord audioRecord.  
            /* 获取开始时间* */  
            startTime = System.currentTimeMillis();  
            // pre=mMediaRecorder.getMaxAmplitude();  
            updateMicStatus();
            Log.i("ACTION_START", "startTime" + startTime);  
        } catch (IllegalStateException e) {  
            Log.i(TAG,  
                    "call startAmr(File mRecAudioFile) failed!"  
                            + e.getMessage());  
        } catch (IOException e) {  
            Log.i(TAG,  
                    "call startAmr(File mRecAudioFile) failed!"  
                            + e.getMessage());  
        }  
  
    }  
  
    /** 
     * 停止录音 
     *  
     * @param mMediaRecorder 
     */  
    public long stopRecord() {  
    	mHandler.removeCallbacks(mUpdateMicStatusTimer);
        if (mMediaRecorder == null)
            return 0L;  
       
        if (startTime==0||(startTime<endTime)) {
        	 return 0L;  
		} else {
			 mMediaRecorder.stop(); 
			 mMediaRecorder.release();
		}
       
        endTime = System.currentTimeMillis();  
        return endTime - startTime;  
    }  
  
    private final Handler mHandler = new Handler();  
    
    private Runnable mUpdateMicStatusTimer = new Runnable() {  
        public void run() {  
            updateMicStatus();  
        }  
    };  
  
    /** 
     * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20 
     * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值 
     * 当我不对着麦克风说任何话的时候，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准值。 
     * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话 
     */  
    private int BASE = 600;  
    private int SPACE = 100;// 间隔取样时间  
    private ImageView view;  
  
    private void updateMicStatus() {  
        if (mMediaRecorder != null && view != null) {
            int vuSize = 8 * mMediaRecorder.getMaxAmplitude() / 32768;  
            //int ratio = mMediaRecorder.getMaxAmplitude() / BASE;  
           /* int db = 0;// 分贝  
            if (ratio > 1)  
                db = (int) (20 * Math.log10(ratio));  */
            switch (vuSize % 8) {
            case 0:  
                view.setBackgroundResource(R.drawable.small_amp1); 
                break;  
            case 1:  
                view.setBackgroundResource(R.drawable.small_amp1);  
                break;  
            case 2:  
                view.setBackgroundResource(R.drawable.small_amp2);  
                break;  
            case 3:  
                view.setBackgroundResource(R.drawable.small_amp3);  
                break;  
            case 4:  
                view.setBackgroundResource(R.drawable.small_amp4);  
                break;  
            case 5:  
                view.setBackgroundResource(R.drawable.small_amp5);  
                break;  
            case 6:  
                view.setBackgroundResource(R.drawable.small_amp6);  
                break;  
            case 7:  
                view.setBackgroundResource(R.drawable.small_amp7);  
                break;  
            default:  
                //view.setImageResource(R.drawable.amp6);  
                break;  
            }  
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);  
            /* 
             * if (db > 1) { vuSize = (int) (20 * Math.log10(db)); Log.i("mic_", 
             * "麦克风的音量的大小：" + vuSize); } else Log.i("mic_", "麦克风的音量的大小：" + 0); 
             */  
        }  
    }  
  
}  