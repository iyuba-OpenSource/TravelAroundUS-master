package com.iyuba.core.lil.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;

import java.util.List;

/**
 * @title: 评测句子的操作
 * @date: 2023/12/21 14:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface EvalSentenceDao {

    //获取单个评测数据
    @Query("select * from EvalSentenceEntity where voaId=:voaId and userId=:userId and paraId=:paraId and lineN=:lineN")
    EvalSentenceEntity getSingleEvalData(int voaId,int userId,String paraId,String lineN);

    //获取当前账号下的评测数据
    @Query("select * from EvalSentenceEntity where voaId=:voaId and userId=:userId")
    List<EvalSentenceEntity> getMultiEvalData(int voaId,int userId);

    //保存单个评测数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(EvalSentenceEntity evalSentenceEntity);
}
