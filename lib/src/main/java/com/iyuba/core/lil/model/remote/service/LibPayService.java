package com.iyuba.core.lil.remote.service;

import com.iyuba.core.lil.remote.bean.Alipay_info;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title: 支付接口
 * @date: 2023/12/16 11:01
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LibPayService {

    //支付宝支付接口
    //http://vip.iyuba.cn/alipay.jsp?&amount=1&code=148760c9e66c944056505f4d3b05a9a4&product_id=10&WIDbody=%25E8%258A%25B1%25E8%25B4%25B930.0%25E5%2585%2583%25E8%25B4%25AD%25E4%25B9%25B0%25E6%259C%25AC%25E5%25BA%2594%25E7%2594%25A8vip1%25E4%25B8%25AA%25E6%259C%2588&WIDsubject=%25E6%259C%25AC%25E5%25BA%2594%25E7%2594%25A8vip&app_id=260&userId=15105425&WIDtotal_fee=30.0
    @POST()
    Observable<Alipay_info> getAlipay(@Url String url,
                                      @Query("amount") String amount,
                                      @Query("code") String code,
                                      @Query("product_id") String productId,
                                      @Query("WIDbody") String WIDbody,
                                      @Query("WIDsubject") String WIDsubject,
                                      @Query("app_id") String appId,
                                      @Query("userId") int userId,
                                      @Query("WIDtotal_fee") String WIDtotal_fee);
}
