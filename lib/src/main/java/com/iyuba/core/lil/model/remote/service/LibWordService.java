package com.iyuba.core.lil.model.remote.service;

import com.iyuba.core.lil.model.remote.bean.Word_collect;
import com.iyuba.core.lil.model.remote.bean.Word_search;
import com.iyuba.core.lil.remote.bean.Word_note;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title: 单词服务
 * @date: 2023/12/18 11:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LibWordService {

    //获取生词本数据(xml类型)
    //http://word.iyuba.cn/words/wordListService.jsp?u=12071118&pageNumber=1&pageCounts=30
    @GET()
    Observable<Word_note> getWordNoteData(@Url String url,
                                          @Query("u") int uid,
                                          @Query("pageNumber") int pageNum,
                                          @Query("pageCounts") int showCount);

    //收藏/取消收藏生词数据(xml类型)[插入-insert,删除-delete]
    //http://word.iyuba.cn/words/updateWord.jsp?userId=12071118&mod=delete&groupName=Iyuba&word=my
    @GET()
    Observable<Word_collect> collectWord(@Url String url,
                                         @Query("userId") int userId,
                                         @Query("mod") String mod,
                                         @Query("groupName") String groupName,
                                         @Query("word") String word);

    //查询单个单词相关内容
    //http://apps.iyuba.cn/iyuba/searchApiNew.jsp?format=json&key=test&pages=1&pageNum=3&parentID=0&type=voa&flg=0&userid=12071118
    @POST()
    Observable<Word_search> searchWord(@Url String url,
                                       @Query("format") String format,
                                       @Query("key") String word,
                                       @Query("pages") int pageIndex,
                                       @Query("pageNum") int showCount,
                                       @Query("parentID") int parentId,
                                       @Query("type") String type,
                                       @Query("flg") int flg,
                                       @Query("userid") int userId);
}
