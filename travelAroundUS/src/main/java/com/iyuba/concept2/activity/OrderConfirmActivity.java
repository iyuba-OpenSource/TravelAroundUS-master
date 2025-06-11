package com.iyuba.concept2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.ShopCartAdapter;
import com.iyuba.concept2.protocol.UserPositionResponse;
import com.iyuba.concept2.protocol.UserPostionRequest;
import com.iyuba.concept2.sqlite.db.UserAddrInfo;
import com.iyuba.concept2.sqlite.mode.BookDetail;
import com.iyuba.concept2.util.ExeProtocol;
import com.iyuba.concept2.widget.MyListView;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ivotsm on 2017/2/28.
 */

public class OrderConfirmActivity extends BasisActivity {
    private Context mContext;
    private ArrayList<BookDetail> bookDetails = new ArrayList<>();
    private ShopCartAdapter adapter;
    private UserAddrInfo userInfo = new UserAddrInfo();
    private int totalAmount = 0;
    private float totalPrice = 0.0f;

    @BindView(R.id.user_name)
    TextView name;
    @BindView(R.id.user_phone)
    TextView phone;
    @BindView(R.id.user_address)
    TextView address;
    @BindView(R.id.edit_user)
    ImageView goEdit;
    @BindView(R.id.send_way)
    TextView sendWay;
    @BindView(R.id.send_fee)
    TextView sendFee;
    @BindView(R.id.titlebar_back_button)
    ImageButton backBtn;
    @BindView(R.id.titlebar_title)
    TextView title;
    @BindView(R.id.titlebar_overflow_button)
    ImageButton overflowBtn;
    @BindView(R.id.order_list)
    MyListView list;
    @BindView(R.id.goods_amount)
    TextView goodsAmount;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.confirm)
    TextView confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm);

        mContext = this;
        ButterKnife.bind(this);

        final Bundle bundle = getIntent().getBundleExtra("books");
        bookDetails = (ArrayList<BookDetail>) bundle.getSerializable("books");

        for (int i = 0; i < bookDetails.size(); i++) {
            totalAmount = totalAmount + bookDetails.get(i).num;
            totalPrice += Float.parseFloat(bookDetails.get(i).totalPrice) * bookDetails.get(i).num;
        }

        goodsAmount.setText("共计" + String.valueOf(totalAmount) + "件商品");
        price.setText("合 计: ¥" + String.valueOf(totalPrice));

        adapter = new ShopCartAdapter(mContext, bookDetails, 1);
        list.setAdapter(adapter);

        setListViewHeightBasedOnChildren(list);

        title.setText("确认订单");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        overflowBtn.setVisibility(View.GONE);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().equals("") || phone.getText().equals("") || address.getText().equals("")) {
                    Toast.makeText(mContext, "请完善个人信息", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("books", bundle);
                    intent.setClass(mContext, BookMarketOrderPayActivity.class);
                    startActivity(intent);
                }
            }
        });

        goEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ModifyAddressActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        handler.sendEmptyMessage(1);
    }

    /**
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(MyListView listView) {
        if (listView == null) return;
        ShopCartAdapter listAdapter = (ShopCartAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            Bundle bundle = data.getBundleExtra("userInfo");
            UserAddrInfo ui = (UserAddrInfo) bundle.getSerializable("userInfo");
            name.setText("收货人:" + ui.realName);
            phone.setText("电话号:" + ui.phone);
            address.setText("收货地址:" + ui.address);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    name.setText("收货人:" + userInfo.realName);
                    phone.setText("电话号:" + userInfo.phone);
                    address.setText("收货地址:" + userInfo.address);
                    break;
                case 1:
                    ExeProtocol.exe(
                            new UserPostionRequest(String.valueOf(UserInfoManager.getInstance().getUserId())),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    UserPositionResponse response = (UserPositionResponse) bhr;
                                    userInfo = response.userInfo;
                                    handler.sendEmptyMessage(0);
                                }

                                @Override
                                public void error() {

                                }
                            });
                    break;
            }
        }
    };
}
