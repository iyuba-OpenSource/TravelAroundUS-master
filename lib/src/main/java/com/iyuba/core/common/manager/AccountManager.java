//package com.iyuba.core.common.manager;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.LoginRequest;
//import com.iyuba.core.common.protocol.base.LoginResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.sqlite.mode.UserInfo;
//import com.iyuba.core.common.sqlite.op.UserInfoOp;
//import com.iyuba.core.common.util.AdTimeUtils;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.lib.R;
//import com.iyuba.module.user.IyuUserManager;
//import com.iyuba.module.user.User;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import personal.iyuba.personalhomelibrary.PersonalHome;
//
///**
// * 用户管理 用于用户信息的保存及权限判断
// *
// * @author chentong
// * @version 1.1 更改内容 引入userinfo数据结构统一管理用户信息
// */
//public class AccountManager {
//    private static AccountManager instance;
//    private static Context mContext;
//
//    public static final int LOGIN_STATUS_UNLOGIN = 0;
//    public static final int LOGIN_STATUS_LOGIN = 1;
//    public int loginStatus = LOGIN_STATUS_UNLOGIN; // 用户登录状态,默认为未登录状态
//    public UserInfo userInfo = new UserInfo();
//    //这里不要为空了，有的地方会进行强制转换，出现错误
////    public String userId = ""; // 用户ID
//    public String userId = "0"; // 用户ID
//    public String userName = ""; // 用户姓名
//    public String userPwd; // 用户密码
//    public String isteacher = "0";
//    public boolean islinshi = false;
//    private boolean loginSuccess = false;
//    public final static String USERNAME = "userName";
//    public final static String USERID = "userId";
//    public final static String USERPASSWORD = "userPassword";
//    public final static String IYUBAAMOUNT = "currUserAmount";
//    public final static String VALIDITY = "validity";
//    public final static String ISVIP = "isVip";
//
//    public String vipTime;
//
//    private String MONEY = "";
//
//    private AccountManager() {
//    }
//
//    public static synchronized AccountManager Instance(Context context) {
//        mContext = context;
//        if (instance == null) {
//            instance = new AccountManager();
//        }
//        return instance;
//    }
//
//    public static synchronized AccountManager getInstance() {
//
//        if (instance == null) {
//            instance = new AccountManager();
//        }
//        return instance;
//    }
//
//    public boolean isLogin() {
//        if (TextUtils.isEmpty(userId) || "0".equals(userId)) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    /**
//     * 检查当前用户是否登录
//     *
//     * @return
//     */
//    public boolean checkUserLogin() {
//        return loginStatus != LOGIN_STATUS_UNLOGIN;
//    }
//
//    public boolean checkUserRealLogin() {
//        return loginStatus != LOGIN_STATUS_UNLOGIN && !islinshi;
//    }
//
//    public int getVipStatus() {
//        int vip = 0;
//        vip = ConfigManager.Instance().loadInt("isvip");
//
//        if (userInfo != null) {
//            userInfo.vipStatus = String.valueOf(vip);
//        }
//        return vip;
//    }
//
//    public boolean isVip(){
//        int vipStatus = getVipStatus();
//        if (vipStatus>0){
//            return true;
//        }
//        return false;
//    }
//
//    public String getVipStringStatus() {
//        String vip = "0";
//        //你这里写的明显不对的，app内部全都是传的int类型，怎么这里用string获取呢，明显错误啊
////        vip = ConfigManager.Instance().loadString("isvip");
//        vip = String.valueOf(ConfigManager.Instance().loadInt("isvip"));
//
//        if (userInfo != null) {
//            userInfo.vipStatus = vip;
//        }
//        return vip;
//    }
//
//
////    public void setLoginState(int state) {
////        loginStatus = state;
////        userId = ConfigManager.Instance().loadString("userId");
////        String[] nameAndPwd = getUserNameAndPwd();
////        userName = nameAndPwd[0];
////        userPwd = nameAndPwd[1];
////        userInfo = new UserInfoOp(mContext).selectData(userId);
////        if (userInfo == null) {
////            userInfo = new UserInfo();
////        }
////        userInfo.username = userName;
////        userInfo.uid = userId;
////        ConfigManager.Instance().putInt("isvip",
////                Integer.parseInt(userInfo.vipStatus));
////    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public void setUserPwd(String userPwd) {
//        this.userPwd = userPwd;
//    }
//
//    /**
//     * 用户登录
//     *
//     * @param userName
//     * @param userPwd
//     * @return
//     */
//    public boolean login(final String userName, final String userPwd,
//                         final OperateCallBack rc) {
//        String latitude = "0";
//        String longitude = "0";
//
//        ExeProtocol.exe(new LoginRequest(userName, userPwd,
//                latitude, longitude), new ProtocolResponse() {
//
//            @Override
//            public void finish(BaseHttpResponse bhr) {
//                LoginResponse rr = (LoginResponse) bhr;
//                if (rr.result.equals("101")) {
//                    ConfigManager.Instance().putBoolean("islinshi", false);
//
//                    @SuppressLint("SimpleDateFormat")
//                    final SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    final Date date = new Date(Long.parseLong(rr.validity) * 1000l);
//                    String validity = sdfDateFormat.format(date);
//                    ConfigManager.Instance().putString("validity", validity);
//                    ConfigManager.Instance().putString("iyubi", rr.amount);
//                    vipTime = validity;
//                    islinshi = false;
//                    // 登录成功
//                    setUserName(rr.username);
//                    setUserPwd(userPwd);
//                    Refresh(rr);
//                    if (rc != null) {
//                        rc.success(rr.username);
//                    }
//
//                    AccountManager manager = AccountManager.Instance(mContext);
//                    PersonalHome.setSaveUserinfo(manager.getUserId(),manager.userName,manager.getVipStringStatus());
//                } else {
//                    // 登录失败
//                    handler.sendEmptyMessage(1);
//                    loginSuccess = false;
//                    if (rc != null) {
//                        rc.fail(null);
//                    }
//                }
//            }
//
//            @Override
//            public void error() {
//                handler.sendEmptyMessage(2);
//                loginSuccess = false;
//                if (rc != null) {
//                    rc.fail(null);
//                }
//            }
//        });
//        return loginSuccess;
//    }
//
//    /**
//     * 用户登出
//     *
//     * @return
//     */
//    public boolean loginOut() {
//        new UserInfoOp(mContext).delete(userId);
//        loginStatus = LOGIN_STATUS_UNLOGIN;
//        userId = "0"; // 用户ID
//        userName = null; // 用户姓名
//        userPwd = null; // 用户密码
//        userInfo = null;
//        SettingConfig.Instance().setAutoLogin(false);
//        saveUserNameAndPwd("", "");
//        ConfigManager.Instance().putInt("isvip", 0);
//        ConfigManager.Instance().putInt("isteacher", 0);
//
//        ConfigManager.Instance().putString("validity", "");
//        ConfigManager.Instance().putString("iyubi", "0");
//        vipTime = "";
//        return true;
//    }
//
//
//    /**
//     * 保存账户密码
//     *
//     * @param userName
//     * @param userPwd
//     */
//    public void saveUserNameAndPwd(String userName, String userPwd) {
//        ConfigManager.Instance().putString("userName", userName);
//        ConfigManager.Instance().putString("userPwd", userPwd);
//    }
//
//    /**
//     * 获取用户名及密码
//     *
//     * @return string[2] [0]=userName,[1]=userPwd
//     */
//    public String[] getUserNameAndPwd() {
//        String[] nameAndPwd = new String[]{
//                ConfigManager.Instance().loadString("userName"),
//                ConfigManager.Instance().loadString("userPwd")};
//        return nameAndPwd;
//    }
//
//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    break;
//                case 1: // 弹出错误信息
//                    CustomToast.showToast(mContext, R.string.login_fail);
//                    break;
//                case 2:
//                    CustomToast.showToast(mContext, R.string.login_faild);
//                    break;
//                case 3:
//                    CustomToast.showToast(mContext, R.string.person_vip_limit);
//                    break;
//            }
//        }
//    };
//
//    /*
//     * 处理userinfo数据写入
//     */
//    public void Refresh(LoginResponse rr) {
//        userId = rr.uid; // 成功返回用户ID
//        ConfigManager.Instance().putString("userId", userId);
//        ConfigManager.Instance().putString("isteacher", rr.isteacher);
//        userInfo = new UserInfoOp(mContext).selectData(userId);
//        if (userInfo == null) {
//            userInfo = new UserInfo();
//        }
//        userInfo.uid = userId;
//        userInfo.username = rr.username;
//
//        setMONEY(rr.money);
//        Log.e("钱包", getMONEY() + "==");
//        userInfo.iyubi = rr.amount;
//        userInfo.vipStatus = rr.vip;
//        userInfo.isteacher = rr.isteacher;
//        isteacher = rr.isteacher;
//        long time = Long.parseLong(rr.validity);
//        if (time < 0) {
//            userInfo.deadline = "终身VIP";
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
//                    Locale.CHINA);
//            try {
//                long allLife = sdf.parse("2099-01-01").getTime() / 1000;
//                if (time > allLife) {
//                    userInfo.deadline = "终身VIP";
//                } else {
//                    userInfo.deadline = sdf.format(new Date(time * 1000));
//                }
//            } catch (ParseException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        loginSuccess = true;
//        loginStatus = LOGIN_STATUS_LOGIN;
//        if (userInfo.vipStatus != null && !userInfo.vipStatus.equals("0")) {
//            SettingConfig.Instance().setHighSpeed(true);
//        } else {
//            SettingConfig.Instance().setHighSpeed(false);
//        }
//        ConfigManager.Instance().putInt("isvip",
//                Integer.parseInt(userInfo.vipStatus));
//        new UserInfoOp(mContext).saveData(userInfo);
//    }
//
//    public String getMONEY() {
//        return MONEY;
//    }
//
//    public void setMONEY(String MONEY) {
//        this.MONEY = MONEY;
//    }
//
//
//    public int getUserId() {
//        if (userId != null && !userId.equals("")) {
//            return Integer.parseInt(userId);
//        }
//        return 0;
//    }
//
//    /**********************辅助功能*********************/
//    //填充模块的用户信息
//    public void setModelUserInfo(){
//        if (checkUserLogin()){
//            User user = new User();
//            user.vipStatus = getVipStringStatus();
//            if (AdTimeUtils.isTime()){
//                user.vipStatus = "1";
//            }
//            user.name = userName;
//            user.uid = getUserId();
//            IyuUserManager.getInstance().setCurrentUser(user);
//        }
//
//        if (getUserId() == 0){
//            IyuUserManager.getInstance().logout();
//        }
//    }
//}
