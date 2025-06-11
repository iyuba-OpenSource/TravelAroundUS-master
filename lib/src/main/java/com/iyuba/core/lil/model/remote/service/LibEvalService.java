package com.iyuba.core.lil.model.remote.service;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.model.remote.bean.Mix_result;
import com.iyuba.core.lil.model.remote.bean.Publish_result;
import com.iyuba.core.lil.remote.bean.base.BaseBean_data;

/**
 * @title:  评测相关的接口
 * @date: 2023/12/21 14:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LibEvalService {

    //提交句子评测
    //http://iuserspeech.iyuba.cn:9001/test/ai/
    //sentence			The%20Blind%20Date%20%20ACT%20I%0D
    //flg			0
    //paraId			1
    //newsId			4
    //IdIndex			1
    //appId			230
    //wordId			0
    //type			familyalbum
    //userId			12071118
    //file	application/octet-stream	/storage/emulated/0/Android/data/com.iyuba.American/iyuba/travel/audio/sound40.mp3	38.54 KB (39,468 bytes)
    @POST()
    Observable<BaseBean_data<Eval_result>> submitEvalData(@Url String url,
                                                          @Body RequestBody body);

    //发布单句评测到排行榜
    //http://voa.iyuba.cn/voa/UnicomApi?topic=familyalbum&platform=android&format=json&protocol=60003&userid=12071118&username=aiyuba_lil&voaid=5&idIndex=2&score=60&shuoshuotype=2&content=wav8/202312/familyalbum/20231221/17031422138614604.mp3
    @POST()
    Observable<Publish_result> publishSentence(@Url String url,
                                               @Query("topic") String topic,
                                               @Query("platform") String platform,
                                               @Query("format") String format,
                                               @Query("protocol") int protocol,
                                               @Query("userid") int userId,
                                               @Query("username") String userName,
                                               @Query("voaid") int voaId,
                                               @Query("idIndex") String idIndex,
                                               @Query("score") String score,
                                               @Query("shuoshuotype") int shuoshuotype,
                                               @Query("content") String content);

    //合成音频
    //http://iuserspeech.iyuba.cn:9001/test/merge/
    //audios		wav8/202312/familyalbum/20231221/17031422008159676.mp3,wav8/202312/familyalbum/20231221/17031422138614604.mp3,wav8/202312/familyalbum/20231221/17031282356977396.mp3,wav8/202312/familyalbum/20231221/17031279874958188.mp3
    //type			familyalbum
    @POST()
    Observable<Mix_result> margeEvalSentence(@Url String url,
                                             @Body RequestBody body);

    //发布合成音频到排行榜
    //http://voa.iyuba.cn/voa/UnicomApi
    //topic	familyalbum
    //platform	android
    //format	json
    //protocol	60003
    //userid	12071118
    //username	aiyuba_lil
    //voaid	5
    //score	37
    //shuoshuotype	4
    //content	wav6/202312/familyalbum/20231221/17031428131210406.mp3
    //rewardVersion  1
    @FormUrlEncoded
    @POST()
    Observable<Publish_result> publishMixSentence(@Url String url,
                                                  @Field("topic") String topic,
                                                  @Field("platform") String platform,
                                                  @Field("format") String format,
                                                  @Field("protocol") int protocol,
                                                  @Field("userid") int userId,
                                                  @Field("username") String userName,
                                                  @Field("voaid") int voaId,
                                                  @Field("score") String score,
                                                  @Field("shuoshuotype") int shuoshuotype,
                                                  @Field("content") String content,
                                                  @Field("rewardVersion") int rewardVersion);
}
