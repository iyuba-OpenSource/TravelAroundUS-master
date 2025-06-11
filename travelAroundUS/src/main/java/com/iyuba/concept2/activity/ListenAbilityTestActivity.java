package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.UploadTestRecordRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.JsonUtil;
import com.iyuba.concept2.widget.RoundProgressBar;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.lil.user.UserInfoManager;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/30.
 */

public class ListenAbilityTestActivity extends BasisActivity {

    private final int TYPE_JUSTIC_SOUND = 0;//准确辨音
    private final int TYPE_LOGIC = 1;//听能逻辑
    private final int TYPE_MATCH = 2;//音义匹配
    private final int TYPE_DICTATION = 3;//听写
    private String[] mTestTypeArr = Constant.LIS_ABILITY_ARR;//能力测评的维度


    private TextView question, textA, textB, textC, textD, nextQues, timeUsed, quesIndex, testTotalTime;
    private Context mContext = this;
    private ArrayList<IntelTestQues> listenQues;
    private int quesTotalNo = 40, index = 0;
    private LinearLayout lA, lB, lC, lD;
    private int totalTime = 600;
    private ProgressBar pb;
    private RoundProgressBar rp;
    private Player player;

    private int duration = 0, position = 0;
    private Timer mTimer;
    private ImageView imageA, imageB, imageC, imageD;
    private boolean flag = false;
    private EditText et;
    private boolean completeFlag = false;
    private int rightNum[] = {0, 0, 0, 0, 0, 0};
    private GetDeviceInfo deviceInfo;
    private AbilityTestRecordOp zdbHelper;
    private TestRecord mTestRecord;
    /**
     * 用于存储每个类型的题目个数的
     */
    private int[] mCategoryType = new int[4];

    private int score1 = 0, score2 = 0, score3 = 0, score4 = 0, score5 = 0, score6 = 0, undoNum = 0;
    private String beginTime, endTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_ability_test);
        beginTime = df.format(new Date());// new Date()为获取当前系统时间
        mTestRecord = new TestRecord();

        nextQues = (TextView) findViewById(R.id.next_w_ques);
        timeUsed = (TextView) findViewById(R.id.time_used);
        quesIndex = (TextView) findViewById(R.id.ques_index);
        testTotalTime = (TextView) findViewById(R.id.intel_test_total_time);

        question = (TextView) findViewById(R.id.write_ques);
        textA = (TextView) findViewById(R.id.A_w_text);
        textB = (TextView) findViewById(R.id.B_w_text);
        textC = (TextView) findViewById(R.id.C_w_text);
        textD = (TextView) findViewById(R.id.D_w_text);

        lA = (LinearLayout) findViewById(R.id.ans_w_A);
        lB = (LinearLayout) findViewById(R.id.ans_w_B);
        lC = (LinearLayout) findViewById(R.id.ans_w_C);
        lD = (LinearLayout) findViewById(R.id.ans_w_D);

        imageA = (ImageView) findViewById(R.id.ques_image_a);
        imageB = (ImageView) findViewById(R.id.ques_image_b);
        imageC = (ImageView) findViewById(R.id.ques_image_c);
        imageD = (ImageView) findViewById(R.id.ques_image_d);

        pb = (ProgressBar) findViewById(R.id.w_ques_progress);
        rp = (RoundProgressBar) findViewById(R.id.listen_ques_play);
        et = (EditText) findViewById(R.id.listen_answer_edit);
        rp.setVisibility(View.VISIBLE);
        player = new Player(mContext, null);
        deviceInfo = new GetDeviceInfo(mContext);
        zdbHelper = new AbilityTestRecordOp(mContext);

        listenQues = new ArrayList<>();
        listenQues = (ArrayList<IntelTestQues>) getIntent().getSerializableExtra("QuestionList");
        formatQuesList();

        quesTotalNo = listenQues.size();

        mTestRecord.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        //答题记录  共性的
        mTestRecord.appId = Constant.APPID;
        mTestRecord.index = 1;
        mTestRecord.deviceId = deviceInfo.getLocalMACAddress();
        mTestRecord.testMode = Constant.ABILITY_LISTEN;

        totalTime = getIntent().getIntExtra("totalTime", 0);
        testTotalTime.setText(String.valueOf(totalTime) + "m");
        timeUsed.setText(String.valueOf(totalTime) + "m 00s");
        totalTime = totalTime * 60;
        pb.setMax(totalTime);
        pb.setProgress(totalTime);


        nextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    zdbHelper.saveTestRecord(mTestRecord);
