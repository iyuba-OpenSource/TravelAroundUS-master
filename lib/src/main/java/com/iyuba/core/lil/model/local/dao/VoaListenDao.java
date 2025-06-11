package com.iyuba.core.lil.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.core.lil.model.local.bean.VoaCollectEntity;
import com.iyuba.core.lil.model.local.bean.VoaListenEntity;

import java.util.List;

/**
 * @title: 课程试听操作(课程阅读历史)
 * @date: 2023/12/20 10:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 懒得弄，这里其实和收藏可以弄成一起的，懒了，自己弄一个吧
 */
@Dao
public interface VoaListenDao {

    //插入单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(VoaListenEntity listenEntity);

    //删除单个数据
    @Query("delete from VoaListenEntity where voaId=:voaId and userId=:userId")
    void deleteSingleData(int voaId,int userId);

    //获取单个数据
    @Query("select * from VoaListenEntity where voaId=:voaId and userId=:userId")
    VoaListenEntity getSingleData(int voaId, int userId);

    //获取多个数据
    @Query("select * from VoaListenEntity where userId=:userId order by createDate desc")
    List<VoaListenEntity> getMultiData(int userId);
}
