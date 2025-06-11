package com.iyuba.core.lil.model.remote.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 查词回调
 * @date: 2023/12/25 17:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Word_search implements Serializable {

    /**
     * WordId : 291421
     * Word : test
     * def : ["试验","考验","测验","化验"]n.
     * ph_am : test
     * ph_am_mp3 : http://res.iciba.com/resource/amp3/1/0/09/8f/098f6bcd4621d373cade4e832627b4f6.mp3
     * titleData : [{"Category":"10","Title_Cn":"检验认可度","CreateTime":"2023-08-17 09:00:00.0","Title":"'Run Something Up the Flagpole' to Test an Idea","Texts":41,"Sound":"http://staticvip.iyuba.cn/sounds/voa/202308/17328.mp3","Pic":"http://staticvip.iyuba.cn/images/voa/202308/17328.jpg","VoaId":"17328","ReadCount":"8843"},{"Category":"6","Title_Cn":"疫情后美国数学、阅读考试成绩继续下降","CreateTime":"2023-06-28 10:00:00.0","Title":"Math, Reading Test Scores in US Continue to Drop after Pandemic","Texts":41,"Sound":"http://staticvip.iyuba.cn/sounds/voa/202306/17154.mp3","Pic":"http://staticvip.iyuba.cn/images/voa/202306/17154.jpg","VoaId":"17154","ReadCount":"12727"},{"Category":"3","Title_Cn":"测试一下你花园的土壤","CreateTime":"2023-06-01 10:00:00.0","Title":"For Healthy Plants, Test Your Garden\u2019s Soil","Texts":44,"Sound":"http://staticvip.iyuba.cn/sounds/voa/202305/17062.mp3","Pic":"http://staticvip.iyuba.cn/images/voa/202305/17062.jpg","VoaId":"17062","ReadCount":"9313"}]
     * ph_en : test
     * titleToal : 3
     * ph_en_mp3 : http://res.iciba.com/resource/amp3/oxford/0/72/b8/72b81c9d32113317d5c83a1bd78d85ac.mp3
     * textData : [{"EndTiming":"272.81","ParaId":"19","IdIndex":"3","SoundText":"http://staticvip.iyuba.cn/sounds/voa/sentence/202312/17728/17728_19_3.wav","Sentence_cn":"它还将测试周围地区的地下水。","Timing":"269.16","VoaId":"17728","Sentence":"It would also test groundwater in the surrounding area."},{"EndTiming":"278.98","ParaId":"19","IdIndex":"4","SoundText":"http://staticvip.iyuba.cn/sounds/voa/sentence/202312/17728/17728_19_4.wav","Sentence_cn":"它还需要获得许可才能测试30公里外的处理中心。","Timing":"273.49","VoaId":"17728","Sentence":"And it required permission to test a processing center 30 kilometers away."},{"EndTiming":"162.74","ParaId":"7","IdIndex":"2","SoundText":"http://staticvip.iyuba.cn/sounds/voa/sentence/202312/17718/17718_7_2.wav","Sentence_cn":"密歇根州交通部(DOT)表示，它将被用于测试和改进这项技术，为更广泛的发布做准备。","Timing":"149.56","VoaId":"17718","Sentence":"It will be used to test and improve the technology in preparation for wider releases, said Michigan's Department of Transportation (DOT)."}]
     * English : true
     * WordCn : 试验；考验；测验；化验；
     * textToal : 3
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
    private List<TitleDataBean> titleData;
    private List<TextDataBean> textData;

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

    public List<TitleDataBean> getTitleData() {
        return titleData;
    }

    public void setTitleData(List<TitleDataBean> titleData) {
        this.titleData = titleData;
    }

    public List<TextDataBean> getTextData() {
        return textData;
    }

    public void setTextData(List<TextDataBean> textData) {
        this.textData = textData;
    }

    public static class TitleDataBean {
        /**
         * Category : 10
         * Title_Cn : 检验认可度
         * CreateTime : 2023-08-17 09:00:00.0
         * Title : 'Run Something Up the Flagpole' to Test an Idea
         * Texts : 41
         * Sound : http://staticvip.iyuba.cn/sounds/voa/202308/17328.mp3
         * Pic : http://staticvip.iyuba.cn/images/voa/202308/17328.jpg
         * VoaId : 17328
         * ReadCount : 8843
         */

        private String Category;
        private String Title_Cn;
        private String CreateTime;
        private String Title;
        private int Texts;
        private String Sound;
        private String Pic;
        private String VoaId;
        private String ReadCount;

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

        public int getTexts() {
            return Texts;
        }

        public void setTexts(int Texts) {
            this.Texts = Texts;
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
    }

    public static class TextDataBean {
        /**
         * EndTiming : 272.81
         * ParaId : 19
         * IdIndex : 3
         * SoundText : http://staticvip.iyuba.cn/sounds/voa/sentence/202312/17728/17728_19_3.wav
         * Sentence_cn : 它还将测试周围地区的地下水。
         * Timing : 269.16
         * VoaId : 17728
         * Sentence : It would also test groundwater in the surrounding area.
         */

        private String EndTiming;
        private String ParaId;
        private String IdIndex;
        private String SoundText;
        private String Sentence_cn;
        private String Timing;
        private String VoaId;
        private String Sentence;

        public String getEndTiming() {
            return EndTiming;
        }

        public void setEndTiming(String EndTiming) {
            this.EndTiming = EndTiming;
        }

        public String getParaId() {
            return ParaId;
        }

        public void setParaId(String ParaId) {
            this.ParaId = ParaId;
        }

        public String getIdIndex() {
            return IdIndex;
        }

        public void setIdIndex(String IdIndex) {
            this.IdIndex = IdIndex;
        }

        public String getSoundText() {
            return SoundText;
        }

        public void setSoundText(String SoundText) {
            this.SoundText = SoundText;
        }

        public String getSentence_cn() {
            return Sentence_cn;
        }

        public void setSentence_cn(String Sentence_cn) {
            this.Sentence_cn = Sentence_cn;
        }

        public String getTiming() {
            return Timing;
        }

        public void setTiming(String Timing) {
            this.Timing = Timing;
        }

        public String getVoaId() {
            return VoaId;
        }

        public void setVoaId(String VoaId) {
            this.VoaId = VoaId;
        }

        public String getSentence() {
            return Sentence;
        }

        public void setSentence(String Sentence) {
            this.Sentence = Sentence;
        }
    }
}
