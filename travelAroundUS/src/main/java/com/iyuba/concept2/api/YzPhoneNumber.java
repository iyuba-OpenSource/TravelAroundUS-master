package com.iyuba.concept2.api;

import com.iyuba.concept2.api.data.YzPhoneResult;
import com.iyuba.configation.Constant;

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
    Call<YzPhoneResult> getYzPhoneNumberState(@Query("format")String format, @Query("userphone") String userphone);
}
