package com.iyuba.concept2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.listener.AppUpdateCallBack;
import com.iyuba.concept2.listener.DownLoadFailCallBack;
import com.iyuba.concept2.manager.VersionManager;
import com.iyuba.concept2.util.FailOpera;
import com.iyuba.concept2.util.FileDownProcessBar;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

/**
 * 关于界面
 */

public class AboutActivity extends BasisActivity implements AppUpdateCallBack {
	private View background;
	private Button backBtn;
	private View url;
	private View appUpdateBtn;
	private View appNewImg;
	private TextView currVersionCode;
	private String version_code;
	private String appUpdateUrl;// 版本号
	private ProgressBar progressBar_downLoad; // 下载进度条

	//channel点击次数-测试
	private int channelClickCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		CrashApplication.getInstance().addActivity(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		background = findViewById(R.id.backlayout);
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		url = findViewById(R.id.imageView_url_btn);// 下载其他应用
		//这里华为审核不通过，直接屏蔽掉
		url.setVisibility(View.GONE);
		url.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AboutActivity.this, Web.class);
				intent.putExtra("url", "http://app."+Constant.IYBHttpHead()+"/android");
				intent.putExtra("title", "爱语吧精品应用");
				startActivity(intent);
			}
		});
		appUpdateBtn = findViewById(R.id.relativeLayout1);
		appUpdateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkAppUpdate();
			}
		});
		appNewImg = findViewById(R.id.imageView_new_app);
		currVersionCode = (TextView) findViewById(R.id.app_version);
		currVersionCode.setText(getResources()
				.getString(R.string.about_version) + VersionManager.VERSION_CODE);
		progressBar_downLoad = (ProgressBar) findViewById(R.id.progressBar_update);
		checkAppUpdate();


		//设置channel显示-仅用于测试
		ImageView channelView = findViewById(R.id.imageView1);
		TextView channelText = findViewById(R.id.channel);
		channelView.setOnClickListener(v->{
			channelClickCount++;

			if (channelClickCount>=6){
				channelText.setVisibility(View.VISIBLE);
				channelText.setText(ChannelReaderUtil.getChannel(AboutActivity.this));
			}
		});


		//显示备案号
		TextView filingNumberView = findViewById(R.id.filingNumber);
		filingNumberView.setText("京ICP备14035507号-8A");
	}

	/**
	 * 检查新版本
	 */
	public void checkAppUpdate() {
		VersionManager.Instace(this).checkNewVersion(VersionManager.version,
				this);
	}

	@Override
	public void appUpdateSave(String version_code, String newAppNetworkUrl) {
		this.version_code = version_code;
		this.appUpdateUrl = newAppNetworkUrl;
		handler.sendEmptyMessage(0);
	}

	@Override
	public void appUpdateFaild() {
		handler.sendEmptyMessage(1);
	}

	@Override
	public void appUpdateBegin(String newAppNetworkUrl) {
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (appNewImg != null) {
					appNewImg.setVisibility(View.VISIBLE);
					appUpdateBtn.setEnabled(false);
					progressBar_downLoad.setVisibility(View.VISIBLE);
				}
				showAlertAndCancel(
						getResources().getString(R.string.about_update_alert_1)
								+ version_code
								+ getResources().getString(
										R.string.about_update_alert_2),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//这里增加路径判断，主要是避免出现覆盖安装时之前的路径双斜杠问题
								if (ConfigManager.Instance().loadString(
										"media_saving_path").contains("//")){
									CustomToast.showToast(AboutActivity.this,"下载路径出错，请在‘设置’界面重新设置");
									return;
								}

								// 下载更新
								progressBar_downLoad.setMax(100); // 设置progressBar最大值
								FileDownProcessBar fdpb = new FileDownProcessBar(
										AboutActivity.this,
										progressBar_downLoad);
								
								fdpb.downLoadApkFile(appUpdateUrl,
										Constant.appfile + "_" + version_code,
										new DownLoadFailCallBack() {

											@Override
											public void downLoadSuccess(
													String localFilPath) {
												appUpdateBtn.setEnabled(true);
												progressBar_downLoad
														.setVisibility(View.INVISIBLE);
												FailOpera.Instace(
														AboutActivity.this)
														.openFile(localFilPath);
											}

											@Override
											public void downLoadFaild(
													String errorInfo) {
												CustomToast.showToast(
														getBaseContext(),
														R.string.about_error,
														1000);
											}
										});
							}
						});
				break;
			case 1:
				if (appNewImg != null) {
					appNewImg.setVisibility(View.INVISIBLE);
					progressBar_downLoad.setVisibility(View.INVISIBLE);
				}
				
				CustomToast.showToast(getBaseContext(),
						R.string.about_update_isnew, 1000);
				break;
			case 2:
				break;
			}
		}
	};

	private void showAlertAndCancel(String msg,
			DialogInterface.OnClickListener ocl) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(R.string.alert_title);
		alert.setMessage(msg);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setButton(AlertDialog.BUTTON_POSITIVE,
				getResources().getString(R.string.alert_btn_ok), ocl);
		alert.setButton(AlertDialog.BUTTON_NEGATIVE,
				getResources().getString(R.string.alert_btn_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						appUpdateBtn.setEnabled(true);
						progressBar_downLoad.setVisibility(View.INVISIBLE);
					}
				});
		alert.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
