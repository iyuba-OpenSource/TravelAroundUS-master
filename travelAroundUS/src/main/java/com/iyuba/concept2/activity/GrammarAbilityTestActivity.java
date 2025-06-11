package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

/**
 * Created by Administrator on 2016/9/30.
 */

public class GrammarAbilityTestActivity extends BasisActivity {

    private Context mContext = this;
    private ArrayList<IntelTestQues> grammarQues;

    private int index = 0, quesTotalNo = 0;
    private TextView question, A, B, C, D, timeNum, quesNo, testTotalTime,quesInstruction;
    private Button notKnownButton;
    private ImageView wordImage, wordPlay;
    private Player player;
    private LinearLayout ansChoices, chosnChar;
    private RelativeLayout ansA, ansB, ansC, ansD, choicesr, keyboardr;
    private RoundProgressBar wordRead;
    private ProgressBar pb;
    private int totalTime = 900;
    private boolean completeFlag = false;
    private GetDeviceInfo deviceInfo;
    private AbilityTestRecordOp zdbHelper;
    private int rightNum[] = {0, 0, 0, 0, 0, 0};
    private int[] mCategory = new int[6];
    private String[] mTestTypeArr = Constant.GRAM_ABILITY_ARR;//能力测评的维度
    private TestRecord mTestRecord;
    /**
     * 题目的类型   0--实词  1--虚词  2--引语  3--被动语态  4--句子  5 --时态
     */

    private final int TYPE_MINGCI = 0;//名词
    private final int TYPE_DAICI = 1;//代词
    private final int TYPE_XINGRONGCIFUCI = 2;//形容词副词
    private final int TYPE_DONGCI = 3;//动词
    private final int TYPE_SHITAI = 4;//时态
    private final int TYPE_JUZI = 5;//句子

