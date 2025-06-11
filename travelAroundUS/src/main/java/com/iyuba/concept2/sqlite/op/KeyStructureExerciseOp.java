package com.iyuba.concept2.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.KeyStructureExercise;

import android.content.Context;
import android.database.Cursor;

public class KeyStructureExerciseOp extends DatabaseService {
	public static final String TABLE_NAME = "key_structure_exercise";
	public static final String ID = "id";
	public static final String DESCRIBE_EN = "describe_EN";
	public static final String DESCRIBE_CN = "describe_CN";
	public static final String NUMBER = "number";
	public static final String NOTE = "note";
	public static final String TYPE = "type";
	public static final String QUESTION_NUMBER = "question_number";
	public static final String ANSWER = "answer";

	public KeyStructureExerciseOp(Context context) {
		super(context);
	}

	/**
	 * 查询id的数据
	 * 
	 * @return
	 */
	public synchronized List<KeyStructureExercise> findDataByVoaId(String id) {
		List<KeyStructureExercise> exercises= new ArrayList<KeyStructureExercise>();
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + ID + ", " + DESCRIBE_EN + ", " + DESCRIBE_CN + ", " + NUMBER
						+ ", " + NOTE + ", " + TYPE + ", " + QUESTION_NUMBER + ", " + ANSWER
						+ " from "+ TABLE_NAME 
						+ " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			KeyStructureExercise tempExercise = new KeyStructureExercise();
			tempExercise.id = cursor.getInt(0);
			tempExercise.describeEN = cursor.getString(1);
			tempExercise.describeCN = cursor.getString(2);
			tempExercise.number = cursor.getString(3);
			tempExercise.note = cursor.getString(4);
			tempExercise.type = cursor.getInt(5);
			tempExercise.questionNumber = cursor.getString(6);
			tempExercise.answer = cursor.getString(7);
			
			exercises.add(tempExercise);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return exercises;
	}


}
