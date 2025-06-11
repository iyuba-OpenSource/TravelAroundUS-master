package com.iyuba.core.search.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.search.bean.SearchArticleBean;
import com.iyuba.core.search.bean.SearchInfoBean;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.lib.R;

import static com.youdao.sdk.common.YoudaoSDK.getApplicationContext;

/**
 * voa列表 搜索结果
 */
public class VoaListAdapter extends RecyclerView.Adapter {


    private SearchInfoBean searchInfoBean;
    private Context mContext;

    public VoaListAdapter(Context context) {
        mContext = context;
    }

    public void setData(SearchInfoBean searchInfoBean) {
        this.searchInfoBean = searchInfoBean;
    }
    public void appendData(SearchInfoBean searchInfoBean) {
        if (this.searchInfoBean!=null) {
            for (SearchArticleBean voaBean : searchInfoBean.getTitleData()) {
                this.searchInfoBean.getTitleData().add(voaBean);
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        View view = mInflater.inflate(R.layout.search_article_item, viewGroup, false);
        return new VoaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof VoaViewHolder) {
            ((VoaViewHolder) viewHolder).setData(position);
            ((VoaViewHolder) viewHolder).setListener(position);
        }
    }

    @Override
    public int getItemCount() {
        if (searchInfoBean!=null) {
            return searchInfoBean.getTitleData().size();
        }
        return 0;
    }

    public class VoaViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivArticleCover;
        private TextView tvEnTitle;
        private TextView tvCnTitle;
        private TextView tvReadNum;

        public VoaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArticleCover=itemView.findViewById(R.id.iv_article_cover);
            tvEnTitle=itemView.findViewById(R.id.tv_en_title);
            tvCnTitle=itemView.findViewById(R.id.tv_cn_title);
            tvReadNum=itemView.findViewById(R.id.tv_read_num);
        }

        public void setData(int position) {
            tvEnTitle.setText(searchInfoBean.getTitleData().get(position).getTitle());
            tvCnTitle.setText(searchInfoBean.getTitleData().get(position).getTitle_Cn());
            tvReadNum.setText(searchInfoBean.getTitleData().get(position).getReadCount() + "次阅读");
            Glide.with(getApplicationContext()).load(searchInfoBean.getTitleData().get(position).getPic()).into(ivArticleCover);

        }

        public void setListener(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchArticleBean articleBean = searchInfoBean.getTitleData().get(position);
                    //跳转到文章视频通用模块
                    Intent intent = AudioContentActivity.getIntent2Me(mContext,articleBean.getCategory(),
                            articleBean.getTitle(),
                            articleBean.getTitle_Cn(), articleBean.getPic(), "voa",
                            articleBean.getVoaId(), articleBean.getSound());
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
