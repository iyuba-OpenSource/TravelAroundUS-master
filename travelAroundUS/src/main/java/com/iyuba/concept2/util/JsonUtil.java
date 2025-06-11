package com.iyuba.concept2.util;

import android.util.Log;


import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.LogUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;


public class JsonUtil {

    public static String buildJsonForTestRecord(List<TestRecord> tRecords) throws JSONException {
        String jsondata;
        JSONObject jsonRoot = new JSONObject();
        JSONArray json = new JSONArray();

        for (int i = 0; i < tRecords.size(); i++) {
            JSONObject jsonObj = new JSONObject();
            TestRecord tRecord = tRecords.get(i);
            jsonObj.put("uid", tRecord.uid);
            jsonObj.put("LessonId", tRecord.LessonId);
            jsonObj.put("TestNumber", tRecord.TestNumber);
            jsonObj.put("BeginTime", tRecord.BeginTime);
            jsonObj.put("TestTime", tRecord.TestTime);
            jsonObj.put("RightAnswer", tRecord.RightAnswer);
            jsonObj.put("UserAnswer", tRecord.UserAnswer);
            jsonObj.put("AnswerResut", tRecord.AnswerResult);
            jsonObj.put("index", tRecord.index);
            //把每个数据当作一对象添加到数组里
            json.put(jsonObj);

        }
        jsonRoot.put("datalist", json);
        jsondata = jsonRoot.toString();
        Log.e("JSON", jsondata);
        return jsondata;
        //调用解析JSON方法
        //parserJson(jsondata);
    }

    /**
     * 将答题记录和用户的成绩一起传递大数据
     * --
     * Warn:这里有一个问题,账户A的数据上传失败,同一设备登录账户B,上传数据的时候,账户A的数据可能上传B数据呢.
     * Solve:上传数据时比对账号是否相同
     */
    public static String buildJsonForTestRecordDouble(List<TestRecord> tRecords, List<AbilityResult> tResults, String uid) throws JSONException {
        String jsondata;
        JSONObject jsonRoot = new JSONObject();
        JSONArray array = new JSONArray();

        String strMd5 = uid + Constant.APPID + "NewConcept1" + "iyubaExam" + getCurTime();
        String sign = com.iyuba.core.common.util.MD5.getMD5ofStr(strMd5);

        //基本信息
        jsonRoot.put("uid", uid);
        jsonRoot.put("appId", Constant.APPID); //应用ID
        jsonRoot.put("lesson", "NewConcept1"); //课程类型
        jsonRoot.put("sign", sign);
        jsonRoot.put("format", "json"); //返回格式
        jsonRoot.put("DeviceId", android.os.Build.MODEL); //设备ID

        //测试记录
        for (int i = 0; i < tRecords.size(); i++) {
            JSONObject jsonObj = new JSONObject();
            TestRecord tRecord = tRecords.get(i);
            if (tRecord.uid.equals(uid)) {//登录的账户uid与数据库里面的uid相同才可以上传
                jsonObj.put("BeginTime", tRecord.BeginTime);
                jsonObj.put("TestTime", tRecord.TestTime);
                jsonObj.put("TestMode", tRecord.testMode);
                jsonObj.put("TestId", tRecord.TestNumber);
                jsonObj.put("LessonId", tRecord.LessonId);
                jsonObj.put("RightAnswer", tRecord.RightAnswer);
                jsonObj.put("UserAnswer", tRecord.UserAnswer);
                jsonObj.put("AnswerResut", tRecord.AnswerResult);
                jsonObj.put("Category", tRecord.category);//2016.10.26  add by Liuzhenli
                //把每个数据当作一对象添加到数组里
                array.put(jsonObj);
            }
        }
        jsonRoot.put("testList", array);

        //成绩分析记录
        array = new JSONArray();
        for (int i = 0; i < tResults.size(); i++) {
            AbilityResult tResult = tResults.get(i);
            if (tResult.uid.equals(uid)) {

                //把每个数据当作一对象添加到数组里
                if (!tResult.Score1.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score1));
                if (!tResult.Score2.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score2));
                if (!tResult.Score3.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score3));
                if (!tResult.Score4.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score4));
                if (!tResult.Score5.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score5));
                if (!tResult.Score6.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score6));
                if (!tResult.Score7.equals("-1"))
                    array.put(getScoreDetail(tResult.TypeId, tResult.Score7));
            }

        }
        jsonRoot.put("scoreList", array);
        jsondata = jsonRoot.toString();
        Log.e("JSON", jsondata);
        return jsondata;
    }

    public static JSONObject getScoreDetail(int typeid, String s) {
        JSONObject itemObject = new JSONObject();
        //Score里存储的数据格式  题目总数++正确数量++Category(能力)
        String[] res = s.split("\\+\\+");
        try {
            itemObject.put("lessontype", getLessonType(typeid));
            itemObject.put("category", res[2]);
            itemObject.put("testCnt", res[1]);//每一个模块答对的个数
            int score = Integer.parseInt(res[0]) == 0 ? 0 : Integer.parseInt(res[1]) * 100 / Integer.parseInt(res[0]);
            itemObject.put("Score", score + "");//得分转化为百分制
            LogUtils.e(getLessonType(typeid) + "       " + res[2] + "     " + score);
//                json.put(itemObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemObject;
    }

    public static String buildJsonForTestRecordSingle(TestRecord tRecord) throws JSONException {
        String jsondata;
        JSONObject jsonRoot = new JSONObject();
        JSONArray json = new JSONArray();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("uid", tRecord.uid);
        jsonObj.put("LessonId", tRecord.LessonId);
        jsonObj.put("TestNumber", tRecord.TestNumber);
        jsonObj.put("BeginTime", tRecord.BeginTime);
        jsonObj.put("TestTime", tRecord.TestTime);
        jsonObj.put("RightAnswer", tRecord.RightAnswer);
        jsonObj.put("UserAnswer", tRecord.UserAnswer);
        jsonObj.put("AnswerResut", tRecord.AnswerResult);
        //把每个数据当作一对象添加到数组里
        json.put(jsonObj);
        jsonRoot.put("datalist", json);
        jsondata = jsonRoot.toString();
        Log.e("JSON", jsondata);
        return jsondata;
        //调用解析JSON方法
        //parserJson(jsondata);
    }

    /**
     * 根据数据表里存放的编号,获取lessonType
     *
     * @param type 数据存储时的编号   0写作  1单词  语法....
     * @return
     */
    private static String getLessonType(int type) {
        String lessontype = "";
        switch (type) {

            case Constant.ABILITY_TETYPE_WRITE:
                lessontype = Constant.ABILITY_WRITE;
                break;

            case Constant.ABILITY_TETYPE_WORD:
                lessontype = Constant.ABILITY_WORD;
                break;

            case Constant.ABILITY_TETYPE_GRAMMER:
                lessontype = Constant.ABILITY_GRAMMER;
                break;

            case Constant.ABILITY_TETYPE_LISTEN:
                lessontype = Constant.ABILITY_LISTEN;
                break;

            case Constant.ABILITY_TETYPE_SPEAK:
                lessontype = Constant.ABILITY_SPEAK;
                break;

            case Constant.ABILITY_TETYPE_READ:
                lessontype = Constant.ABILITY_READ;
                break;
        }
        LogUtils.e("这里的lessonType    " + lessontype);
        return lessontype;
    }

    private static String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }

}
