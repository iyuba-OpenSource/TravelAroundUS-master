package com.iyuba.core.lil.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.RegistByPhoneActivity;
import com.iyuba.core.common.activity.RegistSubmitActivity;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.util.CommonUtils;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.manager.TempDataManager;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.remote.bean.Mob_verify;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.data.LoginType;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.lib.R;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 新的登录界面
 * @date: 2023/8/25 09:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewLoginActivity extends BaseStackActivity {
    //登录类型
    private static final String tag_loginType = "loginType";
    private String loginType = "loginType";
    //控件
    private View wxLoginView,accountLoginView;
    private EditText etUserName,etPassword;
    private TextView tvTitle,tvForgetPassword,tvSmallLogin,tvWxLogin,tvAccount,tvPrivacy;
    private Button btnBack,btnRegister,btnLogin;
    private CheckBox cbLoginPrivacy;
    private TextView tvLoginPrivacy;
    private RadioButton rbCheck;
    //加载弹窗
    private LoadingDialog loadingDialog;
    //微信小程序的token
    private String wxMiniToken = null;

    public static void start(Context context,String loginType){
        Intent intent = new Intent();
        intent.setClass(context,NewLoginActivity.class);
        intent.putExtra(tag_loginType,loginType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        initView();
        initData();
        initClick();

        switchType();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /************************初始化********************************/
    private void initView(){
        //切换类型界面
        wxLoginView = findViewById(R.id.loginType);
        accountLoginView = findViewById(R.id.accountLoginLayout);

        //账号登录
        etUserName = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        tvTitle = findViewById(R.id.tv_title);
        tvForgetPassword = findViewById(R.id.tv_forgetPassword);
        tvSmallLogin = findViewById(R.id.tv_smallLogin);
        cbLoginPrivacy = findViewById(R.id.cb_login_privacy);
        tvLoginPrivacy = findViewById(R.id.tv_login_privacy);
        btnBack = findViewById(R.id.btn_back);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        //登录类型选择
        tvWxLogin = findViewById(R.id.vx_login);
        tvAccount = findViewById(R.id.account_login);
        rbCheck = findViewById(R.id.agree_other);
        tvPrivacy = findViewById(R.id.agree_tv);

        //设置账号和密码
        String userName = UserInfoManager.getInstance().getLoginAccount();
        if (!TextUtils.isEmpty(userName)){
            etUserName.setText(userName);
        }
        String password = UserInfoManager.getInstance().getLoginPassword();
        if (!TextUtils.isEmpty(password)){
            etPassword.setText(password);
        }
    }

    private void initData(){
        loginType = getIntent().getStringExtra(tag_loginType);
        if (TextUtils.isEmpty(loginType)){
            loginType = LoginType.loginByAccount;
        }

        //隐私政策显示
        tvPrivacy.setText(setPrivacySpan());
        tvPrivacy.setMovementMethod(new LinkMovementMethod());

        //账号登录界面的隐私政策显示
        tvLoginPrivacy.setText(setPrivacySpan());
        tvLoginPrivacy.setMovementMethod(new LinkMovementMethod());
    }

    private void initClick(){
        btnBack.setOnClickListener(v->{
            finish();
        });
        btnRegister.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(this, RegistByPhoneActivity.class);
            startActivity(intent);
            finish();
        });
        btnLogin.setOnClickListener(v->{
            if (!cbLoginPrivacy.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                return;
            }

            if (verifyAccountAndPsd()){
                String userName = etUserName.getText().toString().trim();
                String userPwd = etPassword.getText().toString().trim();

                accountLogin(userName,userPwd);
            }
        });
        /*tvSmallLogin.setOnClickListener(v->{
            if (!cbPrivacy.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                return;
            }

            toWxLogin();
        });*/
        tvForgetPassword.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(this,Web.class);
            intent.putExtra("url","http://m." + Constant.IYBHttpHead() + "/m_login/inputPhonefp.jsp");
            intent.putExtra("title","重置密码");
            startActivity(intent);
        });
        /*tvWxLogin.setOnClickListener(v->{
            if (!rbCheck.isChecked()){
                ToastUtil.showToast(this,"请先阅读并同意隐私政策和用户协议");
                return;
            }

            toWxLogin();
        });*/
        tvAccount.setOnClickListener(v->{
            wxLoginView.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.VISIBLE);
        });
    }

    private SpannableStringBuilder setPrivacySpan(){
        String privacyStr = "隐私政策";
        String termStr = "用户协议";
        String showMsg = "我已阅读并同意"+privacyStr+"和"+termStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //隐私政策
        int privacyIndex = showMsg.indexOf(privacyStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(NewLoginActivity.this, Web.class);
                String url = CommonUtils.getPrivacyPolicyUrl(NewLoginActivity.this);
                intent.putExtra("url", url);
                intent.putExtra("title", privacyStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },privacyIndex,privacyIndex+privacyStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用户协议
        int termIndex = showMsg.indexOf(termStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(NewLoginActivity.this, Web.class);
                String url = CommonUtils.getUserAgreementUrl(NewLoginActivity.this);
                intent.putExtra("url", url);
                intent.putExtra("title", termStr);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        },termIndex,termIndex+termStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    /**************************样式显示*****************************/
    //切换样式
    private void switchType(){
        if (loginType.equals(LoginType.loginByWXSmall)){
            //微信登录
            /*wxLoginView.setVisibility(View.VISIBLE);
            accountLoginView.setVisibility(View.GONE);

            getWXSmallToken();*/
        }else if (loginType.equals(LoginType.loginByVerify)){
            //秒验登录
            wxLoginView.setVisibility(View.GONE);
            tvSmallLogin.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.GONE);

            showVerify();
        }else {
            //账号登录
            wxLoginView.setVisibility(View.GONE);
            tvSmallLogin.setVisibility(View.GONE);
            accountLoginView.setVisibility(View.VISIBLE);
        }
    }

    /******************************登录方式************************/
    /*************微信登录***********/
    //获取微信小程序的token
    /*private void getWXSmallToken(){
        startLoading("正在加载登录信息～");
        LoginPresenter.getWXSmallToken(new Observer<VXTokenResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(VXTokenResponse bean) {
                closeLoading();

                if (bean.getResult().equals("200")){
                    wxMiniToken = bean.getToken();

                    //这里判断微信是否已经安装
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(NewLoginActivity.this, Constant.getWxKey());
                    if (!wxapi.isWXAppInstalled()){
                        wxMiniToken = null;

                        loginType = LoginType.loginByAccount;
                        switchType();
                    }
                }else {
                    wxMiniToken = null;

                    loginType = LoginType.loginByAccount;
                    switchType();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeLoading();
                wxMiniToken = null;

                loginType = LoginType.loginByAccount;
                switchType();
            }

            @Override
            public void onComplete() {

            }
        });
    }*/

    //跳转到微信登录
    /*private void toWxLogin(){
        IWXAPI wxapi = WXAPIFactory.createWXAPI(NewLoginActivity.this,Constant.getWxKey());
        if (!wxapi.isWXAppInstalled()){
            ToastUtil.showToast(NewLoginActivity.this,"您还未安装微信客户端");
            return;
        }

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName="gh_a8c17ad593be";
        req.path="/subpackage/getphone/getphone?token="+wxMiniToken+"&appid="+Constant.APPID;
        req.miniprogramType=WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        wxapi.sendReq(req);

        //放在临时框架中，后面要用
        WxLoginSession.getInstance().setWxSmallToken(wxMiniToken);

        finish();
    }*/

    /*************秒验登录***********/
    //展示秒验功能
    private void showVerify(){
        if (SecVerify.isVerifySupport()&& TempDataManager.getInstance().getMobVerify()){
            startLoading("正在获取登录信息~");

            SecVerify.setDebugMode(false);
            SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
                @Override
                public void initCallback(OAuthPageEventResultCallback callback) {
                    callback.pageOpenCallback(new PageOpenedCallback() {
                        @Override
                        public void handle() {
                            closeLoading();

                            Log.d("界面显示测试", "pageOpenCallback");
                        }
                    });
                }
            });
            SecVerify.verify(new VerifyCallback() {
                @Override
                public void onOtherLogin() {
                    //点击其他登录方式
                    closeLoading();
                    loginType = LoginType.loginByAccount;
                    switchType();

                    Log.d("界面显示测试", "onOtherLogin");
                }

                @Override
                public void onUserCanceled() {
                    //用户取消
                    closeLoading();
                    SecVerify.finishOAuthPage();
                    NewLoginActivity.this.finish();

                    Log.d("界面显示测试", "onUserCanceled");
                }

                @Override
                public void onComplete(VerifyResult result) {
                    //调用完成
                    if (result!=null){
                        //这里调用接口，从服务器获取数据展示
                        checkMobDataFromServer(result);
                    }else {
                        closeLoading();
                        loginType = LoginType.loginByAccount;
                        switchType();
                    }

                    Log.d("界面显示测试", "onComplete");
                }

                @Override
                public void onFailure(VerifyException e) {
                    //调用失败
                    closeLoading();
                    loginType = LoginType.loginByAccount;
                    switchType();

                    Log.d("界面显示测试", "onFailure");
                }
            });
        }else {
            closeLoading();
            loginType = LoginType.loginByAccount;
            switchType();
        }
    }

    //秒验和服务器查询
    private Disposable mobVerifyDis;
    private void checkMobDataFromServer(VerifyResult result){
        startLoading("正在获取用户信息～");
        LibRxUtil.unDisposable(mobVerifyDis);
        LessonRemoteManager.mobVerifyFromServer(result.getToken(), result.getOpToken(), result.getOperator())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Mob_verify>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mobVerifyDis = d;
                    }

                    @Override
                    public void onNext(Mob_verify bean) {
                        //存在数据
                        if (bean!=null){
                            //存在账号数据
                            if (bean.getIsLogin().equals("1")&&bean.getUserinfo()!=null){
                                //根据20001重新获取数据
                                UserInfoManager.getInstance().getRemoteUserInfo(bean.getUserinfo().getUid(), new UserinfoCallbackListener() {
                                    @Override
                                    public void onSuccess() {
                                        closeLoading();
                                        SecVerify.finishOAuthPage();

                                        NewLoginActivity.this.finish();
                                    }

                                    @Override
                                    public void onFail(String errorMsg) {
                                        closeLoading();
                                        SecVerify.finishOAuthPage();

                                        ToastUtil.showToast(NewLoginActivity.this,errorMsg);
                                        loginType = LoginType.loginByAccount;
                                        switchType();
                                    }
                                });
                                return;
                            }

                            //不存在账号数据，但是存在电话号
                            if (bean.getRes()!=null&&!TextUtils.isEmpty(bean.getRes().getPhone())){
                                closeLoading();
                                SecVerify.finishOAuthPage();

                                Intent intent = new Intent();
                                intent.setClass(NewLoginActivity.this, RegistSubmitActivity.class);
                                intent.putExtra("phoneNumb", bean.getRes().getPhone());
                                intent.putExtra("isSecVerify",true);
                                //合成随机数据显示
                                intent.putExtra("username",getRandomByPhone(bean.getRes().getPhone()));
                                intent.putExtra("password",bean.getRes().getPhone().substring(bean.getRes().getPhone().length()-6));
                                startActivity(intent);

                                NewLoginActivity.this.finish();
                                return;
                            }
                        }

                        closeLoading();
                        SecVerify.finishOAuthPage();

                        ToastUtil.showToast(NewLoginActivity.this,"获取登录信息失败，请手动登录或注册账号");
                        loginType = LoginType.loginByAccount;
                        switchType();
                    }

                    @Override
                    public void onError(Throwable e) {
                        SecVerify.finishOAuthPage();
                        ToastUtil.showToast(NewLoginActivity.this,"获取登录信息失败，请手动登录或注册账号");
                        loginType = LoginType.loginByAccount;
                        switchType();
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(mobVerifyDis);
                    }
                });
    }

    /*************账号登录************/
    //验证账号和密码
    private boolean verifyAccountAndPsd(){
        String userName = etUserName.getText().toString().trim();
        String userPwd = etPassword.getText().toString().trim();

        if (userName.length() < 3) {
            ToastUtil.showToast(this,getResources().getString(R.string.login_check_effective_user_id));
            return false;
        }

        if (userPwd.length() == 0) {
            ToastUtil.showToast(this,getResources().getString(R.string.login_check_user_pwd_null));
            return false;
        }

        if (userPwd.length() < 6 || userPwd.length() > 20) {
            ToastUtil.showToast(this,getResources().getString(R.string.login_check_user_pwd_constraint));
            return false;
        }

        return true;
    }

    //账号登录
    private Disposable accountLoginDis;

    private void accountLogin(String userName,String userPwd){
        startLoading("正在登录～");
        UserInfoManager.getInstance().postRemoteAccountLogin(userName, userPwd, new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                closeLoading();
                isToFulfillInfo();
            }

            @Override
            public void onFail(String errorMsg) {
                closeLoading();
                ToastUtil.showToast(NewLoginActivity.this,errorMsg);
            }
        });
    }

    //判断是否跳转到用户信息完善界面
    private void isToFulfillInfo() {
        NewLoginActivity.this.finish();
    }

    /*******************************辅助功能***********************/
    //开启加载弹窗
    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
        }

        if (loadingDialog.isShowing()){
            return;
        }

        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void closeLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }


    //根据手机号随机生成用户名称
    private String getRandomByPhone(String phone){
        StringBuilder builder = new StringBuilder();
        builder.append("iyuba");

        //随机数
        for (int i = 0; i < 4; i++) {
            int randomInt = (int) (Math.random()*10);
            builder.append(randomInt);
        }

        String lastPhone = null;
        if (phone.length()>4){
            lastPhone = phone.substring(phone.length()-4);
        }else {
            String time = String.valueOf(System.currentTimeMillis());
            lastPhone = time.substring(time.length()-4);
        }
        builder.append(lastPhone);
        return builder.toString();
    }
}
