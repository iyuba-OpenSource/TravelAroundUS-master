package com.iyuba.core.lil.model.local.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;

/**
 * @title: 文章收藏模型
 * @date: 2023/12/20 10:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"voaId","userId"})
public class VoaCollectEntity implements Serializable {

    @NonNull
    public int voaId;
    @NonNull
    public int userId;

    public long createDate;//yyyy-MM-dd HH:mm:ss

    public VoaCollectEntity() {
    }

    @Ignore
    public VoaCollectEntity(int voaId, int userId, long createDate) {
        this.voaId = voaId;
        this.userId = userId;
        this.createDate = createDate;
    }
}
