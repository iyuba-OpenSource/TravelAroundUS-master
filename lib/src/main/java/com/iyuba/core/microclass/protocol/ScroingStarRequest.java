package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 
 * @author chentong
 * @time 13.4.18 获取文章评论列表API
 */
public class ScroingStarRequest extends BaseJSONRequest {

	String format = "json"; // 可选，默认为json格式
	String packid = "0";
	String pageNumber = "1";
	String pageCount = "15";

	public ScroingStarRequest(String PackId, String pageNumber) {
		this.packid = PackId;
		
		//先用苹果的评论
		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/appApi/UnicomApi?protocol=60001&platform=ios&format=json&voaid="
				+ packid
				+ "&pageNumber="
				+ pageNumber
				+ "&pageCounts="
				+ pageCount + "&appName=microclass");
		Log.d("ScroingStarRequest", "http://daxue."+Constant.IYBHttpHead()+"/appApi/UnicomApi?protocol=60001&platform=ios&format=json&voaid="
				+ packid
				+ "&pageNumber="
				+ pageNumber
				+ "&pageCounts="
				+ pageCount + "&appName=microclass");
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ScroingStarResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO 自动生成的方法存根

	}

}
