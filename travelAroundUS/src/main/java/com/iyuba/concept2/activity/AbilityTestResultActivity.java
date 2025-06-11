package com.iyuba.concept2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.user.UserInfoManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 用户答完题目之后显示的界面  分数  题目概况  能力概况
 *
 * @author Administrator
 * @version 1.0.0
 * @time 2016/9 16:57
 */
public class AbilityTestResultActivity extends AppBaseActivity {
    /**
     * 保存每一个类型的答题结果
     */
    private int[] results;
    private Context mContext;
    private final int FINISHSELF = 10000;
    /**
     * 答对题目的个数
     */
    private int rightNum;
    /**
     * 没有回答的题目数
     */
    private int mUndoNum;
    private String[] abilityType;
    private int testType;
    /**
     * 总的题目数量, 可以根据测试项目类型赋值
     */
    private int mTotalTestNum;
    private AbilityResult mAbilityResult;
    private String userTimeToshow;
    private int mTotalScore;
    private String mTypeName;
    private int mAbilityTypeCount;//能力数目

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ability_result;
    }

    @Override
    protected void initVariables() {
        initCommons();
        mContext = this;
        rightNum = 0;
        String lastTestTime = "未知";//上次测试时间
        testType = getIntent().getIntExtra("testType", -1);
        AbilityTestRecordOp helper = new AbilityTestRecordOp(mContext);
        ArrayList<AbilityResult> abilityResults = helper.getAbilityTestRecord(testType, String.valueOf(UserInfoManager.getInstance().getUserId()), false);
        Log.e("abilityResults", abilityResults.toString());
        //根据每个题目的类型转化成百分制成绩进行展示
        if (abilityResults != null && abilityResults.size() > 0) {
            mAbilityResult = abilityResults.get(abilityResults.size() - 1);
            String begintime = mAbilityResult.beginTime;
            String endtime = mAbilityResult.endTime;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date begin = formatter.parse(begintime);
                Date end = formatter.parse(endtime);
                long usedTime = end.getTime() / 1000 - begin.getTime() / 1000;//单位 秒
                userTimeToshow = usedTime / 60 + "分" + usedTime % 60 + "秒";
            } catch (Exception e) {
                e.printStackTrace();
            }
            rightNum = mAbilityResult.DoRight;
            lastTestTime = mAbilityResult.endTime;
            mUndoNum = mAbilityResult.UndoNum;
            mTotalTestNum = mAbilityResult.Total;
        }

        int typecount = 0;
        switch (testType) {
            case Constant.ABILITY_TETYPE_WRITE://写作
                mTypeName = "写作";
                typecount = Constant.WRITE_ABILITY_ARR.length;
                abilityType = new String[typecount];
                getResult(mAbilityResult, typecount);
                break;
            case Constant.ABILITY_TETYPE_WORD://单词
                mTypeName = "单词";
                typecount = Constant.WORD_ABILITY_ARR.length;
                abilityType = new String[typecount];
                getResult(mAbilityResult, typecount);
               /* results[0] = getResult(mAbilityResult.Score1, 0);
                results[1] = getResult(mAbilityResult.Score2, 1);
                results[2] = getResult(mAbilityResult.Score3, 2);
                results[3] = getResult(mAbilityResult.Score4, 3);
                results[4] = getResult(mAbilityResult.Score5, 4);
                results[5] = getResult(mAbilityResult.Score6, 5);
                if (typecount > 6)
                    results[6] = getResult(mAbilityResult.Score7, 6);*/
                break;
            case Constant.ABILITY_TETYPE_GRAMMER://语法
                mTypeName = "语法";
                typecount = Constant.GRAM_ABILITY_ARR.length;
                abilityType = new String[typecount];
                getResult(mAbilityResult, typecount);
               /* results[0] = getResult(mAbilityResult.Score1, 0);
                results[1] = getResult(mAbilityResult.Score2, 1);
                results[2] = getResult(mAbilityResult.Score3, 2);
                results[3] = getResult(mAbilityResult.Score4, 3);
                results[4] = getResult(mAbilityResult.Score5, 4);
                results[5] = getResult(mAbilityResult.Score6, 5);*/
                break;
            case Constant.ABILITY_TETYPE_LISTEN://听力
                mTypeName = "听力";
                typecount = Constant.LIS_ABILITY_ARR.length;
                abilityType = new String[typecount];
                getResult(mAbilityResult, typecount);
                /*results[0] = getResult(mAbilityResult.Score1, 0);
                results[1] = getResult(mAbilityResult.Score2, 1);
                results[2] = getResult(mAbilityResult.Score3, 2);
                if (typecount > 3)
                    results[3] = getResult(mAbilityResult.Score4, 3);
                if (typecount > 4)
                    results[4] = getResult(mAbilityResult.Score5, 4);
                if (typecount > 5)
                    results[5] = getResult(mAbilityResult.Score6, 5);
                if (typecount > 6)
                    results[6] = getResult(mAbilityResult.Score7, 6);*/
                break;
            case Constant.ABILITY_TETYPE_SPEAK://口语
                mTypeName = "口语";
                typecount = Constant.SPEAK_ABILITY_ARR.length;
                abilityType = new String[typecount];
                getResult(mAbilityResult, typecount);
               /* results[0] = getResult(mAbilityResult.Score1, 0);
                results[1] = getResult(mAbilityResult.Score2, 1);
                results[2] = getResult(mAbilityResult.Score3, 2);
                results[3] = getResult(mAbilityResult.Score4, 3);*/
                break;
            case Constant.ABILITY_TETYPE_READ://阅读
                mTypeName = "阅读";
                typecount = Constant.READ_ABILITY_ARR.length;
                abilityType = new String[typecount];
                getResult(mAbilityResult, typecount);
                /*results[0] = getResult(mAbilityResult.Score1, 0);//ever  NullPointException Here
                results[1] = getResult(mAbilityResult.Score2, 1);
                results[2] = getResult(mAbilityResult.Score3, 2);
                results[3] = getResult(mAbilityResult.Score4, 3);
                results[4] = getResult(mAbilityResult.Score5, 4);
                results[5] = getResult(mAbilityResult.Score6, 5);*/
                break;
        }

        mTotalScore = mAbilityResult.DoRight * 100 / mTotalTestNum;
    }

    private void getResult(AbilityResult mAbilityResult, int typecount) {
        results = new int[7];
        for (int i = 0; i < typecount; i++) {
            results[0] = getResult(mAbilityResult.Score1, 0);
            results[1] = getResult(mAbilityResult.Score2, 1);
            results[2] = getResult(mAbilityResult.Score3, 2);
            if (typecount > 3)
                results[3] = getResult(mAbilityResult.Score4, 3);
            if (typecount > 4)
                results[4] = getResult(mAbilityResult.Score5, 4);
            if (typecount > 5)
                results[5] = getResult(mAbilityResult.Score6, 5);
            if (typecount > 6)
                results[6] = getResult(mAbilityResult.Score7, 6);
        }
    }

    /**
     * 根据数据库中的数据保存过情况,计算用户的各项
     *
     * @param scoreInfo 分数信息
     * @param i
     * @return
     */
    private int getResult(String scoreInfo, int i) {
        int totalNum = 0;
        int userScore = 0;
        if (!scoreInfo.equals("-1")) {
            totalNum = Integer.parseInt(scoreInfo.split("\\+\\+")[0]);//试题总数
            userScore = Integer.parseInt(scoreInfo.split("\\+\\+")[1]);//用户答对的题数
            abilityType[i] = scoreInfo.split("\\+\\+")[2];//应用能力名称
        }

        return totalNum == 0 ? 0 : userScore * 100 / totalNum;//转化为百分制
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        TextView tv_user_score = findView(R.id.tv_user_score);//用户测试成绩
//        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/EDOSZ.TTF");
//        tv_user_score.setTypeface(typeface);

        Button btn_goto_test = findView(R.id.btn_goto_test);
        TextView tv_title = findView(R.id.tv_titlebar_sub);
        String titleName = "我的走遍美国英语能力(" + mTypeName + ")";
        tv_title.setText(titleName);
        tv_user_score.setText(mTotalScore + "");
        btn_goto_test.setOnClickListener(gotoTargetActivityClickListener());


        //题目分析控件
        TextView tv_titleNum = findView(R.id.tv_ability_result_titleNum);
        TextView tv_rightNum = findView(R.id.tv_ability_result_rightnum);
        TextView tv_wrongNum = findView(R.id.tv_ability_result_wrongnum);
        TextView tv_undoNum = findView(R.id.tv_ability_result_undonum);
        TextView tv_usetime = findView(R.id.tv_ability_result_usetime);

        int score3 = Integer.parseInt(mAbilityResult.Score3.split("\\+\\+")[0]) == -1 ? 0 : Integer.parseInt(mAbilityResult.Score3.split("\\+\\+")[1]);
        int score4 = Integer.parseInt(mAbilityResult.Score4.split("\\+\\+")[0]) == -1 ? 0 : Integer.parseInt(mAbilityResult.Score4.split("\\+\\+")[1]);
        int score5 = Integer.parseInt(mAbilityResult.Score5.split("\\+\\+")[0]) == -1 ? 0 : Integer.parseInt(mAbilityResult.Score5.split("\\+\\+")[1]);
        int score6 = Integer.parseInt(mAbilityResult.Score6.split("\\+\\+")[0]) == -1 ? 0 : Integer.parseInt(mAbilityResult.Score6.split("\\+\\+")[1]);
        int score7 = Integer.parseInt(mAbilityResult.Score7.split("\\+\\+")[0]) == -1 ? 0 : Integer.parseInt(mAbilityResult.Score7.split("\\+\\+")[1]);

        int totalscore = Integer.parseInt(mAbilityResult.Score1.split("\\+\\+")[1])
                + Integer.parseInt(mAbilityResult.Score2.split("\\+\\+")[1])
                + score3 + score4 + score5 + score6 + score7;

        totalscore = totalscore > mTotalTestNum ? mTotalTestNum : totalscore;//答对的题目数 千万不可以大于试题总数 这种情况应该不会发生
        int undo = mAbilityResult.UndoNum > mTotalTestNum - rightNum ? mTotalTestNum - rightNum : mAbilityResult.UndoNum;//这种情况一般也不会发生,会不会欺骗消费者?
        int worongNum = mTotalTestNum - rightNum - undo < 0 ? 0 : mTotalTestNum - rightNum - undo;//会出现负数吗?

        //赋值
        tv_titleNum.setText(mTotalTestNum + "");//试题总数
        tv_rightNum.setText(totalscore + "");
        tv_wrongNum.setText(worongNum + "");
        tv_undoNum.setText(undo + "");
        tv_usetime.setText(userTimeToshow);


        //能力测评名录
        TextView tv_result_name1 = findView(R.id.tv_ability_result_name1);
        TextView tv_result_name2 = findView(R.id.tv_ability_result_name2);
        TextView tv_result_name3 = findView(R.id.tv_ability_result_name3);
        TextView tv_result_name4 = findView(R.id.tv_ability_result_name4);

        ProgressBar pb_ability_result_1 = findView(R.id.pb_ability_result_1);
        ProgressBar pb_ability_result_2 = findView(R.id.pb_ability_result_2);
        ProgressBar pb_ability_result_3 = findView(R.id.pb_ability_result_3);
        ProgressBar pb_ability_result_4 = findView(R.id.pb_ability_result_4);

        TextView tv_score1 = findView(R.id.tv_ability_result_score1);
        TextView tv_score2 = findView(R.id.tv_ability_result_score2);
        TextView tv_score3 = findView(R.id.tv_ability_result_score3);
        TextView tv_score4 = findView(R.id.tv_ability_result_score4);

        switch (abilityType.length) {
            case 7:
                LinearLayout ll_ability_result_7 = findView(R.id.ll_ability_result_7);
                ll_ability_result_7.setVisibility(View.VISIBLE);

                //能力名称赋值
                TextView tv_result_name7 = findView(R.id.tv_ability_result_name7);
                tv_result_name7.setText(abilityType[6]);
                //进度条
                ProgressBar pb_ability_result_7 = findView(R.id.pb_ability_result_7);
                pb_ability_result_7.setProgress(results[6]);
                //分数
                TextView tv_score7 = findView(R.id.tv_ability_result_score7);
                tv_score7.setText(results[6] + "");
            case 6:
                LinearLayout ll_ability_result_6 = findView(R.id.ll_ability_result_6);
                LinearLayout ll_ability_result_5 = findView(R.id.ll_ability_result_5);
                ll_ability_result_6.setVisibility(View.VISIBLE);
                ll_ability_result_5.setVisibility(View.VISIBLE);

                //能力名称赋值
                TextView tv_result_name5 = findView(R.id.tv_ability_result_name5);
                TextView tv_result_name6 = findView(R.id.tv_ability_result_name6);
                tv_result_name5.setText(abilityType[4]);
                tv_result_name6.setText(abilityType[5]);
                //进度条
                ProgressBar pb_ability_result_5 = findView(R.id.pb_ability_result_5);
                ProgressBar pb_ability_result_6 = findView(R.id.pb_ability_result_6);
                pb_ability_result_5.setProgress(results[4]);
                pb_ability_result_6.setProgress(results[5]);
                //分数
                TextView tv_score5 = findView(R.id.tv_ability_result_score5);
                TextView tv_score6 = findView(R.id.tv_ability_result_score6);
                tv_score5.setText(results[4] + "");
                tv_score6.setText(results[5] + "");
            case 4:
                LinearLayout ll_ability_result_4 = findView(R.id.ll_ability_result_4);
                ll_ability_result_4.setVisibility(View.VISIBLE);
                //能力名称赋值
                tv_result_name4.setText(abilityType[3]);
                //进度条
                pb_ability_result_4.setProgress(results[3]);
                //分数
                tv_score4.setText(results[3] + "");

            case 3:
                //能力名称赋值
                tv_result_name1.setText(abilityType[0]);
                tv_result_name2.setText(abilityType[1]);
                tv_result_name3.setText(abilityType[2]);

                //进度条
                pb_ability_result_1.setProgress(results[0]);
                pb_ability_result_2.setProgress(results[1]);
                pb_ability_result_3.setProgress(results[2]);

                //分数
                tv_score1.setText(results[0] + "");
                tv_score2.setText(results[1] + "");
                tv_score3.setText(results[2] + "");

                break;

        }

    }

    /**
     * 进入对应微课界面
     */
    private View.OnClickListener gotoTargetActivityClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hander.sendEmptyMessage(FINISHSELF);
                Intent intent = new Intent();
                // intent.setClass(AbilityTestResultActivity.this, mTargetClass);

                //TODO  跳转微课
               /* ImoocConstantManager.OWNERID = "21";
                intent.setClass(mContext, IMoocListActivity.class);*/
                hander.sendEmptyMessage(FINISHSELF);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void loadData() {

    }

    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FINISHSELF:
                    finish();
                    break;
            }
        }
    };


}

