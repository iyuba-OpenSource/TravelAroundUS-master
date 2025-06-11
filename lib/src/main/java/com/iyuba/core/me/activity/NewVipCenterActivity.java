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
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TabHost;
//import android.widget.TextView;
//
//import androidx.core.content.ContextCompat;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.thread.GitHubImageLoader;
//import com.iyuba.core.common.widget.MyGridView;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.lil.user.util.LoginUtil;
//import com.iyuba.core.me.adapter.MyGridAdapter;
//import com.iyuba.core.me.pay.PayOrderActivity;
//import com.iyuba.lib.R;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.Random;
//
///**
// * 新版本VIP中心, 被弃用！！！
// * <p>
// * 2019。01。24 修改过
// */
//public class NewVipCenterActivity extends BasisActivity {
//    private Context mContext;
//    private TextView tv_username;
//    private TextView tv_iyucoin;
//    private RelativeLayout rl_buyforevervip;
//    private MyGridView gv_tequan;
//    private TextView localVip, goldVip;
//    private String username;
//    private String iyubi;
//    private CustomDialog wettingDialog;
//    private Button quarter;
//    private Button month;
//    private Button half_year;
//    private Button year;
//    private Button threeyear;
//    private Button lifelong;
//    private Button only12;
//    private Button iv_back;
//    private TabHost th;
//    private ContextCompat contextCompat;
//    private TextView tv_vip_html;
//
//    private double price;
//
//    private Button goldMonth, goldThreeMonth, goldsixMonth, goldYear;
//
//    private TextView tv_user_name, tv_aiyubi, tv_time;
//    private ImageView circularImageView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.update_vip);
//        CrashApplication.getInstance().addActivity(this);
//        mContext = this;
//        //contextCompat = new ContextCompat();
//
//      /*  Bundle extra = getIntent().getExtras();
//        username = extra.getString("username");
//        iyubi = extra.getString("iyubi");
//        if(username==null )
//            username = AccountManager.Instance(this).userName;
//        if(username!=null&&"".equals(username)){
//            username = AccountManager.Instance(this).userId;
//        }
//        if(iyubi==null){
//            if(AccountManager.Instance(this).userInfo!=null)
//                iyubi = AccountManager.Instance(this).userInfo.iyubi;
//        }*/
//
//        initView();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        username = UserInfoManager.getInstance().getUserName();
//        iyubi = String.valueOf(UserInfoManager.getInstance().getIyuIcon());
//
//        tv_username.setText(username);
//        tv_iyucoin.setText(iyubi);
//
//
//        tv_user_name.setText(username);
//        tv_aiyubi.setText("爱语币余额：" + iyubi);
//        if (ConfigManager.Instance().loadInt("isvip") >= 1) {
//            tv_time.setText("VIP到期时间：" + AccountManager.Instance(mContext).userInfo.deadline);
//        } else {
//            tv_time.setText("未开通VIP");
//        }
//
//    }
//
//    private void initView() {
//        //在布局中隐藏了黄金会员
//        wettingDialog = WaittingDialog.showDialog(mContext);
//        iv_back = (Button) findViewById(R.id.iv_back);
//        iv_back.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NewVipCenterActivity.this.finish();
//            }
//        });
//        tv_username = (TextView) findViewById(R.id.tv_username);
//
//        tv_iyucoin = (TextView) findViewById(R.id.tv_iyucoin);
//        btn_buyiyuba = (Button) findViewById(R.id.btn_buyiyuba);
//        tv_vip_html = (TextView) findViewById(R.id.tv_vip_html);
//
//
//        tv_aiyubi = (TextView) findViewById(R.id.tv_aiyubi);
//        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
//        tv_time = (TextView) findViewById(R.id.tv_time);
//        circularImageView = (ImageView) findViewById(R.id.image_head);
////
//
//        GitHubImageLoader.Instace(mContext).setCirclePic(
//                AccountManager.Instance(mContext).userId, circularImageView);
//
//        tv_vip_html.setOnClickListener(ocl);
//        btn_buyiyuba.setOnClickListener(ocl);
//        th = (TabHost) findViewById(R.id.tabhost);
//
//        month = (Button) findViewById(R.id.btn_buyapp1);
//        quarter = (Button) findViewById(R.id.btn_buyapp2);
//        half_year = (Button) findViewById(R.id.btn_buyapp3);
//        year = (Button) findViewById(R.id.btn_buyapp4);
//
//
//        goldMonth = (Button) findViewById(R.id.btn_buyapp6);
//        goldThreeMonth = (Button) findViewById(R.id.btn_buyapp7);
//        goldsixMonth = (Button) findViewById(R.id.btn_buyapp8);
//        goldYear = (Button) findViewById(R.id.btn_buyapp9);
//
//        goldMonth.setOnClickListener(ocl);
//        goldThreeMonth.setOnClickListener(ocl);
//        goldsixMonth.setOnClickListener(ocl);
//        goldYear.setOnClickListener(ocl);
//
//        threeyear = (Button) findViewById(R.id.btn_buyapp5);
//        gv_tequan = (MyGridView) findViewById(R.id.view1);
////        localVip = (MyGridView) findViewById(R.id.view2);
//        localVip = (TextView) findViewById(R.id.view2);
//        goldVip = (TextView) findViewById(R.id.view3);
//        lifelong = (Button) findViewById(R.id.rl_buyforevervip);
//        only12 = (Button) findViewById(R.id.rl_buyforevervip_12);
//
//
//        month.setOnClickListener(ocl);
//        quarter.setOnClickListener(ocl);
//        half_year.setOnClickListener(ocl);
//        year.setOnClickListener(ocl);
//        threeyear.setOnClickListener(ocl);
//        lifelong.setOnClickListener(ocl);
//        only12.setOnClickListener(ocl);
//
//        th.setup();
//        th.addTab(th.newTabSpec("tab1").setIndicator(composeLayout("全站vip", R.drawable.all_vip)).setContent(R.id.view1));
//        th.addTab(th.newTabSpec("tab2").setIndicator(composeLayout("本应用永久vip", R.drawable.forever_vip)).setContent(R.id.view2));
//        th.addTab(th.newTabSpec("tab3").setIndicator(composeLayout("黄金vip", R.drawable.gold_vip)).setContent(R.id.view3));
//        final MyGridAdapter mga = new MyGridAdapter(mContext);
//        gv_tequan.setAdapter(mga);
//        gv_tequan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String hint = mga.getHint(i);
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage(hint);
//                builder.setTitle("权限介绍");
//                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//                builder.create().show();
//            }
//        });
//
////        final MyGridAdapter mgam = new MyGridAdapter(mContext);
////        String[] imageText = new String[]{"无广告", "一键换肤", "难度分类"};
////        int[] image = new int[]{R.drawable.tequan1, R.drawable.skin_vip, R.drawable.difficulty_vip};
////        String[] imageHint = new String[]{"应用无广告，只为学习生", "皮肤随心情，色彩由你定", "难度筛文章，更加针对性"};
////        mgam.setImageText(imageText);
////        mgam.setImgs(image);
////        mgam.setHint(imageHint);
////        localVip.setAdapter(mgam);
////        localVip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                String hint = mgam.getHint(i);
////                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
////                builder.setMessage(hint);
////                builder.setTitle("权限介绍");
////                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                    }
////                });
////                builder.create().show();
////            }
////        });
//       /* localVip.setText("1.普通用户有广告，VIP会员应用无广告\n" +
//                "2.普通用户评论无标识，VIP会员专享尊贵标识\n" +
//                "3.普通用户不能使用智慧化评测功能，VIP会员可以对自身水平进行智慧化评测\n" +
//                "4.普通用户不能进行语音调速，VIP会员可以自由选择音频播放速度\n" +
//                "5.普通用户积分只能兑换VIP，VIP会员的积分可以兑换纸质书电话费等\n" +
//                "6.本应用永久VIP仅限Android平台新概念英语应用使用\n" +
//                "7.普通用户不能下载看一看里面的视频，VIP会员可以无限制下载\n" +
//                "8.更多本应用VIP功能，Coming soon.");*/
//
//
//        localVip.setText("1. 尊贵V标识,智能化学习与评测全部永久开放\n" +
//                "2. 同享全站会员无广告、高速下载、语音调速、换话费等特权\n" +
//                "3. 看一看 美剧无限下载\n" +
//                "4. 搜一搜 音频视频无限下载(微课除外)\n" +
//                "5. 永久VIP仅限Android平台走遍美国使用(不含微课)\n" +
//                "6. VIP更多功能，敬请期待");
//
//        goldVip.setText("敬请期待");//新概念名师团队
////        "1.免费开通全部微课资源\n" +
////                "2.尊享智慧化学习系统：随时随地刷习题\n" +
////                "3.尊享爱语吧旗下全站会员特权"
//        View view = th.getTabWidget().getChildAt(0);
////        view.setBackgroundColor(0xc3c8f9);
////        view.setBackgroundResource(R.drawable.boy);
//        view.setBackgroundColor(0xFFAFEEEE);
////        th.getCurrentTabView().setBackgroundColor(0xAFEEEE);
//        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                Log.e("tabid", tabId);
//                View view;
//                switch (tabId) {
//                    case "tab1":
//                        view = th.getTabWidget().getChildAt(0);
//                        view.setBackgroundColor(0xFFE2F3FF);
//                        view = th.getTabWidget().getChildAt(1);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        view = th.getTabWidget().getChildAt(2);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        break;
//                    case "tab2":
//                        view = th.getTabWidget().getChildAt(0);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        view = th.getTabWidget().getChildAt(1);
//                        view.setBackgroundColor(0xFFE2F3FF);
//                        view = th.getTabWidget().getChildAt(2);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        break;
//                    case "tab3":
//                        view = th.getTabWidget().getChildAt(0);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        view = th.getTabWidget().getChildAt(1);
//                        view.setBackgroundColor(0x00FFFFFF);
//                        view = th.getTabWidget().getChildAt(2);
//                        view.setBackgroundColor(0xFFE2F3FF);
//                        break;
//                }
//            }
//        });
////        th.getTabWidget().setStripEnabled(true);
//    }
//
//
//    private void buyVip(int month) {
//        Intent intent = new Intent(mContext, PayOrderActivity.class);
//        price = getSpend(month);
//        //price =0.01;
//
////        handler.sendEmptyMessage(3);
//        intent.putExtra("type", month);
//        intent.putExtra("out_trade_no", getOutTradeNo());
//        if (month == 0) {
//            intent.putExtra("subject", "走遍美国永久vip");//走遍美国永久vip
//        } else if (month == 120) {
//            intent.putExtra("subject", "走遍美国一年vip");//走遍美国永久vip
//        } else {
//            intent.putExtra("subject", "全站vip");
//        }
//        intent.putExtra("body", "花费" + price + "元购买全站vip");
//        intent.putExtra("price", price + "");  //价格
//        startActivity(intent);
////        if (month != 0) {        //全站vip
////			PayManager.Instance(mContext).payAmount(
////					AccountManager.Instance(mContext).userId, getSpend(month),
////					month, new OperateCallBack() {
////
////						@Override
////						public void success(String message) {
////							// TODO Auto-generated method stub
////							Message hmsg = handler.obtainMessage(2, message);// 对话框提示支付成功
////							handler.sendMessage(hmsg);
////							ConfigManager.Instance().putBoolean("isvip", true);
////						}
////
////						@Override
////						public void fail(String message) {
////							// TODO Auto-generated method stub
////							handler.sendEmptyMessage(1);
////						}
////					});
//
////        } else {        //永久vip
////			PayManager.Instance(mContext).payAmount(
////					AccountManager.Instance(mContext).userId, Constant.price,
////					new OperateCallBack() {
////
////						@Override
////						public void success(String message) {
////							// TODO Auto-generated method stub
////							Message hmsg = handler.obtainMessage(2, message);// 对话框提示支付成功
////							handler.sendMessage(hmsg);
////							ConfigManager.Instance().putBoolean("isvip", true);
////						}
////
////						@Override
////						public void fail(String message) {
////							// TODO Auto-generated method stub
////							handler.sendEmptyMessage(1);
////						}
////					});
//
//    }
//
//    public double getSpend(int month) {
//        double result = 199;
////        double result = 0.01;
//        switch (month) {
//            case 1:
//                result = 30;
//                break;
//            case 3:
//                result = 88;
//                break;
//            case 6:
//                result = 158;
//                break;
//            case 12:
//                result = 298;
//                break;
//            case 36:
//                result = 588;//588
//                break;
//            case 120:
//                result = 99;
//                break;
//        }
//        return result;
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(final Message msg) {
//            super.handleMessage(msg);
//            Dialog dialog;
//            switch (msg.what) {
//                case 0:
//                    if (UserInfoManager.getInstance().isLogin()) {
//                        final int month = msg.arg1;
//                        buyVip(month);
//                    } else {
////                        Intent intent = new Intent();
////                        intent.setClass(mContext, Login.class);
////                        startActivity(intent);
//                        LoginUtil.startToLogin(mContext);
//                    }
//
////                    String content1 = mContext
////                            .getString(R.string.alert_buy_content1);
////                    String content2 = mContext
////                            .getString(R.string.alert_buy_content2);
////                    StringBuffer sb = new StringBuffer();
////                    if (month == 0) {
////                        sb.append(content1).append(Constant.price).append(content2);
////                    } else {
////                        sb.append(content1).append(getSpend(month))
////                                .append(content2);
////                    }
////                    dialog = new AlertDialog.Builder(mContext)
////                            .setIcon(android.R.drawable.ic_dialog_alert)
////                            .setTitle(R.string.alert_title)
////                            .setMessage(sb.toString())
////                            .setPositiveButton(R.string.alert_btn_buy,
////                                    new DialogInterface.OnClickListener() {
////                                        public void onClick(DialogInterface dialog,
////                                                            int whichButton) {
////                                            buyVip(month);
////                                        }
////                                    })
////                            .setNeutralButton(R.string.alert_btn_cancel, null)
////                            .create();
////                    dialog.show();// 如果要显示对话框，一定要加上这句
//                    break;
//                case 1:
//                    wettingDialog.dismiss();
//                    dialog = new AlertDialog.Builder(mContext)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setTitle(R.string.alert_title)
//                            .setMessage(R.string.alert_recharge_content)
//                            .setPositiveButton(R.string.alert_btn_recharge,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int whichButton) {
//                                            buyIyubi();
//                                        }
//                                    })
//                            .setNeutralButton(R.string.alert_btn_cancel, null)
//                            .create();
//                    dialog.show();// 如果要显示对话框，一定要加上这句
//                    break;
//                case 2:
//                    wettingDialog.dismiss();
//                    CustomToast.showToast(mContext, R.string.buy_success_update);
//                    tv_iyucoin.setText(msg.obj.toString());
//                    break;
//                case 3:
//                    wettingDialog.show();
//                    break;
//                case 4:
//                    wettingDialog.dismiss();
//                    dialog = new AlertDialog.Builder(mContext)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setTitle(R.string.alert_title)
//                            .setMessage(R.string.alert_all_life_vip)
//                            .setPositiveButton(R.string.alert_btn_recharge,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,
//                                                            int whichButton) {
//                                            buyIyubi();
//                                        }
//                                    })
//                            .setNeutralButton(R.string.alert_btn_cancel, null)
//                            .create();
//                    dialog.show();// 如果要显示对话框，一定要加上这句
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//    private OnClickListener ocl = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            /*
//             * if (AccountManager.Instance(mContext).userInfo.deadline
//             * .equals("终身VIP")) { handler.sendEmptyMessage(4); } else
//             */
//            if (v == month) {
////				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 200) {
////					handler.obtainMessage(0, 1, 0).sendToTarget();
////				} else {
////					handler.sendEmptyMessage(1);
////				}
//                handler.obtainMessage(0, 1, 0).sendToTarget();
//            } else if (v == quarter) {
////				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 600) {
////					handler.obtainMessage(0, 3, 0).sendToTarget();
////				} else {
////					handler.sendEmptyMessage(1);
////				}
//                handler.obtainMessage(0, 3, 0).sendToTarget();
//            } else if (v == half_year) {
////				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 1000) {
////					handler.obtainMessage(0, 6, 0).sendToTarget();
////				} else {
////					handler.sendEmptyMessage(1);
////				}
//                handler.obtainMessage(0, 6, 0).sendToTarget();
//            } else if (v == year) {
////				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 2000) {
////					handler.obtainMessage(0, 12, 0).sendToTarget();
////				} else {
////					handler.sendEmptyMessage(1);
////				}
//                handler.obtainMessage(0, 12, 0).sendToTarget();
//            } else if (v == threeyear) {
////				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= 2000) {
////					handler.obtainMessage(0, 12, 0).sendToTarget();
////				} else {
////					handler.sendEmptyMessage(1);
////				}
//                handler.obtainMessage(0, 36, 0).sendToTarget();
//            } else if (v == lifelong) {
////                double amount = Constant.price;
////				if (!iyubi.equals("") && Integer.parseInt(iyubi) >= amount) {
////					handler.obtainMessage(0, 0, 0).sendToTarget();
////				} else {
////					handler.sendEmptyMessage(1);
////				}
//                handler.obtainMessage(0, 0, 0).sendToTarget();
//            } else if (v == only12) {
//                handler.obtainMessage(0, 120, 0).sendToTarget();
//            } else if (v == btn_buyiyuba) {
//                buyIyubi();
//            } else if (v == goldMonth) {
//
//                buyGoldVip(1);
//            } else if (v == goldThreeMonth) {
//                buyGoldVip(3);
//            } else if (v == goldsixMonth) {
//                buyGoldVip(6);
//            } else if (v == goldYear) {
//                buyGoldVip(12);
//            } else if (v == tv_vip_html) {
//
//
//                Intent intent = new Intent(NewVipCenterActivity.this, Web.class);
//
//                intent.putExtra("url", "http://vip." + Constant.IYBHttpHead() + "/vip/vip.html");
//                intent.putExtra("title", "全站VIP");
//                startActivity(intent);
//            }
//
//
//        }
//    };
//    private Button btn_buyiyuba;
//
//    private void buyIyubi() {
//        Intent intent = new Intent();
//        intent.setClass(mContext, BuyIyubiActivity.class);
//        intent.putExtra("url", "http://app." + Constant.IYBHttpHead() + "/wap/index.jsp?uid="
//                + AccountManager.Instance(mContext).userId + "&appid="
//                + Constant.APPID);
////		Log.e("NewVipCenterActivity url ",""+"http://app."+Constant.IYBHttpHead+"/wap/index.jsp?uid="
////				+ AccountManager.Instance(mContext).userId + "&appid="
////				+ Constant.APPID);
//        intent.putExtra("title", Constant.APPName);
//        startActivity(intent);
//    }
//
//    private String getOutTradeNo() {
//        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
//        Date date = new Date();
//        String key = format.format(date);
//        Random r = new Random();
//        key = key + Math.abs(r.nextInt());
//        key = key.substring(0, 15);
//        return key;
//    }
//
//    public View composeLayout(String s, int i) {
//        LinearLayout layout = new LinearLayout(this);
//        layout.setGravity(Gravity.CENTER_HORIZONTAL);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        ImageView iv = new ImageView(this);
//        iv.setImageResource(i);
//        iv.setAdjustViewBounds(true);
//        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, 15, 0, 0);
//
//        layout.addView(iv, lp);
//        TextView tv = new TextView(this);
//        tv.setGravity(Gravity.CENTER);
//        tv.setSingleLine(true);
//        tv.setText(s);
//        tv.setTextColor(0xFF598aad);
//        tv.setTextSize(14);
//        LinearLayout.LayoutParams lpo = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        lpo.setMargins(0, 0, 0, 15);
//        layout.addView(tv, lpo);
//        return layout;
//    }
//
//    private String goldPrice;
//
//    private void buyGoldVip(int vip_type) {
//
//
//        if (AccountManager.Instance(mContext).checkUserLogin() && !AccountManager.Instance(mContext).islinshi) {
//
//
//            Intent intent = new Intent();
//            String subject;
//            String body;
//            subject = "黄金会员";
//
//            switch (vip_type) {
//                case 1:
//                    goldPrice = "98";
//                    break;
//                case 3:
//                    goldPrice = "288";
//                    break;
//                case 6:
//                    goldPrice = "518";
//                    break;
//                case 12:
//                    goldPrice = "998";//998
//                    break;
//            }
//
//
//            body = Constant.APPName + "-" + "花费" + goldPrice + "元购买" + Constant.APPName + "黄金会员";
//            intent.setClass(this, PayOrderActivity.class);
//            intent.putExtra("type", vip_type);
//            intent.putExtra("out_trade_no", getOutTradeNo());
//            intent.putExtra("subject", subject);
//            intent.putExtra("body", body);
//            intent.putExtra("price", goldPrice);
//            startActivity(intent);
//        } else {
////            Intent intent = new Intent();
////            intent.setClass(mContext, Login.class);
////            startActivity(intent);
//            LoginUtil.startToLogin(mContext);
//        }
//
//    }
//}
