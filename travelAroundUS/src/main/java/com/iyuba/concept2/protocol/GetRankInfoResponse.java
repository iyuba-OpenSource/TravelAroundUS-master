package com.iyuba.concept2.protocol;

import com.iyuba.concept2.sqlite.mode.RankUser;
import com.iyuba.concept2.util.GsonUtils;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.iyuba.headnewslib.util.GsonUtils;

/**
 * Created by Administrator on 2017/1/3.
 */

public class GetRankInfoResponse extends BaseJSONResponse {
    public String uid = "";
    public String myName = "";
    public String myImgSrc = "";
    public String myScores = "";
    public String myCount = "";
    public String myRanking = "";
    public String result = "";
    public String message = "";
    public List<RankUser> rankUsers = new ArrayList<RankUser>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {

        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            message = jsonRoot.getString("message");
            result = jsonRoot.getString("result");

            if (message.equals("Success")) {
                uid = jsonRoot.getString("myid");
                myImgSrc = jsonRoot.getString("myimgSrc");
                myScores = jsonRoot.getString("myscores");
                myCount = jsonRoot.getString("mycount");
                myRanking = jsonRoot.getString("myranking");
                rankUsers = GsonUtils.toObjectList(jsonRoot.getString("data"), RankUser.class);
//                for (Iterator itr = comments.iterator(); itr.hasNext(); ) {
//                    Comment ct = (Comment) itr.next();
//                    ct.produceBackList();
//                }
               try {
                   if(uid!=null&&!"".equals(uid)&&Integer.parseInt(uid)<50000000)
                   myName = jsonRoot.getString("myname");
               }catch (Exception e){
                   e.printStackTrace();
               }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
