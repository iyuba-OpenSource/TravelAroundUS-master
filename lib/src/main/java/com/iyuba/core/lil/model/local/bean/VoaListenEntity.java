package com.iyuba.core.lil.model.local.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 课程试听表
 * @date: 2023/12/20 18:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"voaId","userId"})
public class VoaListenEntity {

    @NonNull
    public int voaId;
    @NonNull
    public int userId;

    public long createDate;//yyyy-MM-dd HH:mm:ss

    public VoaListenEntity() {
    }

    @Ignore
    public VoaListenEntity(int voaId, int userId, long createDate) {
        this.voaId = voaId;
        this.userId = userId;
        this.createDate = createDate;
    }
}
