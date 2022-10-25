package com.logimetrix.locationsync;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logimetrix.locationsync.Modal.DealerModal;
import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.Modal.RetailerCustomModel;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.Modal.WeekModel;
import com.logimetrix.locationsync.adapter.RetailerListAdapter;
import com.logimetrix.locationsync.adapter.WeekListAdapter;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    RetailerListAdapter retailerListAdapter;
    EditText et_uniqueid;
    Button btn_login, submit;
    private AppBarConfiguration appBarConfiguration;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    //APIInterface apiInterface1 = APIClient1.getClient().create(APIInterface.class);
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PERMISSION_ALL = 1;
    protected PowerManager.WakeLock mWakeLock;
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
   // BottomNavigationView bottomNav;
    String api_token="";
    ArrayList<Retailerpojo> aitRecord;
    WeekListAdapter weekAdapter;
    List<String> responseList;
    String[] PERMISSIONS = {
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };

    BroadcastReceiver broadcastReceiver;
    private int battery_value = 0;
    String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"};
    int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};


    public String retailerId = "1",dealerId = "1";
    ProgressBar progressBar;
    RecyclerView recyclerViewRetailers;
    LinearLayoutManager linearLayoutManager;
    public List<RetailerModel.Retailer> retailerList;
    public ArrayList<RetailerModel.Retailer> retailerArrayList = new ArrayList<>();
    public List<DealerModal.Dealer> dealerList;
    int role_id;
    AutoCompleteTextView autocomplete;
    SearchView searchView;
    private List<WeekModel> weekList = new ArrayList<>();

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
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
     //   role_id = Integer.parseInt(pref.getString("role_id",null));
        System.out.println("access token is ::"+pref.getString("api_token",null));
        api_token=pref.getString("api_token",null);
     //   role_id=pref.getString("role_id",null);
     //   Log.d(TAG, "onCreate: "+role_id);


        setSupportActionBar(findViewById(R.id.toolbar));
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


        searchView = (SearchView) findViewById(R.id.searchView);
        submit = findViewById(R.id.btnDealerRetailer);
        progressBar = findViewById(R.id.progressRecycler);
        recyclerViewRetailers = findViewById(R.id.rcyclRtlrLst);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewRetailers.setLayoutManager(linearLayoutManager);


        //remaove
       // recyclerViewRetailers.setLayoutManager(new LinearLayoutManager(this));
        //remove when to use SetList
       // retailerListAdapter = new RetailerListAdapter();
       // recyclerViewRetailers.setAdapter(retailerListAdapter);

        installListener();

        aitRecord=new ArrayList<>();
        responseList = new ArrayList<String>();
        et_uniqueid=(EditText)findViewById(R.id.et_uniqueid);

        fuel=(ImageView) findViewById(R.id.fuel);

        recyclerViewRetailers.setLayoutManager(new LinearLayoutManager(this));
        btn_login=(Button)findViewById(R.id.btn_login);
        

        Log.d(TAG, "onCreate: "+pref.getString("role_id",null));

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

        //remove for demo

        //for reatilers
       // fetchRetailers();

       // fetchDealers();

        fetchWeek();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Mains.this, "Retailer ID : "+retailerId, Toast.LENGTH_SHORT).show();
                Toast.makeText(Mains.this, "Dealer ID : "+dealerId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Mains.this,CollectMoney.class);
                intent.putExtra("dealerID",dealerId);
                intent.putExtra("retailerID", retailerId);
                startActivity(intent);

            }
        });
        //Remove
       // getRetailers();

//        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
//        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
//        registerReceiver(locationSwitchStateReceiver,filter);


        // assign variable
      //  autocomplete = (AutoCompleteTextView)
        //        findViewById(R.id.autoCompleteTextView1);

      //  ArrayAdapter<String> adapter = new ArrayAdapter<String>
      //          (this,android.R.layout.select_dialog_item, arr);
