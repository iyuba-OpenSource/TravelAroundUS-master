package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.NewWord;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取单词表数据库 
 */
public class MainWordOp extends DatabaseService {
	public static final String TABLE_NAME_WORD = "words";
	public static final String ID = "id";
	public static final String WORD = "word";
	public static final String AUDIO_URL = "audio";
	public static final String PRON = "pron";
	public static final String DEF = "def";
	public static final String VIEW_COUNT = "view_count";
	public static final String CREATE_DATE = "create_date";
	
	public MainWordOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(NewWord word) {
		String dateTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateTime = sdf.format(new Date());
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select * from " + TABLE_NAME_WORD + " where word='" + word.word
						+ "' AND id='" + word.id + "'", new String[] {});
		
		int databaseHasNum = cursor.getCount();
		
		if (databaseHasNum == 0) {
			importDatabase.openDatabase().execSQL(
					"insert into " + TABLE_NAME_WORD + " (" + ID + "," + WORD
							+ "," + AUDIO_URL + "," + PRON + "," + DEF + ","
							+ VIEW_COUNT + "," + CREATE_DATE  
							+ ") values(?,?,?,?,?,?,?,?)",
							new Object[] { word.id, word.word, word.audio,
								word.pron, word.def, word.viewCount, dateTime});
		}
		
		closeDatabase(null);
	}
	
	/**
	 * @return
	 */
	public synchronized NewWord findData(String word) {
		word = word.toLowerCase(); 
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + ID + "," + WORD + "," + AUDIO_URL + "," + PRON + ","
						+ DEF + "," + VIEW_COUNT + "," + CREATE_DATE 
						+ " from " + TABLE_NAME_WORD
						+ " where word ='" + word + "'", new String[] {});
		
		NewWord tempWord = null;
		
		if(cursor.getCount() != 0) {
			cursor.moveToFirst();
			
			tempWord = new NewWord();
			tempWord.id = String.valueOf(cursor.getInt(0));
			tempWord.word = cursor.getString(1);
			tempWord.audio = cursor.getString(2);
			
			try {
				tempWord.pron =URLDecoder.decode(cursor.getString(3), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			tempWord.def = cursor.getString(4);
			tempWord.viewCount = cursor.getString(5);
			tempWord.createDate = cursor.getString(6);
		}
		
		if (cursor!=null) {
			cursor.close();
		}
		closeDatabase(null);
		
		return tempWord;
	}
}
