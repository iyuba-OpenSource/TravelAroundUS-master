package com.iyuba.core.common.protocol.base;

import com.iyuba.core.common.entity.ClearUserResponse;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginOutUserResponse extends BaseJSONResponse {
    public ClearUserResponse response;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        try {
            JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
            response=new ClearUserResponse();
            String result = jsonObjectRootRoot.getString("result");
            response.result = result;
        } catch (JSONException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return true;
    }
}
