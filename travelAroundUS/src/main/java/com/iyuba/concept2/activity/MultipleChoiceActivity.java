package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.MultipleResultAdapter;
import com.iyuba.concept2.manager.VoaDataManager;
import com.iyuba.concept2.protocol.UpdateExerciseRecordRequest;
import com.iyuba.concept2.protocol.UpdateExerciseRecordResponse;
import com.iyuba.concept2.sqlite.mode.ExerciseRecord;
import com.iyuba.concept2.sqlite.mode.MultipleChoice;
import com.iyuba.concept2.sqlite.op.MultipleChoiceOp;
import com.iyuba.concept2.widget.components.ResultTextViews;
import com.iyuba.concept2.widget.indicator.ExercisePageIndicator;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单项选择题
 */
public class MultipleChoiceActivity extends BasisActivity {
    public static MultipleChoiceActivity instance;

    private List<MultipleChoice> multipleChoiceMap;
    private Map<Integer, ExerciseRecord> exerciseRecordMap;
    private MultipleChoiceOp multipleChoiceOp;
    private int curQuestionId;
    private int questionNum;
    private ExercisePageIndicator pageIndicator;

    private RoundProgressBar circleProgress;
    private TextView questionNumValueText, rightRateValueText,
            spendTimeValueText;
    private TextView questionNumText, rightRateText, spendTimeText;

    private TextView question_index;
    private TextView testTypeTextView, questionTextView;
    private TextView answerAIcon, answerBIcon, answerCIcon, answerDIcon;
    private TextView answerAText, answerBText, answerCText, answerDText;
    private View answerA, answerB, answerC, answerD, select_view;
    private LinearLayout question;
    private TextView formerQuestionButton, nextQuestionButton;
    private TextView exerciseResult;
    private TextView tvRedo;
    private ImageView backButton;
    // private Button checkButton;
    private View startExerciseView, noExerciseView, exerciseView, resultView;
    private ImageView startExercise;

    private int exerciseStatus;// 0 表示做练习 1表示查看结果

    private int userTime = 0; //0表示第一次做，1表示读取记录（读取记录不显示时间）

    private long startTime, endTime;
    private String startDate, endDate;
    private List<String> date = new ArrayList<String>();
    private Context mContext;
    private int voaId;

    private ResultTextViews resultTextViews;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

    private Chronometer timer; //计时器
    private ImageView image_result;

