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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.iyuba.concept2.R;
import com.iyuba.concept2.listener.RequestCallBack;
import com.iyuba.concept2.protocol.AbilityTestQuestionRequest;
import com.iyuba.concept2.protocol.AbilityTestQuestionResponse;
import com.iyuba.concept2.protocol.GetAbilityResultRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.MyPolygonView;
import com.iyuba.concept2.util.NetWorkState;
import com.iyuba.concept2.widget.DrawView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.multithread.DownloadProgressListener;
import com.iyuba.multithread.FileDownloader;
import com.iyuba.multithread.MultiThreadDownloadManager;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 展示听说读写各个模块能力图谱,加载网络试题
 *
 * @author liuzhenli  2016/9/? 16:42
 * @version 1.0.0
 */

public class AbilityMapSubActivity extends AppBaseActivity {
    private int[] mScoreArr = {0, 0, 0, 0, 0, 0};
    private Context mContext;
    private final int FINISHSELF = 10000;
    private final int GETTESTCONTENTS = 10001;
    private final int BEGINTEST = 10002;
    private final int SHOW_DIALOG = 10003;
    private final int DOWNLOAD_AUDIO = 10004;
    private final int GET_NET_RESULT = 10005;
    /**
     * 用户答对的试题个数
     */
    private int mDoRightNum;
    private int mTotalTestNum = 100;//试题总数
    private String lastTestTime;//上次测试时间
    private ArrayList<IntelTestQues> mQuesLists;
    private ProgressBar mPb_download_test;
    private String mAbilityType;
    private AbilityTestRecordOp atro;

    /**
     * 开始测试按钮
     */
    private Button mBtn_goto_test;
    /**
     * 上一次测试的结果 展示测试时间接掌握情况
     */
    private TextView mTv_lasttime_resut;

    private Bundle data = new Bundle();
    //最后测试分析
    private String testRecordInfo;

