package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteAnswerQuesRequest extends BaseJSONRequest {
	private String format = "json";
	
	public DeleteAnswerQuesRequest(String flg, String id,String uid) {
		
		 
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/delQuestion.jsp?"
				+ "format=" + format
				+ "&flg=" +flg
				+ "&uid=" + uid
				+ "&delId=" + id);
		
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/delQuestion.jsp?"
				+ "format=" + format
				+ "flg=" +flg
				+ "&uid=" + uid
				+ "&delId=" + id);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new DeleteAnswerQuesResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
