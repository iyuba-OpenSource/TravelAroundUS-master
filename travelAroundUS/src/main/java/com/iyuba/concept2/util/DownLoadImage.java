package com.iyuba.concept2.util;

import java.io.InputStream;

import com.iyuba.concept2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownLoadImage extends AsyncTask<String, Void, Bitmap> {
	ImageView imageView;
	Context mContext;

	public DownLoadImage(Context mContext, ImageView imageView) {
		this.mContext = mContext;
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		String url = urls[0];
		Bitmap tmpBitmap = null;
		if (NetWorkState.getAPNType() != 2) {
			tmpBitmap = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.userimg);
		} else {
			try {
				InputStream is = new java.net.URL(url).openStream();
				if (is == null)
					tmpBitmap = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.userimg);
				else
					tmpBitmap = BitmapFactory.decodeStream(is);
			} catch (Exception e) {
				tmpBitmap = BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.userimg);
				e.printStackTrace();
			}
		}
		return tmpBitmap;
	}
	
	public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {//h
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

	@Override
	protected void onPostExecute(Bitmap result) {
		imageView.setImageBitmap(getRoundedCornerBitmap(result, 5));
	}
}
