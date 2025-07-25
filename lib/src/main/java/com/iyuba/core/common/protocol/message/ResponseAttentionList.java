/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.Attention;
import com.iyuba.core.me.sqlite.mode.Fans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author
 * 
 */
public class ResponseAttentionList extends BaseJSONResponse {

	public String result;// 返回代码
	public String message;// 返回信息
	public Fans fan = new Fans();
	public JSONArray data;
	public ArrayList<Attention> fansList;
	public int num;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		fansList = new ArrayList<Attention>();
		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getString("result");
			message = jsonObjectRootRoot.getString("message");
			Log.e("result--msg",result+"----"+message);
			if (result.equals("550")) {
				num = jsonObjectRootRoot.getInt("num");
				data = jsonObjectRootRoot.getJSONArray("data");
				if (data != null && data.length() != 0) {
					int size = data.length();
					Attention fans;
					JSONObject jsonObject;
					for (int i = 0; i < size; i++) {
						fans = new Attention();
						jsonObject = ((JSONObject) data.opt(i));
						//fans.bkname = jsonObject.getString("bkname");
						fans.vip = jsonObject.getString("vip");
						fans.mutual = jsonObject.getString("mutual");
						fans.followuid = jsonObject.getString("followuid");
						fans.dateline = jsonObject.getString("dateline");
						fans.fusername = jsonObject.getString("fusername");
						fans.doing = jsonObject.getString("doing");
						fansList.add(fans);
					}
					Log.e("TAG--Attention",fansList.size()+"");
				}
			}
		} catch (JSONException e) {
			// TODO: handle exception
			Log.e("result--msg",e.toString());
			e.printStackTrace();
		}
		return true;
	}

}
