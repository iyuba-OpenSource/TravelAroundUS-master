package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.concept2.sqlite.mode.Comment;
import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

import java.util.ArrayList;
import java.util.Vector;

/**
 * 
 * 
 * @author zhujiyang
 * @time 12.11.02
 * 
 */

public class CommentResponse extends BaseXMLResponse {
	public ArrayList<Comment> Comments = new ArrayList<Comment>();
	public int total;
	public String resultCode;
	public String pageNumber;
	public String totalPage;
	public String firstPage;
	public String prevPage;
	public String nextPage;
	public String lastPage;
	public String counts;
	public String message;
	private Comment tempComment;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		resultCode = Utility.getSubTagContent(bodyElement, "ResultCode");
		message = Utility.getSubTagContent(bodyElement, "Message");
		
		if (resultCode != null && resultCode.equals("511")) {
			pageNumber = Utility.getSubTagContent(bodyElement, "PageNumber");
			totalPage = Utility.getSubTagContent(bodyElement, "TotalPage");
			firstPage = Utility.getSubTagContent(bodyElement, "FirstPage");
			prevPage = Utility.getSubTagContent(bodyElement, "PrevPage");
			nextPage = Utility.getSubTagContent(bodyElement, "NextPage");
			lastPage = Utility.getSubTagContent(bodyElement, "LastPage");
			counts = Utility.getSubTagContent(bodyElement, "Counts");
			
			Log.e("CommentResponse counts", counts + "");

			Vector rankVector = bodyElement.getChildren();

			for (int i = 0; i < rankVector.size(); i++) {
				kXMLElement ranKXMLElement = (kXMLElement) rankVector
						.elementAt(i);

				if (ranKXMLElement.getTagName().equals("Row")) {
					tempComment = new Comment();

					tempComment.id = Utility.getSubTagContent(ranKXMLElement,
							"id");
					tempComment.imgsrc = Utility.getSubTagContent(
							ranKXMLElement, "ImgSrc");
					tempComment.userId = Utility.getSubTagContent(
							ranKXMLElement, "Userid");
					tempComment.agreeCount = Integer.valueOf(Utility
							.getSubTagContent(ranKXMLElement, "agreeCount"));
					tempComment.againstCount = Integer.valueOf(Utility
							.getSubTagContent(ranKXMLElement, "againstCount"));
					tempComment.shuoshuo = Utility.getSubTagContent(
							ranKXMLElement, "ShuoShuo");
					tempComment.shuoshuoType = Integer.valueOf(Utility
							.getSubTagContent(ranKXMLElement, "ShuoShuoType"));
					tempComment.username = Utility.getSubTagContent(
							ranKXMLElement, "UserName");
					tempComment.createdate = Utility.getSubTagContent(
							ranKXMLElement, "CreateDate");
					Comments.add(tempComment);
				}
			}
		} else if (resultCode != null && resultCode.equals("510")) {
			Log.d("获取评论内容", "没有评论内容" + message);
		}

		return true;
	}
}
 