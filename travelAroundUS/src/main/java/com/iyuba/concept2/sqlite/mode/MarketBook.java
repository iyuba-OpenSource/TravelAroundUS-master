package com.iyuba.concept2.sqlite.mode;

import java.io.Serializable;

/**
 * Created by ivotsm on 2017/2/20.
 */

public class MarketBook implements Serializable {
    private String bookId= "";
    private String originalPrice= "";
    private String totalPrice= "";
    private String publishHouse= "";
    private String editInfo= "";
    private String editImg= "";

//    public MarketBook() {
//        bookId = "";
//        originalPrice = "";
//        totalPrice = "";
//        publishHouse = "";
//        editInfo = "";
//        editImg = "";
//    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPublishHouse() {
        return publishHouse;
    }

    public void setPublishHouse(String publishHouse) {
        this.publishHouse = publishHouse;
    }

    public String getEditInfo() {
        return editInfo;
    }

    public void setEditInfo(String editInfo) {
        this.editInfo = editInfo;
    }

    public String getEditImg() {
        return editImg;
    }

    public void setEditImg(String editImg) {
        this.editImg = editImg;
    }

    @Override
    public String toString() {
        return "MarketBook [bookId=" + bookId + ", originalPrice=" + originalPrice + ", totalPrice=" + totalPrice + ", publishHouse="
                + publishHouse + ", editInfo=" + editInfo + ", editImg=" + editImg
                + "]";
    }
}
