package com.iyuba.core.common.entity;

/**
 * 因为之前自动登录是用账号密码登录，加上一键登录后无需账号密码，因此如果
 * 用手机号一键登录则无法 使用之前的自动登录逻辑。
 * 于是这里忽略之前的自动登录逻辑，加一个实体类本地保存所有的登录信息如果使用
 * 一键登录的情况下则用此自动登录
 * */
public class AutoLoginLocalData {
    private String userName;
    private String uid;
    private String amount;//应该是爱语币
    private String result;
    private String validity;
    private String imgSrc;
    private String isTeacher;
    private String money;
    private String vipStatus;
    private String vipTime;

    public AutoLoginLocalData() {
    }

    public String getVipTime() {
        return vipTime;
    }

    public void setVipTime(String vipTime) {
        this.vipTime = vipTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(String isTeacher) {
        this.isTeacher = isTeacher;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    @Override
    public String toString() {
        return "AutoLoginLocalData{" +
                "userName='" + userName + '\'' +
                ", uid='" + uid + '\'' +
                ", amount='" + amount + '\'' +
                ", result='" + result + '\'' +
                ", validity='" + validity + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", isTeacher='" + isTeacher + '\'' +
                ", money='" + money + '\'' +
                ", vipStatus='" + vipStatus + '\'' +
                ", vipTime='" + vipTime + '\'' +
                '}';
    }
}
