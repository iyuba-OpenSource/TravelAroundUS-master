//package com.iyuba.core.common.manager;
//
//import android.content.Context;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.core.common.listener.OperateCallBack;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.PayRequest;
//import com.iyuba.core.common.protocol.base.PayResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//
///**
// * 购买VIP管理
// *
// * @author chentong
// * @version 1.0
// *
// */
//public class PayManager {
//	private static PayManager instance;
//	private static Context mContext;
//
//	public static PayManager Instance(Context mContext) {
//		if (instance == null) {
//			instance = new PayManager();
//		}
//		PayManager.mContext = mContext;
//		return instance;
//	}
//
//	public void payAmount(final String userId, final int amount,
//			final OperateCallBack resultListener) {
//		ClientSession.Instace().asynGetResponse(new PayRequest(userId, amount),
//				new IResponseReceiver() {
//					@Override
//					public void onResponse(BaseHttpResponse response,
//							BaseHttpRequest request, int rspCookie) {
//						PayResponse payResponse = (PayResponse) response;
//
//						if (payResponse.result.equals("1"))// 支付成功
//						{
//							SettingConfig.Instance().setHighSpeed(true);
//							AccountManager.Instance(mContext).userInfo.vipStatus = payResponse.vipflag;
//							ConfigManager.Instance().putInt("isvip",
//									Integer.parseInt(payResponse.vipflag));
//							AccountManager.Instance(mContext).userInfo.deadline = "终身VIP";
//							resultListener.success(payResponse.amount);
//						} else {// 支付失败
//							if (Integer.parseInt(payResponse.amount) < amount)// 余额不足
//							{// 提示用户余额不足，并跳转到充值页面
//								resultListener.fail(payResponse.amount);
//							} else {
//								resultListener.fail(payResponse.msg);
//							}
//
//						}
//					}
//				});
//	}
//
//	public void payAmount(final String userId, final int amount,
//			final int month, final OperateCallBack resultListener) {
//		ClientSession.Instace().asynGetResponse(
//				new PayRequest(userId, amount, month), new IResponseReceiver() {
//					@Override
//					public void onResponse(BaseHttpResponse response,
//							BaseHttpRequest request, int rspCookie) {
//						PayResponse payResponse = (PayResponse) response;
//
//						if (payResponse.result.equals("1"))// 支付成功
//						{
//							SettingConfig.Instance().setHighSpeed(true);
//							AccountManager.Instance(mContext).userInfo.vipStatus = payResponse.vipflag;
//							ConfigManager.Instance().putInt("isvip",
//									Integer.parseInt(payResponse.vipflag));
//							AccountManager.Instance(mContext).userInfo.deadline = payResponse.VipEndTime
//									.split(" ")[0];
//							resultListener.success(payResponse.amount);
//						} else {// 支付失败
//							if (Integer.parseInt(payResponse.amount) < amount)// 余额不足
//							{// 提示用户余额不足，并跳转到充值页面
//								resultListener.fail(payResponse.amount);
//							} else {
//								resultListener.fail(payResponse.msg);
//							}
//
//						}
//					}
//				});
//	}
//}
