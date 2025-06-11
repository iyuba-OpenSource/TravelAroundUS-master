/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yao 验证短信验证码
 */
public class RequestSubmitMessageCode extends BaseJSONRequest {

	public RequestSubmitMessageCode(String userphone) {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2()+"/sendMessage3.jsp?format=json"
				+ "&userphone=" + userphone);
		Log.e("RequestSubmitMessageCode", "http://api."+Constant.IYBHttpHead2()+"/sendMessage3.jsp?format=json"
				+ "&userphone=" + userphone);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseSubmitMessageCode();
	}

}
