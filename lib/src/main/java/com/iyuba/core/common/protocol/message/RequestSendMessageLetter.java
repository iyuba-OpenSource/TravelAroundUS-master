/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * @author 发私信
 */
public class RequestSendMessageLetter extends BaseJSONRequest {
	public static final String protocolCode = "60002";

	public RequestSendMessageLetter(String uid, String username, String context) {
		// super(protocolCode);
		// TODO Auto-generated constructor stub
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2()+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&username="
				+ URLEncoder.encode(username) + "&context="
				+ URLEncoder.encode(context) + "&sign="
				+ MD5.getMD5ofStr(protocolCode + uid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseSendMessageLetter();
	}

}
