/**
 * 
 */
package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yao
 * 
 */
public class ReadNoticeRequest extends BaseJSONRequest {

	public ReadNoticeRequest(String ids) {
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/readNotice.jsp?ids="+ids);
		
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/readNotice.jsp?ids="+ids);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ReadNoticeResponse();
	}

}
