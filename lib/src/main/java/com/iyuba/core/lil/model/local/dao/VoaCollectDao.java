package com.iyuba.core.lil.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.core.lil.model.local.bean.VoaCollectEntity;

import java.util.List;

/**
 * @title:
 * @date: 2023/12/20 10:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface VoaCollectDao {

    //插入单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(VoaCollectEntity collectBean);

    //删除单个数据
    @Query("delete from VoaCollectEntity where voaId=:voaId and userId=:userId")
    void deleteSingleData(int voaId,int userId);

    //获取单个数据
    @Query("select * from VoaCollectEntity where voaId=:voaId and userId=:userId")
    VoaCollectEntity getSingleData(int voaId, int userId);

    //获取多个数据
    @Query("select * from VoaCollectEntity where userId=:userId  order by createDate desc")
    List<VoaCollectEntity> getMultiData(int userId);
}
