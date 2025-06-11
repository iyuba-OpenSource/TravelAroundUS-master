package com.iyuba.concept2.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.MarketBook;
import com.iyuba.core.common.thread.GitHubImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivotsm on 2017/2/20.
 */

public class BookListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<MarketBook> books = new ArrayList<>();

    public BookListAdapter(Context mContext, List<MarketBook> books) {
        this.mContext = mContext;
        this.books.addAll(books);
        inflater = LayoutInflater.from(mContext);
    }

    public void addBooks(List<MarketBook> books){
        this.books.addAll(books);
        notifyDataSetChanged();
    }

    public void replaceBooks(List<MarketBook> books){
        this.books.clear();
        this.books.addAll(books);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int i) {
        return books.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MarketBook mb ;//= new MarketBook()
        mb = books.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.book_item, null);
        }

        ImageView logo = ViewHolder.get(convertView, R.id.book_logo);
        TextView content = ViewHolder.get(convertView, R.id.book_content);
        TextView press = ViewHolder.get(convertView, R.id.book_press);
        TextView nowPrice = ViewHolder
                .get(convertView, R.id.book_now_price);
        TextView originPrice = ViewHolder
                .get(convertView, R.id.book_origin_price);

        originPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        GitHubImageLoader.Instace(mContext).setRawPic(mb.getEditImg(), logo,
                R.drawable.failed_image);
        content.setText(mb.getEditInfo());
        press.setText(mb.getPublishHouse());
        nowPrice.setText(mb.getTotalPrice());
        originPrice.setText(mb.getOriginalPrice());

        return convertView;
    }
}
