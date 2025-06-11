package com.iyuba.concept2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22.
 */
public class KeyboardAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> letters = new ArrayList<String>();

    public KeyboardAdapter(Context mContext, List<String> letters) {
        this.mContext = mContext;
        this.letters.addAll(letters);
        this.letters.add("mmm");
    }

    @Override
    public int getCount() {
        return letters.size();
    }

    @Override
    public Object getItem(int i) {
        return letters.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 19)
        {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.keyboard_back, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.keyboard_text);
            ImageView iv = (ImageView) convertView.findViewById(R.id.keyboard_ensure);

            tv.setVisibility(View.INVISIBLE);
            iv.setVisibility(View.VISIBLE);


//            ImageView iv;
//            if (convertView == null) {
//                iv = new ImageView(mContext);
//                iv.setAdjustViewBounds(true);
//
//                iv.setMaxWidth(50);
//                iv.setMaxHeight(50);
//
//
//            } else {
//                iv = (ImageView) convertView;
//            }
//
//            iv.setImageResource(R.drawable.confirm);

            return convertView;
        }else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.keyboard_back, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.keyboard_text);
            ImageView iv = (ImageView) convertView.findViewById(R.id.keyboard_ensure);

            tv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.INVISIBLE);

            tv.setText(letters.get(position));
//            TextView tv;
//            if (convertView == null) {
//                tv = new TextView(mContext);
//                tv.setGravity(Gravity.CENTER);
//                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
////                tv.setBackgroundResource(R.drawable.keyboard_rectangle);
////                tv.setHeight(20);
////                tv.setWidth(20);
//
//            } else {
//                tv = (TextView) convertView;
//            }
//            Log.e("position",String.valueOf(position));
//            tv.setText(letters.get(position));

            return convertView;
        }
    }
}
