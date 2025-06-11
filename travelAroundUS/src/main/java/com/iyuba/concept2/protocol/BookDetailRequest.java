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
 * Created by ivotsm on 2017/2/22.
 */

public class BookDetailRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public BookDetailRequest(String bookId) {
        String sign = bookId + Constant.APPID + date;
        setAbsoluteURI("http://m."+Constant.IYBHttpHead()+"/mall/getBookInfo.jsp?appid=" +
                Constant.APPID +
                "&bookId=" +
                bookId +
                "&sign=" +
                MD5.getMD5ofStr(sign));
        Log.e("BookDetailRequest", "http://m."+Constant.IYBHttpHead()+"/mall/getBookInfo.jsp?appid=" +
                Constant.APPID +
                "&bookId=" +
                bookId +
                "&sign=" +
                MD5.getMD5ofStr(sign));
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new BookDetailResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
