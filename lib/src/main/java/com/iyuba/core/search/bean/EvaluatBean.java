package com.iyuba.core.search.bean;

import java.io.Serializable;
import java.util.List;

/**
 * evaluating 评测成功数据
 */
public class EvaluatBean  implements Serializable {

    /**
     * sentence : Hello Banin,
     * total_score : 2.5
     * word_count : 1
     * URL : wav/voa/580497974067406.wav
     * words : [{"content":"HELLO","index":0,"score":2.5}]
     */

    private int position;
    private String sentence;
    private String total_score;
    private String paraId;
    private String IdIndex;
    private String voaId;
    private int word_count;
    private String URL;
    private int time;
    private String nLocalMP3Path;
    private List<WordsBean> words;

    public String getnLocalMP3Path() {
        return nLocalMP3Path;
    }

    public void setnLocalMP3Path(String nLocalMP3Path) {
        this.nLocalMP3Path = nLocalMP3Path;
    }

    public String getVoaId() {
        return voaId;
    }

    public void setVoaId(String voaId) {
        this.voaId = voaId;
    }

    public String getIdIndex() {
        return IdIndex;
    }

    public void setIdIndex(String idIndex) {
        IdIndex = idIndex;
    }

    public String getParaId() {
        return paraId;
    }

    public void setParaId(String paraId) {
        this.paraId = paraId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public void setWords(List<WordsBean> words) {
        this.words = words;
    }

    public static class WordsBean implements Serializable{
        /**
         * content : HELLO
         * index : 0
         * score : 2.5
         */

        private String content;
        private int index;
        private double score;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
