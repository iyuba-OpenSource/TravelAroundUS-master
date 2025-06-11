package com.iyuba.core.lil.model.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.bean.VoaCollectEntity;
import com.iyuba.core.lil.model.local.bean.VoaListenEntity;
import com.iyuba.core.lil.model.local.dao.EvalSentenceDao;
import com.iyuba.core.lil.model.local.dao.VoaCollectDao;
import com.iyuba.core.lil.model.local.dao.VoaListenDao;
import com.iyuba.core.lil.util.LibResUtil;

/**
 * @title: 本地辅助数据库
 * @date: 2023/12/20 10:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Database(entities = {VoaCollectEntity.class, VoaListenEntity.class, EvalSentenceEntity.class},exportSchema = false,version = 1)
public abstract class LibRoomDB extends RoomDatabase {
    private static LibRoomDB instance;

    public static LibRoomDB getInstance(){
        if (instance==null){
            synchronized (LibRoomDB.class){
                if (instance==null){
                    instance = Room.databaseBuilder(LibResUtil.getInstance().getContext(), LibRoomDB.class,LibRoomDB.getDBName())
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }


    //数据库名称
    private static String getDBName(){
        //这里设置为包名最后一个+db
        String packageName = LibResUtil.getInstance().getApplication().getPackageName();
        int index = packageName.lastIndexOf(".");
        String dbName = packageName.substring(index+1);
        return dbName+".db";
    }

    /*******************************dao操作类*****************************/
    public abstract VoaCollectDao getVoaCollectDao();//课程收藏
    public abstract VoaListenDao getVoaListenDao();//课程收藏

    public abstract EvalSentenceDao getEvalSentenceDao();//句子评测
}
