package com.iyuba.concept2.protocol;

import com.iyuba.concept2.sqlite.mode.IntelTestQues;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class IntelTestQuesResponse extends BaseJSONResponse {

    public List<IntelTestQues> mList = new ArrayList<IntelTestQues>();
    public String result;
    public String total;
    public int totalTime;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonObjectRoot;
        try {
            jsonObjectRoot = new JSONObject(bodyElement);
            result = jsonObjectRoot.getString("result");
            total = jsonObjectRoot.getString("Total");
            totalTime = jsonObjectRoot.getInt("Time");
            JSONArray arr = new JSONArray(jsonObjectRoot.getString("TestList"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                IntelTestQues itq = new IntelTestQues();
                try {
                    itq.choiceD = temp.getString("Answer4");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.testId = temp.getString("TestId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.answer = temp.getString("Answer");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.choiceB = temp.getString("Answer2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.category = temp.getString("Category");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.choiceC = temp.getString("Answer3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.choiceA = temp.getString("Answer1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.question = temp.getString("Question");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.id = temp.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.testType = temp.getString("TestType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.tags = temp.getString("Tags");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.image = temp.getString("Pic");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itq.sound = temp.getString("Sounds");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    itq.attach = temp.getString("Attach");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mList.add(itq);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}
