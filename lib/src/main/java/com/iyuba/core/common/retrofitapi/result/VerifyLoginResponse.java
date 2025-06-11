package com.iyuba.core.common.retrofitapi.result;

public class VerifyLoginResponse {

    private String isLogin;
    private Res res;
    private Userinfo userinfo;

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public Res getRes() {
        return res;
    }

    public void setUserinfo(Userinfo userinfo) {
        this.userinfo = userinfo;
    }

    public Userinfo getUserinfo() {
        return userinfo;
    }

    public class Res {

        private boolean valid;
        private String phone;
        private int isValid;

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public boolean getValid() {
            return valid;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setIsValid(int isValid) {
            this.isValid = isValid;
        }

        public int getIsValid() {
            return isValid;
        }

    }

    public class Userinfo {

        private long uid;
        private long expireTime;
        private String result;
        private String Amount;
        private String vipStatus;
        private String nickname;
        private int credits;
        private String message;
        private String username;
        private String email;
        private int jiFen;
        private String imgSrc;
        private String money;
        private String mobile;
        private String isteacher;

        public void setUid(long uid) {
            this.uid = uid;
        }

        public long getUid() {
            return uid;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }

        public void setAmount(String Amount) {
            this.Amount = Amount;
        }

        public String getAmount() {
            return Amount;
        }

        public void setVipStatus(String vipStatus) {
            this.vipStatus = vipStatus;
        }

        public String getVipStatus() {
            return vipStatus;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public int getCredits() {
            return credits;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setJiFen(int jiFen) {
            this.jiFen = jiFen;
        }

        public int getJiFen() {
            return jiFen;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMoney() {
            return money;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMobile() {
            return mobile;
        }

        public void setIsteacher(String isteacher) {
            this.isteacher = isteacher;
        }

        public String getIsteacher() {
            return isteacher;
        }

    }

}
