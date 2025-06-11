package com.iyuba.concept2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class TestArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mStringArray;

    public TestArrayAdapter(Context context, List<String> data_list) {
        super(context, android.R.layout.simple_spinner_item, data_list);
        mContext = context;
        mStringArray = data_list;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner展开后的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent,
                    false);
        }

        // 此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray.get(position));
        tv.setTextSize(18f);
        tv.setGravity(Gravity.CENTER);

        if (position % 2 == 0) {
            tv.setBackgroundColor(0xff86caf9);
            tv.setTextColor(Color.WHITE);
        } else {
            tv.setBackgroundColor(0xffdbe6ec);
            tv.setTextColor(0xff86caf9);
        }

        return convertView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner选择后结果的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }

        // 此处text1是Spinner默认的用来显示文字的TextView
        RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setLayoutParams(layoutParams);
        tv.setText(mStringArray.get(position));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18f);
        tv.setTextColor(Color.WHITE);
        return convertView;
    }

}
