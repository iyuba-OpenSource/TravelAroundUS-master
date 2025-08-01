
package com.iyuba.core.teacher.protocol;import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.teacher.sqlite.mode.AnswerInfo;
import com.iyuba.core.teacher.sqlite.mode.Chat;
import com.iyuba.core.teacher.sqlite.mode.Question;

public class GetChatListResponse extends BaseJSONResponse {

	public String result;
	public String total;
	public String message;
	public Question item=new Question();
	public List<AnswerInfo> infoList = new ArrayList<AnswerInfo>();
	public List<List<Chat>> chatList = new ArrayList<List<Chat>>();
	public	HashMap<String ,String> cat1=new HashMap<String, String>();
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		JSONObject jsonBody = null;
		try {
			jsonBody = new JSONObject(bodyElement);
			Log.e("GetChatListResponse", jsonBody.toString());
			result = jsonBody.getString("result");
			total = jsonBody.getString("total");
			
			
			//去question信息
			JSONObject qjson= jsonBody.getJSONObject("question");
			item.qid = qjson.getInt("questionid");
			item.uid = qjson.getString("uid");
			item.username = qjson.getString("username");
			item.userimg = qjson.getString("imgsrc");
			item.question =qjson.getString("question");
			item.img = qjson.getString("img");
			item.audio = qjson.getString("audio");
			item.ansCount = qjson.getInt("answercount");
			item.time = qjson.getString("createtime");
			item.location = qjson.getString("location");
			item.type=qjson.getString("app");
			item.source=qjson.getString("app");
			item.agree=qjson.getInt("agreecount");
			item.questiondetail=qjson.getString("questiondetail");
			item.category1=cat1.get(qjson.getString("category1"));
			if(cat1.get(qjson.getString("category1"))==null)
				item.category1= qjson.getString("category1");
			//item.category2=cat2.get(jsonObject.getString("category2"));
		//	if(cat2.get(jsonObject.getString("category2"))==null)  
				item.category2=qjson.getString("category2");
			
			
			
			
			
			
			
			if (result.equals("1")) {
				JSONArray data = jsonBody.getJSONArray("answers");
				if (data != null && data.length() != 0) {
					int size = data.length();
					AnswerInfo item = null;
					
					Chat chat = null;
					JSONObject jsonObject = null;
					JSONArray jsonArray = null;
					for (int i = 0; i < size; i++) {
						List<Chat> list = new ArrayList<Chat>();
						item = new AnswerInfo();
						chat = new Chat();
						jsonObject = ((JSONObject) data.opt(i));
						item.answerid = jsonObject.getInt("answerid");
						item.qid = jsonObject.getInt("questionid");
						item.uid = jsonObject.getInt("authorid");
						item.username = jsonObject.getString("username");
						item.userimg = jsonObject.getString("imgsrc");
						item.time = jsonObject.getString("answertime");
						item.timg = jsonObject.getString("timg");
//						item.location = jsonObject.getString("location");
						item.agreeCount=jsonObject.getInt("agreecount");
						infoList.add(item);
						chat.chatid=0;
						chat.answerid = item.answerid;
						chat.content = jsonObject.getString("answer");
						chat.type = jsonObject.getInt("type");
						chat.fromid = item.uid;
						chat.userType = 0;
						chat.fromname = item.username;
						chat.fromimg = item.userimg;
						chat.createtime = item.time;
						list.add(chat);

						jsonArray = jsonObject.getJSONArray("zquestions");
						if (jsonArray != null && jsonArray.length() != 0) {
							for (int k = 0; k < jsonArray.length(); k++) {
								JSONObject json = null;
								try {
									chat = new Chat();
									json = ((JSONObject) jsonArray.opt(k));

									chat.chatid = json.getInt("zqid");
									chat.answerid = json.getInt("answerid");
								//	chat.content = json.getString("answer");
									chat.type = json.getInt("type");
									chat.content = json.getString("zcontent");
									Log.e("infoList", json.getString("zcontent"));
									chat.fromid = json.getInt("fromid");
									chat.fromname = json
											.getString("fromusername");
									chat.fromimg = json.getString("fromimgsrc");
									chat.createtime = json
											.getString("createtime");
									chat.toid = json.getInt("toid");
									
									if(item.uid == chat.fromid) {
										chat.userType = 0;
									} else {
										chat.userType = 1;
									}
									list.add(chat);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}

						chatList.add(list);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.e("infoList", infoList.size() + "#####");
		Log.e("chatList", chatList.size() + "******");

		return true;
	}
	public  void setCat1(){
		cat1=new HashMap<String, String>();
		cat1.put("0", "其他");
		cat1.put("1", "口语");
		cat1.put("2", "听力");
		cat1.put("3", "阅读");
		cat1.put("4", "写作");
		cat1.put("5", "翻译");
		cat1.put("6", "单词");
		cat1.put("7", "语法");
		cat1.put("8", "其他");
	}
}
