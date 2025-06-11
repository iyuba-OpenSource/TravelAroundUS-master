package com.iyuba.core.common.activity;

/**
 * 手机注册界面
 *
 * @author czf
 * @version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.retrofitapi.YzPhoneNumber;
import com.iyuba.core.common.retrofitapi.result.ApiRequestFactory;
import com.iyuba.core.common.retrofitapi.result.YzPhoneResult;
import com.iyuba.core.common.util.TelNumMatch;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.lib.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 手机号注册
 */
public class RegistByPhoneActivity extends BasisActivity {
    private Context mContext;
    private EditText phoneNum, messageCode;
    private Button getCodeButton;
    private TextView toEmailButton;
    private Button backBtn;
    private String phoneNumString = "", messageCodeString = "";
    private Timer timer;
    private CheckBox protocol_Img;
    private TextView protocol;
    private EventHandler eh;
    private TimerTask timerTask;
    //private SmsContent smsContent;
    private CustomDialog waittingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setContentView(R.layout.regist_layout_phone);
        CrashApplication.getInstance().addActivity(this);
        waittingDialog = WaittingDialog.showDialog(mContext);
        messageCode = (EditText) findViewById(R.id.regist_phone_code);
        phoneNum = (EditText) findViewById(R.id.regist_phone_numb);
        getCodeButton = (Button) findViewById(R.id.regist_getcode);
        btnNextStep = findViewById(R.id.btn_nextStep);

        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handlerSms.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);

        //smsContent = new SmsContent(RegistByPhoneActivity.this, handler_verify);
        protocol = (TextView) findViewById(R.id.protocol);
        protocol_Img=findViewById(R.id.protocol_Img);
        String name = TextAttr.encode("走遍美国");
