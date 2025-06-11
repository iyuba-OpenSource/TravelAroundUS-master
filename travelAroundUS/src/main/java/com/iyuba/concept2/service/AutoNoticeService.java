package com.iyuba.concept2.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.iyuba.concept2.R;
import com.iyuba.concept2.activity.WelcomeActivity;
import com.iyuba.configation.Constant;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AutoNoticeService extends Service {


	public static final int NOTIFICATIN_ID = Integer.valueOf(Constant.APPID);
	private static int voaid;
	private Notification mNotification;
	private String title;
	private int newsCount;//新的文章数
	private String lastestTitle;
	private SharedPreferences preferences;
	HttpResponse mHttpResponse=null;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		preferences = getApplicationContext().getSharedPreferences("config", Activity.MODE_PRIVATE);
		voaid=preferences.getInt("lastvoaid", -1);
		new Thread(new Runnable() {
			@Override
			public void run() {
				getNew();
				if (newsCount==0) {
					stopSelf();
				}
			}
		}).start();
				
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void showNotification() {
		createNotification();
	}

	public void removeNotification() {
		stopForeground(true);
		stopSelf();
	}
	private void getNew(){
		HttpGet mHttpGet=new HttpGet("http://apps."+Constant.IYBHttpHead()+"/voa/titleApi.jsp?maxid="+voaid+"&type=android&format=json");
		try {
			mHttpResponse=new DefaultHttpClient().execute(mHttpGet);
			 if (mHttpResponse.getStatusLine().
					 getStatusCode() == 200){
						 String result = EntityUtils.
								 toString(mHttpResponse.getEntity());  
						 JSONObject jsonObjectRoot = new JSONObject(result);
							newsCount = Integer.parseInt(jsonObjectRoot.getString("total"));
							lastestTitle=jsonObjectRoot.getJSONArray("data").getJSONObject(0).getString("Title_cn");
							if (newsCount==0) {
								stopSelf();
								return;
							}
							else {
								showNotification();
							}
							
					 }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			stopSelf();
		}
	}

	public void createNotification() {
		if (mNotification == null) {
			mNotification = new Notification();
		}
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification.icon = R.drawable.familyalbum_icon;
		mNotification.tickerText = title;
		mNotification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(this, WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		mNotification.contentIntent = pendingIntent;
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notify_auto);
		contentView.setImageViewResource(R.id.icon, R.drawable.familyalbum_icon);
		contentView.setTextViewText(R.id.text1,getString(R.string.setting_auto_notice_info1) + "  "+getString(R.string.setting_auto_notice_info2)+"\n");
		contentView.setTextViewText(R.id.text2, getString(R.string.setting_auto_notice_info3) + "  "+ lastestTitle+getString(R.string.setting_auto_notice_info5)+"\n");
		contentView.setTextViewText(R.id.text3, getString(R.string.setting_auto_notice_info6) + "  "+newsCount+getString(R.string.setting_auto_notice_info4));
		mNotification.contentView = contentView;
		mNotificationManager.notify(NOTIFICATIN_ID, mNotification);
	}

}
