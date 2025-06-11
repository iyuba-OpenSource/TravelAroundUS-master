/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.thread;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.lib.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 下载图片
 *
 * @author 作者 陈彤
 * @time 2014.4.30
 */
public class GitHubImageLoader {
	public ImageLoader imageLoader;
	private static GitHubImageLoader instance;
	private DisplayImageOptions options;

	private GitHubImageLoader(Context mContext) {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
	}

	/**
	 * 单例初始化
	 */
	public static synchronized GitHubImageLoader Instace(Context mContext) {
		if (instance == null) {
			instance = new GitHubImageLoader(mContext);
		}
		return instance;
	}

	/**
	 * 下载用户头像（中等大小）并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param pic    头像对应的ImageView
	 */
	public void setPic(String userid, ImageView pic) {
		setPic(userid, "middle", pic);
	}

	/**
	 * 下载用户头像（中等大小）转换成圆形并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param pic    头像对应的ImageView
	 */
	public void setCirclePic(String userid, ImageView pic) {
		setCirclePic(userid, "middle", pic);
	}


	public void setCirclePicGrild(String userid, ImageView pic,Context context,int drawable) {
		String url="http://api."+com.iyuba.configation.Constant.IYBHttpHead2()+"/v2/api.iyuba?protocol=10005&uid=" + userid
				+ "&size=middle";
		setCircleImage(url,context,drawable,pic);
	}

	/**
	 * 下载用户头像并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param size   头像大小（small, middle, big）
	 * @param pic    头像对应的ImageView
	 */
	public void setPic(String userid, String size, ImageView pic) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultavatar)
				.showImageForEmptyUri(R.drawable.defaultavatar)
				.showImageOnFail(R.drawable.defaultavatar).cacheInMemory(true)
				.delayBeforeLoading(1000).cacheOnDisk(true).build();
		imageLoader.displayImage(
				"http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" + userid
						+ "&size=" + size, pic, options);

	}

	/**
	 * 下载用户头像转换成圆形并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param size   头像大小（small, middle, big）
	 * @param pic    头像对应的ImageView
	 */
	public void setCirclePic(String userid, String size, ImageView pic) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultavatar)
				.showImageForEmptyUri(R.drawable.defaultavatar)
				.showImageOnFail(R.drawable.defaultavatar).cacheInMemory(true)
				.delayBeforeLoading(1000).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(90)).build();
		imageLoader.displayImage(
				"http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" + userid
						+ "&size=" + size, pic, options);

	}

	/**
	 * 从url下载图片在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setRawPic(String url, ImageView pic, int drawable) {
		options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).delayBeforeLoading(1000)
				.showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.build();
		imageLoader.displayImage(url, pic, options);
	}

	/**
	 * 从url下载图片（默认15度圆角）并在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setPic(String url, ImageView pic, int drawable) {
		setPic(url, pic, drawable, 15);
	}

	/**
	 * 从url下载图片并在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 * @param degree   图片边缘圆角角度
	 */
	public void setPic(String url, ImageView pic, int drawable, int degree) {
		options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).delayBeforeLoading(1000)
				.showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(degree)).build();
		imageLoader.displayImage(url, pic, options);
	}

	/**
	 * 从url下载图片转换成圆形并在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setCirclePic(String url, ImageView pic, int drawable) {
		setPic(url, pic, drawable, 90);
	}

	/**
	 * 加载圆形图片
	 * @param imageUrl
	 * @param context
	 * @param placeImage
	 * @param imageView
	 */
	public static void setCircleImage(String imageUrl, final Context context, int placeImage, final ImageView imageView) {
		/*Glide.with(context).load(imageUrl).asBitmap().centerCrop().placeholder(placeImage).error(placeImage).into(new BitmapImageViewTarget(imageView) {
			@Override
			protected void setResource(Bitmap resource) {
				RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
				circularBitmapDrawable.setCircular(true);
				imageView.setImageDrawable(circularBitmapDrawable);
			}
		});*/
		LibGlide3Util.loadCircleImg(context,imageUrl,placeImage,imageView);
	}

	public void clearCache() {
		// TODO Auto-generated method stub
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
	}

	public void clearMemoryCache() {
		// TODO Auto-generated method stub
		imageLoader.clearMemoryCache();
	}

	public void exit() {
		imageLoader.destroy();
	}
}
