package com.iyuba.core.teacher.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatRequest extends BaseJSONRequest {
	private String format = "json";
	
	public ChatRequest(String uid, int answerid, int fromid, String content) {
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/zaskQuestion.jsp?"
				+ "&format=" + format
				+ "&answerid=" + answerid
				+ "&fromid=" +fromid
				+ "&zcontent=" + content);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new ChatResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
