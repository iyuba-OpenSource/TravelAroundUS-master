package com.iyuba.concept2.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/2.
 */
public class IntelTestQuesRequest extends BaseJSONRequest {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String sign ;

    public IntelTestQuesRequest(String lesson,String category) {
        sign = "NewConcept1" + category + df.format(new Date());
        String aaa = "http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?&protocol=20000&lesson=" +
                lesson +
                "&category=" +
                category +
                "&sign=" + MD5.getMD5ofStr(sign) + "&format=json";
        setAbsoluteURI(aaa);
        Log.e("aaarequest", aaa);
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new IntelTestQuesResponse();
    }
}
