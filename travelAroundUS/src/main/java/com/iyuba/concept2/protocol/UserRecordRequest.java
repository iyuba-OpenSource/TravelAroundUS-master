package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRecordRequest extends BaseJSONRequest {

	public UserRecordRequest(String uid) {
		// TODO Auto-generated constructor stub

		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/ecollege/getStudyRecord.jsp?uid="
				+ uid);

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new UserRecordResponse();
	}

}
