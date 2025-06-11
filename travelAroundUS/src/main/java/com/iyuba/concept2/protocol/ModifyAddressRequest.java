package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.concept2.sqlite.db.UserAddrInfo;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.me.pay.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivotsm on 2017/3/1.
 */

public class ModifyAddressRequest extends BaseJSONRequest {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    private String time = df.format(new Date());

    public ModifyAddressRequest(UserAddrInfo userAddrInfo, String uid) throws UnsupportedEncodingException {
        String sign = uid + time;
        setAbsoluteURI("http://m."+Constant.IYBHttpHead()+"/mall/updateAddressInfo.jsp?qq=" +
                userAddrInfo.qq +
                "&mobile=" +
                userAddrInfo.phone +
                "&address=" +
                URLEncoder.encode(userAddrInfo.address, "UTF-8") +
                "&uid=" +
                uid +
                "&email=" +
                userAddrInfo.email +
                "&realname=" +
                URLEncoder.encode(userAddrInfo.name, "UTF-8") +
                "&sign=" +
                MD5.getMD5ofStr(sign) +
                "&zipCode=" +
                "0");
        Log.e("ModifyAddr","http://m."+Constant.IYBHttpHead()+"/mall/updateAddressInfo.jsp?qq=" +
                userAddrInfo.qq +
                "&mobile=" +
                userAddrInfo.phone +
                "&address=" +
                URLEncoder.encode(userAddrInfo.address, "UTF-8") +
                "&uid=" +
                uid +
                "&email=" +
                userAddrInfo.email +
                "&realname=" +
                URLEncoder.encode(userAddrInfo.name, "UTF-8") +
                "&sign=" +
                MD5.getMD5ofStr(sign) +
                "&zipCode=" +
                "0");
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new ModifyAddressResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
