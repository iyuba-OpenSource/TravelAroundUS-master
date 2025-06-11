package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.VoaStructure;

public class VoaStructureOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_structure";
	public static final String ID = "id";
	public static final String DESC_EN = "desc_EN";
	public static final String DESC_CN = "desc_CH";
	public static final String NUMBER = "number";
	public static final String NOTE = "note";

	public VoaStructureOp(Context context) {
		super(context);
	}

	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized VoaStructure findData(int id) {
		VoaStructure structure = null;
		
		Cursor cursor = importDatabase.openDatabase()
				.rawQuery(
				"select " + DESC_EN + ", " + DESC_CN + ", "
						+ NUMBER + ", " + NOTE
						+ " from "+ TABLE_NAME 
						+ " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			structure = new VoaStructure();
			structure.descEN = cursor.getString(0);
			structure.descCH = cursor.getString(1);
			structure.number = cursor.getString(2);
			structure.note = cursor.getString(3);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return structure;
	}


}
