package com.iyuba.concept2.sqlite.mode;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/4.
 */

public class RankUser implements Serializable {
    private String uid = "";
    private String sort = "";
    private String count = "";
    private String scores = "";
    private String imgSrc = "";
    private String name = "";
    private String ranking = "";


    public RankUser() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        return "RankUser [sort=" + sort + ", uid=" + uid + ", name=" + name + ", imgSrc="
                + imgSrc + ", count=" + count + ", scores=" + scores
                + ", ranking=" + ranking + "]";
    }
}
