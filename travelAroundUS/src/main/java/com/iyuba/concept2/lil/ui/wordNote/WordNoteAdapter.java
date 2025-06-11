package com.iyuba.concept2.lil.ui.wordNote;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.sqlite.mode.NewWord;
import com.iyuba.core.lil.remote.bean.Word_note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/12/18 09:32
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordNoteAdapter extends RecyclerView.Adapter<WordNoteAdapter.NoteHolder> {

    private Context context;
    private List<NewWord> list;

    //选中的数据
    private Map<String,NewWord> selectMap = new HashMap<>();
    //当前状态
    private boolean isEditStatus = false;

    public WordNoteAdapter(Context context, List<NewWord> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_word_note,parent,false);
        return new NoteHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        if (holder==null){
            return;
        }

        NewWord showData = list.get(position);
        holder.word.setText(showData.word);
        if (TextUtils.isEmpty(showData.pron)){
            holder.pron.setVisibility(View.GONE);
        }else {
            holder.pron.setVisibility(View.VISIBLE);
            holder.pron.setText("["+showData.pron+"]");
        }
        holder.def.setText(showData.def);

        holder.audio.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onPlay(showData.audio);
            }
        });
        holder.check.setOnClickListener(v->{
            //设置选中和非选中
            if (selectMap.get(showData.word)==null){
                selectMap.put(showData.word,showData);
            }else {
                selectMap.remove(showData.word);
            }
            notifyDataSetChanged();

            //设置回调
            if (onItemClickListener!=null){
                onItemClickListener.onSelect(selectMap.keySet().size());
            }
        });
        holder.itemView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onSearch(showData.word);
            }
        });

        //设置编辑状态
        if (isEditStatus){
            holder.check.setVisibility(View.VISIBLE);
        }else {
            holder.check.setVisibility(View.GONE);
        }

        //检查是否选中
        NewWord selectData = selectMap.get(showData.word);
        if (selectData!=null){
            holder.check.setImageResource(R.drawable.ic_selected_default);
        }else {
            holder.check.setImageResource(R.drawable.ic_select_default);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder{

        private TextView word;
        private ImageView audio;
        private TextView pron;
        private TextView def;
        private ImageView check;

        public NoteHolder(View rootView){
            super(rootView);

            word = rootView.findViewById(R.id.word);
            audio = rootView.findViewById(R.id.audio);
            pron = rootView.findViewById(R.id.pron);
            def = rootView.findViewById(R.id.def);
            check = rootView.findViewById(R.id.check);
            check.setVisibility(View.GONE);
        }
    }

    //点击回调
    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        //点击播放
        void onPlay(String audioUrl);
        //选中回调
        void onSelect(int allSelectCount);
        //跳转查询
        void onSearch(String keyWord);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //选中的数据
    public List<NewWord> getSelectData(){
        List<NewWord> tempList = new ArrayList<>();
        if (selectMap.keySet().size()>0){
            for (String key:selectMap.keySet()){
                tempList.add(selectMap.get(key));
            }
        }
        return tempList;
    }

    //刷新数据显示
    public void refreshData(List<NewWord> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //设置编辑状态
    public void setStatus(boolean isEdit){
        this.isEditStatus = isEdit;
        if (!isEdit){
            this.selectMap.clear();
        }
        notifyDataSetChanged();
    }
}
