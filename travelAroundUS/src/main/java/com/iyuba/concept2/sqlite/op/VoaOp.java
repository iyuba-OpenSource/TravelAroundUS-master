package com.iyuba.concept2.sqlite.op;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.concept2.sqlite.compator.VoaCompator;
import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.Voa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取文章列表数据
 */
public class VoaOp extends DatabaseService {
	public static final String TABLE_NAME = "voa";
	public static final String VOA_ID = "VoaId";
	public static final String LESSON = "lesson";
	public static final String ART = "art";
	public static final String TITLE = "Title";
	public static final String TITLE_CN = "Title_cn";
	public static final String SOUND = "Sound";
	public static final String URL = "Url";
	public static final String READ_COUNT = "ReadCount";
	public static final String IS_COLLECT = "is_collect";
	public static final String IS_READ = "IsRead";
	public static final String IS_DOWNLOAD = "Downloading";//is_download
	public static final String IS_SYNCHRO = "is_synchro";
//	public static final String DOWNLOAD_TIME = "download";

	private Context mContext;

	public VoaOp(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<Voa> voas) {

		if (voas != null && voas.size() != 0) {
			for (int i = 0; i < voas.size(); i++) {
				Voa tempVoa = voas.get(i);

				Cursor cursor = importDatabase.openDatabase().rawQuery(
						"select * from " + TABLE_NAME + " where " + VOA_ID
								+ "=" + tempVoa.voaId, new String[] {});

				int databaseHasNum = cursor.getCount();
				closeDatabase(null);

				if (databaseHasNum == 0) {
					importDatabase.openDatabase().execSQL(
							"insert into " + TABLE_NAME + " (" + VOA_ID + ","
									+ TITLE + "," + TITLE_CN + "," + SOUND
									+ "," + URL + "," + READ_COUNT + ","
									+ IS_DOWNLOAD + "," + IS_READ
									+ ") values(?,?,?,?,?,?,?,?)",
							new Object[] { tempVoa.voaId, tempVoa.title,
									tempVoa.titleCn, tempVoa.sound,
									tempVoa.url, tempVoa.readCount,
									tempVoa.isDownload, "0" });

					closeDatabase(null);
				}

				cursor.close();
				cursor = null;
			}
		}
	}

	public synchronized int getReadVoaNum() {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select count(*) from " + TABLE_NAME + " where " + IS_READ
						+ " = 1", new String[] {});

		cursor.moveToFirst();
		int num = cursor.getInt(0);

		closeDatabase(null);

		if (cursor != null) {
			cursor.close();
		}

		return num;
	}

	public synchronized int getDownloadVoaNum() {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select count(*) from " + TABLE_NAME + " where " + IS_DOWNLOAD
						+ " = 1", new String[] {});

		cursor.moveToFirst();
		int num = cursor.getInt(0);

		closeDatabase(null);

		if (cursor != null) {
			cursor.close();
		}

		return num;
	}

	/**
	 * 批量修改
	 *
	 * @param voas
	 */
	public synchronized void updateData(List<Voa> voas) {
		if (voas != null && voas.size() != 0) {
			for (int i = 0; i < voas.size(); i++) {
				Voa tempVoa = voas.get(i);

				importDatabase.openDatabase().execSQL(
						"update " + TABLE_NAME + " set " + TITLE + "='"
								+ tempVoa.title + "', " + TITLE_CN + "='"
								+ tempVoa.titleCn + "', " + SOUND + "='"
								+ tempVoa.sound + "'," + URL + "='"
								+ tempVoa.url + "'," + "'," + IS_DOWNLOAD
								+ "='" + tempVoa.isDownload + "'," + READ_COUNT
								+ "='" + tempVoa.readCount + "' where "
								+ VOA_ID + "=" + tempVoa.voaId);

				closeDatabase(null);
			}
		}
	}

	/**
	 * 单一修改
	 *
	 * @param voaId
	 */
	public void updateIsRead(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_READ + "='1' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	public void updateReadCount(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + READ_COUNT + "="
						+ READ_COUNT + "+1 where " + VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 查询voa全部数据
	 *
	 * @return
	 */
	public synchronized List<Voa> findDataByBook(int bookIndex) {
		int from = bookIndex * 1000;
		int to = from + 1000;

		List<Voa> voas = new ArrayList<Voa>();

		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + ", "+ LESSON + ", "+ ART + ", " + TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + ", " + READ_COUNT + ", "
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME
//						+ " where " + VOA_ID + ">'" + from + "' and " + VOA_ID + "<'" + to + "'"
						+ " ORDER BY " + VOA_ID + " ASC", new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}

		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);

		return voas;
	}

	/**
	 * 查询数据分页
	 *
	 * @return
	 */
	public synchronized List<Voa> findDataByPage(int curBook, int count,
			int offset) {
		int from = curBook * 1000;
		int to = from + 1000;

		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + ","+ LESSON + ", "+ ART + ", " + TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME,
//						+ " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
//						+ "<'" + to + "' Limit " + count + " Offset " + offset,
				new String[] {});

		if (cursor.getCount() == 0) {
			if (cursor != null) {
				cursor.close();
			}

			closeDatabase(null);
			return voas;
		} else {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				voas.add(fillIn(cursor));
			}

			if (cursor != null) {
				cursor.close();
			}

			closeDatabase(null);
			return voas;
		}
	}

	/**
	 * 根据void主键查询
	 */
	public synchronized Voa findDataById(int voaId) {

		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + LESSON + ", "+ ART + ", "+ TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME +" where " + VOA_ID + " ="+voaId, null);//占位符，可以为空
