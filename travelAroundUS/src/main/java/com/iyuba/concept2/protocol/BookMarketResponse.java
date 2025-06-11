package com.iyuba.concept2.protocol;

import com.iyuba.concept2.sqlite.mode.MarketBook;
import com.iyuba.concept2.util.GsonUtils;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivotsm on 2017/2/20.
 */

public class BookMarketResponse extends BaseJSONResponse {
    public String result = "";
    public String size = "";
    public String message = "";
    public List<MarketBook> books;

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        books = new ArrayList<>();
        JSONObject jsonRoot = null;
        try {
            jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            size = jsonRoot.getString("size");
            message = jsonRoot.getString("message");
            books = GsonUtils.toObjectList(jsonRoot.getString("data"), MarketBook.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
