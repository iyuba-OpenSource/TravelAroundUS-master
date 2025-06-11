package com.iyuba.concept2.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.ViewGroup;

public class IeltsBibleUtil {

	/**
	 * 去掉汉自的方法 如出入 "34题目 " 返回 34
	 * 
	 * @param str
	 * @return
	 */
	public static String filterUnNumber(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String formateTime(String dateline) {

		long transTime = Long.parseLong(dateline) * 1000;
		String timeShow = DateFormat.format("yyyy-MM-dd kk:mm:ss", transTime)
				.toString();
		String time = dateline;
		String pattern = "yyyy-MM-dd HH:mm:ss";

		String display = "";
		int tMin = 60 * 1000;
		int tHour = 60 * tMin;
		int tDay = 24 * tHour;

		if (time != null) {
			try {
				Date tDate = new SimpleDateFormat(pattern).parse(timeShow);
				Date today = new Date();
				SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
				SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
				Date thisYear = new Date(thisYearDf.parse(
						thisYearDf.format(today)).getTime());
				Date yesterday = new Date(todayDf.parse(todayDf.format(today))
						.getTime());
				Date beforeYes = new Date(yesterday.getTime() - tDay);
				if (tDate != null) {
					SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
					long dTime = today.getTime() - tDate.getTime();
					if (tDate.before(thisYear)) {
						display = new SimpleDateFormat("yyyy年MM月dd日")
								.format(tDate);
					} else {

						if (dTime < tMin) {
							display = "刚刚";
						} else if (dTime < tHour) {
							display = (int) Math.ceil(dTime / tMin) + "分钟前";
						} else if (dTime < tDay && tDate.after(yesterday)) {
							display = (int) Math.ceil(dTime / tHour) + "小时前";
						} else if (tDate.after(beforeYes)
								&& tDate.before(yesterday)) {
							// display = "昨天" + new
							// SimpleDateFormat("HH:mm").format(tDate);
							display = "昨天";
						} else {
							display = halfDf.format(tDate);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return display;
	}

	/** 获取状态栏的高度 */
	public static int getStatusBarHeight(Context context) {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	private static boolean flag=false;
	public static boolean canUseImmersion(Activity a) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2&&flag) {

			// 透明状态栏
			a.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			a.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			setStatusBarDarkMode(true, a);
			return true;
		}else {
			return false;
		}
	}
	
	public static void setStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 查找Button、ImageButton并设置单击监听器
	 * @param view
	 */
	public static void findButtonSetOnClickListener(View view, OnClickListener listener) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				View child = viewGroup.getChildAt(i);
				if (child instanceof Button || child instanceof ImageButton) {
					// 如果是按钮设置单击监听器
					child.setOnClickListener(listener);
				} else if (child instanceof ViewGroup) {
					findButtonSetOnClickListener(child, listener);
				}
			}
		}
	}
	
	
	/**
	 * 获取屏幕的宽
	 * @param context
	 * @return 
	 */
	public static int getScreenWidth(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getWidth();
	}
	
	/** 
	 * 格式化毫秒值，如果有小时，则格式化为时分秒，如：01:30:59，如果没有小时，则格式化为分和秒，如30:59
	 * @param duration
	 * @return
	 */
	public static String formatMills(long duration) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.add(Calendar.MILLISECOND, (int) duration);
		boolean hasHour = duration / (1000 * 60 * 60) > 0;	// 是否有小时数
		String inFormat = hasHour ? "kk:mm:ss" : "mm:ss";	// kk 代表1 ~ 24， HH 代表 0 ~ 23
		return DateFormat.format(inFormat, calendar).toString();
	}
	
	
}