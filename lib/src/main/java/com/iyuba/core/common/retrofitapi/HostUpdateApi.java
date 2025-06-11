package com.iyuba.core.common.retrofitapi;

import com.iyuba.core.common.retrofitapi.result.HostUpdateResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface HostUpdateApi {

    String BASE_URL = "http://111.198.52.105:8085/api/";

    @GET("getDomain.jsp")
    Observable<HostUpdateResult> getHostUpdate(@Query("appId") String appId,
                                               @Query("short1") String short1,
                                               @Query("short2") String short2);
}
