package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetChatListRequest extends BaseJSONRequest {
	private String format = "json";
	
	public GetChatListRequest(int qid) {
		Log.e("GetChatListRequest", "http://www."+Constant.IYBHttpHead()+"/question/getQuestionDetail.jsp?"
				+ "format=" + format
				+ "&questionid=" + qid);
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/getQuestionDetail.jsp?"
				+ "format=" + format
				+ "&questionid=" + qid);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new GetChatListResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