//						+ " where " + VOA_ID + " =?",
//				new String[] { String.valueOf(voaId) });
		if (cursor.moveToNext()) {
			Voa voa = fillIn(cursor);
			if (cursor != null) {
				cursor.close();
			}
			closeDatabase(null);
			return voa;
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);
		return null;
	}

	/*********************************收藏操作**************************/
	/**
	 * 查询收藏
	 */
	public synchronized List<Voa> findDataFromCollection(int curBook) {
		int from = 0;
		int to = 0;

		if (curBook != 0) {
			from = curBook * 1000;
			to = from + 1000;
		} else {
			from = 0;
			to = 5000;
		}

		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + ","+ LESSON + ", "+ ART + ", " + TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME+ " where " + IS_COLLECT + " ='1'",
//						+ " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
//						+ " <'" + to + "' and " + IS_COLLECT + " ='1'",
				new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);
		if (voas.size() != 0) {
			return voas;
		}
		return null;
	}

	/**
	 * 查询收藏
	 */
	public synchronized List<Voa> findDataFromCollection() {
		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + ","+ LESSON + ", "+ ART + ", " + TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME
						+ " where " + IS_COLLECT + " ='1'", new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);

		return voas;
	}

	/**
	 * 添加收藏
	 */
	public synchronized void insertDataToCollection(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_COLLECT + "='1' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 删除收藏
	 */
	public synchronized void deleteDataInCollection(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_COLLECT + "='0' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 删除收藏
	 */
	public synchronized void deleteAllInCollection() {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_COLLECT + "='0'  ");
		closeDatabase(null);
	}

	/************************************试听操作**************************/
	/**
	 * 查询试听
	 */
	public synchronized List<Voa> findDataFromRead(int curBook) {
		int from = 0;
		int to = 0;

		if (curBook != 0) {
			from = curBook * 1000;
			to = from + 1000;
		} else {
			from = 0;
			to = 5000;
		}

		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + ","+ LESSON + ", "+ ART + ", " + TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME
						+ " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
						+ " <'" + to + "' and " + IS_READ + " ='1'",
				new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);
		if (voas.size() != 0) {
			return voas;
		}
		return null;
	}

	/**
	 * 查询试听
	 */
	public synchronized List<Voa> findDataFromRead() {
		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + LESSON + ", "+ ART + ", "+ TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME
						+ " where " + IS_READ + " ='1'", new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);

		return voas;
	}

	/**
	 * 添加试听
	 */
	public synchronized void insertDataToRead(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_READ + "='1' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 删除试听
	 */
	public synchronized void deleteDataInRead(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_READ + "='0' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 删除试听
	 */
	public synchronized void deleteAllInRead() {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_READ + "='0'  ");
		closeDatabase(null);
	}

	/*************************************下载操作*****************************/
	/**
	 * 查询下载
	 */
	public synchronized List<Voa> findDataFromDownload(int curBook) {
		int from = 0;
		int to = 0;

		if (curBook != 0) {
			from = curBook * 1000;
			to = from + 1000;
		} else {
			from = 0;
			to = 5000;
		}

		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + LESSON + ", "+ ART + ", "+ TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME
						+ " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
						+ " <'" + to + "' and " + IS_DOWNLOAD + " ='1'",
				new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);
		if (voas.size() != 0) {
			return voas;
		}
		return null;
	}

	/**
	 * 查询下载
	 */
	public synchronized List<Voa> findDataFromDownload() {
		List<Voa> voas = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + LESSON + ", "+ ART + ", "+ TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME
						+ " where " + IS_DOWNLOAD + " ='1'", new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voas.add(fillIn(cursor));
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);

		return voas;
	}

	/**
	 * 添加下载
	 */
	public synchronized void insertDataToDownload(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_DOWNLOAD + "='1' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 删除下载
	 */
	public synchronized void deleteDataInDownload(int voaId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_DOWNLOAD + "='0' where "
						+ VOA_ID + "=" + voaId);
		closeDatabase(null);
	}

	/**
	 * 删除下载
	 */
	public synchronized void deleteAllInDownload() {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_DOWNLOAD + "='0'  ");
		closeDatabase(null);
	}

	/**
	 * 更新synchro的状态
	 */
	public synchronized void updateSynchro(int voaid, int state) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + IS_SYNCHRO + "=" + state
					+ " where " + VOA_ID + "=" + voaid);
		closeDatabase(null);
	}

	/**
	 * 查询未更新到服务器
	 * @return
	 */
	public synchronized List<Voa> findUnSynchroData() {
		List<Voa> voaList = new ArrayList<Voa>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + LESSON + ", "+ ART + ", "+ TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_SYNCHRO
						+ " from " + TABLE_NAME
						+ " where " + IS_SYNCHRO + " =0"
						, new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voaList.add(fillIn(cursor));
		}
		if (cursor!=null) {
			cursor.close();
		}
		closeDatabase(null);

		return voaList;
	}

	public synchronized Map<Integer, Voa> findAllData() {

		Map<Integer, Voa> voas = new HashMap<Integer, Voa>();
		Voa tempVoa;
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + "," + LESSON + ", "+ ART + ", "+ TITLE + ", " + TITLE_CN + ", "
						+ SOUND + ", " + URL + "," + READ_COUNT + ","
						+ IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD
						+ " from " + TABLE_NAME,
				new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			tempVoa = fillIn(cursor);

			voas.put(tempVoa.voaId, tempVoa);
		}

		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);
		if (voas.size() != 0) {
			return voas;
		}

		return null;
	}

	/**
	 * 查询包含str的课程
	 */
	public synchronized Map<Integer, Voa> findData(Map<Integer, Voa> voaMap,
			String str) {
		Voa tempVoa;
		int voaId;

		str = str.toLowerCase();

		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + VOA_ID + " from " + TABLE_NAME + " where lower("
						+ TITLE + ") like '%" + str + "%'" + " OR " + TITLE_CN
						+ " like '%" + str + "%'", new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			voaId = cursor.getInt(0);
			tempVoa = voaMap.get(voaId);
			tempVoa.titleFind = 1;

			voaMap.put(voaId, tempVoa);
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);

		return voaMap;
	}

	public List<Voa> getSearchResult(String str) {
		Map<Integer, Voa> voaMap = findAllData();
		voaMap = findData(voaMap, str);
		VoaDetailOp textDetailOp = new VoaDetailOp(mContext);
		voaMap = textDetailOp.findData(voaMap, str);

		Collection<Voa> voaCollecton = voaMap.values();
		List<Voa> voaList = new ArrayList<Voa>();
		for (Voa voa : voaCollecton) {
			if (voa.titleFind != 0 || voa.textFind != 0) {
				voaList.add(voa);
			}
		}

		VoaCompator voaCompator = new VoaCompator();
		Collections.sort(voaList, voaCompator);

		return voaList;

	}

	public List<Integer> getSearchResultLessonId(String str) {
		Map<Integer, Voa> voaMap = findAllData();
		voaMap = findData(voaMap, str);
		VoaDetailOp textDetailOp = new VoaDetailOp(mContext);
		voaMap = textDetailOp.findData(voaMap, str);

		Collection<Voa> voaCollecton = voaMap.values();
		List<Integer> voaList = new ArrayList<Integer>();
		for (Voa voa : voaCollecton) {
			if (voa.titleFind != 0 || voa.textFind != 0) {
				voaList.add(voa.lesson);
			}
		}
// 排序
//		VoaCompator voaCompator = new VoaCompator();
//		Collections.sort(voaList, voaCompator);

		return voaList;

	}

	@SuppressLint("Range")
	private Voa fillIn(Cursor cursor) {
		Voa voa = new Voa();
		voa.voaId = cursor.getInt(0);
		voa.lesson = cursor.getInt(1);
		voa.art = cursor.getInt(2);

		voa.title = cursor.getString(3);
		voa.titleCn = cursor.getString(4);

		int a = cursor.getColumnIndex("is_collect");
		voa.isCollect = cursor.getString(cursor.getColumnIndex("is_collect"));

		voa.sound = cursor.getString(6);
		voa.url = cursor.getString(7);
		voa.readCount = cursor.getString(8);
		//voa.isCollect = cursor.getString(6);
		voa.isRead = cursor.getString(9);
		voa.isDownload = cursor.getString(10);
		return voa;
	}
}
