package com.logimetrix.locationsync;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.logimetrix.locationsync.databinding.ActivityMainBinding;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Location mcurrentLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    TextView textview_first;
    private ProcessingService mYourService;
    EditText et_uniqueid,et_password;
    Button btn_login;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageView simpleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        simpleImageView=(ImageView) findViewById(R.id.simpleImageView);
        if(pref.getString("id",null)!=null)
        {
            startActivity(new Intent(MainActivity.this,Mains.class));
            finish();
        }
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        String rationale = "Please provide location permission so that you can ...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
            }
        });
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean bool = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        showdialogforgps();
        LocationManager lm1 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        boolean bool1 = lm1.isProviderEnabled(LocationManager.GPS_PROVIDER);
        startLocationUpdates();
        initui();

        Glide.with(this)
                .asGif()
                .load(R.drawable.person_gif) //Your gif resource
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into((ImageView)findViewById(R.id.simpleImageView));
    }
    public void initui()
    {
        et_uniqueid=(EditText) findViewById(R.id.et_uniqueid);
        et_password=(EditText) findViewById(R.id.et_password);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }
    public void validation()
    {
        if(et_uniqueid.getText().toString().equals(""))
        {
            et_uniqueid.setError("Enter Username");
            et_uniqueid.requestFocus();
        }
        else if(et_password.getText().toString().equals(""))
        {
            et_password.setError("Enter Password");
            et_password.requestFocus();
        }
        else
        {
            ProgressDialog progressdialog = new ProgressDialog(MainActivity.this);
            progressdialog.setMessage("Please Wait....");
            progressdialog.show();
            progressdialog.setCancelable(false);
            Call<String> call1 = apiInterface.login(et_uniqueid.getText().toString(),et_password.getText().toString());
            call1.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    progressdialog.dismiss();
                    System.out.println("response is :: "+response.body().toString());
                    try {
                        JSONObject jj=new JSONObject(response.body().toString());
                        boolean bool=jj.getBoolean("flag");
                        if(bool) {
                            JSONObject jj1 = jj.getJSONObject("user");
                            editor.putString("email", jj1.getString("email"));
                            editor.putString("id", jj1.getString("id"));
                            editor.putString("api_token", jj1.getString("api_token"));
                            editor.apply();
                            editor.commit();
                            startActivity(new Intent(MainActivity.this,Mains.class));
                            finish();
                        }else
                        {
                            Toast.makeText(MainActivity.this,"Wrong Credentials!",Toast.LENGTH_LONG).show();
                            et_uniqueid.setError("Wrong Username");
                            et_uniqueid.requestFocus();
                            et_password.setError("Wrong Password");
                            et_password.requestFocus();
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
    }

    private boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    private void showdialogforgps() {
        if (!isGPSEnabled()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(MainActivity.this);
            alertbox.setMessage("Application requires GPS for your actual position and GPS is not on, press OK button to switch on");
            alertbox.setCancelable(false);
            alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    turnGPSOn();
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),222);
                }
            });
            alertbox.show();
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
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                                                        @Override
         public void onSuccess(Location location) {
                if (location != null) {
                        mcurrentLocation = location;
                     } else {
                 getNewLocation();
                      }
                   }
             }
        );

        mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    private void getNewLocation() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mcurrentLocation = locationResult.getLastLocation();

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        };
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
}