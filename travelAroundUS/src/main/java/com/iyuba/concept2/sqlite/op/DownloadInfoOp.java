package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

public class DownloadInfoOp  extends DatabaseService {
	private String TAG = "download_info";

	public DownloadInfoOp(Context context) {
		super(context);
	}
	
	public synchronized void insert(List<DownloadInfo> infoList) {
		Log.d(TAG, "insert");
		try {
			for(DownloadInfo info : infoList) {
				Cursor cursor = importDatabase.openDatabase().rawQuery(
						"select * from download_info where voa_id=" + info.voaId, new String[] {});
				
				int databaseHasNum = cursor.getCount();
				closeDatabase(null);
				
				if(databaseHasNum == 0) {
					importDatabase.openDatabase().execSQL(
							"INSERT INTO download_info(voa_id, url, downloaded_bytes, download_state, "
									+ "total_bytes, download_per, save_path) VALUES(?, ?, ?, ?, ?, ?, ?)",
							new Object[] { info.voaId, info.url, info.downloadedBytes,
									info.downloadedState, info.totalBytes,
									info.downloadPer, info.savePath });
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null);
		}
	}
    //插入下载信息？
	public synchronized void insert(DownloadInfo info) {
		Log.d(TAG, "insert");
		try {
			importDatabase.openDatabase().execSQL(
					"INSERT INTO download_info(voa_id, url, downloaded_bytes, download_state, "
							+ "total_bytes, download_per, save_path) VALUES(?, ?, ?, ?, ?, ?, ?)",
					new Object[] { info.voaId, info.url, info.downloadedBytes,
							info.downloadedState, info.totalBytes,
							info.downloadPer, info.savePath });
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null);
		}
	}

	public void delete(int voaId) {
		Log.d(TAG, "delete");
		try {
			importDatabase.openDatabase().execSQL("DELETE FROM download_info WHERE voa_id=?",
					new Object[] { voaId });
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null);
		}
	}

	public synchronized void update(DownloadInfo info) {
		Log.d(TAG, "update");
		try {
			importDatabase.openDatabase().execSQL("UPDATE download_info SET url=?, downloaded_bytes=?, download_state=?,"
							+ " total_bytes=?, download_per=?, save_path=?"
							+ " WHERE voa_id=?",
					new Object[] { info.url, info.downloadedBytes, info.downloadedState,
									info.totalBytes, info.downloadPer, info.savePath, info.voaId });
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null);
		}
	}

	public synchronized DownloadInfo query(int voaId) {
		Log.d(TAG, "query");
		Cursor c = null;
		DownloadInfo info = null;
		try {
			c = importDatabase.openDatabase().rawQuery(
					"SELECT voa_id, url, downloaded_bytes, download_state,"
							+ " total_bytes, download_per, save_path"
							+ " FROM download_info WHERE voa_id=?",
					new String[] { String.valueOf(voaId) });
			info = null;
			if (c.moveToNext())
				info = new DownloadInfo();
			 	info.voaId = c.getInt(0);
			 	info.url = c.getString(1);
			 	info.downloadedBytes = c.getLong(2);
			 	info.downloadedState = c.getInt(3);
			 	info.totalBytes = c.getLong(4);
			 	info.downloadPer = c.getInt(5);
			 	info.savePath = c.getString(6);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			closeDatabase(null);
		}
		return info;
	}
	//获取数据库中下载表的全部数据，表是空的
	public synchronized List<DownloadInfo> query() {
		Log.d(TAG, "query");
		Cursor cursor = null;
		List<DownloadInfo> infoList = new ArrayList<DownloadInfo>();
		DownloadInfo info = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"SELECT voa_id, url, downloaded_bytes, download_state,"
							+ " total_bytes, download_per, save_path"
							+ " FROM download_info",
					new String[] { });
			info = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				info = new DownloadInfo();
			 	info.voaId = cursor.getInt(0);
			 	info.url = cursor.getString(1);
			 	info.downloadedBytes = cursor.getLong(2);
			 	info.downloadedState = cursor.getInt(3);
			 	if(info.downloadedState == 1 || info.downloadedState == -2) {
			 		info.downloadedState = -1;
			 	}
			 	info.totalBytes = cursor.getLong(4);
			 	info.downloadPer = cursor.getInt(5);
			 	info.savePath = cursor.getString(6);
			 	infoList.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			closeDatabase(null);
		}
		return infoList;
	}
}
