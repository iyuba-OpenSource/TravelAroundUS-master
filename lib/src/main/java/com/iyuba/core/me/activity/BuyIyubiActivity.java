package com.iyuba.core.me.activity;

/**
 * 网页显示
 *
 * @author chentong
 * @version 1.0
 * @para 传入"url" 网址；"title"标题显示
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.me.pay.IyubiPayOrderActivity;
import com.iyuba.lib.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class BuyIyubiActivity extends BasisActivity implements OnClickListener {
    private ImageView backButton;
    private TextView textView;
    private ImageView iv_buy1;
    private ImageView iv_buy2;
    private ImageView iv_buy3;
    private ImageView iv_buy4;
    private ImageView iv_buy5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_buy_iyubi);
        setProgressBarVisibility(true);
        CrashApplication.getInstance().addActivity(this);
        backButton = (ImageView) findViewById(R.id.lib_button_back);
        textView = (TextView) findViewById(R.id.web_buyiyubi_title);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        textView.setText(title);

        initView();

    }

    private void initView() {
        iv_buy1 = (ImageView) findViewById(R.id.iv_buy1);
        iv_buy2 = (ImageView) findViewById(R.id.iv_buy2);
        iv_buy3 = (ImageView) findViewById(R.id.iv_buy3);
        iv_buy4 = (ImageView) findViewById(R.id.iv_buy4);
        iv_buy5 = (ImageView) findViewById(R.id.iv_buy5);

        iv_buy1.setOnClickListener(this);
        iv_buy2.setOnClickListener(this);
        iv_buy3.setOnClickListener(this);
        iv_buy4.setOnClickListener(this);
        iv_buy5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("isIyubi",true);
        intent.putExtra("productID", "1");
        intent.setClass(this, IyubiPayOrderActivity.class);
        intent.putExtra("out_trade_no", getOutTradeNo());
        intent.putExtra("subject", "爱语币");

        if (view == iv_buy1) {
            intent.putExtra("price", "19.9");  //价格
            intent.putExtra("amount", "210");  //数量
            intent.putExtra("body", "花费19.9元购买210个爱语币");
        } else if (view == iv_buy2) {
            intent.putExtra("price", "59.9");  //价格
            intent.putExtra("amount", "650");  //数量
            intent.putExtra("body", "花费59.9元购买650个爱语币");
        } else if (view == iv_buy3) {
            intent.putExtra("price", "99.9");  //价格
            intent.putExtra("amount", "1100");  //数量
            intent.putExtra("body", "花费99.9元购买1100个爱语币");
        } else if (view == iv_buy4) {
            intent.putExtra("price", "599");  //价格
            intent.putExtra("amount", "6600");  //数量
            intent.putExtra("body", "花费599元购买6600个爱语币");
        } else if (view == iv_buy5) {
            intent.putExtra("price", "999");  //价格
            intent.putExtra("amount", "12000");  //数量
            intent.putExtra("body", "花费999元购买12000个爱语币");
        }
        startActivity(intent);

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
}
