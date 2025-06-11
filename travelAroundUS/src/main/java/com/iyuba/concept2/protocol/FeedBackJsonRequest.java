package com.iyuba.concept2.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 反馈请求
 * 
 * @author chentong
 * 
 */
public class FeedBackJsonRequest extends BaseJSONRequest {
	private String content;
	private String email;
	private String uid;

	public FeedBackJsonRequest(String content, String email, String uid) {
		String para[] = content.split(" ");
		content = "";
		for (int i = 0; i < para.length - 1; i++)
			content += para[i] + "%20";
		content += para[para.length - 1];
		this.content = content;
		if (uid != null && uid.length() != 0) {
			this.uid = uid;
		} else {
			this.uid = "";
		}
		if (email != null && email.length() != 0) {
			this.email = email;
		} else {
			this.email = "";
		}
		setAbsoluteURI(Constant.feedBackUrl() + this.uid + "&content="
				+ this.content + "&email=" + this.email);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new FeedBackJsonResponse();
	}

}
