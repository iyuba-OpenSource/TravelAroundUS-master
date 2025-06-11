package com.iyuba.concept2.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface FamilyUsaApi {
    String BASE_URL = "http://familyusa.iyuba.cn/";

    @GET("getPdf.jsp")
    Observable<ResponseBody> getPdf(@QueryMap Map<String,String> map);
}
