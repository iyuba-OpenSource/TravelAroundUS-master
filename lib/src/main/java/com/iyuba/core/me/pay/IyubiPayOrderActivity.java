package com.iyuba.core.me.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.lib.R;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by howard9891 on 2016/10/28.
 */

public class IyubiPayOrderActivity extends Activity {
    private TextView payorder_username;
    private TextView payorder_rmb_amount;
    private NoScrollListView methodList;
    private PayMethodAdapter methodAdapter;
    private Button payorder_submit_btn;
    private boolean confirmMutex = true;
    private static final String TAG = IyubiPayOrderActivity.class.getSimpleName();
    private Context mContext;
    private static final String Seller = "iyuba@sina.com";
    private String price;
    private String subject;
    private String body;
    private String amount;
    private int type;
    private String out_trade_no;
    private IWXAPI mWXAPI;
    private String mWeiXinKey;
    private int selectPosition = 0;
    private Button button;
    private String productId;
    private TextView tv_header;
    private TextView tv_vip_info;

    private TextView vipAgreement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_buyvip);
        Intent intent = getIntent();
        price = intent.getStringExtra("price");
        amount = intent.getStringExtra("amount");

//        type = intent.getIntExtra("type", -1);
        subject = intent.getStringExtra("subject");
        body = intent.getStringExtra("body");
        out_trade_no = intent.getStringExtra("out_trade_no");
        productId = intent.getStringExtra("productID");
        findView();
