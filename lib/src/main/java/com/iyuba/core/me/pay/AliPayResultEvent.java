package com.iyuba.core.me.pay;

/**
 * 阿里支付结果回调
 */
public interface AliPayResultEvent {

    interface Code {
        String SUCCESS = "9000";
        String IN_CONFIRMATION = "8000";
        String CANCELED = "6001";
        String NET_ERROR = "6002";
    }

    interface Message {
        String SUCCESS = "支付成功";
        String IN_CONFIRMATION = "支付结果确认中";
        String CANCELED = "您已取消支付";
        String NET_ERROR = "网络连接出错";
        String FAILURE = "支付失败";
    }
}
