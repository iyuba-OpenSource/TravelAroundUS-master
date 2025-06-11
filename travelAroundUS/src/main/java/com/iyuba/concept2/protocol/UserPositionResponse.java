package com.iyuba.concept2.protocol;


import com.iyuba.concept2.sqlite.db.UserAddrInfo;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/28.
 */

public class UserPositionResponse extends BaseJSONResponse {
    public String result = "";
    public UserAddrInfo userInfo = new UserAddrInfo();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");

            if (result.equals("1")) {
                JSONObject data = jsonRoot.getJSONObject("data");
                userInfo.email = data.getString("email");
                userInfo.qq = data.getString("qq");
                userInfo.realName = data.getString("realname");
                userInfo.uname = data.getString("uname");
                userInfo.phone = data.getString("mobile");
                userInfo.address = data.getString("address");
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