//                    userAns[index] = "A";
                    Editable aaa = et.getText();
                    if (listenQues.get(index - 1).testType.equals("2")) {
                        if (aaa.toString().equals(listenQues.get(index - 1).answer)) {
                            Log.e("spellword", aaa.toString());
                            Log.e("wordanswer", listenQues.get(index - 1).answer);
                            score4++;
                        }
                        if (aaa.toString().equals(""))
                            undoNum++;
                    } else {
                        undoNum++;
                    }
                    proIECC();
                } else {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    zdbHelper.saveTestRecord(mTestRecord);
//                    if (completeFlag)
//                        gotoResultActivity();
//                    else {
//                        undoNum++;
//                        gotoResultActivity();
//                    }
                    if (et.getText().toString().equals("")) {
                        undoNum++;
                        gotoResultActivity();
                    } else if (et.getText().toString().equals(listenQues.get(index - 1).answer)) {
                        Log.e("spellword", et.getText().toString());
                        Log.e("wordanswer", listenQues.get(index - 1).answer);
                        score4++;
                        gotoResultActivity();
                    } else {
                        gotoResultActivity();
                    }
                }
            }
        });

        rp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mLocalAudioPrefix = Constant.envir + "audio/";
                String au = mLocalAudioPrefix + listenQues.get(index - 1).sound;
                player.playUrl(au);
                rp.setBackgroundResource(R.drawable.listen_ques_stop);
                mTimer = new Timer();
                mTimer.schedule(new ListenAbilityTestActivity.RequestTimerTask(), 0, 10);
            }
        });

        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
//                    userAns[index] = "A";
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (listenQues.get(index - 1).answer.equals("A")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    switch (listenQues.get(index - 1).category) {
                        case "准确辨音":
                            score1++;
                            break;
                        case "听能逻辑":
                            score2++;
                            break;
                        case "音义匹配":
                            score3++;
                            break;
                        case "听写":
                            score4++;
                            break;
                    }
                    completeFlag = true;
                }
            }
        });

        lB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
