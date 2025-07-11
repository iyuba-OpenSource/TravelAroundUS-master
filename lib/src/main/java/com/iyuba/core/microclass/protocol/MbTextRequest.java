package com.iyuba.core.microclass.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;


/*
 * 移动课堂资源数据
 * http://class."+Constant.IYBHttpHead+"/getClass.iyuba?protocol=10003&id=1&sign=7aded281a2b9311cd4e92bbe653b566f
 * */
public class MbTextRequest extends BaseJSONRequest{
	{
		requestId = 0;
	}
	
	private String id,packId;
	
	public MbTextRequest(String id,String packid){
		this.id=id;
		this.packId = packid;
		setAbsoluteURI("http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?protocol=10003&id="+this.id
				+"&sign="+MD5.getMD5ofStr("10003class"+this.id));
		System.out.println("http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?protocol=10003&id="+this.id
				+"&sign="+MD5.getMD5ofStr("10003class"+this.id));
	}
	
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new MbTextResponse(id,packId);
	}

}

