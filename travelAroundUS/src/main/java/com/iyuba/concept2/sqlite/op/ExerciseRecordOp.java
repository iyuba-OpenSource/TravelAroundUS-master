package com.iyuba.concept2.sqlite.op;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.ExerciseRecord;

import android.content.Context;
import android.database.Cursor;

/**
 * 获取单词表数据库 
 */
public class ExerciseRecordOp extends DatabaseService {
	public static final String TABLE_NAME = "exercise_record";
	public static final String UID = "uid";
	public static final String VOA_ID = "voa_id";
	public static final String INDEX_ID = "index_id";
	public static final String BEGIN_TIME = "begin_time";
	public static final String USER_ANSWER = "user_answer";
	public static final String RIGHT_ANSWER = "right_answer";
	public static final String ANSWER_RESULT = "answer_result";
	public static final String TEST_TIME = "test_time";
	
	public ExerciseRecordOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveOrUpdateData(Collection<ExerciseRecord> exerciseRecords) {
		
		if (exerciseRecords != null && exerciseRecords.size() != 0) {
			for (ExerciseRecord exerciseRecord : exerciseRecords) {
				Cursor cursor = importDatabase.openDatabase().rawQuery(
						"select * from " + TABLE_NAME + " where " + VOA_ID + "=? and index_id=?"
								, new String[] {exerciseRecord.voaId + "", exerciseRecord.TestNumber + ""});
				int databaseHasNum = cursor.getCount();
				closeDatabase(null);
				cursor.close();
				
				if (databaseHasNum == 0) {
					importDatabase.openDatabase().execSQL(
							"insert into " + TABLE_NAME + " (" + UID + "," + VOA_ID + ","
									+ INDEX_ID + "," + BEGIN_TIME + "," + USER_ANSWER + "," + RIGHT_ANSWER 
									+ "," + ANSWER_RESULT + "," + TEST_TIME + ") values(?,?,?,?,?,?,?,?)",
							new Object[] { exerciseRecord.uid, exerciseRecord.voaId,
									exerciseRecord.TestNumber, exerciseRecord.BeginTime, 
									exerciseRecord.UserAnswer, exerciseRecord.RightAnswer, 
									exerciseRecord.AnswerResut, exerciseRecord.TestTime });
				} else {
					importDatabase.openDatabase().execSQL(
							"update " + TABLE_NAME + " set " + BEGIN_TIME + "='"
									+ exerciseRecord.BeginTime + "', " + USER_ANSWER + "='"
									+ exerciseRecord.UserAnswer + "', " + RIGHT_ANSWER + "='"
									+ exerciseRecord.RightAnswer + "'," + ANSWER_RESULT + "='"
									+ exerciseRecord.AnswerResut + "'," + TEST_TIME + "='" 
									+ exerciseRecord.TestTime
									+ "' where " + VOA_ID + "='" + exerciseRecord.voaId
									+ "' and " + INDEX_ID + "='" + exerciseRecord.TestNumber + "'");
				}
				
				closeDatabase(null);
			}
		}
	}
	
	/**
	 * @return
	 */
	public synchronized Map<Integer, ExerciseRecord> findData(int voaId) {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + UID + "," + VOA_ID + "," + INDEX_ID + "," + BEGIN_TIME + "," 
						+ USER_ANSWER + "," + RIGHT_ANSWER + "," + ANSWER_RESULT + "," 
						+ ANSWER_RESULT + "," + TEST_TIME 
						+ " from " + TABLE_NAME
						+ " where " + VOA_ID + "=" + voaId , new String[] {});
		
		Map<Integer, ExerciseRecord> multChoiceMap = new HashMap<Integer, ExerciseRecord>();
		ExerciseRecord exerciseRecord = null;
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			exerciseRecord = new ExerciseRecord();
			exerciseRecord.uid = cursor.getInt(0);
			exerciseRecord.voaId = cursor.getInt(1);
			exerciseRecord.TestNumber = cursor.getInt(2);
			exerciseRecord.BeginTime = cursor.getString(3);
			exerciseRecord.UserAnswer = cursor.getString(4);
			exerciseRecord.RightAnswer = cursor.getString(5);
			exerciseRecord.AnswerResut = cursor.getInt(6);
			exerciseRecord.TestTime = cursor.getString(7);
			
			multChoiceMap.put(exerciseRecord.TestNumber, exerciseRecord);
		}
		
		if (cursor!=null) {
			cursor.close();
		}
		closeDatabase(null);
		
		return multChoiceMap;
	}
	
	public String tansformAnswer(String answer) {
		String result = "";
		
		switch(Integer.valueOf(answer)) {
		case 1:result = "a"; break;
		case 2:result = "b"; break;
		case 3:result = "c"; break;
		case 4:result = "d"; break;
		}
		
		return result;
	}
}
