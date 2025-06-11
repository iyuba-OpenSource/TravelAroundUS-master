package com.iyuba.concept2.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 用户登录
 * 
 * @author chentong
 */
public class AppUpdateRequest extends BaseJSONRequest {

	private int version;

	public AppUpdateRequest(int version) {
		this.version = version;
		setAbsoluteURI(Constant.appUpdateUrl() + this.version);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new AppUpdateResponse();
	}

}
