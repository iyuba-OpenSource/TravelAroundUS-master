package com.iyuba.concept2.sqlite.op;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.StudyTime;

import android.content.Context;
import android.database.Cursor;

/**
 * 获取文章列表数据
 * 
 * @author ct
 * @time 12.9.27
 * 
 */
public class StudyTimeOp extends DatabaseService {
	public static final String TABLE_NAME = "study_time";
	public static final String ID = "id";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";

	public StudyTimeOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(StudyTime studyTime) {
		importDatabase.openDatabase().execSQL(
				"insert into " + TABLE_NAME + " (" 
						+ START_TIME + "," + END_TIME
						+ ") values(?,?)",
				new Object[] { studyTime.startTime, studyTime.endTime });
		
		closeDatabase(null);
	}

	public synchronized String getTodayStudyTime() {
		String dateTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateTime = sdf.format(new Date());
		
		String from = dateTime.substring(0, 10) + " 00:00:00";
		String to = dateTime.substring(0, 10) + " 23:59:59";
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + ID + ", " + START_TIME + ", " + END_TIME
						+ " from " + TABLE_NAME 
						+ " where " + START_TIME + ">= '" + from 
						+ "' and "+ END_TIME + "<= '" + to + "'", new String[] {});
		
		List<StudyTime> list = new ArrayList<StudyTime>();
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(fillIn(cursor));
		}
		
		closeDatabase(null);
		
		if (cursor!=null) {
			cursor.close();
		}
		
		return computeStudyTime(list);
	}

	public synchronized String getPreStudyTime() {
		List<StudyTime> list = new ArrayList<StudyTime>();
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + ID + ", " + START_TIME + ", " + END_TIME
						+ " from " + TABLE_NAME, new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(fillIn(cursor));
		}
		
		closeDatabase(null);
		
		if (cursor!=null) {
			cursor.close();
		}
		
		if(list.size() == 0) {
			return "00:00:00";
		} else {
			return computePreStudyTime(list);
		}
		
	}
	
	public synchronized int getStudyTimeCount() {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select COUNT(*) from " + TABLE_NAME, new String[] {});
		
		cursor.moveToFirst();
		int num = cursor.getInt(0);
		
		closeDatabase(null);
		
		if (cursor!=null) {
			cursor.close();
		}
		
		return num;
	}
	
	public String computeStudyTime(List<StudyTime> list) {
		String result = "";
		
		int hour = 0;
		int minute = 0;
		int second = 0;
		
		for(StudyTime studyTime : list) {
			hour += Integer.valueOf(studyTime.endTime.substring(11, 13)) - Integer.valueOf(studyTime.startTime.substring(11, 13));
			minute += Integer.valueOf(studyTime.endTime.substring(14, 16)) - Integer.valueOf(studyTime.startTime.substring(14, 16));
			second += Integer.valueOf(studyTime.endTime.substring(17, 19)) - Integer.valueOf(studyTime.startTime.substring(17, 19));
		}
		
		if(second < 0) {
			second += 60;
			minute--;
		}
		
		if(minute < 0) {
			minute += 60;
			hour--;
		}
		
		result = hour+ "-" + minute + "-"+second + "-";
		
		return result;
	}
	
	public String computePreStudyTime(List<StudyTime> list) {
		String result = "";
		
		int hour = 0;
		int minute = 0;
		int second = 0;
		
		for(StudyTime studyTime : list) {
			hour += Integer.valueOf(studyTime.endTime.substring(11, 13)) - Integer.valueOf(studyTime.startTime.substring(11, 13));
			minute += Integer.valueOf(studyTime.endTime.substring(14, 16)) - Integer.valueOf(studyTime.startTime.substring(14, 16));
			second += Integer.valueOf(studyTime.endTime.substring(17, 19)) - Integer.valueOf(studyTime.startTime.substring(17, 19));
		}
		
		if(second < 0) {
			second += 60;
			minute--;
		}
		
		if(minute < 0) {
			minute += 60;
			hour--;
		}
		
		result = hour + "-" + minute + "-"+second;
		
//		Log.e("result", result);
		
		return result;
	}
	
	private StudyTime fillIn(Cursor cursor){
		StudyTime studyTime = new StudyTime();
		studyTime.id = cursor.getInt(0);
		studyTime.startTime = cursor.getString(1);
		studyTime.endTime = cursor.getString(2);
		
		return studyTime;
	}
}
