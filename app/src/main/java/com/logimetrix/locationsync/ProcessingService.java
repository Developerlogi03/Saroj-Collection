package com.logimetrix.locationsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessingService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public int counter=0;
    Timer timer ;
    TimerTask timerTask ;
    String TAG = "Timers" ;
    int Your_X_SECS = 60 ;
    private final String TAG1 = "BackgroundLocationUpdateService";
    private final String TAG_LOCATION = "TAG_LOCATION";
    private Context context;
    private boolean stopService = false;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
   // APIInterface apiInterface = APIClient1.getClient().create(APIInterface.class);
    /* For Google Fused API */
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private String latitude = "0.0", longitude = "0.0";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LocationManager locationManager;
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    private int battery_value = 0;
    private GPSTracker gpsTracker;
    BroadcastReceiver broadcastReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.logimetrix.locationsync";
        String channelName = "Background Location";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle(null)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

        Log.e(TAG_LOCATION, "Foreground.");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        pref=getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor= pref.edit();
        buildGoogleApiClient();
        startTimer();

       installListener();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.location.PROVIDERS_CHANGED");
//        this.getApplicationContext().registerReceiver(receiver, filter);


        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService = true;
        System.out.println("Service Destroyed");

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);

            Log.e(TAG_LOCATION, "Location Update Callback Removed");
        }
        //Internet receiver
        //unregisterReceiver(broadcastReceiver);

        stopTimerTask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, MyReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }
    final Handler handler = new Handler() ;
    public void startTimer () {
        timer = new Timer() ;
        initializeTimerTask() ;
        timer.schedule( timerTask , 100 , Your_X_SECS * 1000 ) ; //
    }
    public void stopTimerTask () {
        if ( timer != null ) {
            timer .cancel() ;
            timer = null;
        }
    }

    public void initializeTimerTask () {

        timerTask = new TimerTask() {
            public void run () {
                handler .post( new Runnable() {
                    public void run () {


                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                        if (isGpsEnabled || isNetworkEnabled) {
                            //location is enabled
                            syncgps("On");
                            gpsTracker = new GPSTracker(getApplicationContext());
                            if (gpsTracker.canGetLocation()) {

                                latitude = String.valueOf(gpsTracker.getLatitude());
                                longitude = String.valueOf(gpsTracker.getLongitude());
                            }
                            System.out.println("service is start!!"+latitude+"-"+longitude);
                           // Toast.makeText(context, latitude+"  "+longitude, Toast.LENGTH_SHORT).show();

                            Call<String> call1 = apiInterface.sendlocation(pref.getString("id",""),""+latitude,""+longitude,pref.getString("api_token",""));
                            call1.enqueue(new Callback<String>() {
                                              @Override
                                              public void onResponse(Call<String> call, Response<String> response) {
                                                //  Toast.makeText(context, "Sent to Server", Toast.LENGTH_SHORT).show();
                                              }

                                              @Override
                                              public void onFailure(Call<String> call, Throwable t) {
                                                  call.cancel();
                                              }
                                          });





                            //installListener();
                           // synclocation();
                          //  Toast.makeText(context, "Gps On.", Toast.LENGTH_SHORT).show();
                        } else {
                            gpsTracker = new GPSTracker(getApplicationContext());
                            if (gpsTracker.canGetLocation()) {

                                latitude = String.valueOf(gpsTracker.getLatitude());
                                longitude = String.valueOf(gpsTracker.getLongitude());
                            }
                            System.out.println("service is start!!"+latitude+"-"+longitude);
                           // installListener();
                            synclocation();
                            syncgps("Off");
                        }
                    //    createNotification() ;
                    }
                }) ;
            }
        } ;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setExpirationDuration(10 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e(TAG_LOCATION, "GPS Success");
                        requestLocationUpdate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            int REQUEST_CHECK_SETTINGS = 214;

                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult((Activity) getApplicationContext(), REQUEST_CHECK_SETTINGS);
                        } catch (Exception sie) {
                            Log.e(TAG_LOCATION, "Unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG_LOCATION, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG_LOCATION, "checkLocationSettings -> onCanceled");
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectGoogleClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = "0.0";
        longitude = "0.0";

        Log.e(TAG_LOCATION, "Location Changed Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());


        if (latitude.equalsIgnoreCase("0.0") && longitude.equalsIgnoreCase("0.0")) {
            requestLocationUpdate();
        } else {
            Log.e(TAG_LOCATION, "Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
             }
     //   locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
     //   // getting GPS status
     //   Boolean gps = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
     //   if (gps){
     //       syncgps("On");
     //       Toast.makeText(context, "Gps On", Toast.LENGTH_SHORT).show();
     //   }else {
     //       syncgps("Off");
     //       Toast.makeText(context, "Gps Off", Toast.LENGTH_SHORT).show();
     //   }
//
    }

    protected synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connectGoogleClient();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG_LOCATION, "Location Received");
                mCurrentLocation = locationResult.getLastLocation();
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    public void synclocation(){
       Call<String> call1 = apiInterface.sendlocation(pref.getString("id",""),""+latitude,""+longitude,pref.getString("api_token",""));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("service response is :: "+response.body());
                //extra toast
               // Toast.makeText(context, "Sent to Server", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
             //   progressdialog.dismiss();
                call.cancel();
            }
        });

    }
    private class BatteryMonitorBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.e("", arg1.toString());
            battery_value = arg1.getIntExtra("level", 0);

        }
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {


            }
        }
    };
    public void syncgps(String status)
    {
        int battery=getBatteryPercentage(getApplicationContext());
        System.out.println("battery level is :: "+battery);
        Call<String> call1 = apiInterface.gpsStatus(pref.getString("id",""),status,String.valueOf(battery));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println(status+" :: syncgps response is :: ");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //   progressdialog.dismiss();
                call.cancel();
            }
        });
    }

    public static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }




    private void installListener() {

        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();

                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    Log.d("Internet Connection---", info.toString() + " "
                            + state.toString());

                    if (state == NetworkInfo.State.CONNECTED) {

                        onNetworkUp();

                    } else {

                        onNetworkDown();

                    }

                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private void onNetworkDown() {
        Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
    }

    private void onNetworkUp() {

    }



}
