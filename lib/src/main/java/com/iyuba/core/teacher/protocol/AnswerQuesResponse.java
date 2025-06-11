package com.iyuba.core.teacher.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class AnswerQuesResponse extends BaseJSONResponse {

    public String result = "";
    public String message = "";
    public String jiFen = "0";

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(bodyElement);
            result = jsonBody.getString("result");
            message = jsonBody.getString("message");
            jiFen = jsonBody.getString("jiFen");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

}
