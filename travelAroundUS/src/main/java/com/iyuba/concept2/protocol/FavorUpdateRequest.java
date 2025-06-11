package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

import java.io.IOException;

public class FavorUpdateRequest extends BaseXMLRequest {
		
	private StringBuilder uri = new StringBuilder("http://daxue."+Constant.IYBHttpHead()+"/appApi/updateCollect.jsp?");

	public FavorUpdateRequest(String userid, int voaid, String type) {
		uri.append("userId=" + userid);
		uri.append("&voaId=" + voaid);
		uri.append("&sentenceId=0");
		uri.append("&type=" + type);
		uri.append("&groupName=Iyuba");
		uri.append("&sentenceFlg=0");
		uri.append("&appName="+Constant.TOPICID);//appName=concept familyalbum
		
		setAbsoluteURI(uri.toString());
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new FavorUpdateResponse();
	}

}
