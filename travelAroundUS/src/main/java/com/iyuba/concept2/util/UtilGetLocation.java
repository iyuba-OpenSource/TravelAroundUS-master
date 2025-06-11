package com.iyuba.concept2.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class UtilGetLocation {
	private  double latitude=0.0;  
	private  double longitude =0.0;  
	private static Context mContext;
	private static UtilGetLocation instanceGetLocation;
	public static UtilGetLocation getInstance(Context context) {
		mContext=context;
		if (instanceGetLocation==null) {
			instanceGetLocation = new UtilGetLocation();
		}
		return instanceGetLocation;
	}

	/**
	 * @return
	 * string[0]是latitude，string[1]是longitude
	 */
	public  String[] getLocation() {
		LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);  
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
           
            if(location != null){  
                latitude = location.getLatitude();  
                longitude = location.getLongitude();  
                Log.d("mark", "222222222222");
                }
            else {
            	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);     
                Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);     
               /* while(location1  == null)  
                {
                	locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);  
                }*/
                if(location1 != null){     
                    latitude = location1.getLatitude(); //经度     
                    longitude = location1.getLongitude(); //纬度  
                    Log.d("mark", "6666666666");
                }     
                Log.d("mark", "3333333333333333");
			}
            Log.d("mark", "1111111111111");
        }else{
        	
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);     
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);     
           /* while(location  == null)  
            {
            	locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);  
            }  */
            if(location != null){     
                latitude = location.getLatitude(); //经度     
                longitude = location.getLongitude(); //纬度  
                Log.d("mark", "555555555555");
            }     
            Log.d("mark", "4444444444444");
        }
        Log.d("latitude", latitude+"");
        Log.d("longitude", longitude+"");
        String[] strings={latitude+"",longitude+""};
        if (latitude!=0||longitude!=0) {
        	 locationManager.removeUpdates(locationListener);
		}
       // locationManager.removeUpdates(locationListener);
        return strings;
		
	}
	
	//监听状态
	LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数  
        @Override  
        public void onStatusChanged(String provider, int status, Bundle extras) {  
              
        }  
        // Provider被enable时触发此函数，比如GPS被打开  
        @Override  
        public void onProviderEnabled(String provider) {  
              
        }  
          
        // Provider被disable时触发此函数，比如GPS被关闭   
        @Override  
        public void onProviderDisabled(String provider) {  
              
        }  
          
        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发   
        @Override  
        public void onLocationChanged(Location location) {  
            if (location != null) {     
                Log.e("Map", "Location changed : Lat: "    
                + location.getLatitude() + " Lng: "    
                + location.getLongitude());     
            }  
        }  
    };  
	
}
