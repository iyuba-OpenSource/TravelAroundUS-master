package com.iyuba.core.common.protocol.message;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseSendMsg extends BaseJSONResponse {
	// {"system":0,"letter":2,"notice":1,"follow":0}
	public boolean isSuccess;
	public String message;
	public int ResultCode;
	public int AddScore;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {

		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			ResultCode = jsonObjectRootRoot.getInt("ResultCode");
			AddScore = jsonObjectRootRoot.getInt("AddScore");
			message = jsonObjectRootRoot.getString("Message");
			if (ResultCode==501){
				isSuccess=true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

}
