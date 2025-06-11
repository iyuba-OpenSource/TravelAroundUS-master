package com.iyuba.core.teacher.protocol;
import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AgreeAgainstRequest extends BaseJSONRequest {

	public AgreeAgainstRequest(String uid, String uname,String type,String typeid) {
		super();
		
		 //用户名转码
		 try {
			 uname=	new String (uname.getBytes( "UTF-8") );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		 uname= TextAttr.encode(TextAttr.encode(TextAttr.encode(uname)));
		
		// TODO 自动生成的构造函数存根
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/updateAgree.jsp?uid="+uid
				+"&username="+uname+"&type="+type+"&typeid="+typeid
				);
		
		
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/updateAgree.jsp?uid="+uid
				+"&username="+uname+"&type="+type+"&typeid="+typeid);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO 自动生成的方法存根

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO 自动生成的方法存根
		return new AgreeAgainstResponse();
	}

}
