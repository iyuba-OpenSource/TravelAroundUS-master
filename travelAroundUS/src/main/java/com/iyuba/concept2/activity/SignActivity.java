package com.iyuba.concept2.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.iyuba.concept2.R;
import com.iyuba.concept2.protocol.AddScoreRequest;
import com.iyuba.concept2.protocol.AddScoreResponse;
import com.iyuba.concept2.protocol.SignRequest;
import com.iyuba.concept2.protocol.SignResponse;
import com.iyuba.concept2.sqlite.mode.SignBean;
import com.iyuba.concept2.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.Base64Coder;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * 打卡页面
 */

public class SignActivity extends Activity {


    private ImageView imageView;
    private ImageView qrImage;
    private TextView tv1, tv2, tv3;
    private Context mContext;
    private TextView sign;
    private ImageView userIcon;
    private TextView tvShareMsg;
    private int signStudyTime = 3 * 60;
    private String loadFiledHint = "打卡加载失败";

    String shareTxt;
    LinearLayout ll;
    CustomDialog mWaittingDialog;
    String addCredit = "";//Integer.parseInt(bean.getAddcredit());
    String days = "";//Integer.parseInt(bean.getDays());
    String totalCredit = "";//bean.getTotalcredit();
    String money = "";
//    private int QR_HEIGHT = 77;
//    private int QR_WIDTH = 77;

    private TextView tvUserName;
    private TextView tvAppName;
    private TextView tv_finish;

    private ImageView btn_close;
    private MaterialDialog dialog, dialog_share;

    //是否已经打卡
    private boolean isSign = false;

    private String qrImgUrl = "";
    String uid= "";

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;

        mWaittingDialog = WaittingDialog.showDialog(SignActivity.this);
        mWaittingDialog.setTitle("请稍后");
        setContentView(R.layout.activity_sign);

