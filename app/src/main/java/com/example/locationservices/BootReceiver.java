package com.example.locationservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String PROVIDERS_CHANGED_STRING = "android.location.PROVIDERS_CHANGED";
    private static final String MODE_CHANGED_STRING = "android.location.MODE_CHANGED";
    private static final String BOOT_COMPLETED_STRING = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "BootReceiver";
    protected Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "GPS Enabled: "+isGPSEnabled);
//                Log.d(TAG, "Network Enabled: "+isNetworkEnabled);
        if (action.equals(PROVIDERS_CHANGED_STRING)){
            Log.d(TAG, "Provider status changed");
            if (isLocationModeAvailable(context)){
                if (getLocationMode(context) == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY ||
                        getLocationMode(context) == Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
                    manageLocationService(isGPSEnabled);
                }
            } else {
                manageLocationService(isGPSEnabled);
            }

        } else if (action.equals(MODE_CHANGED_STRING)){
            Log.d(TAG, "Location mode changed");
            if (!isGPSEnabled){
                if (getLocationMode(context) != Settings.Secure.LOCATION_MODE_BATTERY_SAVING)
                    manageLocationService(isGPSEnabled);
            } else {
                manageLocationService(isGPSEnabled);
            }

        } else if (action.equals(BOOT_COMPLETED_STRING)){
            manageLocationService(isGPSEnabled);
        }
    }

    private void manageLocationService(boolean isGPSEnabled){
        if (isGPSEnabled) {
            Log.d(TAG, "Starting Location service");
        } else {
            Log.d(TAG, "GPS is off");
        }
    }

    private boolean isLocationModeAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= 19 && getLocationMode(context) != Settings.Secure.LOCATION_MODE_OFF) {
            return true;
        } else return false;
    }

    public int getLocationMode(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
