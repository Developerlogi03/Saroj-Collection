package com.logimetrix.locationsync;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.logimetrix.locationsync.databinding.ActivityMainsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Mains extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    ImageView fuel;
    Retailersadapter adapter;
    EditText et_uniqueid;
    Button btn_login;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainsBinding binding;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PERMISSION_ALL = 1;
    protected PowerManager.WakeLock mWakeLock;
    RecyclerView ai_report_recycler;
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
   // BottomNavigationView bottomNav;
    String api_token="";
    ArrayList<Retailerpojo> aitRecord;
    List<String> responseList;
    AutoCompleteTextView ait_et_farmername;
    String[] PERMISSIONS = {
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };
    Button check,cash;
    String rid="";
    Retailerpojo ri;
    ArrayAdapter<Retailerpojo> adapter123;
    LineChartView lineChartView;
    private int battery_value = 0;
    String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"};
    int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
    private BroadcastReceiver locationSwitchStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    //location is enabled
                    syncgps("On");
                   // Toast.makeText(Mains.this, "Gps On.", Toast.LENGTH_SHORT).show();
                } else {
                    syncgps("Off");
                    //location is disabled
                  //   Toast.makeText(Mains.this, "Gps Off.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private class BatteryMonitorBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.e("", arg1.toString());
            battery_value = arg1.getIntExtra("level", 0);

        }
    }
    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);
        setTitle("Welcome");
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        StrictMode.setThreadPolicy(old);

        Intent battery_intent = new Intent(getApplicationContext(), BatteryMonitorBroadcast.class);
        BatteryMonitorBroadcast batteryMonitorBroadcast = new BatteryMonitorBroadcast();
        registerReceiver(batteryMonitorBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sendBroadcast(battery_intent);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);


        aitRecord=new ArrayList<>();
        responseList = new ArrayList<String>();
        et_uniqueid=(EditText)findViewById(R.id.et_uniqueid);
        check=(Button)findViewById(R.id.check);
        cash=(Button)findViewById(R.id.cash);
        fuel=(ImageView) findViewById(R.id.fuel);
        ait_et_farmername=(AutoCompleteTextView)findViewById(R.id.ait_et_farmername);
        ai_report_recycler=(RecyclerView)findViewById(R.id.ai_report_recycler);
        ai_report_recycler.setLayoutManager(new LinearLayoutManager(this));
        btn_login=(Button)findViewById(R.id.btn_login);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        System.out.println("access token is ::"+pref.getString("api_token",null));
        api_token=pref.getString("api_token",null);
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        Intent intent = new Intent();
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
        final PowerManager pm1 = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm1.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Wavelock");
        this.mWakeLock.acquire();

        try {
            Intent intent1 = new Intent();
            String manufacturer = Build.MANUFACTURER;
            System.out.println("manufacturer is :: "+manufacturer);
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }
            else if ("huawei".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }
            else if ("asus".equalsIgnoreCase(manufacturer)) {
                intent1.setComponent(new ComponentName("com.asus.mobilemanager","com.asus.mobilemanager.autostart.AutoStartActivity"));
            }
            else {
                Log.e("other phone ", "===>");
            }
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //turnGPSOn();
        showdialogforgps();
        if (isGPSEnabled()) {
            startService(new Intent(getBaseContext(), ProcessingService.class));
        }
        getRetailers();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ait_et_farmername.getText().toString().equals("")){
                    ait_et_farmername.setError("Enter retailers name");
                    ait_et_farmername.requestFocus();
                }
                else {
                    startActivity(new Intent(Mains.this, CheckPayment.class).putExtra("rid",rid));
                //    finish();
                }
            }
        });
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ait_et_farmername.getText().toString().equals("")){
                    ait_et_farmername.setError("Enter retailers name");
                    ait_et_farmername.requestFocus();
                }
                else {
                    startActivity(new Intent(Mains.this, Cash.class).putExtra("rid",rid));
                 //   finish();
                }
            }
        });
        adapter123 = new ArrayAdapter<Retailerpojo>
                (Mains.this,android.R.layout.select_dialog_item,aitRecord);

        ait_et_farmername.setAdapter(adapter123);
        adapter123.notifyDataSetChanged();
        ait_et_farmername.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id_farmer_name) {
               // ri = (Retailerpojo) parent.getItemAtPosition(position);
                System.out.println("rid is is in:::::");
                Retailerpojo freindPOJO= adapter123.getItem(position);
                System.out.println("rid is::"+freindPOJO.getId());
                rid=freindPOJO.getId();
            }
        });
//        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
//        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
//        registerReceiver(locationSwitchStateReceiver,filter);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        lineChartView = findViewById(R.id.chart);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#000000"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Performance");
        yAxis.setTextColor(Color.parseColor("#000000"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
        fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void showdialogforgps() {
        if (!isGPSEnabled()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(Mains.this);
            alertbox.setMessage("Application requires GPS for your actual position and GPS is not on, press OK button to switch on");
            alertbox.setCancelable(false);
            alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),222);
                }
            });
            alertbox.show();
        }
    }
    public void syncgps(String status)
    {
        int battery=getBatteryPercentage(getApplicationContext());
        Call<String> call1 = apiInterface.gpsStatus(pref.getString("id",""),status,String.valueOf(battery));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("service response is :: "+response.body().toString());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222) {
            showdialogforgps();
        }
    }
    private boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean bool = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return bool;
    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("provider is ::"+provider);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    public void getRetailers()
    {
        System.out.println("naveen response is :: ");
        ProgressDialog progressdialog = new ProgressDialog(Mains.this);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        Call<String> call1 = apiInterface.retalers(api_token);
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jj=new JSONObject(response.body().toString());
                    boolean bool=jj.getBoolean("flag");
                    if(bool) {
                        JSONArray jj2 = jj.getJSONArray("retailers");
                        for(int i=0;i< jj2.length();i++) {
                            JSONObject jj1=jj2.getJSONObject(i);
                            Retailerpojo rp = new Retailerpojo();
                            rp.setId(jj1.getString("id"));
                            rp.setName(jj1.getString("name"));
                          //  rp.setHindi_name(jj1.getString("hindi_name"));
                            rp.setMobile_number(jj1.getString("mobile_number"));
                            rp.setAddress(jj1.getString("address"));
                           // rp.setHindi_address(jj1.getString("hindi_address"));
                            rp.setGst_number(jj1.getString("gst_number"));
                            rp.setIs_active(jj1.getString("is_active"));
//                            rp.setCreated_at(jj1.getString("created_at"));
//                            rp.setUpdated_at(jj1.getString("updated_at"));
                            responseList.add(jj1.getString("name")+" "+jj1.getString("mobile_number"));
                            aitRecord.add(rp);
                        }
                        progressdialog.dismiss();

//                        adapter = new Retailersadapter(Mains.this, aitRecord);
//                        ai_report_recycler.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();



                    }else
                    {
                        Toast.makeText(Mains.this, "Session Expire!", Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(Mains.this,MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressdialog.dismiss();
                call.cancel();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title(subLocality + "," + state
                            + "," + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

}