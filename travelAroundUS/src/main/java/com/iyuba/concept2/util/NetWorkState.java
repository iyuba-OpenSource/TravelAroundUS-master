package com.iyuba.concept2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iyuba.configation.RuntimeManager;

public class NetWorkState {

	public static boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) RuntimeManager
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	public static int getAPNType() {
		int netType = 0;
		ConnectivityManager connMgr = (ConnectivityManager) RuntimeManager
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			netType = 2;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 2;
		}
		return netType;
	}
}
