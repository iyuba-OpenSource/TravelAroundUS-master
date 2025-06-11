package com.iyuba.concept2.sqlite.mode;

import java.io.Serializable;

public class StudyTimeBeanNew implements Serializable {
    private String result;
    private String totalWord;
    private String totalTime;
    private String totalUser;
    private String ranking;
    private String totalDaysTime;
    private String totalWords;
    private String totalDays;
    private String sentence;
    private String shareId;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setTotalWord(String totalWord) {
        this.totalWord = totalWord;
    }

    public String getTotalWord() {
        return totalWord;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalUser(String totalUser) {
        this.totalUser = totalUser;
    }

    public String getTotalUser() {
        return totalUser;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getRanking() {
        return ranking;
    }

    public void setTotalDaysTime(String totalDaysTime) {
        this.totalDaysTime = totalDaysTime;
    }

    public String getTotalDaysTime() {
        return totalDaysTime;
    }

    public void setTotalWords(String totalWords) {
        this.totalWords = totalWords;
    }

    public String getTotalWords() {
        return totalWords;
    }

    public void setTotalDays(String totalDays) {
        this.totalDays = totalDays;
    }

    public String getTotalDays() {
        return totalDays;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareId() {
        return shareId;
    }

    @Override
    public String toString() {
        return "StudyTimeBeanNew{" +
                "result='" + result + '\'' +
                ", totalWord='" + totalWord + '\'' +
                ", totalTime='" + totalTime + '\'' +
                ", totalUser='" + totalUser + '\'' +
                ", ranking='" + ranking + '\'' +
                ", totalDaysTime='" + totalDaysTime + '\'' +
                ", totalWords='" + totalWords + '\'' +
                ", totalDays='" + totalDays + '\'' +
                ", sentence='" + sentence + '\'' +
                ", shareId='" + shareId + '\'' +
                '}';
    }
}
