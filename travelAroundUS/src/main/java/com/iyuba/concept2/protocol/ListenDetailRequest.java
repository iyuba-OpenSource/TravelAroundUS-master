package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class ListenDetailRequest extends BaseJSONRequest {
	private String uid;
	private String page;
	private String numPerPage;
	private String testMode;
	SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	String sign;

	public ListenDetailRequest(String uid, String page, String numPerPage,
			String testMode) {
		this.uid = uid;
		this.page = page;
		this.numPerPage = numPerPage;
		this.testMode = testMode;
		this.sign = uid + dft.format(System.currentTimeMillis());
		
		
		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/ecollege/getStudyRecordByTestMode.jsp?format=json&uid="
				+ uid
				+ "&Pageth="
				+ page
				+ "&NumPerPage="
				+ numPerPage
				+ "&TestMode=" + testMode + "&sign=" + MD5.getMD5ofStr(sign));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub


	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ListenDetailResponse();
	}

}
