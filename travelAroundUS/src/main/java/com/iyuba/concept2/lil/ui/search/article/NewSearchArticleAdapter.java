package com.iyuba.concept2.lil.ui.search.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.concept2.R;
import com.iyuba.core.lil.model.remote.bean.Word_search;

import java.util.List;

/**
 * @title: 新搜索界面-文章适配器
 * @date: 2023/12/25 16:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchArticleAdapter extends RecyclerView.Adapter<NewSearchArticleAdapter.ArticleHolder> {

    private Context context;
    private List<Word_search.TitleDataBean> list;

    public NewSearchArticleAdapter(Context context, List<Word_search.TitleDataBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ArticleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.search_article_item,parent,false);
        return new ArticleHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleHolder holder, int position) {
        if (holder==null){
            return;
        }

        Word_search.TitleDataBean dataBean = list.get(position);
        Glide.with(context).load(dataBean.getPic()).into(holder.ivPic);
        holder.tvTitle.setText(dataBean.getTitle());
        holder.tvTitleCn.setText(dataBean.getTitle_Cn());
        holder.tvReadCount.setText(dataBean.getReadCount()+"词阅读");

        holder.itemView.setOnClickListener(v->{
            if (onArticleClickListener!=null){
                onArticleClickListener.onItemClick(dataBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ArticleHolder extends RecyclerView.ViewHolder{

        private ImageView ivPic;
        private TextView tvTitle;
        private TextView tvTitleCn;
        private TextView tvReadCount;

        public ArticleHolder(View rootView){
            super(rootView);

            ivPic = rootView.findViewById(R.id.iv_article_cover);
            tvTitle = rootView.findViewById(R.id.tv_en_title);
            tvTitleCn = rootView.findViewById(R.id.tv_cn_title);
            tvReadCount = rootView.findViewById(R.id.tv_read_num);
        }
    }

    //刷新数据
    public void refreshData(List<Word_search.TitleDataBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //点击回调
    public OnArticleClickListener onArticleClickListener;

    public interface OnArticleClickListener{
        void onItemClick(Word_search.TitleDataBean dataBean);
    }

    public void setOnArticleClickListener(OnArticleClickListener onArticleClickListener) {
        this.onArticleClickListener = onArticleClickListener;
    }
}
