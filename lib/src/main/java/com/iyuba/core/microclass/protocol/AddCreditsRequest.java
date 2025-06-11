package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.Base64Coder;

import org.json.JSONException;
import org.json.JSONObject;

public class AddCreditsRequest extends BaseJSONRequest{
	public AddCreditsRequest(String uid,String idindex){
		String  addIntegralUrl ="http://api."+Constant.IYBHttpHead()+"/credits/updateScore.jsp?srid=49";
		String url =addIntegralUrl+
				 "&uid="+uid+
				 "&idindex="+idindex+
				 "&mobile=1"+
				 "&flag="+"1234567890"+Base64Coder.getTime();
		Log.e("AddCoinRequest",url);		    
		setAbsoluteURI(url);		
	}
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}
	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new AddCreditsResponse();
	}
    
	
}
