package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AgreeAgainstRequest extends BaseJSONRequest {


    public AgreeAgainstRequest(String protocol, String commnetId, int type) {
        super();
        if (type != 2) {
            setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/appApi//UnicomApi?"
                    + "protocol=" + protocol
                    + "&id=" + commnetId);
            Log.e("zanRequest", "http://daxue."+Constant.IYBHttpHead()+"/appApi//UnicomApi?"
                    + "protocol=" + protocol
                    + "&id=" + commnetId);
        } else {
            setAbsoluteURI("http://voa."+Constant.IYBHttpHead()+"/voa/UnicomApi?id="
                    + commnetId + "&protocol=" + protocol);
            Log.e("zanRequest", "http://voa."+Constant.IYBHttpHead()+"/voa/UnicomApi?id="
                    + commnetId + "&protocol=" + protocol);
        }
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AgreeAgainstResponse();
    }

}