//        mWeiXinKey = mContext.getString(R.string.weixin_key);
        mWeiXinKey = Constant.mWeiXinKey;
        mWXAPI = WXAPIFactory.createWXAPI(this, mWeiXinKey, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        payorder_username.setText(UserInfoManager.getInstance().getUserName());

    }

    private void findView() {
        button = (Button) findViewById(R.id.btn_back);
        tv_header = (TextView) findViewById(R.id.tv_header);
        tv_vip_info = findViewById(R.id.tv_vip_info);
        tv_vip_info.setText(body);
        tv_header.setText("购买爱语币");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        payorder_username = (TextView) findViewById(R.id.payorder_username_tv);
        payorder_username.setText(UserInfoManager.getInstance().getUserName());
        payorder_rmb_amount = (TextView) findViewById(R.id.payorder_rmb_amount_tv);
        payorder_rmb_amount.setText(price + "元");
        methodList = (NoScrollListView) findViewById(R.id.payorder_methods_lv);
        payorder_submit_btn = (Button) findViewById(R.id.payorder_submit_btn);
        methodList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                methodAdapter.changeSelectPosition(position);
                methodAdapter.notifyDataSetChanged();
            }
        });
        methodAdapter = new PayMethodAdapter(this);
        methodList.setAdapter(methodAdapter);
        payorder_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmMutex) {
                    confirmMutex = false;
                    String newSubject;
                    String newbody;
                    try {
                        newSubject = URLEncoder.encode(subject, "UTF-8");
                        newbody = URLEncoder.encode(body, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        newSubject = "iyubi";
                        newbody = "iyubi";
                    }
                    switch (selectPosition) {
                        case PayMethodAdapter.PayMethod.ALIPAY:
                            if (!UserInfoManager.getInstance().isLogin())
                                showNormalDialog();
                            else
                                payByAlipay(newbody, newSubject);
                            break;
                        case PayMethodAdapter.PayMethod.WEIXIN:
                            Log.e("PayOrderActivity", "weixin");
                            if (mWXAPI.isWXAppInstalled()) {
                                payByWeiXin();
                            } else {
                                /*new AlertDialog.Builder(PayOrderActivity.this)
                                        .setTitle("提示")
                                        .setMessage("微信未安装无法使用微信支付!")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                confirmMutex = true;
                                                dialog.cancel();
                                            }
                                        })
                                        .show();*/
                                ToastUtil.showToast(mContext, "您还未安装微信客户端");
                            }
                            break;
                      /*  case PayMethodAdapter.PayMethod.BANKCARD:
                            payByWeb();
                            break;*/
                        default:
                            payByAlipay(newbody, newSubject);
                            break;
                    }
                }
            }
        });

        //设置vip支付协议
        vipAgreement = findViewById(R.id.vip_agreement);
        vipAgreement.setText(setVipAgreement());
        vipAgreement.setMovementMethod(new LinkMovementMethod());
    }

    //设置会员服务协议的样式
    private SpannableStringBuilder setVipAgreement() {
        String vipStr = "《会员服务协议》";
        String showMsg = "点击支付即代表您已充分阅读并同意" + vipStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //会员服务协议
        int termIndex = showMsg.indexOf(vipStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String jumpUrl = Constant.VIP_AGREEMENT_URL;
                String title = "会员协议";
                Web.start(IyubiPayOrderActivity.this,jumpUrl,title);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, termIndex, termIndex + vipStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    private void payByAlipay(String body, String subject) {
        confirmMutex = true;
        RequestCallBack rc = new RequestCallBack() {
            @Override
            public void requestResult(Request result) {
                OrderGenerateRequest request = (OrderGenerateRequest) result;
                if (request.isRequestSuccessful()) {
                    // 完整的符合支付宝参数规范的订单信息
                    final String payInfo = request.orderInfo + "&sign=\"" + request.orderSign
                            + "\"&" + "sign_type=\"RSA\"";

                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(IyubiPayOrderActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo, true);

                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            alipayHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    validateOrderFail();
                }
            }
        };
        OrderGenerateRequest orderRequest = new OrderGenerateRequest(productId, Seller, out_trade_no,
                subject, price, body, "", Constant.APPID,
                String.valueOf(UserInfoManager.getInstance().getUserId()), amount,
                mOrderErrorListener, rc);
        CrashApplication.getInstance().getQueue().add(orderRequest);
    }

    private void payByWeiXin() {
        confirmMutex = true;
        RequestCallBack rc = new RequestCallBack() {
            @Override
            public void requestResult(Request result) {
                OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
                if (first.isRequestSuccessful()) {
                    Log.e(TAG, "OrderGenerateWeiXinRequest success!");
                    PayReq req = new PayReq();
                    req.appId = mWeiXinKey;
                    req.partnerId = first.partnerId;
                    req.prepayId = first.prepayId;
                    req.nonceStr = first.nonceStr;
                    req.timeStamp = first.timeStamp;
                    req.packageValue = "Sign=WXPay";
                    req.sign = buildWeixinSign(req, first.mchkey);
                    mWXAPI.sendReq(req);
                } else {
                    validateOrderFail();
                }
            }
        };
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        OrderGenerateWeiXinRequest request = new OrderGenerateWeiXinRequest(productId, mWeiXinKey,mWeiXinKey, Constant.APPID, uid, price, amount, body, mOrderErrorListener, rc);
        CrashApplication.getInstance().getQueue().add(request);
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
        Log.i(TAG, sb.toString());
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }

    private String buildWeixinStringA(PayReq payReq) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        return sb.toString();
    }

    /*private void payByWeb() {
        String url = "http://app."+Constant.IYBHttpHead+"/wap/servlet/paychannellist?";
        url += "out_user=" + AccountManager.Instance(mContext).userId;
        url += "&appid=" + Constant.APPID;
        url += "&amount=" + 0;
        Intent intent = WebActivity.buildIntent(this, url, "订单支付");
        startActivity(intent);
        confirmMutex = true;
        finish();
    }*/

    private void validateOrderFail() {
        ToastUtil.showToast(mContext, "订单异常!!!!!!!!");
        IyubiPayOrderActivity.this.finish();
//        new AlertDialog.Builder(PayOrderActivity.this)
//                .setTitle("订单异常!")
//                .setMessage("订单验证失败!")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        PayOrderActivity.this.finish();
//                    }
//                })
//                .show();
    }

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
//            ToastUtil.showToast(mContext, "订单异常!");
//            PayOrderActivity.this.finish();
            new AlertDialog.Builder(IyubiPayOrderActivity.this)
                    .setTitle("订单提交出现问题!")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmMutex = true;
                            dialog.dismiss();
                            IyubiPayOrderActivity.this.finish();
                        }
                    })
                    .show();
        }
    };

    private Handler alipayHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    confirmMutex = true;
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        ConfigManager.Instance().putBoolean("isvip", true);
                     /*   // TODO refresh user's iyubi information!!!
                        new AlertDialog.Builder(PayOrderActivity.this)
                                .setTitle("支付成功")
                                .setMessage("请填写详细的联系地址以便收到所赠礼品！")
                                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        PayOrderActivity.this.finish();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        String url = "http://www."+Constant.IYBHttpHead+"/sendbook/indexmm.jsp?uid="
                                                + AccountManager.Instance(mContext).userId + "&platform=android";
                                       *//* Intent intent = WebActivity.buildIntent(PayOrderActivity
                                                .this, url, "赠送礼品");
                                        startActivity(intent);*//*
                                        PayOrderActivity.this.finish();
                                    }
                                })
                                .show();
                    */
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.showToast(IyubiPayOrderActivity.this, "支付结果确认中", 1500);
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.showToast(IyubiPayOrderActivity.this, "您已取消支付", 1500);
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.showToast(IyubiPayOrderActivity.this, "网络连接出错", 1500);
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.showToast(IyubiPayOrderActivity.this, "支付失败", 1500);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("临时用户无法购买vip和爱语币！");
        normalDialog.setPositiveButton("登录",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        Intent intent = new Intent();
//                        intent.setClass(mContext, Login.class);
//                        startActivity(intent);
                        LoginUtil.startToLogin(mContext);
                    }
                });
        normalDialog.setNegativeButton("确定",null);
        //显示
        normalDialog.show();
    }
}
