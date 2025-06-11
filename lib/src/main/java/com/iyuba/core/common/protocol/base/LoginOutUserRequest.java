package com.iyuba.core.common.protocol.base;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.module.toolbox.MD5;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登录
 *
 * @author chentong
 */
public class LoginOutUserRequest extends BaseJSONRequest {

    public LoginOutUserRequest(String userName, String password) {
        String protocol="11005";

        String passwordMD5 = MD5.getMD5ofStr(password);
        String sign = buildV2Sign(protocol, userName, passwordMD5, "iyubaV2");

        String requestUrl = "http://api.iyuba.com.cn/v2/api.iyuba?protocol="+protocol +
                "&username=" + userName +
                "&password=" + passwordMD5 +
                "&sign=" + sign +
                "&format=json";
        setAbsoluteURI(requestUrl);
        Log.e("loginoutUser", requestUrl);
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub
    }

    @Override
    public BaseHttpResponse createResponse() {
        // TODO Auto-generated method stub
        return new LoginOutUserResponse();
    }

    private String buildV2Sign(String... stuffs) {
        StringBuilder sb = new StringBuilder();
        for (String stuff : stuffs) {
            sb.append(stuff);
        }
        return MD5.getMD5ofStr(sb.toString());
    }
}
