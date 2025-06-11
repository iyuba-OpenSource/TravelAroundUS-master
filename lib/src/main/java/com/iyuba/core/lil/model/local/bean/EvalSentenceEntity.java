package com.iyuba.core.lil.model.local.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 单句评测结果表
 * @date: 2023/12/21 14:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"voaId","userId","paraId","lineN"})
public class EvalSentenceEntity {

    @NonNull
    public int voaId;
    @NonNull
    public String paraId;//本地数据说的是 段落id
    @NonNull
    public String lineN;//本地数据说的是 句子行数
    @NonNull
    public int userId;

    //句子
    public String sentence;
    //线上的评测url
    public String audioUrl;
    //本地的评测地址
    public String localPath;
    //原始的评测分数【例如：1.75】
    public double originalScore;
    //展示的评测分数【例如：35】
    public int showScore;

    //单词数据(用string进行保存，需要的时候转换回来)
    public String evalWords;

    public EvalSentenceEntity() {
    }

    @Ignore
    public EvalSentenceEntity(int voaId, @NonNull String paraId, @NonNull String lineN, int userId, String sentence, String audioUrl, String localPath, double originalScore, int showScore, String evalWords) {
        this.voaId = voaId;
        this.paraId = paraId;
        this.lineN = lineN;
        this.userId = userId;
        this.sentence = sentence;
        this.audioUrl = audioUrl;
        this.localPath = localPath;
        this.originalScore = originalScore;
        this.showScore = showScore;
        this.evalWords = evalWords;
    }
}
