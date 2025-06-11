/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.concept2.util;

import android.content.Context;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.iyuba.concept2.R;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ScreenShot;
import com.iyuba.core.common.util.TextAttr;

/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class Share {
	private Context mContext;

	public Share(Context context) {
		mContext = context;
	}

	private void shareMessage(String imagePath, String text, String url,
			String title) {
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.icon,
//				mContext.getString(R.string.app_name));
		// address是接收人地址，仅在信息和邮件使用
		oks.setAddress("");
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(text);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(imagePath);
		oks.setImageUrl(url);
		oks.setUrl(url);
		oks.setComment("爱语吧的这款应用" + Constant.APPName + "真的很不错啊~推荐！");
		oks.setSite(Constant.APPName);
		oks.setSiteUrl(url);

		oks.setSilent(false);
		oks.show(mContext);
	}

	public void prepareMessage(int episodeId, int actId, String title, String content) {
		ScreenShot.savePic(mContext, Constant.screenShotAddr);
		String imagePath = Constant.screenShotAddr;
		String text = mContext.getString(R.string.study_info) + "《" + title + "》"
				+ " 新浪微博：@iyuba 下载链接：" + "http://app."+Constant.IYBHttpHead()+"/androidApp.jsp?id=222&appId=36";
		String url = "http://app."+Constant.IYBHttpHead()+"/androidApp.jsp?id=222&appId=36";
		String chinese = content;
		int length = TextAttr.getLength(text);
		if (chinese.length() < 140 - length) {
			text = text + chinese;
		} else {
			int sublength = (int) (138.5 - length + TextAttr
					.getEnglishCount(chinese) / 4);
			text = text + chinese.substring(0, sublength) + "...";
		}
		shareMessage(imagePath, text, url, title);
	}
	
	public void prepareMessage(int id, String title, String content,String path) {
		String text = mContext.getString(R.string.study_info) + title + "》"
				+ " [  http://m."+Constant.IYBHttpHead()+"/MSEn/play.jsp?id=" + id + " ]";
		String url = " http://m."+Constant.IYBHttpHead()+"/MSEn/play.jsp?id=" + id;
		String chinese = content;
		int length = TextAttr.getLength(text);
		if (chinese.length() < 140 - length) {
			text = text + chinese;
		} else {
			int sublength = (int) (138.5 - length + TextAttr
					.getEnglishCount(chinese) / 4);
			text = text + chinese.substring(0, sublength) + "...";
		}
		shareMessage(path, text, url, title);
	}
}
