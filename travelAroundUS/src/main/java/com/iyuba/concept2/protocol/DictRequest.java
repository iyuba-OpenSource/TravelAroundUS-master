package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

import java.io.IOException;

/**
 * 获取网页单词本
 * @author Administrator
 *
 */
public class DictRequest extends BaseXMLRequest {
	
	String word="";
	public DictRequest(String word){
		this.word=word;
		setAbsoluteURI("http://word."+Constant.IYBHttpHead()+"/words/apiWord.jsp?q="+word);
	}
	
	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new DictResponse();
	}

}
