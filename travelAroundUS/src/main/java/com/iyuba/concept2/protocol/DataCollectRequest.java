package com.iyuba.concept2.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

public class DataCollectRequest extends BaseJSONRequest {
	/**
	 *  学习记录提交
	 *  参数说明：
	 *  BeginTime（非空）：听句子的开始时间
	 *  EndTime：         听句子的结束时间
	 *  EndFlg：          完成标志0：只开始听； 1：听力完成； 2：做题完成
	 *  TestNumber（可空）：      测试题号（默认值为0）
	 *  testWords（可空）：       测试单词数（默认值为0）
	 *  TestMode（可空）：        测试模式1：听力   2：口语    3：阅读   4：写作   0：未确定
	 *  userAnswer（可空）：      所选单词
	 *  score（可空）：           用户得分（默认值为0）
	 *
	 **/
	public DataCollectRequest(String uid, String beginTime, String endTime,
			String lesson, String lessonId, String testNumber,
			String testWords, String testMode, String userAnswer, String score,
			String endFlg, String deviceId, String sign)
			throws UnsupportedEncodingException {
		 String requestUrl =
		 "http://daxue."+Constant.IYBHttpHead()+"/ecollege/updateStudyRecordNew.jsp?"
		 + "format=json&platform=android&appName="
		 + URLEncoder.encode(
		 URLEncoder.encode(Constant.APPType, "UTF-8"), "UTF-8")
		 + "&Lesson="                                             //lesson应该上传用户使用的app的名称
		 + URLEncoder.encode(lesson, "UTF-8")
		 + "&appId="
		 + Constant.APPID
		 + "&BeginTime="
		 + URLEncoder.encode(beginTime, "UTF-8")
		 + "&EndTime="
		 + URLEncoder.encode(endTime, "UTF-8")
		 + "&EndFlg="
		 + endFlg
		 + "&LessonId="
		 + lessonId
		 + "&TestNumber="
		 + testNumber
		 + "&TestWords="
		 + testWords
		 + "&TestMode="
		 + testMode
		 + "&UserAnswer="
		 + userAnswer
		 + "&Score="
		 + score
		 + "&DeviceId="
		 + deviceId
		 + "&uid="
		 + uid
		 + "&sign="
		 + MD5.getMD5ofStr(sign);
//		String requestUrl ="";
		Log.e("url",
				"http://daxue."+Constant.IYBHttpHead()+"/ecollege/updateStudyRecordNew.jsp?"
						+ "format=json&platform=android&appName="
						+ URLEncoder.encode(
								URLEncoder.encode(Constant.APPType, "UTF-8"),
								"UTF-8")
						+ "&Lesson="
						+ URLEncoder.encode(lesson, "UTF-8")
						+ "&appId="
						+ Constant.APPID
						+ "&BeginTime="
						+ URLEncoder.encode(beginTime, "UTF-8")
						+ "&EndTime="
						+ URLEncoder.encode(endTime, "UTF-8")
						+ "&EndFlg="
						+ endFlg
						+ "&LessonId="
						+ lessonId
						+ "&TestNumber="
						+ testNumber
						+ "&TestWords="
						+ testWords
						+ "&TestMode="
						+ testMode
						+ "&UserAnswer="
						+ userAnswer
						+ "&Score="
						+ score
						+ "&DeviceId="
						+ deviceId
						+ "&uid="
						+ uid
						+ "&sign=" + MD5.getMD5ofStr(sign));
//		String requestUrl = "";
		setAbsoluteURI(requestUrl);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new DataCollectResponse();
	}

}
