//package com.iyuba.core.common.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Html;
//import android.text.method.LinkMovementMethod;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.gson.Gson;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.entity.AutoLoginLocalData;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.protocol.base.LoginResponse;
//import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
//import com.iyuba.core.common.retrofitapi.result.VerifyLoginResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.util.AdTimeUtils;
//import com.iyuba.core.common.util.ChangeVideoEvnet;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.manager.TempDataManager;
//import com.iyuba.lib.R;
//import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
//import com.iyuba.module.user.IyuUserManager;
//import com.mob.secverify.OAuthPageEventCallback;
//import com.mob.secverify.SecVerify;
//import com.mob.secverify.VerifyCallback;
//import com.mob.secverify.common.exception.VerifyException;
//import com.mob.secverify.datatype.VerifyResult;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import okhttp3.FormBody;
//import okhttp3.RequestBody;
//import personal.iyuba.personalhomelibrary.PersonalHome;
//import rx.Observer;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//
///**
// * 登录界面
// *
// * @author chentong
// * @version 1.2
// * 修改内容 API更新; userinfo引入; VIP更新方式变化
// */
//public class Login extends BasisActivity {
//    private Button backBtn;
//    private Button registBtn, loginBtn;
//    private String userName, userPwd;
//    private EditText userNameET, userPwdET;
//    private CheckBox autoLogin;
//    private CustomDialog cd;
//    private Context mContext;
//    private TextView findPassword;
//
//    private LinearLayout loginLayout;
//
//    public static void start(Context context){
//        Intent intent = new Intent();
//        intent.setClass(context,Login.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        secVerify();
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.login_layout);
//
//        //进入登录界面先进入秒验
//        CrashApplication.getInstance().addActivity(this);
//        mContext = this;
//        cd = WaittingDialog.showDialog(mContext);
//        userNameET = (EditText) findViewById(R.id.editText_userId);
//        userPwdET = (EditText) findViewById(R.id.editText_userPwd);
//        if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//            String[] nameAndPwd = AccountManager.Instance(mContext)
//                    .getUserNameAndPwd();
//            userName = nameAndPwd[0];
//            userPwd = nameAndPwd[1];
//            handler.sendEmptyMessage(0);
//        }
//
//        autoLogin = (CheckBox) findViewById(R.id.checkBox_autoLogin);
//        autoLogin.setChecked(SettingConfig.Instance().isAutoLogin());
//        autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                // TODO Auto-generated method stub
//                SettingConfig.Instance().setAutoLogin(isChecked);
//            }
//        });
//
//        //这里是自动登录按钮的点击，初始化的时候不需要默认
//        //360应用市场因为默认显示被驳回
//        if (!autoLogin.isChecked()) {
//            autoLogin.setChecked(true);
//            SettingConfig.Instance().setAutoLogin(true);
//        }
//
//        backBtn = (Button) findViewById(R.id.button_back);
//        backBtn.setOnClickListener(v -> {
//            Log.e("TAG", "onClick: 返回");
//            finish();
//        });
//
//        loginBtn = (Button) findViewById(R.id.button_login);
//        loginBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 登录操作
//                loginM();
//            }
//        });
//        registBtn = (Button) findViewById(R.id.button_regist);
//        registBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                //修改为手机注册优先
//                Intent intent = new Intent();
////				intent.setClass(mContext, RegistActivity.class);
//                intent.setClass(mContext, RegistByPhoneActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        findPassword = (TextView) findViewById(R.id.find_password);
//        findPassword
//                .setText(Html
//                        .fromHtml("<a href=\"http://m." + Constant.IYBHttpHead() + "/m_login/inputPhonefp.jsp?\">"
//                                + getResources().getString(
//                                R.string.login_find_password) + "</a>"));
//        findPassword.setMovementMethod(LinkMovementMethod.getInstance());
//
//        loginLayout = findViewById(R.id.loginLayout);
//    }
//
//    /**
//     * 秒验
//     */
//    public void secVerify(){
//        if (SecVerify.isVerifySupport()&& TempDataManager.getInstance().getMobVerify()){
//            startLoading("正在获取登录信息~");
//            SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
//                @Override
//                public void initCallback(OAuthPageEventResultCallback callback) {
//                    callback.pageOpenCallback(new PageOpenedCallback() {
//                        @Override
//                        public void handle() {
//                            closeLoading();
//                        }
//                    });
//                }
//            });
//            SecVerify.verify(new VerifyCallback() {
//                @Override
//                public void onOtherLogin() {
//                    // 用户点击“其他登录方式”，处理自己的逻辑
//                    closeLoading();
//                    updateLoginShow(true);
//
//                }
//                @Override
//                public void onUserCanceled() {
//                    // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
//                    closeLoading();
//                    Login.this.finish();
//                }
//
//                @Override
//                public void onComplete(VerifyResult verifyResult) {
//                    // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
//                    if (verifyResult!=null){
//                        // opToken
//                        String opToken = verifyResult.getOpToken();
//                        // token
//                        String token = verifyResult.getToken();
//                        // 运营商类型，[CMCC:中国移动，CUCC：中国联通，CTCC：中国电信]
//                        String operator = verifyResult.getOperator();
//                        Log.e("TAG", "秒验 onComplete: "+opToken+";"+token+";"+ operator);
//                        secVerifyLogin(verifyResult);
//                    }else {
//                        closeLoading();
//                        updateLoginShow(true);
//                    }
//                }
//
//                @Override
//                public void onFailure(VerifyException e) {
//                    //TODO处理失败的结果
//                    closeLoading();
//                    updateLoginShow(true);
//                }
//            });
//        }else {
//            updateLoginShow(true);
//        }
//    }
//
//    /**
//     * 验证
//     */
//    public boolean verification() {
//        userName = userNameET.getText().toString();
//        userPwd = userPwdET.getText().toString();
//        if (userName.length() < 3) {
//            userNameET.setError(getResources().getString(
//                    R.string.login_check_effective_user_id));
//            return false;
//        }
//
//        if (userPwd.length() == 0) {
//            userPwdET.setError(getResources().getString(
//                    R.string.login_check_user_pwd_null));
//            return false;
//        }
//        if (!checkUserPwd(userPwd)) {
//            userPwdET.setError(getResources().getString(
//                    R.string.login_check_user_pwd_constraint));
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 匹配密码
//     *
//     * @param userPwd
//     * @return
//     */
//    public boolean checkUserPwd(String userPwd) {
//        if (userPwd.length() < 6 || userPwd.length() > 20)
//            return false;
//        return true;
//    }
//
//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    userNameET.setText(userName);
//                    userPwdET.setText(userPwd);
//                    break;
//                case 1:
//                    cd.show();
//                    break;
//                case 2:
//                    cd.dismiss();
//                    break;
//                case 3:
//                    EventBus.getDefault().post(new ChangeVideoEvnet(true));//event发布
//                    finish();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void finish() {
//        super.finish();
//        if (AccountManager.Instance(mContext).userName == null) {
//            SettingConfig.Instance().setAutoLogin(false);
//        }
//    }
//
//    private void loginM() {
//        if (verification()) {
//            handler.sendEmptyMessage(1);
//            AccountManager.Instance(mContext).login(userName, userPwd,
//                    new OperateCallBack() {
//                        @Override
//                        public void success(String result) {
//                            if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//                                AccountManager.Instance(mContext).saveUserNameAndPwd(result, userPwd);
//                            } else {
//                                AccountManager.Instance(mContext).saveUserNameAndPwd("", "");
//                            }
//
//                            handler.sendEmptyMessage(2);
//                            handler.sendEmptyMessage(3);
//                            initCommonData();
//                        }
//
//                        @Override
//                        public void fail(String message) {
//                            handler.sendEmptyMessage(2);
//                        }
//                    });
//        }
//    }
//
//    public static void initCommonData() {
//        //这里根据登录状态进行存储数据
//        if (AccountManager.getInstance().checkUserLogin()) {
//            com.iyuba.module.user.User user = new com.iyuba.module.user.User();
//            AccountManager managerLib = AccountManager.getInstance();
//            user.vipStatus = String.valueOf(managerLib.getVipStatus());
//            user.uid = managerLib.getUserId();
//            user.name = managerLib.userName;
//            if (AdTimeUtils.isTime()) {
//                user.vipStatus = "1";
//            }
//            IyuUserManager.getInstance().setCurrentUser(user);
//        }
//    }
//
//    private void secVerifyLogin(VerifyResult verifyResult) {
//        RequestBody body = new FormBody.Builder()
//                .add("appkey", Constant.SMSAPPID2)
//                .add("token", URLEncoder.encode(verifyResult.getToken()))
//                .add("opToken", verifyResult.getOpToken())
//                .add("operator", verifyResult.getOperator())
//                .add("appId", Constant.APPID)
//                .build();
//        ApiRequestFactory.getApiApi().verifyLogin(body)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<VerifyLoginResponse>() {
//                    @Override
//                    public void onCompleted() {
//                        closeLoading();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtil.showToast(Login.this,"自动登录失败，请使用账号密码登录");
//                        updateLoginShow(true);
//                    }
//
//                    @Override
//                    public void onNext(VerifyLoginResponse verifyLoginResponse) {
//                        initLoginInfo(verifyLoginResponse.getUserinfo());
//                    }
//                });
//    }
//
//    private void initLoginInfo(VerifyLoginResponse.Userinfo info){
//        AutoLoginLocalData autoLoginLocalData = new AutoLoginLocalData();
//
//        ConfigManager.Instance().putBoolean("islinshi", false);
//
//        @SuppressLint("SimpleDateFormat")
//        final SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        final Date date = new Date(info.getExpireTime());
//        String validity = sdfDateFormat.format(date);
//        ConfigManager.Instance().putString("validity", validity);
//        ConfigManager.Instance().putString("iyubi", info.getAmount());
//        autoLoginLocalData.setVipTime(validity);
//
//        AccountManager.getInstance().vipTime = validity;
//        AccountManager.getInstance().islinshi = false;
//        // 登录成功
//        AccountManager.getInstance().setUserName(info.getUsername());
//        AccountManager.getInstance().setUserPwd(userPwd);
//        autoLoginLocalData.setUserName(info.getUsername());
//        LoginResponse rr = new LoginResponse();
//        rr.amount = info.getAmount();
//        rr.username = info.getUsername();
//        rr.result = info.getResult();
//        rr.validity = String.valueOf(info.getExpireTime()/1000);
//        rr.imgsrc = info.getImgSrc();
//        rr.isteacher = info.getIsteacher();
//        rr.money = info.getMoney();
//        rr.uid = String.valueOf(info.getUid());
//        rr.vip = info.getVipStatus();
//
//        autoLoginLocalData.setAmount(info.getAmount());
//        autoLoginLocalData.setResult(info.getResult());
//        autoLoginLocalData.setValidity(String.valueOf(info.getExpireTime()/1000));
//        autoLoginLocalData.setImgSrc(info.getImgSrc());
//        autoLoginLocalData.setIsTeacher(info.getIsteacher());
//        autoLoginLocalData.setMoney(info.getMoney());
//        autoLoginLocalData.setUid(info.getUid()+"");
//        autoLoginLocalData.setValidity(info.getVipStatus());
//        autoLoginLocalData.setVipStatus(info.getVipStatus());
//
//        AccountManager.getInstance().Refresh(rr);
//
//        AccountManager manager = AccountManager.Instance(mContext);
//        PersonalHome.setSaveUserinfo(manager.getUserId(),manager.userName,manager.getVipStringStatus());
//
//        if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
//            AccountManager.Instance(mContext).saveUserNameAndPwd(info.getUsername(), "-1");
//        } else {
//            AccountManager.Instance(mContext).saveUserNameAndPwd("", "");
//        }
//        initCommonData();
//        /**
//         * 存储本地自动登录信息
//         */
//        Gson gson = new Gson();
//        ConfigManager.Instance().putString("auto_login_data",gson.toJson(autoLoginLocalData));
//
//        finish();
//    }
//
//    //切换登录界面是否显示
//    private void updateLoginShow(boolean isShow){
//        if (isShow){
//            loginLayout.setVisibility(View.VISIBLE);
//        }else {
//            loginLayout.setVisibility(View.GONE);
//        }
//    }
//
//    //开启加载弹窗
//    private LoadingDialog loadingDialog;
//    private void startLoading(String msg){
//        if (loadingDialog==null){
//            loadingDialog = new LoadingDialog(this);
//        }
//
//        if (loadingDialog.isShowing()){
//            return;
//        }
//
//        loadingDialog.setMessage(msg);
//        loadingDialog.show();
//    }
//
//    //关闭加载弹窗
//    private void closeLoading(){
//        if (loadingDialog!=null&&loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }
//    }
//}
//
