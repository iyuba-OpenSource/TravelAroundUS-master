/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.VOABaseJsonRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * @author yao 编辑用户信息
 */
public class RequestEditUserInfo extends VOABaseJsonRequest {
	public static final String protocolCode = "20003";
	public String md5Status = "1"; // 0=未加密,1=加密

	public RequestEditUserInfo(String userId, String key, String value) {
		super(protocolCode);
		setRequestParameter("id", userId);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + userId + "iyubaV2"));
		setRequestParameter("key", key);
		setRequestParameter("value",
				URLEncoder.encode(URLEncoder.encode(value)));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ResponseEditUserInfo();
	}

}
