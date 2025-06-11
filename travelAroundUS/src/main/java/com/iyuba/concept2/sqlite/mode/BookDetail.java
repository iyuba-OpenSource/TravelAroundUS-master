package com.iyuba.concept2.sqlite.mode;

import java.io.Serializable;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class BookDetail implements Serializable {
    public String appImg = "";
    public String appInfo = "";
    public String appName = "";
    public String appPrice = "";
    public String authorImg = "";
    public String authorInfo = "";
    public String bookAuthor = "";
    public String bookPrice = "";
    public String classImg = "";
    public String classInfo = "";
    public String classPrice = "";
    public String contentImg = "";
    public String contentInfo = "";
    public String createTime = "";
    public String desc = "";
    public String editImg = "";
    public String editInfo = "";
    public String flg = "";
    public String groups = "";
    public String id = "";
    public String imgSrc = "";
    public String name = "";
    public String pkg = "";
    public String publishHouse = "";
    public String totalPrice = "";
    public String types = "";
    public String updateTime = "";
    public String bookId = "";

    public int num = 0;
    public String uid = "";

    public String getAppImg() {
        return appImg;
    }

    public void setAppImg(String appImg) {
        this.appImg = appImg;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPrice() {
        return appPrice;
    }

    public void setAppPrice(String appPrice) {
        this.appPrice = appPrice;
    }

    public String getAuthorImg() {
        return authorImg;
    }

    public void setAuthorImg(String authorImg) {
        this.authorImg = authorImg;
    }

    public String getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorInfo(String authorInfo) {
        this.authorInfo = authorInfo;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getClassImg() {
        return classImg;
    }

    public void setClassImg(String classImg) {
        this.classImg = classImg;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public String getClassPrice() {
        return classPrice;
    }

    public void setClassPrice(String classPrice) {
        this.classPrice = classPrice;
    }

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    public String getContentInfo() {
        return contentInfo;
    }

    public void setContentInfo(String contentInfo) {
        this.contentInfo = contentInfo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEditImg() {
        return editImg;
    }

    public void setEditImg(String editImg) {
        this.editImg = editImg;
    }

    public String getEditInfo() {
        return editInfo;
    }

    public void setEditInfo(String editInfo) {
        this.editInfo = editInfo;
    }

    public String getFlg() {
        return flg;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getPublishHouse() {
        return publishHouse;
    }

    public void setPublishHouse(String publishHouse) {
        this.publishHouse = publishHouse;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "BookDetail [appImg=" + appImg + ", appInfo=" + appInfo + ", appName=" + appName + ", appPrice="
                + appPrice + ", authorImg=" + authorImg + ", authorInfo=" + authorInfo + ", bookAuthor=" + bookAuthor + ", bookPrice=" + bookPrice + ", classImg=" + classImg + ", classInfo=" + classInfo + ", classPrice=" + classPrice + ", contentImg=" + contentImg + ", contentInfo=" + contentInfo + ", createTime=" + createTime + ", desc=" + desc + ", editImg=" + editImg + ", editInfo=" + editInfo + ", flg=" + flg + ", groups=" + groups + ", id=" + id + ", imgSrc=" + imgSrc + ", name=" + name + ", pkg=" + pkg + ", publishHouse=" + publishHouse + ", totalPrice=" + totalPrice + ", types=" + types + ", updateTime=" + updateTime + ", bookId=" + bookId + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
