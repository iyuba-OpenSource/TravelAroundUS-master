package com.iyuba.concept2.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.concept2.R;
import com.iyuba.concept2.api.data.EvaluateRecordResponse;
import com.iyuba.concept2.util.ConceptApplication;
import com.iyuba.core.search.bean.SearchInfoBean;

import java.util.List;

/**
 * 句子列表 搜索结果
 */
public class ColloquialCircleAdapter extends RecyclerView.Adapter<ColloquialCircleAdapter.ViewHolder> {

    private EvaluateRecordResponse data;

    private OnLongClickListener onLongClickListener;
    private OnSoundClickListener onSoundClickListener;

    //选中的界面
    private ViewHolder selectHolder;

    public ColloquialCircleAdapter() {

    }

    public void setData(EvaluateRecordResponse data) {
        this.data = data;
    }

    public void appendData(SearchInfoBean searchInfoBean) {
        if (this.data != null) {
            // playPosition = -1;//设置没有播放的
            for (EvaluateRecordResponse.Data bean : data.getData()) {
                this.data.getData().add(bean);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        View view = mInflater.inflate(R.layout.item_colloquial_circle, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        EvaluateRecordResponse.Data bean = this.data.getData().get(position);
        Glide.with(ConceptApplication.getContext()).load(bean.getImgSrc()).into(viewHolder.headImg);
        viewHolder.publishTime.setText(bean.getCreateDate());
        viewHolder.tvUserName.setText(bean.getUserName());
        viewHolder.tvScore.setText(bean.getScore());
        viewHolder.soundImg.setOnClickListener((View v) ->{
            if(onSoundClickListener != null){
                this.selectHolder = viewHolder;
                onSoundClickListener.onSoundClick(bean, viewHolder.soundImg);
            }
        });
        viewHolder.itemView.setOnLongClickListener(v->{
            if(onLongClickListener != null){
                onLongClickListener.onLongClick(bean);
                return true;
            }
            return false;
        });
    }

    public void setOnSoundClickListener(OnSoundClickListener onSoundClickListener){
        this.onSoundClickListener = onSoundClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public int getItemCount() {
        if (data != null && data.getData() != null) {
            return data.getData().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView headImg;
        private TextView tvUserName;
        public ImageView soundImg;
        private TextView tvScore;
        private TextView publishTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headImg = itemView.findViewById(R.id.head_img);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            soundImg = itemView.findViewById(R.id.item_circle_voice);
            tvScore = itemView.findViewById(R.id.tv_score);
            publishTime = itemView.findViewById(R.id.publish_time);
        }
    }

    public interface OnSoundClickListener{
        void onSoundClick(EvaluateRecordResponse.Data bean, ImageView img);
    }

    public interface OnLongClickListener{
        void onLongClick(EvaluateRecordResponse.Data bean);
    }

    //获取选中的样式
    public ViewHolder getSelectHolder(){
        return selectHolder;
    }
}
