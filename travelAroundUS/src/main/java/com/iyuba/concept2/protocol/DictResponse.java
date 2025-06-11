package com.iyuba.concept2.protocol;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;
import com.iyuba.concept2.sqlite.mode.NewWord;

import java.util.Vector;

public class DictResponse extends BaseXMLResponse {
	public NewWord newWord;
	public String result;
	
	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		newWord = new NewWord();
		result = Utility.getSubTagContent(bodyElement, "result");
		
		newWord.word = Utility.getSubTagContent(bodyElement, "key");
		newWord.audio = Utility.getSubTagContent(bodyElement, "audio");
		newWord.pron = Utility.getSubTagContent(bodyElement, "pron");
		newWord.def = Utility.getSubTagContent(bodyElement, "def");
		
		Vector rankVector = bodyElement.getChildren();
		StringBuffer sentence = new StringBuffer();
		
		for (int i = 0; i < rankVector.size(); i++) {
			kXMLElement ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			if (ranKXMLElement.getTagName().equals("sent")) {
				sentence.append(Utility.getSubTagContent(ranKXMLElement,
						"number"));
				sentence.append("：");
				sentence.append(Utility
						.getSubTagContent(ranKXMLElement, "orig"));
				sentence.append("<br/>");
				sentence.append(Utility.getSubTagContent(ranKXMLElement,
						"trans"));
				sentence.append("<br/>");
			}
		}
		
		newWord.examples = sentence.toString();
		
		return true;
	}

}
