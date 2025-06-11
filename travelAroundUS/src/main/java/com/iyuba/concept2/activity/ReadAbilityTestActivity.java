package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.UploadTestRecordRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.FileUtil;
import com.iyuba.concept2.util.JsonUtil;
import com.iyuba.concept2.widget.RoundProgressBar;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */

public class ReadAbilityTestActivity extends BasisActivity {
    private TextView question, textA, textB, textC, textD, nextQues, timeUsed, quesIndex,testTotalTime;
    private Context mContext = this;
    private List<IntelTestQues> readQues;
    private int index = 0, quesTotalNo = 0;
    private LinearLayout lA, lB, lC, lD;
    private int totalTime = 600;
    private ProgressBar pb;
    private boolean completeFlag = false;
    private GetDeviceInfo deviceInfo;
    private AbilityTestRecordOp zdbHelper;
    private int rightNum[] = {0, 0, 0, 0};
    private int[] mCategory = new int[4];//测试题的种类
    private String[] mTestTypeArr = Constant.READ_ABILITY_ARR;
    private int mTestType;
    private int TYPE_WORD = 0;
    private int TYPE_SENTENCE = 1;
    private int TYPE_LOGIC = 2;
    private int TYPE_ARTICLE = 3;

    private int score1 = 0, score2 = 0, score3 = 0, score4 = 0, score5 = 0, score6 = 0, undoNum = 0;
    private String beginTime, endTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private AbilityResult abilityResult = new AbilityResult();
    private TestRecord mTestRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_ability_test);

        nextQues = (TextView) findViewById(R.id.next_w_ques);
        timeUsed = (TextView) findViewById(R.id.time_used);
        quesIndex = (TextView) findViewById(R.id.ques_index);
        testTotalTime = (TextView) findViewById(R.id.intel_test_total_time);

        deviceInfo = new GetDeviceInfo(mContext);
        zdbHelper = new AbilityTestRecordOp(mContext);

        beginTime = df.format(new Date());// new Date()为获取当前系统时间

        question = (TextView) findViewById(R.id.write_ques);
        textA = (TextView) findViewById(R.id.A_w_text);
        textB = (TextView) findViewById(R.id.B_w_text);
        textC = (TextView) findViewById(R.id.C_w_text);
        textD = (TextView) findViewById(R.id.D_w_text);

        lA = (LinearLayout) findViewById(R.id.ans_w_A);
        lB = (LinearLayout) findViewById(R.id.ans_w_B);
        lC = (LinearLayout) findViewById(R.id.ans_w_C);
        lD = (LinearLayout) findViewById(R.id.ans_w_D);

        pb = (ProgressBar) findViewById(R.id.w_ques_progress);

        readQues = new ArrayList<IntelTestQues>();
        readQues = (ArrayList<IntelTestQues>) getIntent().getSerializableExtra("QuestionList");
        formatQuesList();
        quesTotalNo = readQues.size();
        mTestRecord = new TestRecord();

        mTestRecord.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        //答题记录  共性的
        mTestRecord.appId = Constant.APPID;
        mTestRecord.index = 1;
        mTestRecord.deviceId = deviceInfo.getLocalMACAddress();
        mTestRecord.testMode = Constant.ABILITY_READ;

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
                    undoNum++;
                    proIECC();
                } else {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    zdbHelper.saveTestRecord(mTestRecord);
                    if (completeFlag)
                        gotoResultActivity();
                    else {
                        undoNum++;
                        gotoResultActivity();
                    }
                }
            }
        });


        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
