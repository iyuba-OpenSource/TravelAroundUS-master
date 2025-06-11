package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaAnnotation;
import com.iyuba.concept2.sqlite.mode.VoaSummaryLanguageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取总结语言列表数据
 * 
 * @author 赵皓
 * @time 18.11.13
 * 
 */
public class SummaryLanguageOp extends DatabaseService {
	public static final String TABLE_NAME = "UsefulLanguage";
	public static final String ID = "lesson";
	//public static final String ANNO_N = "number";
	public static final String NOTE_EN = "noteEN";
	public static final String NOTE_CH = "noteCH";
	public static final String SENTENCE = "sentence";

	public SummaryLanguageOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<VoaAnnotation> annos) {
		if (annos != null && annos.size() != 0) {
			for (int i = 0; i < annos.size(); i++) {
				VoaAnnotation anno = annos.get(i);
				importDatabase.openDatabase().execSQL(
						"insert into " + TABLE_NAME + " (" + ID + ","
									+ NOTE_EN + "," + NOTE_CH + ") values(?,?,?,?)",
							new Object[] { anno.id, anno.annoN,
								anno.note});
					
				closeDatabase(null);
			}
		}

	}

	/**
	 * 批量修改
	 * 
	 * @param annos
	 */
	public synchronized void updateData(List<VoaAnnotation> annos) {
		if (annos != null && annos.size() != 0) {
			for (int i = 0; i < annos.size(); i++) {
				VoaAnnotation anno = annos.get(i);
				
				importDatabase.openDatabase().execSQL(
						"update " + TABLE_NAME + " set " + ID + "=' "
								+ anno.id + "'," + NOTE_EN + "='"
								+ anno.annoN + "', " + NOTE_CH + "='"
								+ anno.note);
				
				closeDatabase(null);
			}
		}
	}

	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized List<VoaSummaryLanguageBean> findDataByVoaId(int id) {
		List<VoaSummaryLanguageBean> annos = new ArrayList<VoaSummaryLanguageBean>();
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + ID + ", " + NOTE_EN + ", "
						+ NOTE_CH + ", "+ SENTENCE +" from "
						+ TABLE_NAME + " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaSummaryLanguageBean tempAnno = new VoaSummaryLanguageBean();
			tempAnno.voaId = cursor.getInt(0);
			tempAnno.noteEn = cursor.getString(1);
			tempAnno.notoCh = cursor.getString(2);
			tempAnno.sentence=cursor.getString(3);
			annos.add(tempAnno);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return annos;
	}


}
