package com.iyuba.core.teacher.activity;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.iyuba.lib.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowLargePicActivity extends Activity {
	
//	private ImageView btnBack;
	private PhotoView discPic;

	 String  pic;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_show_disc_pic);
         pic=getIntent().getStringExtra("pic");
         
         Log.e("ShowLargePicActivity:",pic);
         
        initWidget();
    }
	
	public void initWidget() {
//		btnBack = (ImageView) findViewById(R.id.btn_back);
		discPic = (PhotoView) findViewById(R.id.disc_pic);
        discPic.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
//		GitHubImageLoader.Instance(this).setPic(pic,
//				discPic, R.drawable.nearby_no_icon,0);
//

		Glide.with(ShowLargePicActivity.this).load(pic).error(R.drawable.photo_pic).into(discPic);
		/*ImageLoader.getInstance().loadImage(pic,new SimpleImageLoadingListener(){
			
			 @Override  
	            public void onLoadingComplete(String imageUri, View view,  
	                    Bitmap loadedImage) {  
	                super.onLoadingComplete(imageUri, view, loadedImage);  
	                
	                discPic.setImageBitmap(loadedImage);  
	                LayoutParams para;
	                para = discPic.getLayoutParams();
	                int height = loadedImage.getHeight();
	                int width = loadedImage.getWidth();
//	                float f=mContext.getResources().getDisplayMetrics().density;
//	               float bit=width/300;
//	                height=(int)(height/bit);
//	               width=(int)(300/1.5*f);
//	               height=(int)(height/1.5*f);
	                para.height = height;
	                para.width = width;
	                discPic.setLayoutParams(para);
			 }
		});*/
//		btnBack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
		/*discPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});*/
	}
}
