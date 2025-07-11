package com.iyuba.core.common.manager;

/**
 * 后台播放管理
 * 
 * @author chentong
 * @version 1.0
 */
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.service.bgPlayService.Background;
import com.iyuba.core.common.service.bgPlayService.Background.MyBinder;

public class BackgroundManager {
	private static BackgroundManager instance;
	public static Context mContext;
	public static Background bindService;//非静态变为静态
	public ServiceConnection conn;

	private BackgroundManager() {
		conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				//不要每次都初始化啊！！！
				if (bindService==null) {
					MyBinder binder = (MyBinder) service;
					bindService = binder.getService();
					bindService.init(mContext);
				}
			}
		};
	};

	public static synchronized BackgroundManager Instance() {
		mContext = RuntimeManager.getContext();
		if (instance == null||bindService==null) {
			instance = new BackgroundManager();
		}
		return instance;
	}
}
