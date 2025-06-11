package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.me.pay.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ivotsm on 2017/2/20.
 */

public class BookMarketRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public BookMarketRequest(String pageSize, String pageCount,String types) {
        String sign = Constant.APPID + types + date + pageSize + pageCount;
        setAbsoluteURI("http://m."+Constant.IYBHttpHead()+"/mall/getbooklist.jsp?sign=" +
                MD5.getMD5ofStr(sign) +
                "&appid=" +
                Constant.APPID +
                "&types=" +
                types +
                "&pageSize=" +
                pageSize +
                "&pageCount=" +
                pageCount);
        Log.e("BookMarket","http://m."+Constant.IYBHttpHead()+"/mall/getbooklist.jsp?sign=" +
                MD5.getMD5ofStr(sign) +
                "&appid=" +
                Constant.APPID +
                "&types=" +
                types +
                "&pageSize=" +
                pageSize +
                "&pageCount=" +
                pageCount);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new BookMarketResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
