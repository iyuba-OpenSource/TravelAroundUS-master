package com.iyuba.concept2.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class SignResponse extends BaseJSONResponse {
    public JSONObject jsonObjectRoot;


    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        // TODO Auto-generated method stub

        try {
            jsonObjectRoot = new JSONObject(bodyElement);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
