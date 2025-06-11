package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.IseManager;
import com.iyuba.concept2.protocol.UploadTestRecordRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.FileUtil;
import com.iyuba.concept2.util.JsonUtil;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.concept2.widget.RoundProgressBar;
import com.iyuba.concept2.widget.cdialog.CustomToast;
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

public class SpeakAbilityTestActivity extends BasisActivity {

    /**
     * 题目的类型   0--口语发音  1--口语表达  2--口语素材  3--口语逻辑
     */
    private String[] mTestTypeArr = Constant.SPEAK_ABILITY_ARR;
    private int mTestType;
    private int TYPE_FAYIN = 0;//口语发音
    private int TYPE_BIAODA = 1;//口语表达
    private int TYPE_SUCAI = 2;//口语素材
    private int TYPE_LUOJI = 3;//口语逻辑
    private int[] mCategory = new int[4];//记录每个题型的题目有几个

    private TextView questionIntro, questionContent, textA, textB, textC, textD, nextQues, timeUsed, quesIndex,testTotalTime;
    private Context mContext = this;
    private List<IntelTestQues> speakQues;
    private int index = 0, quesTotalNo = 0;
    private LinearLayout lA, lB, lC, lD, speakChoices;
    private RelativeLayout speakVoice;
    private int totalTime = 600;
    private ProgressBar pb;
    private RoundProgressBar rp;

    private IseManager iseManager;
    private boolean completeFlag = false;

    private AbilityTestRecordOp zdbHelper;
    private GetDeviceInfo deviceInfo;
    private int rightNum[] = {0, 0, 0, 0, 0, 0};

