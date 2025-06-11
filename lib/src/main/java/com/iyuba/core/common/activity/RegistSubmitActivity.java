package com.iyuba.core.common.activity;

/**
 * 手机注册完善内容界面
 * 
 * @author czf
 * @version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestPhoneNumRegister;
import com.iyuba.core.common.protocol.message.ResponsePhoneNumRegister;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.me.activity.UpLoadImage;
import com.iyuba.lib.R;

public class RegistSubmitActivity extends BasisActivity {
	private Context mContext;
	private EditText userNameEditText, passWordEditText;
	private Button submitButton, backButton;
	private String userName, passWord;
	private CustomDialog wettingDialog;
	private TextView tvTips;

	//参数标记
	private static final String tag_phoneNum = "phoneNum";
	private static final String tag_account = "account";

	//实际的数据
	private String phoneNumber;

	public static void start(Context context,String phoneNum,String account){
		Intent intent = new Intent();
		intent.setClass(context,RegistSubmitActivity.class);
		intent.putExtra(tag_phoneNum,phoneNum);
		intent.putExtra(tag_account,account);
		context.startActivity(intent);
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		CrashApplication.getInstance().addActivity(this);
		setContentView(R.layout.regist_layout_phone_regist);
		backButton = (Button) findViewById(R.id.button_back);
		userNameEditText = (EditText) findViewById(R.id.regist_phone_username);
		passWordEditText = (EditText) findViewById(R.id.regist_phone_paswd);
		submitButton = (Button) findViewById(R.id.regist_phone_submit);
		tvTips = findViewById(R.id.tv_tips);
		wettingDialog = WaittingDialog.showDialog(mContext);

		phoneNumber = getIntent().getStringExtra(tag_phoneNum);
		String account = getIntent().getStringExtra(tag_account);
		if (!TextUtils.isEmpty(account)){
			userNameEditText.setText(account);
			passWordEditText.setText(phoneNumber.substring(phoneNumber.length()-6));

			tvTips.setText("密码默认为手机号后6位");
		}

		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// TODO 自动生成的方法存根
				if (verification()) {// 验证通过
					// 开始注册
					handler.sendEmptyMessage(0);// 在handler中注册
				}
			}

		});
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				finish();
			}
		});


		//这里根据外面的数据显示
		String randomUserName = getIntent().getStringExtra("username");
		String randomPassword = getIntent().getStringExtra("password");
		if (!TextUtils.isEmpty(randomUserName)&&!TextUtils.isEmpty(randomPassword)){
			userNameEditText.setText(randomUserName);
			passWordEditText.setText(randomPassword);
		}
	}

	/**
	 * 验证
	 */
	public boolean verification() {
		userName = userNameEditText.getText().toString();
		passWord = passWordEditText.getText().toString();
		if (!checkUserId(userName)) {
			userNameEditText.setError(mContext
					.getString(R.string.regist_check_username_1));
			return false;
		}
		if (!checkUserName(userName)) {
			userNameEditText.setError(mContext
					.getString(R.string.regist_check_username_2));
			return false;
		}
		if (!checkUserPwd(passWord)) {
			passWordEditText.setError(mContext
					.getString(R.string.regist_check_userpwd_1));
			return false;
		}
		return true;
	}

	/**
	 * 匹配用户名1
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserId(String userId) {
		if (userId.length() < 3 || userId.length() > 20)
			return false;
		return true;
	}

	/**
	 * 匹配用户名2 验证非手机号 邮箱号
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserName(String userId) {
		if (userId
				.matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
			return false;
		}
		if (userId.matches("^(1)\\d{10}$")) {
			return false;
		}

		return true;
	}

	/**
	 * 匹配密码
	 * 
	 * @param userPwd
	 * @return
	 */
	public boolean checkUserPwd(String userPwd) {
		if (userPwd.length() < 6 || userPwd.length() > 20)
			return false;
		return true;
	}

	private void regist() {
		ExeProtocol.exe(new RequestPhoneNumRegister(userName, passWord, phoneNumber), new ProtocolResponse() {
			@Override
			public void finish(BaseHttpResponse bhr) {
				// TODO Auto-generated method stub
				ResponsePhoneNumRegister rr = (ResponsePhoneNumRegister) bhr;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (rr.isRegSuccess) {
							CustomToast.showToast(mContext, R.string.regist_success);
							handler.sendEmptyMessage(4);
							handler.sendEmptyMessage(6);
						} else if (rr.resultCode.equals("112")) {
							// 提示用户已存在
							handler.sendEmptyMessage(3);
						} else {
							handler.sendEmptyMessage(1);// 弹出错误提示
						}
					}
				});
			}

			@Override
			public void error() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mContext,"注册失败请检查网络",Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void gotoLogin() {
		/*AccountManager.Instance(mContext).login(userName, passWord,
				new OperateCallBack() {
					@Override
					public void success(String result) {
						// TODO Auto-generated method stub
						if (SettingConfig.Instance().isAutoLogin()) {// 保存账户密码
							AccountManager.Instance(mContext)
									.saveUserNameAndPwd(userName, passWord);
						} else {
							AccountManager.Instance(mContext)
									.saveUserNameAndPwd("", "");
						}
						Intent intent = new Intent(mContext, UpLoadImage.class);
						intent.putExtra("regist", true);
						startActivity(intent);
						finish();
					}

					@Override
					public void fail(String message) {
						// TODO Auto-generated method stub

					}
				});*/

		//这里回调登录接口处理
		UserInfoManager.getInstance().postRemoteAccountLogin(userName, passWord, new UserinfoCallbackListener() {
			@Override
			public void onSuccess() {
				//这里不要跳转到头像界面，直接跳转回我的界面
				/*Intent intent = new Intent(mContext, UpLoadImage.class);
				intent.putExtra("regist", true);
				startActivity(intent);*/
				finish();
			}

			@Override
			public void onFail(String errorMsg) {

			}
		});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				handler.sendEmptyMessage(5);
				regist();
				break;
			case 1:
				handler.sendEmptyMessage(4);
				CustomToast.showToast(mContext, R.string.regist_fail);
				break;
			case 2:
				CustomToast.showToast(mContext, R.string.regist_success);
				break;
			case 3:
				handler.sendEmptyMessage(4);
				CustomToast.showToast(mContext, R.string.regist_userid_exist);
				break;
			case 4:
				wettingDialog.dismiss();
				break;
			case 5:
				wettingDialog.show();
				break;
			case 6:
				gotoLogin();
				break;
			default:
				break;
			}
		}
	};
}
