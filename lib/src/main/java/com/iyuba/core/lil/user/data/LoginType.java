package com.iyuba.core.lil.user.data;

/**
 * @title: 登录类型
 * @date: 2023/8/25 09:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class LoginType {

    //微信登录
    public static final String loginByWXSmall = "loginByWXSmall";
    //秒验登录
    public static final String loginByVerify = "loginByVerify";
    //账号登录
    public static final String loginByAccount = "loginByAccount";

    private static LoginType instance;
    public static LoginType getInstance(){
        if (instance==null){
            synchronized (LoginType.class){
                if (instance==null){
                    instance = new LoginType();
                }
            }
        }
        return instance;
    }

    /****************参数*******************/
    private String curLoginType = loginByAccount;

    public void setCurLoginType(String curLoginType){
        this.curLoginType = curLoginType;
    }

    public String getCurLoginType() {
        return curLoginType;
    }
}
