package com.iyuba.concept2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.iyuba.concept2.R;
import com.iyuba.concept2.listener.RequestCallBack;
import com.iyuba.concept2.manager.DataManager;
import com.iyuba.concept2.protocol.GetAbilityResultRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.MyPolygonView;
import com.iyuba.concept2.widget.DrawView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.lil.user.UserInfoManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 我的雅思英语能力图谱
 * 雅思能力测试 总界面
 * 包含 单词 语法 听力 口语 阅读 写作 等功能的入口
 *
 * @author liuzhenli 2016/9/? 10:51
 */

public class AbilityMapActivity extends AppBaseActivity {

    private final int GET_NET_RESULT = 10000;//从服务器获取已经上传的数据
    private final int GET_LOCAL_RESULT = 10001;//从数据库获取成绩数据

    ArrayList<AbilityResult> mWriteTestResults;
    ArrayList<AbilityResult> mWordTestResults;
    ArrayList<AbilityResult> mGrammarTestResults;
    ArrayList<AbilityResult> mListenTestResults;
    ArrayList<AbilityResult> mSpeakTestResults;
    ArrayList<AbilityResult> mReadTestResults;

    private Context mContext;
    private AbilityTestRecordOp helper;

    private String[] mAbilityTypeArr = Constant.ABILITY_TYPE_ARR;
    private int[] mResult = new int[mAbilityTypeArr.length];//测试结果,六个方面
    private AbilityTestRecordOp atro;
    private MyPolygonView muPolygonView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ability;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initVariables() {
        mContext = this;
        atro = new AbilityTestRecordOp(mContext);
        helper = new AbilityTestRecordOp(mContext);

        handler.sendEmptyMessage(GET_LOCAL_RESULT);//用户是否登录,都先从本地数据库获取数据进行展示

        //如果登录了,获取服务器保存的答题记录
        if (isUserLogin()) {

            //检测数据库中 答题记录是否有未上传的数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    uploadTestRecordToNet(String.valueOf(UserInfoManager.getInstance().getUserId()), 0, atro);
                }
            }).start();

            handler.sendEmptyMessage(GET_NET_RESULT);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initCommons();
        muPolygonView = (MyPolygonView) findViewById(R.id.muPolygonView);
        //文字
        String[] text = mAbilityTypeArr;
        //区域等级，值不能超过n边形的个数
        int[] area = mResult;
        muPolygonView.setArea(area);
        muPolygonView.setText(text);
        muPolygonView.setN(area.length);
        /**单词能力测试*/
        Button btn_words = findView(R.id.btn_ability_words);
        btn_words.setOnClickListener(wordAblityButtonClickListener());
        /**语法*/
        Button btn_grammar = findView(R.id.btn_ability_grammer);
        btn_grammar.setOnClickListener(grammarAbilityButtonClickListener());
        /**听力能力测试*/
        Button btn_listen = findView(R.id.btn_ability_listen);
        btn_listen.setOnClickListener(listenAblityButtonClickListener());
        /**口语*/
        Button btn_speak = findView(R.id.btn_ability_spoken);
        btn_speak.setOnClickListener(speakAblityButtonClickListener());
        /**阅读*/
        Button btn_read = findView(R.id.btn_ability_read);
        btn_read.setOnClickListener(readAbilityButtonClickListener());
        /**写作*/
        Button btn_write = findView(R.id.btn_ability_write);
        btn_write.setOnClickListener(writeAbilityButtonClickListener());

      /*  ImageButton ib_guide = findView(R.id.ibtn_test_guide);
       // ib_guide.setVisibility(View.VISIBLE);
        ib_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent();
                intent.setClass(mContext,GuideActivity.class);
                startActivity(intent);

            }
        });*/
    }


    /**
     * 写作能力测试点击事件
     */

    private View.OnClickListener writeAbilityButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTargetClass(AbilityMapSubActivity.class, mWriteTestResults, Constant.ABILITY_WRITE);
            }
        };
    }


    //单词能力测试
    private View.OnClickListener wordAblityButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTargetClass(AbilityMapSubActivity.class, mWordTestResults, Constant.ABILITY_WORD);
            }
        };
    }

    //语法能力测试点击事件
    @NonNull
    private View.OnClickListener grammarAbilityButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTargetClass(AbilityMapSubActivity.class, mGrammarTestResults, Constant.ABILITY_GRAMMER);
            }
        };
    }

    //听力能力测试
    private View.OnClickListener listenAblityButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTargetClass(AbilityMapSubActivity.class, mListenTestResults, Constant.ABILITY_LISTEN);
            }
        };
    }

    /**
     * 口语能力测试
     */
    private View.OnClickListener speakAblityButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTargetClass(AbilityMapSubActivity.class, mSpeakTestResults, Constant.ABILITY_SPEAK);
            }
        };
    }

    /**
     * 阅读能力测试点击事件
     */
    private View.OnClickListener readAbilityButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTargetClass(AbilityMapSubActivity.class, mReadTestResults, Constant.ABILITY_READ);
            }
        };
    }


    /**
     * 向目标activity跳转
     *
     * @param targetClass 目标activity
     */
    private void gotoTargetClass(Class targetClass, ArrayList arr, String abilityType) {
        Intent intent = new Intent();

        intent.setClass(AbilityMapActivity.this, targetClass);
        intent.putExtra("resultLists", arr);
        intent.putExtra("abilityType", abilityType);
        startActivity(intent);
    }


    @Override
    protected void loadData() {

    }

    /**
     * 六种能力单项分数
     *
     * @param testResults 数据库存储的数据
     * @return 分数
     */
    private int getAbilityTypeScore(ArrayList<AbilityResult> testResults) {

        int score = 0;

        if (testResults != null && testResults.size() != 0) {
            int doRight;
            int total;
            doRight = testResults.get(testResults.size() - 1).DoRight;
            total = testResults.get(testResults.size() - 1).Total;
            score = doRight * 100 / total;
        }

        return score;
    }

    private void drawAbilityMap() {

        LinearLayout layout = findView(R.id.root);
        layout.removeAllViews();
        DrawView view = new DrawView(this);
        view.setData(mAbilityTypeArr, mResult);
        view.invalidate();
        layout.addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DataManager.getInstance().abilityResList != null && DataManager.getInstance().abilityResList.size() > 0) {//全局变量存有数据
            obtainTestResults(DataManager.getInstance().abilityResList);
        } else {
            handler.sendEmptyMessage(GET_LOCAL_RESULT);//用户是否登录,都先从本地数据库获取数据进行展示
        }
        if (isUserLogin()) {
            handler.sendEmptyMessage(GET_NET_RESULT);
        }
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_NET_RESULT:
                    //从服务器获取上一次的测试结果
                    //GetAbilityResultRequest(String uid, String lesson, String testmode, int flag)

                    GetAbilityResultRequest request = new GetAbilityResultRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), "NewConcept1", "", 1, getCurTime(), new RequestCallBack() {
                        @Override
                        public void requestResult(Request res) {
                            GetAbilityResultRequest req = (GetAbilityResultRequest) res;
                            if (req.isRequestSuccessful()) {//获取网络数据成功了.重新绘制图谱
                                LogUtils.e("走了这里");
                                ArrayList<AbilityResult> results = req.resLists;

                                DataManager.getInstance().abilityResList = results;//全局变量

                                obtainTestResults(results);//赋值并绘制图谱
                            } else {//获取网络数据失败,从本地获取
                                handler.sendEmptyMessage(GET_LOCAL_RESULT);
                            }

                        }
                    });
                    CrashApplication.getInstance().getQueue().add(request);
                    break;
                case GET_LOCAL_RESULT:
                    //获取本地存储的写作评测结果   约40个题目
                    String uid = String.valueOf(UserInfoManager.getInstance().getUserId());//默认值是""
                    if (uid.equals("0")) {
                        uid = "";
                    }

                    //能力类型
                    mWriteTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_WRITE, uid, false);
                    mResult[0] = getAbilityTypeScore(mWriteTestResults);

                    //获取本地存储的单词评测结果 约100个题目
                    mWordTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_WORD, uid, false);
                    mResult[1] = getAbilityTypeScore(mWordTestResults);

                    //获取本地存储的语法评测结果 约60个题目
                    mGrammarTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_GRAMMER, uid, false);
                    mResult[2] = getAbilityTypeScore(mGrammarTestResults);

                    //获取本地存储的听力评测结果 约40个题目
                    mListenTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_LISTEN, uid, false);
                    mResult[3] = getAbilityTypeScore(mListenTestResults);

                    //获取本地存储的口语评测结果 约40个题目
                    mSpeakTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_SPEAK, uid, false);
                    mResult[4] = getAbilityTypeScore(mSpeakTestResults);

                    //获取本地存储的阅读评测结果 约40个题目
                    mReadTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_READ, uid, false);
                    mResult[5] = getAbilityTypeScore(mReadTestResults);
                    drawAbilityMap();

                    initMyPolygonView();
                    break;
            }
        }
    };

    private void obtainTestResults(ArrayList<AbilityResult> results) {
        for (int i = 0; i < results.size(); i++) {
            LogUtils.e(results.get(i).testMode + "  fenshu:  " + results.get(i).score);
            switch (results.get(i).testMode) {
                case Constant.ABILITY_WRITE:
                    mResult[0] = Integer.parseInt(results.get(i).score);
                    break;
                case Constant.ABILITY_WORD:
                    mResult[1] = Integer.parseInt(results.get(i).score);
                    break;
                case Constant.ABILITY_GRAMMER:
                    mResult[2] = Integer.parseInt(results.get(i).score);
                    break;
                case Constant.ABILITY_LISTEN:
                    mResult[3] = Integer.parseInt(results.get(i).score);
                    break;
                case Constant.ABILITY_SPEAK:
                    mResult[4] = Integer.parseInt(results.get(i).score);
                    break;
                case Constant.ABILITY_READ:
                    mResult[5] = Integer.parseInt(results.get(i).score);
                    break;
            }
        }
        drawAbilityMap();
        initMyPolygonView();
    }

    private String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }

    //加载多边形统计图
    private void initMyPolygonView() {
        String[] text = mAbilityTypeArr;
        //区域等级，值不能超过n边形的个数
        int[] area = mResult;

        muPolygonView.setArea(area);
        muPolygonView.setText(text);
        muPolygonView.setN(area.length);
        muPolygonView.invalidate();
    }

}




