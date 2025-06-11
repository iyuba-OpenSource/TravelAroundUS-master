package com.iyuba.core.lil.model.remote.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 单句评测结果数据
 * @date: 2023/12/21 14:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Eval_result implements Serializable {

    /**
     * sentence : The Blind Date  ACT I
     * words : [{"index":"0","content":"The","pron":"DH AH","pron2":"ðə","user_pron":"DH AH","user_pron2":"ðə","score":"5.0","insert":"","delete":"","substitute_orgi":"","substitute_user":""},{"index":"1","content":"Blind","pron":"B L AY N D","pron2":"blaɪnd","user_pron":"B AY AA N D T AY N D","user_pron2":"baɪɒndtaɪnd","score":"0.0","insert":"AY AA N D","delete":"","substitute_orgi":"L","substitute_user":"T"},{"index":"2","content":"Date","pron":"D EY T","pron2":"deɪt","user_pron":"R AE T","user_pron2":"ræt","score":"1.67","insert":"","delete":"","substitute_orgi":"D EY","substitute_user":"R AE"},{"index":"3","content":"ACT","pron":"AE K T","pron2":"ækt","user_pron":"AE T","user_pron2":"æt","score":"3.33","insert":"","delete":"K","substitute_orgi":"","substitute_user":""},{"index":"4","content":"I","pron":"AY","pron2":"aɪ","user_pron":"","user_pron2":"","score":"0.0","insert":"","delete":"AY","substitute_orgi":"","substitute_user":""}]
     * scores : 35
     * total_score : 1.75
     * filepath : /data/projects/voa/mp34/202312/familyalbum/20231221/17031403528577848.mp3
     * URL : wav8/202312/familyalbum/20231221/17031403528577848.mp3
     */

    private String sentence;
    private int scores;
    private double total_score;
    private String filepath;
    @SerializedName("URL")
    private String url;
    private List<WordsBean> words;

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

    public static class WordsBean {
        /**
         * index : 0
         * content : The
         * pron : DH AH
         * pron2 : ðə
         * user_pron : DH AH
         * user_pron2 : ðə
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
