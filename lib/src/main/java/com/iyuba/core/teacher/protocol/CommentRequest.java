package com.iyuba.core.teacher.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentRequest  extends BaseJSONRequest {
	private String format = "json";
	
	public CommentRequest(String uid, String username, int authorType, int qid, String answer) {
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
				+ "format=" + format
				+ "&authorid=" + uid
				+ "&username=" + username
				+ "&questionid=" + qid
				+ "&answer=" + answer
				+ "&authortype=" + authorType);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new CommentResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
