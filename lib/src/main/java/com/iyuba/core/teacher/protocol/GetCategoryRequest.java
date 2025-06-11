package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetCategoryRequest  extends BaseJSONRequest {


	public GetCategoryRequest( ) {
		setAbsoluteURI("http://www. "+Constant.IYBHttpHead()+"/question/getCategoryList.jsp");
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/getCategoryList.jsp");
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new GetCategoryResponse();
	}

}
