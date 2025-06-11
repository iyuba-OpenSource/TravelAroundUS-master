package com.iyuba.core.teacher.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.buaa.ct.imageselector.view.ImageCropActivity;
import com.buaa.ct.imageselector.view.ImageSelectorActivity;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.listener.OperateCallBack;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.thread.UploadFile;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.SaveImage;
import com.iyuba.core.common.util.TakePictureUtil;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.teacher.protocol.GetTeacherInfoRequest;
import com.iyuba.core.teacher.protocol.GetTeacherInfoResponse;
import com.iyuba.core.teacher.protocol.UpdateBasicRequest;
import com.iyuba.core.teacher.protocol.UpdateBasicResponse;
import com.iyuba.core.teacher.sqlite.mode.Teacher;
import com.iyuba.lib.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.PermissionUtils;

public class TeacherBaseInfo extends Activity {
	private TextView quesCancel;
	private ContextMenu contextMenu;
	private Context mContext;
	
    public  String size;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	private String tempFilePath = Environment.getExternalStorageDirectory()
			+ "/teacher.jpg";
	private File tempFile = new File(tempFilePath);

	private CustomDialog cd;

	private ImageView teaacherhead;
	private String  attachment="";
   private TextView next2,editteachername,editteachermobile,editteacheremail,editteacherweixin,editteacherdesc;
   RadioButton  RadioButton1,RadioButton2;
   private Teacher teacher=new Teacher();
   Spinner  spteachercert;
   
   private ArrayAdapter<String> adapter;

   private List<String> list;

   private String[] items = {"专科","本科","硕士","博士","其他"};
   
   public void intiSpiner(Spinner spinner ){
	    
	   list = new ArrayList<String>();

	   for(int i = 0; i < items.length; i++)

	   {

	   list.add(items[i]);

	   }

	   adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);

	   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	   spinner.setAdapter(adapter);
	   
   }
   
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lib_teacherbaseinfo);
		mContext = this;
		cd=  WaittingDialog.showDialog(mContext);
	
		initWidget();
		handler.sendEmptyMessage(1);
	}

	public void initData() {
		 cd.dismiss();
		 if(!teacher.timg.equals(""))	handler.sendEmptyMessage(12);
		 editteachername.setText(teacher.tname);
		 editteachermobile.setText(teacher.tphone);
		 editteacherdesc.setText(teacher.tonedesc);
		 editteacherweixin.setText(teacher.tweixin);
		 editteacheremail.setText(teacher.temail);
		 if(teacher.tsex.trim().equals("女"))RadioButton2.setChecked(true);
		
		 if(!teacher.topedu.equals("")){
			 int theNum=0;
			 for(int i=0;i<items.length;i++){
				 if(items[i].equals(teacher.topedu)){
					theNum=i; 
				 break;
				 }
				 
			 }
			 
			 
			 spteachercert.setSelection(theNum,true);
			 
			 
		 }
		 
		 
	}

	public void initWidget() {
		
		
		
		spteachercert=(Spinner) findViewById(R.id.spteachercert);
		intiSpiner(spteachercert);
		
		
		
		contextMenu = (ContextMenu) findViewById(R.id.context_menu1);
		 teaacherhead=(ImageView) findViewById(R.id.teaacherhead);
		 teaacherhead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				setContextMenu();
			}
		});
		 
		 
		 
		editteacheremail=(TextView) findViewById(R.id.editteacheremail);
		 editteacherweixin=(TextView) findViewById(R.id.editteacherweixin);
		 editteacherdesc=(TextView) findViewById(R.id.editteacherdesc);
	
		 editteachermobile=(TextView) findViewById(R.id.editteachermobile);
		RadioButton1=(RadioButton) findViewById(R.id.RadioButton1);
		RadioButton2=(RadioButton) findViewById(R.id.RadioButton2);
		
		
		editteachername=(TextView) findViewById(R.id.editteachername);
		quesCancel = (TextView) findViewById(R.id.tbutton_back);
		
		next2 = (TextView) findViewById(R.id.next2);
		
		next2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//获取老师的基本信息
				teacher.uid=String.valueOf(UserInfoManager.getInstance().getUserId());
				teacher.username=UserInfoManager.getInstance().getUserName();
				teacher.tname=editteachername.getText().toString().trim();
				teacher.tphone=editteachermobile.getText().toString().trim();
				teacher.temail=editteacheremail.getText().toString().trim();
				teacher.tweixin=editteacherweixin.getText().toString().trim();
				teacher.tonedesc= editteacherdesc.getText().toString().trim();
				if(!attachment.equals(""))teacher.timg=attachment;
				if(RadioButton2.isChecked())
				{
					teacher.tsex="女";
				}else{
				    teacher.tsex="男";
				}
			      teacher.topedu=spteachercert.getSelectedItem().toString();
			      //判断是否上传老师头像
			      if(teacher.timg.equals("")) {
						handler.sendEmptyMessage(16);
						return;
					}
			      //判断是否有必填项没有填
					if(editteachername.getText().toString().trim().equals("")||editteachermobile.getText().toString().trim().equals("")
							||editteacheremail.getText().toString().trim().equals("")||editteacherweixin.getText().toString().trim().equals("")
							||editteacherdesc.getText().toString().trim().equals("")
							){
						handler.sendEmptyMessage(17);
						return;
					}
			      
			      
				updateBasic();
				Intent intent = new Intent(mContext, TeacherBaseInfo2.class);
				startActivity(intent);
				finish();
			}
		});
        
		quesCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	
	
		
		
		
		
		
		
	}
