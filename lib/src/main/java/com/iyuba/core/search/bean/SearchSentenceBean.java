package com.iyuba.core.search.bean;

/**
 * 搜索句子数据
 */
public class SearchSentenceBean {

    /**
     * EndTiming : 177.3
     * ParaId : 14
     * IdIndex : 1
     * SoundText : http://static."+Constant.IYBHttpHead+"/sounds/voa/sentence/201812/7364/7364_14_1.wav
     * Sentence_cn : 戴恩和他的家人研究了塞弗伦斯镇的其他规定，包括一条把宠物只定义为猫和狗的规定。
     * Timing : 166.6
     * VoaId : 7364
     * Sentence : Dane and his family have researched other Severance rules, including one that defines
     * pets only as cats and dogs.
     */

    private String EndTiming;
    private String ParaId;
    private String IdIndex;
    private String SoundText;
    private String Sentence_cn;
    private String Timing;
    private String VoaId;
    private String Sentence;
    private boolean isClick;//是否被选择
    private boolean isTest;//是否录音过
    private boolean isSendOut=false;//是否发送过分享
    private boolean isPlay=false;

    //替换数据
//    private EvaluatBean evaluatBean;
//    public EvaluatBean getEvaluateBean() {
//        return evaluatBean;
//    }
//    public void setEvaluatBean(EvaluatBean evaluatBean) {
//        this.evaluatBean = evaluatBean;
//    }
    private EvaluationBeanNew evaluatBean;

    public EvaluationBeanNew getEvaluatBean() {
        return evaluatBean;
    }

    public void setEvaluatBean(EvaluationBeanNew evaluatBean) {
        this.evaluatBean = evaluatBean;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean isSendOut() {
        return isSendOut;
    }

    public void setSendOut(boolean sendOut) {
        isSendOut = sendOut;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public boolean isClick() {

        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

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
