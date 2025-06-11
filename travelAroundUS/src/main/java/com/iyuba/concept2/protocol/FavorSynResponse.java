package com.iyuba.concept2.protocol;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FavorSynResponse extends BaseXMLResponse {
	public List<Integer> list = new ArrayList<Integer>();
	public int total;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		Vector rankVector = bodyElement.getChildren();
		total = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"totalPage"));
		int size = rankVector.size();

		kXMLElement ranKXMLElement = null;
		int voaId = 0;
		for (int i = 0; i < size; i++) {
			ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			try {
				voaId = Integer.parseInt(Utility.getSubTagContent(
						ranKXMLElement, "voaid"));
				list.add(voaId);
			} catch (Exception e) {

			}
		}
		return true;
	}
}
