package com.iyuba.concept2.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/3/1.
 */

public class ModifyAddressResponse extends BaseJSONResponse {
    public String result = "";

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
