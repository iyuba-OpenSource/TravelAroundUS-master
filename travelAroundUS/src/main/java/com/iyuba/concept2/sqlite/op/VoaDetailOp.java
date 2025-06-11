package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.mode.VoaDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取文章内容数据库
 */
public class VoaDetailOp extends DatabaseService {
	public static final String TABLE_NAME = "voadetail";//voa_detail
	public static final String VOA_ID = "VoaId";
	public static final String PARA_ID = "ParaId";
	public static final String INDEX_N = "IdIndex";
	public static final String START_TIME = "StartTiming";
	public static final String END_TIME = "EndTiming";
	public static final String TIMING = "Timing";
	public static final String SENTENCE = "Sentence";
	public static final String SENTENCE_CN = "Sentence_cn";
	public static final String IMG_PATH = "ImgPath";

	public VoaDetailOp(Context context) {
		super(context);
	}

	public void CreateTabSql() {
		closeDatabase(null);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<VoaDetail> textDetails) {
		if (textDetails != null && textDetails.size() != 0) {
			SQLiteDatabase sqLiteDatabase=importDatabase.openDatabase();
			sqLiteDatabase.beginTransaction();
			for (int i = 0; i < textDetails.size(); i++) {
				VoaDetail textDetail = textDetails.get(i);
				sqLiteDatabase.execSQL(
						"insert into " + TABLE_NAME + " (" + VOA_ID + ","
								+ PARA_ID + "," + INDEX_N + "," + START_TIME
								+ "," + END_TIME + "," + TIMING + "," + SENTENCE 
								+ "," + IMG_PATH + "," + SENTENCE_CN
								+ ") values(?, ?, ?, ?, ?, ?, ?, ?, ?)", 
								new Object[] { textDetail.voaId,
									textDetail.paraId, textDetail.lineN,
									textDetail.startTime, textDetail.endTime, 
									textDetail.timing, textDetail.sentence,
									textDetail.imgPath, textDetail.sentenceCn });
					}
			sqLiteDatabase.setTransactionSuccessful();
			sqLiteDatabase.endTransaction();
		closeDatabase(null);
		}
	}

	/**
	 * 
	 */
	public  List<VoaDetail> findDataByVoaId(int voaId) {
		List<VoaDetail> textDetails = new ArrayList<VoaDetail>();
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
						+ START_TIME + "," + END_TIME + "," + TIMING + ", " 
						+ SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN 
						+ " from " + TABLE_NAME
						+ " where " + VOA_ID + "=? ORDER BY " + START_TIME
						+ " ASC", new String[] { String.valueOf(voaId) });
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaDetail textDetail = new VoaDetail();
			textDetail.voaId = cursor.getInt(0);
			textDetail.paraId = cursor.getString(1);
			textDetail.lineN = cursor.getString(2);
			textDetail.startTime = cursor.getDouble(3);
			textDetail.endTime = cursor.getDouble(4);
			textDetail.timing = cursor.getDouble(5);
			textDetail.sentence = cursor.getString(6).replaceFirst("--- ", "");
			textDetail.imgPath = cursor.getString(7);
			textDetail.sentenceCn = cursor.getString(8);
			
			textDetails.add(textDetail);
		}
		
		if (cursor!=null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		if (textDetails.size() != 0) {
			return textDetails;
		}
		
		return null;
	}

	public  synchronized Map<Integer, Voa> findData(Map<Integer, Voa> voaMap, String str) {
		Voa tempVoa;
		int voaId;
		
		str = str.toLowerCase();
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID 
						+ " from " + TABLE_NAME
						+ " where lower(sentence) like '%" + str + "%'"
						+ " OR " + SENTENCE_CN + " like '%" + str + "%'"
						, new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voaId = cursor.getInt(0);
			tempVoa = voaMap.get(voaId);
			tempVoa.textFind = tempVoa.textFind + 1;
			
			voaMap.put(voaId, tempVoa);
		}
		
		if (cursor!=null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return voaMap;
	}
}
