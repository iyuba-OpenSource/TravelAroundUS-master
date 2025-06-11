package com.iyuba.core.lil.model.remote.service;

import com.iyuba.core.lil.model.remote.bean.Chapter_collect;
import com.iyuba.core.lil.model.remote.bean.Chapter_collect_show;
import com.iyuba.core.lil.remote.bean.base.BaseBean_data;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title: 收藏接口
 * @date: 2023/12/20 11:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LibCollectService {

    //获取文章收藏的数据
    //http://daxue.iyuba.cn/appApi/getCollect.jsp?userId=12071118&groupName=Iyuba&type=voa&sentenceFlg=0&appName=familyalbum
    @GET()
    Observable<Chapter_collect_show> getChapterCollectData(@Url String url,
                                                           @Query("userId") int userId,
                                                           @Query("groupName") String groupName,
                                                           @Query("type") String type,
                                                           @Query("sentenceFlg") int flag,
                                                           @Query("appName") String appName);

    //收藏/取消收藏文章(删除-del、插入-insert)
    //http://daxue.iyuba.cn/appApi/updateCollect.jsp?userId=12071118&voaId=10&sentenceId=0&type=insert&groupName=Iyuba&sentenceFlg=0&appName=familyalbum
    @POST()
    Observable<Chapter_collect> collectVoa(@Url String url,
                                           @Query("userId") int userId,
                                           @Query("voaId") int voaId,
                                           @Query("sentenceId") int sentenceId,
                                           @Query("type") String type,
                                           @Query("groupName") String groupName,
                                           @Query("sentenceFlg") int flag,
                                           @Query("appName") String topic);
}
