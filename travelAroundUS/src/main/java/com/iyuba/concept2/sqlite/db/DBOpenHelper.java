package com.iyuba.concept2.sqlite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库更新表
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {


	public static final String DB_NAME = "familyalbum.sqlite";//"concept_database.sqlite";// 数据库名称

	public static final int VERSION = 11;// 数据库版本，如果要更新数据库，在此版本号的基础上+1
	
	public DBOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("DBOpenHelper", "数据库已初始化...");
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("DBOpenHelper", "数据库已更新...");
	}
}
