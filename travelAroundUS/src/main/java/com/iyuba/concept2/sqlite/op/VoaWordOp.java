package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaWord;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取单词数据库
 */
public class VoaWordOp extends DatabaseService {

    public static final String TABLE_NAME = "Words";//voa_word
    public static final String VOA_ID = "id";
    public static final String WORD = "Word";
    public static final String DEF = "def";
    public static final String AUDIO = "audio";
    public static final String PRON = "pron";
   // public static final String EXAMPLES = "examples";  去掉了这个字段


    public VoaWordOp(Context context) {
        super(context);
    }

    /**
     * 批量插入数据
     */
    public synchronized void saveData(List<VoaWord> voaWords) {

        if (voaWords != null && voaWords.size() != 0) {
            for (int i = 0; i < voaWords.size(); i++) {
                VoaWord tempword = voaWords.get(i);
                importDatabase.openDatabase().execSQL(
                        "insert into " + TABLE_NAME + " (" + VOA_ID + ","
                                 + WORD + "," + DEF + "," + AUDIO
                                + "," + PRON  + ") values(?,?,?,?,?,?)",  //+ "," + EXAMPLES + "
                        new Object[]{tempword.voaId, tempword.word,
                                tempword.def, tempword.audio,
                                tempword.pron/*, tempword.examples*/});
                closeDatabase(null);
            }
        }
    }

    /**
     * 查找
     *
     * @return
     */
    public synchronized List<VoaWord> findDataByVoaId(int voaId) {
        List<VoaWord> voaWords = new ArrayList<VoaWord>();

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + ","
                        + AUDIO + "," + PRON +" from " + TABLE_NAME
                        + " where " + VOA_ID + "= '" + voaId + "'",
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaWord tempWord = new VoaWord();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            //tempWord.examples = cursor.getString(5);
            voaWords.add(tempWord);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (voaWords.size() != 0) {
            return voaWords;
        }

        return null;
    }

    public synchronized int findWordCount() {
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * " + " from " + TABLE_NAME
                ,
                null);
        cursor.moveToFirst();
        int num = cursor.getCount();
        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);
        return num;
    }

//    public synchronized List<VoaWord> findDataByVoaIds( HashSet<Integer> integerHashSet) {
//        List<VoaWord> voaWords = new ArrayList<VoaWord>();
//        Iterator<Integer> iterator=integerHashSet.iterator();
//        Cursor cursor = importDatabase.openDatabase().rawQuery(
//                "select * " + " from " + TABLE_NAME
//                ,
//                null);
//        cursor.moveToFirst();
//        for (int i = 0; i < integerHashSet.size(); i++) {
//            cursor.moveToPosition(iterator.next());
//            VoaWord tempWord = new VoaWord();
//            tempWord.voaId = cursor.getString(0);
//            tempWord.word = cursor.getString(1);
//            tempWord.def = cursor.getString(2);
//            tempWord.audio = cursor.getString(3);
//            tempWord.pron = cursor.getString(4);
//            tempWord.examples = cursor.getString(5);
//            voaWords.add(tempWord);
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        closeDatabase(null);
//
//        if (voaWords.size() != 0) {
//            return voaWords;
//        }
//
//        return null;
//    }

    public synchronized List<VoaWord> findDataByVoaIds( List<Integer> wordIndex) {
        List<VoaWord> voaWords = new ArrayList<VoaWord>();
//        Iterator<Integer> iterator=integerHashSet.iterator();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * " + " from " + TABLE_NAME
                ,
                null);
        cursor.moveToFirst();
        for (int i = 0; i < wordIndex.size(); i++) {
            cursor.moveToPosition(wordIndex.get(i));
            VoaWord tempWord = new VoaWord();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            //tempWord.examples = cursor.getString(5);
            voaWords.add(tempWord);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (voaWords.size() != 0) {
            return voaWords;
        }

        return null;
    }


    public void updateData(VoaWord voaWord) {
        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME
                        + " set " + PRON + " = '" + voaWord.pron
                        + "'," + AUDIO + " = '" + voaWord.audio
                        + "' where " + WORD + " = '" + voaWord.word + "'");

        closeDatabase(null);
    }

}
