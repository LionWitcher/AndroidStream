package com.verizon.stream.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Process;
import android.os.SystemClock;

import java.io.DataOutputStream;
import java.io.IOException;

public class GeoData {
    private static final String LOG_TAG = "GeoData";
    private int mLatitude;
    private int mLongitude;

    public GeoData(int latitude, int longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public static void inject(Context context, int latitude, int longitude) {
        GeoData geoData = new GeoData(latitude, longitude);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        geoData.setMockProvider(locationManager, appOpsManager, context.getPackageName());
        geoData.setMockLocation(locationManager);
    }

    private void setMockProvider(LocationManager locationManager, AppOpsManager appOpsManager, String packageName) {
        int grantedPermission = appOpsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, Process.myUid(), packageName);
        if (grantedPermission != AppOpsManager.MODE_ALLOWED) {
            if (!setPermission(true, packageName)) {
                return;
            }
        }
        try {
            locationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    Criteria.POWER_LOW,
                    Criteria.ACCURACY_FINE);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE,
                    null, System.currentTimeMillis());
        } catch (SecurityException se) {
            LOG.e(LOG_TAG, "Add test provider error: " + se.getMessage(), se);
        }
    }

    private void removeMockProvider(LocationManager locationManager) {
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
    }

    private void setMockLocation(LocationManager locationManager) {
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(mLatitude);
        mockLocation.setLongitude(mLongitude);
        mockLocation.setAltitude(0);
        mockLocation.setAccuracy(Criteria.ACCURACY_FINE);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
    }

    private boolean setPermission(boolean allow, String packageName) {
        try {
            java.lang.Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            StringBuilder command = new StringBuilder()
                    .append("appops set")
                    .append(" ")
                    .append(packageName)
                    .append(" ")
                    .append(AppOpsManager.OPSTR_MOCK_LOCATION)
                    .append(" ")
                    .append(allow ? "allow" : "deny");
            outputStream.writeBytes(command.toString());
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            LOG.e(LOG_TAG, "Set permission error: " + e.getMessage());
            return false;
        }
    }
}