//
      //  autocomplete.setThreshold(2);
      //  autocomplete.setAdapter(adapter);


        retailerList = new ArrayList<>();
        String token = pref.getString("api_token",null);
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<List<RetailerModel.Retailer>> call = apiService.retalersNew(token);

        call.enqueue(new Callback<List<RetailerModel.Retailer>>() {
            @Override
            public void onResponse(Call<List<RetailerModel.Retailer>> call, Response<List<RetailerModel.Retailer>> response) {
                if (response.isSuccessful()){
                    retailerList = response.body();
                    Log.d("TAG","list response "+retailerList);
                    System.out.print("myList : "+retailerList);
                    // retailerListAdapter.setMovieList(getApplicationContext(),retailerList);
                    retailerListAdapter = new RetailerListAdapter(retailerList, getApplicationContext(), new CustomItemClickListener() {
                        @Override
                        public void onItemClick(RetailerModel.Retailer retailer, int position) {
                            Toast.makeText(Mains.this, retailer.getName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWeekClick(WeekModel weekModel, int position) {

                        }

                        @Override
                        public void onGroupClick(GroupModel groupModel, int position) {

                        }

                        @Override
                        public void onRetailerClick(RetailerCustomModel retailerCustomModel, int position) {

                        }
                    });

                    recyclerViewRetailers.setAdapter(retailerListAdapter);

                }

            }

            @Override
            public void onFailure(Call<List<RetailerModel.Retailer>> call, Throwable t) {
              //  Toast.makeText(Mains.this, "Connection Error! Please try again.", Toast.LENGTH_SHORT).show();
                Log.d("TAG","Response = "+t.toString());
            }
        });




        // for retailers

        /*

          searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()){
                    retailerListAdapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    retailerListAdapter.getFilter().filter(newText);
                }else {
                    fetchRetailers();
                }
                return false;
            }
        });

         */



    }

    private void fetchWeek() {
        progressBar.setVisibility(View.VISIBLE);
        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<String> call = apiInterface.week(token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    String routId, routeName, dayId = null, dayName;
                    if (!response.body().isEmpty()){
                        String jsonResponse = response.body().toString();
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(jsonResponse);

                            if (jsonObject.optString("flag").equals("true")){

                                JSONArray dataArray  = jsonObject.getJSONArray("routes");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    routId = dataobj.getString("id");
                                    routeName = dataobj.getString("name");
                                    dayId = dataobj.getString("day");

                                    JSONArray dataDays  = jsonObject.getJSONArray("day");

                                    for (int j = 0; j<dataDays.length(); j++){
                                        JSONObject dayData = dataDays.getJSONObject(j);
                                        dayName = dayData.getString(dayId);

                                        WeekModel weekModel = new WeekModel();
                                        weekModel.setName(dayName);
                                        weekModel.setRoutId(routId);
                                        weekList.add(weekModel);
                                        weekAdapter = new WeekListAdapter(weekList, getApplicationContext(), new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(RetailerModel.Retailer retailer, int position) {

                                            }

                                            @Override
                                            public void onWeekClick(WeekModel weekModel, int position) {
                                                Intent intent = new Intent(Mains.this,GroupActivity.class);
                                                intent.putExtra("routeId", weekModel.getRoutId().toString());
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onGroupClick(GroupModel groupModel, int position) {

                                            }

                                            @Override
                                            public void onRetailerClick(RetailerCustomModel retailerCustomModel, int position) {

                                            }
                                        });
                                        recyclerViewRetailers.setAdapter(weekAdapter);
                                        weekAdapter.notifyDataSetChanged();
                                    }

                                }

                                //

                            }else if (jsonObject.optString("flag").equals("false")){

                                if (jsonObject.optString("is_token_expired").equals("true")){
                                    new SweetAlertDialog(Mains.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Logout")
                                            .setContentText(jsonObject.optString("message").toString())
                                            .setConfirmText("okay")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    logout();
                                                }
                                            })
                                            .show();
                                }
                                Toast.makeText(Mains.this,jsonObject.optString("message").toString() , Toast.LENGTH_SHORT).show();

                                // Toast.makeText(FarmerBasicKYC.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    private void fetchRetailers() {
        progressBar.setVisibility(View.VISIBLE);
        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RetailerModel> call = apiInterface.retalers(token);
        call.enqueue(new Callback<RetailerModel>() {
            @Override
            public void onResponse(Call<RetailerModel> call, Response<RetailerModel> response) {
                if (response.isSuccessful()){
                    if (!response.body().getFlag()){
                        if (response.body().getTokenExpired()){
                            new SweetAlertDialog(Mains.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Logout")
                                    .setContentText(response.body().getMessage())
                                    .setConfirmText("okay")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            logout();
                                        }
                                    })
                                    .show();

                        }
                        Toast.makeText(Mains.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }else {

                        List<RetailerModel.Retailer> listRetailer;                   //Changes for Search Filter
                        listRetailer = response.body().getRetailers();
                        retailerArrayList = new ArrayList<>(listRetailer);

                     //   retailerList = response.body().getRetailers();

                        //Remove if use setList
                        retailerListAdapter = new RetailerListAdapter(retailerArrayList, getApplicationContext(), new CustomItemClickListener() {
                            @Override
                            public void onItemClick(RetailerModel.Retailer retailer, int position) {
                                Intent intent = new Intent(Mains.this,CollectMoney.class);
                                retailerId = String.valueOf(retailer.getId());
                                intent.putExtra("retailerId",retailerId);
                                startActivity(intent);
                            }

                            @Override
                            public void onWeekClick(WeekModel weekModel, int position) {

                            }

                            @Override
                            public void onGroupClick(GroupModel groupModel, int position) {

                            }

                            @Override
                            public void onRetailerClick(RetailerCustomModel retailerCustomModel, int position) {

                            }
                        });

                        recyclerViewRetailers.setAdapter(retailerListAdapter);


                        AutoCompletePlaceAdapter adapter = new AutoCompletePlaceAdapter(getApplicationContext(), retailerList);

                        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(
                                R.id.autoCompleteTextView1
                        );
                        autoCompleteTextView.setThreshold(2);
                        autoCompleteTextView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onFailure(Call<RetailerModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Mains.this, "No response from server!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchDealers(){
        progressBar.setVisibility(View.VISIBLE);
        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DealerModal> call = apiInterface.dealers(token);
        call.enqueue(new Callback<DealerModal>() {
            @Override
            public void onResponse(Call<DealerModal> call, Response<DealerModal> response) {
                if (!response.body().getFlag()){
                    Toast.makeText(Mains.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }else{
                    dealerList = response.body().getDealers();

                    AutoCompleteDealerAdapter autoCompleteDealerAdapter = new AutoCompleteDealerAdapter(getApplicationContext(), dealerList){

                    };

                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
                    autoCompleteTextView.setThreshold(2);
                    autoCompleteTextView.setAdapter(autoCompleteDealerAdapter);
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<DealerModal> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Mains.this, "No response from server!", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.voucher:
              //  Toast.makeText(this, "Not Allowed", Toast.LENGTH_SHORT).show();
                //remove for demo
                voucher();

                return true;
            case R.id.logOut:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void logout() {

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        Intent i = new Intent(Mains.this,MainActivity.class);
        startActivity(i);
        finish();

    }

    private void voucher() {
        if (role_id <= 1){
            Intent intent = new Intent(Mains.this, VoucherApproval.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(Mains.this, VoucherActivity.class);
            startActivity(intent);
        }
        
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
        //Call<String> call1 = apiInterface.gpsStatus(pref.getString("id",""),status,String.valueOf(battery));
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

        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222) {
         //   showdialogforgps();
            turnGPSOn(); //edited
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


    @Override
    protected void onPause() {
        super.onPause();
        installListener();
        startService(new Intent(this,ProcessingService.class));
    }


    public class AutoCompleteDealerAdapter extends ArrayAdapter<DealerModal.Dealer> {
        private List<DealerModal.Dealer> allDealerList;
        private List<DealerModal.Dealer> filteredDealerList;
        private CustomItemClickListener customItemClickListener;



        public AutoCompleteDealerAdapter(@NonNull Context context, @NonNull List<DealerModal.Dealer> dealerList){
            super(context,0,dealerList);
            allDealerList = new ArrayList<>(dealerList);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return dealerFilter;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_simple,parent,false);
            }
            TextView placeDealer = convertView.findViewById(R.id.autocomplete_item_place_label);
            TextView placeDealer1 = convertView.findViewById(R.id.autocomplete_item_place_label_address);

            DealerModal.Dealer dealer = getItem(position);
            if (dealer != null){
                placeDealer.setText(dealer.getName());
                placeDealer1.setText(dealer.getAddress1());
            }


            return convertView;
        }

        private Filter dealerFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();
                filteredDealerList = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0){
                    filteredDealerList.addAll(allDealerList);
                }else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (DealerModal.Dealer dealer: allDealerList) {
                        if (dealer.getName().toLowerCase().contains(filterPattern)) {
                            filteredDealerList.add(dealer);
                        }
                    }
                }
                results.values =filteredDealerList;
                results.count  =filteredDealerList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clear();
                addAll((List) filterResults.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((DealerModal.Dealer) resultValue).getName();
            }
        };

    }


    public class AutoCompletePlaceAdapter extends ArrayAdapter<RetailerModel.Retailer> {
        private List<RetailerModel.Retailer> allPlacesList;
        private List<RetailerModel.Retailer> filteredPlacesList;

        public AutoCompletePlaceAdapter(@NonNull Context context, @NonNull List<RetailerModel.Retailer> placesList) {
            super(context, 0, placesList);

            allPlacesList = new ArrayList<>(placesList);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return placeFilter;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_simple, parent, false
                );
            }
            TextView placeLabel = convertView.findViewById(R.id.autocomplete_item_place_label);
            TextView placeLabel1 = convertView.findViewById(R.id.autocomplete_item_place_label_address);

            RetailerModel.Retailer place = getItem(position);
            if (place != null) {
                placeLabel.setText(place.getName());
                placeLabel1.setText(place.getAddress());
                retailerId = String.valueOf(place.getId());
            }

            return convertView;
        }

        private Filter placeFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                filteredPlacesList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredPlacesList.addAll(allPlacesList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (RetailerModel.Retailer place: allPlacesList) {
                        if (place.getName().toLowerCase().contains(filterPattern)) {
                            filteredPlacesList.add(place);
                        }
                    }
                }

                results.values = filteredPlacesList;
                results.count = filteredPlacesList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((List) results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((RetailerModel.Retailer) resultValue).getName();
            }
        };
    }



}