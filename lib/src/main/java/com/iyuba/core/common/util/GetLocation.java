package com.iyuba.core.common.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;

import timber.log.Timber;

public class GetLocation {

    /**
     * @param context the context parameter
     * @return a Pair value, first is latitude, second is longitude
     */
    @SuppressWarnings("MissingPermission")
    public static Pair<Double, Double> getLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Timber.d("GPS_PROVIDER is able to use");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, sStubListener);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                if (latitude != 0.0 && longitude != 0.0) {
                    Timber.i("Get location by GPS! latitude %s longitude %s", latitude, longitude);
                    lm.removeUpdates(sStubListener);
                    return new Pair<>(latitude, longitude);
                }
            }
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Timber.d("NETWORK_PROVIDER is able to use");
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, sStubListener);
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                if (latitude != 0.0 && longitude != 0.0) {
                    Timber.i("Get location by NETWORK! latitude %s longitude %s", latitude, longitude);
                    lm.removeUpdates(sStubListener);
                    return new Pair<>(latitude, longitude);
                }
            }
        }
        return new Pair<>(0.0, 0.0);
    }

    // 监听状态
    private static LocationListener sStubListener = new LocationListener() {
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

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {

        }
    };

}
