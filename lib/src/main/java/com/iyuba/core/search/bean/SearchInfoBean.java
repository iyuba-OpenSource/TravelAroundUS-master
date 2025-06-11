package com.iyuba.core.search.bean;

import java.util.List;

public class SearchInfoBean {
    /**
     * WordId : 288467
     * Word : dog
     * def : ["狗","蹩脚货","丑女人","卑鄙小人"]n.
     * ph_am : d%C9%94g
     * ph_am_mp3 : http://res.iciba.com/resource/amp3/1/0/06/d8/06d80eb0c50b49a509b49f2424e8c805.mp3
     * ph_en : d%C9%92g
     * titleToal : 1
     * ph_en_mp3 : http://res.iciba.com/resource/amp3/0/0/06/d8/06d80eb0c50b49a509b49f2424e8c805.mp3
     * English : true
     * WordCn : 狗；蹩脚货；丑女人；卑鄙小人；
     * textToal : 1
     *
     * textData : [{"EndTiming":"177.3","ParaId":"14","IdIndex":"1","SoundText":"http://static."+Constant.IYBHttpHead+"/sounds/voa/sentence/201812/7364/7364_14_1.wav","Sentence_cn":"戴恩和他的家人研究了塞弗伦斯镇的其他规定，包括一条把宠物只定义为猫和狗的规定。","Timing":"166.6","VoaId":"7364","Sentence":"Dane and his family have researched other Severance rules, including one that defines pets only as cats and dogs. "}]
     * titleData : [{"Category":"3","Title_Cn":"老布什的服务犬守在灵柩前陪其走完最后旅程","CreateTime":"2018-12-08 07:06:35.0","Title":"George H.W. Bush\u2019s Service Dog Sully Stays by His Side","Sound":"http://static."+Constant.IYBHttpHead+"/sounds/voa/201812/7360.mp3","Pic":"http://static."+Constant.IYBHttpHead+"/images/voa/7360.jpg","VoaId":"7360","ReadCount":"80222"}]
     */

    private String WordId;
    private String Word;
    private String def;
    private String ph_am;
    private String ph_am_mp3;
    private String ph_en;
    private int titleToal;
    private String ph_en_mp3;
    private boolean English;
    private String WordCn;
    private int textToal;
    private boolean isCollectWord;
    private List<SearchArticleBean> titleData;
    private List<SearchSentenceBean> textData;

    public boolean isCollectWord() {
        return isCollectWord;
    }

    public void setCollectWord(boolean collectWord) {
        isCollectWord = collectWord;
    }

    public String getWordId() {
        return WordId;
    }

    public void setWordId(String WordId) {
        this.WordId = WordId;
    }

    public String getWord() {
        return Word;
    }

    public void setWord(String Word) {
        this.Word = Word;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getPh_am() {
        return ph_am;
    }

    public void setPh_am(String ph_am) {
        this.ph_am = ph_am;
    }

    public String getPh_am_mp3() {
        return ph_am_mp3;
    }

    public void setPh_am_mp3(String ph_am_mp3) {
        this.ph_am_mp3 = ph_am_mp3;
    }

    public String getPh_en() {
        return ph_en;
    }

    public void setPh_en(String ph_en) {
        this.ph_en = ph_en;
    }

    public int getTitleToal() {
        return titleToal;
    }

    public void setTitleToal(int titleToal) {
        this.titleToal = titleToal;
    }

    public String getPh_en_mp3() {
        return ph_en_mp3;
    }

    public void setPh_en_mp3(String ph_en_mp3) {
        this.ph_en_mp3 = ph_en_mp3;
    }

    public boolean isEnglish() {
        return English;
    }

    public void setEnglish(boolean English) {
        this.English = English;
    }

    public String getWordCn() {
        return WordCn;
    }

    public void setWordCn(String WordCn) {
        this.WordCn = WordCn;
    }

    public int getTextToal() {
        return textToal;
    }

    public void setTextToal(int textToal) {
        this.textToal = textToal;
    }

    public List<SearchArticleBean> getTitleData() {
        return titleData;
    }

    public void setTitleData(List<SearchArticleBean> titleData) {
        this.titleData = titleData;
    }

    public List<SearchSentenceBean> getTextData() {
        return textData;
    }

    public void setTextData(List<SearchSentenceBean> textData) {
        this.textData = textData;
    }

}