//                    userAns[index] = "A";
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (readQues.get(index - 1).answer.equals("A")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (readQues.get(index - 1).answer.equals("A")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
                                score4++;
                                break;
                        }
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
                    if (readQues.get(index - 1).answer.equals("B")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
                                score4++;
                                break;
                        }
                    }

                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (readQues.get(index - 1).answer.equals("B")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
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
                    if (readQues.get(index - 1).answer.equals("C")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (readQues.get(index - 1).answer.equals("C")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
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
                    if (readQues.get(index - 1).answer.equals("D")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (readQues.get(index - 1).answer.equals("D")) {
                        switch (readQues.get(index - 1).category) {
                            case "词汇认知":
                                score1++;
                                break;
                            case "句法理解":
                                score2++;
                                break;
                            case "语义和逻辑":
                                score3++;
                                break;
                            case "语篇":
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
        List<IntelTestQues> word = new ArrayList<IntelTestQues>();
        List<IntelTestQues> sentence = new ArrayList<IntelTestQues>();
        List<IntelTestQues> match = new ArrayList<IntelTestQues>();
        List<IntelTestQues> article = new ArrayList<IntelTestQues>();

        for (int i = 0; i < readQues.size(); i++) {
            switch (readQues.get(i).category) {
                case "词汇认知":
                    word.add(readQues.get(i));
                    break;
                case "句法理解":
                    sentence.add(readQues.get(i));
                    break;
                case "语义和逻辑":
                    match.add(readQues.get(i));
                    break;
                case "语篇":
                    article.add(readQues.get(i));
                    break;
            }
        }

        mCategory[0] = word.size();
        mCategory[1] = sentence.size();
        mCategory[2] = match.size();
        mCategory[3] = article.size();

        readQues.clear();
        readQues.addAll(word);
        readQues.addAll(sentence);
        readQues.addAll(match);
        readQues.addAll(article);
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

    /**
     * 转化为存储到数据表的格式
     *
     * @param id 与score 对应的 index
     * @return s
     */
    private String parseSavePattern(int id) {
        return mCategory[id] + "++" + rightNum[id] + "++" + mTestTypeArr[id];
    }


    private void proIECC() {
//        quesNo.setText(String.valueOf(index + 1) + "/60");
//        voiceImp.setText(String.valueOf(point));
        //答题记录的
        mTestRecord.BeginTime = deviceInfo.getCurrentTime();
        mTestRecord.LessonId = readQues.get(index).id + "";
        mTestRecord.TestNumber = Integer.valueOf(readQues.get(index).testId);
        mTestRecord.UserAnswer = "";
        mTestRecord.RightAnswer = readQues.get(index).answer;
        mTestRecord.category = readQues.get(index).category;

        String mLocalAudioPrefix = Constant.envir + "audio/";
        if (readQues.get(index).attach.equals("")) {
            question.setText(readQues.get(index).question);
        } else {
            String quesAll = FileUtil.getTextFromFile(mLocalAudioPrefix + readQues.get(index).attach) + "\n" + readQues.get(index).question;
            question.setText(quesAll);
        }
        question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        question.setPadding(5, 0, 0, 0);

        textA.setText(readQues.get(index).choiceA);
        textB.setText(readQues.get(index).choiceB);
        textC.setText(readQues.get(index).choiceC);
        textD.setText(readQues.get(index).choiceD);

        quesIndex.setText(String.valueOf(index + 1) + "/" + quesTotalNo);
        setChoicesBack(0);

        index++;

        if (index == quesTotalNo) {
            nextQues.setText("完 成");
        }
    }

    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            totalTime--;
            handler.sendEmptyMessageDelayed(4, 1000);
        }
    };

    private void gotoResultActivity() {
        int totalrightnum = 0;
        rightNum[0] = score1;
        rightNum[1] = score2;
        rightNum[2] = score3;
        rightNum[3] = score4;
        String endTime = deviceInfo.getCurrentTime();
                   /* for (int i = 0; i < rightNum.length; i++) {
                        totalnum = totalnum + rightNum[i];
                    }*/
        for (int x : rightNum) {
            totalrightnum = totalrightnum + x;
        }
        //成绩分析保存数据库
        abilityResult.TypeId = Constant.ABILITY_TETYPE_READ;
        //Score字段格式   小项能力类型题目总数++正确的个数++能力类型
        abilityResult.Score1 = parseSavePattern(TYPE_WORD);//mCategory[TYPE_DANXUAN] + "++" + rightNum[TYPE_DANXUAN] + "++" + mCategoryArray[TYPE_DANXUAN];
        abilityResult.Score2 = parseSavePattern(TYPE_SENTENCE);//mCategory[TYPE_PANDUAN] + "++" + rightNum[TYPE_PANDUAN] + "++" + mCategoryArray[TYPE_PANDUAN];
        abilityResult.Score3 = parseSavePattern(TYPE_LOGIC);//mCategory[TYPE_BIAOTIDUIYIN] + "++" + rightNum[TYPE_BIAOTIDUIYIN] + "++" + mCategoryArray[TYPE_BIAOTIDUIYIN];
        abilityResult.Score4 = parseSavePattern(TYPE_ARTICLE);//mCategory[TYPE_PEIDUI] + "++" + rightNum[TYPE_PEIDUI] + "++" + mCategoryArray[TYPE_PEIDUI];
        abilityResult.Score5 = "-1";//mCategory[TYPE_JIANDA] + "++" + rightNum[TYPE_JIANDA] + "++" + mCategoryArray[TYPE_JIANDA];
        abilityResult.Score6 = "-1";//mCategory[TYPE_WANCHENGJUZI] + "++" + rightNum[TYPE_WANCHENGJUZI] + "++" + mCategoryArray[TYPE_WANCHENGJUZI];
        abilityResult.Score7 = "-1";
        abilityResult.DoRight = totalrightnum;//正确个数
        abilityResult.Total = quesTotalNo;//题目总数
        abilityResult.UndoNum = undoNum;//没有回答的个数
        abilityResult.beginTime = beginTime;//测试开始时间
        abilityResult.endTime = endTime;//测试结束时间
        abilityResult.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                zdbHelper.seveTestRecord(abilityResult);

                uploadTestRecordToNet(mTestRecord.uid, Constant.ABILITY_TETYPE_READ);
            }
        }).start();

        Intent intent = new Intent();
        intent.putExtra("testType", 5);
        intent.setClass(getApplicationContext(), AbilityTestResultActivity.class);
        startActivity(intent);
        ReadAbilityTestActivity.this.finish();

    }

    private void saveUserChoices(int type) {
        switch (type) {
            case 1:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (readQues.get(index - 1).answer.equals("A")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "A";
                handler.sendEmptyMessage(7);
                break;
            case 2:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (readQues.get(index - 1).answer.equals("B")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "B";
                handler.sendEmptyMessage(7);
                break;
            case 3:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (readQues.get(index - 1).answer.equals("C")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "C";
                handler.sendEmptyMessage(7);
                break;
            case 4:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (readQues.get(index - 1).answer.equals("D")) {
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
        showAlertDialog(index, quesTotalNo, "阅读", zdbHelper, readQues);
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RoundProgressBar tempBar = null;
            switch (msg.what) {
                case 5:
                    proIECC();
                    break;
                case 4:
                    pb.incrementProgressBy(-1);
                    String a = String.valueOf(totalTime / 60);
                    String b = String.valueOf(totalTime % 60);
                    timeUsed.setText(a + "m " + b + "s");
                    if (totalTime != 0)
                        thread.run();
                    else if (!ReadAbilityTestActivity.this.isFinishing()) {
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
