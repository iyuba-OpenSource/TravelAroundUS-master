package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.AbilityResult;
import com.iyuba.concept2.sqlite.mode.TestRecord;
import com.iyuba.core.common.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */
public class AbilityTestRecordOp extends DatabaseService {

    public static final String UID = "uid";
    public static final String BEGINTIME = "BeginTime";
    public static final String LESSONID = "LessonId";
    public static final String TESTNUMBER = "TestNumber";
    public static final String ISUPLOAD = "IsUpload";
    public static final String TESTMODE = "TestMode";
    // TestRecord 字段
    public static final String TESTTIME = "TestTime";
    public static final String USERANSWER = "UserAnswer";
    public static final String RIGHTANSWER = "RightAnswer";
    public static final String ANSWERRESULT = "AnswerResult";


    public AbilityTestRecordOp(Context context) {
        super(context);
    }

    /**
     * @param testId
     * @return
     */
    public synchronized void setAbilityResultIsUpload(int testId) {
        String sqlString = "update TestRecord" + " set IsUpload = 1 "
                + " where TestNumber = '" + testId + "'";
        importDatabase.openDatabase().execSQL(sqlString);
    }

    /**
     * @param
     * @return
     */
    public synchronized void saveTestRecord(TestRecord testRecord) {
        String sqlString = "insert into TestRecord(uid,LessonId,TestNumber,UserAnswer,RightAnswer,AnswerResult,BeginTime,"
                + "TestTime,IsUpload,TestMode)" + " values(?,?,?,?,?,?,?,?,?,?)";
        Object[] objects = new Object[]{testRecord.uid, testRecord.LessonId,
                testRecord.TestNumber, testRecord.UserAnswer,
                testRecord.RightAnswer, testRecord.AnswerResult,
                testRecord.BeginTime, testRecord.TestTime, testRecord.IsUpload, testRecord.testMode};
        importDatabase.openDatabase().execSQL(sqlString, objects);
    }

    /**
     * @return List<StudyRecord>
     */

    public ArrayList<TestRecord> getWillUploadTestRecord() {

        ArrayList<TestRecord> testRecordList = new ArrayList<TestRecord>();
        String sqlSting = "select * from TestRecord" + " where IsUpload = '0'";
        Cursor cursor = null;
        try {
            cursor = importDatabase.openDatabase().rawQuery(
                    sqlSting, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    TestRecord testRecord = new TestRecord();
                    testRecord.uid = cursor.getString(cursor.getColumnIndex(UID));
                    testRecord.LessonId = cursor.getString(cursor.getColumnIndex(LESSONID));
                    testRecord.TestNumber = cursor.getInt(cursor.getColumnIndex(TESTNUMBER));
                    testRecord.BeginTime = cursor.getString(cursor.getColumnIndex(BEGINTIME));
                    testRecord.TestTime = cursor.getString(cursor.getColumnIndex(TESTTIME));
                    testRecord.UserAnswer = cursor.getString(cursor.getColumnIndex(USERANSWER));
                    testRecord.RightAnswer = cursor.getString(cursor.getColumnIndex(RIGHTANSWER));
                    testRecord.AnswerResult = cursor.getInt(cursor.getColumnIndex(ANSWERRESULT));
                    testRecord.IsUpload = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ISUPLOAD)));
                    testRecord.testMode = cursor.getString(cursor.getColumnIndex(TESTMODE));
                    testRecordList.add(i, testRecord);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return testRecordList;
    }

    /**
     * @param TestNumber
     * @return
     */
    public synchronized void setTestRecordIsUpload(int TestNumber) {
        String sqlString = "update TestRecord" + " set IsUpload = '1' " + " where TestNumber = '" + TestNumber + "'";
        importDatabase.openDatabase().execSQL(sqlString);
    }

    public synchronized void seveTestRecord(AbilityResult res) {
        String sqlString = "insert into ability_result(TypeId,Score1,Score2,Score3,Score4,Score5,Score6,Score7,Total,UndoNum,DoRight,beginTime,endTime,IsUpload,UserId)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] objects = new Object[]{res.TypeId, res.Score1, res.Score2, res.Score3, res.Score4, res.Score5, res.Score6, res.Score7, res.Total, res.UndoNum, res.DoRight, res.beginTime, res.endTime, 0, res.uid};
        Log.e("sqlString",sqlString);
        importDatabase.openDatabase().execSQL(sqlString, objects);
        closeDatabase(null);
    }

