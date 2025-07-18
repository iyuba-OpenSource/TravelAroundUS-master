package com.iyuba.core.me.pay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.model.remote.manager.LessonRemoteManager;
import com.iyuba.core.lil.remote.bean.Alipay_info;
import com.iyuba.core.lil.ui.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibAliPayResultUtil;
import com.iyuba.core.lil.util.LibRxTimer;
import com.iyuba.core.lil.util.LibRxUtil;
import com.iyuba.core.lil.view.LoadingDialog;
import com.iyuba.imooclib.IMooc;
import com.iyuba.lib.R;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by howard9891 on 2016/10/28.
 *
 * 付款界面
 */

public class PayOrderActivity extends BaseStackActivity {
    private TextView payorder_username;
    private TextView payorder_rmb_amount;
    private NoScrollListView methodList;
    private PayMethodAdapter methodAdapter;
    private Button payorder_submit_btn;
    private boolean confirmMutex = true;
    private static final String TAG = PayOrderActivity.class.getSimpleName();
    private Context mContext;
    private static final String Seller = "iyuba@sina.com";
    private String price;
    private String subject;
    private String body;
    private String amount;
    private int type;
    private String out_trade_no;
    private IWXAPI msgApi;
    private String mWeiXinKey = "wxfa4799656aff72d9";

    private int selectPosition = 0;
    private Button button;
    private String productId;
    private TextView mTvVipInfo;
    private String mVipInfo;

    private TextView vipAgreement;

    public static Intent buildIntent(Context context,String price,int type,String subject,String body,String outTradeNo,String orderinfo,String productId,String amount){
        Intent intent = new Intent();
        intent.setClass(context,PayOrderActivity.class);
        intent.putExtra("price",price);
        intent.putExtra("type",type);
        intent.putExtra("subject",subject);
        intent.putExtra("body",body);
        intent.putExtra("out_trade_no",outTradeNo);
        intent.putExtra("orderinfo",orderinfo);
        intent.putExtra("productId",productId);
        intent.putExtra("amount",amount);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        mContext = this;
        Intent intent = getIntent();
        Boolean isIyubi = intent.getBooleanExtra("isIyubi", false);

        if (isIyubi) {
            setContentView(R.layout.activity_buyiyubi);
        } else {
            setContentView(R.layout.activity_buyvip);
        }

        //切换这里显示购买价格
        price = intent.getStringExtra("price");
//        price = "0.01";

        type = intent.getIntExtra("type", -1);
        subject = intent.getStringExtra("subject");
        body = intent.getStringExtra("body");
        out_trade_no = intent.getStringExtra("out_trade_no");
        productId = intent.getStringExtra("productId");//getProductId(subject);
        amount = getAmount(type);

        //微课内容
        if (subject.equals("微课直购")){
            productId = intent.getStringExtra("productId");
            amount = intent.getStringExtra("amount");
        }

        findView();
        msgApi = WXAPIFactory.createWXAPI(mContext, null);
         // 将该app注册到微信
        msgApi.registerApp(mWeiXinKey);
        //此app暂无申请微信支付的权限
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private String getAmount(int type) {
        String amount;
        if (type == 0) {
            amount = "0";
        } else if (type==120){
            amount = "12";
        }else {
            amount = type + "";
        }
        return amount;
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
                Web.start(PayOrderActivity.this,jumpUrl,title);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, termIndex, termIndex + vipStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    private String getProductId(String str) {
        String productId = "";
        if (str.equals("走遍美国永久vip")||str.equals("走遍美国一年vip")) {
            productId = "10";//本应用永久会员都为10
        } else if (str.equals("全站vip")) {
            productId = "0";
        } else if (str.equals("黄金会员")) {
            productId = "22";//21是新概念的，不能购买黄金会员了
        }

        /*if (type == 0) {
            productId = "10";
        } else {
            productId = "0";
        }*/
        return productId;
    }

    private void findView() {
        button = (Button) findViewById(R.id.btn_back);
        mTvVipInfo =findViewById(R.id.tv_vip_info);
        mTvVipInfo.setText(body);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        payorder_username = findViewById(R.id.payorder_username_tv);
        payorder_username.setText(UserInfoManager.getInstance().getUserName());
        payorder_rmb_amount = findViewById(R.id.payorder_rmb_amount_tv);
        payorder_rmb_amount.setText(price + "元");
        methodList = findViewById(R.id.payorder_methods_lv);
        payorder_submit_btn = findViewById(R.id.payorder_submit_btn);
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
        payorder_submit_btn.setOnClickListener(view -> {
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(this);
                return;
            }


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
                        startLoading("正在获取支付信息～");
                        payByAlipay(newbody, newSubject);
                        break;
                    case PayMethodAdapter.PayMethod.WEIXIN:
                        if (msgApi.isWXAppInstalled()) {
                            startLoading("正在获取支付信息～");
                            payByWeiXin();
                        } else {
                            ToastUtil.showToast(mContext, "您还未安装微信客户端");
                        }
                        break;
                  /*  case PayMethodAdapter.PayMethod.BANKCARD:
                        payByWeb();
                        break;*/
                    default:
                        ToastUtil.showToast(this,"暂未支持当前支付功能");
                        break;
                }
            }
        });

