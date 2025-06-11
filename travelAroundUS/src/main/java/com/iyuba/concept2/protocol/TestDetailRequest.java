package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class TestDetailRequest extends BaseJSONRequest {
	SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	private String sign;

	public TestDetailRequest(String uid, String testMode,String page, String numPerPage) {
		this.sign = uid + dft.format(System.currentTimeMillis());
		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/ecollege/getTestRecordDetail.jsp?format=json&uid="
				+ uid
				+ "&TestMode="
				+ testMode
				+ "&sign="
				+ MD5.getMD5ofStr(sign)+"&Pageth="
						+ page
						+ "&NumPerPage="
						+ numPerPage);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new TestDetailResponse();
	}

}
