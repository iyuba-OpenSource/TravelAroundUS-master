package com.iyuba.core.lil.model.remote.manager;

import com.iyuba.configation.Constant;
import com.iyuba.core.lil.model.remote.RemoteHelper;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_click_result;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_clock_submit;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_result;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_reward_vip;
import com.iyuba.core.lil.model.remote.bean.ad.Ad_stream_result;
import com.iyuba.core.lil.remote.service.LibAdService;
import com.iyuba.core.lil.util.LibEncodeUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * @title: 广告接口管理
 * @date: 2024/1/4 10:27
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class AdRemoteManager {

    //接口-获取广告信息(开屏、插屏、banner)
    //开屏：0，banner：4
    public static Observable<List<Ad_result>> getAd(int userId, int adFlag, int adAppId){
        String url = "http://dev."+ Constant.IYBHttpHead()+"/getAdEntryAll.jsp";

        LibAdService commonService = RemoteHelper.getInstance().createJson(LibAdService.class);
        return commonService.getAdInfo(url,userId,adAppId,adFlag);
    }

    //接口-获取信息流广告数据
    //http://dev.iyuba.cn/getAdEntryAll.jsp
    public static Observable<List<Ad_stream_result>> getTemplateAd(int userId, int flag, int appId){
        String url = "http://dev."+ Constant.IYBHttpHead()+"/getAdEntryAll.jsp";

        LibAdService adService = RemoteHelper.getInstance().createJson(LibAdService.class);
        return adService.getStreamAd(url,appId,userId,flag);
    }

    //接口-点击广告获取奖励
    public static Observable<Ad_click_result> getAdClickReward(int uid, int platform, int adSpace){
        String url = "http://api."+Constant.IYBHttpHead()+"/credits/adClickReward.jsp";

        int appId = Integer.parseInt(Constant.APPID);
        long timestamp = System.currentTimeMillis()/1000L;
        String sign = LibEncodeUtil.md5(String.valueOf(uid)+String.valueOf(appId)+"iyubaV2"+String.valueOf(timestamp));

        LibAdService commonService = RemoteHelper.getInstance().createJson(LibAdService.class);
        return commonService.getAdClick(url,uid,appId,platform,adSpace,timestamp,sign);
    }

    //接口-激励广告获取vip
    public static Observable<Ad_reward_vip> getAdRewardVip(int userId){
        String url = "http://vip."+Constant.IYBHttpHead()+"/openIncentiveVip.jsp";

        int appId = Integer.parseInt(Constant.APPID);
        long timestamp = System.currentTimeMillis()/1000L;
        String sign = LibEncodeUtil.md5(String.valueOf(userId)+String.valueOf(appId)+"iyubaV2"+String.valueOf(timestamp));

        LibAdService commonService = RemoteHelper.getInstance().createJson(LibAdService.class);
        return commonService.getAdRewardVip(url,userId,appId,timestamp,sign);
    }

    //接口-提交广告数据
    public static Observable<Ad_clock_submit> submitAdData(int userId, String device, String deviceId, String packageName, String ads){
        String url = "http://iuserspeech."+Constant.IYBHttpHead()+":9001/japanapi/addAdInfo.jsp";

        int appId = Integer.parseInt(Constant.APPID);
        long timestamp = System.currentTimeMillis()/1000L;
        int os = 2;

        LibAdService commonService = RemoteHelper.getInstance().createJson(LibAdService.class);
        return commonService.submitAdData(url,String.valueOf(timestamp),appId,device,deviceId,userId,packageName,os,ads);
    }
}
