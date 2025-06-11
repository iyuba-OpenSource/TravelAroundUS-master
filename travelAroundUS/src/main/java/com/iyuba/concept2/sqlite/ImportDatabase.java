package com.iyuba.concept2.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.db.DBOpenHelper;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 导入数据库
 *
 * @author chentong
 */
public class ImportDatabase {
    private final int BUFFER_SIZE = 400000;
    public static final String PACKAGE_NAME = "com.iyuba.American";//concept2  American AmericanQuick
    public static final String DB_NAME = "familyalbum.sqlite";// 数据库名称
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME + "/" + "databases"; // 在手机里存放数据库的位置
    public static final DBOpenHelper mdbhelper = new DBOpenHelper(RuntimeManager.getContext());
    private static SQLiteDatabase database = null;
    private static Context mContext;
    //更新记录：2022-6-30更新voa_sound数据库，增加words字段
    private int lastVersion = 13, currentVersion = 14; // 数据库版本，如果要更新数据库，在此版本号的基础上修改

    public ImportDatabase(Context context) {
        mContext = context;
    }

    public synchronized SQLiteDatabase openDatabase() {
        this.database = mdbhelper.getWritableDatabase();
        return this.database;
    }

    /**
     * 修改后，此函数作为第一次运行时的创建数据库函数
     *
     * @param dbfile
     */
    public synchronized void openDatabase(String dbfile) {

        try {
            lastVersion = ConfigManager.Instance().loadInt("database_version");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File database = new File(dbfile);

        LogUtils.e("数据库lastVersion", lastVersion + "");
        LogUtils.e("数据库currentVersion", currentVersion + "");

        if (currentVersion > lastVersion) {
            if (database.exists()) {
                database.delete();
                loadDataBase(dbfile);
                ConfigManager.Instance().putInt("database_version", currentVersion);
                LogUtils.e(" 数据库更新"+currentVersion+"旧版本"+lastVersion);
            } else if (!database.exists()) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                    ConfigManager.Instance().putInt("database_version",
                            currentVersion);
                    loadDataBase(dbfile);
                    LogUtils.e("数据库不更新");
            }
        }
    }

    public void closeDatabase() {

    }

    /**
     * 将数据库文件拷贝到需要的位置
     *
     * @param dbfile
     */
    private void loadDataBase(String dbfile) {
        try {
            InputStream is = mContext.getResources().openRawResource(
                    R.raw.familyalbum);    // 欲导入的数据库  concept_database
            BufferedInputStream bis = new BufferedInputStream(is);

            Log.e("load", "load");
            Log.e("datapath", DB_PATH);
            if (!(new File(DB_PATH).exists())) {
                new File(DB_PATH).mkdir();

                Log.e("file", new File(DB_PATH).exists() + "");
            }

            FileOutputStream fos = new FileOutputStream(dbfile);
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = bis.read(buffer)) > 0) {
                bfos.write(buffer, 0, count);
            }

            bis.close();
            is.close();
            bfos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
