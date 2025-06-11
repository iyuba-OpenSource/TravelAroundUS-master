package com.iyuba.concept2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class CheckNetWork
{
  /**
   * 网络是否可用
   * @param mActivity
   * @return
   * 
   */
  public static boolean isNetworkAvailable(Context context) { 
    ConnectivityManager connectivity = (ConnectivityManager) context 
            .getSystemService(Context.CONNECTIVITY_SERVICE); 
    if (connectivity == null) { 
        return false; 
    } else { 
        NetworkInfo info = connectivity.getActiveNetworkInfo(); //getAllNetworkInfo()
        
        if (info != null && info.isConnected()) {
          return true;
        }
        return false; 
    } 
    
} 
 
  /**
   * 
   * @return
   * 功能：判断是否为wifi网络
   */
  public static boolean isWifi(Context context)
  {
//    Context context = mActivity.getApplicationContext(); 
    ConnectivityManager connectivity = (ConnectivityManager) context 
            .getSystemService(Context.CONNECTIVITY_SERVICE); 
    State wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    if(wifi==State.CONNECTED||wifi==State.CONNECTING)
      return true;
    else {
      return false;
    }
  }

}
