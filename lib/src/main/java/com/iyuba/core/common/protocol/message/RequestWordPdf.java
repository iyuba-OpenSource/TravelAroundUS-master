package com.iyuba.core.common.protocol.message;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestWordPdf extends BaseJSONRequest {

	//http://ai.iyuba.cn/management/getWordToPDF.jsp?u=4729911&pageNumber=1&pageCounts=1200
	public RequestWordPdf(String userId, int pageNumber, int pageCounts) {
		setAbsoluteURI("http://ai."+Constant.IYBHttpHead()+"/management/getWordToPDF.jsp?u="
				+ userId + "&pageNumber=" + pageNumber + "&pageCounts=" + pageCounts);
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
		return new ResponseWordPdf();
	}

}
