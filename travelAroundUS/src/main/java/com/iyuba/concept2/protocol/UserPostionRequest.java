package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.me.pay.MD5;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/28.
 */

public class UserPostionRequest extends BaseJSONRequest {

    public UserPostionRequest(String uid) {
        setAbsoluteURI("http://m."+Constant.IYBHttpHead()+"/mall/getAddressInfo.jsp?uid=" +
                uid +
                "&sign=" +
                MD5.getMD5ofStr("get" + uid + "addressInfo"));
        Log.e("UserPositon","http://m."+Constant.IYBHttpHead()+"/mall/getAddressInfo.jsp?uid=" +
                uid +
                "&sign=" +
                MD5.getMD5ofStr("get" + uid + "addressInfo"));
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new UserPositionResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
