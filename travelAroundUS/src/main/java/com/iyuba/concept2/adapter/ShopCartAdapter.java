package com.iyuba.concept2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.BookDetail;
import com.iyuba.concept2.widget.AmountView;
import com.iyuba.core.common.thread.GitHubImageLoader;

import java.util.ArrayList;

public class ShopCartAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BookDetail> arrayList = new ArrayList<BookDetail>();
    private onCheckedChanged listener;
    private int source = 0;  //0 from cart;1 from order;2 from orderResult

    public ShopCartAdapter(Context context, ArrayList<BookDetail> arrayList, int source) {
        this.source = source;
        this.context = context;
        this.arrayList.addAll(arrayList);
    }

    public ShopCartAdapter(Context context) {
        this.context = context;

    }

    public void addList(ArrayList<BookDetail> arrayList) {
        this.arrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (arrayList != null && arrayList.size() == 0) ? 0 : arrayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arrayList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View currentView, ViewGroup arg2) {
        HolderView holderView = null;
        if (currentView == null) {
            holderView = new HolderView();
            currentView = LayoutInflater.from(context).inflate(R.layout.adapter_listview_cart, null);
            holderView.content = (TextView) currentView.findViewById(R.id.book_name);
            holderView.totalPrice = (TextView) currentView.findViewById(R.id.total_price);
            holderView.av = (AmountView) currentView.findViewById(R.id.amount_view);
            holderView.cb_choice = (CheckBox) currentView.findViewById(R.id.cb_choice);
            holderView.img = (ImageView) currentView.findViewById(R.id.iv_adapter_list_pic);
            holderView.numSolid = (TextView) currentView.findViewById(R.id.amount_solid);
            holderView.numDecrease = (Button) currentView.findViewById(R.id.btnDecrease);
            holderView.numIncrease = (Button) currentView.findViewById(R.id.btnIncrease);
            holderView.et = (EditText) currentView.findViewById(R.id.tv_num);
            currentView.setTag(holderView);
        } else {
            holderView = (HolderView) currentView.getTag();
        }
        if (arrayList.size() != 0) {
            if (source == 0) {
                holderView.content.setText(arrayList.get(position).editInfo);
                holderView.totalPrice.setText("¥" + arrayList.get(position).totalPrice);
//            holderView.tvNum.setText(String.valueOf(arrayList.get(position).num));
                holderView.av.setGoods_storage(1000);
                holderView.av.setText(arrayList.get(position).num);
                GitHubImageLoader.Instace(context).setRawPic(arrayList.get(position).editImg, holderView.img,
                        R.drawable.failed_image);
                holderView.cb_choice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean choice) {
                        listener.getChoiceData(position, choice, 0, true);
                    }
                });
                final HolderView finalHolderView = holderView;
//                holderView.et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                holderView.et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        listener.getChoiceData(position, finalHolderView.cb_choice.isChecked(), 0 - Integer.parseInt(s.toString()), false);
                        Log.e("beforeTextChanged","beforeTextChanged");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        listener.getChoiceData(position, finalHolderView.cb_choice.isChecked(), Integer.parseInt(s.toString()), false);
                        Log.e("afterTextChanged","afterTextChanged");
                    }
                });
            } else if (source == 1) {
                holderView.content.setText(arrayList.get(position).editInfo);
                holderView.totalPrice.setText("¥" + arrayList.get(position).totalPrice);
//            holderView.tvNum.setText(String.valueOf(arrayList.get(position).num));
                holderView.av.setVisibility(View.GONE);
                holderView.av.setGoods_storage(1000);
                holderView.av.setText(arrayList.get(position).num);
                GitHubImageLoader.Instace(context).setRawPic(arrayList.get(position).editImg, holderView.img,
                        R.drawable.failed_image);
                holderView.numSolid.setVisibility(View.VISIBLE);
                holderView.numSolid.setText("x" + String.valueOf(arrayList.get(position).num));
                holderView.cb_choice.setVisibility(View.GONE);
            } else if (source == 2) {
                if (arrayList.get(position).bookAuthor.equals("图书商城")) {
                    holderView.content.setText(arrayList.get(position).editInfo);
                    holderView.totalPrice.setText(arrayList.get(position).createTime);
//                holderView.totalPrice.setTextColor(0x333333);
//            holderView.tvNum.setText(String.valueOf(arrayList.get(position).num));
                    holderView.av.setVisibility(View.GONE);
                    holderView.av.setGoods_storage(10000);
                    holderView.av.setText(arrayList.get(position).num);
                    GitHubImageLoader.Instace(context).setRawPic(arrayList.get(position).editImg, holderView.img,
                            R.drawable.market_book);
                    holderView.numSolid.setVisibility(View.VISIBLE);
                    holderView.numSolid.setText(arrayList.get(position).totalPrice);
//                holderView.numSolid.setTextColor(0xff0000);
                    holderView.cb_choice.setVisibility(View.GONE);
                } else if (arrayList.get(position).bookAuthor.equals("爱语币")) {
                    holderView.content.setText(arrayList.get(position).editInfo);
                    holderView.totalPrice.setText(arrayList.get(position).createTime);
//                holderView.totalPrice.setTextColor(0x333333);
//            holderView.tvNum.setText(String.valueOf(arrayList.get(position).num));
                    holderView.av.setVisibility(View.GONE);
                    holderView.av.setGoods_storage(1000);
                    holderView.av.setText(arrayList.get(position).num);
                    holderView.img.setImageResource(R.drawable.market_aiyubi);
                    holderView.numSolid.setVisibility(View.VISIBLE);
                    holderView.numSolid.setText(arrayList.get(position).totalPrice);
//                holderView.numSolid.setTextColor(0xff0000);
                    holderView.cb_choice.setVisibility(View.GONE);
                } else {
                    holderView.content.setText(arrayList.get(position).editInfo);
                    holderView.totalPrice.setText(arrayList.get(position).createTime);
                    holderView.totalPrice.setTextColor(Color.parseColor("#333333"));
//                holderView.totalPrice.setTextColor(0x333333);
//            holderView.tvNum.setText(String.valueOf(arrayList.get(position).num));
                    holderView.av.setVisibility(View.GONE);
                    holderView.av.setGoods_storage(1000);
                    holderView.av.setText(arrayList.get(position).num);
                    holderView.img.setImageResource(R.drawable.market_vip);
                    holderView.numSolid.setVisibility(View.VISIBLE);
                    holderView.numSolid.setText(arrayList.get(position).totalPrice);
                    holderView.numSolid.setTextColor(Color.parseColor("#ff0000"));
//                holderView.numSolid.setTextColor(0xff0000);
                    holderView.cb_choice.setVisibility(View.GONE);
                }
            }


        }
        return currentView;
    }

    public class HolderView {

        private ImageView img;
        private TextView content;
        private TextView totalPrice;
        private TextView numSolid;
        private AmountView av;
        private CheckBox cb_choice;
        private Button numDecrease;
        private Button numIncrease;
        private EditText et;
    }

    public interface onCheckedChanged {

        void getChoiceData(int position, boolean isChoice, int bookNum, boolean flag);  //flag确定的是哪个listener调用的
    }

    public void setOnCheckedChanged(onCheckedChanged listener) {
        this.listener = listener;
    }

}
