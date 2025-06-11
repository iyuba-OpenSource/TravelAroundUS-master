package com.iyuba.concept2.protocol;

import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 雅思能力测试返回的内容
 * Created by Administrator on 2016/8/30.
 */
public class AbilityTestQuestionResponse extends BaseJSONResponse {

    public int mResult;
    public int mTotal;
    public int mTestTime;
    public ArrayList<IntelTestQues> mQuestionLists = new ArrayList<>();

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String jsonBody) {


        String responseString = jsonBody.toString().trim();
        ArrayList<IntelTestQues> arraylist = new ArrayList<>();

        try {
            JSONObject jsonbody = new JSONObject(jsonBody);

            if (jsonbody.has("result")) {
                LogUtils.e("reuslt: " + jsonbody.getString("result"));
                LogUtils.e("json:  " + jsonbody.toString());
                mResult = Integer.parseInt(jsonbody.getString("result"));
            }
            if (mResult == 1) {//请求返回标记
                mTotal = jsonbody.getInt("Total");
                mTestTime = jsonbody.getInt("Time");
//                LogUtils.e("试题总数:  " + mTotal);
                if (jsonbody.has("TestList")) {//判断是否有该字段
                    JSONArray data = jsonbody.getJSONArray("TestList");
                    JSONObject opt;
                    if (data != null && data.length() != 0) {
                        for (int i = 0; i < data.length(); i++) {
                            IntelTestQues listenQues = new IntelTestQues();
                            opt = data.getJSONObject(i);
                            if (opt.has("TestId"))
                                listenQues.testId = opt.getString("TestId").trim();
                            // LogUtils.e("TestId "+listenQues.TestId);
                            if (opt.has("Sounds"))
                                listenQues.sound = opt.getString("Sounds").trim();
                            if (opt.has("Answer"))
                                listenQues.answer = opt.getString("Answer").trim();
                            if (opt.has("Category"))
                                listenQues.category = opt.getString("Category").trim();
                            if (opt.has("Question"))
                                listenQues.question = opt.getString("Question").trim();
                            if (opt.has("id"))
                                listenQues.id = opt.getString("id").trim();
                            if (opt.has("TestType"))
                                listenQues.testType = opt.getString("TestType").trim();
                            if (opt.has("Tags"))
                                listenQues.tags = opt.getString("Tags").trim();
                            if (opt.has("Answer1"))
                                listenQues.choiceA = opt.getString("Answer1").trim();
                            if (opt.has("Answer2"))
                                listenQues.choiceB = opt.getString("Answer2").trim();
                            if (opt.has("Answer3"))
                                listenQues.choiceC = opt.getString("Answer3").trim();
                            if (opt.has("Answer4"))
                                listenQues.choiceD = opt.getString("Answer4").trim();
                            if (opt.has("Pic"))
                                listenQues.image = opt.getString("Pic");
                            if (opt.has("Attach"))
                                listenQues.attach = opt.getString("Attach");
                            mQuestionLists.add(listenQues);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
