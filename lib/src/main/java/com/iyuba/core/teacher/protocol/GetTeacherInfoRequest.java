package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetTeacherInfoRequest  extends BaseJSONRequest {
	private String format = "json";
	
	public GetTeacherInfoRequest(String uid) {
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/teacher/api/getTeacherInfo.jsp?format=json&uid="+uid);
		
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/teacher/api/getTeacherInfo.jsp?format=json&uid="+uid);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new GetTeacherInfoResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