    private int score1 = 0, score2 = 0, score3 = 0, score4 = 0, score5 = 0, score6 = 0, undoNum = 0;
    private String beginTime, endTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_ability_test);

        mTestRecord = new TestRecord();
        beginTime = df.format(new Date());// new Date()为获取当前系统时间
        grammarQues = new ArrayList<>();
        grammarQues = (ArrayList<IntelTestQues>) getIntent().getSerializableExtra("QuestionList");
        quesTotalNo = grammarQues.size();
        deviceInfo = new GetDeviceInfo(mContext);
        zdbHelper = new AbilityTestRecordOp(mContext);

        timeNum = (TextView) findViewById(R.id.time_num);
        quesNo = (TextView) findViewById(R.id.ques_no);
        question = (TextView) findViewById(R.id.ques_word);
        wordImage = (ImageView) findViewById(R.id.word_img);
        wordPlay = (ImageView) findViewById(R.id.word_play);
        testTotalTime = (TextView) findViewById(R.id.intel_test_total_time);
        A = (TextView) findViewById(R.id.ans_A);
        B = (TextView) findViewById(R.id.ans_B);
        C = (TextView) findViewById(R.id.ans_C);
        D = (TextView) findViewById(R.id.ans_D);
        ansA = (RelativeLayout) findViewById(R.id.ans_ar);
        ansB = (RelativeLayout) findViewById(R.id.ans_br);
        ansC = (RelativeLayout) findViewById(R.id.ans_cr);
        ansD = (RelativeLayout) findViewById(R.id.ans_dr);
        choicesr = (RelativeLayout) findViewById(R.id.ans_choicesr);
        keyboardr = (RelativeLayout) findViewById(R.id.keyboardr);
        quesInstruction = (TextView) findViewById(R.id.ques_instruction);
        ansChoices = (LinearLayout) findViewById(R.id.ans_choices);
        wordRead = (RoundProgressBar) findViewById(R.id.word_read);
        notKnownButton = (Button) findViewById(R.id.not_known_button);
        chosnChar = (LinearLayout) findViewById(R.id.chosen_char);
        pb = (ProgressBar) findViewById(R.id.time_line);

        wordImage.setVisibility(View.GONE);
        wordPlay.setVisibility(View.GONE);
        question.setVisibility(View.VISIBLE);
        ansChoices.setVisibility(View.VISIBLE);
        wordRead.setVisibility(View.GONE);
        choicesr.setVisibility(View.VISIBLE);
        keyboardr.setVisibility(View.GONE);
        quesInstruction.setVisibility(View.GONE);

        mTestRecord.uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        //答题记录  共性的
        mTestRecord.appId = Constant.APPID;
        mTestRecord.index = 1;
        mTestRecord.deviceId = deviceInfo.getLocalMACAddress();
        mTestRecord.testMode = Constant.ABILITY_GRAMMER;

        totalTime = getIntent().getIntExtra("totalTime", 0);
        testTotalTime.setText(String.valueOf(totalTime)+"m");
        timeNum.setText(String.valueOf(totalTime)+"m 00s");
        totalTime = totalTime * 60;

        pb.setMax(totalTime);
        pb.setProgress(totalTime);

        notKnownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    zdbHelper.saveTestRecord(mTestRecord);
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

        ansA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (grammarQues.get(index - 1).answer.equals("A")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }
                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (grammarQues.get(index - 1).answer.equals("A")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        ansB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (grammarQues.get(index - 1).answer.equals("B")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }
                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (grammarQues.get(index - 1).answer.equals("B")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        ansC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (grammarQues.get(index - 1).answer.equals("C")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }
                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (grammarQues.get(index - 1).answer.equals("C")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        ansD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < quesTotalNo) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (grammarQues.get(index - 1).answer.equals("D")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }
                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (grammarQues.get(index - 1).answer.equals("D")) {
                        switch (grammarQues.get(index - 1).category) {
                            case "名词":
                                score1++;
                                break;
                            case "代词":
                                score2++;
                                break;
                            case "形容词副词":
                                score3++;
                                break;
                            case "动词":
                                score4++;
                                break;
                            case "时态":
                                score5++;
                                break;
                            case "句子":
                                score6++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });
        handler.sendEmptyMessage(5);
        thread.run();
    }

    /**
     * @param index 0为清除，1为A项加深，2为B项加深，以此类推
     */
    private void setChoicesBack(int index) {
        switch (index) {
            case 0:
                A.setBackgroundColor(0x00ffffff);
                B.setBackgroundColor(0x00ffffff);
                C.setBackgroundColor(0x00ffffff);
                D.setBackgroundColor(0x00ffffff);
                break;
            case 1:
                A.setBackgroundColor(0x80dce0d5);
                B.setBackgroundColor(0x00ffffff);
                C.setBackgroundColor(0x00ffffff);
                D.setBackgroundColor(0x00ffffff);
                break;
            case 2:
                A.setBackgroundColor(0x00ffffff);
                B.setBackgroundColor(0x80dce0d5);
                C.setBackgroundColor(0x00ffffff);
                D.setBackgroundColor(0x00ffffff);
                break;
            case 3:
                A.setBackgroundColor(0x00ffffff);
                B.setBackgroundColor(0x00ffffff);
                C.setBackgroundColor(0x80dce0d5);
                D.setBackgroundColor(0x00ffffff);
                break;
            case 4:
                A.setBackgroundColor(0x00ffffff);
                B.setBackgroundColor(0x00ffffff);
                C.setBackgroundColor(0x00ffffff);
                D.setBackgroundColor(0x80dce0d5);
                break;
        }
    }

    private void proIECC() {
        mTestRecord.BeginTime = deviceInfo.getCurrentTime();
        mTestRecord.LessonId = grammarQues.get(index).id + "";
        mTestRecord.TestNumber = Integer.valueOf(grammarQues.get(index).testId);
        mTestRecord.UserAnswer = "";
        mTestRecord.RightAnswer = grammarQues.get(index).answer;
        mTestRecord.category = grammarQues.get(index).category;

        quesNo.setText(String.valueOf(index + 1) + "/" + String.valueOf(quesTotalNo));
        question.setText(grammarQues.get(index).question);
        question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
        question.setPadding(5, 0, 0, 0);

        A.setText(grammarQues.get(index).choiceA);
        B.setText(grammarQues.get(index).choiceB);
        C.setText(grammarQues.get(index).choiceC);
        D.setText(grammarQues.get(index).choiceD);

        switch (grammarQues.get(index).category){
            case "名词":
                mCategory[0]++;
                break;
            case "代词":
                mCategory[1]++;
                break;
            case "形容词副词":
                mCategory[2]++;
                break;
            case "动词":
                mCategory[3]++;
                break;
            case "时态":
                mCategory[4]++;
                break;
            case "句子":
                mCategory[5]++;
                break;
        }

        setChoicesBack(0);

        index++;

        if (index == quesTotalNo)
            notKnownButton.setText("完 成");
    }

    private void saveUserChoices(int type) {
        switch (type) {
            case 1:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (grammarQues.get(index - 1).answer.equals("A")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "A";
                handler.sendEmptyMessage(7);
                break;
            case 2:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (grammarQues.get(index - 1).answer.equals("B")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "B";
                handler.sendEmptyMessage(7);
                break;
            case 3:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (grammarQues.get(index - 1).answer.equals("C")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "C";
                handler.sendEmptyMessage(7);
                break;
            case 4:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (grammarQues.get(index - 1).answer.equals("D")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "D";
                handler.sendEmptyMessage(7);
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


    private void gotoResultActivity() {
        rightNum[0]=score1;
        rightNum[1]=score2;
        rightNum[2]=score4;
        rightNum[3]=score3;
        rightNum[4]=score5;
        rightNum[5]=score6;
        String resulttosave = "";
        int totalnum = 0;
        String endTime = deviceInfo.getCurrentTime();  //获取系统当前时间,作为测试是按保存
        for (int i = 0; i < rightNum.length; i++) {
            totalnum = totalnum + rightNum[i];
            // resulttosave = resulttosave + "+++" + totalnum + "+++" + endTime;//前面6个是单词部分的评测结果,接着一个是总数,然后是测试时间
        }

        //保存数据库
        final AbilityResult abilityResult = new AbilityResult();
        abilityResult.TypeId = Constant.ABILITY_TETYPE_GRAMMER;
        abilityResult.Score1 = parseSavePattern(TYPE_MINGCI);
        abilityResult.Score2 = parseSavePattern(TYPE_DAICI);
        abilityResult.Score3 = parseSavePattern(TYPE_XINGRONGCIFUCI);
        abilityResult.Score4 = parseSavePattern(TYPE_DONGCI);
        abilityResult.Score5 = parseSavePattern(TYPE_SHITAI);
        abilityResult.Score6 = parseSavePattern(TYPE_JUZI);
        abilityResult.Score7 = "-1";
        abilityResult.DoRight = totalnum;
        abilityResult.UndoNum = undoNum;
        abilityResult.Total = quesTotalNo;
        abilityResult.beginTime = beginTime;
        abilityResult.endTime = endTime;
        abilityResult.uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        //上传到服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                zdbHelper.seveTestRecord(abilityResult);
                uploadTestRecordToNet(mTestRecord.uid, Constant.ABILITY_TETYPE_GRAMMER);
            }
        }).start();

//        zdbHelper.seveTestRecord(abilityResult);

        Intent intent = new Intent();
        intent.putExtra("testType", Constant.ABILITY_TETYPE_GRAMMER);
        intent.putExtra("totalNum", quesTotalNo);//试题的总数
        intent.setClass(getApplicationContext(), AbilityTestResultActivity.class);
        this.finish();
        startActivity(intent);

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

    @Override
    public void onBackPressed() {
        //showAlertDialog();
        showAlertDialog(index, quesTotalNo, "语法", zdbHelper, grammarQues);
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

    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            totalTime--;
            handler.sendEmptyMessageDelayed(4, 1000);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RoundProgressBar tempBar = null;
            switch (msg.what) {
                case 0:
                    String url = (String) msg.obj;
                    player = new Player(mContext, null);
                    player.playUrl(url);
                    break;
                case 2:// set the read button progress bar with value of voice
                    // volume
                    try {
                        tempBar = wordRead;
                        int db = msg.arg1;
                        // Log.e(TAG, "sound DB value: " + db);
                        tempBar.setCricleProgressColor(0xff87c973);
                        tempBar.setMax(100);
                        tempBar.setProgress(db);
                    } catch (Exception e) {
                        Log.e("val", "handler.case2");
                    }
                    break;
                case 3: // reset the read button progress bar
                    try {
                        wordRead
                                .setBackgroundResource(R.drawable.sen_i_read);
                        tempBar = wordRead;
                        tempBar.setCricleProgressColor(0xff87c973);
                        tempBar.setMax(100);
                        tempBar.setProgress(0);
                    } catch (Exception e) {
                        Log.e("val", "handler.case3");
                    }
                    break;
                case 4:
                    pb.incrementProgressBy(-1);
                    String a = String.valueOf(totalTime / 60);
                    String b = String.valueOf(totalTime % 60);
                    timeNum.setText(a + "m " + b + "s");
                    if (totalTime != 0)
                        thread.run();
                    else if (!GrammarAbilityTestActivity.this.isFinishing()) {
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
