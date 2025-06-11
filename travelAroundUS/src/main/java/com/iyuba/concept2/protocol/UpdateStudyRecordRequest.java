package com.iyuba.concept2.protocol;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UpdateStudyRecordRequest extends BaseXMLRequest {

	private String appName = "concept";
	private String macAddress="";
	private String platform = "android";
	
	public UpdateStudyRecordRequest(String uid, String title, int voaId, String startTime, String endTime) {
		WifiManager wifiManager = (WifiManager) CrashApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			WifiInfo wInfo = wifiManager.getConnectionInfo();
			if (wInfo != null) {
				macAddress = wInfo.getMacAddress(); 
			}
		}
		
		try {
			appName = URLEncoder.encode(
						URLEncoder.encode(appName, "UTF-8"), "UTF-8");
			
			startTime = URLEncoder.encode(startTime, "UTF-8");
			title = URLEncoder.encode(title, "UTF-8");
			endTime = URLEncoder.encode(endTime, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "http://daxue."+Constant.IYBHttpHead()+"/ecollege/updateStudyRecord.jsp?format=xml" +
				"&uid=" + uid + 
				"&BeginTime=" + startTime + 
				"&EndTime=" + endTime + 
				"&appName=" + appName +
				"&Lesson=" + title +
				"&LessonId=" + voaId +
				"&appId=" + Constant.APPID +
				"&DeviceId=" + macAddress + 
				"&platform=" + platform;
		
		
		setAbsoluteURI(url);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new UpdateStudyRecordResponse();
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		
	}

}
