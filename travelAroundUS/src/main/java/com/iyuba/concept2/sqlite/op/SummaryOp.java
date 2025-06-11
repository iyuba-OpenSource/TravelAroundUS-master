package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.SummaryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取总结语言列表数据
 * 
 * @author 赵皓
 * @time 18.11.13
 * 
 */
public class SummaryOp extends DatabaseService {
	public static final String TABLE_NAME = "Summary";
	public static final String ID = "lesson";
	//public static final String ANNO_N = "number";
	//public static final String NOTE_EN = "noteEN";
	//public static final String NOTE_CH = "noteCH";
	public static final String SENTENCE = "sentence";

	public SummaryOp(Context context) {
		super(context);
	}
	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized List<SummaryBean> findDataByVoaId(int id) {
		List<SummaryBean> annos = new ArrayList<SummaryBean>();
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + ID + ", " + SENTENCE +" from "
						+ TABLE_NAME + " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			SummaryBean tempAnno = new SummaryBean();
			tempAnno.voaId = cursor.getInt(0);
			tempAnno.sentence=cursor.getString(1);
			annos.add(tempAnno);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return annos;
	}


}
