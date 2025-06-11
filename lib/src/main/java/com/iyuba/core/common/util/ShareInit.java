package com.iyuba.core.common.util;

import android.content.Context;

import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.mob.MobSDK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareInit {

    public void initShare(Context context){

        MobSDK.init(context,"19a1773990a19", "e05c1fa7afaff36759e5728b6157f78b");
        MobSDK.submitPolicyGrantResult(true);
        initMobConfig();
    }

    /**
     * init mob share's confignation
     */
    private void initMobConfig(){
        List<String> list=new ArrayList<>();
        list.add("QQ");
        list.add("QQ空间");
        list.add("新浪微博");
        list.add("微信好友");
        list.add("微信收藏");
        list.add("微信朋友圈");

//        ShareBottomDialog.setSharedPlatform(list);

        //因为在gradle里 配置了平台信息，所以这里就不需要再配置
        initPlatforms();

        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);
    }
    private static void initPlatforms() {
        String wechatAppId = "wxfa4799656aff72d9";
        String wechatAppSecret = "a9c2d3ef501e9dfd16074d366086855a";
        String qqId = "1104787899";
        String qqKey = "l9ZZLPFaZsa6AmoX";
        String sinaKey = "4128172096";
        String sinaSecret = "72976fa4926482dbc017889a293ce49f";
        setDevInfo(QQ.NAME, qqId, qqKey);
        setDevInfo(QZone.NAME, qqId, qqKey);
        setDevInfo(SinaWeibo.NAME, sinaKey, sinaSecret);
        setDevInfo(Wechat.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatMoments.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatFavorite.NAME, wechatAppId, wechatAppSecret);
    }

    private static void setDevInfo(String platform, String str1, String str2) {
        HashMap<String, Object> devInfo = new HashMap<>();
        if (SinaWeibo.NAME.equals(platform)) {
            devInfo.put("Id", "1");
            devInfo.put("SortId", "1");
            devInfo.put("AppKey", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("RedirectUrl", "http://iyuba.cn");
            devInfo.put("ShareByAppClient", "true");
        } else if (QQ.NAME.equals(platform)) {
            devInfo.put("Id", "2");
            devInfo.put("SortId", "2");
            devInfo.put("AppId", str1);
            devInfo.put("AppKey", str2);
            devInfo.put("Enable", "true");
            devInfo.put("ShareByAppClient", "true");
        } else if (QZone.NAME.equals(platform)) {
            devInfo.put("Id", "3");
            devInfo.put("SortId", "3");
            devInfo.put("AppId", str1);
            devInfo.put("AppKey", str2);
            devInfo.put("Enable", "true");
            devInfo.put("ShareByAppClient", "true");
        } else if (Wechat.NAME.equals(platform)) {
            devInfo.put("Id", "4");
            devInfo.put("SortId", "4");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("BypassApproval", "false");
        } else if (WechatMoments.NAME.equals(platform)) {
            devInfo.put("Id", "5");
            devInfo.put("SortId", "5");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("BypassApproval", "false");
        } else if (WechatFavorite.NAME.equals(platform)) {
            devInfo.put("Id", "6");
            devInfo.put("SortId", "6");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
        }
        ShareSDK.setPlatformDevInfo(platform, devInfo);
    }

}
