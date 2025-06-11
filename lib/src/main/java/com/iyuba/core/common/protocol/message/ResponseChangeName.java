package com.iyuba.core.common.protocol.message;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseChangeName extends BaseJSONResponse {
	// {"system":0,"letter":2,"notice":1,"follow":0}
	public boolean isSuccess;
	public String message;
	public int result;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {

		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getInt("result");
			message = jsonObjectRootRoot.getString("message");
			if (result==121){
				isSuccess=true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

}
