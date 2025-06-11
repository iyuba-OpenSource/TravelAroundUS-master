package com.iyuba.core.search.request;

import android.util.Log;

import com.android.volley.Response.Listener;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.Base64Coder;
import com.iyuba.core.me.pay.BaseJsonObjectRequest;
import com.iyuba.core.me.pay.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

public class AddCreditsRequest extends BaseJsonObjectRequest {
	private static final String TAG = AddCreditsRequest.class.getSimpleName();
	public String result;
	public int addCredit;
	public int totalCredit;
	public String message = "";

	public AddCreditsRequest(final int uid, int voaid, int srid,
                             final RequestCallBack rc) {
		super(Constant.addCreditsUrl() + "srid=" + srid
				+ "&uid=" + uid + "&appid=" + Constant.APPID
				+ "&idindex="	+ voaid	+ "&mobile=1"	+ "&flag="+"1234567890"+Base64Coder.getTime());
//				+ Base64Coder
//						.encode(MD5.getMD5ofStr(AccountManager.Instance(null).userPwd)));
//		Log.e("userPwd", Base64Coder
//				.encode(MD5.getMD5ofStr(AccountManager.Instance(null).userPwd)));
		setResListener(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonBody) {
				Log.e(TAG, jsonBody.toString());
				try {
					result = jsonBody.getString("result");
					Log.e("score",result);
					if(isRequestSuccessful()){
						String addcred = jsonBody.getString("addcredit");
						if(!"".equals(addcred))
							addCredit = Integer.parseInt(addcred);
						else
							addCredit = 0;
						String total = jsonBody.getString("totalcredit");
						if(!"".equals(total))
							totalCredit = Integer.parseInt(total);
						else
							totalCredit = 0;
					} else {
						message = jsonBody.getString("message");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				rc.requestResult(AddCreditsRequest.this);
			}
		});
	}

	@Override
	public boolean isRequestSuccessful() {
		return ("200".equals(result)) ? true : false;
	}
	
	public boolean isShareFirstlySuccess(){
		return isRequestSuccessful();
	}
	
	public boolean isShareRepeatlySuccess(){
		return ("201".equals(result)) ? true : false;
	}

}
