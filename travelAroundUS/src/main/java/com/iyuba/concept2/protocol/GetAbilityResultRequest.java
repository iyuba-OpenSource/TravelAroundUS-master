package com.iyuba.concept2.protocol;


import com.android.volley.Response.Listener;
import com.iyuba.concept2.listener.RequestCallBack;
import com.iyuba.concept2.network.BaseJsonObjectRequest;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.MD5;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 获取上次测试的结果
 * Created by Liuzhenli on 2016/8/30.
 */
public class GetAbilityResultRequest extends BaseJsonObjectRequest {

    public String message;// 返回信息
    public String result;// 返回代码
    public ArrayList<AbilityResult> resLists = new ArrayList<>();


    /**
     * @param lesson   测试课程  NewConcept1 NewConcept2 NewConcept3 NewConcept4  IELTS   Toefl  cet4 cet6
     * @param testmode 测试的内容  W--Word--单词  G--语法 L--听力 S--口语   R--阅读   X--写作
     * @param flag     1:代表取听说读写单词语法这一级的测试成绩且只取最后一次测试的;
     *                 2:代表取听说读写单词语法这一级的测试成绩且只取出所有的;
     *                 3:代表取(听或说或读或写或单词或语法)下面子测试成线,且只取最后一次测试的.
     *                 4:代表取(听或说或读或写或单词或语法)下面子测试成线, 且取所有的
     */
    public GetAbilityResultRequest(String uid, String lesson, String testmode, int flag, String curTime, final RequestCallBack callback) {

        super("http://daxue."+Constant.IYBHttpHead()+"/ecollege/getExamScore.jsp?appId=" + Constant.APPID
                + "&uid=" + uid
                + "&lesson=" + "NewConcept1"
                + "&testMode=" + testmode
                + "&flg=" + flag
                + "&sign=" + MD5.getMD5ofStr(uid + lesson + flag + testmode + Constant.APPID + curTime)
                + "&format=json");

        //Md5(uid+lesson+flg+testMode++appId+"YYYY-MM-DD"); YYYY-MM-DD是系统日期
        //sign = md5.getMD5ofStr(uid + lesson +flag+ testmode + Constant.APPID + getCurTime());
        // String url = "http://class."+Constant.IYBHttpHead+"/getClass.iyuba?&protocol=20000&lesson="+lesson+"&category="+category+"&sign="+sign+"&format=json";

        LogUtils.e("get testresult's url is:   " + "http://daxue."+Constant.IYBHttpHead()+"/ecollege/getExamScore.jsp?appId=" + Constant.APPID
                + "&uid=" + uid
                + "&lesson=" + "NewConcept1"
                + "&testMode=" + testmode
                + "&flg=" + flag
                + "&sign=" + MD5.getMD5ofStr(uid + lesson + flag + testmode + Constant.APPID + curTime)
                + "&format=json");


        setResListener(new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonBody) {
                LogUtils.e("获取结果:   " + jsonBody.toString());
                try {
                    if (jsonBody.has("result"))
                        result = jsonBody.getString("result");

                    if (jsonBody.has("msg"))
                        message = jsonBody.getString("msg");

                    if (jsonBody.has("data")) {
                        result = "1";
                        JSONArray dataArr = jsonBody.getJSONArray("data");
                        if (dataArr != null) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject job = dataArr.getJSONObject(i);
                                AbilityResult result = new AbilityResult();
                                if (job.has("TestTime"))
                                    result.testTime = job.getString("TestTime");
                                if (job.has("Score"))
                                    result.score = job.getString("Score");
                                if (job.has("TestMode"))
                                    result.testMode = job.getString("TestMode");
                                if (job.has("Category"))
                                    result.category = job.getString("Category");
                                resLists.add(result);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.requestResult(GetAbilityResultRequest.this);
            }
        });
    }

    @Override
    public boolean isRequestSuccessful() {
        LogUtils.e(result);
        if ("1".equals(result)) {//1是正常有数据返回;0:正常没有数据; -1是异常
            return true;
        }
        return false;
    }
}




















