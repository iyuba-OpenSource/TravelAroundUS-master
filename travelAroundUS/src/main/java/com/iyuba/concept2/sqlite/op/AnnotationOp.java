package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取文章列表数据
 * 
 * @author ct
 * @time 12.9.27
 * 
 */
public class AnnotationOp extends DatabaseService {
	public static final String TABLE_NAME = "Note";//voa_annotation
	public static final String ID = "id";
	public static final String ANNO_N = "number";
	public static final String NOTE = "note";
	public static final String ANNO_DETAIL = "anno_detail";

	public AnnotationOp(Context context) {
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
									+ ANNO_N + "," + NOTE + ") values(?,?,?,?)",
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
								+ anno.id + "'," + ANNO_N + "='"
								+ anno.annoN + "', " + NOTE + "='"
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
	public synchronized List<VoaAnnotation> findDataByVoaId(int id) {
		List<VoaAnnotation> annos = new ArrayList<VoaAnnotation>();
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + ID + ", " + ANNO_N + ", "
						+ NOTE + " from "
						+ TABLE_NAME + " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaAnnotation tempAnno = new VoaAnnotation();
			tempAnno.id = cursor.getInt(0);
			tempAnno.annoN = cursor.getInt(1);
			tempAnno.note = cursor.getString(2);
			
			annos.add(tempAnno);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return annos;
	}


}