    private ListView recyclerView;
    private MultipleResultAdapter multipleResultAdapter;
    private List<ExerciseRecord> list_multipleResult = new ArrayList<>();

    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
        }

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {

        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.multiple_choice);
        mContext = this;
        instance = this;
        initMultipleChoice();

        /**
         * 做过的题展示做题记录
         */
        if(multipleChoiceMap != null){
            if(multipleChoiceMap.get(0).userAnswer != null && !multipleChoiceMap.get(0).userAnswer.isEmpty()){
                userTime = 1;
                /**
                 * 如果有做题记录，设置开始按钮隐藏
                 */
                startExerciseView.setVisibility(View.GONE);
                exerciseView.setVisibility(View.VISIBLE);
                exerciseRecordMap = new HashMap<>();
                for (int i = 0; i < multipleChoiceMap.size(); i++) {
                    ExerciseRecord exerciseRecord = new ExerciseRecord();
                    exerciseRecord.UserAnswer = multipleChoiceMap.get(i).userAnswer;
                    exerciseRecord.RightAnswer = multipleChoiceMap.get(i).answer;
                    exerciseRecordMap.put(multipleChoiceMap.get(i).number-1, exerciseRecord);
                }
                makeResult(false);
                refreshQuestion();
            }
        }

    }


    private void timerStart() {
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0" + hour + ":%s");
        timer.start();
    }

    private void initMultipleChoice() {
        //开始计时
        timer = findViewById(R.id.timer);
        image_result = findViewById(R.id.image_result);
        recyclerView = findViewById(R.id.result_listview);//成绩列表
        //设置RecyclerView管理器
        multipleResultAdapter = new MultipleResultAdapter(MultipleChoiceActivity.this, list_multipleResult);
        recyclerView.setAdapter(multipleResultAdapter);
        recyclerView.setOnItemClickListener((parent, view, position, id) -> setQuestion(position));

        voaId = VoaDataManager.Instace().voaTemp.voaId;

        startExercise = findViewById(R.id.start_exercise);

        circleProgress = findViewById(R.id.result_round_bar);

        rightRateValueText = findViewById(R.id.right_rate_value);
        spendTimeValueText = findViewById(R.id.test_spendtime_value);
        questionNumValueText = findViewById(R.id.question_sum_value);

        rightRateText = findViewById(R.id.right_rate);
        spendTimeText = findViewById(R.id.test_spendtime);
        questionNumText = findViewById(R.id.question_sum);
        question_index = findViewById(R.id.question_index); //题号

        pageIndicator = findViewById(R.id.pageIndicator);
        testTypeTextView = findViewById(R.id.test_type);
        questionTextView = findViewById(R.id.question_body);
        answerAIcon = findViewById(R.id.answer_a_icon);
        answerBIcon = findViewById(R.id.answer_b_icon);
        answerCIcon = findViewById(R.id.answer_c_icon);
        answerDIcon = findViewById(R.id.answer_d_icon);
        answerAText = findViewById(R.id.answer_a_text);
        answerBText = findViewById(R.id.answer_b_text);
        answerCText = findViewById(R.id.answer_c_text);
        answerDText = findViewById(R.id.answer_d_text);
        answerA = findViewById(R.id.answer_a);
        answerB = findViewById(R.id.answer_b);
        answerC = findViewById(R.id.answer_c);
        answerD = findViewById(R.id.answer_d);
        select_view = findViewById(R.id.select_question);
        question = findViewById(R.id.question);
        formerQuestionButton = findViewById(R.id.former_question_button);
        nextQuestionButton = findViewById(R.id.next_question_button);
        exerciseResult = findViewById(R.id.result);
        tvRedo = findViewById(R.id.redo);
        backButton = findViewById(R.id.back);
        resultTextViews = findViewById(R.id.result_textviews);

        startExerciseView = findViewById(R.id.start_exercise_view);
        noExerciseView = findViewById(R.id.no_exercise_view);
        exerciseView = findViewById(R.id.exercise_view);
        resultView = findViewById(R.id.result_view);

        multipleChoiceOp = new MultipleChoiceOp(mContext);

        multipleChoiceMap = multipleChoiceOp.findData(voaId);//voaId
        questionNum = multipleChoiceMap.size();//multipleChoiceMap=null

        image_result.setOnClickListener(olc);
        startExercise.setOnClickListener(olc);
        formerQuestionButton.setOnClickListener(olc);
        nextQuestionButton.setOnClickListener(olc);
        exerciseResult.setOnClickListener(olc);
        backButton.setOnClickListener(olc);
        answerA.setOnClickListener(olc);
        answerB.setOnClickListener(olc);
        answerC.setOnClickListener(olc);
        answerD.setOnClickListener(olc);
        tvRedo.setOnClickListener(olc);
        pageIndicator.setOnClickListener(olc);
        pageIndicator.setIndicator(questionNum);
        pageIndicator.setCurrIndicator(0);

        exerciseResult.setVisibility(View.INVISIBLE);

        exerciseView.setVisibility(View.GONE);
        resultView.setVisibility(View.GONE);

        if (questionNum == 0) {
            noExerciseView.setVisibility(View.VISIBLE);
            startExerciseView.setVisibility(View.GONE);
        } else {
            startExerciseView.setVisibility(View.VISIBLE);
            noExerciseView.setVisibility(View.GONE);
        }
    }

    public void setMultiplechoice() {
        exerciseRecordMap = new HashMap<>();
        if (exerciseRecordMap.size() != 0) {
            exerciseStatus = 1;
        }
        curQuestionId = 0;
        refreshQuestion();
    }

    OnClickListener olc = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_exercise:
                    initQuestion();
                    break;
                case R.id.redo:
                    initQuestion();
                    colorBack();
                    exerciseStatus = 0;
                    userTime = 0;
                    resultView.setVisibility(View.GONE);
                    exerciseResult.setText("提交答案");
                    exerciseResult.setVisibility(View.GONE);
                    break;
                // 点击上一题
                case R.id.former_question_button:
                case R.id.former_question:
                    nextQuestionButton.setVisibility(View.VISIBLE);
                    if (curQuestionId != 0) {
                        pageIndicator.setCurrIndicator(--curQuestionId);
                        refreshQuestion();
                        date.remove(date.size() - 1);
                        setVisibility();
                    } else {
                        CustomToast.showToast(mContext, "已是第一题", 1000);
                    }
                    break;
                case R.id.next_question_button:
                case R.id.next_question:
                    if (curQuestionId < questionNum - 1) {
                        pageIndicator.setCurrIndicator(++curQuestionId);
                        refreshQuestion();
                        date.add(df.format(new Date()));
                        setVisibility();
                    } else {
                        CustomToast.showToast(mContext, "已是最后一题", 1000);
                    }
                    break;
                case R.id.result:// 提交答案，或者查看成绩
                    if (exerciseStatus == 0) {
                        date.add(df.format(new Date()));
                        AlertDialog alert = new AlertDialog.Builder(mContext).create();
                        alert.setTitle("");
                        alert.setMessage(getResources().getString(
                                R.string.submit_result));
                        alert.setIcon(android.R.drawable.ic_dialog_alert);
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources()
                                        .getString(R.string.alert_btn_ok),
                                (dialog, which) -> {
                                    makeResult(true);
                                    timer.stop();
                                });

                        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.alert_btn_cancel),
                                (dialog, which) -> {
                                });
                        alert.show();
                        // TODO: 2022/12/13 提交答案的时候更新做题记录
                        saveSubjectRecord();
                    } else if (exerciseStatus == 1) {
                        exerciseView.setVisibility(View.GONE);
                        resultView.setVisibility(View.VISIBLE);
                    }
                    userTime = 1;
                    break;
                case R.id.back:
                case R.id.pageIndicator:
                    exerciseView.setVisibility(View.GONE);
                    resultView.setVisibility(View.VISIBLE);
                    break;
                case R.id.answer_a:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);
                        colorBack();
                        answerAText.setTextColor(Color.WHITE);
                        answerAIcon.setTextColor(Color.WHITE);
                        answerA.setBackgroundResource(R.drawable.multiple_yellow_bg);
                        makeChoice("a");
                    }

                    break;
                case R.id.answer_b:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);
                        colorBack();
                        answerBText.setTextColor(Color.WHITE);
                        answerBIcon.setTextColor(Color.WHITE);
                        answerB.setBackgroundResource(R.drawable.multiple_yellow_bg);
                        makeChoice("b");
                    }
                    break;
                case R.id.answer_c:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);
                        colorBack();
                        answerCText.setTextColor(Color.WHITE);
                        answerCIcon.setTextColor(Color.WHITE);
                        answerC.setBackgroundResource(R.drawable.multiple_yellow_bg);
                        makeChoice("c");
                    }
                    break;
                case R.id.answer_d:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);
                        colorBack();
                        answerDText.setTextColor(Color.WHITE);
                        answerDIcon.setTextColor(Color.WHITE);
                        answerD.setBackgroundResource(R.drawable.multiple_yellow_bg);
                        makeChoice("d");
                    }
                    break;
                case R.id.image_result:
                    setQuestion(0);
                    break;
                default:
                    break;
            }
        }
    };

    private void saveSubjectRecord(){
        for (int i = 0; i < multipleChoiceMap.size(); i++) {
            multipleChoiceMap.get(i).userAnswer = exerciseRecordMap.get(multipleChoiceMap.get(i).number-1).UserAnswer;
        }
        multipleChoiceOp.saveData(multipleChoiceMap);
    }

    public void makeResult(boolean isSubmit) {
        if (!UserInfoManager.getInstance().isLogin()) {
//            Intent intent = new Intent();
//            intent.setClass(mContext, Login.class);
//            mContext.startActivity(intent);
            LoginUtil.startToLogin(mContext);
        } else {
            exerciseStatus = 1;
            // TODO
            question.setVisibility(View.VISIBLE);
            ConfigManager.Instance().putBoolean("is_exercising", false);

            int rightsum = 0;// 正确的题数

            ExerciseRecord exerciseRecord = null;
            for (int i = 0; i < questionNum; i++) {
                exerciseRecord = exerciseRecordMap.get(i);
                exerciseRecord.voaId = voaId;
                exerciseRecord.TestNumber = i + 1;
                exerciseRecord.uid = UserInfoManager.getInstance().getUserId();

                if (exerciseRecord.UserAnswer
                        .equals(exerciseRecord.RightAnswer)) {
                    exerciseRecord.AnswerResut = 1;
                    rightsum++;
                } else {
                    exerciseRecord.AnswerResut = 0;
                }
            }

            int rightrate = (int) ((rightsum / Float.valueOf(questionNum)) * 100);

            circleProgress.setCricleProgressColor(0xff1ABC9C);
            circleProgress.setMax(questionNum);
            circleProgress.setProgress(rightsum);
            questionNumValueText.setText(": " + questionNum);
            rightRateValueText.setText(": " + rightrate + "%");
            if(userTime == 1){
                spendTimeText.setVisibility(View.GONE);
                spendTimeValueText.setVisibility(View.GONE);
            }else{
                spendTimeText.setVisibility(View.VISIBLE);
                spendTimeValueText.setVisibility(View.VISIBLE);
                spendTimeValueText.setText(getSpendTime());
            }
            exerciseView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);
            if(isSubmit){
                new Thread(new UpdateExerciseRecordThread()).start();
            }

            resultTextViews.setResultMap(exerciseRecordMap);
            resultTextViews.refResultTextView();


            if(!list_multipleResult.isEmpty()){
                list_multipleResult.clear();
            }
            for (int i = 0; i < exerciseRecordMap.size(); i++) {
                list_multipleResult.add(exerciseRecordMap.get(i));
            }
            multipleResultAdapter.notifyDataSetChanged();
        }
    }

    private final void makeChoice(String answer) {
        ExerciseRecord exerciseRecord = exerciseRecordMap.get(curQuestionId);
        exerciseRecord.UserAnswer = answer;
        exerciseRecordMap.put(curQuestionId, exerciseRecord);
    }

    // 根据提供的答案设置相应testrecord的答案
    private final void makeAnswer(ExerciseRecord exerciseRecord) {
        String answer = exerciseRecord.UserAnswer;
        if (answer.trim().length() != 0) {
            int answerN = answer.charAt(0) - 'a';
            switch (answerN) {
                case 0:
                    if (answerAText.getText().equals("空")) {
                        answerAText.setVisibility(View.GONE);
                    } else {
                        answerAText.setVisibility(View.VISIBLE);
                    }
                    answerAText.setTextColor(Color.WHITE);
                    answerAIcon.setTextColor(Color.WHITE);
                    answerA.setBackgroundResource(R.drawable.multiple_yellow_bg);
                    break;
                case 1:
                    answerBText.setTextColor(Color.WHITE);
                    answerBIcon.setTextColor(Color.WHITE);
                    answerB.setBackgroundResource(R.drawable.multiple_yellow_bg);
                    break;
                case 2:
                    answerCText.setTextColor(Color.WHITE);
                    answerCIcon.setTextColor(Color.WHITE);
                    answerC.setBackgroundResource(R.drawable.multiple_yellow_bg);
                    break;
                case 3:
                    answerDText.setTextColor(Color.WHITE);
                    answerDIcon.setTextColor(Color.WHITE);
                    answerD.setBackgroundResource(R.drawable.multiple_yellow_bg);
                    break;
            }
        }

        if (exerciseStatus == 1) {
            int rightN = exerciseRecord.RightAnswer.charAt(0) - 'a';
            switch (rightN) {
                case 0:
                    answerAText.setTextColor(Color.WHITE);
                    answerAIcon.setTextColor(Color.WHITE);
                    answerA.setBackgroundResource(R.drawable.multiple_green_bg);
                    break;
                case 1:
                    answerBText.setTextColor(Color.WHITE);
                    answerBIcon.setTextColor(Color.WHITE);
                    answerB.setBackgroundResource(R.drawable.multiple_green_bg);
                    break;
                case 2:
                    answerCText.setTextColor(Color.WHITE);
                    answerCIcon.setTextColor(Color.WHITE);
                    answerC.setBackgroundResource(R.drawable.multiple_green_bg);
                    break;
                case 3:
                    answerDText.setTextColor(Color.WHITE);
                    answerDIcon.setTextColor(Color.WHITE);
                    answerD.setBackgroundResource(R.drawable.multiple_green_bg);

                    break;
            }

            if (answer.trim().length() != 0) {
                int answerN = answer.charAt(0) - 'a';
                if (answerN != rightN) {
                    switch (answerN) {
                        case 0:
                            answerAText.setTextColor(Color.WHITE);
                            answerAIcon.setTextColor(Color.WHITE);
                            answerA.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                        case 1:
                            answerBText.setTextColor(Color.WHITE);
                            answerBIcon.setTextColor(Color.WHITE);
                            answerB.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                        case 2:
                            answerCText.setTextColor(Color.WHITE);
                            answerCIcon.setTextColor(Color.WHITE);
                            answerC.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                        case 3:
                            answerDText.setTextColor(Color.WHITE);
                            answerDIcon.setTextColor(Color.WHITE);
                            answerD.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                    }
                }
            }
        }
        setVisibility();
    }

    // 选项的颜色复位
    private final void colorBack() {
        answerA.setBackgroundResource(R.drawable.multiple_white_bg);
        answerB.setBackgroundResource(R.drawable.multiple_white_bg);
        answerC.setBackgroundResource(R.drawable.multiple_white_bg);
        answerD.setBackgroundResource(R.drawable.multiple_white_bg);

        answerAIcon.setTextColor(Color.BLACK);
        answerBIcon.setTextColor(Color.BLACK);
        answerCIcon.setTextColor(Color.BLACK);
        answerDIcon.setTextColor(Color.BLACK);

        answerAText.setTextColor(Color.BLACK);
        answerBText.setTextColor(Color.BLACK);
        answerCText.setTextColor(Color.BLACK);
        answerDText.setTextColor(Color.BLACK);
    }

    // 获取所用时间
    private final String getSpendTime() {
        endTime = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        endDate = df.format(new Date());
        int hour;
        int minute;
        int second;
        hour = (int) ((endTime - startTime) / 3600000);
        minute = (int) ((endTime - startTime) % 3600000) / 60000;
        second = (int) (((endTime - startTime) % 3600000) % 60000) / 1000;
        String string = ": ";
        if (hour != 0) {
            string = string + hour + "h";
        }
        if (minute != 0) {
            string = string + minute + "m";
        }
        if (second != 0) {
            string = string + second + "s";
        }
        return string;
    }


    void refreshQuestion() {
        if (questionNum > 0) {
            // 显示题型和问题
            MultipleChoice multipleChoice = multipleChoiceMap.get(curQuestionId);
            select_view.setVisibility(View.VISIBLE);
            testTypeTextView.setText("选择题");
            testTypeTextView.setTextSize(Constant.textSize);
            //这里重新处理下选择题的序号
//            question_index.setText(multipleChoice.indexId + ". ");
            question_index.setText(multipleChoice.number + ". ");
            questionTextView.setText(multipleChoice.question);
            answerAText.setText(multipleChoice.choiceA);
            answerBText.setText(multipleChoice.choiceB);
            answerCText.setText(multipleChoice.choiceC);
            answerDText.setText(multipleChoice.choiceD);
            // setVisibility();
            ExerciseRecord exerciseRecord = exerciseRecordMap
                    .get(curQuestionId);
            if (exerciseRecord == null) {
                exerciseRecord = new ExerciseRecord();
            }
            exerciseRecord.RightAnswer = multipleChoice.answer;
            exerciseRecordMap.put(curQuestionId, exerciseRecord);
            if (curQuestionId != (questionNum - 1) && exerciseStatus == 0) {
                exerciseResult.setVisibility(View.GONE);
            } else {
                exerciseResult.setVisibility(View.VISIBLE);
            }
            if (exerciseStatus == 1) {
                backButton.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
            }

            colorBack();

            setAnswer(exerciseRecordMap.get(curQuestionId));
        }
    }

    //设置选项是否显示
    private void setVisibility() {
        // 显示选项
        if (TextUtils.equals(answerAText.getText(), "空")) {
            answerAText.setVisibility(View.GONE);
            answerAIcon.setVisibility(View.GONE);
            answerA.setVisibility(View.INVISIBLE);
        } else {
            answerAText.setVisibility(View.VISIBLE);
            answerA.setVisibility(View.VISIBLE);
            answerAIcon.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(answerBText.getText(), "空")) {
            answerBIcon.setVisibility(View.GONE);
            answerBText.setVisibility(View.GONE);
            answerB.setVisibility(View.INVISIBLE);
        } else {
            answerBText.setVisibility(View.VISIBLE);
            answerB.setVisibility(View.VISIBLE);
            answerBIcon.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(answerCText.getText(), "空")) {
            answerCIcon.setVisibility(View.GONE);
            answerCText.setVisibility(View.GONE);
            answerC.setVisibility(View.INVISIBLE);
        } else {
            answerCText.setVisibility(View.VISIBLE);
            answerC.setVisibility(View.VISIBLE);
            answerCIcon.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(answerDText.getText(), "空")) {
            answerDIcon.setVisibility(View.GONE);
            answerDText.setVisibility(View.GONE);
            answerD.setVisibility(View.INVISIBLE);
        } else {
            answerDText.setVisibility(View.VISIBLE);
            answerD.setVisibility(View.VISIBLE);
            answerDIcon.setVisibility(View.VISIBLE);
        }
    }

    // 根据所提供的testrecord记录，设置曾经选过选项的颜色（用于用户前后切换试题时的显示）
    private void setAnswer(ExerciseRecord exerciseRecord) {
        if (userTime != 0) {
            makeAnswer(exerciseRecord);
        }
    }

    // 点击答案后回看原题
    public void setQuestion(int index) {
        curQuestionId = index;
        pageIndicator.setCurrIndicator(index);
        refreshQuestion();

        exerciseView.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.GONE);
        exerciseResult.setText("查看成绩");
    }


    /**
     * 将做题记录转换为json，用于提交做题记录
     *
     * @return
     */
    public JSONObject transformToJson() {
        List<ExerciseRecord> exerciseRecordList = new ArrayList<ExerciseRecord>();

        ExerciseRecord tempExerciseRecord = null;
        for (int i = 0; i < exerciseRecordMap.size(); i++) {
            tempExerciseRecord = exerciseRecordMap.get(i);
            tempExerciseRecord.BeginTime = date.get(i);
            tempExerciseRecord.TestTime = date.get(i + 1);

            if (tempExerciseRecord != null) {
                exerciseRecordList.add(tempExerciseRecord);
            }
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;

        for (ExerciseRecord exerciseRecord : exerciseRecordList) {
            jsonObject = new JSONObject();

            try {
                jsonObject.put("uid", exerciseRecord.uid);
                jsonObject.put("LessonId", exerciseRecord.voaId);
                jsonObject.put("TestNumber", exerciseRecord.TestNumber);
                jsonObject.put("BeginTime", exerciseRecord.BeginTime);
                jsonObject.put("UserAnswer", exerciseRecord.UserAnswer);
                jsonObject.put("RightAnswer", exerciseRecord.RightAnswer);
                jsonObject.put("AnswerResut", exerciseRecord.AnswerResut);
                jsonObject.put("TestTime", exerciseRecord.TestTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }

        JSONObject jsonRoot = new JSONObject();

        try {
            jsonRoot.put("datalist", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonRoot;
    }

    /**
     * 提交做题记录
     */
    class UpdateExerciseRecordThread implements Runnable {
        @Override
        public void run() {

            ClientSession.Instace().asynGetResponse(
                    new UpdateExerciseRecordRequest(transformToJson()),
                    (response, request, rspCookie) -> {
                        UpdateExerciseRecordResponse tr = (UpdateExerciseRecordResponse) response;

                        if (tr != null && tr.result.equals("0") == false) {
                            Looper.prepare();
                            if (tr.jifen.equals("0"))
                                Toast.makeText(mContext,
                                        "数据提交成功!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(mContext,
                                        "数据提交成功，恭喜您获得了" + tr.jifen + "分",
                                        Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }, null, mNetStateReceiver);
        }
    }

    /**
     * 初始化题目
     */
    private void initQuestion(){
        if(!UserInfoManager.getInstance().isLogin()){
//            startActivity(new Intent(this,Login.class));
            LoginUtil.startToLogin(this);
            return;
        }

        startExerciseView.setVisibility(View.GONE);
        exerciseView.setVisibility(View.VISIBLE);
        timerStart();
        setMultiplechoice();
        startTime = System.currentTimeMillis();
        startDate = df.format(new Date());
        date.add(df.format(new Date()));
    }

}
