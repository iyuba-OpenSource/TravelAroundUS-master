package com.iyuba.core.lil.remote.bean;

/**
 * @title:
 * @date: 2023/12/16 11:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Alipay_info {

    /**
     * alipayTradeStr : alipay_sdk=alipay-sdk-java-4.10.184.ALL&app_id=2021002111635676&biz_content=%7B%22body%22%3A%221%E4%B8%AA%E6%9C%88%E6%9C%AC%E5%BA%94%E7%94%A8VIP%22%2C%22out_trade_no%22%3A%2220231216105523_15105425_260%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%221%E4%B8%AA%E6%9C%88%E6%9C%AC%E5%BA%94%E7%94%A8VIP%E8%B4%AD%E4%B9%B0%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%2230.0%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fvip.iyuba.cn%2FnotifyAliNewUrl.jsp&sign=ja4ac2BjDF3sbydKKbQxvXYwFlmBX6xW%2BvX22HScsrY8n%2FPHzoIr88mlLtrDzYWE4U8zfk8cx6l5RVmT33mjBl52%2BHl8E0H19uR1EkwpGxWwRe1T5ulpid4gOx6afBuSpVb3nqJcH10HfuKG0289LAJSzwifdTZ9dbiKarKNRtpI0Ix%2FvFxflZoq6S4%2FmuWloIBnZiWHvB7UrxOZlwvY5eBeZiHM%2Fe51lC9k8lvVUqlavamOaEq72hOijgZ%2BNyuuMl9Kg9ERD%2FGIPXrtIql5aX6z8Lxy0382CNFYQ7o2wIQiXJKh2bKfDZTi9JVAi00UQuQr0ilQUeDEEyq1ZVz%2BqA%3D%3D&sign_type=RSA2&timestamp=2023-12-16+10%3A55%3A23&version=1.0
     * result : 200
     * message : Success
     */

    private String alipayTradeStr;
    private String result;
    private String message;

    public String getAlipayTradeStr() {
        return alipayTradeStr;
    }

    public void setAlipayTradeStr(String alipayTradeStr) {
        this.alipayTradeStr = alipayTradeStr;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