//设置菜单
	public void setContextMenu() {
		contextMenu.setText(mContext.getResources().getStringArray(
				R.array.choose_pic));
		contextMenu.setCallback(new ResultIntCallBack() {

			@Override
			public void setResult(int result) {
				Intent intent;
				switch (result) {
				case 0:
					if (Build.VERSION.SDK_INT >= 23) {


						if (PermissionUtils.hasSelfPermissions(TeacherBaseInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})) {

							TakePictureUtil.photoPath = ImageSelectorActivity.startCameraDirect(TeacherBaseInfo.this);

						} else {

							CustomToast.showToast(TeacherBaseInfo.this, "拍照或存储权限未开启，开启后可正常使用");
						}

					} else {

						TakePictureUtil.photoPath = ImageSelectorActivity.startCameraDirect(TeacherBaseInfo.this);


					}
					break;
				case 1:
					if (Build.VERSION.SDK_INT >= 23) {


						if (PermissionUtils.hasSelfPermissions(TeacherBaseInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

							ImageSelectorActivity.start(TeacherBaseInfo.this, 1, ImageSelectorActivity.MODE_SINGLE, false, true, true);


						} else {

							CustomToast.showToast(TeacherBaseInfo.this, "存储权限未开启，开启后可正常使用");
						}

					} else {

						ImageSelectorActivity.start(TeacherBaseInfo.this, 1, ImageSelectorActivity.MODE_SINGLE, false, true, true);


					}
					break;
				default:
					break;
				}
				contextMenu.dismiss();
			}
		});
		contextMenu.show();
	}
	
	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case ImageSelectorActivity.REQUEST_CAMERA:
					ImageCropActivity.startCrop(this, TakePictureUtil.photoPath);
					break;
				case ImageSelectorActivity.REQUEST_IMAGE:
					if (resultCode == RESULT_OK) {
						ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
						TakePictureUtil.photoPath = images.get(0);
						teaacherhead.setImageBitmap(getImage());

						handler.sendEmptyMessageDelayed(19, 500);

					}
					break;
				case ImageCropActivity.REQUEST_CROP:
					TakePictureUtil.photoPath = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);

					teaacherhead.setImageBitmap(getImage());
					new UploadThread().start();
					break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			if (requestCode == ImageSelectorActivity.REQUEST_CAMERA) {

			}
		}

	}
	private Bitmap getImage() {
		try {
			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inPreferredConfig = Bitmap.Config.RGB_565;
			return BitmapFactory.decodeFile(TakePictureUtil.photoPath, op);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	
	
	
	
	
	
	private void startPhotoZoom1(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
		
		Bitmap photo = intent.getExtras().getParcelable("data");
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(intent.getData(), proj, null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		tempFilePath = cursor.getString(column_index);
		Log.e("startPhotoZoom",tempFilePath);

	}
	
	
	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			SaveImage.saveImage(tempFilePath, photo);
			teaacherhead.setImageDrawable(drawable);
			//teaacherhead.setBackgroundDrawable(drawable);
			new  UploadThread().start();
		}
	}

	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				cd.dismiss();
				break;
			case 1:
				cd.show();
				getTeacherData();
				break;
			case 2:
				CustomToast.showToast(mContext, R.string.ask_question_fail);
				break;
			case 3:
				CustomToast.showToast(mContext, R.string.ask_question_success);
				finish();
				break;
			case 4:
				CustomToast.showToast(mContext, R.string.question_tip);
				break;
				
			case 6:
				CustomToast.showToast(mContext, "请选择问题类型");
				break;
				
			case 10:
				CustomToast.showToast(mContext, size);
				break;
			case 12:
				GitHubImageLoader.Instace(mContext).setPic("http://www."+Constant.IYBHttpHead()+"/question/"+teacher.timg,
						 teaacherhead, R.drawable.photo_pic,0);
				break;
			case 13:
				CustomToast.showToast(mContext, attachment);
				break;
			case 16:
				CustomToast.showToast(mContext, "请上传您的头像!");
				break;
			case 17:
				CustomToast.showToast(mContext, "请填写必填信息！");
				break;
			case 18:
               initData();
				break;
			}
			
		}
	};

	private Bitmap getImageZoomed(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 40, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 40;
		while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

 //上传老师的头像
	class UploadThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				String uri="http://www."+Constant.IYBHttpHead()+"/question/teacher/api/upLoad.jsp?format=json";
      Log.e("iyuba", uri);





				Bitmap bt = getImageZoomed(TakePictureUtil.photoPath);

//				Bitmap bt=BitmapFactory.decodeFile(TakePictureUtil.photoPath);
				File f = new File(TakePictureUtil.photoPath);

				FileInputStream fis = new FileInputStream(f);
				FileChannel fc = fis.getChannel();

//				Log.e("iyuba",fc.size()/1024+"------------"+100);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				int percent = 20;

				bt.compress(Bitmap.CompressFormat.JPEG, percent, stream);

				size = stream.size() / 1024 + "+++1++" + fc.size() / 1024;
				Log.e("iyuba", stream.size() / 1024 + "------------" + percent);

				//讲压缩后的文件保存在temp2下
//				String temp2=Constant.envir+"/temp2.jpg";
				String temp2 = TakePictureUtil.photoPath;
				FileOutputStream os = new FileOutputStream(temp2);
				os.write(stream.toByteArray());
				os.close();

				UploadFile.postHead(TakePictureUtil.photoPath, uri, new OperateCallBack() {

					@Override
					public void success(String message) {
					    attachment=message;
					}

					@Override
					public void fail(String message) {
						attachment=message;
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
//获取老师的基本信息
	public void getTeacherData() {
		
		ExeProtocol.exe(new GetTeacherInfoRequest(String.valueOf(UserInfoManager.getInstance().getUserId())), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				GetTeacherInfoResponse tr = (GetTeacherInfoResponse) bhr;
				teacher=tr.item;
				handler.sendEmptyMessage(18);
				//initData();
			}
			@Override
			public void error() {
				
			}
		});
	}
	
//提交或更新老师基本信息	
public void updateBasic() {
		ExeProtocol.exe(new UpdateBasicRequest(teacher), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				UpdateBasicResponse tr = (UpdateBasicResponse) bhr;
				if(tr.result.equals("1")) 	;
			}
			@Override
			public void error() {
				
			}
		});
	}
	
	
}
