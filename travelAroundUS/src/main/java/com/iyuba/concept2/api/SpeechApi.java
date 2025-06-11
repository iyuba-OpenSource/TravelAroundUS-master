package com.iyuba.concept2.api;

import com.iyuba.concept2.api.data.EvaluateRecordResponse;

import java.util.Map;


import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface SpeechApi {

    String BASE_URL = "http://voa.iyuba.cn/voa/";

    @GET("UnicomApi")
    Observable<EvaluateRecordResponse> listEvaluateRecord(@QueryMap Map<String,String> map);


}
