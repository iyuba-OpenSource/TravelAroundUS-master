package com.iyuba.concept2.protocol;

import com.iyuba.concept2.util.MD5;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/1/3.
 */

public class GetRankInfoRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public GetRankInfoRequest(String uid, String topic, String topicid, String start, String total) {
//        String sign = URLEncoder.encode(uid + type + start + total + date);
        String sign = MD5.getMD5ofStr(uid + topic + topicid + start + total + date);
        setAbsoluteURI("http://daxue."+Constant.IYBHttpHead()+"/ecollege/getTopicRanking.jsp?topic=" +
                topic +
                "&topicid=" +
                topicid +
                "&uid=" +
                uid +
                "&type=" +
                "D" +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign);
        LogUtils.e("GetRankInfoRequest "+"http://daxue."+Constant.IYBHttpHead()+"/ecollege/getTopicRanking.jsp?topic=" +
                topic +
                "&topicid=" +
                topicid +
                "&uid=" +
                uid +
                "&type=" +
                "D" +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetRankInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
