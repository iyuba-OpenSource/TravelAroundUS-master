package com.iyuba.core.common.protocol.message;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestSendMssage extends BaseJSONRequest {
	public static final String protocolCode = "60002";

	public RequestSendMssage(String userId, int  voaId, String content) {
		//http://daxue.iyuba.cn/appApi/UnicomApi?platform=android&format=json&protocol=60002&
		// userid=50000038&voaid=1001&content=%E5%A4%A7%E5%AE%B6%E5%A5%BD&shuoshuotype=0&appName=concept
		content = TextAttr.encode(content);
		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/appApi/UnicomApi?protocol="
				+ protocolCode + "&userid=" + userId + "&voaid=" + voaId +"&content=" + content
				+"&shuoshuotype=" + 0 +"&appName=" + "familyalbum"+"&format=" + "json"+"&platform=" + "android");
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ResponseSendMsg();
	}

}
