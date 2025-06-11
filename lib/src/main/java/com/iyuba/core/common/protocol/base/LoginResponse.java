package com.iyuba.core.common.protocol.base;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class LoginResponse extends BaseXMLResponse {
    public String result, uid, username, imgsrc, vip, validity, amount, isteacher, money;

    @Override
    protected boolean extractBody(kXMLElement headerEleemnt,
                                  kXMLElement bodyElement) {
        // TODO Auto-generated method stub
        try {
            result = Utility.getSubTagContent(bodyElement, "result");
            uid = Utility.getSubTagContent(bodyElement, "uid");
            username = Utility.getSubTagContent(bodyElement, "username");
            imgsrc = Utility.getSubTagContent(bodyElement, "imgSrc");
            vip = Utility.getSubTagContent(bodyElement, "vipStatus");
            money = Utility.getSubTagContent(bodyElement, "money");
            validity = Utility.getSubTagContent(bodyElement, "expireTime");
            amount = Utility.getSubTagContent(bodyElement, "Amount");
            isteacher = Utility.getSubTagContent(bodyElement, "isteacher");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
