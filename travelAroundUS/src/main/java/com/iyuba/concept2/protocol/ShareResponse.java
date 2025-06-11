package com.iyuba.concept2.protocol;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class ShareResponse extends BaseXMLResponse {

	public int result = 0;
	public String shareId = "";
	public String msg = "";

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"result"));
		shareId = Utility.getSubTagContent(bodyElement, "shareId");
		msg = Utility.getSubTagContent(bodyElement, "msg");
		return true;
	}

}
