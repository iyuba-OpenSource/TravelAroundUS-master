package com.iyuba.concept2.protocol;

import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class AppUpdateResponse extends BaseJSONResponse {
	public String result = "";
	public String msg = "";
	public String data = "";

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		String[] body = bodyElement.split(",");
//		Log.e("body.length", body.length + "");
		if (body.length == 3) {
			result = body[0];
//			Log.e("result", result);
			msg = body[1];
			data = body[2];
		} else if (body.length == 2) {
			result = body[0];
			msg = body[1];
		}
		return true;
	}

}
