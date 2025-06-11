package com.iyuba.core.common.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.common.sqlite.mode.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 获取单词表数据库
 *
 * @author ct
 * @time 12.9.27  现在使用中
 */
public class WordOp extends DatabaseService {
    public static final String TABLE_NAME_WORD = "word";
    public static final String ID = "user";
    public static final String KEY = " key ";
    public static final String LANG = "lang";
    public static final String AUDIOURL = "audiourl";
    public static final String PRON = "pron";
    public static final String DEF = "def";
    public static final String EXAMPLES = "examples";
    public static final String CREATEDATE = "createdate";
    public static final String ISDELETE = "isdelete";

    public WordOp(Context context) {
        super(context);
    }

    public synchronized void saveData(Word word) {
        String dateTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        dateTime = sdf.format(new Date());
        importDatabase.openDatabase().execSQL(
                "insert or replace into " + TABLE_NAME_WORD + " (" + ID + ","
                        + KEY + "," + LANG + "," + AUDIOURL + "," + PRON + ","
                        + DEF + "," + EXAMPLES + "," + CREATEDATE + ","
                        + ISDELETE + ") values(?,?,?,?,?,?,?,?,?)",
                new Object[]{word.userId, word.key, word.lang, word.audioUrl,
                        word.pron, word.def, word.examples, dateTime, "0"});//0为收藏 1为删除
        closeDatabase(null);

    }

