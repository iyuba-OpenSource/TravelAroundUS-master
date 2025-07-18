package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.concept2.sqlite.mode.RankBean;
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

public class GetRankTestInfoResponse extends BaseJSONResponse {


    public String myImgSrc = "";
    public String myId = "";
    public String myRanking = "";
    public String myName = "";
    public String result = "";
    public String message = "";

    public String myTotalRight = "";//正确数
    public String myTotalTest = "";  //总题数


    public List<RankBean> rankBeans = new ArrayList<RankBean>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        Log.e("GetRank-测试排行榜", "====================" + bodyElement);
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            message = jsonRoot.getString("message");

            if (message.equals("Success")) {

                if (jsonRoot.has("myimgSrc"))
                    myImgSrc = jsonRoot.getString("myimgSrc");//---
                if (jsonRoot.has("myid")) //-----
                    myId = jsonRoot.getString("myid");
                if (jsonRoot.has("myranking"))
                    myRanking = jsonRoot.getString("myranking"); //---
                if (jsonRoot.has("result"))
                    result = jsonRoot.getString("result");//---
                if (jsonRoot.has("totalRight"))
                    myTotalRight = jsonRoot.getString("totalRight"); //总的正确数
                if (jsonRoot.has("totalTest"))
                    myTotalTest = jsonRoot.getString("totalTest");  //总测试题数
                if (jsonRoot.has("myname"))
                    myName = jsonRoot.getString("myname");

                rankBeans = GsonUtils.toObjectList(jsonRoot.getString("data"), RankBean.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
