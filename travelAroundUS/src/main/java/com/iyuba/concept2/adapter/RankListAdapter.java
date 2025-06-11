package com.iyuba.concept2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.RankUser;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.thread.GitHubImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.iyuba.headnewslib.adapter.ViewHolder;

/**
 * 学习-排行的适配器
 * Created by Administrator on 2017/1/4.
 */

public class RankListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RankUser> rankUserList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Pattern p;
    private Matcher m;

    public RankListAdapter(Context mContext, List<RankUser> rankUserList) {
        this.mContext = mContext;
        this.rankUserList.addAll(rankUserList);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return rankUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(rankUserList.get(position).getSort());
    }

    public void resetList(List<RankUser> list){
        rankUserList.clear();
        rankUserList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<RankUser> list){
        rankUserList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rank_info_item, null);
        }
        RankUser ru = rankUserList.get(position);
        Log.e("RankListAdapter",String.valueOf(rankUserList.size()));
        String firstChar;

        ImageView rankLogoImage = ViewHolder.get(convertView, R.id.rank_logo_image);
        TextView rankLogoText = ViewHolder.get(convertView, R.id.rank_logo_text);
        ImageView userImage = ViewHolder.get(convertView, R.id.user_image);
        TextView userImageText = ViewHolder.get(convertView, R.id.user_image_text);
        TextView userName = ViewHolder.get(convertView, R.id.rank_user_name);
        TextView userInfo = ViewHolder.get(convertView, R.id.rank_user_info);

        firstChar = getFirstChar(ru.getName());
        switch (ru.getRanking()) {
            case "1":
                rankLogoText.setVisibility(View.INVISIBLE);
                rankLogoImage.setVisibility(View.VISIBLE);
                rankLogoImage.setImageResource(R.drawable.rank_gold);

                if (ru.getImgSrc().equals("http://static1."+Constant.IYBHttpHead()+"/uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    userImage.setVisibility(View.VISIBLE);
                    userImageText.setVisibility(View.INVISIBLE);
                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
                            R.drawable.noavatar_small);
                    if(ru.getName()!=null&&!"".equals(ru.getName()))
                        userName.setText(ru.getName());
                    else
                        userName.setText(ru.getUid());
                    userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            case "2":
                rankLogoText.setVisibility(View.INVISIBLE);
                rankLogoImage.setVisibility(View.VISIBLE);
                rankLogoImage.setImageResource(R.drawable.rank_silvery);

                if (ru.getImgSrc().equals("http://static1."+Constant.IYBHttpHead()+"/uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    userImage.setVisibility(View.VISIBLE);
                    userImageText.setVisibility(View.INVISIBLE);
                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
                            R.drawable.noavatar_small);
                    if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                            &&!"null".equals(ru.getName()))
                        userName.setText(ru.getName());
                    else
                        userName.setText(ru.getUid());
                    userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            case "3":
                rankLogoText.setVisibility(View.INVISIBLE);
                rankLogoImage.setVisibility(View.VISIBLE);
                rankLogoImage.setImageResource(R.drawable.rank_copper);

                if (ru.getImgSrc().equals("http://static1."+Constant.IYBHttpHead()+"/uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    userImageText.setVisibility(View.INVISIBLE);
                    userImage.setVisibility(View.VISIBLE);
                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
                            R.drawable.noavatar_small);
                    if(ru.getName()!=null&&!"".equals(ru.getName()))
                        userName.setText(ru.getName());
                    else
                        userName.setText(ru.getUid());
                    userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            default:
                rankLogoImage.setVisibility(View.INVISIBLE);
                rankLogoText.setVisibility(View.VISIBLE);
                rankLogoText.setText(ru.getRanking());
                rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rankLogoText.setSingleLine(true);
                rankLogoText.setSelected(true);
//                rankLogoText.setFocusable(true);
//                rankLogoText.setFocusableInTouchMode(true);

                if (ru.getImgSrc().equals("http://static1."+Constant.IYBHttpHead()+"/uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                                &&!"null".equals(ru.getName()))
                            userName.setText(ru.getName());
                        else
                            userName.setText(ru.getUid());
                        userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    userImageText.setVisibility(View.INVISIBLE);
                    userImage.setVisibility(View.VISIBLE);
                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
                            R.drawable.noavatar_small);
                    if(ru.getName()!=null&&!"".equals(ru.getName())&&!"none".equals(ru.getName())
                            &&!"null".equals(ru.getName()))
                        userName.setText(ru.getName());
                    else
                        userName.setText(ru.getUid());
                    userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;

        }
        return convertView;
    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }
}
