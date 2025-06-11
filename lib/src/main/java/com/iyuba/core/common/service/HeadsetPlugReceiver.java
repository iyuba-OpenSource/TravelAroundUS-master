package com.iyuba.core.common.service;

/**
 * 耳机监听广播
 * 
 * @author 陈彤
 * @version 1.0
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.play.ExtendedPlayer;

public class HeadsetPlugReceiver extends BroadcastReceiver {
//	private BackPlayer vv = null;
	private ExtendedPlayer vv = null;
	private boolean first = true;

	public HeadsetPlugReceiver() {
		vv = BackgroundManager.Instance().bindService
				.getPlayer();
	}

	public HeadsetPlugReceiver(ExtendedPlayer player) {
		vv = player;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (intent.hasExtra("state")) {
			if (intent.getIntExtra("state", 0) == 1) {
				boolean isAutoPlay = ConfigManager.Instance().loadBoolean(
						"autoplay");
				if (isAutoPlay && !vv.isPlaying()) {
					vv.start();
				}
				first = false;
			} else if (intent.getIntExtra("state", 0) == 0) {
				if (!first) {
					boolean isAutoStop = ConfigManager.Instance().loadBoolean(
							"autostop");
					if (isAutoStop && vv.isPlaying()) {
						vv.pause();
					}
				}
			}
		}
	};

}
