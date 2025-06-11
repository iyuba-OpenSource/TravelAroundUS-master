package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SlideShowCourseListRequest extends BaseJSONRequest {

	private String type;
	
	public SlideShowCourseListRequest(String typeDesc) {
		this.type=typeDesc;
		
		//根据ID和Type取包的信息
		setAbsoluteURI("http://app."+Constant.IYBHttpHead()+"/dev/getScrollPicApi.jsp?type="+type);
		
		Log.e("SlideRequest:","http://app."+Constant.IYBHttpHead()+"/dev/getScrollPicApi.jsp?type="+type);
		
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new SlideShowCourseListResponse();
	}

}

