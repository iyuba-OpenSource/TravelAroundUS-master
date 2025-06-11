package com.iyuba.concept2.protocol;

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
 * @time 13.4.18
 *获取文章评论列表API
 */
public class CommentRequest extends BaseJSONRequest {

	String format = "json"; // 可选，默认为json格式
	String bbcid = "0";
	String pageNumber = "1";
	String pageCount = "15";
	String appName = "familyalbum";//concept
	public CommentRequest(String bbcid , String pageNumber) {
		this.bbcid = bbcid;
		setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/appApi/UnicomApi?protocol=60001&platform=android&format=xml&voaid="
				+ bbcid + "&pageNumber=" + pageNumber + "&pageCounts=" + pageCount + "&appName=" + appName);
		//setMethod(BaseHttpRequest.POST);
		Log.e("CommentRequest", "http://daxue."+Constant.IYBHttpHead()+"/appApi/UnicomApi?protocol=60001&platform=android&format=xml&voaid="
				+ bbcid + "&pageNumber=" + pageNumber + "&pageCounts=" + pageCount + "&appName=" + appName);
	}
	
	/*public CommentRequest(String bbcid, String pageNumber, String pageCount) {
		this.bbcid = bbcid;
		this.pageNumber = pageNumber;
		this.pageCount = pageCount;
		setAbsoluteURI("http://apps."+Constant.IYBHttpHead+"/voa/updateShuoShuo.jsp?groupName=iyuba&mod=select&topicId="
				+ bbcid + "&pageNumber=" + pageNumber + "&pageCounts=" + pageCount + "&format=" + format);
	}*/
	@Override
	public BaseHttpResponse createResponse() {
		return new CommentResponse();
	}
	
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}


}
