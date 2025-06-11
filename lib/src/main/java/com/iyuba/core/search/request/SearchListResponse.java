package com.iyuba.core.search.request;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.search.bean.SearchInfoBean;
import com.iyuba.module.toolbox.GsonUtils;

import org.json.JSONObject;

/**
 * Created by zh on 2018/12/17.
 * 搜索请求 没有message，data
 */

public class SearchListResponse extends BaseJSONResponse {
    public SearchInfoBean searchInfoBean;
    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        searchInfoBean = GsonUtils.toObject(bodyElement, SearchInfoBean.class);
        return true;
    }
}
