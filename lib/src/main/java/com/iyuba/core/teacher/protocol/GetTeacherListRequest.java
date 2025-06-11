
package com.iyuba.core.teacher.protocol;
import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetTeacherListRequest extends BaseJSONRequest {
	private String format = "json";
	
	public GetTeacherListRequest(int pageNum,int category) {
		setAbsoluteURI("http://www."+Constant.IYBHttpHead() +"/question/teacher/api/getTeacherList.jsp?format=json"+"&pageNum="
				+pageNum+"&category="+category 
				);

	    Log.e("iyuba", "http://www."+Constant.IYBHttpHead() +"/question/teacher/api/getTeacherList.jsp?format=json"+"&pageNum="
				+pageNum+"&category="+category);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new GetTeacherListResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
