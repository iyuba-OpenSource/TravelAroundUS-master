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

/**
 * Created by ivotsm on 2017/3/2.
 */

public class OrderDetailRequest extends BaseJSONRequest {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    private String time = df.format(new Date());

    public OrderDetailRequest(String uid,String pageNumber,String cnt){
        setAbsoluteURI("http://vip."+Constant.IYBHttpHead()+"/getBuyList.jsp?uid=" +
                uid + "&format=json&sign=" + MD5.getMD5ofStr(uid+"iyuba"+time) +"&pagenumber="+pageNumber+"&cnt="+cnt);
        Log.e("OrderDetail","http://vip."+Constant.IYBHttpHead()+"/getBuyList.jsp?uid=" +
                uid + "&format=json&sign=" + MD5.getMD5ofStr(uid+"iyuba"+time) +"&pagenumber="+pageNumber+"&cnt="+cnt);
    }
    @Override
    public BaseHttpResponse createResponse() {
        return new OrderDetailResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
