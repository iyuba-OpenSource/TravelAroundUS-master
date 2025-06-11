package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.UploadTestRecordRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.concept2.sqlite.op.AbilityTestRecordOp;
import com.iyuba.concept2.util.JsonUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;

import org.json.JSONException;

import java.util.ArrayList;



/**
 * 基类
 *
 * @author liuzhenli
 * @version 1.0.0
 * @time 2016/9 16:59
 */
public abstract class AppBaseActivity extends BaseActivity {

//    private ZDBHelper mZDBHelper;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

    }

    public void initCommons() {
        ImageButton selector_btn_bg = findView(R.id.btn_nav_sub);
        if (selector_btn_bg != null) {
            selector_btn_bg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });
        }
    }


    /**
     * alertdialog 提示用户是继续还是退出测试
     *
     * @param cur   当前进度
     * @param total 试题总数
     */
    public void showAlertDialog(final int cur, int total, final String type, final AbilityTestRecordOp atro,final ArrayList<IntelTestQues> mQuesList) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("提示:");
        dialog.setMessage("测试进度:" + cur + "/" + total + ",是否放弃测试?");
        dialog.setPositiveButton("离开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pidition) {
                ToastUtil.showToast(mContext, type + "测试未完成");
                //已经保存的数据库记录需要标记一下,不上传服务器
                for (int i = 0; i < cur - 1; i++) {
                    atro.setTestRecordIsUpload(Integer.valueOf(mQuesList.get(i).testId));
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

    private String jsonForTestRecord;

    /***
     * 上传测试结果到大数据
     *
     * @param uid     用户的id
     * @param ability 测试类型id 0写作...
     */
    public void uploadTestRecordToNet(String uid, int ability,AbilityTestRecordOp atro) {
        ArrayList<TestRecord> mTestRecordList = new ArrayList<TestRecord>();
        ArrayList<AbilityResult> mAbilityResultLists = new ArrayList<AbilityResult>();
        Log.e("atro",atro.toString());
        mTestRecordList = atro.getWillUploadTestRecord();//每一个题目
        mAbilityResultLists = atro.getAbilityTestRecord(ability, uid, true);//每一项能力结果
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
            UploadTestRecordRequest up = new UploadTestRecordRequest(jsonForTestRecord,url);

            String result = up.getResultByName("result");
            String jifen = up.getResultByName("jiFen");
            LogUtils.e("积分:" + jifen + "结果   " + result);
//            if (Integer.parseInt(jifen) > 0) {
//                ToastUtil.showToast(mContext, "测评数据成功同步到云端 +" + jifen + "积分");
//            }
            Message msg = new Message();
            msg.what = 1;
            msg.obj = jifen;
            handler.sendMessage(msg);

            TestRecord testRecords;
            AbilityResult aResult;
            if (!result.equals("-1")&&!result.equals("-2")) {// 成功
                int size = mTestRecordList.size();
                for (int i = 0; i < size; i++) {
                    testRecords = (TestRecord) mTestRecordList.toArray()[i];
                    //mZDBHelper.setTestRecordIsUpload(testRecords.TestNumber);
                    atro.setTestRecordIsUpload(testRecords.TestNumber);
                }
                for (int i = 0; i < mAbilityResultLists.size(); i++) {
                    aResult = (AbilityResult) mAbilityResultLists.toArray()[i];
                    atro.setAbilityResultIsUpload(aResult.TestId);
                }
            }
        } else {
            LogUtils.e("没有数据上传服务器");
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String jifen = (String) msg.obj;
                    if (Integer.parseInt(jifen) > 0) {
                        ToastUtil.showToast(mContext, "测评数据成功同步到云端 +" + jifen + "积分");
                    }
                    break;
            }
        }
    };
//    /***
//     *  上传测试结果到大数据
//     * @param uid 用户的id
//     * @param ability 测试类型id
//     */
//    public void uploadTestRecordToNet(String uid, int ability) {
//        ArrayList<TestRecord> mTestRecordList = atro.getWillUploadTestRecord();
//        ArrayList<AbilityResult> mAbilityResultLists = atro.getAbilityTestRecord(ability);
//        try {
//            jsonForTestRecord = JsonUtil.buildJsonForTestRecordDouble(mTestRecordList, mAbilityResultLists, uid);
//            LogUtils.e("hhhhhh", "buildJsonForTestRecord" + jsonForTestRecord);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        LogUtils.e("执行到的地方测试：", "获取将要上传的做题记录！！！！！！！");
//        //  jsonForTestRecord = URLEncoder.encode(jsonForTestRecord, "UTF-8").substring(jsonForTestRecord.indexOf("{"), jsonForTestRecord.lastIndexOf("}") + 1);
//        UploadAbilityTestRecordRequest up = new UploadAbilityTestRecordRequest(jsonForTestRecord);
//
//        String result = up.getResultByName("result");
//        String jifen = up.getResultByName("jiFen");
//        LogUtils.e("积分:" + jifen + "结果   " + result);
//
//        TestRecord testRecords;
//        AbilityResult aResult;
//        if (!result.equals("-1")) {// 成功
//            int size = mTestRecordList.size();
//            for (int i = 0; i < size; i++) {
//                testRecords = (TestRecord) mTestRecordList.toArray()[i];
//                atro.setTestRecordIsUpload(testRecords.TestNumber);
//            }
//            for (int i = 0; i < mAbilityResultLists.size(); i++) {
//                aResult = (AbilityResult) mAbilityResultLists.toArray()[i];
//                atro.setAbilityResultIsUpload(aResult.TestId);
//            }
//        }
//    }

}
