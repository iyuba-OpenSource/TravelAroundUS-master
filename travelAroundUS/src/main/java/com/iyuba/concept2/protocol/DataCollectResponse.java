package com.iyuba.concept2.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class DataCollectResponse extends BaseJSONResponse{

	public String result;
	public String message;
	public String score;
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result= jsonObjectRoot.getString("result");
			message= jsonObjectRoot.getString("message");
			score = jsonObjectRoot.getString("jifen");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return true;
	}



}
