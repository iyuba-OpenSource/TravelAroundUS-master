package com.iyuba.concept2.sqlite.op;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iyuba.concept2.sqlite.db.DatabaseService;
import com.iyuba.concept2.sqlite.mode.BookDetail;
import com.iyuba.concept2.sqlite.mode.MarketBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ivotsm on 2017/4/7.
 */

public class ShopCartOp extends DatabaseService {
    private Context context;

    public static final String TABLE_NAME = "shop_cart";
    public static final String APPIMG = "appImg";
    public static final String APPINFO = "appInfo";
    public static final String APPNAME = "appName";
    public static final String APPPRICE = "appPrice";
    public static final String AUTHORIMG = "authorImg";
    public static final String AUTHORINFO = "authorInfo";
    public static final String BOOKAUTHOR = "bookAuthor";
    public static final String BOOKPRICE = "bookPrice";
    public static final String CLASSIMG = "classImg";
    public static final String CLASSINFO = "classInfo";
    public static final String CLASSPRICE = "classPrice";
    public static final String CONTENTIMG = "contentImg";
    public static final String CONTENTINFO = "contentInfo";
    public static final String CREATETIME = "createTime";
    public static final String DESC = "desc";
    public static final String EDITIMG = "editImg";
    public static final String EDITINFO = "editInfo";
    public static final String FLG = "flg";
    public static final String GROUPS = "groups";
    public static final String ID = "id";
    public static final String IMGSRC = "imgSrc";
    public static final String NAME = "name";
    public static final String PKG = "pkg";
    public static final String PUBLISHHOUSE = "publishHouse";
    public static final String TOTALPRICE = "totalPrice";
    public static final String TYPES = "types";
    public static final String UPDATETIME = "updateTime";
    public static final String BOOKID = "bookId";
    public static final String UID = "uid";
    public static final String NUMBER = "number";

    public ShopCartOp(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 批量插入数据
     */
    public synchronized void saveDatas(List<MarketBook> books) {
        int size = books.size();
        if (books == null || size == 0)
            return;
        try {
            for (int i = 0; i < size; i++) {
                ContentValues contentValues = new ContentValues();
                MarketBook marketBook = null;
                marketBook = books.get(i);
                if (marketBook != null) {
                    contentValues.put("id", marketBook.getBookId());
                    contentValues.put("totalPrice", marketBook.getTotalPrice());
                    contentValues.put("editInfo", marketBook.getEditInfo());
                    contentValues.put("editImg", marketBook.getEditImg());
                }
                contentValues.put("uid", "0");
                importDatabase.openDatabase().insert("shop_cart", null, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized int dataNum(String uid, String id) {
        Cursor cursor = importDatabase.openDatabase().query("shop_cart", new String[]{"id"}, "uid=? and id=?", new String[]{uid, id}, null, null, null);
        cursor.moveToFirst();

        return cursor.getCount();
    }

    public synchronized long saveData(BookDetail bookDetail, String uid) {
        ContentValues cv = new ContentValues();
        cv.put("appImg", bookDetail.appImg);
        cv.put("appInfo", bookDetail.appInfo);
        cv.put("appName", bookDetail.appName);
        cv.put("appPrice", bookDetail.appPrice);
        cv.put("authorImg", bookDetail.authorImg);
        cv.put("authorInfo", bookDetail.authorInfo);
        cv.put("bookAuthor", bookDetail.bookAuthor);
        cv.put("bookPrice", bookDetail.bookPrice);
        cv.put("classImg", bookDetail.classImg);
        cv.put("classInfo", bookDetail.classInfo);
        cv.put("classPrice", bookDetail.classPrice);
        cv.put("contentImg", bookDetail.contentImg);
        cv.put("contentInfo", bookDetail.contentInfo);
        cv.put("createTime", bookDetail.createTime);
        cv.put("desc", bookDetail.desc);
        cv.put("editImg", bookDetail.editImg);
        cv.put("editInfo", bookDetail.editInfo);
        cv.put("flg", bookDetail.flg);
        cv.put("groups", bookDetail.groups);
        cv.put("id", bookDetail.id);
        cv.put("imgSrc", bookDetail.imgSrc);
        cv.put("name", bookDetail.name);
        cv.put("pkg", bookDetail.pkg);
        cv.put("publishHouse", bookDetail.publishHouse);
        cv.put("totalPrice", bookDetail.totalPrice);
        cv.put("types", bookDetail.types);
        cv.put("updateTime", bookDetail.updateTime);
        cv.put("bookId", bookDetail.bookId);
        cv.put("uid", uid);
        cv.put("number", 1);

        long result = importDatabase.openDatabase().insert("shop_cart", null, cv);
        return result;
    }

    public synchronized void updateBookNum(BookDetail bookDetail, String uid) {
        importDatabase.openDatabase().execSQL("update shop_cart set number = number + 1 where id = " + bookDetail.id + " and uid =" + uid);
    }

    public synchronized int getUserCartNum(String uid) {
        Cursor cursor = importDatabase.openDatabase().query("shop_cart", new String[]{"editImg", "editInfo", "totalPrice", "number"}, "uid=?", new String[]{uid}, null, null, null);
        cursor.moveToFirst();
        return cursor.getCount();
    }

    public synchronized List<BookDetail> getBookDetails(String uid) {
        List<BookDetail> bookDetails = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().query("shop_cart", new String[]{"editImg", "editInfo", "totalPrice", "number", "id"}, "uid=?", new String[]{uid}, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            BookDetail bookDetail = new BookDetail();
            bookDetail.editImg = cursor.getString(0);
            bookDetail.editInfo = cursor.getString(1);
            bookDetail.totalPrice = cursor.getString(2);
            bookDetail.num = cursor.getInt(3);
            bookDetail.id = cursor.getString(4);
            bookDetails.add(bookDetail);
        }
        return bookDetails;
    }

    public synchronized void deleteBookDetail(String id, String uid) {
        importDatabase.openDatabase().delete("shop_cart", "id=? and uid=?", new String[]{id, uid});
    }

    public synchronized HashMap<String, BookDetail> getBookOrders() {
        HashMap<String, BookDetail> bookDetailHashMap = new HashMap<>();
        Cursor cursor = importDatabase.openDatabase().query("shop_cart", new String[]{"id", "totalPrice", "editInfo", "editImg"}, "uid=?", new String[]{"0"}, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            BookDetail bookDetail = new BookDetail();
            bookDetail.id = cursor.getString(0);
            bookDetail.totalPrice = cursor.getString(1);
            bookDetail.editInfo = cursor.getString(2);
            bookDetail.editImg = cursor.getString(3);
            bookDetailHashMap.put(bookDetail.id, bookDetail);
        }
        return bookDetailHashMap;
    }

}