        //设置vip支付协议
        vipAgreement = findViewById(R.id.vip_agreement);
        vipAgreement.setText(setVipAgreement());
        vipAgreement.setMovementMethod(new LinkMovementMethod());
    }

    //支付宝支付
    private Disposable aliPayDis;
    private void payByAlipay(String body, String subject) {
        confirmMutex = true;
        /*RequestCallBack rc = new RequestCallBack() {
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
                            PayTask alipay = new PayTask(PayOrderActivity.this);
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
        CrashApplication.getInstance().getQueue().add(orderRequest);*/

        //这里需要更换支付宝支付的链接
        LessonRemoteManager.getAlipay(UserInfoManager.getInstance().getUserId(), amount,productId,subject,price,body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Alipay_info>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        aliPayDis = d;
                    }

                    @Override
                    public void onNext(Alipay_info bean) {
                        if (bean!=null&&bean.getResult().equals("200")){
                            payorder_submit_btn.setClickable(true);
                            confirmMutex = true;

                            //调用支付宝app
                            Observable.create(new ObservableOnSubscribe<Pair<Boolean,String>>() {
                                @Override
                                public void subscribe(ObservableEmitter<Pair<Boolean, String>> emitter) throws Exception {
                                    String payInfo = bean.getAlipayTradeStr();
                                    PayTask alipay = new PayTask(PayOrderActivity.this);
                                    Map<String, String> result = alipay.payV2(payInfo, true);
                                    LibAliPayResultUtil payResult = new LibAliPayResultUtil(result);
                                    if (payResult!=null&&payResult.getResultStatus().equals("9000")){
                                        emitter.onNext(new Pair<>(true,payResult.getResultStatus()));
                                    }else {
                                        emitter.onNext(new Pair<>(false,payResult.getResultStatus()));
                                    }
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Pair<Boolean, String>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(Pair<Boolean, String> pair) {
                                            /*if (pair.first&&pair.second.equals("9000")){
                                                new AlertDialog.Builder(PayOrderActivity.this)
                                                        .setTitle("提示")
                                                        .setMessage("支付成功")
                                                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();

                                                                //刷新用户信息
                                                                refreshUserInfo();
                                                            }
                                                        })
                                                        .show();
                                            }else {
                                                if (TextUtils.equals(pair.second, "8000")) {
                                                    CustomToast.showToast(PayOrderActivity.this, "支付结果确认中", 1500);
                                                } else if (TextUtils.equals(pair.second, "6001")) {
                                                    CustomToast.showToast(PayOrderActivity.this, "您已取消支付", 1500);
                                                } else if (TextUtils.equals(pair.second, "6002")) {
                                                    CustomToast.showToast(PayOrderActivity.this, "网络连接出错", 1500);
                                                } else {
                                                    // 其他值就可以判断为支付失败，或者系统返回的错误
                                                    CustomToast.showToast(PayOrderActivity.this, "支付失败", 1500);
                                                }
                                            }*/

                                            stopLoading();
                                            showAliPayStatus(pair.second);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            stopLoading();
                                            showPayFailDialog("支付出现异常，请重试");
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }else {
                            stopLoading();
                            showPayFailDialog("获取支付订单失败，请重试("+bean.getResult()+")");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        showPayFailDialog("获取支付订单异常，请重试("+e.getMessage()+")");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(aliPayDis);
                    }
                });
    }

    private void payWxByWeb(){
        Log.e(TAG, "payWxByWeb: "+price);
        String sign = "iyubaPay" + price + amount + productId + (System.currentTimeMillis())/1000/(24*60*60);
        String url = "http://m.iyuba.cn/weixinPay/appWeiXinPay.html?uid="+
        UserInfoManager.getInstance().getUserId() +
                "&money="+price+"&amount="+amount+"&appid="+
                Constant.APPID+"&productid="+productId+
                "&weichatId=wx776141d0d2778905&sign="+ MD5.getMD5ofStr(sign);
        Log.e(TAG, "payWxByWeb: "+" sign:"+sign+" url:"+url);
        Web.start(mContext, url, "开通会员");
    }

    //拉起微信的微信支付
    private void payByWeiXin() {
        confirmMutex = true;
        RequestCallBack rc = result -> {
            OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
            if (first.isRequestSuccessful()) {
                PayReq req = new PayReq();
                req.appId = mWeiXinKey;
                req.partnerId = first.partnerId;
                req.prepayId = first.prepayId;
                req.nonceStr = first.nonceStr;
                req.timeStamp = first.timeStamp;
                req.packageValue = "Sign=WXPay";
                req.sign = buildWeixinSign(req, first.mchkey);
                msgApi.sendReq(req);
            } else {
                validateOrderFail();
            }
        };

        try {
            //两次加码，因为一次会出现乱码
            body = URLEncoder.encode(body, "UTF-8");
            body = URLEncoder.encode(body, "UTF-8");
            subject = URLEncoder.encode(subject, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }

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
        ToastUtil.showToast(mContext, "服务器正忙,请稍后再试!");
        PayOrderActivity.this.finish();
        /* new AlertDialog.Builder(PayOrderActivity.this)
                .setTitle("订单异常!")
                .setMessage("订单验证失败!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PayOrderActivity.this.finish();
                    }
                })
                .show();*/
    }

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
//            ToastUtil.showToast(mContext, "订单异常!");
//            PayOrderActivity.this.finish();
            new AlertDialog.Builder(PayOrderActivity.this)
                    .setTitle("订单提交出现问题!")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmMutex = true;
                            dialog.dismiss();
                            PayOrderActivity.this.finish();
                        }
                    })
                    .show();
        }
    };

    /******************************回调********************************/
    //微信支付回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(WXPayResultEvent event) {
        /*switch (event.getCode()) {
            case 0:
                //更新微课直购的状态
                IMooc.notifyCoursePurchased();
                //刷新用户信息
                refreshUserInfo();
                break;
            case 1:
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("支付错误")
                        .setPositiveButton("确定", null).show();
                break;
            case -2:
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("用户取消支付\n")
                        .setPositiveButton("确定", null).show();
                break;
            default:
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("支付失败")
                        .setPositiveButton("确定", null).show();
                break;
        }*/
        stopLoading();

        int errCode = event.getResultCode();
        switch (errCode) {
            case -2:
                //取消支付
                showPayFailDialog("取消支付");
                break;
            case 0://支付成功
                showPayUnknownDialog();
                break;
            default://其他情况
                showPayUnknownDialog();
                break;
        }
    }

    //支付宝支付回调
    public void showAliPayStatus(String payStatus) {
        stopLoading();

        switch (payStatus) {
            case AliPayResultEvent.Code.SUCCESS:
                //成功
                showPayUnknownDialog();
                break;
            case AliPayResultEvent.Code.IN_CONFIRMATION:
                showPayFailDialog(AliPayResultEvent.Message.IN_CONFIRMATION);
                break;
            case AliPayResultEvent.Code.CANCELED:
                showPayFailDialog(AliPayResultEvent.Message.CANCELED);
                break;
            case AliPayResultEvent.Code.NET_ERROR:
                showPayFailDialog(AliPayResultEvent.Message.NET_ERROR);
                break;
            default:
                showPayUnknownDialog();
                break;
        }
    }


    /************************新的操作***********************/
    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        if (!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //支付链接状态弹窗
    private void showPayLinkStatusDialog(String statusMsg) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(statusMsg)
                .setPositiveButton("确定", null)
                .create().show();
    }

    //支付状态弹窗
    private void showPayStatusDialog(boolean isFinish, String showMsg) {
        if (!isFinish) {
            showPayLinkStatusDialog(showMsg);
            return;
        }

        if (!TextUtils.isEmpty(showMsg)){
            showPayFailDialog(showMsg);
            return;
        }

        showPayUnknownDialog();
    }

    //支付失败状态弹窗
    private void showPayFailDialog(String showMsg) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(showMsg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false)
                .create().show();
    }

    //支付中间状态弹窗
    private void showPayUnknownDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("是否支付完成\n\n(如会员、课程未生效，请退出后重新登录)")
                .setPositiveButton("已完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getUserInfo();
                    }
                }).setNegativeButton("未完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUserInfo();
            }
        }).setCancelable(false)
                .create().show();
    }

    //获取用户信息
    private void getUserInfo(){
        startLoading("正在更新用户信息～");

        //这里延迟1s后刷新用户信息，便于服务端合并数据
        LibRxTimer.getInstance().timerInMain("delayTime", 1000L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                LibRxTimer.getInstance().cancelTimer("delayTime");
                UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
                    @Override
                    public void onSuccess() {
                        stopLoading();
                        //回调信息(后面直接刷新了，这里不用操作)
//                        EventBus.getDefault().post(new LoginEvent());
                        //刷新微课信息
                        if (subject.equals("微课直购")){
                            IMooc.notifyCoursePurchased();
                        }

                        finish();
                    }

                    @Override
                    public void onFail(String errorMsg) {
                        stopLoading();
                        finish();
                    }
                });
            }
        });
    }
}
