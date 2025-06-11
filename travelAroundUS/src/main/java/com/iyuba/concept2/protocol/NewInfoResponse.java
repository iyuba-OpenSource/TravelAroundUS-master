package com.iyuba.concept2.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class NewInfoResponse extends BaseJSONResponse {
	public int system=0;
	public int letter=0;
	public int notice=0;
	public int follow=0;
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			system=jsonObjectRootRoot.getInt("system");
			letter=jsonObjectRootRoot.getInt("letter");
			notice=jsonObjectRootRoot.getInt("notice");
			follow=jsonObjectRootRoot.getInt("follow");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}


}
