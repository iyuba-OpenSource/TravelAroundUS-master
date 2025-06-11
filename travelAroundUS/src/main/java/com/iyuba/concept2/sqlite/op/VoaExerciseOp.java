package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaExercise;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取做题数据库
 */
public class VoaExerciseOp extends DatabaseService{
	
	public static final String TABLE_NAME = "voa_exercise";
	public static final String VOA_ID = "voa_id";
	public static final String PROBLEM_N = "problem_N";
	public static final String EXC_TYPE = "exc_type";
	public static final String QUESTION = "question";
	public static final String CHOICE_A = "choice_A";
	public static final String CHOICE_B = "choice_B";
	public static final String CHOICE_C = "choice_C";
	public static final String CHOICE_D = "choice_D";
	public static final String ANSWER = "answer";

	public VoaExerciseOp(Context context) {
		super(context);
	}
	
	public synchronized void saveData(List<VoaExercise> voaExcercises) {
		if (voaExcercises != null && voaExcercises.size() != 0) {
			SQLiteDatabase sqLiteDatabase = importDatabase.openDatabase();
			sqLiteDatabase.beginTransaction();
			
			for (int i = 0; i < voaExcercises.size(); i++) {
				VoaExercise tempVoaExcercise = voaExcercises.get(i);
				sqLiteDatabase.execSQL(
						"insert into " + TABLE_NAME + " (" + VOA_ID + ","
								+ PROBLEM_N + "," + EXC_TYPE + "," + QUESTION
								+ "," + CHOICE_A + "," + CHOICE_B + ","
								+ CHOICE_C + "," + CHOICE_D+ "," + ANSWER
								+ ") values(?,?,?,?,?,?,?,?,?)", 
								new Object[] { tempVoaExcercise.voaId,
									tempVoaExcercise.problemN, tempVoaExcercise.excType,
									tempVoaExcercise.question, tempVoaExcercise.choiceA,
									tempVoaExcercise.choiceB, tempVoaExcercise.choiceC,
									tempVoaExcercise.choiceD, tempVoaExcercise.answer});
					}
			sqLiteDatabase.setTransactionSuccessful();
			sqLiteDatabase.endTransaction();
		closeDatabase(null);
		}
	}
	
	public  List<VoaExercise> findDataByVoaId(int voaId) {
		List<VoaExercise> voaExercises = new ArrayList<VoaExercise>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + PROBLEM_N + ", " + EXC_TYPE + ", "
						+ QUESTION + ", " + CHOICE_A + ", " + CHOICE_B + ","
						+ CHOICE_C + "," + CHOICE_D + "," +ANSWER+ " from " + TABLE_NAME
						+ " where " + VOA_ID + " = ?"
						, new String[] { String.valueOf(voaId) });
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaExercise voaExercise = new VoaExercise();
			voaExercise.voaId = cursor.getInt(0);
			voaExercise.problemN = cursor.getInt(1);
			voaExercise.excType = cursor.getString(2);
			voaExercise.question = cursor.getString(3);
			voaExercise.choiceA = cursor.getString(4);
			voaExercise.choiceB = cursor.getString(5);
			voaExercise.choiceC = cursor.getString(6);
			voaExercise.choiceD = cursor.getString(7);
			voaExercise.answer = cursor.getString(8);
			voaExercises.add(voaExercise);
		}
		if (cursor!=null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		if (voaExercises.size() != 0) {
			return voaExercises;
		}
		return null;
	}


	
}
