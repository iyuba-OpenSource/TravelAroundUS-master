package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseListRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

private String id,type,blogmaxId,pageCounts;
	
	public CourseListRequest(String id,String type,String pageNumber) {
		this.id=id;
		this.type=type;
		MD5 m=new MD5();
		
		//根据ID和Type取包的信息
		setAbsoluteURI("http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?protocol=10102&id="+this.id+"&type=1&pageNumber="+pageNumber+"&pageCounts=20" +
				"&sign=" + MD5.getMD5ofStr("10102class"+this.id));
		
		Log.d("CourseListRequest:","http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?protocol=10102&id="+this.id+"&type=1&pageNumber="+pageNumber+"&pageCounts=20" +
				"&sign=" + MD5.getMD5ofStr("10102class"+this.id));
		
		//该地址取全部包的信息
//		setAbsoluteURI("http://class."+Constant.IYBHttpHead+"/getClass.iyuba?protocol=10004&sign=7dfa531f79338a7c565fe03e516bea5c");
//					
//		System.out.println("http://class."+Constant.IYBHttpHead+"/getClass.iyuba?protocol=10004&sign=7dfa531f79338a7c565fe03e516bea5c");
			
		//这里获取对应的APPID对应的课程包
//		setAbsoluteURI("http://class."+Constant.IYBHttpHead+"/getClass.iyuba?protocol=10102&id="+this.id+"&type="+this.type+"&sign="+
//						m.getMD5ofStr("10102class"+this.id));
//		System.out.println("http://class."+Constant.IYBHttpHead+"/getClass.iyuba?protocol=10102&id="+this.id+"&type="+this.type+"&sign="+
//				m.getMD5ofStr("10102class"+this.id));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CourseListResponse();
	}

}