    public synchronized void saveData(ArrayList<Word> words) {
        if (words != null && words.size() != 0) {
            int size = words.size();
            Word word;
            String dateTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.CHINA);
            for (int i = 0; i < size; i++) {
                word = words.get(i);
                dateTime = sdf.format(new Date());
                importDatabase.openDatabase().execSQL(
                        "insert or replace into " + TABLE_NAME_WORD + " (" + ID
                                + "," + KEY + "," + LANG + "," + AUDIOURL + ","
                                + PRON + "," + DEF + "," + EXAMPLES + ","
                                + CREATEDATE + "," + ISDELETE
                                + ") values(?,?,?,?,?,?,?,?,?)",
                        new Object[]{word.userId, word.key, word.lang,
                                word.audioUrl, word.pron, word.def,
                                word.examples, dateTime, "0"});
                closeDatabase(null);
            }
        }
    }

    /**
     * 查找本地数据库 当前用户收藏的全部单词
     *
     * @return
     */
    public synchronized List<Word> findDataByAll(String userid) {
        List<Word> words = new ArrayList<Word>();
        Cursor cursor = importDatabase.openDatabase()
                .rawQuery(
                        "select " + ID + "," + KEY + "," + LANG + ","
                                + AUDIOURL + "," + PRON + "," + DEF + ","
                                + EXAMPLES + "," + CREATEDATE + "," + ISDELETE
                                + " from " + TABLE_NAME_WORD + " where user='"
                                + userid + "' AND " + ISDELETE + "='0'"
                                + " ORDER BY " + ID + " DESC", new String[]{});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.userId = cursor.getString(0);
            word.key = cursor.getString(1);
            word.lang = cursor.getString(2);
            word.audioUrl = cursor.getString(3);
            word.pron = cursor.getString(4);
            word.def = cursor.getString(5);
            word.examples = cursor.getString(6);
            word.createDate = cursor.getString(7);
            word.delete = cursor.getString(8);
            words.add(word);
        }
        words.size();
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (words.size() != 0) {
            return words;
        }
        return null;
    }

    /**
     * 查找本地数据库 当前用户是否收藏了此单词
     *
     * @return
     */
    public synchronized boolean findDataByUser(String userId, String thisWord) {
        List<Word> words = new ArrayList<Word>();
        Cursor cursor = importDatabase.openDatabase()
                .rawQuery(
                        "select " + ID + "," + KEY + "," + LANG + ","
                                + AUDIOURL + "," + PRON + "," + DEF + ","
                                + EXAMPLES + "," + CREATEDATE + "," + ISDELETE
                                + " from " + TABLE_NAME_WORD + " where user='"
                                + userId + "' AND " + ISDELETE + "='0'"
                                + " ORDER BY " + ID + " DESC", new String[]{});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                word = new Word();
                word.userId = cursor.getString(0);
                word.key = cursor.getString(1);
                word.lang = cursor.getString(2);
                word.audioUrl = cursor.getString(3);
                word.pron = cursor.getString(4);
                word.def = cursor.getString(5);
                word.examples = cursor.getString(6);
                word.createDate = cursor.getString(7);
                word.delete = cursor.getString(8);
                words.add(word);
        }
        words.size();
        for (Word word1:words){
            if (word1.key.equals(thisWord)){
                cursor.close();
                closeDatabase(null);
                return true;
            }
        }
        cursor.close();
        closeDatabase(null);
        return false;
    }


    public synchronized List<Word> findDataByAllStatus(String userid) {
        List<Word> words = new ArrayList<Word>();
        Cursor cursor = importDatabase.openDatabase()
                .rawQuery(
                        "select " + ID + "," + KEY + "," + LANG + ","
                                + AUDIOURL + "," + PRON + "," + DEF + ","
                                + EXAMPLES + "," + CREATEDATE + "," + ISDELETE
                                + " from " + TABLE_NAME_WORD + " where user="
                                + userid, new String[]{});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.userId = cursor.getString(0);
            word.key = cursor.getString(1);
            word.lang = cursor.getString(2);
            word.audioUrl = cursor.getString(3);
            word.pron = cursor.getString(4);
            word.def = cursor.getString(5);
            word.examples = cursor.getString(6);
            word.createDate = cursor.getString(7);
            word.delete = cursor.getString(8);
            words.add(word);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (words.size() != 0) {
            return words;
        }
        return null;
    }

    /**
     * 查找删除的数据
     *
     * @return
     */
    public synchronized List<Word> findDataByDelete(String userid) {
        List<Word> words = new ArrayList<Word>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + ID + "," + KEY + "," + LANG + "," + AUDIOURL + ","
                        + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE
                        + "," + ISDELETE + " from " + TABLE_NAME_WORD
                        + " where user='" + userid + "' AND " + ISDELETE
                        + "='1'", new String[]{});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.userId = cursor.getString(0);
            word.key = cursor.getString(1);
            word.lang = cursor.getString(2);
            word.audioUrl = cursor.getString(3);
            word.pron = cursor.getString(4);
            word.def = cursor.getString(5);
            word.examples = cursor.getString(6);
            word.createDate = cursor.getString(7);
            word.delete = cursor.getString(8);
            words.add(word);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (words.size() != 0) {
            return words;
        }
        return null;
    }

    /**
     * 查找删除后的数据
     *
     * @return
     */
    public synchronized List<Word> findDataNow(String userid) {
        List<Word> words = new ArrayList<Word>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + ID + "," + KEY + "," + LANG + "," + AUDIOURL + ","
                        + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE
                        + "," + ISDELETE + " from " + TABLE_NAME_WORD
                        + " where user='" + userid + "' AND " + ISDELETE
                        + "!='1'", new String[]{});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.userId = cursor.getString(0);
            word.key = cursor.getString(1);
            word.lang = cursor.getString(2);
            word.audioUrl = cursor.getString(3);
            word.pron = cursor.getString(4);
            word.def = cursor.getString(5);
            word.examples = cursor.getString(6);
            word.createDate = cursor.getString(7);
            word.delete = cursor.getString(8);
            words.add(word);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (words.size() != 0) {
            return words;
        }
        return null;
    }

    /**
     * 删除
     *
     * @param userId ID集合，以�?”分�?每项加上""
     * @return
     */
    public synchronized boolean deleteItemWord(String userId) {
        try {
            importDatabase.openDatabase().execSQL(
                    "delete from " + TABLE_NAME_WORD + " where " + ID + "='"
                            + userId + "' AND " + ISDELETE + "='1'");
            closeDatabase(null);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置状态为删除
     *
     * @param keys
     * @param userId
     * @return
     */
    public synchronized boolean tryToDeleteItemWord(String keys, String userId) {
        try {
            importDatabase.openDatabase().execSQL(
                    "update " + TABLE_NAME_WORD + " set " + ISDELETE
                            + "='1' where " + ID + "='" + userId + "' AND "
                            + KEY + " in (" + keys + ")");
            closeDatabase(null);
            LogUtil.e("单词 删除成功"+keys);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("单词 删除失败原因"+e.toString());
            return false;
        }
    }

    //删除
    public synchronized boolean deleteKeyWord(String keys, String userId) {
        try {
            importDatabase.openDatabase().execSQL(
                    "delete from " + TABLE_NAME_WORD + " where " + ID + "='"
                            + userId + "' AND " + KEY + " in (" + keys + ")");
            closeDatabase(null);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
}
