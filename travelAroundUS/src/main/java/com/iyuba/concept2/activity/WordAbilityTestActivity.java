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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.ise.result.Result;
import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.KeyboardAdapter;
import com.iyuba.concept2.manager.IseManager;
import com.iyuba.concept2.protocol.UploadTestRecordRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.JsonUtil;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.concept2.widget.RoundProgressBar;
import com.iyuba.concept2.widget.cdialog.CustomToast;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.lil.user.UserInfoManager;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class WordAbilityTestActivity extends BasisActivity {
    private Context mContext = this;
    private List<IntelTestQues> allQuesList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> CTEQuesWithPhotoList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> ETCQuesWithPhotoList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> CTEQuesList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> ETCQuesList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> listeningQuesList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> readQuesList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> spellQuesList = new ArrayList<IntelTestQues>();
    private List<IntelTestQues> sentenceQuesList = new ArrayList<IntelTestQues>();

    private int index = 0, quesTotalNo = 0;
    private TextView quesInstruction, question, A, B, C, D, timeNum, quesNo, testTotalTime;
    private Button notKnownButton;
    private ImageView wordImage, wordPlay;
    private Player player;
    private LinearLayout ansChoices, chosnChar;
    private RelativeLayout ansA, ansB, ansC, ansD, choicesr, keyboardr;
    private RoundProgressBar wordRead;
    private IseManager iseManager;
    private final String[] arr = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private List standardLetters = Arrays.asList(arr);
    private List<String> letters = new ArrayList<String>();
    private Result rs;
    private boolean flag;
    private GridView virtualKey;
    private List<String> chosn = new ArrayList<String>();
    private int wordIndex = 0, wordLength = 0;
    private ProgressBar pb;
    private int totalTime = 600;
    private boolean completeFlag = false;
    private GetDeviceInfo deviceInfo;
    private int rightNum[] = {0, 0, 0, 0, 0, 0};
    private int[] mCategory = new int[6];
    private String[] mTestTypeArr = Constant.WORD_ABILITY_ARR;//能力测评的维度
    private final int TYPE_ZHONGYING = 0;//实词
    private final int TYPE_YINGZHONG = 1;//虚词
    private final int TYPE_FAYIN = 2;//引语
    private final int TYPE_YINYI = 3;//被动语态
    private final int TYPE_PINXIE = 4;//句子
    private final int TYPE_YINGYONG = 5;//时态
    private final int TYPE_7 = 6;
    private AbilityTestRecordOp zdbHelper;
    private String mLocalAudioPrefix = Constant.envir + "audio/";

    private int score1 = 0, score2 = 0, score3 = 0, score4 = 0, score5 = 0, score6 = 0, undoNum = 0;
    private String beginTime, endTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    private int typeId = 0;
    private TestRecord mTestRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_ability_test);

        beginTime = df.format(new Date());// new Date()为获取当前系统时间

        iseManager = IseManager.getInstance(mContext, handler);

        timeNum = (TextView) findViewById(R.id.time_num);
        quesNo = (TextView) findViewById(R.id.ques_no);
        quesInstruction = (TextView) findViewById(R.id.ques_instruction);
        question = (TextView) findViewById(R.id.ques_word);
        wordImage = (ImageView) findViewById(R.id.word_img);
        wordPlay = (ImageView) findViewById(R.id.word_play);
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

        ansChoices = (LinearLayout) findViewById(R.id.ans_choices);
        wordRead = (RoundProgressBar) findViewById(R.id.word_read);
        notKnownButton = (Button) findViewById(R.id.not_known_button);
        virtualKey = (GridView) findViewById(R.id.virtual_keyboard);
        chosnChar = (LinearLayout) findViewById(R.id.chosen_char);
        pb = (ProgressBar) findViewById(R.id.time_line);
        testTotalTime = (TextView) findViewById(R.id.intel_test_total_time);

        totalTime = getIntent().getIntExtra("totalTime", 0);
        testTotalTime.setText(String.valueOf(totalTime) + "m");
        timeNum.setText(String.valueOf(totalTime) + "m 00s");
        totalTime = totalTime * 60;

        pb.setMax(totalTime);
        pb.setProgress(totalTime);
