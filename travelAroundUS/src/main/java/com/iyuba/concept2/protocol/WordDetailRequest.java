package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class WordDetailRequest extends BaseJSONRequest {
	private String uid;
	SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	private String sign;

	public WordDetailRequest(String uid, String mode) {
		this.uid = uid;
		this.sign = uid + dft.format(System.currentTimeMillis());
		// setAbsoluteURI("http://daxue."+Constant.IYBHttpHead+"/ecollege/getListenDetailRecord.jsp?format=json&uid="
		// + uid);
		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/ecollege/getWordsRecordDetail.jsp?format=json&uid="
				+ uid
				+ "&TestMode="
				+ mode
				+ "&sign="
				+ MD5.getMD5ofStr(sign));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new WordDetailResponse();
	}

}
