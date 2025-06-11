package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;

public class CommentAgreeOp extends DatabaseService {

	public CommentAgreeOp(Context context) {
		super(context);
	}

	public static final String TABLE_NAME = "comment_on_comment";
	public static final String UID = "uid";
	public static final String COMMENTID = "comment_id";
	public static final String AGREE = "agree";

	public synchronized int findDataByAll(String commentid, String uid) {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + COMMENTID + " , " + UID + "," + AGREE + " from "
						+ TABLE_NAME + " where " + COMMENTID + " = ? AND "
						+ UID + " = ?", new String[] { commentid, uid });
		if (cursor != null && cursor.getCount() == 0) {
			cursor.close();
			return 0;
		} else {
			int temp = 0;
			cursor.moveToFirst();
			if (cursor.getString(2).equals("against")) {
				temp = 2;
			} else {
				temp = 1;
			}
			cursor.close();
			return temp;
		}
	}

	public synchronized void saveData(String commentid, String uid, String agree) {
		if (commentid != null && uid != null) {
			importDatabase.openDatabase().execSQL(
					"insert or replace into " + TABLE_NAME + " (" + COMMENTID
							+ "," + UID + "," + AGREE + ") values(?,?,?)",
					new Object[] { commentid, uid, agree });
			closeDatabase(null);
		}
	}

}