//        getTestQuesThread.run();
        allQuesList = (ArrayList<IntelTestQues>) getIntent().getSerializableExtra("QuestionList");
        sortQuesList();
        deviceInfo = new GetDeviceInfo(mContext);
        zdbHelper = new AbilityTestRecordOp(mContext);

        mTestRecord = new TestRecord();
        deviceInfo = new GetDeviceInfo(mContext);
        mTestRecord.uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        mTestRecord.appId = Constant.APPID;
        mTestRecord.index = 1;
        mTestRecord.deviceId = deviceInfo.getLocalMACAddress();
        mTestRecord.testMode = Constant.ABILITY_WORD;


        notKnownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (index < quesTotalNo) {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    zdbHelper.saveTestRecord(mTestRecord);
                    undoNum++;
//                        zdbHelper.saveTestRecord(mTestRecord);
                    handler.sendEmptyMessage(5);
//                    handler.sendEmptyMessage(7);
                } else {
                    mTestRecord.TestTime = deviceInfo.getCurrentTime();
                    zdbHelper.saveTestRecord(mTestRecord);
                    if (completeFlag) {
                        gotoResultActivity();
                    } else {
                        undoNum++;
//                            zdbHelper.saveTestRecord(mTestRecord);
                        gotoResultActivity();
//                        handler.sendEmptyMessage(7);
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
                    if (allQuesList.get(index - 1).answer.equals("A")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);

                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(1);
                    saveUserChoices(1);
                    if (allQuesList.get(index - 1).answer.equals("A")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
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
                    if (allQuesList.get(index - 1).answer.equals("B")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(2);
                    saveUserChoices(2);
                    if (allQuesList.get(index - 1).answer.equals("B")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
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
                    if (allQuesList.get(index - 1).answer.equals("C")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(3);
                    saveUserChoices(3);
                    if (allQuesList.get(index - 1).answer.equals("C")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
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
                    if (allQuesList.get(index - 1).answer.equals("D")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
                                score6++;
                                break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(5, 100);
                }

                if (index == quesTotalNo && !completeFlag) {
                    setChoicesBack(4);
                    saveUserChoices(4);
                    if (allQuesList.get(index - 1).answer.equals("D")) {
                        switch (allQuesList.get(index - 1).category) {
                            case "中英力":
                                score1++;
                                break;
                            case "英中力":
                                score2++;
                                break;
                            case "音义力":
                                score4++;
                                break;
                            case "应用力":
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


    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            totalTime--;
            handler.sendEmptyMessageDelayed(4, 1000);
        }
    };

    private void pageAdjust(int quesTypeIndex) {
        switch (quesTypeIndex) {
            case 1:
                wordPlay.setVisibility(View.INVISIBLE);
                wordImage.setVisibility(View.VISIBLE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.VISIBLE);
                wordRead.setVisibility(View.GONE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
            case 2:
                wordImage.setVisibility(View.VISIBLE);
                wordPlay.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.VISIBLE);
                wordRead.setVisibility(View.GONE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
            case 3:
                wordImage.setVisibility(View.GONE);
                wordPlay.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.VISIBLE);
                wordRead.setVisibility(View.GONE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
            case 4:
                wordImage.setVisibility(View.GONE);
                wordPlay.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.VISIBLE);
                wordRead.setVisibility(View.GONE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
            case 5:
                wordImage.setVisibility(View.INVISIBLE);
                wordPlay.setVisibility(View.VISIBLE);
                question.setVisibility(View.GONE);
                ansChoices.setVisibility(View.VISIBLE);
                wordRead.setVisibility(View.GONE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
            case 6:
                wordImage.setVisibility(View.GONE);
                wordPlay.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.GONE);
                wordRead.setVisibility(View.VISIBLE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
            case 7:
                wordImage.setVisibility(View.GONE);
                wordPlay.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.GONE);
                wordRead.setVisibility(View.GONE);
                choicesr.setVisibility(View.GONE);
                keyboardr.setVisibility(View.VISIBLE);
                chosnChar.setVisibility(View.VISIBLE);
                break;
            case 8:
                wordImage.setVisibility(View.GONE);
                wordPlay.setVisibility(View.GONE);
                question.setVisibility(View.VISIBLE);
                ansChoices.setVisibility(View.VISIBLE);
                wordRead.setVisibility(View.GONE);
                chosnChar.setVisibility(View.GONE);
                choicesr.setVisibility(View.VISIBLE);
                keyboardr.setVisibility(View.GONE);
                break;
        }
    }

    private void proIECC() {
        quesNo.setText(String.valueOf(index + 1) + "/" + String.valueOf(quesTotalNo));

        if (index < quesTotalNo) {
            String quesAll;
            switch (allQuesList.get(index).quesType) {
                case 1:                //有图中选英
                    typeId = 1;
                    pageAdjust(1);
                    quesAll = allQuesList.get(index).question;
                    quesInstruction.setText(quesAll.substring(0, quesAll.indexOf("++")));
                    question.setText(quesAll.substring(quesAll.indexOf("++") + 2, quesAll.length()));
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    wordImage.setImageBitmap(getImageBitmap(allQuesList.get(index).image));//设置本地图片

                    A.setText(allQuesList.get(index).choiceA);
                    B.setText(allQuesList.get(index).choiceB);
                    C.setText(allQuesList.get(index).choiceC);
                    D.setText(allQuesList.get(index).choiceD);
                    break;
                case 2:                 //有图英选中
                    typeId = 2;
                    pageAdjust(2);
                    quesAll = allQuesList.get(index).question;
                    quesInstruction.setText(quesAll.substring(0, quesAll.indexOf("++")));
                    question.setText(quesAll.substring(quesAll.indexOf("++") + 2, quesAll.length()));
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    wordImage.setImageBitmap(getImageBitmap(allQuesList.get(index).image));//设置本地图片

                    A.setText(allQuesList.get(index).choiceA);
                    B.setText(allQuesList.get(index).choiceB);
                    C.setText(allQuesList.get(index).choiceC);
                    D.setText(allQuesList.get(index).choiceD);
                    break;
                case 3:                  //无图中选英
                    typeId = 1;
                    pageAdjust(3);
                    quesAll = allQuesList.get(index).question;
                    quesInstruction.setText(quesAll.substring(0, quesAll.indexOf("++")));
                    question.setText(quesAll.substring(quesAll.indexOf("++") + 2, quesAll.length()));
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

                    A.setText(allQuesList.get(index).choiceA);
                    B.setText(allQuesList.get(index).choiceB);
                    C.setText(allQuesList.get(index).choiceC);
                    D.setText(allQuesList.get(index).choiceD);
                    break;
                case 4:                  //无图英选中
                    typeId = 2;
                    pageAdjust(4);
                    quesAll = allQuesList.get(index).question;
                    quesInstruction.setText(quesAll.substring(0, quesAll.indexOf("++")));
                    question.setText(quesAll.substring(quesAll.indexOf("++") + 2, quesAll.length()));
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

                    A.setText(allQuesList.get(index).choiceA);
                    B.setText(allQuesList.get(index).choiceB);
                    C.setText(allQuesList.get(index).choiceC);
                    D.setText(allQuesList.get(index).choiceD);
                    break;
                case 5:                  //听音选中
                    typeId = 3;
                    pageAdjust(5);
                    quesInstruction.setText(allQuesList.get(index).question);

                    A.setText(allQuesList.get(index).choiceA);
                    B.setText(allQuesList.get(index).choiceB);
                    C.setText(allQuesList.get(index).choiceC);
                    D.setText(allQuesList.get(index).choiceD);

                    wordPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Message msg = handler.obtainMessage();
                            msg.what = 0;
//                            msg.obj = "http://static2."+Constant.IYBHttpHead+"/NewConcept1/sounds/" + allQuesList.get(index - 1).sound;
                            msg.obj = mLocalAudioPrefix + allQuesList.get(index - 1).sound;
                            handler.sendMessage(msg);
                        }
                    });
                    break;
                case 6:                  //读音
                    typeId = 4;
                    pageAdjust(6);
                    quesInstruction.setText(allQuesList.get(index).question);
                    question.setText(allQuesList.get(index).answer);
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

                    final String wd = allQuesList.get(index).answer;
                    wordRead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mTestRecord.TestTime = deviceInfo.getCurrentTime();
                            if (!NetWorkState.isConnectingToInternet()) {
                                CustomToast.showToast(mContext, R.string.alert_net_content,
                                        1000);
                                return;
                            } else {
                                notKnownButton.setClickable(false);
                                wordRead.setBackgroundResource(R.drawable.sen_i_stop);
                                iseManager.startEvaluate(wd, Integer.valueOf(allQuesList.get(index).id));
                            }
                        }
                    });
                    break;
                case 7:                       //拼写
                    typeId = 5;
                    pageAdjust(7);
                    Collections.shuffle(standardLetters);

                    quesAll = allQuesList.get(index).question;
                    quesInstruction.setText(quesAll.substring(0, quesAll.indexOf("++")));
                    question.setText(quesAll.substring(quesAll.indexOf("++") + 2, quesAll.length()));
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    chosnChar.removeAllViews();

                    String word = allQuesList.get(index).answer;
                    char[] words = word.toCharArray();
                    wordLength = words.length;
                    wordIndex = 0;
                    letters.clear();
                    chosn.clear();

                    for (int i = 0; i < words.length; i++) {
                        letters.add(String.valueOf(words[i]));
                        chosn.add("-");
                    }

                    showChosnChars(chosn);


                    for (int i = 0; i < standardLetters.size(); i++) {
                        flag = true;
                        for (int j = 0; j < letters.size(); j++) {
                            if (standardLetters.get(i).equals(letters.get(j)))
                                flag = false;
                        }
                        if (flag)
                            letters.add((String) standardLetters.get(i));
                        if (letters.size() == 19)
                            break;
                    }


                    Collections.shuffle(letters);
                    virtualKey.setAdapter(new KeyboardAdapter(mContext, letters));
                    virtualKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            StringBuilder chosnSpellWord = new StringBuilder();
                            if (i == 19) {
                                for (int j = 0; j < chosn.size(); j++) {
                                    chosnSpellWord.append(chosn.get(j));
                                }
                                mTestRecord.UserAnswer = chosnSpellWord.toString();
                                Log.e("chosnSpellWord", chosnSpellWord.toString());
                                Log.e("answer", allQuesList.get(index - 1).answer);
                                if (chosnSpellWord.toString().equals(allQuesList.get(index - 1).answer)) {
                                    score5++;
                                    mTestRecord.AnswerResult = 100;
                                } else {
                                    mTestRecord.AnswerResult = 0;
                                }
                                handler.sendEmptyMessage(5);
                            } else {
                                TextView tv1 = (TextView) view.findViewById(R.id.keyboard_text);
                                String a = (String) tv1.getText();
                                if (wordIndex < wordLength) {
                                    chosn.set(wordIndex, a);
                                    showChosnChars(chosn);
                                    wordIndex++;
                                }
                            }
                        }
                    });
                    break;
                case 8:                   //句子
                    typeId = 6;
                    pageAdjust(8);
                    quesInstruction.setText("阅读句子，选出正确的答案。");
                    question.setText(allQuesList.get(index).question);
                    question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

                    A.setText(allQuesList.get(index).choiceA);
                    B.setText(allQuesList.get(index).choiceB);
                    C.setText(allQuesList.get(index).choiceC);
                    D.setText(allQuesList.get(index).choiceD);
                    break;
            }

            //答题记录的
            mTestRecord.BeginTime = deviceInfo.getCurrentTime();
            mTestRecord.LessonId = allQuesList.get(index).id + "";
            mTestRecord.TestNumber = Integer.valueOf(allQuesList.get(index).testId);
            mTestRecord.UserAnswer = "";
            mTestRecord.RightAnswer = allQuesList.get(index).answer;
            mTestRecord.category = allQuesList.get(index).category;

            index++;
            setChoicesBack(0);
            if (index == quesTotalNo)
                notKnownButton.setText("完 成");
        }

    }

    private void gotoResultActivity() {
        int totalnum = 0;
        rightNum[0] = score1;
        rightNum[1] = score2;
        rightNum[2] = score3;
        rightNum[3] = score4;
        rightNum[4] = score5;
        rightNum[5] = score6;
        String endTime = deviceInfo.getCurrentTime();
        for (int i = 0; i < rightNum.length; i++) {
            totalnum = totalnum + rightNum[i];
            Log.e("scorex", String.valueOf(rightNum[i]));
        }

        //保存数据库
        final AbilityResult abilityResult = new AbilityResult();
        abilityResult.TypeId = Constant.ABILITY_TETYPE_WORD;

        abilityResult.Score1 = parseSavePattern(TYPE_ZHONGYING);
        abilityResult.Score2 = parseSavePattern(TYPE_YINGZHONG);
        abilityResult.Score3 = parseSavePattern(TYPE_FAYIN);
        abilityResult.Score4 = parseSavePattern(TYPE_YINYI);
        abilityResult.Score5 = parseSavePattern(TYPE_PINXIE);
        abilityResult.Score6 = parseSavePattern(TYPE_YINGYONG);

        if (mTestTypeArr.length > 6) {
            abilityResult.Score7 = parseSavePattern(TYPE_7);
        } else {
            abilityResult.Score7 = "-1";
        }

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
                uploadTestRecordToNet(mTestRecord.uid, Constant.ABILITY_TETYPE_WORD);
            }
        }).start();

        Intent intent = new Intent();
        intent.putExtra("testType", 1);
        intent.setClass(getApplicationContext(), AbilityTestResultActivity.class);
        this.finish();
        startActivity(intent);
    }

    private void sortQuesList() {
        for (int i = 0; i < allQuesList.size(); i++) {
            if (allQuesList.get(i).category.equals("中英力")) {
                if (allQuesList.get(i).image.equals("")) {
                    allQuesList.get(i).quesType = 3;
                    CTEQuesList.add(allQuesList.get(i));
                } else {
                    allQuesList.get(i).quesType = 1;
                    CTEQuesWithPhotoList.add(allQuesList.get(i));
                }
            } else if (allQuesList.get(i).category.equals("英中力")) {
                if (allQuesList.get(i).image.equals("")) {
                    allQuesList.get(i).quesType = 4;
                    ETCQuesList.add(allQuesList.get(i));
                } else {
                    allQuesList.get(i).quesType = 2;
                    ETCQuesWithPhotoList.add(allQuesList.get(i));
                }
            } else if (allQuesList.get(i).category.equals("音义力")) {
                allQuesList.get(i).quesType = 5;
                listeningQuesList.add(allQuesList.get(i));
            } else if (allQuesList.get(i).category.equals("发音力")) {
                allQuesList.get(i).quesType = 6;
                readQuesList.add(allQuesList.get(i));
            } else if (allQuesList.get(i).category.equals("拼写力")) {
                allQuesList.get(i).quesType = 7;
                spellQuesList.add(allQuesList.get(i));
            } else if (allQuesList.get(i).category.equals("应用力")) {
                allQuesList.get(i).quesType = 8;
                sentenceQuesList.add(allQuesList.get(i));
            }
        }

        mCategory[0] = CTEQuesWithPhotoList.size() + CTEQuesList.size();
        mCategory[1] = ETCQuesWithPhotoList.size() + ETCQuesList.size();
        mCategory[2] = readQuesList.size();
        mCategory[3] = listeningQuesList.size();
        mCategory[4] = spellQuesList.size();
        mCategory[5] = sentenceQuesList.size();

        allQuesList.clear();
        allQuesList.addAll(CTEQuesWithPhotoList);
        allQuesList.addAll(ETCQuesWithPhotoList);
        allQuesList.addAll(CTEQuesList);
        allQuesList.addAll(ETCQuesList);
        allQuesList.addAll(listeningQuesList);
        allQuesList.addAll(readQuesList);
        allQuesList.addAll(spellQuesList);
        allQuesList.addAll(sentenceQuesList);
        quesTotalNo = allQuesList.size();


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

    private void showChosnChars(final List<String> chosnn) {
        chosnChar.removeAllViews();
        for (int i = 0; i < wordLength; i++) {
            TextView tv = new TextView(mContext);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            tv.setText(chosnn.get(i));
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(5, 0, 5, 0);
            chosnChar.addView(tv);
        }
        ImageView iv = new ImageView(mContext);
        iv.setImageResource(R.drawable.delete);
        iv.setAdjustViewBounds(true);
        iv.setMaxHeight(80);
        iv.setMaxWidth(80);
        iv.setPadding(10, 0, 10, 0);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wordIndex != 0) {
                    chosnn.set(wordIndex - 1, "-");
                    wordIndex--;
                    showChosnChars(chosnn);
                }
            }
        });
        chosnChar.addView(iv);
    }

    private void saveUserChoices(int type) {
        switch (type) {
            case 1:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (allQuesList.get(index - 1).answer.equals("A")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "A";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
            case 2:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (allQuesList.get(index - 1).answer.equals("B")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "B";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
            case 3:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (allQuesList.get(index - 1).answer.equals("C")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "C";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
            case 4:
                mTestRecord.TestTime = deviceInfo.getCurrentTime();
                if (allQuesList.get(index - 1).answer.equals("D")) {
                    mTestRecord.AnswerResult = 100;
                } else {
                    mTestRecord.AnswerResult = 0;
                }
                mTestRecord.UserAnswer = "D";
                zdbHelper.saveTestRecord(mTestRecord);
                break;
        }
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

    /**
     * 获取图片资源
     *
     * @param imageName 图片的名字
     * @return
     */

    public Bitmap getImageBitmap(String imageName) {
        String imagePathString = mLocalAudioPrefix + "/" + imageName;

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float density = dm.density;
        LogUtils.e("density:   " + density);
        // int screenWidth = (int)
        // (mContext.getWindowManager().getDefaultDisplay().getWidth() * 0.95);
        // int screenHeight = (int) (getWindowManager().getDefaultDisplay()
        // .getHeight());

        //Bitmap bitmap = ImageUtil.getBitmap(mContext, imagePathString);

        try {
            FileInputStream fis = new FileInputStream(imagePathString);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //showAlertDialog();
        showAlertDialog(index, quesTotalNo, "单词", zdbHelper, allQuesList);
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
                    else if (!WordAbilityTestActivity.this.isFinishing()) {
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
                    int score = msg.arg1;
//                    int index = msg.arg2;
                    notKnownButton.setClickable(true);
                    boolean is_rejected = (Boolean) msg.obj;
                    rs = iseManager.getResultEva();
                    if (is_rejected) {
                        mTestRecord.UserAnswer = "-1";
                        CustomToast.showToast(mContext, "语音异常，请重新录入!", 1500);

                    } else {
                        mTestRecord.RightAnswer = allQuesList.get(index - 1).answer;
                        CustomToast.showToast(mContext, "评测成功", 1800);
                        Log.e("score", String.valueOf(score));
                        if (score > 60) {
                            score3++;
                            mTestRecord.UserAnswer = allQuesList.get(index - 1).answer;
                        } else {
                            mTestRecord.UserAnswer = "-1";
                        }
                        zdbHelper.saveTestRecord(mTestRecord);
                        handler.sendEmptyMessage(5);
                    }
//                    handler.sendEmptyMessageDelayed(9,10);
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
