package com.logimetrix.locationsync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cash extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    EditText twonote,twonote1,twonote2,twonote3,twonote4,twonote5,twonote6,twonote7,twonote8,twonote9;
    Button btn_login;
    TextView total;
 String ioo="";
    DatePickerDialog datePickerDialog;
    SimpleDateFormat sdf;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private String latitude = "0.0", longitude = "0.0";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String rid="";
    EditText date123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cash Payment");
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        buildGoogleApiClient();
        Intent ii=getIntent();
        rid=ii.getStringExtra("rid");
        twonote=(EditText) findViewById(R.id.twonote);
        date123=(EditText) findViewById(R.id.date123);
        total=(TextView) findViewById(R.id.total);
        twonote1=(EditText) findViewById(R.id.twonote1);
        twonote2=(EditText) findViewById(R.id.twonote2);
        twonote3=(EditText) findViewById(R.id.twonote3);
        twonote4=(EditText) findViewById(R.id.twonote4);
        twonote5=(EditText) findViewById(R.id.twonote5);
        twonote6=(EditText) findViewById(R.id.twonote6);
        twonote7=(EditText) findViewById(R.id.twonote7);
        twonote8=(EditText) findViewById(R.id.twonote8);
        twonote9=(EditText) findViewById(R.id.twonote9);
        btn_login=(Button) findViewById(R.id.btn_login);
        date123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(Cash.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        //  String currentdate = sdf.format(new Date());
                        //  String calanderDate = sdf.format(newDate.getTime());
                        try {
                            date123.setText(year+ "-"+(monthOfYear+1)+"-"+dayOfMonth);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        twonote.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                 io+=getValue(twonote.getText().toString())*2000;
                 io+=getValue(twonote1.getText().toString())*500;
                 io+=getValue(twonote2.getText().toString())*200;
                 io+=getValue(twonote3.getText().toString())*100;
                 io+=getValue(twonote4.getText().toString())*50;
                 io+=getValue(twonote5.getText().toString())*20;
                 io+=getValue(twonote6.getText().toString())*10;
                 io+=getValue(twonote7.getText().toString())*5;
                 io+=getValue(twonote8.getText().toString())*2;
                 io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote2.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote3.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote4.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote5.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote6.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote7.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote8.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        twonote9.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    int io=0;
                    io+=getValue(twonote.getText().toString())*2000;
                    io+=getValue(twonote1.getText().toString())*500;
                    io+=getValue(twonote2.getText().toString())*200;
                    io+=getValue(twonote3.getText().toString())*100;
                    io+=getValue(twonote4.getText().toString())*50;
                    io+=getValue(twonote5.getText().toString())*20;
                    io+=getValue(twonote6.getText().toString())*10;
                    io+=getValue(twonote7.getText().toString())*5;
                    io+=getValue(twonote8.getText().toString())*2;
                    io+=getValue(twonote9.getText().toString())*1;
                    total.setText("Rs"+io);
                    ioo=""+io;
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ioo=="")
                {
                    Toast.makeText(Cash.this,"Enter Amount",Toast.LENGTH_LONG).show();
                }
                else if(date123.getText().toString().equals("")){
                    date123.setError("Enter date");
                    date123.requestFocus();
                }
                else {
                    try {
                        JSONObject jsonl = new JSONObject();
                        jsonl.put("lat", latitude);
                        jsonl.put("lng", longitude);
                        JSONObject jsonl1 = new JSONObject();
                        jsonl1.put("2000", twonote.getText().toString());
                        jsonl1.put("500", twonote1.getText().toString());
                        jsonl1.put("200", twonote2.getText().toString());
                        jsonl1.put("100", twonote3.getText().toString());
                        jsonl1.put("50", twonote4.getText().toString());
                        jsonl1.put("20", twonote5.getText().toString());
                        jsonl1.put("10", twonote6.getText().toString());
                        jsonl1.put("5", twonote7.getText().toString());
                        jsonl1.put("2", twonote8.getText().toString());
                        jsonl1.put("1", twonote9.getText().toString());
                        System.out.println("jsonl is::"+jsonl);
                        System.out.println("multipals_cheque_no is::"+jsonl1);
                        System.out.println("user_id is::"+ pref.getString("id", ""));
                        System.out.println("api_token is::"+ pref.getString("api_token", ""));
                        System.out.println("retailer_id is::"+rid);
                        System.out.println("amount is::"+ioo);
                        System.out.println("transaction_date is::"+ date123.getText().toString());
                        ProgressDialog progressdialog = new ProgressDialog(Cash.this);
                        progressdialog.setMessage("Please Wait....");
                        progressdialog.show();
                        progressdialog.setCancelable(false);
                        Call<String> call1 = apiInterface.addpayment1("" + jsonl, pref.getString("id", ""), pref.getString("api_token", ""), rid, "cash", ioo, ""+jsonl1, date123.getText().toString());
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                progressdialog.dismiss();
                                System.out.println("response is :: " + response);
                                System.out.println("response is :: " + response.body());
                                try {
                                    JSONObject jj = new JSONObject(response.body().toString());
                                    boolean bool = jj.getBoolean("flag");
                                    if (bool) {
                                        Toast.makeText(Cash.this, "Payment Added Successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Cash.this, Mains.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Cash.this, "Error!", Toast.LENGTH_LONG).show();
                                        // finish();
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
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public int getValue(String text){
        if(TextUtils.isEmpty(text)){
            return 0;
        }
        return Integer.valueOf(text);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            //  Log.e(TAG_LOCATION, "Location Update Callback Removed");
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //Log.e(TAG_LOCATION, "GPS Success");
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
                            rae.startResolutionForResult(Cash.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            // Log.e(TAG_LOCATION, "Unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //  Log.e(TAG_LOCATION, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                // Log.e(TAG_LOCATION, "checkLocationSettings -> onCanceled");
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        if (latitude.equalsIgnoreCase("0.0") && longitude.equalsIgnoreCase("0.0")) {
            requestLocationUpdate();
        } else {
            //Log.e(TAG_LOCATION, "Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Cash.this);
        mSettingsClient = LocationServices.getSettingsClient(Cash.this);

        mGoogleApiClient = new GoogleApiClient.Builder(Cash.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connectGoogleClient();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //   Log.e(TAG_LOCATION, "Location Received");
                mCurrentLocation = locationResult.getLastLocation();
                onLocationChanged(mCurrentLocation);
            }
        };
    }
    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(Cash.this);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
}