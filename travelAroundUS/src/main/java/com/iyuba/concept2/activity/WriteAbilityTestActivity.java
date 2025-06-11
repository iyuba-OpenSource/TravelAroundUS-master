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
 * Created by Administrator on 2016/10/10.
 */

public class WriteAbilityTestActivity extends BasisActivity {

    private TextView question, textA, textB, textC, textD, nextQues, timeUsed, quesIndex,testTotalTime;
    private Context mContext = this;
    private List<IntelTestQues> writeQues;
    private int index = 0, quesTotalNo = 0;
    private LinearLayout lA, lB, lC, lD;
    private int totalTime = 600;
    private ProgressBar pb;
    private boolean completeFlag = false;
    private GetDeviceInfo deviceInfo;
    private AbilityTestRecordOp zdbHelper;
    private int rightNum[] = {0, 0, 0, 0};
    private int[] mCategory = new int[4];//测试题的种类
    private String[] mTestTypeArr = Constant.WRITE_ABILITY_ARR;
    private int mTestType;
    private int TYPE_EXPRESS = 0;
    private int TYPE_STRUCTURE = 1;
    private int TYPE_LOGIC = 2;
    private int TYPE_MATERIAL = 3;

    private int score1 = 0, score2 = 0, score3 = 0, score4 = 0, score5 = 0, score6 = 0, undoNum = 0;
    private String beginTime, endTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private TestRecord mTestRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_ability_test);

        beginTime = df.format(new Date());// new Date()为获取当前系统时间

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
        deviceInfo = new GetDeviceInfo(mContext);
        zdbHelper = new AbilityTestRecordOp(mContext);


        pb = (ProgressBar) findViewById(R.id.w_ques_progress);

        writeQues = new ArrayList<IntelTestQues>();
        writeQues = (ArrayList<IntelTestQues>) getIntent().getSerializableExtra("QuestionList");
        formatQuesList();
        quesTotalNo = writeQues.size();

        mTestRecord = new TestRecord();
        mTestRecord.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        //答题记录  共性的
        mTestRecord.appId = Constant.APPID;
        mTestRecord.index = 1;
        mTestRecord.deviceId = deviceInfo.getLocalMACAddress();
        mTestRecord.testMode = Constant.ABILITY_WRITE;

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
                    if (writeQues.get(index - 1).answer.equals("A")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
                                score4++;
                                break;
                        }
                    }

                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (writeQues.get(index - 1).answer.equals("A")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
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
                    if (writeQues.get(index - 1).answer.equals("B")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (writeQues.get(index - 1).answer.equals("B")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
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
                    if (writeQues.get(index - 1).answer.equals("C")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (writeQues.get(index - 1).answer.equals("C")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
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
                    if (writeQues.get(index - 1).answer.equals("D")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (writeQues.get(index - 1).answer.equals("D")) {
                        switch (writeQues.get(index - 1).category) {
                            case "基础表达":
                                score1++;
                                break;
                            case "写作结构":
                                score2++;
                                break;
                            case "写作逻辑":
                                score3++;
                                break;
                            case "写作素材":
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

    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            totalTime--;
            handler.sendEmptyMessageDelayed(4, 1000);
        }
    };

    private void formatQuesList() {
        List<IntelTestQues> express = new ArrayList<IntelTestQues>();
        List<IntelTestQues> structure = new ArrayList<IntelTestQues>();
        List<IntelTestQues> logic = new ArrayList<IntelTestQues>();
        List<IntelTestQues> material = new ArrayList<IntelTestQues>();

        for (int i = 0; i < writeQues.size(); i++) {
            switch (writeQues.get(i).category) {
                case "基础表达":
                    express.add(writeQues.get(i));
                    break;
                case "写作结构":
                    structure.add(writeQues.get(i));
                    break;
                case "写作逻辑":
                    logic.add(writeQues.get(i));
                    break;
                case "写作素材":
                    material.add(writeQues.get(i));
                    break;
            }
        }

        mCategory[0] = express.size();
        mCategory[1] = structure.size();
        mCategory[2] = logic.size();
        mCategory[3] = material.size();

        writeQues.clear();
        writeQues.addAll(express);
        writeQues.addAll(structure);
        writeQues.addAll(logic);
        writeQues.addAll(material);
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

    private void proIECC() {
        //答题记录的
        mTestRecord.BeginTime = deviceInfo.getCurrentTime();
        mTestRecord.LessonId = writeQues.get(index).id + "";
        mTestRecord.TestNumber = Integer.valueOf(writeQues.get(index).testId);
        mTestRecord.UserAnswer = "";
        mTestRecord.RightAnswer = writeQues.get(index).answer;
        mTestRecord.category = writeQues.get(index).category;

        String mLocalAudioPrefix = Constant.envir + "audio/";
        if (writeQues.get(index).attach.equals("")) {
            question.setText(writeQues.get(index).question);
        } else {
            String quesAll = writeQues.get(index).question + "\n" + FileUtil.getTextFromFile(mLocalAudioPrefix + writeQues.get(index).attach);
            question.setText(quesAll);
        }
        question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        question.setPadding(5, 0, 0, 0);

        textA.setText(writeQues.get(index).choiceA);
        textB.setText(writeQues.get(index).choiceB);
        textC.setText(writeQues.get(index).choiceC);
        textD.setText(writeQues.get(index).choiceD);

        quesIndex.setText(String.valueOf(index + 1) + "/" + quesTotalNo);
        setChoicesBack(0);

        index++;

        if (index == quesTotalNo)
            nextQues.setText("完 成");
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

    private void gotoResultActivity() {
        String endTime = deviceInfo.getCurrentTime();
        String resulttosave = "";
        int totalnum = 0;
        rightNum[0] = score1;
        rightNum[1] = score2;
        rightNum[2] = score3;
        rightNum[3] = score4;
        for (int i = 0; i < rightNum.length; i++) {
            totalnum = totalnum + rightNum[i];
        }

        //保存数据库
        final AbilityResult abilityResult = new AbilityResult();
        abilityResult.TypeId = Constant.ABILITY_TETYPE_WRITE;
        abilityResult.Score1 = parseSavePattern(TYPE_EXPRESS);// mCategory[TYPE_BIAODA] + "++" + rightNum[TYPE_BIAODA] + "++" + "写作表达";
        abilityResult.Score2 = parseSavePattern(TYPE_STRUCTURE);//mCategory[TYPE_JIEGOU] + "++" + rightNum[TYPE_JIEGOU] + "++" + "写作结构";
        abilityResult.Score3 = parseSavePattern(TYPE_LOGIC);//mCategory[TYPE_LUOJI] + "++" + rightNum[TYPE_LUOJI] + "++" + "写作逻辑";
        abilityResult.Score4 = parseSavePattern(TYPE_MATERIAL);//mCategory[TYPE_SUCAI] + "++" + rightNum[TYPE_SUCAI] + "++" + "写作素材";
        abilityResult.Score5 = "-1";
        abilityResult.Score6 = "-1";
        abilityResult.Score7 = "-1";
        abilityResult.DoRight = totalnum;
        abilityResult.Total = quesTotalNo;
        abilityResult.UndoNum = undoNum;
        abilityResult.beginTime = beginTime;
        abilityResult.endTime = endTime;
        abilityResult.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                zdbHelper.seveTestRecord(abilityResult);
                uploadTestRecordToNet(mTestRecord.uid, Constant.ABILITY_TETYPE_WRITE);
            }
        }).start();

        Intent intent = new Intent();
        intent.putExtra("testType", 0);
        intent.setClass(getApplicationContext(), AbilityTestResultActivity.class);
        this.finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //showAlertDialog();
        showAlertDialog(index, quesTotalNo, "写作", zdbHelper, writeQues);
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

    private void saveUserChoices(int type) {
        switch (type) {
            case 1:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (writeQues.get(index - 1).answer.equals("A")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "A";
                handler.sendEmptyMessage(7);
                break;
            case 2:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (writeQues.get(index - 1).answer.equals("B")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "B";
                handler.sendEmptyMessage(7);
                break;
            case 3:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (writeQues.get(index - 1).answer.equals("C")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "C";
                handler.sendEmptyMessage(7);
                break;
            case 4:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (writeQues.get(index - 1).answer.equals("D")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "D";
                handler.sendEmptyMessage(7);
                break;
        }
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
                case 4:
                    pb.incrementProgressBy(-1);
                    String a = String.valueOf(totalTime / 60);
                    String b = String.valueOf(totalTime % 60);
                    timeUsed.setText(a + "m " + b + "s");
                    if (totalTime != 0)
                        thread.run();
                    else if (!WriteAbilityTestActivity.this.isFinishing()) {
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
