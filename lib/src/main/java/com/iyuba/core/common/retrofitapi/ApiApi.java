package com.iyuba.core.common.retrofitapi;


import com.iyuba.core.common.retrofitapi.result.UserInfoReponse;
import com.iyuba.core.common.retrofitapi.result.VerifyLoginResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ApiApi {
    String BASE_URL = "http://api.iyuba.com.cn/";
    @POST("v2/api.iyuba?protocol=10010")
    Observable<VerifyLoginResponse> verifyLogin(@Body RequestBody body);

    @POST("/v2/api.iyuba")
    Observable<UserInfoReponse> getUserInfo(@QueryMap Map<String, String> map);
}
