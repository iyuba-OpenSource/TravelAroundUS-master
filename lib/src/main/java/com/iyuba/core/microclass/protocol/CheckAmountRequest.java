package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

import java.io.IOException;


/**
 * 鏌ヨ鐖辫甯?
 * 
 * @author chentong
 * 
 */
public class CheckAmountRequest extends BaseXMLRequest {
	private String UserId;
	
	public CheckAmountRequest(String userId) {
		if(userId == null){
			this.UserId = "0";
		}else{
			this.UserId=userId;
		}
		setAbsoluteURI("http://app."+Constant.IYBHttpHead()+"/pay/checkApi.jsp?userId=" + UserId);
		Log.e("check","http://app."+Constant.IYBHttpHead()+"/pay/checkApi.jsp?userId=" + UserId );
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CheckAmountResponse();
	}

}

