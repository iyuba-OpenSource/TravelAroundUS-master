package com.iyuba.concept2.protocol;

import com.iyuba.concept2.sqlite.mode.Comment;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivotsm on 2017/3/13.
 */

public class UserCommentResponse extends BaseJSONResponse {
    public String result = "";
    public String message = "";
    public List<Comment> comments = new ArrayList<>();
    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        try {
            JSONObject jsonObjectRoot = new JSONObject(bodyElement);
            result= jsonObjectRoot.getString("result");
            message= jsonObjectRoot.getString("message");
            JSONArray jsonArrayData = jsonObjectRoot.getJSONArray("data");
            for (int i= 0;i<jsonArrayData.length();i++){
                Comment comment = new Comment();
                JSONObject jsonElement = jsonArrayData.getJSONObject(i);
                comment.id = jsonElement.getString("id");
                comment.shuoshuo = jsonElement.getString("ShuoShuo");
                comment.createdate = jsonElement.getString("CreateDate");
                comment.agreeCount = jsonElement.getInt("agreeCount");
                comment.shuoshuoType=jsonElement.getInt("shuoshuotype");
                comment.score =jsonElement.getString("score");
                comment.indexId =jsonElement.getString("idIndex");
                comments.add(comment);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return true;
    }
}
