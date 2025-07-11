/**
 *
 */
package com.iyuba.core.common.protocol.message;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.me.sqlite.mode.MessageLetter;

/**
 * @author yao 私信列表
 */
public class ResponseMessageLetterList extends BaseJSONResponse {

    public String result;// 返回代码
    public String message;// 返回信息
    public MessageLetter letter = new MessageLetter();
    public JSONArray data;
    public ArrayList<MessageLetter> list;
    public int num;
    public int firstPage;
    public int prevPage;
    public int nextPage;
    public int lastPage;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        // TODO Auto-generated method stub
        list = new ArrayList<MessageLetter>();

        try {
            JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
            result = jsonObjectRootRoot.getString("result");
            firstPage = jsonObjectRootRoot.getInt("firstPage");
            prevPage = jsonObjectRootRoot.getInt("prevPage");
            nextPage = jsonObjectRootRoot.getInt("nextPage");
            lastPage = jsonObjectRootRoot.getInt("lastPage");
            if (result.equals("601")) {
                data = jsonObjectRootRoot.getJSONArray("data");
                if (data != null && data.length() != 0) {
                    int size = data.length();
                    MessageLetter letter;
                    JSONObject jsonObject;
                    for (int i = 0; i < size; i++) {
                        letter = new MessageLetter();
                        jsonObject = ((JSONObject) data.opt(i));
                        letter.friendid = jsonObject.getString("friendid");
                        letter.pmnum = jsonObject.getInt("pmnum");
                        letter.lastmessage = TextAttr.decode(jsonObject
                                .getString("lastmessage"));
                        letter.name = jsonObject.getString("name");
                        letter.dateline = jsonObject.getString("dateline");
                        letter.plid = jsonObject.getString("plid");
                        letter.isnew = jsonObject.getString("isnew");
                        list.add(letter);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return true;
    }
}
