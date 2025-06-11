//package com.iyuba.core.me.activity;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.Login;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.manager.PayManager;
//import com.iyuba.core.common.thread.GitHubImageLoader;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.lib.R;
//
//import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
//
///**
// * 购买VIP
// *
// * @author chentong
// * @version 1.1 修改内容 更换为主站VIP和单应用终身VIP
// */
//public class BuyVip extends BasisActivity {
//	private View relativeLayout_noLogin, relativetLayout_login; // 登录提示面板
//	private ImageView month, quarter, half_year, year, lifelong;
//	private TextView username, account, explain, state, deadline;
//	private Button loginBtn, buy_iyubi, back,buy_iyubi_big;
//	private Context mContext;
//	private int iyubi_amount;
//	private CustomDialog wettingDialog;
//	private ImageView photo;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.iyuba_store);
//		CrashApplication.getInstance().addActivity(this);
//		mContext = this;
//		wettingDialog = WaittingDialog.showDialog(mContext);
//		relativeLayout_noLogin = findViewById(R.id.relativeLayout_noLogin);
//		relativetLayout_login = findViewById(R.id.relativeLayout_Login);
//		loginBtn = (Button) findViewById(R.id.button_to_login);
//		loginBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(mContext, Login.class);
//				startActivity(intent);
//			}
//		});
//		if (!AccountManager.Instance(mContext).checkUserLogin()) {// 未登录
//			relativeLayout_noLogin.setVisibility(View.VISIBLE);
//			relativetLayout_login.setVisibility(View.GONE);
//		} else {
//			relativeLayout_noLogin.setVisibility(View.GONE);
//			relativetLayout_login.setVisibility(View.VISIBLE);
//		}
//	}
//
//	private void init() {
//		back = (Button) findViewById(R.id.button_back);
//		month = (ImageView) findViewById(R.id.month);
//		quarter = (ImageView) findViewById(R.id.quarter);
//		half_year = (ImageView) findViewById(R.id.half_year);
//		year = (ImageView) findViewById(R.id.year);
//		lifelong = (ImageView) findViewById(R.id.life_long);
//		buy_iyubi = (Button) findViewById(R.id.buy_iyubi);
//		buy_iyubi_big = (Button)findViewById(R.id.btn_buyIyubi);
//		username = (TextView) findViewById(R.id.buy_username);
//		username.setText(AccountManager.Instance(mContext).userName);
//		explain = (TextView) findViewById(R.id.explain2);
//		explain.setText(mContext.getString(R.string.buy_explain2)
//				+ Constant.price + mContext.getString(R.string.buy_explain3));
//		account = (TextView) findViewById(R.id.buy_account);
//		account.setText(AccountManager.Instance(mContext).userInfo.iyubi);
//		state = (TextView) findViewById(R.id.buy_state);
//		deadline = (TextView) findViewById(R.id.buy_deadline);
//		photo = (ImageView) findViewById(R.id.img);
//		GitHubImageLoader.Instace(mContext).setCirclePic(
//				AccountManager.Instance(mContext).userId, photo);
//		iyubi_amount = Integer
//				.parseInt(AccountManager.Instance(mContext).userInfo.iyubi);
//		month.setOnClickListener(ocl);
//		quarter.setOnClickListener(ocl);
//		half_year.setOnClickListener(ocl);
//		year.setOnClickListener(ocl);
//		lifelong.setOnClickListener(ocl);
//		back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				onBackPressed();
//			}
//		});
//
//		buy_iyubi_big.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				buyIyubi();
//			}
//		});
//		buy_iyubi.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				buyIyubi();
//			}
//		});
//		if (AccountManager.Instance(mContext).userInfo.vipStatus.equals("1")) {
//			state.setText("VIP");
//			deadline.setText(AccountManager.Instance(mContext).userInfo.deadline);
//		} else {
//			state.setText(R.string.person_common_user);
//			deadline.setText(R.string.person_not_vip);
//		}
//		photo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				startActivity(PersonalHomeActivity.buildIntent (mContext,
//						Integer.valueOf(AccountManager.getInstance().userId),
//						AccountManager.getInstance().userName, 0));//点击其他人头像时也可以进入
//
//			}
//		});
//	}
//
//	private void buyVip(int month) {
//		handler.sendEmptyMessage(3);
//		if (month != 0) {
//			PayManager.Instance(mContext).payAmount(
//					AccountManager.Instance(mContext).userId, month * 100,
//					month, new OperateCallBack() {
//
//						@Override
//						public void success(String message) {
//							// TODO Auto-generated method stub
//							Message hmsg = handler.obtainMessage(2, message);// 对话框提示支付成功
//							handler.sendMessage(hmsg);
//						}
//
//						@Override
//						public void fail(String message) {
//							// TODO Auto-generated method stub
//							handler.sendEmptyMessage(1);
//						}
//					});
//		} else {
//			PayManager.Instance(mContext).payAmount(
//					AccountManager.Instance(mContext).userId, Constant.price,
//					new OperateCallBack() {
//
//						@Override
//						public void success(String message) {
//							// TODO Auto-generated method stub
//							Message hmsg = handler.obtainMessage(2, message);// 对话框提示支付成功
//							handler.sendMessage(hmsg);
//						}
//
//						@Override
//						public void fail(String message) {
//							// TODO Auto-generated method stub
//							handler.sendEmptyMessage(1);
//						}
//					});
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		if (!AccountManager.Instance(mContext).checkUserLogin()) {// 未登录
//			relativeLayout_noLogin.setVisibility(View.VISIBLE);
//			relativetLayout_login.setVisibility(View.GONE);
//		} else {
//			init();
//			relativeLayout_noLogin.setVisibility(View.GONE);
//			relativetLayout_login.setVisibility(View.VISIBLE);
//		}
//	}
//
//	Handler handler = new Handler() {
//		@Override
//		public void handleMessage(final Message msg) {
//			super.handleMessage(msg);
//			Dialog dialog;
//			switch (msg.what) {
//			case 0:
//				final int month = msg.arg1;
//				String content1 = mContext
//						.getString(R.string.alert_buy_content1);
//				String content2 = mContext
//						.getString(R.string.alert_buy_content2);
//				StringBuffer sb = new StringBuffer();
//				if (month == 0) {
//					sb.append(content1).append(Constant.price).append(content2);
//				} else {
//					sb.append(content1).append(month * 100).append(content2);
//				}
//				dialog = new AlertDialog.Builder(mContext)
//						.setIcon(android.R.drawable.ic_dialog_alert)
//						.setTitle(R.string.alert_title)
//						.setMessage(sb.toString())
//						.setPositiveButton(R.string.alert_btn_buy,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int whichButton) {
//										buyVip(month);
//									}
//								})
//						.setNeutralButton(R.string.alert_btn_cancel, null)
//						.create();
//				dialog.show();// 如果要显示对话框，一定要加上这句
//				break;
//			case 1:
//				wettingDialog.dismiss();
//				dialog = new AlertDialog.Builder(mContext)
//						.setIcon(android.R.drawable.ic_dialog_alert)
//						.setTitle(R.string.alert_title)
//						.setMessage(R.string.alert_recharge_content)
//						.setPositiveButton(R.string.alert_btn_recharge,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int whichButton) {
//										buyIyubi();
//									}
//								})
//						.setNeutralButton(R.string.alert_btn_cancel, null)
//						.create();
//				dialog.show();// 如果要显示对话框，一定要加上这句
//				break;
//			case 2:
//				wettingDialog.dismiss();
//				CustomToast.showToast(mContext, R.string.buy_success_update);
//				AccountManager.Instance(mContext).userInfo.iyubi = msg.obj
//						.toString();
//				account.setText(msg.obj.toString());
//				break;
//			case 3:
//				wettingDialog.show();
//				break;
//			case 4:
//				wettingDialog.dismiss();
//				dialog = new AlertDialog.Builder(mContext)
//						.setIcon(android.R.drawable.ic_dialog_alert)
//						.setTitle(R.string.alert_title)
//						.setMessage(R.string.alert_all_life_vip)
//						.setPositiveButton(R.string.alert_btn_recharge,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int whichButton) {
//										buyIyubi();
//									}
//								})
//						.setNeutralButton(R.string.alert_btn_cancel, null)
//						.create();
//				dialog.show();// 如果要显示对话框，一定要加上这句
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	private OnClickListener ocl = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if (AccountManager.Instance(mContext).userInfo.deadline
//					.equals("终身VIP")) {
//				handler.sendEmptyMessage(4);
//			} else if (v == month) {
//				if (iyubi_amount >= 200) {
//					handler.obtainMessage(0, 2, 0).sendToTarget();
//				} else {
//					handler.sendEmptyMessage(1);
//				}
//			} else if (v == quarter) {
//				if (iyubi_amount >= 600) {
//					handler.obtainMessage(0, 6, 0).sendToTarget();
//				} else {
//					handler.sendEmptyMessage(1);
//				}
//			} else if (v == half_year) {
//				if (iyubi_amount >= 1000) {
//					handler.obtainMessage(0, 10, 0).sendToTarget();
//				} else {
//					handler.sendEmptyMessage(1);
//				}
//			} else if (v == year) {
//				if (iyubi_amount >= 2000) {
//					handler.obtainMessage(0, 20, 0).sendToTarget();
//				} else {
//					handler.sendEmptyMessage(1);
//				}
//			} else if (v == lifelong) {
//				int amount = Constant.price;
//				if (iyubi_amount >= amount) {
//					handler.obtainMessage(0, 0, 0).sendToTarget();
//				} else {
//					handler.sendEmptyMessage(1);
//				}
//			}
//		}
//	};
//
//	private void buyIyubi() {
//		Intent intent = new Intent();
//		intent.setClass(mContext, Web.class);
//		intent.putExtra("url", "http://app."+Constant.IYBHttpHead()+"/wap/index.jsp?uid="
//				+ AccountManager.Instance(mContext).userId + "&appid="
//				+ Constant.APPID);
//		intent.putExtra("title", Constant.APPName);
//		startActivity(intent);
//	}
//}
