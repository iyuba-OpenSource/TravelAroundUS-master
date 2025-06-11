package com.iyuba.core.lil.model.remote.bean;

/**
 * @title: 发布到排行榜
 * @date: 2023/12/21 15:13
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Publish_result {

    /**
     * ResultCode : 501
     * AddScore : 0
     * ShuoshuoId : 20925050
     * Message : OK
     * reward : 0
     * rewardMessage :
     */

    private String ResultCode;
    private int AddScore;
    private int ShuoshuoId;
    private String Message;
    private String reward;
    private String rewardMessage;

    public String getResultCode() {
        return ResultCode;
    }

    public void setResultCode(String ResultCode) {
        this.ResultCode = ResultCode;
    }

    public int getAddScore() {
        return AddScore;
    }

    public void setAddScore(int AddScore) {
        this.AddScore = AddScore;
    }

    public int getShuoshuoId() {
        return ShuoshuoId;
    }

    public void setShuoshuoId(int ShuoshuoId) {
        this.ShuoshuoId = ShuoshuoId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRewardMessage() {
        return rewardMessage;
    }

    public void setRewardMessage(String rewardMessage) {
        this.rewardMessage = rewardMessage;
    }
}
