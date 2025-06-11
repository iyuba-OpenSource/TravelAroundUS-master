package com.iyuba.concept2.widget.sharedialog;

import com.iyuba.configation.Constant;
import com.iyuba.concept2.sqlite.mode.Shareable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class CallPlatform {
	private static final String TAG = CallPlatform.class.getSimpleName();
	
	int SHARE_ARTICLE = 0;
	int SHARE_REREAD = 1;
	String praise = "这篇来自爱语吧走遍美国的文章很不错，推荐大家学一学！";
	private HashMap<String, Object> item;
	private Shareable stuff;
	private ShareParams sp;
	private PlatformActionListener pal;
	int kind;
	
	public CallPlatform(HashMap<String, Object> item, Shareable stuff, PlatformActionListener pal, int kind) {
		this.item = item;
		this.stuff = stuff;
		this.pal = pal;
		this.kind = kind;
		try {
			setParams();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	} 

	
	
	private void setParams() throws UnsupportedEncodingException {
	  sp = new ShareParams();
	  if(kind == SHARE_REREAD){                                        //分享跟读
		if (item.get("ItemText").equals("QQ")) {
			sp.setTitle(stuff.getShareTitle());
			sp.setTitleUrl(stuff.getShareUrl());
			sp.setText(stuff.getShareShortText());
			sp.setImageUrl(stuff.getShareImageUrl());
			Platform qq = ShareSDK.getPlatform(QQ.NAME);
			qq.setPlatformActionListener(pal);
			qq.share(sp);
		} else if (item.get("ItemText").equals("微信好友")) {
			sp.setShareType(Platform.SHARE_MUSIC);
			sp.setUrl(stuff.getShareUrl());
			sp.setTitle(stuff.getShareTitle());
			sp.setTitleUrl(stuff.getShareUrl()); 		
			sp.setText(stuff.getShareShortText());
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setMusicUrl(stuff.getShareAudioUrl());
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			wechat.setPlatformActionListener(pal);
			wechat.share(sp);

			
		} else if (item.get("ItemText").equals("微信朋友圈")) {
			sp.setShareType(Platform.SHARE_MUSIC);
			sp.setTitle(stuff.getShareTitle());
			sp.setTitleUrl(stuff.getShareUrl());
			sp.setText(stuff.getShareShortText());
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setUrl(stuff.getShareUrl());
			sp.setMusicUrl(stuff.getShareAudioUrl());
			Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
			wechatMoments.setPlatformActionListener(pal);
			wechatMoments.share(sp);
		} else if (item.get("ItemText").equals("新浪微博")) {
			sp.setText(stuff.getShareLongText());
			sp.setImageUrl(stuff.getShareImageUrl());
			Platform sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
			sinaWeibo.SSOSetting(true);
			sinaWeibo.setPlatformActionListener(pal);
			sinaWeibo.share(sp);
		} else if (item.get("ItemText").equals("QQ空间")) {
			sp.setTitle(stuff.getShareTitle());
			sp.setTitleUrl(stuff.getShareUrl());
			sp.setText(stuff.getShareShortText());
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setSite(Constant.APPName);
			sp.setSiteUrl(stuff.getShareUrl());
			Platform qZone = ShareSDK.getPlatform(QZone.NAME);
			qZone.setPlatformActionListener(pal);
			qZone.share(sp);
		} else if(item.get("ItemText").equals("微信收藏")){
			sp.setShareType(Platform.SHARE_MUSIC);
			sp.setTitle(stuff.getShareTitle());
			sp.setTitleUrl(stuff.getShareUrl());
			sp.setText(stuff.getShareShortText());
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setUrl(stuff.getShareUrl());
			sp.setMusicUrl(stuff.getShareAudioUrl());
			Platform wechatFavorite = ShareSDK.getPlatform(WechatFavorite.NAME);
			wechatFavorite.setPlatformActionListener(pal);
			wechatFavorite.share(sp);
		}
		
	}else{                                                                                     //分享文章
		
		if (item.get("ItemText").equals("QQ")) {
			sp.setTitle(stuff.getArticleShareShortText());
			sp.setTitleUrl(stuff.getArticleShareUrl());
			sp.setText(stuff.getArticleShareShortTextCn() + "\n" + praise);
			sp.setImageUrl(stuff.getShareImageUrl());
			Platform qq = ShareSDK.getPlatform(QQ.NAME);
			qq.setPlatformActionListener(pal);
			qq.share(sp);
		} else if (item.get("ItemText").equals("微信好友")) {
			sp.setShareType(Platform.SHARE_WEBPAGE);
			sp.setUrl(stuff.getArticleShareUrl());
			sp.setTitle(stuff.getArticleShareShortText() + "\n" + stuff.getArticleShareShortTextCn());
			sp.setTitleUrl(stuff.getArticleShareUrl()); 		
			sp.setText(stuff.getArticleShareShortTextCn() + "\n" + praise);
			sp.setImageUrl(stuff.getShareImageUrl());
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			wechat.setPlatformActionListener(pal);
			wechat.share(sp);
		} else if (item.get("ItemText").equals("微信朋友圈")) {
			sp.setShareType(Platform.SHARE_WEBPAGE);
			sp.setTitle(stuff.getArticleShareShortText() + "\n" + stuff.getArticleShareShortTextCn());
			sp.setTitleUrl(stuff.getArticleShareUrl());
			sp.setText(stuff.getArticleShareShortTextCn() + "\n" + praise);
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setUrl(stuff.getArticleShareUrl());
			Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
			wechatMoments.setPlatformActionListener(pal);
			wechatMoments.share(sp);
		} else if (item.get("ItemText").equals("新浪微博")) {
			sp.setText(stuff.getArticleShareShortText() + "\n" + stuff.getArticleShareShortTextCn() + "\n" + praise + "\n"
		           + stuff.getArticleShareUrl()
					);
			sp.setImageUrl(stuff.getShareImageUrl());
			Platform sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
			sinaWeibo.SSOSetting(true);
			sinaWeibo.setPlatformActionListener(pal);
			sinaWeibo.share(sp);
		} else if (item.get("ItemText").equals("QQ空间")) {
			sp.setTitle(stuff.getArticleShareShortText());
			sp.setTitleUrl(stuff.getArticleShareUrl());
			sp.setText(stuff.getArticleShareShortTextCn() + "\n" + praise);
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setSite(Constant.APPName);
			sp.setSiteUrl(stuff.getArticleShareUrl());
			Platform qZone = ShareSDK.getPlatform(QZone.NAME);
			qZone.setPlatformActionListener(pal);
			qZone.share(sp);
		} else if(item.get("ItemText").equals("微信收藏")){
			sp.setShareType(Platform.SHARE_WEBPAGE);
			sp.setTitle(stuff.getArticleShareShortText() + "\n" + stuff.getArticleShareShortTextCn());
			sp.setTitleUrl(stuff.getArticleShareUrl());
			sp.setText(stuff.getArticleShareShortTextCn() + "\n" + praise);
			sp.setImageUrl(stuff.getShareImageUrl());
			sp.setUrl(stuff.getArticleShareUrl());
			Platform wechatFavorite = ShareSDK.getPlatform(WechatFavorite.NAME);
			wechatFavorite.setPlatformActionListener(pal);
			wechatFavorite.share(sp);
		}
		
	}
			
			
			
	}
	

}