    private String mTitle;
    private TextView tv_nextTextTime;
    private CustomDialog mWaittingDialog;
    private int totalTime = 0;
    private long nexttime;
    private long currentTime;
    private MyPolygonView muPolygonView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ability_map_gettest;
    }

    @Override
    protected void initVariables() {
        mContext = this;
        alertDialog = new CustomDialog(mContext, R.style.MyDialogStyle);
        mAbilityType = getIntent().getStringExtra("abilityType");//W--单词,G--语法,L--听力,S--口语,R--阅读,X--写作
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atro = new AbilityTestRecordOp(mContext);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initCommons();
        drawMap();

        TextView tv_title = findView(R.id.tv_titlebar_sub);
        String titleBarstr = "我的走遍美国英语能力图谱(" + mTitle + ")";
        tv_title.setText(titleBarstr);
        tv_nextTextTime = findView(R.id.tv_next_testtime);

        mBtn_goto_test = findView(R.id.btn_goto_test);
        mBtn_goto_test.setOnClickListener(gotoAbilityTestClickListener());

        mTv_lasttime_resut = findView(R.id.tv_result_advice);

        mWaittingDialog = new CustomDialog(mContext);//等待进度条
        mWaittingDialog.setTitle("正在准备，请稍后······");

        if (mAbilityType.equals(Constant.ABILITY_WORD))
            testRecordInfo = "上次测评时间:" + lastTestTime + "\n走遍美国英语考试总词汇:3313个\n" + "您目前掌握的词汇:[[" + mDoRightNum * 3313 / mTotalTestNum + "]]个";
        else
            testRecordInfo = "上次测评时间:" + lastTestTime + "\n" + mTitle + "测评总分:100分\n" + "您的测评成绩为:[[" + mDoRightNum * 100 / mTotalTestNum + "]]分";

        //mTv_lasttime_resut.setText(testRecordInfo);
        showTextWithColor(mTv_lasttime_resut, testRecordInfo);
        mPb_download_test = findView(R.id.pb_download);//下载的进度条

        setNextTime(lastTestTime);


    }

    /**
     * 底部展示下一次测试的时间
     */
    private void setNextTime(String lastTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy年MM月dd日");
        //计算一个星期之后的时间
        if (!lastTime.equals("未知")) {//已经有过测试记录
            try {
                Date date = formatter.parse(lastTime.substring(0, 10));//获取当前的测试时间,毫秒级
                long nextTime = date.getTime() + 7 * 24 * 60 * 60 * 1000;//一个星期之后的测试时间  秒

                tv_nextTextTime.setText("*提示:您可以在" + formatter2.format(nextTime) + "进行下次测评");
                tv_nextTextTime.invalidate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 进入单词此时界面
     */
    private View.OnClickListener gotoAbilityTestClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断距离上一次测试时间是否够7天 用户登录状态下判断是否可以测试
                if (isUserLogin() && lastTestTime.length() > 10 && !timeOver(lastTestTime)) {
//                    ToastUtil.showToast(mContext, "还未到测评时间呢(^_^)");
                    new AlertDialog.Builder(mContext).setTitle("您还需要" + leftTime(nexttime - currentTime * 1000) + "才能进行下一次测试")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确认”后的操作
                                }
                            }).show();
                    //这里可以提示或者直接进入微课学习系统

                    return;
                }

                //需要在用户登录状态下才可以加载试题
                if (checkUserLoginAndLogin())
                    if (mQuesLists == null || mQuesLists.size() == 0 || mCompleteNum < mTotalFileCount2Down)//当前没有出题,开始出题
                    {
                        mWaittingDialog.show();
                        hander.sendEmptyMessage(GETTESTCONTENTS);//从服务器获取试题内容
                    } else {//题目已经出完
                        mBtn_goto_test.setText("开始测评");
                        if (!alertDialog.isShowing())
                            hander.sendEmptyMessage(SHOW_DIALOG);
                    }
            }
        };
    }

    @Override
    protected void loadData() {

    }

    private Class mTargetClass;

    /**
     * 绘制图谱
     */
    private void drawMap() {
        final DrawView view = new DrawView(this);
        switch (mAbilityType) {
            case Constant.ABILITY_WRITE:
                mTitle = "写作";
                String[] abilityType1 = Constant.WRITE_ABILITY_ARR;
                mScoreArr = getResults(abilityType1.length);
                view.setData(abilityType1, mScoreArr);
                drawMap(view);
                initMyPolygonView(abilityType1, mScoreArr);
                mTargetClass = WriteAbilityTestActivity.class;
                getUserRes(abilityType1);
                break;

            case Constant.ABILITY_WORD:
                mTitle = "单词";
                String[] abilityType2 = Constant.WORD_ABILITY_ARR;
                mScoreArr = getResults(abilityType2.length);
                mTargetClass = WordAbilityTestActivity.class;
                view.setData(abilityType2, mScoreArr);

                drawMap(view);

                getUserRes(abilityType2);
                initMyPolygonView(abilityType2, mScoreArr);
                break;

            case Constant.ABILITY_GRAMMER:
                mTitle = "语法";
                String[] abilityType3 = Constant.GRAM_ABILITY_ARR;
                mScoreArr = getResults(abilityType3.length);
                view.setData(abilityType3, mScoreArr);
                mTargetClass = GrammarAbilityTestActivity.class;
                drawMap(view);
                initMyPolygonView(abilityType3, mScoreArr);
                getUserRes(abilityType3);
                break;

            case Constant.ABILITY_LISTEN:
                mTitle = "听力";
                String[] abilityType4 = Constant.LIS_ABILITY_ARR;
                mScoreArr = getResults(abilityType4.length);
                view.setData(abilityType4, mScoreArr);
                mTargetClass = ListenAbilityTestActivity.class;
                drawMap(view);
                initMyPolygonView(abilityType4, mScoreArr);
                getUserRes(abilityType4);
                break;

            case Constant.ABILITY_SPEAK:
                mTitle = "口语";
                String[] abilityType5 = Constant.SPEAK_ABILITY_ARR;
                mScoreArr = getResults(abilityType5.length);
                view.setData(abilityType5, mScoreArr);
                mTargetClass = SpeakAbilityTestActivity.class;
                drawMap(view);
                initMyPolygonView(abilityType5, mScoreArr);
                getUserRes(abilityType5);
                break;

            case Constant.ABILITY_READ:
                mTitle = "阅读";
                String[] abilityType6 = Constant.READ_ABILITY_ARR;//测试维度
                mScoreArr = getResults(abilityType6.length);//每个维度的成绩
                view.setData(abilityType6, mScoreArr);
                mTargetClass = ReadAbilityTestActivity.class;
                drawMap(view);
                initMyPolygonView(abilityType6, mScoreArr);
                getUserRes(abilityType6);
                break;
        }
    }

    private void drawMap(DrawView view) {
        LinearLayout layout = findView(R.id.root);
        layout.removeAllViews();
        view.setMinimumHeight((int) (300 * getScreenDensity()));//设置最小高度
        view.setMinimumWidth((int) (300 * getScreenDensity()));//设置最小宽度
        //通知view组件重绘
        view.invalidate();
        layout.addView(view);


    }

    private void initMyPolygonView(final String[] text, final int[] area) {

        Message message=new Message();
        Bundle bundle=new Bundle();
        bundle.putStringArray("text",text);
        bundle.putIntArray("area",area);
        message.setData(bundle);//bundle传值，耗时，效率低
        message.what=999;//标志是哪个线程传数据
        hander.sendMessage(message);//发送message信息



    }

    /**
     * 从服务器获取用于的各个模块的水平
     *
     * @param arr 各个模块的能力类型 eg {中英力,英中力,....}
     */
    private void getUserRes(String[] arr) {
        if (isUserLogin()) {//从服务器获取数据
            Message msg = hander.obtainMessage();
            msg.what = GET_NET_RESULT;
            data.remove("ability");
            data.putStringArray("ability", arr);
            msg.setData(data);
            hander.sendMessage(msg);
        }
    }

    /**
     * 获取数据库中存储的内容: Score1  Score2 ......
     *
     * @param resCount
     * @return
     */
    private int[] getResults(int resCount) {
        mScoreArr = new int[resCount];
        mDoRightNum = 0;
        lastTestTime = "未知";
//        if (!isUserLogin()) {//用户没有登录,使用本地的数据
        ArrayList<AbilityResult> abilityResults = (ArrayList<AbilityResult>) getIntent().getSerializableExtra("resultLists");
        //根据每个题目的类型转化成百分制成绩进行展示
        if (abilityResults != null && abilityResults.size() > 0) {
            switch (resCount) {
                case 7:
                    mScoreArr[6] = getResult(abilityResults.get(abilityResults.size() - 1).Score7);
                case 6:
                    mScoreArr[5] = getResult(abilityResults.get(abilityResults.size() - 1).Score6);
                case 5:
                    mScoreArr[4] = getResult(abilityResults.get(abilityResults.size() - 1).Score5);
                case 4:
                    mScoreArr[3] = getResult(abilityResults.get(abilityResults.size() - 1).Score4);
                case 3:
                    mScoreArr[0] = getResult(abilityResults.get(abilityResults.size() - 1).Score1);
                    mScoreArr[1] = getResult(abilityResults.get(abilityResults.size() - 1).Score2);
                    mScoreArr[2] = getResult(abilityResults.get(abilityResults.size() - 1).Score3);
                    break;
            }
            mDoRightNum = abilityResults.get(abilityResults.size() - 1).DoRight;
            lastTestTime = abilityResults.get(abilityResults.size() - 1).endTime;
            mTotalTestNum = abilityResults.get(abilityResults.size() - 1).Total;
        }
//        }
        return mScoreArr;
    }

    /**
     * 根据数据表中获取的Score信息,转换为百分制的分数
     *
     * @param score 存储格式:本类型试题总数+用户答对的试题个数+测试类型
     * @return
     */
    private int getResult(String score) {
        mTotalTestNum = Integer.parseInt(score.split("\\+\\+")[0]);//试题总数
        int userScore = Integer.parseInt(score.split("\\+\\+")[1]);//用户答对的题数
        return mTotalTestNum == 0 ? 0 : userScore * 100 / mTotalTestNum;
    }

    private int mTotalFileCount2Down;//总共需要下载的文件数量
    private ArrayList<IntelTestQues> mListWithSound; //需要下载音频
    private ArrayList<IntelTestQues> mListWithImage;//需要下载图片
    private ArrayList<IntelTestQues> mListWithAttach;//需要下载Txt文档
    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FINISHSELF:
                    finish();
                    break;
                case GETTESTCONTENTS:
                    mCompleteNum = 0;//点击初始化,否则下载是从头开始的,但是默认已经完成了上一次的个数.

                    mQuesLists = new ArrayList<>();
                    String lesson = "NewConcept1";
                    LogUtils.e("准备出题了-----");
                    ClientSession.Instace().asynGetResponse(
                            new AbilityTestQuestionRequest(lesson, mAbilityType),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(BaseHttpResponse response,
                                                       BaseHttpRequest request, int rspCookie) {
                                    AbilityTestQuestionResponse res = (AbilityTestQuestionResponse) response;

                                    if (res.mResult != 0) {//服务器返回数据
                                        LogUtils.e("出题成功了");
                                        if (mQuesLists != null && mQuesLists.size() > 0) {
                                            mQuesLists.clear();
                                        }
                                        mQuesLists.addAll(res.mQuestionLists);
                                        totalTime = res.mTestTime;
                                        mListWithSound = new ArrayList<IntelTestQues>();
                                        mListWithImage = new ArrayList<IntelTestQues>();
                                        mListWithAttach = new ArrayList<IntelTestQues>();

                                        for (int i = 0; i < mQuesLists.size(); i++) {
                                            if (mQuesLists.get(i).image != null && !mQuesLists.get(i).image.trim().equals("")) {
                                                mListWithImage.add(mQuesLists.get(i));
                                                LogUtils.e(mQuesLists.get(i).image);
                                            }
                                            if (mQuesLists.get(i).sound != null && !mQuesLists.get(i).sound.trim().equals("")) {
                                                mListWithSound.add(mQuesLists.get(i));
                                            }

                                            if (mQuesLists.get(i).attach != null && !mQuesLists.get(i).attach.trim().equals("")) {
                                                mListWithAttach.add(mQuesLists.get(i));
                                            }

                                            if (mQuesLists.get(i).testType.equals("4")) {
                                                if (mQuesLists.get(i).choiceA != null && !mQuesLists.get(i).choiceA.trim().equals("")) {
                                                    IntelTestQues itq = new IntelTestQues();
                                                    itq.id = mQuesLists.get(i).id;
                                                    itq.image = mQuesLists.get(i).choiceA;
                                                    mListWithImage.add(itq);
                                                    LogUtils.e(itq.image);
                                                }

                                                if (mQuesLists.get(i).choiceB != null && !mQuesLists.get(i).choiceB.trim().equals("")) {
                                                    IntelTestQues itq = new IntelTestQues();
                                                    itq.id = mQuesLists.get(i).id;
                                                    itq.image = mQuesLists.get(i).choiceB;
                                                    mListWithImage.add(itq);
                                                    LogUtils.e(itq.image);
                                                }

                                                if (mQuesLists.get(i).choiceC != null && !mQuesLists.get(i).choiceC.trim().equals("")) {
                                                    IntelTestQues itq = new IntelTestQues();
                                                    itq.id = mQuesLists.get(i).id;
                                                    itq.image = mQuesLists.get(i).choiceC;
                                                    mListWithImage.add(itq);
                                                    LogUtils.e(itq.image);
                                                }

                                                if (mQuesLists.get(i).choiceD != null && !mQuesLists.get(i).choiceD.trim().equals("")) {
                                                    IntelTestQues itq = new IntelTestQues();
                                                    itq.id = mQuesLists.get(i).id;
                                                    itq.image = mQuesLists.get(i).choiceD;
                                                    mListWithImage.add(itq);
                                                    LogUtils.e(itq.image);
                                                }
                                            }
                                        }
                                        //显示进度条
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mWaittingDialog.isShowing())
                                                    mWaittingDialog.dismiss();
                                                mPb_download_test.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        mTotalFileCount2Down = mListWithSound.size() + mListWithImage.size() + mListWithAttach.size();
                                        mPercent = 100.0f / mTotalFileCount2Down;


                                        LogUtils.e("音频个数   " + mListWithSound.size() + "   图片个数  " + mListWithImage.size() + "txt个数:  " + mListWithAttach.size());
                                        if (mListWithSound.size() + mListWithImage.size() + mListWithAttach.size() == 0) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hander.sendEmptyMessage(SHOW_DIALOG);
                                                    mBtn_goto_test.setText("开始测评");
                                                }
                                            });
                                        }

                                        if (mListWithSound.size() != 0)
                                            checkNetwork(Integer.valueOf(mListWithSound.get(0).id), mListWithSound.get(0).sound);

                                        if (mListWithImage.size() != 0)
                                            checkNetwork(Integer.valueOf(mListWithImage.get(mListWithImage.size() - 1).id), mListWithImage.get(mListWithImage.size() - 1).image);

                                        if (mListWithAttach.size() != 0)
                                            checkNetwork(Integer.valueOf(mListWithAttach.get(mListWithAttach.size() - 1).id), mListWithAttach.get(mListWithAttach.size() - 1).attach);
                                    } else {
                                        LogUtils.e("返回数据为空,出题失败了");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showLongToast(mContext, "网络连接失败,请检查网络是否可用");
                                            }
                                        });
                                    }

                                }
                            }, null, null);
                    break;
                case BEGINTEST://开始测试
                    hander.sendEmptyMessage(FINISHSELF);
                    Intent intent = new Intent();
                    intent.putExtra("QuestionList", ((Serializable) mQuesLists));
                    intent.putExtra("totalTime", totalTime);
                    intent.setClass(AbilityMapSubActivity.this, mTargetClass);
                    startActivity(intent);
                    break;

                case SHOW_DIALOG:
                    if (canShowdialog) {
                        showToTestDialog();
                    }
                    break;
                case DOWNLOAD_AUDIO:
                    if (mTotalFileCount2Down < mListWithSound.size()) {//说明音频已经下载完成了
                        checkNetwork(Integer.valueOf(mListWithSound.get(msg.arg1).id), mListWithSound.get(msg.arg1).sound);
                    } else {//下载音频
                        LogUtils.e("下载音频的第几项?" + (msg.arg1));
                        checkNetwork(Integer.valueOf(mListWithImage.get(msg.arg1).id), mListWithImage.get(msg.arg1).image);
                    }
                    break;
                case GET_NET_RESULT://从服务器获取上一次的测试结果

                    final String[] abilityt = msg.getData().getStringArray("ability");
                    for (String a : abilityt) {
                        LogUtils.e(a);
                    }
                    GetAbilityResultRequest request = new GetAbilityResultRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), "NewConcept1", mAbilityType, 3, getCurTime(), new RequestCallBack() {
                        @Override
                        public void requestResult(Request res) {
                            GetAbilityResultRequest req = (GetAbilityResultRequest) res;
                            if (req.isRequestSuccessful()) {//获取网络数据成功了.重新绘制图谱
                                int score = 0;
                                ArrayList<AbilityResult> results = req.resLists;
                                for (int i = 0; i < results.size(); i++) {
                                    for (int j = 0; j < abilityt.length; j++) {
                                        if (abilityt[j].equals(results.get(i).category)) {
                                            mScoreArr[j] = Integer.parseInt(results.get(i).score);
                                            score += mScoreArr[j];
                                            LogUtils.e(results.get(i).category + "  fenshu:  " + mScoreArr[j]);
                                        }
                                    }
                                }
                                score = mScoreArr.length == 0 ? 0 : score / mScoreArr.length;
                                lastTestTime = results.get(0).testTime;

                                //绘制图谱:
                                DrawView view = new DrawView(mContext);
                                view.setData(abilityt, mScoreArr);
                                drawMap(view);
                                initMyPolygonView(abilityt, mScoreArr);
                                //文字部分的:
                                if (mAbilityType.equals(Constant.ABILITY_WORD))
                                    testRecordInfo = "上次测评时间:" + lastTestTime.substring(0, 19) + "\n走遍美国英语考试总词汇:3313个\n" + "您目前掌握的词汇:[[" + score * 3313 / 100 + "]]个";
                                else
                                    testRecordInfo = "上次测评时间:" + lastTestTime.substring(0, 19) + "\n" + mTitle + "测评总分:100分\n" + "您的测评成绩为:[[" + score + "]]分";
                                showTextWithColor(mTv_lasttime_resut, testRecordInfo);

                                // mTv_lasttime_resut.setText(testRecordInfo);

                                setNextTime(lastTestTime);
                            }

                            if (((GetAbilityResultRequest) res).result.equals("0")) {//正常但没有数据
                                LogUtils.e("00000000");
                            }

                        }
                    });
                    CrashApplication.getInstance().getQueue().add(request);
                    break;
                case 999:

                            int[] area= msg.getData().getIntArray("area");
                            String[]  text = msg.getData().getStringArray("text");

                            if (area != null) {
                                muPolygonView = (MyPolygonView)findViewById(R.id.muPolygonView);
                                //区域等级，值不能超过n边形的个数
                                muPolygonView.setArea(area);
                                muPolygonView.setText(text);
                                muPolygonView.setN(area.length);
                                muPolygonView.invalidate();
                            }



                    break;

            }
        }

    };

    private String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }

    /**
     * 音频下载
     */
    private void checkNetwork(final int audioId, final String filename) {
        // 判断有没有网络
        int apnType = NetWorkState.getAPNType();
        switch (apnType) {
            case 0:// 没有网
                ToastUtil.showToast(mContext, "网络连接失败,请检查网络是否可用");
                break;
            case 1:// 移动网络
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("提示");
                        dialog.setMessage("您目前的网络状态,下载将耗费手机流量,是否继续下载？");
                        dialog.setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        downloadAudio(audioId, filename);
                                    }
                                });
                        dialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mPb_download_test.setVisibility(View.GONE);
                                        dialog.dismiss();
                                    }
                                });

                        dialog.show();

                    }
                });


                break;
            case 2:// wifi
                downloadAudio(audioId, filename);
                break;

            case 3:


                break;


            default:
                break;
        }

    }

    private int mCompleteNum;
    private float mPercent;

    protected void downloadAudio(int audioId, final String filename) {

        // 下载地址、存储路径
        String downloadUrl = "";
        String fileSaveDir;
        int threadNum = 1;
        fileSaveDir = Constant.envir + "audio/" + filename;
        if (filename.trim().toUpperCase().endsWith(".MP3")) {//音频
            downloadUrl = Constant.ABILITY_AUDIO_URL_PRE() + filename;
        } else if (filename.trim().toUpperCase().endsWith(".PNG")) {//图片
            downloadUrl = Constant.ABILITY_IMAGE_URL_PRE() + filename;
        } else if (filename.trim().toUpperCase().endsWith(".TXT")) {//txt文档
            downloadUrl = Constant.ABILITY_ATTACH_URL_PRE() + filename;
        }
        LogUtils.e("downloadUrl:" + downloadUrl);
        File file = new File(fileSaveDir);
        if (file.exists()) {//音频存在不用下载
            //发消息,接着下载
            mCompleteNum++;
            LogUtils.e("下载完成---" + mCompleteNum);
            if (mCompleteNum < mListWithSound.size()) {//音频
                downloadAudio(Integer.valueOf(mListWithSound.get(mCompleteNum).id), mListWithSound.get(mCompleteNum).sound);

            } else if (mCompleteNum >= mListWithSound.size() && mCompleteNum < mListWithSound.size() + mListWithImage.size()) {//图片
                downloadAudio(Integer.valueOf(mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).id), mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).image);

            } else if (mCompleteNum < mTotalFileCount2Down) {//txt
                downloadAudio(Integer.valueOf(mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).id), mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).attach);
            } else {
                LogUtils.e("全部下载完成了");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtn_goto_test.setText("开始测评");
                        if (!alertDialog.isShowing())
                            hander.sendEmptyMessage(SHOW_DIALOG);
                    }
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPb_download_test.setProgress((int) (mCompleteNum * mPercent));
                    if ((int) (mCompleteNum * mPercent) < 100)
                        mBtn_goto_test.setText((int) (mCompleteNum * mPercent) + "%");
                    else
                        mBtn_goto_test.setText("开始测评");
                }
            });
        } else {

            MultiThreadDownloadManager.enQueue(mContext, audioId, downloadUrl, new File(fileSaveDir), threadNum,
                    new DownloadProgressListener() {

                        @Override
                        public void onProgressUpdate(int id, String downloadurl, int fileDownloadSize) {
                        }

                        @Override
                        public void onDownloadStoped(int id) {
                            LogUtils.e("下载停止了");
                        }

                        @Override
                        public void onDownloadStart(FileDownloader fileDownloader, int id, int fileTotalSize) {

                        }

                        @Override
                        public void onDownloadError(String errorMessage) {
                        }

                        @Override
                        public void onDownloadComplete(int id, String savePathFullName) {
                            mCompleteNum++;
                            LogUtils.e("下载完成---" + mCompleteNum);
                            //发消息,接着下载
                            if (mCompleteNum < mListWithSound.size()) {//音频
                                downloadAudio(Integer.valueOf(mListWithSound.get(mCompleteNum).id), mListWithSound.get(mCompleteNum).sound);

                            } else if (mCompleteNum >= mListWithSound.size() && mCompleteNum < mListWithSound.size() + mListWithImage.size()) {//图片
                                downloadAudio(Integer.valueOf(mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).id), mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).image);

                            } else if (mCompleteNum < mTotalFileCount2Down) {//txt
                                downloadAudio(Integer.valueOf(mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).id), mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).attach);
                            } else {
                                LogUtils.e("全部下载完成了");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBtn_goto_test.setText("开始测试");
                                        if (!alertDialog.isShowing())
                                            hander.sendEmptyMessage(SHOW_DIALOG);
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPb_download_test.setProgress((int) (mCompleteNum * mPercent));
                                    if ((int) (mCompleteNum * mPercent) < 100)
                                        mBtn_goto_test.setText((int) (mCompleteNum * mPercent) + "%");
                                    else
                                        mBtn_goto_test.setText("开始测试");
                                }
                            });
                        }
                    }
            );
        }

    }

    private CustomDialog alertDialog;

    private void showToTestDialog() {//当前activity不可见的时候,调用show方法闪退
        mPb_download_test.setVisibility(View.GONE);

        /**自定义的dialog*/
        LayoutInflater inflater2 = LayoutInflater.from(mContext);
        View merge_view = inflater2.inflate(R.layout.dialog_custom, null);//要填充的layout

        alertDialog.setContentView(merge_view);
        alertDialog.show();//

        TextView deleteTxt = (TextView) merge_view.findViewById(R.id.tv_dialog_title);//标题
        if (deleteTxt != null)
            deleteTxt.setText("温馨提示");
        TextView content = (TextView) merge_view.findViewById(R.id.tv_dialog_content);//提示的内容
        if (content != null)
            content.setText(mTitle + "测评即将开始,为确保测评结果接近您的真实水平,请在规定的时间内完成.");//NullPointException here

        Button rechCancleBtn = (Button) merge_view.findViewById(R.id.btn_dialog_no);
        Button rechOkBtn = (Button) merge_view.findViewById(R.id.btn_dialog_yes);
        rechOkBtn.setText("开始测评");
        rechCancleBtn.setText("以后再说");
        rechCancleBtn.setOnClickListener(new View.OnClickListener() {//取消按钮
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        rechOkBtn.setOnClickListener(new View.OnClickListener() {//开始按钮
            @Override
            public void onClick(View v) {
                hander.sendEmptyMessage(BEGINTEST);
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 是否可以显示dialog
     */
    private boolean canShowdialog = true;

    @Override
    public void onPause() {
        super.onPause();
        canShowdialog = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        drawMap();
        canShowdialog = true;
    }

    /**
     * 判断距离上次测试时是否够一个星期
     *
     * @param time 上次测试的时间
     * @return true--大于规定的时间  false--小于规定的时间
     */
    public boolean timeOver(String time) {
        SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //当前测试时间精确到日
        String last = time.substring(0, 10);
        try {
            Date testtime = formatter3.parse(last);
            nexttime = testtime.getTime() + 7 * 24 * 60 * 60 * 1000;//一个星期之后的测试时间  秒
//            nextTime = date.getTime() + 7 * 24 * 60 * 60 * 1000;//下次测试的时间  秒
            currentTime = formatter.parse(new GetDeviceInfo(mContext).getCurrentTime()).getTime() / 1000;//当前时间 秒
            long today = System.currentTimeMillis();//获取当前的时间
            if (today >= nexttime) {
                System.out.print("可以测试");
                return true;
            } else {
                System.out.print("不可以测试");
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private float getScreenDensity() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }

    private String leftTime(long second) {
        second = second / 1000;
        long hour = second / 3600;
        long minute = second % 3600 / 60;
        long leftSecond = second % 3600 % 60;

        return String.valueOf(hour) + "时" + String.valueOf(minute) + "分" + String.valueOf(leftSecond) + "秒";
    }

    /**
     * 让某几个文字显示颜色
     *
     * @param str 字符串
     */
    private void showTextWithColor(TextView v, String str) {


        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xffff0000);//前景色
        String wordsor = str.replace("[[", "").replace("]]", "");
        SpannableStringBuilder words = new SpannableStringBuilder(wordsor);
        words.setSpan(colorSpan, str.indexOf("[["), str.indexOf("]]") - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setText(words);
    }

}

//package com.iyuba.concept2.activity;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.iyuba.concept2.R;
//import com.iyuba.concept2.protocol.IntelTestQuesRequest;
//import com.iyuba.concept2.protocol.IntelTestQuesResponse;
//import com.iyuba.concept2.sqlite.mode.AbilityResult;
//import com.iyuba.concept2.sqlite.mode.IntelTestQues;
//import com.iyuba.concept2.util.NetWorkState;
//import com.iyuba.concept2.widget.DrawView;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.util.GetDeviceInfo;
//import com.iyuba.core.common.util.LogUtils;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.multithread.DownloadProgressListener;
//import com.iyuba.multithread.FileDownloader;
//import com.iyuba.multithread.MultiThreadDownloadManager;
//
//import java.io.File;
//import java.io.Serializable;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
///**
// * 展示听说读写各个模块能力图谱,加载网络试题
// *
// * @author liuzhenli
// * @version 1.0.0
// * @time 2016/9/? 16:42
// */
//
//public class AbilityMapSubActivity extends AppBaseActivity {
//    private int[] results = {0, 0, 0, 0, 0, 0};
//    private Context mContext;
//    private int totalTime = 0;
//    private final int FINISHSELF = 10000;
//    private final int GETTESTCONTENTS = 10001;
//    private final int BEGINTEST = 10002;
//    private final int SHOW_DIALOG = 10003;
//    private final int DOWNLOAD_AUDIO = 10004;
//    private final int DOWNLOAD_IMAGE = 10005;
//    /**
//     * 用户答对的试题个数
//     */
//    private int mDoRightNum;
//    private int mTotalTestNum = 100;//试题总数
//    private String lastTestTime;//上次测试时间
//    private ArrayList<IntelTestQues> mQuesLists;
//    private ProgressBar mPb_download_test;
//    private CustomDialog mWaittingDialog;
//    private String mAbilityType;
//
//    private Button mBtn_goto_test;
//
//    private long nextTime;
//    private long currentTime;
//
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.activity_ability_map_gettest;
//    }
//
//    @Override
//    protected void initVariables() {
//        mContext = this;
//        alertDialog = new CustomDialog(mContext, R.style.MyDialogStyle);
//    }
//
//
//    private String mTitle;
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        initCommons();
//        drawMap();
//
//        TextView tv_title = findView(R.id.tv_titlebar_sub);
//        String titleBarstr = "我的新概念英语能力图谱(" + mTitle + ")";
//        tv_title.setText(titleBarstr);
//        TextView tv_nextTextTime = findView(R.id.tv_next_testtime);
//
//
//        mBtn_goto_test = findView(R.id.btn_goto_test);
//        mBtn_goto_test.setOnClickListener(gotoAbilityTestClickListener());
//        //  CircularProgressButton cpbtn = findView(R.id.btnWithText);
//
//
//        //最后测试时间
//        String testRecordInfo;
//        TextView tv_lasttime_resut = findView(R.id.tv_result_advice);
//        if (mAbilityType.equals(Constant.ABILITY_WORD))
//            testRecordInfo = "上次测试时间:" + lastTestTime + "\n新概念考试总词汇:3313个\n" + "您目前掌握的词汇:" + mDoRightNum * 3313 / mTotalTestNum + "个";
//        else
//            testRecordInfo = "上次测试时间:" + lastTestTime + "\n" + mTitle + "测试总分:100分\n" + "您的测试成绩为:" + mDoRightNum * 100 / mTotalTestNum + "分";
//
//        tv_lasttime_resut.setText(testRecordInfo);
//
//
//        mPb_download_test = findView(R.id.pb_download);//下载的进度条
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy年MM月dd日");
//
//        if (!lastTestTime.equals("未知")) {//已经有过测试记录
//            try {
//                Date date = formatter.parse(lastTestTime);
//                nextTime = date.getTime() + 7 * 24 * 60 * 60 * 1000;//下次测试的时间  秒
//                currentTime = formatter.parse(new GetDeviceInfo(mContext).getCurrentTime()).getTime() / 1000;//当前时间 秒
//                // LogUtils.e("下次测试时间:" + nextTime + "  ");
//                tv_nextTextTime.setText("*提示:您可以在" + formatter2.format(nextTime) + "进行下次测试");
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//        }
//        mWaittingDialog = new CustomDialog(mContext);//等待进度条
//        mWaittingDialog.setTitle("正在准备，请稍后······");
//    }
//
//    /**
//     * 进入单词此时界面
//     */
//    private View.OnClickListener gotoAbilityTestClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //需要在用户登录状态下才可以加载试题
//                if (checkUserLoginAndLogin()) {
//                    if (nextTime > currentTime) {
//                        new AlertDialog.Builder(mContext).setTitle("您还需要" + leftTime(nextTime - currentTime * 1000) + "才能进行下一次测试")
//                                .setIcon(android.R.drawable.ic_dialog_info)
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // 点击“确认”后的操作
//                                    }
//                                }).show();
//                    } else {
//                        if (mQuesLists == null || mQuesLists.size() == 0 || mCompleteNum < mTotalFileCount2Down)//当前没有出题,开始出题
//                        {
//                            mWaittingDialog.show();
//                            hander.sendEmptyMessage(GETTESTCONTENTS);//从服务器获取试题内容
//                        } else {//题目已经出完
//                            mBtn_goto_test.setText("进入测试");
//                            if (!alertDialog.isShowing())
//                                // showToTestDialog();
//                                hander.sendEmptyMessage(SHOW_DIALOG);
//
//                        }
//                    }
//
//                }
//
//            }
//        };
//    }
//
//    @Override
//    protected void loadData() {
//
//    }
//
//    private Class mTargetClass;
//
//    /**
//     * 绘制图谱
//     */
//    private void drawMap() {
//        mAbilityType = getIntent().getStringExtra("abilityType");//W--单词,G--语法,L--听力,S--口语,R--阅读,X--写作
//        LinearLayout layout = findView(R.id.root);
//        final DrawView view = new DrawView(this);
//
//        switch (mAbilityType) {
//            case Constant.ABILITY_WRITE:
//                mTitle = "写作";
//                String[] abilityType1 = Constant.WRITE_ABILITY_ARR;
//                results = getResults(abilityType1.length);
//                view.setData(abilityType1, results);
//                mTargetClass = WriteAbilityTestActivity.class;
//                break;
//
//            case Constant.ABILITY_WORD:
//                mTitle = "单词";
//                String[] abilityType = Constant.WORD_ABILITY_ARR;
//                results = getResults(abilityType.length);
//                mTargetClass = WordAbilityTestActivity.class;
//                view.setData(abilityType, results);
//                break;
//            case Constant.ABILITY_GRAMMER:
//                mTitle = "语法";
//                String[] abilityType3 = Constant.GRAM_ABILITY_ARR;
//                results = getResults(abilityType3.length);
//                view.setData(abilityType3, results);
//                mTargetClass = GrammarAbilityTestActivity.class;
//                break;
//            case Constant.ABILITY_LISTEN:
//                mTitle = "听力";
//                String[] abilityType4 = Constant.LIS_ABILITY_ARR;
//                results = getResults(abilityType4.length);
//                view.setData(abilityType4, results);
//                mTargetClass = ListenAbilityTestActivity.class;
//                break;
//
//            case Constant.ABILITY_SPEAK:
//                mTitle = "口语";
//                String[] abilityType5 = Constant.SPEAK_ABILITY_ARR;
//                results = getResults(abilityType5.length);
//                view.setData(abilityType5, results);
//                mTargetClass = SpeakAbilityTestActivity.class;
//                break;
//            case Constant.ABILITY_READ:
//                mTitle = "阅读";
//                String[] abilityType6 = Constant.READ_ABILITY_ARR;
//                results = getResults(abilityType6.length);
//                view.setData(abilityType6, results);
//                mTargetClass = ReadAbilityTestActivity.class;
//                break;
//        }
//        view.setMinimumHeight((int) (300 * getScreenDensity()));//设置最小高度
//        view.setMinimumWidth((int) (300 * getScreenDensity()));//设置最小宽度
//        //通知view组件重绘
//        view.invalidate();
//        layout.addView(view);
//    }
//
//    private float getScreenDensity() {
//        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//        float density = dm.density;
//        return density;
//    }
//
//    /**
//     * 获取数据库中存储的内容: Score1  Score2 ......
//     *
//     * @param resCount
//     * @return
//     */
//    private int[] getResults(int resCount) {
//
//
//        results = new int[resCount];
//        mDoRightNum = 0;
//        lastTestTime = "未知";
//        ArrayList<AbilityResult> abilityResults = (ArrayList<AbilityResult>) getIntent().getSerializableExtra("resultLists");
//        //根据每个题目的类型转化成百分制成绩进行展示
//        if (abilityResults != null && abilityResults.size() > 0) {
//            switch (resCount) {
//                case 7:
//                    results[6] = getResult(abilityResults.get(abilityResults.size() - 1).Score7);
//                case 6:
//                    results[5] = getResult(abilityResults.get(abilityResults.size() - 1).Score6);
//                case 5:
//                    results[4] = getResult(abilityResults.get(abilityResults.size() - 1).Score5);
//                case 4:
//                    results[0] = getResult(abilityResults.get(abilityResults.size() - 1).Score1);
//                    results[1] = getResult(abilityResults.get(abilityResults.size() - 1).Score2);
//                    results[2] = getResult(abilityResults.get(abilityResults.size() - 1).Score3);
//                    results[3] = getResult(abilityResults.get(abilityResults.size() - 1).Score4);
//                    break;
//            }
//            mDoRightNum = abilityResults.get(abilityResults.size() - 1).DoRight;
//            lastTestTime = abilityResults.get(abilityResults.size() - 1).endTime;
//            mTotalTestNum = abilityResults.get(abilityResults.size() - 1).Total;
//        }
//
//        return results;
//    }
//
//    /**
//     * 根据数据表中获取的Score信息,转换为百分制的分数
//     *
//     * @param score 存储格式:本类型试题总数+用户答对的试题个数+测试类型
//     * @return
//     */
//    private int getResult(String score) {
//        mTotalTestNum = Integer.parseInt(score.split("\\+\\+")[0]);//试题总数
//        int userScore = Integer.parseInt(score.split("\\+\\+")[1]);//用户答对的题数
//        return mTotalTestNum == 0 ? 0 : userScore * 100 / mTotalTestNum;
//    }
//
//    private int mTotalFileCount2Down;//总共需要下载的文件数量
//    private ArrayList<IntelTestQues> mListWithSound; //需要下载音频
//    private ArrayList<IntelTestQues> mListWithImage;//需要下载图片
//    private ArrayList<IntelTestQues> mListWithAttach;//需要下载Txt文档
//    Handler hander = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case FINISHSELF:
//                    finish();
//                    break;
//                case GETTESTCONTENTS:
//                    mQuesLists = new ArrayList<IntelTestQues>();
//                    String lesson = "NewConcept1";
//                    LogUtils.e("准备出题了-----");
//                    ClientSession.Instance().asynGetResponse(
//                            new IntelTestQuesRequest(lesson, mAbilityType),
//                            new IResponseReceiver() {
//                                @Override
//                                public void onResponse(BaseHttpResponse response,
//                                                       BaseHttpRequest request, int rspCookie) {
//                                    IntelTestQuesResponse res = (IntelTestQuesResponse) response;
//
//                                    if (res.result.equals("1")) {//服务器返回数据
//                                        LogUtils.e("出题成功了");
//                                        if (mQuesLists != null && mQuesLists.size() > 0) {
//                                            mQuesLists.clear();
//                                        }
//                                        mQuesLists.addAll(res.mList);
//                                        totalTime = res.totalTime;
//                                        //下载音频和图片
//
//                                        mListWithSound = new ArrayList<IntelTestQues>();
//                                        mListWithImage = new ArrayList<IntelTestQues>();
//                                        mListWithAttach = new ArrayList<IntelTestQues>();
//                                        for (int i = 0; i < mQuesLists.size(); i++) {
//                                            if (mQuesLists.get(i).image != null && !mQuesLists.get(i).image.trim().equals("")) {
//                                                mListWithImage.add(mQuesLists.get(i));
//                                                LogUtils.e(mQuesLists.get(i).image);
//                                            }
//                                            if (mQuesLists.get(i).sound != null && !mQuesLists.get(i).sound.trim().equals("")) {
//                                                mListWithSound.add(mQuesLists.get(i));
//                                            }
//
//                                            if (mQuesLists.get(i).attach != null && !mQuesLists.get(i).attach.trim().equals("")) {
//                                                mListWithAttach.add(mQuesLists.get(i));
//                                            }
//
//                                            if (mQuesLists.get(i).testType.equals("4")) {
//                                                if (mQuesLists.get(i).choiceA != null && !mQuesLists.get(i).choiceA.trim().equals("")) {
//                                                    IntelTestQues itq = new IntelTestQues();
//                                                    itq.id = mQuesLists.get(i).id;
//                                                    itq.image = mQuesLists.get(i).choiceA;
//                                                    mListWithImage.add(itq);
//                                                    LogUtils.e(itq.image);
//                                                }
//
//                                                if (mQuesLists.get(i).choiceB != null && !mQuesLists.get(i).choiceB.trim().equals("")) {
//                                                    IntelTestQues itq = new IntelTestQues();
//                                                    itq.id = mQuesLists.get(i).id;
//                                                    itq.image = mQuesLists.get(i).choiceB;
//                                                    mListWithImage.add(itq);
//                                                    LogUtils.e(itq.image);
//                                                }
//
//                                                if (mQuesLists.get(i).choiceC != null && !mQuesLists.get(i).choiceC.trim().equals("")) {
//                                                    IntelTestQues itq = new IntelTestQues();
//                                                    itq.id = mQuesLists.get(i).id;
//                                                    itq.image = mQuesLists.get(i).choiceC;
//                                                    mListWithImage.add(itq);
//                                                    LogUtils.e(itq.image);
//                                                }
//
//                                                if (mQuesLists.get(i).choiceD != null && !mQuesLists.get(i).choiceD.trim().equals("")) {
//                                                    IntelTestQues itq = new IntelTestQues();
//                                                    itq.id = mQuesLists.get(i).id;
//                                                    itq.image = mQuesLists.get(i).choiceD;
//                                                    mListWithImage.add(itq);
//                                                    LogUtils.e(itq.image);
//                                                }
//                                            }
//                                        }
//                                        //显示进度条
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if (mWaittingDialog.isShowing())
//                                                    mWaittingDialog.dismiss();
//                                                mPb_download_test.setVisibility(View.VISIBLE);
//                                            }
//                                        });
//
//                                        mTotalFileCount2Down = mListWithSound.size() + mListWithImage.size() + mListWithAttach.size();
//                                        mPercent = 100.0f / mTotalFileCount2Down;
//
//
//                                        LogUtils.e("音频个数   " + mListWithSound.size() + "   图片个数  " + mListWithImage.size() + "txt个数:  " + mListWithAttach.size());
//                                        if (mListWithSound.size() + mListWithImage.size() + mListWithAttach.size() == 0) {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
////                                                    showToTestDialog();
//                                                    hander.sendEmptyMessage(SHOW_DIALOG);
//                                                    mBtn_goto_test.setText("进入测试");
//                                                }
//                                            });
//                                        }
//
//                                        if (mListWithSound.size() != 0)
//                                            checkNetwork(Integer.valueOf(mListWithSound.get(0).id), mListWithSound.get(0).sound);
//
//                                        if (mListWithImage.size() != 0)
//                                            checkNetwork(Integer.valueOf(mListWithImage.get(mListWithImage.size() - 1).id), mListWithImage.get(mListWithImage.size() - 1).image);
//
//                                        if (mListWithAttach.size() != 0)
//                                            checkNetwork(Integer.valueOf(mListWithAttach.get(mListWithAttach.size() - 1).id), mListWithAttach.get(mListWithAttach.size() - 1).attach);
//
//
//                                    } else {
//                                        LogUtils.e("返回数据为空,出题失败了");
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                ToastUtil.showLongToast(mContext, "网络连接失败,请检查网络是否可用");
//                                            }
//                                        });
//                                    }
//
//                                }
//                            }, null, null);
//                    break;
//                case BEGINTEST://开始测试
//                    hander.sendEmptyMessage(FINISHSELF);
//                    Intent intent = new Intent();
//                    intent.putExtra("QuestionList", ((Serializable) mQuesLists));
//                    intent.putExtra("totalTime", totalTime);
//                    intent.setClass(AbilityMapSubActivity.this, mTargetClass);
//                    startActivity(intent);
//                    break;
//
//                case SHOW_DIALOG:
//                    if (canShowdialog) {
//                        showToTestDialog();
//                    }
//                    break;
//                case DOWNLOAD_AUDIO:
//                    if (mTotalFileCount2Down < mListWithSound.size()) {//说明音频已经下载完成了
//                        checkNetwork(Integer.valueOf(mListWithSound.get(msg.arg1).id), mListWithSound.get(msg.arg1).sound);
//                    } else {//下载音频
//                        LogUtils.e("下载音频的第几项?" + (msg.arg1));
//                        checkNetwork(Integer.valueOf(mListWithImage.get(msg.arg1).id), mListWithImage.get(msg.arg1).image);
//                    }
//                    break;
//
//            }
//        }
//
//    };
//
//    /**
//     * 音频下载
//     */
//    private void checkNetwork(final int audioId, final String filename) {
//        // 判断有没有网络
//        int apnType = NetWorkState.getAPNType();
//        switch (apnType) {
//            case 0:// 没有网
//                ToastUtil.showToast(mContext, "网络连接失败,请检查网络是否可用");
//                break;
//            case 1:// 移动网络
//                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//                dialog.setTitle("提示");
//                dialog.setMessage("您目前的网络状态,下载将耗费手机流量,是否继续下载？");
//                dialog.setPositiveButton("确认",
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                downloadAudio(audioId, filename);
//                            }
//                        });
//                dialog.setNegativeButton("取消",
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                dialog.show();
//                break;
//            case 2:// wifi
//                downloadAudio(audioId, filename);
//                break;
//
//            default:
//                break;
//        }
//
//    }
//
//    private int mCompleteNum;
//    private float mPercent;
//
//    protected void downloadAudio(int audioId, final String filename) {
//
//        // 下载地址、存储路径
//        String downloadUrl = "";
//        String fileSaveDir;
//        int threadNum = 1;
//        fileSaveDir = Constant.envir + "audio/" + filename;
//        Log.e("filename", filename);
//        if (filename.toUpperCase().endsWith(".MP3")) {//音频
//            downloadUrl = Constant.ABILITY_AUDIO_URL_PRE + filename;
//            LogUtils.e("downloadUrl:" + downloadUrl);
//        } else if (filename.toUpperCase().endsWith(".PNG")) {//图片
//            downloadUrl = Constant.ABILITY_IMAGE_URL_PRE + filename;
//        } else if (filename.toUpperCase().endsWith(".TXT")) {//txt文档
//            downloadUrl = Constant.ABILITY_ATTACH_URL_PRE + filename;
//        }
//        LogUtils.e("downloadUrl:" + downloadUrl);
//        File file = new File(fileSaveDir);
//        if (file.exists()) {//音频存在不用下载
//            //发消息,接着下载
//            mCompleteNum++;
//            LogUtils.e("下载完成---" + mCompleteNum);
//            if (mCompleteNum < mListWithSound.size()) {//音频
//                downloadAudio(Integer.valueOf(mListWithSound.get(mCompleteNum).id), mListWithSound.get(mCompleteNum).sound);
//
//            } else if (mCompleteNum >= mListWithSound.size() && mCompleteNum < mListWithSound.size() + mListWithImage.size()) {//图片
//                downloadAudio(Integer.valueOf(mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).id), mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).image);
//
//            } else if (mCompleteNum < mTotalFileCount2Down) {//txt
//                downloadAudio(Integer.valueOf(mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).id), mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).attach);
//            } else {
//                LogUtils.e("全部下载完成了");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mBtn_goto_test.setText("进入测试");
//                        if (!alertDialog.isShowing())
//                            hander.sendEmptyMessage(SHOW_DIALOG);
////                            showToTestDialog();
//                    }
//                });
//            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mPb_download_test.setProgress((int) (mCompleteNum * mPercent));
//                    if ((int) (mCompleteNum * mPercent) < 100)
//                        mBtn_goto_test.setText((int) (mCompleteNum * mPercent) + "%");
//                    else
//                        mBtn_goto_test.setText("进入测试");
//                }
//            });
//        } else {
//            MultiThreadDownloadManager.enQueue(mContext, audioId, downloadUrl, new File(fileSaveDir), threadNum,
//                    new DownloadProgressListener() {
//
//                        @Override
//                        public void onProgressUpdate(int id, String downloadurl, int fileDownloadSize) {
//                        }
//
//                        @Override
//                        public void onDownloadStoped(int id) {
//                            LogUtils.e("下载停止了");
//                        }
//
//                        @Override
//                        public void onDownloadStart(FileDownloader fileDownloader, int id, int fileTotalSize) {
//
//                        }
//
//                        @Override
//                        public void onDownloadError(String errorMessage) {
//                        }
//
//                        @Override
//                        public void onDownloadComplete(int id, String savePathFullName) {
//                            mCompleteNum++;
//                            LogUtils.e("下载完成---" + mCompleteNum);
//                            //发消息,接着下载
//                            if (mCompleteNum < mListWithSound.size()) {//音频
//                                downloadAudio(Integer.valueOf(mListWithSound.get(mCompleteNum).id), mListWithSound.get(mCompleteNum).sound);
//
//                            } else if (mCompleteNum >= mListWithSound.size() && mCompleteNum < mListWithSound.size() + mListWithImage.size()) {//图片
//                                downloadAudio(Integer.valueOf(mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).id), mListWithImage.get(mListWithSound.size() + mListWithImage.size() - mCompleteNum - 1).image);
//
//                            } else if (mCompleteNum < mTotalFileCount2Down) {//txt
//                                downloadAudio(Integer.valueOf(mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).id), mListWithAttach.get(mTotalFileCount2Down - mCompleteNum - 1).attach);
//                            } else {
//                                LogUtils.e("全部下载完成了");
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        mPb_download_test.setVisibility(View.GONE);
//                                        mBtn_goto_test.setText("进入测试");
//                                        if (!alertDialog.isShowing())
//                                            hander.sendEmptyMessage(SHOW_DIALOG);
////                                            showToTestDialog();
//                                    }
//                                });
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mPb_download_test.setProgress((int) (mCompleteNum * mPercent));
//                                    if ((int) (mCompleteNum * mPercent) < 100)
//                                        mBtn_goto_test.setText((int) (mCompleteNum * mPercent) + "%");
//                                    else
//                                        mBtn_goto_test.setText("进入测试");
//                                }
//                            });
//                        }
//
//                    }
//            );
//        }
//
//    }
//
//    private CustomDialog alertDialog;
//
//    private void showToTestDialog() {//当前activity不可见的时候,调用show方法闪退
//        mPb_download_test.setVisibility(View.GONE);
//
//        /**自定义的dialog*/
//        LayoutInflater inflater2 = LayoutInflater.from(mContext);
//        View merge_view = inflater2.inflate(R.layout.dialog_custom, null);//要填充的layout
//
//        alertDialog.setContentView(merge_view);
//        alertDialog.show();//
//
//        TextView deleteTxt = (TextView) merge_view.findViewById(R.id.tv_dialog_title);//标题
//        if (deleteTxt != null)
//            deleteTxt.setText("准备测试");
//        TextView content = (TextView) merge_view.findViewById(R.id.tv_dialog_content);//提示的内容
//        if (content != null)
//            content.setText("提示:即将开始测试,测试时间为"+String.valueOf(totalTime)+"分钟");//NullPointException here
//
//        Button rechCancleBtn = (Button) merge_view.findViewById(R.id.btn_dialog_no);
//        Button rechOkBtn = (Button) merge_view.findViewById(R.id.btn_dialog_yes);
//        rechOkBtn.setText("开始测试");
//        rechCancleBtn.setText("以后再说");
//        rechCancleBtn.setOnClickListener(new View.OnClickListener() {//取消按钮
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//
//            }
//        });
//        rechOkBtn.setOnClickListener(new View.OnClickListener() {//开始按钮
//            @Override
//            public void onClick(View v) {
//                hander.sendEmptyMessage(BEGINTEST);
//                alertDialog.dismiss();
//            }
//        });
//    }
//
//    private String leftTime(long second) {
//        Log.e("second", String.valueOf(second));
//        second = second / 1000;
//        long hour = second / 3600;
//        long minute = second % 3600 / 60;
//        long leftSecond = second % 3600 % 60;
//
//        return String.valueOf(hour) + "时" + String.valueOf(minute) + "分" + String.valueOf(leftSecond) + "秒";
//    }
//
//    /**
//     * 是否可以显示dialog
//     */
//    private boolean canShowdialog = true;
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        canShowdialog = false;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        canShowdialog = true;
//    }
//}
