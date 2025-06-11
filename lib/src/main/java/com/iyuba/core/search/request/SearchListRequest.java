package com.iyuba.core.search.request;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zh on 2018/12/17.
 * 搜索列表的请求数据
 */

public class SearchListRequest extends BaseJSONRequest {
    //flg 0为全部 1为Voa文章 2为句子
    public SearchListRequest(String searchKey, int page,int pageNumber,int flg,int userid) {
        int parentID = 0;
        setAbsoluteURI("http://apps."+Constant.IYBHttpHead()+"/iyuba/searchApiNew.jsp?format=json&key=" +
                searchKey +
                "&pages=" +
                page +
                "&pageNum=" +
                pageNumber +
                "&parentID=" +
                parentID +
                "&type=voa" +
                "&flg=" +
                flg+
                "&userid="+
                userid);
        Log.e("SearchListRequest:？？", "http://apps."+Constant.IYBHttpHead()+"/iyuba/searchApiNew.jsp?format=json&key=" +
                searchKey +
                "&pages=" +
                page +
                "&pageNum=" +
                pageNumber +
                "&parentID=" +
                parentID +
                "&type=voa" +
                "&flg=" + flg);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new SearchListResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
