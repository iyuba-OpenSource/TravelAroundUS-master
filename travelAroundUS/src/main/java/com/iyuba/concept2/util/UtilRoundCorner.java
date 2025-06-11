package com.iyuba.concept2.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

public class UtilRoundCorner {
	
	/**
	 * 把图片画成圆角
	 * @param source
	 * 原始图片
	 * @param radius
	 * 浮点型，值越大圆角越大
	 * @return
	 * 画完的图片
	 */
	public static Bitmap roundCorners(final Bitmap source, final float radius) {
		int width = source.getWidth();
		int height = source.getHeight();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(android.graphics.Color.WHITE);
		paint.setShadowLayer(5f, 5.0f, 5.0f, Color.BLACK); //设置阴影层，这是关键，不注释的话可以直接出圆角加阴影的效果   
		Bitmap clipped = Bitmap.createBitmap(width+10, height+10, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(clipped);
		canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius,
		paint);
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		//source.recycle();
		return clipped;
	}
	
	/**
	 * @param source
	 * 图片
	 * @param radius
	 * 圆角
	 * @param color
	 * 阴影颜色
	 * @param dxy
	 * 阴影大小
	 * @return
	 */
	public static Bitmap roundCornersForComment(final Bitmap source, final float radius,
			int color,float dxy) {
		int width = source.getWidth();
		int height = source.getHeight();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(android.graphics.Color.WHITE);
		paint.setShadowLayer(5f, dxy, dxy,color); //设置阴影层，这是关键，不注释的话可以直接出圆角加阴影的效果   
		Bitmap clipped = Bitmap.createBitmap(width+6, height+6, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(clipped);
		canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius,
		paint);
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		//source.recycle();
		return clipped;
	}
	
	/**
	 * 给图片加阴影
	 * @param source
	 * 要画的图片
	 * @return
	 */
	public static Bitmap drawShadow(final Bitmap source) {
			int width = source.getWidth();
			int height = source.getHeight();
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(android.graphics.Color.WHITE);
	        paint.setShadowLayer(5f, 5.0f, 5.0f, Color.BLACK); //设置阴影层，这是关键。   
	        //width和height加的偏移量可以作为参数传入，这里就不写了
			Bitmap clipped = Bitmap.createBitmap(width+10, height+10, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(clipped);
			  //图片阴影效果   
			canvas.drawRoundRect(new RectF(0, 0, width, height), 10f, 10f,
					paint);
			paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
	        canvas.drawBitmap(source, 0,  0, paint);//画上原图。   
			source.recycle();
			return clipped;
			
		}
	/*public static void drawText(final Bitmap source) {
		int width = source.getWidth();
		int height = source.getHeight();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(android.graphics.Color.WHITE);
        paint.setShadowLayer(5f, 5.0f, 5.0f, Color.BLACK); //设置阴影层，这是关键。   
        //width和height加的偏移量可以作为参数传入，这里就不写了
		Bitmap clipped = Bitmap.createBitmap(width+10, height+10, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(clipped);
		  //图片阴影效果   
		canvas.drawRoundRect(new RectF(0, 0, width, height), 10f, 10f,
				paint);
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0,  0, paint);//画上原图。   
		source.recycle();
		return clipped;
	}
	*/
	

}
