package com.iyuba.core.common.retrofitapi;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.result.QQGroupSupportResult;
import com.iyuba.core.common.retrofitapi.result.QQSupportResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface QQSupportApi {

    public static final String BASE_URL = "http://ai." + Constant.IYBHttpHead() + "/japanapi/";

    public static final String BASE_QQ_SUPPORT_URL = "http://ai." + Constant.IYBHttpHead() + "/japanapi/getJpQQ.jsp";
    public static final String BASE_QQ_GROUP_URL = "http://m."+ Constant.IYBHttpHead() +"/m_login/getQQGroup.jsp";

    //获取qq客服信息
    @GET
    Observable<QQSupportResult> getQQSupport(@Url String url,
                                             @Query("appid") String appid);

    //获取qq群信息
    @GET
    Observable<QQGroupSupportResult> getQQGroup(@Url String url,
                                                @Query("type") String type,
                                                @Query("userId") String userId,
                                                @Query("appId") String appId);
}
