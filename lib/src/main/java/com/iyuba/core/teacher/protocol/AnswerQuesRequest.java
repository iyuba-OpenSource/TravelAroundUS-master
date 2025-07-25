package com.iyuba.core.teacher.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AnswerQuesRequest extends BaseJSONRequest {
	private String format = "json";
	
	public AnswerQuesRequest(String uid, String username, int authorType, int qid, String answer) {
		
		 //用户名转码
		 try {
			 username=	new String (username.getBytes( "UTF-8") );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		 username= TextAttr.encode(TextAttr.encode(TextAttr.encode(username)));
		setAbsoluteURI("http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
				+ "format=" + format
				+ "&authorid=" + uid
				+ "&username=" +TextAttr.encode(TextAttr.encode(TextAttr.encode(username))) 
				+ "&questionid=" + qid
				+ "&answer=" + TextAttr.encode(TextAttr.encode(TextAttr.encode( answer)))
				+ "&authortype=" + authorType);
		
		Log.e("iyuba", "http://www."+Constant.IYBHttpHead()+"/question/answerQuestion.jsp?"
				+ "format=" + format
				+ "&authorid=" + uid
				+ "&username=" +TextAttr.encode(TextAttr.encode(TextAttr.encode(username))) 
				+ "&questionid=" + qid
				+ "&answer=" + TextAttr.encode(TextAttr.encode(TextAttr.encode( answer)))
				+ "&authortype=" + authorType);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new AnswerQuesResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
