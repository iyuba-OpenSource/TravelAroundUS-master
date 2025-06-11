package com.iyuba.core.me.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.protocol.message.RequestChangeName;
import com.iyuba.core.common.protocol.message.ResponseChangeName;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.lib.R;

import butterknife.ButterKnife;


public class ModifyNameDialog extends Dialog {

    EditText mUsernameEdt;
    Button mCancelBtn;
    Button mSubmitBtn;
    ProgressBar mProgressBar;

    int uid;
    String oldUsername;
    ModifySuccessCallback mListener;


    public ModifyNameDialog(Context context) {
        super(context, R.style.DialogTheme_personal);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_modify_name_personal);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        initWidget();
        onPostCreate();
        initListener();
    }

    private void initWidget() {
        mUsernameEdt=findViewById(R.id.edit_username);
        mCancelBtn=findViewById(R.id.button_cancel);
        mSubmitBtn=findViewById(R.id.button_submit);
        mProgressBar=findViewById(R.id.progressbar);
    }

    private void initListener() {
        mCancelBtn.setOnClickListener(v -> dismiss());
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = mUsernameEdt.getText().toString();
                if (TextUtils.isEmpty(newName)) {
                    showMessage("用户名不可为空!");
                } else if (newName.length() > 15) {
                    showMessage("用户名过长！");
                } else {
                    requestChange(uid,oldUsername,newName);
                }
            }
        });
    }

    private void onPostCreate() {
        if (oldUsername != null) {
            mUsernameEdt.setText(oldUsername);
        }
    }

    public ModifyNameDialog setUserId(int userId) {
        uid = userId;
        return this;
    }

    public ModifyNameDialog setOldName(String name) {
        oldUsername = name;
        if (mUsernameEdt != null && oldUsername != null) {
            mUsernameEdt.setText(oldUsername);
        }
        return this;
    }

    public ModifyNameDialog setModifySuccessCallback(ModifySuccessCallback l) {
        this.mListener = l;
        return this;
    }

    private void requestChange(int userId, String oldName, final String newName){
        ClientSession.Instace()
                .asynGetResponse(
                        new RequestChangeName(
                                userId,oldName,newName),
                        (response, request, rspCookie) -> {
                            ResponseChangeName rs = (ResponseChangeName) response;
                            Message msg=new Message();
                            msg.what=1;
                            msg.obj=newName;
                            if (rs.isSuccess){
                                handler.sendMessage(msg);
                            }else {
                                handler.sendEmptyMessage(2);
                            }
                        });
    }

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    onModifyResult(true, String.valueOf(msg.obj));
                break;
                case 2:
                    onModifyResult(false, "");
                    break;
            }
        }
    };

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setCancelButton(boolean isOn) {
        mCancelBtn.setClickable(isOn);
    }

    public void setSubmitButton(boolean isOn) {
        mSubmitBtn.setVisibility(isOn ? View.VISIBLE : View.INVISIBLE);
        mSubmitBtn.setClickable(isOn);
    }

    public void setProgress(boolean isVisible) {
        mProgressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void showMessage(String message) {
        ToastUtil.showToast(getContext(), message);
    }

    public void onModifyResult(boolean result, String newName) {
        if (result) {
            showMessage("修改成功!");
            //好像没必要
            //AccountManager.getInstance().updateCurrentUserName(newName);
            dismiss();
            if (mListener != null) mListener.call(newName);
        } else {
            showMessage("请选择其他用户名!");
        }
    }

    public interface ModifySuccessCallback {
        void call(String newName);
    }

}