//        protocol.setText(Html
//                .fromHtml("我已阅读并同意<a href=\"https://ai." + Constant.IYBHttpHead + "/api/protocol.jsp?apptype=" + name + "\">使用条款和隐私政策</a>"));
//        //https://ai." + Constant.IYUBA_CN + "api/protocol.jsp?apptype=
//        //TextAttr.encode("走遍美国")
        String remindString = "我已阅读并同意使用协议和隐私政策";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(remindString);
        ClickableSpan secretString = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Web.start(mContext, "https://ai."+ Constant.IYBHttpHead() +"/api/protocolpri.jsp?apptype=" + Constant.APPName + "&company=1", "用户隐私政策");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(RegistByPhoneActivity.this, R.color.app_color));
            }
        };

        ClickableSpan policyString = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Web.start(mContext, "https://ai."+ Constant.IYBHttpHead() +"/api/protocoluse666.jsp?apptype=" + Constant.APPName + "&company=爱语吧", "用户使用协议");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(RegistByPhoneActivity.this, R.color.app_color));
            }
        };
        spannableStringBuilder.setSpan(secretString, remindString.indexOf("隐私政策"), remindString.indexOf("隐私政策") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(policyString, remindString.indexOf("使用协议"), remindString.indexOf("使用协议") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        protocol.setText(spannableStringBuilder);
        protocol.setMovementMethod(LinkMovementMethod.getInstance());

        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toEmailButton = (TextView) findViewById(R.id.regist_email);
        toEmailButton.setVisibility(View.GONE);
        toEmailButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        toEmailButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, RegistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        getCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取手机验证吗
                sendSms();
            }
        });
        btnNextStep.setOnClickListener(v -> {
            if (!protocol_Img.isChecked()){
                ToastUtil.showToast(mContext,"请同意隐私政策");
                return;
            }

            if (verification()) {
                SMSSDK.submitVerificationCode("86", phoneNumString, messageCode.getText().toString());
            } else {
                CustomToast.showToast(mContext, "验证码不能为空");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    public boolean verification() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();

        if (phoneNumString.length() == 0) {
            phoneNum.setError("手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
            phoneNum.setError("手机号输入错误");
            return false;
        }

        if (messageCodeString.length() == 0) {
            messageCode.setError("验证码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 验证
     */
    public boolean verificationNum() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (phoneNumString.length() == 0) {
            phoneNum.setError("手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
            phoneNum.setError("手机号输入错误");
            return false;
        }

        return true;
    }

    public boolean checkPhoneNum(String userId) {
        if (userId.length() < 2)
            return false;
        TelNumMatch match = new TelNumMatch(userId);
        int flag = match.matchNum();
        /*不check 号码的正确性，只check 号码的长度*/
		/*if (flag == 1 || flag == 2 || flag == 3) {
			return true;
		} else {
			return false;
		}*/
        if (flag == 5) {
            return false;
        } else {
            return true;
        }
    }

    Handler handlerSms = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    CustomToast.showToast(mContext, "验证成功");
                    RegistSubmitActivity.start(RegistByPhoneActivity.this,phoneNumString,getRandomByPhone(phoneNumString));
                    finish();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        boolean smart = (Boolean) msg.obj;
                        if (smart) {
                            //通过智能验证
                            CustomToast.showToast(mContext, "已通过智能验证，成功");
                            RegistSubmitActivity.start(RegistByPhoneActivity.this,phoneNumString,getRandomByPhone(phoneNumString));
                            finish();
                        } else {
                            //依然走短信验证
                            CustomToast.showToast(mContext, "验证码已经发送，请等待接收");
                        }
                    }
                }
            } else {
                //CustomToast.showToast(mContext, "验证失败，请输入正确的验证码！");
                getCodeButton.setText("获取验证码");
                getCodeButton.setEnabled(true);
            }
        }
    };

    Handler handler_time = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what > 0) {
                getCodeButton.setText("重新发送(" + msg.what + "s)");
            } else {
                timer.cancel();
                getCodeButton.setEnabled(true);
                getCodeButton.setText("获取验证码");
            }
        }
    };

    Handler handler_waitting = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    waittingDialog.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext,
                            "手机号已注册，请换一个号码试试~", 2000);
                    break;
            }
        }
    };

    Handler handler_verify = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what == 0) {
                timer.cancel();
                /*
                 * getCodeButton.setText("下一步"); getCodeButton.setEnabled(true);
                 */
                String verifyCode = (String) msg.obj;
                messageCode.setText(verifyCode);
            } else if (msg.what == 1) {
                SMSSDK.getVerificationCode("86", phoneNum.getText().toString());
                timer = new Timer();
                timerTask = new TimerTask() {
                    int i = 60;

                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = i--;
                        handler_time.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask, 1000, 1000);
                getCodeButton.setTextColor(Color.WHITE);
                getCodeButton.setEnabled(false);
            }
        }
    };

    private Button btnNextStep;

    private void sendSms(){
        if (verificationNum()) {
            if (timer != null) {
                timer.cancel();
            }
            handler_waitting.sendEmptyMessage(1);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            phoneNumString = phoneNum.getText().toString();
            ApiRequestFactory.getYzPhoneNumber().
                    getYzPhoneNumberState(YzPhoneNumber.FORMAT, phoneNumString)
                    .enqueue(new Callback<YzPhoneResult>() {
                        @Override
                        public void onResponse(Call<YzPhoneResult> call, Response<YzPhoneResult> response) {
                            if (response.isSuccessful()) {
                                if ("1".equals(response.body().getResult())) {
                                    handler_verify.sendEmptyMessage(1);
                                    //RegistByPhoneActivity.this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContent);
                                } else if ("-1".equals(response.body().getResult())) {
                                    handler_waitting.sendEmptyMessage(3);
                                }
                                handler_waitting.sendEmptyMessage(2);
                            }
                        }

                        @Override
                        public void onFailure(Call<YzPhoneResult> call, Throwable t) {
                            handler_waitting.sendEmptyMessage(2);
                        }
                    });
        } else {
            CustomToast.showToast(mContext, "电话不能为空");
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
