package com.iyuba.concept2.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * 获取测试的内容
 * Created by Liuzhenli on 2016/8/30.
 */
public class AbilityTestQuestionRequest extends BaseJSONRequest {


    /**
     * @param lesson   测试课程  NewConcept1 NewConcept2 NewConcept3 NewConcept4  IELTS   Toefl  cet4 cet6
     * @param category 测试的内容  W--Word--单词  G--语法 L--听力 S--口语   R--阅读   X--写作
     */
    public AbilityTestQuestionRequest(String lesson, String category) {
        String sign;
        MD5 md5 = new MD5();
        //sign加密方式: md5(lesson+category+"yyyy-MM-dd" )
        sign = md5.getMD5ofStr(lesson+category+getCurTime());
        String url = "http://class."+Constant.IYBHttpHead()+"/getClass.iyuba?&protocol=20000&lesson="+lesson+"&category="+category+"&sign="+sign+"&format=json";
        LogUtils.e("试题请求地址   "+url);
        setAbsoluteURI(url);
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AbilityTestQuestionResponse();
    }
    private String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        LogUtils.e("AbiltiyTestRequest", df.format(System.currentTimeMillis()));
        return df.format(System.currentTimeMillis());
    }
}
