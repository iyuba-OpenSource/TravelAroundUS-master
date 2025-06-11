package com.iyuba.concept2.protocol;

import com.iyuba.concept2.sqlite.mode.BookDetail;
import com.iyuba.concept2.util.GsonUtils;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class BookDetailResponse extends BaseJSONResponse {
    public String result = "";
    public BookDetail bookDetail;
    public String message = "";
    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            message = jsonRoot.getString("message");
            bookDetail = GsonUtils.toObject(jsonRoot.getString("data"), BookDetail.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