//    public ArrayList<AbilityResult> getAbilityTestRecord(int typeId) {
//
//        ArrayList<AbilityResult> resultList = new ArrayList<AbilityResult>();
//        Cursor cursor = importDatabase.openDatabase().rawQuery(
//                "select * " + " from " + "ability_result" + " where TypeId = ? "
//                ,
//                new String[]{String.valueOf(typeId)});
//        cursor.moveToFirst();
//
//        cursor.moveToFirst();
//        for (int i = 0; i < cursor.getCount(); i++) {
//            AbilityResult res = new AbilityResult();
//            res.TestId = cursor.getInt(0);
//            res.TypeId = cursor.getInt(1);
//            res.Score1 = cursor.getString(2);
//            res.Score2 = cursor.getString(3);
//            res.Score3 = cursor.getString(4);
//            res.Score4 = cursor.getString(5);
//            res.Score5 = cursor.getString(6);
//            res.Score6 = cursor.getString(7);
//            res.Score7 = cursor.getString(8);
//            res.Total = cursor.getInt(9);
//            res.UndoNum = cursor.getInt(10);
//            res.DoRight = cursor.getInt(11);
//            res.beginTime = cursor.getString(12);
//            res.endTime = cursor.getString(13);
//            res.isUpload = cursor.getInt(14);
//            resultList.add(res);
//            cursor.moveToNext();
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        closeDatabase(null);
//
//        if (resultList.size() != 0) {
//            return resultList;
//        }
//
//        return null;
//    }

    /***
     * 获取雅思能力测试结果
     *
     * @param typeId 本道题的类型
     * @param uid    用户的id
     * @param upload 获取的数据是否上传服务器
     * @return 测试结果
     */
    public ArrayList<AbilityResult> getAbilityTestRecord(int typeId, String uid, boolean upload) {

        ArrayList<AbilityResult> resultList = new ArrayList<>();
        Cursor cursor = null;
        String sqlString = "";

        if (!uid.equals("") && upload) {//获取需要上传服务器的数据  根据用户名和是否已经上传进行选择
            sqlString = "select * from ability_result where UserId =  " + uid + " and  IsUpload = 0";
            LogUtils.e("登录状态下上传服务器sql    " + sqlString);
        } else if (!uid.equals("")) {//用户登录过 展示本地数据时使用
            sqlString = "select * from ability_result where TypeId= " + typeId + " and UserId =  " + uid;
            LogUtils.e("登录状态下展示 sql    " + sqlString);
        } else {
            sqlString = "select * from ability_result where TypeId= " + typeId;//未登录状态下展示本地数据
            LogUtils.e("未登录状态下展示 sql    " + sqlString);
        }

        try {
            cursor = importDatabase.openDatabase().rawQuery(sqlString, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    AbilityResult res = new AbilityResult();
                    res.TestId = cursor.getInt(0);
                    res.TypeId = cursor.getInt(1);
                    res.Score1 = cursor.getString(2);
                    res.Score2 = cursor.getString(3);
                    res.Score3 = cursor.getString(4);
                    res.Score4 = cursor.getString(5);
                    res.Score5 = cursor.getString(6);
                    res.Score6 = cursor.getString(7);
                    res.Score7 = cursor.getString(8);
                    res.Total = cursor.getInt(9);
                    res.UndoNum = cursor.getInt(10);
                    res.DoRight = cursor.getInt(11);
                    res.beginTime = cursor.getString(12);
                    res.endTime = cursor.getString(13);
                    res.isUpload = cursor.getInt(14);
                    res.uid = cursor.getString(15);
                    resultList.add(res);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return resultList;
    }
}