//                    userAns[index] = "A";
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (listenQues.get(index - 1).answer.equals("B")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (listenQues.get(index - 1).answer.equals("B")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        lC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
//                    userAns[index] = "A";
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (listenQues.get(index - 1).answer.equals("C")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (listenQues.get(index - 1).answer.equals("C")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        lD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
//                    userAns[index] = "A";
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (listenQues.get(index - 1).answer.equals("D")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (listenQues.get(index - 1).answer.equals("D")) {
                        switch (listenQues.get(index - 1).category) {
                            case "准确辨音":
                                score1++;
                                break;
                            case "听能逻辑":
                                score2++;
                                break;
                            case "音义匹配":
                                score3++;
                                break;
                            case "听写":
                                score4++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        proIECC();
        thread.run();


    }

    private void formatQuesList() {
        List<IntelTestQues> justicVoice = new ArrayList<IntelTestQues>();
        List<IntelTestQues> logic = new ArrayList<IntelTestQues>();
        List<IntelTestQues> match = new ArrayList<IntelTestQues>();
        List<IntelTestQues> dictation = new ArrayList<IntelTestQues>();

        for (int i = 0; i < listenQues.size(); i++) {
            switch (listenQues.get(i).category) {
                case "准确辨音":
                    justicVoice.add(listenQues.get(i));
                    break;
                case "听能逻辑":
                    logic.add(listenQues.get(i));
                    break;
                case "音义匹配":
                    match.add(listenQues.get(i));
                    break;
                case "听写":
                    dictation.add(listenQues.get(i));
                    break;
            }
        }

        mCategoryType[0] = justicVoice.size();
        mCategoryType[1] = logic.size();
        mCategoryType[2] = match.size();
        mCategoryType[3] = dictation.size();

        listenQues.clear();
        listenQues.addAll(justicVoice);
        listenQues.addAll(logic);
        listenQues.addAll(match);
        listenQues.addAll(dictation);
    }

    /***
     * 上传测试结果到大数据
     *
     * @param uid     用户的id
     * @param ability 测试类型id 0写作...
     */
    public void uploadTestRecordToNet(String uid, int ability) {

        String jsonForTestRecord = "";
        ArrayList<TestRecord> mTestRecordList = zdbHelper.getWillUploadTestRecord();//每一个题目
        ArrayList<AbilityResult> mAbilityResultLists = zdbHelper.getAbilityTestRecord(ability, uid, true);//每一项能力结果
        if (mTestRecordList.size() > 0 || mAbilityResultLists.size() > 0) {//有可传数据再上传
            try {
                jsonForTestRecord = JsonUtil.buildJsonForTestRecordDouble(mTestRecordList, mAbilityResultLists, uid);
                LogUtils.e("hhhhhh", "buildJsonForTestRecord" + jsonForTestRecord);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LogUtils.e("执行到的地方测试：", "获取将要上传的做题记录！！！！！！！");
            //  jsonForTestRecord = URLEncoder.encode(jsonForTestRecord, "UTF-8").substring(jsonForTestRecord.indexOf("{"), jsonForTestRecord.lastIndexOf("}") + 1);

            String url = Constant.url_updateExamRecord();
            UploadTestRecordRequest up = new UploadTestRecordRequest(jsonForTestRecord, url);

            String result = up.getResultByName("result");
            String jifen = up.getResultByName("jiFen");
            LogUtils.e("积分:" + jifen + "结果   " + result);

            Message mg = new Message();
            mg.obj = jifen;
            mg.what = 8;
            handler.sendMessage(mg);

            TestRecord testRecords;
            AbilityResult aResult;
            if (!result.equals("-1") && !result.equals("-2")) {// 成功
                int size = mTestRecordList.size();
                for (int i = 0; i < size; i++) {
                    testRecords = (TestRecord) mTestRecordList.toArray()[i];
                    //mZDBHelper.setTestRecordIsUpload(testRecords.TestNumber);
                    zdbHelper.setTestRecordIsUpload(testRecords.TestNumber);
                }
                for (int i = 0; i < mAbilityResultLists.size(); i++) {
                    aResult = (AbilityResult) mAbilityResultLists.toArray()[i];
                    zdbHelper.setAbilityResultIsUpload(aResult.TestId);
                }
            }
        } else {
            LogUtils.e("没有数据上传服务器");
        }
    }


    /**
     * 转化为存储到数据表的格式
     *
     * @param id 与score 对应的 index
     * @return s
     */
    private String parseSavePattern(int id) {
        return mCategoryType[id] + "++" + rightNum[id] + "++" + mTestTypeArr[id];
    }


    private void gotoResultActivity() {

        int doright = 0;
        rightNum[0] = score1;
        rightNum[1] = score2;
        rightNum[2] = score3;
        rightNum[3] = score4;
        rightNum[4] = score5;
        rightNum[5] = score6;
        String endTime = deviceInfo.getCurrentTime();
        for (int i = 0; i < rightNum.length; i++) {
            doright = doright + rightNum[i];
        }
        //保存数据库  Score存储的方式--该类型的总题数++答对的题目总数
        final AbilityResult abilityResult = new AbilityResult();
        abilityResult.TypeId = Constant.ABILITY_TETYPE_LISTEN;
        abilityResult.Score1 = parseSavePattern(TYPE_JUSTIC_SOUND);//mCategoryType[TYPE_SINGLE] + "++" + rightNum[TYPE_SINGLE] + "++" + "单项选择";
        abilityResult.Score2 = parseSavePattern(TYPE_LOGIC);//mCategoryType[TYPE_MULTY] + "++" + rightNum[TYPE_MULTY] + "++" + "多项选择";
        abilityResult.Score3 = parseSavePattern(TYPE_MATCH);//mCategoryType[TYPE_JUDGE] + "++" + rightNum[TYPE_JUDGE] + "++" + "判断正误";
        abilityResult.Score4 = parseSavePattern(TYPE_DICTATION);//mCategoryType[TYPE_BLANK_SHORT] + "++" + rightNum[TYPE_BLANK_SHORT] + "++" + "简答";
        abilityResult.Score5 = "-1";//mCategoryType[TYPE_BLANK_COMPLETE] + "++" + rightNum[TYPE_BLANK_COMPLETE] + "++" + "完成句子";
        abilityResult.Score6 = "-1";//mCategoryType[TYPE_BLANK_INFO] + "++" + rightNum[TYPE_BLANK_INFO] + "++" + "信息填空";
        abilityResult.Score7 = "-1";//mCategoryType[TYPE_BLANK_LISTEN] + "++" + rightNum[TYPE_BLANK_LISTEN] + "++" + "听写句子";
        abilityResult.DoRight = doright;
        abilityResult.UndoNum = undoNum;
        abilityResult.beginTime = beginTime;
        abilityResult.Total = quesTotalNo;
        abilityResult.endTime = endTime;
        abilityResult.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                zdbHelper.seveTestRecord(abilityResult);

                uploadTestRecordToNet(mTestRecord.uid, Constant.ABILITY_TETYPE_LISTEN);
            }
        }).start();


//        handler.sendEmptyMessage(SAVERESULT);
        Intent intent = new Intent();
        intent.putExtra("testType", Constant.ABILITY_TETYPE_LISTEN);
        intent.putExtra("totalNum", quesTotalNo);//试题的总数
        intent.setClass(getApplicationContext(), AbilityTestResultActivity.class);
        this.finish();
        startActivity(intent);
    }


    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            totalTime--;
            handler.sendEmptyMessageDelayed(4, 1000);
        }
    };


    class RequestTimerTask extends TimerTask {
        @Override
        public void run() {
            if (player.isPlaying() && flag == false) {
                flag = true;
            }
            if (player.isPlaying() && flag) {
                duration = player.getDuration();
                position = player.mediaPlayer.getCurrentPosition();
                rp.setCricleProgressColor(0xfffea523);
                rp.setMax((int) (duration));
                rp.setProgress((int) (position));
            }
            if (!player.isPlaying() && flag) {
//                handler.removeMessages(1);
                handler.sendEmptyMessageDelayed(1, 50);
                flag = false;
//                rp.setProgress(0);
//                rp.setBackgroundResource(R.drawable.listen_ques_play);
            }
        }
    }

    private void proIECC() {
//        quesNo.setText(String.valueOf(index + 1) + "/60");
//        voiceImp.setText(String.valueOf(point));

//        handler.sendEmptyMessage(1);
        mTestRecord.BeginTime = deviceInfo.getCurrentTime();
        mTestRecord.LessonId = listenQues.get(index).id + "";
        mTestRecord.TestNumber = Integer.valueOf(listenQues.get(index).testId);
        mTestRecord.UserAnswer = "";
        mTestRecord.RightAnswer = listenQues.get(index).answer.trim();
        mTestRecord.category = listenQues.get(index).category;

        rp.setBackgroundResource(R.drawable.listen_ques_play);
        if (player.isPlaying()) {
            player.pause();
            rp.setProgress(0);
        }

        et.setText("");
        question.setText(String.valueOf(index + 1) + "." + listenQues.get(index).question);
        question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        question.setPadding(5, 0, 0, 0);

        if (listenQues.get(index).testType.equals("1")) {
            Log.e("choiceD", listenQues.get(index).choiceD);
            if (listenQues.get(index).choiceD.equals("")) {

                lD.setVisibility(View.GONE);
                imageA.setVisibility(View.GONE);
                imageB.setVisibility(View.GONE);
                imageC.setVisibility(View.GONE);
                imageD.setVisibility(View.GONE);

                textA.setVisibility(View.VISIBLE);
                textB.setVisibility(View.VISIBLE);
                textC.setVisibility(View.VISIBLE);
                textD.setVisibility(View.VISIBLE);
                et.setVisibility(View.GONE);
                if (listenQues.get(index).choiceA.contains("[[")) {
                    textA.setText(emphasizeWord(listenQues.get(index).choiceA));
                } else {
                    textA.setText(listenQues.get(index).choiceA);
                }

                if (listenQues.get(index).choiceB.contains("[[")) {
                    textB.setText(emphasizeWord(listenQues.get(index).choiceB));
                } else {
                    textB.setText(listenQues.get(index).choiceB);
                }

                if (listenQues.get(index).choiceC.contains("[[")) {
                    textC.setText(emphasizeWord(listenQues.get(index).choiceC));
                } else {
                    textC.setText(listenQues.get(index).choiceC);
                }

                if (listenQues.get(index).choiceD.contains("[[")) {
                    textD.setText(emphasizeWord(listenQues.get(index).choiceD));
                } else {
                    textD.setText(listenQues.get(index).choiceD);
                }

            } else {
                lD.setVisibility(View.VISIBLE);
                imageA.setVisibility(View.GONE);
                imageB.setVisibility(View.GONE);
                imageC.setVisibility(View.GONE);
                imageD.setVisibility(View.GONE);
                textA.setVisibility(View.VISIBLE);
                textB.setVisibility(View.VISIBLE);
                textC.setVisibility(View.VISIBLE);
                textD.setVisibility(View.VISIBLE);
                et.setVisibility(View.GONE);

                if (listenQues.get(index).choiceA.contains("[[")) {
                    textA.setText(emphasizeWord(listenQues.get(index).choiceA));
                } else {
                    textA.setText(listenQues.get(index).choiceA);
                }

                if (listenQues.get(index).choiceB.contains("[[")) {
                    textB.setText(emphasizeWord(listenQues.get(index).choiceB));
                } else {
                    textB.setText(listenQues.get(index).choiceB);
                }

                if (listenQues.get(index).choiceC.contains("[[")) {
                    textC.setText(emphasizeWord(listenQues.get(index).choiceC));
                } else {
                    textC.setText(listenQues.get(index).choiceC);
                }

                if (listenQues.get(index).choiceD.contains("[[")) {
                    textD.setText(emphasizeWord(listenQues.get(index).choiceD));
                } else {
                    textD.setText(listenQues.get(index).choiceD);
                }
            }
        } else if (listenQues.get(index).testType.equals("4")) {
            lD.setVisibility(View.VISIBLE);
            imageA.setVisibility(View.VISIBLE);
            imageB.setVisibility(View.VISIBLE);
            imageC.setVisibility(View.VISIBLE);
            imageD.setVisibility(View.VISIBLE);
            textA.setVisibility(View.GONE);
            textB.setVisibility(View.GONE);
            textC.setVisibility(View.GONE);
            textD.setVisibility(View.GONE);
            et.setVisibility(View.GONE);

//            int resId = getResources().getIdentifier(listenQues.get(index).choiceA, "drawable", getPackageName());
//            imageA.setImageResource(resId);
//            resId = getResources().getIdentifier(listenQues.get(index).choiceB, "drawable", getPackageName());
//            imageB.setImageResource(resId);
//            resId = getResources().getIdentifier(listenQues.get(index).choiceC, "drawable", getPackageName());
//            imageC.setImageResource(resId);
//            resId = getResources().getIdentifier(listenQues.get(index).choiceD, "drawable", getPackageName());
//            imageD.setImageResource(resId);
            String mLocalAudioPrefix = Constant.envir + "audio/";
            String au = mLocalAudioPrefix + listenQues.get(index).choiceA;
            Bitmap bm = BitmapFactory.decodeFile(au);
            imageA.setImageBitmap(bm);

            au = mLocalAudioPrefix + listenQues.get(index).choiceB;
            bm = BitmapFactory.decodeFile(au);
            imageB.setImageBitmap(bm);

            au = mLocalAudioPrefix + listenQues.get(index).choiceC;
            bm = BitmapFactory.decodeFile(au);
            imageC.setImageBitmap(bm);

            au = mLocalAudioPrefix + listenQues.get(index).choiceD;
            bm = BitmapFactory.decodeFile(au);
            imageD.setImageBitmap(bm);

        } else if (listenQues.get(index).testType.equals("2")) {
            lA.setVisibility(View.GONE);
            lB.setVisibility(View.GONE);
            lC.setVisibility(View.GONE);
            lD.setVisibility(View.GONE);

            et.setVisibility(View.VISIBLE);
        }

        quesIndex.setText(String.valueOf(index + 1) + "/" + String.valueOf(quesTotalNo));
        setChoicesBack(0);

        index++;

        if (index == quesTotalNo)
            nextQues.setText("完 成");
    }

    private SpannableStringBuilder emphasizeWord(String word) {
        int start = word.indexOf("[[");
        int end = word.indexOf("]]");
        String subWord = word.substring(start + 2, end);
        word = word.substring(0, start) + subWord + word.substring(end + 2, word.length());
        SpannableStringBuilder words = new SpannableStringBuilder(word);
        words.setSpan(new ForegroundColorSpan(0xfffea523), word.indexOf(subWord),
                word.indexOf(subWord) + subWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return words;
    }

    /**
     * @param index 0为清除，1为A项加深，2为B项加深，以此类推
     */
    private void setChoicesBack(int index) {
        switch (index) {
            case 0:
                lA.setBackgroundColor(0x00ffffff);
                lB.setBackgroundColor(0x00ffffff);
                lC.setBackgroundColor(0x00ffffff);
                lD.setBackgroundColor(0x00ffffff);
                break;
            case 1:
                lA.setBackgroundColor(0x80dce0d5);
                lB.setBackgroundColor(0x00ffffff);
                lC.setBackgroundColor(0x00ffffff);
                lD.setBackgroundColor(0x00ffffff);
                break;
            case 2:
                lA.setBackgroundColor(0x00ffffff);
                lB.setBackgroundColor(0x80dce0d5);
                lC.setBackgroundColor(0x00ffffff);
                lD.setBackgroundColor(0x00ffffff);
                break;
            case 3:
                lA.setBackgroundColor(0x00ffffff);
                lB.setBackgroundColor(0x00ffffff);
                lC.setBackgroundColor(0x80dce0d5);
                lD.setBackgroundColor(0x00ffffff);
                break;
            case 4:
                lA.setBackgroundColor(0x00ffffff);
                lB.setBackgroundColor(0x00ffffff);
                lC.setBackgroundColor(0x00ffffff);
                lD.setBackgroundColor(0x80dce0d5);
                break;
        }
    }

    private void saveUserChoices(int type) {
        switch (type) {
            case 1:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (listenQues.get(index - 1).answer.equals("A")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "A";
                handler.sendEmptyMessage(7);
                break;
            case 2:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (listenQues.get(index - 1).answer.equals("B")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "B";
                handler.sendEmptyMessage(7);
                break;
            case 3:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (listenQues.get(index - 1).answer.equals("C")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "C";
                handler.sendEmptyMessage(7);
                break;
            case 4:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (listenQues.get(index - 1).answer.equals("D")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "D";
                handler.sendEmptyMessage(7);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showAlertDialog();
        showAlertDialog(index, quesTotalNo, "听力", zdbHelper, listenQues);
    }

    /**
     * alertdialog 提示用户是继续还是退出测试
     *
     * @param cur   当前进度
     * @param total 试题总数
     */
    public void showAlertDialog(final int cur, int total, final String type, final AbilityTestRecordOp zdbHelper, final List<IntelTestQues> mQuesList) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("提示:");
        dialog.setMessage("测试进度:" + cur + "/" + total + ",是否放弃测试?");
        dialog.setPositiveButton("离开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pidition) {
                ToastUtil.showToast(mContext, type + "测试未完成");
                //已经保存的数据库记录需要标记一下,不上传服务器
                for (int i = 0; i < cur - 1; i++) {
                    zdbHelper.setTestRecordIsUpload(Integer.valueOf(mQuesList.get(i).testId));
                }

                finish();
            }
        });
        dialog.setNegativeButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    rp.setProgress(0);
                    rp.setBackgroundResource(R.drawable.listen_ques_play);
//                    rp.setCricleProgressColor(0xfff5f5f5);
//                    rp.setMax(100);
                    Log.e("progressbar reset", "reset");
                    break;
                case 2:
                    rp.setBackgroundResource(R.drawable.listen_ques_stop);
                    break;
                case 3:

                    break;
                case 4:
                    pb.incrementProgressBy(-1);
                    String a = String.valueOf(totalTime / 60);
                    String b = String.valueOf(totalTime % 60);
                    timeUsed.setText(a + "m " + b + "s");
                    if (totalTime != 0)
                        thread.run();
                    else if (!ListenAbilityTestActivity.this.isFinishing()) {
                        new AlertDialog.Builder(mContext).setTitle("不好意思，您的时间用光啦︶︿︶").setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                undoNum = undoNum + quesTotalNo - index;
                                gotoResultActivity();
                            }
                        })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“确认”后的操作
                                        undoNum = undoNum + quesTotalNo - index;
                                        gotoResultActivity();
                                    }
                                }).show();
                    }
                    break;
                case 5:
                    proIECC();
                    break;
                case 7:
                    zdbHelper.saveTestRecord(mTestRecord);
                    break;
                case 8:
                    String jifen = (String) msg.obj;
                    if (Integer.parseInt(jifen) > 0) {
                        ToastUtil.showToast(mContext, "测评数据成功同步到云端 +" + jifen + "积分");
                    }
                    break;
            }
        }
    };


}

