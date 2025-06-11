//package com.iyuba.concept2.protocol;
//
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.BaseJSONRequest;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * 用户登录
// *
// * @author chentong
// */
//public class AdRequest extends BaseJSONRequest {
//
//	public AdRequest() {
//		//TODO
////		setAbsoluteURI("http://app."+Constant.IYBHttpHead+"/dev/getStartPicApi.jsp?format=json&appId="
////				+ Constant.APPID);
//		setAbsoluteURI("http://app."+Constant.IYBHttpHead()+"/dev/getAdEntryAll.jsp?appId=222&flag=1");
//	}
//
//	@Override
//	protected void fillBody(JSONObject jsonObject) throws JSONException {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public BaseHttpResponse createResponse() {
//		// TODO Auto-generated method stub
//		return new AdResponse();
//	}
//
//}
