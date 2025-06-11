package com.iyuba.concept2.lil.ui.search.sentence;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.util.GsonUtils;
import com.iyuba.concept2.widget.RoundProgressBar;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.model.local.bean.EvalSentenceEntity;
import com.iyuba.core.lil.model.local.manager.HelpDataManager;
import com.iyuba.core.lil.model.remote.bean.Eval_result;
import com.iyuba.core.lil.model.remote.bean.Word_search;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibSpanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title: 新搜索界面-句子适配器
 * @date: 2023/12/25 16:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchSentenceAdapter extends RecyclerView.Adapter<NewSearchSentenceAdapter.SentenceHolder> {

    private Context context;
    private List<Word_search.TextDataBean> list;

    //选中的位置
    private int selectPosition = 0;
    //选中的样式
    private SentenceHolder selectHolder;
    //选中的显示数据
    private Word_search.TextDataBean selectData;
    //选中的评测数据
    private EvalSentenceEntity selectEvalData;

    //分享数据
    private Map<String,Integer> shareMap = new HashMap<>();

    public NewSearchSentenceAdapter(Context context, List<Word_search.TextDataBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SentenceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.listitem_read_new,parent,false);
        return new SentenceHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceHolder holder, int position) {
        if (holder==null){
            return;
        }

        Word_search.TextDataBean dataBean = list.get(position);
        holder.indexView.setText(String.valueOf(position+1));
        holder.sentenceView.setText(dataBean.getSentence());
        holder.sentenceCnView.setText(dataBean.getSentence_cn());

        //展示评测的数据
        EvalSentenceEntity curEvalData = HelpDataManager.getInstance().getSingleEvalData(Integer.parseInt(dataBean.getVoaId()), UserInfoManager.getInstance().getUserId(), dataBean.getParaId(),dataBean.getIdIndex());
        if (curEvalData!=null){
            //获取当前的单词数据
            List<Eval_result.WordsBean> wordList = GsonUtils.toObjectList(curEvalData.evalWords,Eval_result.WordsBean.class);
            holder.sentenceView.setText(LibSpanUtil.showSpan(transEvalToWord(wordList),dataBean.getSentence()));

            //其他操作显示
            holder.evalOneView.setVisibility(View.VISIBLE);
            holder.evalView.setVisibility(View.GONE);

            holder.publishView.setVisibility(View.VISIBLE);
            holder.scoreView.setVisibility(View.VISIBLE);
            showEvalScoreView(holder,curEvalData.showScore);

            //纠音显示
            /*if (selectPosition == position){
                String firstErrorWord = getFirstEvalErrorWord(wordList);
                if (firstErrorWord==null){
                    holder.checkLayout.setVisibility(View.GONE);
                }else {
                    holder.checkLayout.setVisibility(View.VISIBLE);
                    holder.checkMsgView.setText(firstErrorWord+" 单词发音有误");
                }
            }else {
                holder.checkLayout.setVisibility(View.GONE);
            }*/
            if (selectPosition == position && getCheckStatus(wordList)){
                holder.checkView.setVisibility(View.VISIBLE);
            }else {
                holder.checkView.setVisibility(View.INVISIBLE);
            }
        }else {
            holder.sentenceView.setText(dataBean.getSentence());

            holder.evalOneView.setVisibility(View.GONE);
            holder.evalView.setVisibility(View.GONE);

            holder.publishView.setVisibility(View.INVISIBLE);
            holder.scoreView.setVisibility(View.INVISIBLE);
            holder.checkView.setVisibility(View.INVISIBLE);
        }

        //展示分享按钮
        String itemId = dataBean.getVoaId()+"_"+dataBean.getParaId()+"_"+dataBean.getIdIndex();
        if (shareMap.get(itemId)==null){
            holder.shareView.setVisibility(View.INVISIBLE);
        }else {
            holder.shareView.setVisibility(View.VISIBLE);
        }

        //设置选中数据显示
        if (position == selectPosition){
            holder.bottomLayout.setVisibility(View.VISIBLE);

            selectHolder = holder;
            selectData = dataBean;
            selectEvalData = curEvalData;
        }else {
            holder.bottomLayout.setVisibility(View.GONE);
        }

        //设置点击操作
        holder.itemView.setOnClickListener(v->{
            //切换item
            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.onItemSwitch(position);
            }
        });
        holder.playView.setOnClickListener(v->{
            //原文播放
            /*double startData = Double.parseDouble(TextUtils.isEmpty(selectData.getTiming())?"0":selectData.getTiming());
            double endData = Double.parseDouble(TextUtils.isEmpty(selectData.getEndTiming())?"0":selectData.getEndTiming());

            long startTime = (long) (startData*1000L);
            long endTime = (long) (endData*1000L);*/

            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.onPlayAudio(selectData.getSoundText());
            }
        });
        holder.recordView.setOnClickListener(v->{
            //录音
            double startData = Double.parseDouble(TextUtils.isEmpty(selectData.getTiming())?"0":selectData.getTiming());
            double endData = Double.parseDouble(TextUtils.isEmpty(selectData.getEndTiming())?"0":selectData.getEndTiming());

            long startTime = (long) (startData*1000L);
            long endTime = (long) (endData*1000L);
            long totalTime = endTime-startTime;
            //这里优化处理下，如果时间<2s，则设置为2s
            if (totalTime<2*1000L){
                totalTime = 2*1000L;
            }

            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.onRecord(Integer.parseInt(selectData.getVoaId()),selectData.getParaId(),selectData.getIdIndex(),selectData.getSentence(),totalTime);
            }
        });
        holder.evalLayout.setOnClickListener(v->{
            //评测播放
            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.onEvalPlay(Integer.parseInt(selectData.getVoaId()),selectData.getParaId(),selectData.getIdIndex(),selectEvalData.audioUrl);
            }
        });
        holder.publishView.setOnClickListener(v->{
            //发布单句评测
            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.onPublish(Integer.parseInt(selectData.getVoaId()),selectData.getParaId(),selectData.getIdIndex(),String.valueOf(selectEvalData.showScore),selectEvalData.audioUrl);
            }
        });
        holder.shareView.setOnClickListener(v->{
            //分享
            String selectShareId = selectData.getVoaId()+"_"+selectData.getParaId()+"_"+selectData.getIdIndex();
            if (shareMap.get(selectShareId)==null){
                ToastUtil.showToast(context,"分享内容不存在");
                return;
            }

            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.onShare(Integer.parseInt(selectData.getVoaId()),selectData.getSentence(),String.valueOf(selectEvalData.showScore),selectEvalData.audioUrl,shareMap.get(selectShareId));
            }
        });
        holder.checkView.setOnClickListener(v->{
            //纠音
            if (onEvalItemClickListener!=null){
                onEvalItemClickListener.checkWord(selectData,selectEvalData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class SentenceHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private TextView sentenceView;
        private TextView sentenceCnView;

        private LinearLayout bottomLayout;
        private RoundProgressBar playView;
        private RoundProgressBar recordView;

        private FrameLayout evalLayout;
        private ImageView evalOneView;
        private RoundProgressBar evalView;

        private ImageView publishView;
        private ImageView shareView;
        private TextView scoreView;

        //这里纠音功能暂时不使用了，因为遮挡问题，使用下面一个
        private LinearLayout checkLayout;
        private TextView checkMsgView;
        private Button checkBtnView;

        private TextView checkView;

        public SentenceHolder(View rootView){
            super(rootView);

            indexView = rootView.findViewById(R.id.sen_index);
            sentenceView = rootView.findViewById(R.id.sen_en);
            sentenceCnView = rootView.findViewById(R.id.sen_zh);

            bottomLayout = rootView.findViewById(R.id.bottom_view);
            playView = rootView.findViewById(R.id.sen_play);
            playView.setCricleProgressColor(R.color.colorPrimary);
            recordView = rootView.findViewById(R.id.sen_i_read);

            evalLayout = rootView.findViewById(R.id.sen_sound_button);
            evalOneView = rootView.findViewById(R.id.sen_read_play);
            evalView = rootView.findViewById(R.id.sen_read_playing);

            publishView = rootView.findViewById(R.id.sen_read_send);
            shareView = rootView.findViewById(R.id.sen_read_collect);
            scoreView = rootView.findViewById(R.id.sen_read_result);

            checkLayout = rootView.findViewById(R.id.word_correct);
            checkMsgView = rootView.findViewById(R.id.word_correct_msg);
            checkBtnView = rootView.findViewById(R.id.word_correct_commit);

            checkView = rootView.findViewById(R.id.tv_check);
        }
    }

    /**********************************刷新数据*******************************/
    public void refreshData(List<Word_search.TextDataBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //保存分享的id
    public void saveShareId(int voaId,String papaId,String lineN,int shareId){
        String itemId = voaId+"_"+papaId+"_"+lineN;
        shareMap.put(itemId,shareId);
    }

    /*********************************回调接口*******************************/
    private OnEvalItemClickListener onEvalItemClickListener;

    public interface OnEvalItemClickListener{
        //切换item
        void onItemSwitch(int position);
        //播放原文
        void onPlayAudio(String audioUrl);
        //录音
        void onRecord(int voaId,String paraId,String lineN,String sentence,long totalTime);
        //播放评测
        void onEvalPlay(int voaId,String paraId,String lineN,String audioUrl);
        //发布评测
        void onPublish(int voaId,String paraId,String lineN,String score,String audioUrl);
        //分享评测
        void onShare(int voaId,String sentence,String score,String audioUrl,int shareId);
        //纠音显示
        void checkWord(Word_search.TextDataBean voaDetail,EvalSentenceEntity sentenceEntity);
    }

    public void setOnEvalItemClickListener(OnEvalItemClickListener onEvalItemClickListener) {
        this.onEvalItemClickListener = onEvalItemClickListener;
    }

    /************************************刷新样式*****************************/
    //刷新item显示
    public void refreshItem(int position){
        this.selectPosition = position;
        notifyDataSetChanged();
    }

    //刷新原文播放样式
    public void refreshPlayView(long progress,long total,boolean isPlay){
        if (selectHolder==null){
            return;
        }

        if (isPlay){
            selectHolder.playView.setBackgroundResource(R.drawable.sen_stop);
            if (progress<=0){
                progress = 0;
            }
            if (total<=0){
                total=0;
                progress = 0;
            }
            selectHolder.playView.setProgress((int) progress);
            selectHolder.playView.setMax((int) total);
        }else {
            selectHolder.playView.setBackgroundResource(R.drawable.sen_play);
            selectHolder.playView.setProgress(0);
        }
    }

    //刷新录音样式
    public void refreshRecordView(long progress,long max,boolean isRecord){
        if (selectHolder==null){
            return;
        }

        if (isRecord){
            selectHolder.recordView.setBackgroundResource(R.drawable.sen_i_stop);
            if (progress<0){
                progress = 0;
            }
            if (max<0){
                max = 0;
                progress = 0;
            }
            selectHolder.recordView.setMax((int) max);
            selectHolder.recordView.setProgress((int) progress);
        }else {
            selectHolder.recordView.setBackgroundResource(R.drawable.sen_i_read);
            selectHolder.recordView.setProgress(0);
        }
    }

    //刷新评测播放样式
    public void refreshEvalView(long progress,long total,boolean isPlay){
        if (selectHolder==null){
            return;
        }

        //根据评测内容显示
        EvalSentenceEntity sentenceEntity = HelpDataManager.getInstance().getSingleEvalData(Integer.parseInt(selectData.getVoaId()),UserInfoManager.getInstance().getUserId(),selectData.getParaId(),selectData.getIdIndex());
        if (sentenceEntity==null){
            return;
        }

        if (isPlay){
            selectHolder.evalOneView.setVisibility(View.GONE);
            selectHolder.evalView.setVisibility(View.VISIBLE);
            /*if (progress<=0){
                progress = 0;
            }
            if (total<=0){
                total=0;
                progress = 0;
            }
            selectHolder.playView.setProgress((int) progress);
            selectHolder.playView.setMax((int) total);*/
        }else {
            selectHolder.evalOneView.setVisibility(View.VISIBLE);
            selectHolder.evalView.setVisibility(View.GONE);
//            selectHolder.playView.setProgress(0);
        }
    }

    /***************************************其他功能******************************/
    //将评测数据转换为合适的单词数据
    private List<Pair<String,Double>> transEvalToWord(List<Eval_result.WordsBean> list){
        List<Pair<String,Double>> pairList = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Eval_result.WordsBean bean = list.get(i);
                double score = TextUtils.isEmpty(bean.getScore())?0:Double.parseDouble(bean.getScore());

                pairList.add(new Pair<>(bean.getContent(),score));
            }
        }

        return pairList;
    }

    //获取评测的第一个错误数据
    private String getFirstEvalErrorWord(List<Eval_result.WordsBean> wordList){
        String firstWord = null;
        if (wordList!=null&&wordList.size()>0){
            checkWord:for (int i = 0; i < wordList.size(); i++) {
                Eval_result.WordsBean wordsBean = wordList.get(i);
                double wordScore = Double.parseDouble(wordsBean.getScore());
                if (wordScore<=LibSpanUtil.tag_low_score){
                    firstWord = wordsBean.getContent();
                    break checkWord;
                }
            }
        }

        return firstWord;
    }

    //设置成绩显示
    private void showEvalScoreView(SentenceHolder holder, int showScore){
        if (showScore < 50) {
            holder.scoreView.setBackgroundResource(R.drawable.sen_score_lower60);
            holder.scoreView.setText("");
        } else if (showScore > 80) {
            holder.scoreView.setText(String.valueOf(showScore));
            holder.scoreView.setBackgroundResource(R.drawable.sen_score_higher_80);
        } else {
            holder.scoreView.setText(String.valueOf(showScore));
            holder.scoreView.setBackgroundResource(R.drawable.sen_score_60_80);
        }
    }

    //获取是否需要显示纠音
    private boolean getCheckStatus(List<Eval_result.WordsBean> list){
        boolean isCheck = false;

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Eval_result.WordsBean wordsBean = list.get(i);
                double showScore = TextUtils.isEmpty(wordsBean.getScore())?0:Double.parseDouble(wordsBean.getScore());
                if (showScore<2.5){
                    isCheck = true;
                }
            }
        }

        return isCheck;
    }
}
