package com.iyuba.concept2.sqlite.mode;

import java.io.Serializable;

/**
 * Created by ivotsm on 2017/3/2.
 */

public class OrderResult implements Serializable {
    public String flg = "";
    public String uid = "";
    public String amount = "";
    public String product = "";
    public String descs = "";
    public String orderId = "";
    public String appId = "";
    public String CreateTime = "";
    public String sendflg = "";
    public String demo = "";
    public String[] bookIds;

    public String getFlg() {
        return flg;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getSendflg() {
        return sendflg;
    }

    public void setSendflg(String sendflg) {
        this.sendflg = sendflg;
    }

    public String getDemo() {
        return demo;
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }

    @Override
    public String toString() {
        return "OrderResult [flg=" + flg + ", uid=" + uid + ", amount=" + amount + ", product="
                + product + ", descs=" + descs + ", orderId=" + orderId + ", appId=" + appId + ", CreateTime=" + CreateTime + ", sendflg=" + sendflg + ", demo=" + demo + "]";
    }
}
