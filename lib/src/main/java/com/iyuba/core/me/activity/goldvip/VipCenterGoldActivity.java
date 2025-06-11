package com.iyuba.core.me.activity.goldvip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.BrandUtil;
import com.iyuba.core.common.util.QQUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.lil.util.LibDateUtil;
import com.iyuba.core.me.activity.BuyIyubiActivity;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.lib.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class VipCenterGoldActivity extends AppCompatActivity {

    //vip类型
    private int vipType = 0;
    private static final String VIP_TYPE="vip_type";
    public static final int VIP_APP = 0;//本应用会员
    public static final int VIP_ALL = 1;//全站会员
    public static final int VIP_GOLD = 2;//黄金会员

    private ImageView userPhoto;
    private TextView tvUserName;
    private TextView tvVipTime;
    private TextView tvVipIntroduce;
    private TextView tvVipAll;
    private TextView tvVipOnly;
    private TextView tvVipGold;
    private GridView gvVip;
    private RecyclerView rvVipSelectList;
    private TextView tvGoBuy;
    private TextView tvFunction;
    private TextView tvBuyIyubi;
    private ImageButton ibBack;
    private TextView tvIyubi;

    //qq支持
    private ImageButton btnServe;

    private LinearLayout llFunction;

    private Context mContext;
    private int[] icon = {R.drawable.ic_vip_function1, R.drawable.ic_vip_function2,
            R.drawable.ic_vip_function3, R.drawable.ic_vip_function4, R.drawable.ic_vip_function5,
            R.drawable.ic_vip_function6, R.drawable.ic_vip_function7, R.drawable.ic_vip_function8,
            R.drawable.ic_vip_function9};
    private String[] iconName = {"无广告", "尊贵标识", "调节语速", "高速无限下载", "查看解析", "智慧化评测", "PDF导出",
            "全部应用", "换话费"};
    private String[] iconInfo = {"去除（开屏外）所有烦人的广告", "亮着VIP尊贵标识", "选择自由调节语速",
            "享受VIP高速通道，无限下载", "查看考试类所有试题答案解析", "享受智能化无限语音评测", "文章pdf无限导出",
            "使用app.iyuba.cn旗下所有APP", "积分商城换取不同价值手机充值卡"};
    private List<Map<String, Object>> data_list;
    private SimpleAdapter simAdapter;

    private BuyVIPAdapter mAdapter;

    private List<BuyVIPItem> siteVipItems;
    private List<BuyVIPItem> goldenVipItems;
    private List<BuyVIPItem> appVipItems;

    private String validity;
    private String iyubi;
    private int vipStatus;
    private static final String TAG = "VipCenterGoldActivity";

    public static void start(Context context,int type){
        Intent intent = new Intent();
        intent.setClass(context,VipCenterGoldActivity.class);
        intent.putExtra(VIP_TYPE,type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this,R.color.vip_toolbar);
        setContentView(R.layout.activity_vip_center_gold);

        mContext = this;

        initView();

        //这里需要进行判断，之前不知道为啥，直接没有判断
        vipType = getIntent().getIntExtra(VIP_TYPE,VIP_APP);
        if (vipType == VIP_ALL){
            tvVipAll.setSelected(true);
            tvVipOnly.setSelected(false);
            tvVipGold.setSelected(false);
        }else if (vipType == VIP_GOLD){
            tvVipAll.setSelected(false);
            tvVipOnly.setSelected(false);
            tvVipGold.setSelected(true);
        }else{
            tvVipAll.setSelected(false);
            tvVipOnly.setSelected(true);
            tvVipGold.setSelected(false);
        }

        llFunction.setVisibility(View.GONE);

        BuyVIPParser parser = new BuyVIPParser(this);
        siteVipItems = parser.parse(R.xml.buy_site_vip_items);
        goldenVipItems = parser.parse(R.xml.buy_golden_vip_items);
        appVipItems = parser.parse(R.xml.buy_app_vip_items);
        initListener();

//        //获取qq客服支持
//        BrandUtil.requestQQSupport();
//        String uid = AccountManager.Instance(mContext).userId;
//        BrandUtil.requestQQGroup(TextUtils.isEmpty(uid)?"0":uid);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        rvVipSelectList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BuyVIPAdapter();
        rvVipSelectList.setAdapter(mAdapter);

        if (vipType == VIP_ALL){
            mAdapter.setData(siteVipItems);

            gvVip.setVisibility(View.VISIBLE);
            llFunction.setVisibility(View.GONE);
            mAdapter.setData(siteVipItems);
            tvVipIntroduce.setText("VIP权限(不包含微课和训练营)");
        }else if (vipType == VIP_GOLD){
            mAdapter.setData(goldenVipItems);

            gvVip.setVisibility(View.GONE);
            llFunction.setVisibility(View.VISIBLE);
            tvFunction.setText(R.string.golden_vip_description);
            mAdapter.setData(goldenVipItems);
            tvVipIntroduce.setText("VIP权限");
        }else {
            mAdapter.setData(appVipItems);

            gvVip.setVisibility(View.GONE);
            llFunction.setVisibility(View.VISIBLE);
            tvFunction.setText(R.string.app_vip_description);
            mAdapter.setData(appVipItems);
            tvVipIntroduce.setText("VIP权限(不包含微课和训练营)");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefresh();//刷新数据
    }

    private void initView(){
        userPhoto=findViewById(R.id.user_photo);
        tvUserName=findViewById(R.id.tv_user_name);
        tvVipTime=findViewById(R.id.tv_vip_time);
        tvVipIntroduce=findViewById(R.id.tv_vip_introduce);
        tvVipAll=findViewById(R.id.tv_vip_all);
        tvVipOnly=findViewById(R.id.tv_vip_only);
        tvVipGold=findViewById(R.id.tv_vip_gold);
        gvVip=findViewById(R.id.gv_vip);
        rvVipSelectList=findViewById(R.id.rv_vip_select_list);
        tvGoBuy=findViewById(R.id.tv_go_buy);
        tvFunction=findViewById(R.id.tv_vip_function);
        tvBuyIyubi=findViewById(R.id.tv_buy_iyubi);
        llFunction=findViewById(R.id.ll_function);
        ibBack =findViewById(R.id.ib_back);
        tvIyubi=findViewById(R.id.tv_iyubi_number);

        btnServe = findViewById(R.id.btn_serve);
    }

    private void initListener() {

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        data_list = new ArrayList<Map<String, Object>>();
        String[] from = {"image", "text"};
        int[] to = {R.id.iv_icon, R.id.tv_text};
        getData();
        simAdapter = new SimpleAdapter(this, data_list, R.layout.item_gridview, from, to);
        gvVip.setAdapter(simAdapter);


        tvVipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvVipAll.setSelected(true);
                tvVipOnly.setSelected(false);
                tvVipGold.setSelected(false);
                gvVip.setVisibility(View.VISIBLE);
                llFunction.setVisibility(View.GONE);
                mAdapter.setData(siteVipItems);
                tvVipIntroduce.setText("VIP权限(不包含微课和训练营)");
            }
        });

        //全站会员图标单击显示详情
        gvVip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence message = iconInfo[position];
                if (position==7){
                    message = Html.fromHtml("使用于<a href=http://app.iyuba.cn>app.iyuba.cn</a>旗下所有APP");
                }
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.dialog_web,null);
                TextView textView =view1.findViewById(R.id.tv_message_web);
                textView.setText(message);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                new AlertDialog.Builder(mContext)
                        .setView(view1)
                        .setPositiveButton(getString(R.string.sure),null)
                        .create()
                        .show();
            }
        });

        tvVipOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvVipAll.setSelected(false);
                tvVipOnly.setSelected(true);
                tvVipGold.setSelected(false);
                gvVip.setVisibility(View.GONE);
                llFunction.setVisibility(View.VISIBLE);
                tvFunction.setText(R.string.app_vip_description);
                mAdapter.setData(appVipItems);
                tvVipIntroduce.setText("VIP权限(不包含微课和训练营)");
            }
        });

        tvVipGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvVipAll.setSelected(false);
                tvVipOnly.setSelected(false);
                tvVipGold.setSelected(true);
                gvVip.setVisibility(View.GONE);
                llFunction.setVisibility(View.VISIBLE);
                tvFunction.setText(R.string.golden_vip_description);
                mAdapter.setData(goldenVipItems);
                tvVipIntroduce.setText("VIP权限");

            }
        });

        tvBuyIyubi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, BuyIyubiActivity.class);
                intent.putExtra("url", "http://app."+com.iyuba.configation.Constant.IYBHttpHead()+"/wap/index.jsp?uid="
                        + UserInfoManager.getInstance().getUserId() + "&appid="
                        + Constant.APPID);
                intent.putExtra("title", Constant.APPName);
                startActivity(intent);
            }
        });

        tvVipIntroduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VipCenterGoldActivity.this, Web.class);

                intent.putExtra("url", "http://vip."+com.iyuba.configation.Constant.IYBHttpHead()+"/vip/vip.html");
                intent.putExtra("title", "vip说明");
                startActivity(intent);
            }
        });

        tvGoBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyVIPItem item = mAdapter.getSelectedItem();
                if (item == null) {
                    ToastUtil.showToast(mContext, "请选择要开通的VIP!");
                } else {
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(mContext);
                    } else {
                        String info = getString(R.string.buy_app_vip_body_info, item.name);
                        String subject = "";
                        if (item.productId==0){
                            subject= "全站vip";
                        }else if (item.productId==10){
                            subject= "永久vip";
                        }else {
                            subject= "黄金vip";
                        }
                        Intent intent = new Intent(mContext, PayOrderActivity.class);
                        String price = String.valueOf(item.price);
                        //String price = "0.01";
                        intent.putExtra("month", item.month+"");
                        intent.putExtra("type", item.month);
                        intent.putExtra("productId", item.productId+"");
                        intent.putExtra("out_trade_no", getOutTradeNo());
                        intent.putExtra("subject", subject);//"全站vip"
                        intent.putExtra("body", info);//购买全站vip3个月
                        intent.putExtra("price", price + "");  //价格
                        startActivity(intent);
                    }
                }
            }
        });

        btnServe.setVisibility(View.GONE);
        btnServe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, btnServe);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.toolbarmenu, popup.getMenu());
                Menu menu = popup.getMenu();
                menu.getItem(0).setTitle(BrandUtil.getBrandChinese() + "用户群:" + ConfigManager.Instance().getQQGroupNumber());
                menu.getItem(1).setTitle("内容QQ:" + ConfigManager.Instance().getQQEditor());
                menu.getItem(2).setTitle("技术QQ:" + ConfigManager.Instance().getQQTechnician());
                menu.getItem(3).setTitle("投诉QQ:" + ConfigManager.Instance().getQQManager());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                        if (item.getItemId() == R.id.test_qq) {
                            QQUtil.startQQGroup(mContext, ConfigManager.Instance().getQQGroupKey());
                        } else if (item.getItemId() == R.id.content_qq) {
                            QQUtil.startQQ(mContext, ConfigManager.Instance().getQQEditor());
                        } else if (item.getItemId() == R.id.tycnolge_qq) {
                            QQUtil.startQQ(mContext, ConfigManager.Instance().getQQTechnician());
                        } else if (item.getItemId() == R.id.tousu_qq) {
                            QQUtil.startQQ(mContext, ConfigManager.Instance().getQQManager());
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    public List<Map<String, Object>> getData() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    private void Assignment() {
        tvUserName.setText(UserInfoManager.getInstance().getUserName());
        if(UserInfoManager.getInstance().isLogin()){
            GitHubImageLoader.Instace(mContext).setCirclePicGrild(//特殊的设置圆形图片方法
                    String.valueOf(UserInfoManager.getInstance().getUserId()), userPhoto,mContext,R.drawable.defaultavatar);
        }

        tvIyubi.setText("爱语币："+iyubi);
        if (UserInfoManager.getInstance().isVip()) {
            tvVipTime.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_vip_logo_white), null, null, null);
            tvVipTime.setCompoundDrawablePadding(10);
            tvVipTime.setText(validity);//vip时间
        } else {
            tvVipTime.setText(R.string.person_common_user);//普通用户
        }
    }

    private void autoRefresh() {

        /*LogUtils.e("isvipooo", ConfigManager.Instance().loadInt("isvip") + "");
        vipStatus = ConfigManager.Instance().loadInt("isvip");
        validity = ConfigManager.Instance().loadString("validity");
        iyubi = ConfigManager.Instance().loadString("iyubi");

        if (!NetWorkState.isConnectingToInternet()) {
            if (AccountManager.Instance(mContext).checkUserLogin() || AccountManager.Instance(mContext).islinshi) {
                Assignment();
            }
        } else if (AccountManager.Instance(mContext).islinshi) {
            if (AccountManager.Instance(mContext).checkUserLogin() || AccountManager.Instance(mContext).islinshi) {
                Assignment();
            }
        } else {
            Assignment();
        }*/


        //重写一个数据
        if (UserInfoManager.getInstance().isLogin()){
            tvUserName.setText(UserInfoManager.getInstance().getUserName());
            GitHubImageLoader.Instace(mContext).setCirclePicGrild(//特殊的设置圆形图片方法
                    String.valueOf(UserInfoManager.getInstance().getUserId()), userPhoto,mContext,R.drawable.defaultavatar);
            if (UserInfoManager.getInstance().isVip()){
                tvVipTime.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_vip_logo_white), null, null, null);
                tvVipTime.setCompoundDrawablePadding(10);
                tvVipTime.setText(LibDateUtil.toDateStr(UserInfoManager.getInstance().getVipTime(), LibDateUtil.YMD));//vip时间
            }else {
                tvVipTime.setText(R.string.person_common_user);
            }
        }else {
            tvUserName.setText("未登录");
            GitHubImageLoader.Instace(mContext).setCirclePicGrild(null,userPhoto,mContext,R.drawable.defaultavatar);
            tvVipTime.setText(R.string.person_common_user);//普通用户
        }
        tvIyubi.setText("爱语币："+UserInfoManager.getInstance().getIyuIcon());

    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity this
     * @param colorId 黄
     */
    public  void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //低版本没有适配
        }
    }
}
