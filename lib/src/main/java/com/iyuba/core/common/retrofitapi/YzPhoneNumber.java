package com.iyuba.core.common.retrofitapi;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.retrofitapi.result.YzPhoneResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/11/4.
 */

public interface YzPhoneNumber {
    String YZNUMBER_URL = "http://api."+Constant.IYBHttpHead2()+"/";
    String FORMAT = "json";
    @GET("sendMessage3.jsp")
    Call<YzPhoneResult> getYzPhoneNumberState(@Query("format") String format, @Query("userphone") String userphone);
}
