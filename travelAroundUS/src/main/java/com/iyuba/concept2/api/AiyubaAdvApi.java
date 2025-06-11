package com.iyuba.concept2.api;


import com.iyuba.concept2.api.data.AiyubaAdvResult;
import com.iyuba.configation.Constant;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/9/2.
 */

public interface AiyubaAdvApi {

    // TODO: 2023/12/12 李涛在技术群里说：广告的接口，新版本发布的话，变更下
    // TODO: 2023/12/12 如果使用的是 http://app.iyuba.cn/dev/getAdxxxx的，修正为：http://dev.iyuba.cn/getAdxxxx 即可  变更了服务器了
//    String BASEURL = "http://app."+Constant.IYBHttpHead()+"/dev/";
    String BASEURL = "http://dev."+Constant.IYBHttpHead();
    
    String FLAG = "4";
    String KPFLAG = "1";
    @GET("getAdEntryAll.jsp")
    Call<List<AiyubaAdvResult>> getAdvByaiyuba(@Query("uid") String uid, @Query("appId") String appid,
                                               @Query("flag") String flag);

    @GET("getAdEntryAll.jsp")
    Observable<List<AiyubaAdvResult>> getAdvByaiyubaNew(@Query("uid") String uid,
                                                     @Query("appId") String appid,
                                                     @Query("flag") String flag);

}
