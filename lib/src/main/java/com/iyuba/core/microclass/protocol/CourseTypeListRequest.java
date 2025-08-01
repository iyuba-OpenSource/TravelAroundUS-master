package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseTypeListRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

	public CourseTypeListRequest() {
		
		//该地址取全部包的分类信息
		
		setAbsoluteURI("http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?protocol=10103&type=0&sign=806e43f1d3416670861ef3b187f6a27c");
					
		Log.d("CourseTypeListRequest:","http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?protocol=10103&type=0&sign=806e43f1d3416670861ef3b187f6a27c");
			
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CourseTypeListResponse();
	}

}

