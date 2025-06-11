package com.iyuba.concept2.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.Book;

import java.util.ArrayList;
import java.util.List;

public class BookOp extends DatabaseService {
	public static final String TABLE_NAME = "book";
	public static final String BOOK_ID = "book_id";
	public static final String BOOK_NAME = "book_name";
	public static final String TOTAL_NUM = "total_num";
	public static final String DOWNLOAD_NUM = "download_num";
	public static final String DOWNLOAD_STATE = "download_state";

	public BookOp(Context context) {
		super(context);
	}

	public synchronized void updateDownloadNum(int bookId) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + DOWNLOAD_NUM + "="
						+ DOWNLOAD_NUM + " + 1" + " where " + BOOK_ID + "="
						+ bookId);
		closeDatabase(null);
	}
	
	public synchronized void updateDownloadNum(Book book) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + DOWNLOAD_NUM + "="
						+ book.downloadNum + " where " + BOOK_ID + "="
						+ book.bookId);
		closeDatabase(null);
	}

	public synchronized int getDownloadNum(int bookId) {
		int downloadNum = 0;
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + DOWNLOAD_NUM + " from " + TABLE_NAME + " WHERE "
						+ BOOK_ID + "=" + bookId, new String[] {});
		cursor.moveToFirst();
		downloadNum = cursor.getInt(0);
		closeDatabase(null);
		
		return downloadNum;
	}

	public void updateDownloadState(int bookId, int downloadState) {

		importDatabase.openDatabase().execSQL(
				"update " + TABLE_NAME + " set " + DOWNLOAD_STATE + "="
						+ downloadState + " where " + BOOK_ID + "=" + bookId);
		closeDatabase(null);
	}

	public synchronized List<Book> findData() {

		List<Book> books = new ArrayList<Book>();

		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + BOOK_ID + ", " + BOOK_NAME + ", " + TOTAL_NUM
						+ ", " + DOWNLOAD_NUM + ", " + DOWNLOAD_STATE
						+ " from " + TABLE_NAME + " ORDER BY book_id", new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			books.add(fillIn(cursor));
		}

		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);

		return books;
	}

	private Book fillIn(Cursor cursor) {
		Book book = new Book();
		book.bookId = cursor.getInt(0);
		book.bookName = cursor.getString(1);
		book.totalNum = cursor.getInt(2);
		book.downloadNum = cursor.getInt(3);
		book.remainNum = book.totalNum - book.downloadNum;
		book.downloadState = cursor.getInt(4);
		
		if(book.downloadState == 1 || book.downloadState == -2) {
			book.downloadState = -1;
		}

		return book;
	}
}
