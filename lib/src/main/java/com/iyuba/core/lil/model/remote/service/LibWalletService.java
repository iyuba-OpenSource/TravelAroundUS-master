package com.iyuba.core.lil.remote.service;

import com.iyuba.core.lil.remote.bean.Reward_history;
import com.iyuba.core.lil.remote.bean.base.BaseBean_data;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title: 钱包信息
 * @date: 2023/12/12 17:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LibWalletService {

    //钱包历史记录
    //http://api.iyuba.cn/credits/getuseractionrecord.jsp?uid=6307010&pages=1&pageCount=20&sign=0fd32b5d167482f0cc3561b2abc70738
    @GET()
    Observable<BaseBean_data<List<Reward_history>>> getWalletHistory(@Url String url,
                                                                       @Query("uid") int uid,
                                                                       @Query("pages") int pages,
                                                                       @Query("pageCount") int pageCount,
                                                                       @Query("sign") String sign);
}
