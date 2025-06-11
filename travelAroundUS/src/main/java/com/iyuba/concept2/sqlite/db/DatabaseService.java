package com.iyuba.concept2.sqlite.db;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.ImportDatabase;

/**
 * 数据库服务
 * 
 * @author chentong
 * 
 */
public class DatabaseService {
	protected static ImportDatabase importDatabase;

	protected DatabaseService(Context context) {
		importDatabase = new ImportDatabase(context);
	}

	/**
	 * 删除表
	 * 
	 * @param tableName
	 */
	public void dropTable(String tableName) {
		//dbOpenHelper.getWritableDatabase().execSQL(
		//		"DROP TABLE IF EXISTS " + tableName);
		importDatabase.openDatabase().execSQL(
				"DROP TABLE IF EXISTS " + tableName);
	}

	/**
	 * 关闭数据库
	 * 
	 * @param DatabaseName
	 */
	public void closeDatabase(String DatabaseName) {
		//dbOpenHelper.getWritableDatabase().close();
		//importDatabase.closeDatabase();
	}

	/**
	 * 删除数据库表数据
	 * 
	 * @param tableName
	 * @param id
	 */
	public void deleteItemData(String tableName, Integer id) {
		//dbOpenHelper.getWritableDatabase().execSQL(
		//		"delete from " + tableName + " where _id=?",
		//		new Object[] { id });
		//closeDatabase(null);
		importDatabase.openDatabase().execSQL(
				"delete from " + tableName + " where _id=?",
				new Object[] { id });
		
		importDatabase.closeDatabase();
	}

	/**
	 * 删除数据库表数据
	 * 
	 * @param tableName
	 * @param ids
	 *            ids格式为"","","",""
	 */
	public void deleteItemsData(String tableName, String ids) {
		//dbOpenHelper.getWritableDatabase().execSQL(
		//		"delete from " + tableName + " where voaid in (" + ids + ")",
		//		new Object[] {});
		//closeDatabase(null);
		importDatabase.openDatabase().execSQL(
				"delete from " + tableName + " where voaid in (" + ids + ")",
				new Object[] {});
		
		importDatabase.closeDatabase();
	}

	/**
	 * 获取数据库表项数
	 * 
	 * @param tableName
	 * @return
	 */
	public long getDataCount(String tableName) {
		// cursor = dbOpenHelper.getReadableDatabase().rawQuery(
		///		"select count(*) from " + tableName, null);
		//cursor.moveToFirst();
		//closeDatabase(null);
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select count(*) from " + tableName, null);
		cursor.moveToFirst();
		
		importDatabase.closeDatabase();

		return cursor.getLong(0);
	}

	/**
	 * 关闭数据库服务
	 */
	public void close() {
		//dbOpenHelper.close();
		importDatabase.closeDatabase();
	}

}
