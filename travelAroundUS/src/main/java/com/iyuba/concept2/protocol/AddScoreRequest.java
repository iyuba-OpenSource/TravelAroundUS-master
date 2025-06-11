package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打卡积分或红包
 */

public class AddScoreRequest extends BaseJSONRequest {

    public AddScoreRequest(String userID, String appid, String time) {


        setAbsoluteURI("http://api."+Constant.IYBHttpHead()+"/credits/updateScore.jsp?srid=81&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time);

        Log.e("BookDetailRequest", "http://api."+Constant.IYBHttpHead()+"/credits/updateScore.jsp?srid=81&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AddScoreResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
