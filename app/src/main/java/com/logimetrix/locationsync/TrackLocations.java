package com.logimetrix.locationsync;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class TrackLocations extends JobService {
    private String TAG = "TrackLocations";
    private static final int TIME_INTERVAL_IN_MINUTE = 10;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
   // private MySharedPreferences sharedPreference;
    private boolean jobCancelled = false;
    private String latitude, longitude;
    private String lat, longi, retailer;
    private GPSTracker gpsTracker;
    private int battery_value = 0;
    private Context contex;
    private boolean mRequestingLocationUpdates;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private Location location;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Service Started");

//loctionUpdate();
        Intent battery_intent = new Intent(getApplicationContext(), BatteryMonitorBroadcast.class);
        BatteryMonitorBroadcast batteryMonitorBroadcast = new BatteryMonitorBroadcast();
        registerReceiver(batteryMonitorBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sendBroadcast(battery_intent);
//        startLocation();
//        towerloc();

//        doBackgroundWork(params);

        return true;
    }


    private void doBackgroundWork(final JobParameters params) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                Looper.prepare();
                for (int r = 0; r < 15; r++) {
                    Log.d(TAG, "run: " + r);
                    if (jobCancelled) {
                        return;
                    }

                    gpsTracker = new GPSTracker(getApplicationContext());

                    if (gpsTracker.canGetLocation()) {

                        latitude = String.valueOf(gpsTracker.getLatitude());
                        longitude = String.valueOf(gpsTracker.getLongitude());
                    }

                    try {
                        Thread.sleep(100 * 60 * TIME_INTERVAL_IN_MINUTE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    startSync(params);

//                    if (!sharedPreference.isLoggedIn()) {
//                        Log.d(TAG, "Job Finished");
//                        jobFinished(params, false);
//                    }
                }
            }
        }).start();
    }

//    public void startLocation() {
//        // Requesting ACCESS_FINE_LOCATION using Dexter library
//        Dexter.withActivity((Activity) contex)
//                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        mRequestingLocationUpdates = true;
//                        startLocationUpdates();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        if (response.isPermanentlyDenied()) {
//                            // open device settings when the permission is
//                            // denied permanently
//                            openSettings();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Executor) this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        // toast(getApplicationContext(), "Started location updates!");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener((Executor) this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) contex, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                // toast(getApplicationContext(), errorMessage);
                        }

                    }
                });
    }


    public void stopLocation() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void towerloc() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        int cellid = cellLocation.getCid();
        int celllac = cellLocation.getLac();

        Log.d("CellLocation", cellLocation.toString());
        Log.d("GSM CELL ID", String.valueOf(cellid));
        Log.d("GSM Location Code", String.valueOf(celllac));
//        Toast.makeText(gpsTracker, "hii", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled before completion");
        jobCancelled = true;
        return true;
    }

    public void startSync(final JobParameters params) {
        try {
            gpsTracker = new GPSTracker(getApplicationContext());

            if (gpsTracker.canGetLocation()) {
                if (!((Double.parseDouble(latitude) == 0.0) && (Double.parseDouble(longitude) == 0.0))) {
                    hitApiLocation();
                } else {
                   // gpsTrackers.startlocation();
//                    startSync(params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void hitApiLocation() {
        Map<String, String> map = new HashMap<>();
//        map.put("LoginID", sharedPreference.getUserDetail().getLoginID());
//        map.put("appkey", sharedPreference.getUserDetail().getDevice_token());
//        map.put("latitude", String.valueOf(Commons.myCrntLatitude));
//        map.put("longitude", String.valueOf(Commons.myCrntLongitude));
//        map.put("Imei_no", URLConstants.getImeiNo(this));
//        map.put("password", sharedPreference.getUserDetail().getPassword());
//        map.put("battery_status", String.valueOf(battery_value));
//        Network.getInstance(this).startVolleyService(URLConstants.sales_tracking, map, trackUserResponse);
//
    }



    private class BatteryMonitorBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.e("", arg1.toString());
            battery_value = arg1.getIntExtra("level", 0);
           // sharedPreference.setbatterystatus(battery_value);
        }
    }
}
