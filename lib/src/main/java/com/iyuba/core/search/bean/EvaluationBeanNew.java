package com.iyuba.core.search.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 新的句子评测接口数据
 * 
 * 前边有result和data，这里在代码中处理了
 */
public class EvaluationBeanNew implements Serializable {

    /**
     * sentence : How are you?
     * words : [{"index":"0","content":"How","pron":"HH AW","pron2":"həʊ","user_pron":"HH AW","user_pron2":"həʊ","score":"5.0","insert":"","delete":"","substitute_orgi":"","substitute_user":""},{"index":"1","content":"are","pron":"AA R","pron2":"ɒr","user_pron":"AA IY IY IY","user_pron2":"ɒiːiːiː","score":"0.0","insert":"IY IY","delete":"","substitute_orgi":"R","substitute_user":"IY"},{"index":"2","content":"you?","pron":"IY UW","pron2":"iːuː","user_pron":"IY IY","user_pron2":"iːiː","score":"2.5","insert":"","delete":"","substitute_orgi":"UW","substitute_user":"IY"}]
     * scores : 33
     * total_score : 1.65
     * filepath : /data/projects/voa/mp34/202206/familyalbum/20220630/16565566116350928.mp3
     * URL : wav8/202206/familyalbum/20220630/16565566116350928.mp3
     */

    private String sentence;
    private int scores;
    private double total_score;
    private String filepath;
    @SerializedName("URL")
    private String url;
    private List<WordsBean> words;

    //这里增加一些数据，仅用于searchactivity相关界面，其他界面没用到
    private String voaId;
    private String idIndex;
    private String paraId;

    public String getVoaId() {
        return voaId;
    }

    public void setVoaId(String voaId) {
        this.voaId = voaId;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(String idIndex) {
        this.idIndex = idIndex;
    }

    public String getParaId() {
        return paraId;
    }

    public void setParaId(String paraId) {
        this.paraId = paraId;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public double getTotal_score() {
        return total_score;
    }

    public void setTotal_score(double total_score) {
        this.total_score = total_score;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public void setWords(List<WordsBean> words) {
        this.words = words;
    }

    public static class WordsBean implements Serializable {
        /**
         * index : 0
         * content : How
         * pron : HH AW
         * pron2 : həʊ
         * user_pron : HH AW
         * user_pron2 : həʊ
         * score : 5.0
         * insert : 
         * delete : 
         * substitute_orgi : 
         * substitute_user : 
         */

        private String index;
        private String content;
        private String pron;
        private String pron2;
        private String user_pron;
        private String user_pron2;
        private String score;
        private String insert;
        private String delete;
        private String substitute_orgi;
        private String substitute_user;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPron() {
            return pron;
        }

        public void setPron(String pron) {
            this.pron = pron;
        }

        public String getPron2() {
            return pron2;
        }

        public void setPron2(String pron2) {
            this.pron2 = pron2;
        }

        public String getUser_pron() {
            return user_pron;
        }

        public void setUser_pron(String user_pron) {
            this.user_pron = user_pron;
        }

        public String getUser_pron2() {
            return user_pron2;
        }

        public void setUser_pron2(String user_pron2) {
            this.user_pron2 = user_pron2;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getInsert() {
            return insert;
        }

        public void setInsert(String insert) {
            this.insert = insert;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public String getSubstitute_orgi() {
            return substitute_orgi;
        }

        public void setSubstitute_orgi(String substitute_orgi) {
            this.substitute_orgi = substitute_orgi;
        }

        public String getSubstitute_user() {
            return substitute_user;
        }

        public void setSubstitute_user(String substitute_user) {
            this.substitute_user = substitute_user;
        }
    }
}