    private int score1 = 0, score2 = 0, score3 = 0, score4 = 0, score5 = 0, score6 = 0, undoNum = 0;
    private String beginTime, endTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private TestRecord mTestRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speak_ability_test);

        beginTime = df.format(new Date());// new Date()为获取当前系统时间
        zdbHelper = new AbilityTestRecordOp(mContext);
        deviceInfo = new GetDeviceInfo(mContext);
        mTestRecord = new TestRecord();

        iseManager = IseManager.getInstance(mContext, handler);

        nextQues = (TextView) findViewById(R.id.next_l_ques);
        timeUsed = (TextView) findViewById(R.id.time_used);
        quesIndex = (TextView) findViewById(R.id.ques_index);
        testTotalTime = (TextView) findViewById(R.id.intel_test_total_time);

        questionIntro = (TextView) findViewById(R.id.speak_ques_introduce);
        questionContent = (TextView) findViewById(R.id.speak_ques_content);
        textA = (TextView) findViewById(R.id.A_l_text);
        textB = (TextView) findViewById(R.id.B_l_text);
        textC = (TextView) findViewById(R.id.C_l_text);
        textD = (TextView) findViewById(R.id.D_l_text);

        lA = (LinearLayout) findViewById(R.id.ans_l_A);
        lB = (LinearLayout) findViewById(R.id.ans_l_B);
        lC = (LinearLayout) findViewById(R.id.ans_l_C);
        lD = (LinearLayout) findViewById(R.id.ans_l_D);
        speakChoices = (LinearLayout) findViewById(R.id.speak_ques_choices);
        speakVoice = (RelativeLayout) findViewById(R.id.speak_ques_speak);

        pb = (ProgressBar) findViewById(R.id.l_ques_progress);
        rp = (RoundProgressBar) findViewById(R.id.sentence_read);

        totalTime = getIntent().getIntExtra("totalTime", 0);
        testTotalTime.setText(String.valueOf(totalTime) + "m");
        timeUsed.setText(String.valueOf(totalTime) + "m 00s");
        totalTime = totalTime * 60;
        pb.setMax(totalTime);
        pb.setProgress(totalTime);

        speakQues = new ArrayList<>();
        speakQues = (ArrayList<IntelTestQues>) getIntent().getSerializableExtra("QuestionList");
        quesTotalNo = speakQues.size();
        formatQuesList();

        mTestRecord.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        //答题记录  共性的
        mTestRecord.appId = Constant.APPID;
        mTestRecord.index = 1;
        mTestRecord.deviceId = deviceInfo.getLocalMACAddress();
        mTestRecord.testMode = Constant.ABILITY_SPEAK;

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
                    if (speakQues.get(index - 1).answer.equals("A")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (speakQues.get(index - 1).answer.equals("A")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
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
                    if (speakQues.get(index - 1).answer.equals("B")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (speakQues.get(index - 1).answer.equals("B")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
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
                    if (speakQues.get(index - 1).answer.equals("C")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (speakQues.get(index - 1).answer.equals("C")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
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
                    if (speakQues.get(index - 1).answer.equals("D")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
                                score4++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (speakQues.get(index - 1).answer.equals("D")) {
                        switch (speakQues.get(index - 1).category) {
                            case "口语发音":
                                score1++;
                                break;
                            case "口语表达":
                                score2++;
                                break;
                            case "口语素材":
                                score3++;
                                break;
                            case "口语逻辑":
                                score4++;
                                break;
                        }
                    }
                    completeFlag = true;
                }
            }
        });

        rp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rp.setClickable(false);//如果正在测评,不要重复用点击
                nextQues.setClickable(false);//测评中,不可点击进下一题
                if (!NetWorkState.isConnectingToInternet()) {
                    CustomToast.showToast(mContext, R.string.alert_net_content,
                            1000);
                    return;
                } else {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    nextQues.setClickable(false);
//                    String a[] = speakQues.get(index - 1).question.split("-");
                    rp.setBackgroundResource(R.drawable.speak_ques_stop);
//                    wordToRead = a[1];
                    iseManager.startEvaluate(speakQues.get(index - 1).answer, index - 1);
//                        point=point+4;
                }
            }
        });

        proIECC();
        thread.run();
    }

    private void formatQuesList() {
        List<IntelTestQues> pronounce = new ArrayList<IntelTestQues>();
        List<IntelTestQues> expression = new ArrayList<IntelTestQues>();
        List<IntelTestQues> material = new ArrayList<IntelTestQues>();
        List<IntelTestQues> logic = new ArrayList<IntelTestQues>();

        for (int i = 0; i < speakQues.size(); i++) {
            switch (speakQues.get(i).category) {
                case "口语发音":
                    pronounce.add(speakQues.get(i));
                    break;
                case "口语表达":
                    expression.add(speakQues.get(i));
                    break;
                case "口语素材":
                    material.add(speakQues.get(i));
                    break;
                case "口语逻辑":
                    logic.add(speakQues.get(i));
                    break;
            }
        }

        mCategory[0] = pronounce.size();
        mCategory[1] = expression.size();
        mCategory[2] = material.size();
        mCategory[3] = logic.size();

        speakQues.clear();
        speakQues.addAll(pronounce);
        speakQues.addAll(expression);
        speakQues.addAll(material);
        speakQues.addAll(logic);
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

        int totalnum = 0;
        rightNum[0] = score1;
        rightNum[1] = score2;
        rightNum[2] = score3;
        rightNum[3] = score4;
        rightNum[4] = score5;
        rightNum[5] = score6;
        //获取系统当前时间,作为测试是按保存
        String endTime = deviceInfo.getCurrentTime();
        for (int i = 0; i < rightNum.length; i++) {
            totalnum = totalnum + rightNum[i];
        }

        //保存数据库
        final AbilityResult abilityResult = new AbilityResult();
        abilityResult.TypeId = Constant.ABILITY_TETYPE_SPEAK;
        abilityResult.Score1 = parseSavePattern(TYPE_FAYIN);//mCategory[TYPE_FAYIN] + "++" + rightNum[TYPE_FAYIN] + "++" + "口语发音";
        abilityResult.Score2 = parseSavePattern(TYPE_BIAODA);//mCategory[TYPE_BIAODA] + "++" + rightNum[TYPE_BIAODA] + "++" + "口语表达";
        abilityResult.Score3 = parseSavePattern(TYPE_SUCAI);//mCategory[TYPE_SUCAI] + "++" + rightNum[TYPE_SUCAI] + "++" + "口语素材";
        abilityResult.Score4 = parseSavePattern(TYPE_LUOJI);// mCategory[TYPE_LUOJI] + "++" + rightNum[TYPE_LUOJI] + "++" + "口语逻辑";
        abilityResult.Score5 = "-1";
        abilityResult.Score6 = "-1";
        abilityResult.Score7 = "-1";
        abilityResult.DoRight = totalnum;
        abilityResult.Total = quesTotalNo;
        abilityResult.UndoNum = undoNum;
        abilityResult.beginTime = beginTime;
        abilityResult.endTime = endTime;
        abilityResult.uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        //数据保存 上传答题记录到大数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                zdbHelper.seveTestRecord(abilityResult);

                uploadTestRecordToNet(mTestRecord.uid, Constant.ABILITY_TETYPE_SPEAK);
            }
        }).start();

        Intent intent = new Intent();
        intent.putExtra("testType", Constant.ABILITY_TETYPE_SPEAK);
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


    private void proIECC() {
        mTestRecord.BeginTime = deviceInfo.getCurrentTime();
        mTestRecord.LessonId = speakQues.get(index).id + "";
        mTestRecord.TestNumber = Integer.valueOf(speakQues.get(index).testId);
        mTestRecord.UserAnswer = "";
        mTestRecord.RightAnswer = speakQues.get(index).answer.trim();
        mTestRecord.category = speakQues.get(index).category;

        String mLocalAudioPrefix = Constant.envir + "audio/";
        switch (speakQues.get(index).category) {
            case "口语发音":
                if (speakQues.get(index).testType.equals("1")) {
                    speakChoices.setVisibility(View.VISIBLE);
                    speakVoice.setVisibility(View.GONE);

                    if (speakQues.get(index).attach.equals("")) {
                        if (speakQues.get(index).question.contains("[[")) {
                            questionIntro.setText(emphasizeWord(speakQues.get(index).question));
                            questionIntro.setPadding(5, 0, 0, 0);
                            questionContent.setText("");
                        } else {
                            questionIntro.setText(speakQues.get(index).question);
                            questionIntro.setPadding(5, 0, 0, 0);
                            questionContent.setText("");
                        }

                    } else {
                        questionIntro.setText(FileUtil.getTextFromFile(mLocalAudioPrefix + speakQues.get(index).attach));
                        questionIntro.setPadding(5, 0, 0, 0);
                        questionContent.setText("");
                    }


                    if (speakQues.get(index).choiceA.contains("[[")) {
                        textA.setText(emphasizeWord(speakQues.get(index).choiceA));
                    } else {
                        textA.setText(speakQues.get(index).choiceA);
                    }

                    if (speakQues.get(index).choiceB.contains("[[")) {
                        textB.setText(emphasizeWord(speakQues.get(index).choiceB));
                    } else {
                        textB.setText(speakQues.get(index).choiceB);
                    }

                    if (speakQues.get(index).choiceC.contains("[[")) {
                        textC.setText(emphasizeWord(speakQues.get(index).choiceC));
                    } else {
                        textC.setText(speakQues.get(index).choiceC);
                    }

                    if (speakQues.get(index).choiceD.contains("[[")) {
                        textD.setText(emphasizeWord(speakQues.get(index).choiceD));
                    } else {
                        textD.setText(speakQues.get(index).choiceD);
                    }

                } else {
                    speakChoices.setVisibility(View.GONE);
                    speakVoice.setVisibility(View.VISIBLE);

//                    String a[] = speakQues.get(index).question.split("-");
                    questionIntro.setText(speakQues.get(index).question);
                    questionContent.setText(speakQues.get(index).answer);
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setPadding(5, 0, 0, 0);
                }
                break;
            case "口语表达":
                speakChoices.setVisibility(View.VISIBLE);
                speakVoice.setVisibility(View.GONE);

                if (speakQues.get(index).attach.equals("")) {
                    questionIntro.setText(speakQues.get(index).question);
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setText("");
                } else {
                    questionIntro.setText(speakQues.get(index).question+"\n"+FileUtil.getTextFromFile(mLocalAudioPrefix + speakQues.get(index).attach));
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setText("");
                }

                textA.setText(speakQues.get(index).choiceA);
                textB.setText(speakQues.get(index).choiceB);
                textC.setText(speakQues.get(index).choiceC);
                textD.setText(speakQues.get(index).choiceD);
                break;
            case "口语素材":
                speakChoices.setVisibility(View.VISIBLE);
                speakVoice.setVisibility(View.GONE);

                if (speakQues.get(index).attach.equals("")) {
                    questionIntro.setText(speakQues.get(index).question);
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setText("");
                } else {
                    questionIntro.setText(speakQues.get(index).question+"\n"+FileUtil.getTextFromFile(mLocalAudioPrefix + speakQues.get(index).attach));
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setText("");
                }

                textA.setText(speakQues.get(index).choiceA);
                textB.setText(speakQues.get(index).choiceB);
                textC.setText(speakQues.get(index).choiceC);
                textD.setText(speakQues.get(index).choiceD);
                break;
            case "口语逻辑":
                speakChoices.setVisibility(View.VISIBLE);
                speakVoice.setVisibility(View.GONE);

                if (speakQues.get(index).attach.equals("")) {
                    questionIntro.setText(speakQues.get(index).question);
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setText("");
                } else {
                    questionIntro.setText(speakQues.get(index).question+"\n"+FileUtil.getTextFromFile(mLocalAudioPrefix + speakQues.get(index).attach));
                    questionIntro.setPadding(5, 0, 0, 0);
                    questionContent.setText("");
                }

                textA.setText(speakQues.get(index).choiceA);
                textB.setText(speakQues.get(index).choiceB);
                textC.setText(speakQues.get(index).choiceC);
                textD.setText(speakQues.get(index).choiceD);
                break;
        }

        quesIndex.setText(String.valueOf(index + 1) + "/" + String.valueOf(quesTotalNo));
        setChoicesBack(0);

        index++;

        if (index == quesTotalNo)
            nextQues.setText("完 成");
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

    @Override
    public void onBackPressed() {
        //showAlertDialog();
        showAlertDialog(index, quesTotalNo, "口语", zdbHelper, speakQues);
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
                if (speakQues.get(index - 1).answer.equals("A")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "A";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
            case 2:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (speakQues.get(index - 1).answer.equals("B")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "B";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
            case 3:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (speakQues.get(index - 1).answer.equals("C")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "C";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
            case 4:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (speakQues.get(index - 1).answer.equals("D")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "D";
                zdbHelper.saveTestRecord(mTestRecord);
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
                case 2:// set the read button progress bar with value of voice
                    // volume
                    try {
                        tempBar = rp;
                        int db = msg.arg1;
                        // Log.e(TAG, "sound DB value: " + db);
                        tempBar.setCricleProgressColor(0xfffea523);
                        tempBar.setMax(100);
                        tempBar.setProgress(db);
                    } catch (Exception e) {
                        Log.e("val", "handler.case2");
                    }
                    break;
                case 3: // reset the read button progress bar
                    try {
                        rp.setBackgroundResource(R.drawable.speak_ques_read);
                        tempBar = rp;
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
                    timeUsed.setText(a + "m " + b + "s");
                    if (totalTime != 0)
                        thread.run();
                    else if (!SpeakAbilityTestActivity.this.isFinishing()) {
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
                case 6:
                    rp.setClickable(true);//已经有测评结果了
                    nextQues.setClickable(true);

                    int score = msg.arg1;
                    int index = msg.arg2;
                    boolean is_rejected = (Boolean) msg.obj;
                    if (is_rejected) {
                        CustomToast.showToast(mContext, "语音异常，请重新录入!", 1500);
                    } else {
                        mTestRecord.AnswerResult = score > 60 ? 100 : 0;
                        mTestRecord.UserAnswer = score > 60 ? speakQues.get(index - 1).answer : "-1";
                        CustomToast.showToast(mContext, "评测成功", 1800);
                        if (score > 60)
                            score1++;
                        proIECC();
                    }
//                    handler.sendEmptyMessageDelayed(9,5);
//                    StudyActivity.instance.videoView.pause();
                    break;
                case 7:
                    if(StudyActivity.instance!=null)
                    StudyActivity.instance.videoView.pause();
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
