package com.iyuba.concept2.protocol;

import java.io.IOException;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

/**
 * 分享
 * 
 * @author chentong
 * 
 */
public class ShareRequest extends BaseXMLRequest {

	/**
	 * 
	 * @param userId
	 *            用户ID
	 * @param titleid
	 *            分享文章ID
	 * @param to
	 *            分享到哪个平台
	 * @param sig
	 *            认证码Md5(userId+appId+from+to+titleId+type+’iyuba’)
	 */
	public ShareRequest(String userId, String titleid, String to, String sig) {
		String requestUrl = "http://app."+Constant.IYBHttpHead()+"/share/doShare.jsp?userId="
				+ userId + "&appId="+Constant.APPID+"&from=android&to=" + to
				+ "&type=0&titleId=" + titleid + "&sign=" + sig;
		setAbsoluteURI(requestUrl);
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ShareResponse();
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {

	}

}
