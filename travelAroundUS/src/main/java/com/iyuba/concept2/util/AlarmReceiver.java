package com.iyuba.concept2.util;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyuba.concept2.service.AutoNoticeService;

/**
 * 在开机时接收广播并注册闹钟来定时提供新文章提醒服务。
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	public AlarmReceiver() {
		
	}

	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent intent1 = new Intent(context, AutoNoticeService.class);
		PendingIntent pendIntent = PendingIntent.getService(context,  
		        0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager noticeAlarmManager=(AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
		 Calendar calendar=Calendar.getInstance();
	    calendar.setTimeInMillis(System.currentTimeMillis());
	    noticeAlarmManager.setInexactRepeating(
				AlarmManager.RTC, //闹钟类型，此处为“在指定时间启动的闹钟”，区别于“在指定的延时过后启动的闹钟”
				calendar.getTimeInMillis(), //第一次的报警时间，可能不调用，依据报警类型而定
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, //距离firstime的时间间隔，也就是频率 每这个时间段触发一次
				pendIntent);//闹钟到点时所作的事
	}

}