        //状态栏处理
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {//android手机小于5.0的直接全屏显示，防止截图留白边
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        initView();

        initData();

        initBackGround();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initData() {

        mWaittingDialog.show();

        uid = String.valueOf(UserInfoManager.getInstance().getUserId());

        ExeProtocol.exe(
                new SignRequest(String.valueOf(UserInfoManager.getInstance().getUserId())),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {


                        SignResponse response = (SignResponse) bhr;
                        try {
                            if (null != mWaittingDialog) {
                                if (mWaittingDialog.isShowing()) {
                                    mWaittingDialog.dismiss();
                                }
                            }
                            final StudyTimeBeanNew bean = new Gson().fromJson(response.jsonObjectRoot.toString(), StudyTimeBeanNew.class);
                            if ("1".equals(bean.getResult())) {
                                Message message=new Message();
                                message.obj=bean;
                                message.what=2;
                                handler.sendMessage(message);
                            } else {
                                toast(loadFiledHint + bean.getResult());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast(loadFiledHint + "！！");
                        }
                    }

                    @Override
                    public void error() {
                        toast("Error!");
                    }
                });


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    sign.setOnClickListener(v -> {
                        qrImage.setVisibility(View.VISIBLE);
                        sign.setVisibility(View.GONE);
                        tvShareMsg.setVisibility(View.VISIBLE);
                        tv_finish.setVisibility(View.VISIBLE);
                        tvShareMsg.setText("长按图片识别二维码");
                        tvShareMsg.setBackground(getResources().getDrawable(R.drawable.sign_bg_yellow));
                        writeBitmapToFile();
                        showShareOnMoment(mContext, String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.APPID);
                    });
                    break;
                case 2:
                    StudyTimeBeanNew bean=(StudyTimeBeanNew)msg.obj;

                    Log.e("TAG", "handleMessage: "+bean.toString());

                    final int time = Integer.parseInt(bean.getTotalTime());

                    tv1.setText(bean.getTotalDays() + ""); //学习天数
                    tv2.setText(bean.getTotalWord() + "");//今日单词
                    int nowRank = Integer.parseInt(bean.getRanking());
                    double allPerson = Double.parseDouble(bean.getTotalUser());
                    double carry;
                    String over = null;
                    if (allPerson != 0) {
                        carry = 1 - nowRank / allPerson;
                        DecimalFormat df = new DecimalFormat("0.00");
                        Log.e("百分比", df.format(carry) + "--" + nowRank + "--" + allPerson);

                        over = df.format(carry).substring(2, 4);
                    }

                    tv3.setText(over + "%同学"); //超越了
                    shareTxt = bean.getSentence() + "我在爱语吧坚持学习了" + bean.getTotalDays() + "天,积累了" + bean.getTotalWords()
                            + "单词如下";
                    qrImgUrl = "http://app.iyuba.cn/android/androidDetail.jsp?u="+uid+"&id="+Constant.APPID+"&f="+bean.getShareId();
//                    Glide.with(mContext).load().placeholder(R.drawable.qr_code).into(qrImage);
                    Bitmap bitmap = generateQRCode(qrImgUrl, 300, 300);
                    qrImage.setImageBitmap(bitmap);
                    //子线程
                    handler.sendEmptyMessage(1);
                    break;
            }
        }
    };

    private void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 生成固定大小的二维码(不需网络权限)
     *
     * @param content 需要生成的内容
     * @param width   二维码宽度
     * @param height  二维码高度
     * @return
     */
    public static Bitmap generateQRCode(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initView() {

        imageView = findViewById(R.id.iv);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        sign = findViewById(R.id.tv_sign);
        ll = findViewById(R.id.ll);
        qrImage = findViewById(R.id.tv_qrcode);
        userIcon = findViewById(R.id.iv_userimg);
        tvUserName = findViewById(R.id.tv_username);
        tvAppName = findViewById(R.id.tv_appname);
        tvShareMsg = findViewById(R.id.tv_sharemsg);

        btn_close = findViewById(R.id.btn_close);
        tv_finish = findViewById(R.id.tv_finish);

        if (getPackageName().equals("com.iyuba.concept2")) {
            tv_finish.setText(" 刚刚在『" + "爱语吧走遍美国" + "』上完成了打卡");
        } else {
            tv_finish.setText(" 刚刚在『" + "走遍美国" + "』上完成了打卡");
        }

        tv_finish.setVisibility(View.INVISIBLE);

        //关闭打卡页面弹出提示
        dialog = new MaterialDialog(SignActivity.this);
        dialog.setTitle("温馨提示");
        dialog.setMessage("点击下边的打卡按钮，成功分享至微信朋友圈才算成功打卡，才能领取红包哦！确定退出么？");
        dialog.setPositiveButton("继续打卡", v -> dialog.dismiss());
        dialog.setNegativeButton("去意已决", v -> {
            dialog.dismiss();
            finish();
        });
        btn_close.setOnClickListener(v -> {
            if (!isSign){
                dialog.show();
            }else {
                finish();
            }
        });
        //当天再次打卡成功后显示
        dialog_share = new MaterialDialog(SignActivity.this);
        dialog_share.setTitle("提醒");
//        dialog_share.setMessage("今日已打卡，不能再次获取红包或积分哦！");
        dialog_share.setPositiveButton("好的", v -> {
            dialog_share.dismiss();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initBackGround() {

        int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
        String url = "http://staticvip." + Constant.IYBHttpHead() + "/images/mobile/" + day + ".jpg";

//      bitmap = returnBitMap(url);
        Glide.with(mContext).load(url).placeholder(R.drawable.sign_background).error(R.drawable.sign_background).into(imageView);
        final String userIconUrl = "http://api." + Constant.IYBHttpHead2() + "/v2/api.iyuba?protocol=10005&uid="
                + UserInfoManager.getInstance().getUserId() + "&size=middle";

        /*Glide.with(mContext)
                .load(userIconUrl)
                .asBitmap()  //这句不能少，否则下面的方法会报错
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .centerCrop()
                .into(new BitmapImageViewTarget(userIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });*/
        LibGlide3Util.loadCircleImg(mContext,userIconUrl,R.drawable.defaultavatar,userIcon);
        if (TextUtils.isEmpty(UserInfoManager.getInstance().getUserName())) {
            tvUserName.setText(String.valueOf(UserInfoManager.getInstance().getUserId()));
        } else {
            tvUserName.setText(UserInfoManager.getInstance().getUserName());
        }
        if (getPackageName().equals("com.iyuba.concept2")) {
            tvAppName.setText("走遍美国" + "--英语学习必备软件");
        } else {
            tvAppName.setText("走遍美国" + "--英语学习必备软件");
        }
    }

    public void writeBitmapToFile() {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            return;
        }
        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();

        File newpngfile = new File(getExternalFilesDir(""), "aaa.png");
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newpngfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block 小学英语
            e.printStackTrace();
        }
        tv_finish.setVisibility(View.GONE);
    }

    private void startInterfaceADDScore(String userID, String appid) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String time = Base64Coder.encode(dateString);
        ExeProtocol.exe(
                new AddScoreRequest(userID.trim(), appid.trim(), time.trim()),
                new ProtocolResponse() {
                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        //打卡完成
                        isSign = true;

                        AddScoreResponse response = (AddScoreResponse) bhr;

                        final SignBean bean = new Gson().fromJson(response.jsonObjectRoot.toString(), SignBean.class);
                        if (bean.getResult().equals("200")) {
                            money = bean.getMoney();
                            addCredit = bean.getAddcredit();
                            days = bean.getDays();
                            totalCredit = bean.getTotalcredit();
                            //打卡成功,您已连续打卡xx天,获得xx元红包,关注[爱语课吧]微信公众号即可提现!
                            runOnUiThread(() -> {
                                float moneyThisTime = Float.parseFloat(money);

                                if (moneyThisTime > 0) {
                                    float allmoney = Float.parseFloat(totalCredit);
                                    //这里直接获取20001接口刷新
                                    UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);

                                    dialog_share.setMessage("打卡成功," + "您已连续打卡" + days + "天,获得" + floatToString(moneyThisTime) + "元,总计: " + floatToString(allmoney) + "元," + "满十元可在\"爱语吧\"公众号提现");
                                    showDialog();

                                } else {

                                    dialog_share.setMessage("打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit);
                                    showDialog();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                dialog_share.setMessage("今日已打卡，重复打卡不能再次获取红包或积分哦！");
                                showDialog();
                            });
                        }
                    }
                    @Override
                    public void error() {
                        //打卡未完成
                        isSign = false;
                    }
                });
    }

    private void showDialog(){
        if (!isDestroyed()){
            dialog_share.show();
        }
    }

    public void showShareOnMoment(Context context, final String userID, final String AppId) {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
//        Bitmap bitmap = BitmapFactory.decodeResource(R.drawable.abc_ab_share_pack_holo_dark, )
        oks.setPlatform(WechatMoments.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setImagePath(getExternalFilesDir("") + "/aaa.png");
        oks.setSilent(true);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                startInterfaceADDScore(userID, AppId);
                tv_finish.setVisibility(View.GONE);
            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("--分享失败===", throwable.toString());
                tv_finish.setVisibility(View.GONE);
            }
            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("--分享取消===", "....");
                tv_finish.setVisibility(View.GONE);
            }
        });
        // 启动分享GUI
        oks.show(context);
    }

    private String floatToString(float fNumber) {
        fNumber = (float) (fNumber * 0.01);
        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

}



