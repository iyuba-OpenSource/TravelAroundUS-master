package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaLifePointBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取文章列表数据
 * 
 * @author ct
 * @time 12.9.27
 * 
 */
public class LifeDataOp extends DatabaseService {
	public static final String TABLE_NAME = "LifePoint";//voa_annotation
	public static final String ID = "id";
	public static final String LESSON = "lesson";
	public static final String ART = "act";
	public static final String ANNO_N = "number";
	public static final String NOTE = "note";

	public LifeDataOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<VoaLifePointBean> annos) {
		if (annos != null && annos.size() != 0) {
			for (int i = 0; i < annos.size(); i++) {
				VoaLifePointBean anno = annos.get(i);
				importDatabase.openDatabase().execSQL(
						"insert into " + TABLE_NAME + " (" + ID + ","
									+ ANNO_N + "," + NOTE + ") values(?,?,?,?)",
							new Object[] { anno.id, anno.number,
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
	public synchronized void updateData(List<VoaLifePointBean> annos) {
		if (annos != null && annos.size() != 0) {
			for (int i = 0; i < annos.size(); i++) {
				VoaLifePointBean anno = annos.get(i);
				
				importDatabase.openDatabase().execSQL(
						"update " + TABLE_NAME + " set " + ID + "=' "
								+ anno.id + "'," + ANNO_N + "='"
								+ anno.number + "', " + NOTE + "='"
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
	public synchronized List<VoaLifePointBean> findDataByVoaId(int lesson,int art) {
		List<VoaLifePointBean> annos = new ArrayList<VoaLifePointBean>();
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + LESSON + ", "+ ART + ", " + ANNO_N + ", "
						+ NOTE + " from "
						+ TABLE_NAME + " WHERE " + LESSON + " = " + lesson + " and "+ ART + " = " + art
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaLifePointBean tempAnno = new VoaLifePointBean();
			tempAnno.lesson = cursor.getInt(0);
			tempAnno.art = cursor.getInt(1);
			tempAnno.number = cursor.getInt(2);
			tempAnno.note = cursor.getString(3);
			annos.add(tempAnno);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return annos;
	}


}
