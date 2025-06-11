package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerFollowRequest extends BaseJSONRequest {
	private String format = "json";
	
	public  AnswerFollowRequest(String uid, String answerid,  String answer) {
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/zaskQuestion.jsp?"
				+ "format=" + format
				+ "&answerid=" + answerid
				+ "&fromid=" + uid
				+ "&zcontent=" +TextAttr.encode(TextAttr.encode(TextAttr.encode( answer)))
				);
		
		
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/zaskQuestion.jsp?"
				+ "format=" + format
				+ "answerid=" + answerid
				+ "&fromid=" + uid
				+ "&zcontent=" +TextAttr.encode(TextAttr.encode(TextAttr.encode( answer)))
				);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new AnswerFollowResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
