package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaDiffculty;

public class VoaDiffcultyOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_diffculty";
	public static final String ID = "id";
	public static final String DESC_EN = "desc_EN";
	public static final String DESC_CH = "desc_CH";
	public static final String NUMBER = "number";
	public static final String NOTE = "note";

	public VoaDiffcultyOp(Context context) {
		super(context);
	}

	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized VoaDiffculty findData(int id) {
		VoaDiffculty diffculty = null;
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"SELECT " + DESC_EN + ", " + DESC_CH + ", "
						+ NUMBER + ", " + NOTE
						+ " FROM " + TABLE_NAME
						+ " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			diffculty = new VoaDiffculty();
			diffculty.descEN = cursor.getString(0);
			diffculty.descCH = cursor.getString(1);
			diffculty.number = cursor.getInt(2);
			diffculty.note = cursor.getString(3);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return diffculty;
	}
}
