package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaSound;
import com.iyuba.core.common.util.LogUtils;

import java.util.ArrayList;


/**
 * 语音评测数据
 */
public class VoaSoundOp extends DatabaseService {
    public static final String TABLE_NAME = "voa_sound";
    public static final String VOA_ID = "voa_id";
    public static final String WORDSCORE = "wordscore";
    public static final String TOTALSCORE = "totalscore";
    public static final String FILEPATH = "filepath";
    public static final String TIME = "time";
    public static final String ITEMID = "itemid";
    public static final String EvaluateWEBPATH = "evaluate_path";

    //新的句子评测表
    public static final String TABLE_NAME_NEW = "voa_sound_new";
    //新的列名
    public static final String WORDS = "words";


    private Context mContext;

    public VoaSoundOp(Context context) {
        super(context);
        mContext = context;
        addWebVoice();
    }
  private synchronized void addWebVoice(){
      if (checkSoundUrlExist()){
         //评测字段已经添加
          LogUtils.e("数据库，评测字段添加成功");
      }else {
          //没有网络评测音频
          try {
              importDatabase.openDatabase().execSQL(
                      "ALTER TABLE " + TABLE_NAME + " ADD " + EvaluateWEBPATH + " varchar ");
              LogUtils.e("数据库，添加评测字段");
          }catch (Exception e){
              LogUtils.e("添加评测字段失败，已经有了"+e);
              e.printStackTrace();
          }
      }

      //这里增加一个word字段，用于保存在评测句子之后返回的数据，然后根据返回的数据进行解析和处理
      if (checkTableColExist(WORDS)){
          //已经存在这个字段
      }else {
          try {
              importDatabase.openDatabase().execSQL(
                      "ALTER TABLE "+TABLE_NAME+" ADD "+WORDS+" varchar"
              );
          }catch (Exception e){
              e.printStackTrace();
          }
      }
      closeDatabase(null);
  }
    /**
     * 方法1：检查某表列是否存在
     *
     * @param
     * @return
     */
    private boolean checkSoundUrlExist() {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = importDatabase.openDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(EvaluateWEBPATH) != -1;
        } catch (Exception e) {
            LogUtils.e("数据库异常捕获，没有评测字段"+e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    //检查表中某列是否存在
    private boolean checkTableColExist(String colName){
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = importDatabase.openDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(colName) != -1;
        } catch (Exception e) {
            LogUtils.e("数据库异常捕获，没有评测字段"+e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 单一修改
     *
     * @param wordscore, totalscore,  voaId
     */
    public void updateWordScore(String wordscore, int totalscore, int voaId, String filepath, String time, int itemid,String evaluatePath) {


        importDatabase.openDatabase().execSQL(
                "insert or replace into " + TABLE_NAME + " (" + VOA_ID + ","
                        + WORDSCORE + "," + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID +"," + EvaluateWEBPATH +
                        " ) values(?,?,?,?,?,?,?)",
                new Object[]{voaId, wordscore, totalscore, filepath, time, itemid,evaluatePath});


        closeDatabase(null);
    }

    //将评测数据添加到数据库
    public void updateEvalData(String wordScore,int totalScore,int voaId,String filePath,String time,int itemId,String evaluatePath,String words){
        importDatabase.openDatabase().execSQL(
                "insert or replace into "+TABLE_NAME+"("
                        +VOA_ID+","+WORDSCORE+","+TOTALSCORE+","+FILEPATH+","+TIME+","+ITEMID+","+EvaluateWEBPATH+","+WORDS+
                        ") values(?,?,?,?,?,?,?,?)",
                new Object[]{voaId,wordScore,totalScore,filePath,time,itemId,evaluatePath,words}
        );

        closeDatabase(null);
    }

    /**
     * 根据itemid查询
     */
    public synchronized VoaSound findDataById(int itemid) {

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID+ "," + EvaluateWEBPATH+","+WORDS
                        + " from " + TABLE_NAME
                        + " where " + ITEMID + " =?",
                new String[]{String.valueOf(itemid)});


        if (cursor.moveToNext()) {
            VoaSound voaSound = fillIn(cursor);
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(null);
            return voaSound;
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return null;
    }


    /**
     * 根据voaid查询
     */
    public synchronized ArrayList<VoaSound> findDataByvoaId(int voaid) {

        ArrayList<VoaSound> voaSoundArrayList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID+ "," + EvaluateWEBPATH+","+WORDS
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + " =?",
                new String[]{String.valueOf(voaid)});


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaSound voaSound = new VoaSound();
            voaSound.voa_id = cursor.getInt(0);
            voaSound.wordScore = cursor.getString(1);
            voaSound.totalScore = cursor.getInt(2);
            voaSound.filepath = cursor.getString(3);
            voaSound.time = cursor.getString(4);
            voaSound.itemId = cursor.getInt(5);
            voaSound.evaluateWebPath = cursor.getString(6);

            //这里增加评测的数据
            voaSound.words = cursor.getString(7);

            voaSoundArrayList.add(voaSound);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return voaSoundArrayList;
    }

    private VoaSound fillIn(Cursor cursor) {
        VoaSound voaSound = new VoaSound();
        voaSound.voa_id = cursor.getInt(0);
        voaSound.wordScore = cursor.getString(1);
        voaSound.totalScore = cursor.getInt(2);
        voaSound.filepath = cursor.getString(3);
        voaSound.time = cursor.getString(4);
        voaSound.itemId = cursor.getInt(5);
        voaSound.evaluateWebPath = cursor.getString(6);

        //这里增加评测的数据
         voaSound.words = cursor.getString(7);
        return voaSound;
    }
}