//package com.iyuba.concept2.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//
//import com.iyuba.concept2.R;
//import com.iyuba.concept2.protocol.GetAbilityResultRequest;
//import com.iyuba.concept2.sqlite.mode.AbilityResult;
//import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
//import com.iyuba.concept2.widget.DrawView;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.manager.AccountManager;
//
//import java.util.ArrayList;
//
///**
// * 我的雅思英语能力图谱
// * 雅思能力测试 总界面
// * 包含 单词 语法 听力 口语 阅读 写作 等功能的入口
// *
// * @author liuzhenli
// * @time 2016/9/? 10:51
// */
//
//public class AbilityMapActivity extends AppBaseActivity {
//
//    private final int GET_NET_RESULT = 10000;//从服务器获取已经上传的数据
//    ArrayList<AbilityResult> mWriteTestResults;
//    ArrayList<AbilityResult> mWordTestResults;
//    ArrayList<AbilityResult> mGrammerTestResults;
//    ArrayList<AbilityResult> mListenTestResults;
//    ArrayList<AbilityResult> mSpeakTestResults;
//    ArrayList<AbilityResult> mReadTestResults;
//
//    private Context mContext;
//    private AbilityTestRecordOp helper;
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.activity_ability;
//    }
//
//    @Override
//    protected void initVariables() {
//        mContext = this;
//
//        //获取服务器保存的答题记录
//        if (isUserLogin()) {
//            handler.sendEmptyMessage(GET_NET_RESULT);
//        }
//
//        helper = new AbilityTestRecordOp(mContext);
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        initCommons();
//        initDataAndDraw();
//        /**单词能力测试*/
//        Button btn_words = findView(R.id.btn_ability_words);
//        btn_words.setOnClickListener(wordAblityButtonClickListener());
//        /**语法*/
//        Button btn_grammer = findView(R.id.btn_ability_grammer);
//        btn_grammer.setOnClickListener(grammerAbilityButtonClickListener());
//        /**听力能力测试*/
//        Button btn_listen = findView(R.id.btn_ability_listen);
//        btn_listen.setOnClickListener(listenAblityButtonClickListener());
//        /**口语*/
//        Button btn_speak = findView(R.id.btn_ability_spoken);
//        btn_speak.setOnClickListener(speakAblityButtonClickListener());
//        /**阅读*/
//        Button btn_read = findView(R.id.btn_ability_read);
//        btn_read.setOnClickListener(readAbilityButtonClickListener());
//        /**写作*/
//        Button btn_write = findView(R.id.btn_ability_write);
//        btn_write.setOnClickListener(writeAbilityButtonClickListener());
//    }
//
//
//    //写作能力测试点击事件
//    @NonNull
//    private View.OnClickListener writeAbilityButtonClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoTargetClass(AbilityMapSubActivity.class, mWriteTestResults, Constant.ABILITY_WRITE);
//            }
//        };
//    }
//
//
//    //单词能力测试
//    private View.OnClickListener wordAblityButtonClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoTargetClass(AbilityMapSubActivity.class, mWordTestResults, Constant.ABILITY_WORD);
//            }
//        };
//    }
//
//    //语法能力测试点击事件
//    @NonNull
//    private View.OnClickListener grammerAbilityButtonClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoTargetClass(AbilityMapSubActivity.class, mGrammerTestResults, Constant.ABILITY_GRAMMER);
//            }
//        };
//    }
//
//    //听力能力测试
//    private View.OnClickListener listenAblityButtonClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoTargetClass(AbilityMapSubActivity.class, mListenTestResults, Constant.ABILITY_LISTEN);
//            }
//        };
//    }
//
//    //口语能力测试
//    private View.OnClickListener speakAblityButtonClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoTargetClass(AbilityMapSubActivity.class, mSpeakTestResults, Constant.ABILITY_SPEAK);
//            }
//        };
//    }
//
//    //阅读能力测试点击事件
//    @NonNull
//    private View.OnClickListener readAbilityButtonClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoTargetClass(AbilityMapSubActivity.class, mReadTestResults, Constant.ABILITY_READ);
//            }
//        };
//    }
//
//
//    /**
//     * 向目标activity跳转
//     *
//     * @param targetClass 目标activity
//     */
//    private void gotoTargetClass(Class targetClass, ArrayList arr, String abilityType) {
//        Intent intent = new Intent();
//
//        intent.setClass(AbilityMapActivity.this, targetClass);
//        intent.putExtra("resultLists", arr);
//        intent.putExtra("abilityType", abilityType);
//        startActivity(intent);
//    }
//
//
//    @Override
//    protected void loadData() {
//
//    }
//
//    /**
//     * 六种能力单项分数
//     *
//     * @param testResults 数据库存储的数据
//     * @return 分数
//     */
//    private int getAbilityTypeScore(ArrayList<AbilityResult> testResults) {
//        int score = 0;
//
//        if (testResults != null && testResults.size() != 0) {
//            int doRight;
//            int total;
//            doRight = testResults.get(testResults.size() - 1).DoRight;
//            total = testResults.get(testResults.size() - 1).Total;
//            score = doRight * 100 / total;
//        }
//        return score;
//    }
//
//    private int[] result = new int[6];//测试结果,六个方面
//
//    private void initDataAndDraw() {
//        String[] abilityType = {"写作", "单词", "语法", "听力", "口语", "阅读"};
//
//        //获取本地存储的写作评测结果   约40个题目
//        mWriteTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_WRITE);
//        result[0] = getAbilityTypeScore(mWriteTestResults);
//
//        //获取本地存储的单词评测结果 约100个题目
//        mWordTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_WORD);
//        result[1] = getAbilityTypeScore(mWordTestResults);
//
//        //获取本地存储的语法评测结果 约60个题目
//        mGrammerTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_GRAMMER);
//        result[2] = getAbilityTypeScore(mGrammerTestResults);
//
//        //获取本地存储的听力评测结果 约40个题目
//        mListenTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_LISTEN);
//        result[3] = getAbilityTypeScore(mListenTestResults);
//
//        //获取本地存储的口语评测结果 约40个题目
//        mSpeakTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_SPEAK);
//        result[4] = getAbilityTypeScore(mSpeakTestResults);
//
//        //获取本地存储的阅读评测结果 约40个题目
//        mReadTestResults = helper.getAbilityTestRecord(Constant.ABILITY_TETYPE_READ);
//        result[5] = getAbilityTypeScore(mReadTestResults);
//
//        LinearLayout layout = findView(R.id.root);
//        final DrawView view = new DrawView(this);
//        view.setData(abilityType, result);
//        view.invalidate();
//        layout.addView(view);
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        initDataAndDraw();
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case GET_NET_RESULT:
//                    //从服务器获取上一次的测试结果
//                    //GetAbilityResultRequest(String uid, String lesson, String testmode, int flag)
//                    new GetAbilityResultRequest(AccountManager.Instance(mContext).userId, "NewConcept1", "", 2);
//                    break;
//            }
//        }
//    };
//
//
//}
