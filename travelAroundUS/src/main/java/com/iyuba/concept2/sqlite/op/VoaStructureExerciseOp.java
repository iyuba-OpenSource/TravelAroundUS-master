package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaStructureExercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoaStructureExerciseOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_structure_exercise";
	public static final String ID = "id";
	public static final String DESC_EN = "desc_EN";
	public static final String DESC_CH = "desc_CH";
	public static final String NUMBER = "number";
	public static final String COLUMN = "column";
	public static final String NOTE = "note";
	public static final String TYPE = "type";
	public static final String QUES_NUM = "ques_num";
	public static final String ANSWER = "answer";

	public VoaStructureExerciseOp(Context context) {
		super(context);
	}

	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized List<Map<Integer, VoaStructureExercise>> findData1(int id) {
		List<Map<Integer, VoaStructureExercise>> structureMapList= new ArrayList<Map<Integer, VoaStructureExercise>>();
		Map<Integer, VoaStructureExercise> structureMap = null;
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + DESC_EN + ", " + DESC_CH + ", "
						+ NUMBER + ", " + NOTE + ", " + TYPE + ", " + ANSWER
						+ " from "+ TABLE_NAME 
						+ " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaStructureExercise structureExercise = new VoaStructureExercise();
			structureExercise.descEN = cursor.getString(0);
			structureExercise.descCN = cursor.getString(1);
			structureExercise.number = cursor.getInt(2);
			structureExercise.note = cursor.getString(3);
			structureExercise.type = cursor.getInt(4);
			structureExercise.answer = cursor.getString(5);
			
			if((structureExercise.descEN != null && !structureExercise.descEN.trim().equals("")) 
					|| (structureExercise.descCN != null && !structureExercise.descCN.trim().equals(""))) {
				if(structureMap != null) {
					structureMapList.add(structureMap);
				}
				
				structureMap = new HashMap<Integer, VoaStructureExercise>();
			}
			
			structureMap.put(structureExercise.number, structureExercise);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return structureMapList;
	}
	
	public synchronized Map<Integer, VoaStructureExercise> findData(int id) {
		Map<Integer, VoaStructureExercise> diffcultieMap = new HashMap<Integer, VoaStructureExercise>();
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + DESC_EN + ", " + DESC_CH + ", "+ NUMBER + ", " + 
						NOTE + ", "+ QUES_NUM + ", " + TYPE + ", " + ANSWER
						+ " from "+ TABLE_NAME 
						+ " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		int index = 0;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaStructureExercise structureExercise = new VoaStructureExercise();
			structureExercise.descEN = cursor.getString(0);
			structureExercise.descCN = cursor.getString(1);
			structureExercise.number = cursor.getInt(2);
			structureExercise.note = cursor.getString(3);
			structureExercise.quesNum = cursor.getInt(4);
			structureExercise.type = cursor.getInt(5);
			structureExercise.answer = cursor.getString(6);
			
			diffcultieMap.put(index++, structureExercise);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return diffcultieMap;
	}
}
