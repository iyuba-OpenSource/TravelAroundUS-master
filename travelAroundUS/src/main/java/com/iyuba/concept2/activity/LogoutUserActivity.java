package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.concept2.R;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.LoginOutUserRequest;
import com.iyuba.core.common.protocol.base.LoginOutUserResponse;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.ChangeVideoEvnet;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibStackUtil;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;

/**
 * 修改用户名界面
 *
 * @author chentong
 * @version 1.2
 */
public class LogoutUserActivity extends BaseStackActivity {
    private TextView mCurrentUsername;

    private Button backBtn;
    private Button registBtn, loginBtn;
    private String userName, userPwd;
    private EditText userPwdET;
    private CheckBox autoLogin;
    private CustomDialog cd;
    private Context mContext;
    private TextView findPassword;
    private boolean boolRequestSuccess = false;

    private Disposable mDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_logout_user);

        CrashApplication.getInstance().addActivity(this);
        mContext = this;

        cd = WaittingDialog.showDialog(mContext);
        // 密码 edit
        userPwdET = findViewById(R.id.editText_userPwd);
        mCurrentUsername=findViewById(R.id.current_username);
        mCurrentUsername.setText("当前用户名："+ UserInfoManager.getInstance().getUserName());
        //返回btn
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //登录btn
        loginBtn = (Button) findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userPwdET.getText().toString())){
                    Toast.makeText(LogoutUserActivity.this,"密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                clearUser(UserInfoManager.getInstance().getUserName(), userPwdET.getText().toString());
            }
        });

        //忘记密码btn
        findPassword = (TextView) findViewById(R.id.find_password);
        findPassword.setText(Html.fromHtml("<a href=\"http://m.iyuba.cn/m_login/inputPhonefp.jsp?\">"
                + getResources().getString(
                R.string.login_find_password) + "</a>"));
        findPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void clearUser(String username, String password) {

        ExeProtocol.exe(new LoginOutUserRequest(username, password), new ProtocolResponse() {

            @Override
            public void finish(BaseHttpResponse bhr) {
                LoginOutUserResponse loginOutUserResponse = (LoginOutUserResponse) bhr;
                if (loginOutUserResponse.response.result.equals("101")) {
                    //success
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearSuccess();
                        }
                    });
                }else {

                    //success
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogoutUserActivity.this,"注销失败!请确认密码及网络正确。",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void error() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LogoutUserActivity.this,"注销失败!请确认密码及网络正确。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void clearSuccess() {
        //注销账号后退出登录
        UserInfoManager.getInstance().clearUserInfo();
        SettingConfig.Instance().setHighSpeed(false);

        //手动写一下
        ConfigManager.Instance().putString("userId", "0");
        ConfigManager.Instance().putString("userName", "");
        ConfigManager.Instance().putInt("isvip", 0);
        //退出登录刷新状态
        EventBus.getDefault().post(new ChangeVideoEvnet(true));//event发布

        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("账户注销成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //直接退出到我的界面
                        LibStackUtil.getInstance().finish(LogoutUserActivity.class);
                        LibStackUtil.getInstance().finish(SetActivity.class);
                    }
                })
                .show();
    }
}
