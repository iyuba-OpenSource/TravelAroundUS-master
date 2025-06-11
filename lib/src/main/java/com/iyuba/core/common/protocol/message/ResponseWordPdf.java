package com.iyuba.core.common.protocol.message;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseWordPdf extends BaseJSONResponse {
	//{"result":1,"filePath":"http://ai.iyuba.cn/management/wordPDF/20191203054524532.pdf"}
	public String filePath;
	public int result;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {

		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getInt("result");
			filePath = jsonObjectRootRoot.getString("filePath");
			if (result==1){
				LogUtils.d("导出PDF成功！"+filePath);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

}
