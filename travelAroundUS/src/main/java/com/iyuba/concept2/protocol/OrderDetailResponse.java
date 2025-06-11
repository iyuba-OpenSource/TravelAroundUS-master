package com.iyuba.concept2.protocol;


import com.iyuba.concept2.sqlite.mode.OrderResult;
import com.iyuba.concept2.util.GsonUtils;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivotsm on 2017/3/2.
 */

public class OrderDetailResponse extends BaseJSONResponse {
    public String result = "";
    public String total = "";
    public String cnt = "";
    public List<OrderResult> orderResults = new ArrayList<>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            total = jsonRoot.getString("total");
            cnt = jsonRoot.getString("cnt");
            if (result.equals("1")) {
                orderResults = GsonUtils.toObjectList(jsonRoot.getString("jsonArray"), OrderResult.class);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
