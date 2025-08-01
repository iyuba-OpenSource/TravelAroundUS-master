
package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetQuesListRequest extends BaseJSONRequest {
    private String format = "json";

    public GetQuesListRequest(int pageNum) {

        int quesAppType = ConfigManager.Instance().loadInt("quesAppType");
        int quesAbilityType = ConfigManager.Instance().loadInt("quesAbilityType");

        setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/getQuestionList.jsp?format=json&type=3"
                + "&category1=" + quesAbilityType + "&category2=" + quesAppType + "&pageNum="
                + pageNum + "&isanswered=-1");

        Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/getQuestionList.jsp?format=json&type=3"
                + "&category1=" + quesAbilityType + "&category2=" + quesAppType + "&pageNum="
                + pageNum + "&isanswered=-1");
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetQuesListResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub

    }

}
