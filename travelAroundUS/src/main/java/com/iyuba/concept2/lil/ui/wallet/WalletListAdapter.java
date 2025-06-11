package com.iyuba.concept2.lil.ui.wallet;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.concept2.R;
import com.iyuba.core.lil.remote.bean.Reward_history;
import com.iyuba.core.lil.util.LibBigDecimalUtil;
import com.iyuba.core.lil.util.LibDateUtil;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/23 08:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.WalletListHolder> {

    private Context context;
    private List<Reward_history> list;

    public WalletListAdapter(Context context, List<Reward_history> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WalletListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_wallet_list,parent,false);
        return new WalletListHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletListHolder holder, int position) {
        if (holder==null){
            return;
        }

        Reward_history history = list.get(position);
        holder.type.setText(history.getType());
        //变更单位为元
        int rewardInt = TextUtils.isEmpty(history.getScore())?0:Integer.parseInt(history.getScore());
        double reward = LibBigDecimalUtil.trans2Double(rewardInt*0.01f);
        holder.reward.setText(String.valueOf(reward));
        //变更时间显示（如果是当天的，则显示今天xx；如果不是，则显示日期+时间）
        long time = LibDateUtil.toDateLong(history.getTime(),LibDateUtil.YMDHMSS);
        long curStartTime = getTodayStartTime();
        long curEndTime = curStartTime+24*60*60*1000;
        String showTime = LibDateUtil.toDateStr(time,LibDateUtil.YMD);
        if (time>curStartTime&&time<curEndTime){
            showTime = "今天"+LibDateUtil.toDateStr(time,LibDateUtil.HM);
        }
        holder.time.setText(showTime);

        holder.itemView.setOnClickListener(v->{
            String showMsg = "类型："+history.getType()+"\n金额："+reward+"元\n时间："+holder.time.getText().toString();
            new AlertDialog.Builder(context)
                    .setTitle("奖励信息")
                    .setMessage(showMsg)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WalletListHolder extends RecyclerView.ViewHolder{

        private TextView type;
        private TextView reward;
        private TextView time;

        public WalletListHolder(View rootView){
            super(rootView);

            type = rootView.findViewById(R.id.type);
            reward = rootView.findViewById(R.id.reward);
            time = rootView.findViewById(R.id.time);
        }
    }

    //刷新数据
    public void refreshData(List<Reward_history> refreshList,boolean isAdd){
        if (isAdd){
            this.list.addAll(refreshList);
        }else {
            this.list = refreshList;
        }
        notifyDataSetChanged();
    }

    //获取今天的开始时间
    private long getTodayStartTime(){
        String todayDate = LibDateUtil.toDateStr(System.currentTimeMillis(),LibDateUtil.YMD);
        long todayTime = LibDateUtil.toDateLong(todayDate,LibDateUtil.YMD);
        return todayTime;
    }
}
