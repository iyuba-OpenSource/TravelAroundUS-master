package com.iyuba.core.search.bean;

/**
 * 搜索文章数据
 */
public class SearchArticleBean {

    /**
     * Category : 3
     * Title_Cn : 老布什的服务犬守在灵柩前陪其走完最后旅程
     * CreateTime : 2018-12-08 07:06:35.0
     * Title : George H.W. Bush’s Service Dog Sully Stays by His Side
     * Sound : http://static."+Constant.IYBHttpHead+"/sounds/voa/201812/7360.mp3
     * Pic : http://static."+Constant.IYBHttpHead+"/images/voa/7360.jpg
     * VoaId : 7360
     * ReadCount : 80222
     */

    private String Category;
    private String Title_Cn;
    private String CreateTime;
    private String Title;
    private String Sound;
    private String Pic;
    private String VoaId;
    private String ReadCount;
    private boolean isClick=false;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getTitle_Cn() {
        return Title_Cn;
    }

    public void setTitle_Cn(String Title_Cn) {
        this.Title_Cn = Title_Cn;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getSound() {
        return Sound;
    }

    public void setSound(String Sound) {
        this.Sound = Sound;
    }

    public String getPic() {
        return Pic;
    }

    public void setPic(String Pic) {
        this.Pic = Pic;
    }

    public String getVoaId() {
        return VoaId;
    }

    public void setVoaId(String VoaId) {
        this.VoaId = VoaId;
    }

    public String getReadCount() {
        return ReadCount;
    }

    public void setReadCount(String ReadCount) {
        this.ReadCount = ReadCount;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
