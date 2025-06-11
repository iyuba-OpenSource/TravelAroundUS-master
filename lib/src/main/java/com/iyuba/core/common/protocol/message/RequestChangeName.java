package com.iyuba.core.common.protocol.message;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestChangeName extends BaseJSONRequest {
	public static final String protocolCode = "10012";

	public RequestChangeName(int userId, String oldName, String newName) {
		String sign = buildV2Sign(protocolCode, String.valueOf(userId));
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2()+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + userId + "&oldUsername=" + oldName +"&username=" + newName
				+"&sign=" + sign+"&format=" + "json");
	}

	private String buildV2Sign(String protocol, String stuff) {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol).append(stuff).append("iyubaV2");
		return com.iyuba.module.toolbox.MD5.getMD5ofStr(sb.toString());
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ResponseChangeName();
	}

}
