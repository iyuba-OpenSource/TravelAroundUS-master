package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdResponse extends BaseJSONResponse {
	public String result = "";
	public String adPicUrl = "";
	public String adPicTime = "";
	public String basePicUrl = "";
	public String basePicTime = "";
	public String startuppic_Url = "";
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
//		JSONObject jsonObjectRoot;
//		try {
//			jsonObjectRoot = new JSONObject(bodyElement);
//			result = jsonObjectRoot.getString("result");
//			if (result.equals("1")) {
//				JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");
//				if (JsonArrayData != null) {
//					JSONObject jsonObjectData = JsonArrayData.getJSONObject(0);
//					adPicTime = jsonObjectData.getString("startuppic_StartDate");
//					adPicUrl = jsonObjectData.getString("startuppic");
//					jsonObjectData = JsonArrayData.getJSONObject(1);
//					basePicTime = jsonObjectData.getString("startuppic_StartDate");
//					basePicUrl = jsonObjectData.getString("startuppic");
//				}
//			}
		try {		
		Log.e("AdResponse",bodyElement);
		JSONArray  jsonArrayRoot =new JSONArray(bodyElement);
		if(jsonArrayRoot!=null){
			JSONObject jsonObjectRoot =jsonArrayRoot.getJSONObject(0);
			result = jsonObjectRoot.getString("result");
			if(result.equals("1")){					
				JSONObject jsonObject = jsonObjectRoot.getJSONObject("data");
				if(jsonObject.has("startuppic_StartDate")){
					adPicTime = jsonObject.getString("startuppic_StartDate");
				}
				if(jsonObject.has("startuppic")){
					adPicUrl ="http://app."+Constant.IYBHttpHead()+"/dev/"+ jsonObject.getString("startuppic");
			    }
				if(jsonObject.has("startuppic_StartDate")){
					basePicTime = jsonObject.getString("startuppic_StartDate");
				}
				if(jsonObject.has("startuppic")){
					basePicUrl = jsonObject.getString("startuppic");
				}
				if(jsonObject.has("startuppic_Url")){
					startuppic_Url = jsonObject.getString("startuppic_Url");
				}
				Log.e("AdResponse",""+adPicUrl);
			}
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
