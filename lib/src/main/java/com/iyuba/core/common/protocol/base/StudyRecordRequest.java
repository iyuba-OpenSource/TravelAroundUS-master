package com.iyuba.core.common.protocol.base;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;
import com.iyuba.core.common.sqlite.mode.StudyRecord;
import com.iyuba.core.common.util.GetMAC;
import com.iyuba.core.common.util.TextAttr;

import java.io.IOException;

/**
 * 获取网页单词本
 * 
 * @author Administrator
 * 
 */
public class StudyRecordRequest extends BaseXMLRequest {
	private StringBuffer sb = new StringBuffer();
	private String device = android.os.Build.BRAND + android.os.Build.MODEL
			+ android.os.Build.DEVICE;

	public StudyRecordRequest(String uid, StudyRecord studyRecord) {
		sb.append("http://daxue."+Constant.IYBHttpHead()+"/ecollege/updateStudyRecord.jsp?format=xml&appId=");
		sb.append(Constant.APPID);
		sb.append("&appName=").append(Constant.APPType);
		sb.append("&Lesson=").append(studyRecord.lesson);
		sb.append("&LessonId=").append(studyRecord.voaid);
		sb.append("&uid=").append(uid);
		sb.append("&Device=").append(TextAttr.encode(device));
		sb.append("&DeviceId=").append(GetMAC.getMAC());
		sb.append("&BeginTime=").append(TextAttr.encode(studyRecord.starttime));
		sb.append("&EndTime=").append(TextAttr.encode(studyRecord.endtime));
		sb.append("&EndFlg=").append(studyRecord.flag);
		if (studyRecord.lesson.equals(TextAttr.encode(TextAttr
				.encode(Constant.APPName)))) {
			sb.append("&testNumber=").append(Constant.type);
		}
		setAbsoluteURI(sb.toString());

		Log.e("StudyRecordRequest",sb.toString());
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new StudyRecordResponse();
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}
}
