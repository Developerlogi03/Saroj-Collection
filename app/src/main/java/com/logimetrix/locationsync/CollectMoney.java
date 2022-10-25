package com.logimetrix.locationsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.logimetrix.locationsync.Modal.ClosingBalance;
import com.logimetrix.locationsync.utils.GpsTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectMoney extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    int minteger = 0,minteger500 = 0,minteger200 = 0,minteger100 = 0,minteger50 = 0,minteger20 = 0,minteger10 = 0,mintegerchange = 0 ;
    public Button btnIncrease, btnDecrease,btnIncrease500,btnIncrease200,btnIncrease100,btnIncrease50,btnIncrease20,btnIncrease10,btnIncreaseChnage,btnDecrease500,btnDecrease200,btnDecrease100,btnDecrease50,btnDecrease20,btnDecrease10,btnDecreasechnage;
    TextView tvValue,tvValue500,tvValue200,tvValue100,tvValue50,tvValue20,tvValue10,tvValuechnage;
    int total2000 = 0, total500= 0, total200= 0, total100= 0,total50= 0,total20= 0,total10= 0,totalchnage= 0, totalCash =0;
    TextView tvTotal, tvClosingBal, tvtotalDebit,tvtotalCredit;
    Button btncashDeposit;
    ProgressBar progressBar;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String dealerId, retailerId;
    int closingBalint,totalDebit, totalCredit;
    RelativeLayout hiddenCash;
    LinearLayout hiddenCheque;
    ImageView imgBack;

    private GpsTracker gpsTracker;
    public String strLatitude = "0.0", strLongitude = "0.0";

    EditText et_uniqueid,bankname,date123,amount, etPaymentrefNoCash, etPaymentrefNocheque;
    DatePickerDialog datePickerDialog;

    String[] firm = { "MOSARAM SHIVRAM DAS - VIJAY LAXMI NAGAR", "MOSARAM CONSUMER PRODUCTS PVT - VIJAY LAXMI NAGAR",
            "MOSARAM CONSUMER PRODUCTS PVT - DEVI KALI ROAD"};

    //MOSARAM SHIVRAM DAS - VIJAY LAXMI NAGAR - id=1
    //MOSARAM CONSUMER PRODUCTS PVT - VIJAY LAXMI NAGAR id=30
    //"MOSARAM CONSUMER PRODUCTS PVT - DEVI KALI ROAD" id=31


    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    int PERMISSION_ID = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_money);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        hiddenCash = (RelativeLayout) findViewById(R.id.hiddenCash);
        hiddenCheque= (LinearLayout) findViewById(R.id.hiddenLinearCheque);
        imgBack = findViewById(R.id.back);

        retailerId = getIntent().getStringExtra("routeId");

        Toast.makeText(getApplicationContext(), retailerId, Toast.LENGTH_SHORT).show();

        Spinner spin = findViewById(R.id.spinnerDealers);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_item,firm);

        spin.setAdapter(ad);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        et_uniqueid=(EditText) findViewById(R.id.et_uniqueid);
        bankname=(EditText) findViewById(R.id.bankname);
        amount=(EditText) findViewById(R.id.amount);
        progressBar = findViewById(R.id.progressRecycler);
        date123 = findViewById(R.id.date123);
        tvClosingBal = findViewById(R.id.tvClosingBal);
        tvtotalCredit = findViewById(R.id.tvtotalCredit);
        tvtotalDebit = findViewById(R.id.tvtotalDebit);

        btnIncrease = findViewById(R.id.increase);
        btnDecrease = findViewById(R.id.decrease);
        tvValue = findViewById(R.id.integer_number);

        tvTotal = findViewById(R.id.tvTotal);
        btncashDeposit = findViewById(R.id.btnDepositCash);

        btnIncrease500 = findViewById(R.id.increase500);
        btnDecrease500 = findViewById(R.id.decrease500);
        tvValue500 = findViewById(R.id.integer_number500);

        btnIncrease200 = findViewById(R.id.increase200);
        btnDecrease200 = findViewById(R.id.decrease200);
        tvValue200 = findViewById(R.id.integer_number200);

        btnIncrease100 = findViewById(R.id.increase100);
        btnDecrease100 = findViewById(R.id.decrease100);
        tvValue100 = findViewById(R.id.integer_number100);

        btnIncrease50 = findViewById(R.id.increase50);
        btnDecrease50 = findViewById(R.id.decrease50);
        tvValue50 = findViewById(R.id.integer_number50);

        btnIncrease20 = findViewById(R.id.increase20);
        btnDecrease20 = findViewById(R.id.decrease20);
        tvValue20 = findViewById(R.id.integer_number20);

        btnIncrease10 = findViewById(R.id.increase10);
        btnDecrease10 = findViewById(R.id.decrease10);
        tvValue10 = findViewById(R.id.integer_number10);

        btnIncreaseChnage = findViewById(R.id.increasecoin);
        btnDecreasechnage = findViewById(R.id.decreasecoin);
        tvValuechnage = findViewById(R.id.integer_numbercoin);

        etPaymentrefNoCash = findViewById(R.id.etpaymentrefcash);
        etPaymentrefNocheque = findViewById(R.id.etpaymentrefcheque);

        //checkClosingBalance();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        date123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(CollectMoney.this, new DatePickerDialog.OnDateSetListener() {

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



       Button depositCheque=(Button) findViewById(R.id.btnDepositCheque);
        depositCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentRef = " ";
                if(et_uniqueid.getText().toString().equals("")){
                    et_uniqueid.setError("Enter Check Number");
                    et_uniqueid.requestFocus();
                }
                else if(bankname.getText().toString().equals("")){
                    bankname.setError("Enter Bank Name");
                    bankname.requestFocus();
                }
             //   else if(date123.getText().toString().equals("")){
             //       date123.setError("Enter date");
             //       date123.requestFocus();
             //   }
                else {
                    if (!etPaymentrefNocheque.getText().toString().trim().isEmpty()){
                        paymentRef = etPaymentrefNocheque.getText().toString();

                        try{
                            JSONObject jsonl = new JSONObject();
                            jsonl.put("lat", "00.00");
                            jsonl.put("lng", "00.00");
                            ProgressDialog progressdialog = new ProgressDialog(CollectMoney.this);
                            progressdialog.setMessage("Please Wait...");
                            progressdialog.show();
                            progressdialog.setCancelable(false);
                            APIInterface apiInterface1 = APIClient.getClient().create(APIInterface.class);
                            Call<String> call1 = apiInterface1.addChequepayment(""+jsonl, pref.getString("id", ""),pref.getString("api_token", ""), retailerId, "Cheque", amount.getText().toString(), et_uniqueid.getText().toString(),dealerId,paymentRef);
                            call1.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    progressdialog.dismiss();
                                    System.out.println("response is :: " + response);
                                    System.out.println("response is :: " + response.body());
                                    try {
                                        JSONObject jj = new JSONObject(response.body().toString());
                                        boolean bool = jj.getBoolean("flag");
                                        if(bool){
                                            Toast.makeText(CollectMoney.this,"Payment Added Successfully!",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(CollectMoney.this, Mains.class));
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(CollectMoney.this,"Error!",Toast.LENGTH_LONG).show();
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
                        }catch (Exception e){}
                    }else {
                        etPaymentrefNocheque.setError("Required");

                    }
                }
            }
        });






        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger = minteger + 1;
                tvValue.setText(String.valueOf(minteger));
                totalCash();
               }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger > 0){
                    minteger = minteger - 1;
                    tvValue.setText(String.valueOf(minteger));
                    totalCash();

                }
            }
        });


        btnIncrease500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger500 = minteger500 + 1;
                tvValue500.setText(String.valueOf(minteger500));

                totalCash();

            }
        });

        btnDecrease500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger500 > 0){
                    minteger500 = minteger500 - 1;
                    tvValue500.setText(String.valueOf(minteger500));

                    totalCash();

                }
            }
        });



        btnIncrease200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger200 = minteger200 + 1;
                tvValue200.setText(String.valueOf(minteger200));
                totalCash();

            }
        });

        btnDecrease200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger200 > 0){
                    minteger200 = minteger200 - 1;
                    tvValue200.setText(String.valueOf(minteger200));
                    totalCash();
                }
            }
        });


        btnIncrease100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger100 = minteger100 + 1;
                tvValue100.setText(String.valueOf(minteger100));
                totalCash();

            }
        });

        btnDecrease100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger100 > 0){
                    minteger100 = minteger100 - 1;
                    tvValue100.setText(String.valueOf(minteger100));
                    totalCash();
                }
            }
        });


        btnIncrease50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger50 = minteger50 + 1;
                tvValue50.setText(String.valueOf(minteger50));
                totalCash();

            }
        });

        btnDecrease50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger50 > 0){
                    minteger50 = minteger50 - 1;
                    tvValue50.setText(String.valueOf(minteger50));
                    totalCash();
                }
            }
        });



        btnIncrease20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger20 = minteger20 + 1;
                tvValue20.setText(String.valueOf(minteger20));
                totalCash();


            }
        });

        btnDecrease20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger20 > 0){
                    minteger20 = minteger20 - 1;
                    tvValue20.setText(String.valueOf(minteger20));
                    totalCash();
                }
            }
        });

        btnIncrease10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minteger10 = minteger10 + 1;
                tvValue10.setText(String.valueOf(minteger10));
                totalCash();

            }
        });

        btnDecrease10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minteger10 > 0){
                    minteger10 = minteger10 - 1;
                    tvValue10.setText(String.valueOf(minteger10));
                    totalCash();
                }
            }
        });

        btnIncreaseChnage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mintegerchange = mintegerchange + 1;
                tvValuechnage.setText(String.valueOf(mintegerchange));
                totalCash();
            }
        });

        btnDecreasechnage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mintegerchange > 0){
                    mintegerchange = mintegerchange - 1;
                    tvValuechnage.setText(String.valueOf(mintegerchange));
                    totalCash();
                }
            }
        });

        btncashDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalCash>0){
                    depositCash();
                }else {
                    Toast.makeText(CollectMoney.this, "Amount can't be zero.", Toast.LENGTH_SHORT).show();
                }

              //  Toast.makeText(CollectMoney.this, strLatitude+strLongitude, Toast.LENGTH_SHORT).show();
            }
        });

       // totalCash();
        ToggleSwitch toggleSwitch = (ToggleSwitch) findViewById(R.id.toggleSwitch);

        toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                position = toggleSwitch.getCheckedTogglePosition();
                if(position == 0){
                    if (closingBalint != 0){
                        hiddenCash.setVisibility(View.VISIBLE);
                        hiddenCheque.setVisibility(View.GONE);
                    }
                    Toast.makeText(CollectMoney.this, "Cash payment selected", Toast.LENGTH_SHORT).show();
                } else {
                    if (closingBalint != 0){
                        hiddenCash.setVisibility(View.GONE);
                        hiddenCheque.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(CollectMoney.this, "Cheque payment selected", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            strLongitude = String.valueOf(location.getLongitude());
                            strLatitude = String.valueOf(location.getLatitude());
                          //  latitudeTextView.setText(location.getLatitude() + "");
                          //  longitTextView.setText(location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            strLongitude = String.valueOf(mLastLocation.getLongitude());
            strLatitude = String.valueOf(mLastLocation.getLatitude());
         //   latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
          //  longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }







    private void depositCash() {
        String paymentRef = " ";
        getLastLocation();

            if (!etPaymentrefNoCash.getText().toString().trim().isEmpty()){
                paymentRef = etPaymentrefNoCash.getText().toString();
                try {
                    JSONObject jsonl = new JSONObject();
                    jsonl.put("lat", strLatitude);
                    jsonl.put("lng", strLongitude);
                    JSONObject jsonl1 = new JSONObject();
                    jsonl1.put("2000", tvValue.getText().toString());
                    jsonl1.put("500", tvValue500.getText().toString());
                    jsonl1.put("200", tvValue200.getText().toString());
                    jsonl1.put("100", tvValue100.getText().toString());
                    jsonl1.put("50", tvValue50.getText().toString());
                    jsonl1.put("20", tvValue20.getText().toString());
                    jsonl1.put("10", tvValue10.getText().toString());
                    jsonl1.put("5", "0");
                    jsonl1.put("2", "0");
                    jsonl1.put("1", "0");
                    System.out.println("jsonl is::"+jsonl);
                    System.out.println("multipals_cheque_no is::"+jsonl1);
                    System.out.println("user_id is::"+ pref.getString("id", ""));
                    System.out.println("api_token is::"+ pref.getString("api_token", ""));
                    System.out.println("retailer_id is::"+retailerId);
                    System.out.println("amount is::"+ totalCash);
                    System.out.println("transaction_date is::"+ date123.getText().toString());
                    ProgressDialog progressdialog = new ProgressDialog(CollectMoney.this);
                    progressdialog.setMessage("Please Wait....");
                    progressdialog.show();
                    progressdialog.setCancelable(false);
                    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

                    Call<String> call1 = apiInterface.addCashpayment1("" + jsonl, pref.getString("id", ""), pref.getString("api_token", ""),retailerId, "Cash",String.valueOf(totalCash), ""+jsonl1,dealerId,paymentRef);
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
                                    Toast.makeText(CollectMoney.this, "Payment Added Successfully!", Toast.LENGTH_LONG).show();

                                    finish();
                                } else {
                                    Toast.makeText(CollectMoney.this, "Error!", Toast.LENGTH_LONG).show();
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
            }else {
                etPaymentrefNoCash.setError("Required");
            }




    }

    private void checkClosingBalance() {
        progressBar.setVisibility(View.VISIBLE);

        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ClosingBalance> call = apiInterface.closingBal(token,dealerId,retailerId);
        call.enqueue(new Callback<ClosingBalance>() {
            @Override
            public void onResponse(Call<ClosingBalance> call, Response<ClosingBalance> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);

                    if (response.body().getType() != null && response.body().getType().toString().toLowerCase().equals("credit")){


                        closingBalint = response.body().getClosingBalance();
                        totalDebit = response.body().getTotalDebit();
                        totalCredit = response.body().getTotalCredit();
                        tvClosingBal.setText(String.valueOf(closingBalint));
                        tvtotalCredit.setText(String.valueOf(totalCredit));
                        tvtotalDebit.setText(String.valueOf(totalDebit));

                        tvClosingBal.setTextColor(Color.parseColor("#00FF00"));

                    }else if (response.body().getType() != null && response.body().getType().toString().toLowerCase().equals("debit")){


                        closingBalint = response.body().getClosingBalance();
                        totalDebit = response.body().getTotalDebit();
                        totalCredit = response.body().getTotalCredit();

                        tvClosingBal.setText(String.valueOf(closingBalint));
                        tvtotalCredit.setText(String.valueOf(totalCredit));
                        tvtotalDebit.setText(String.valueOf(totalDebit));

                        tvClosingBal.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ClosingBalance> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CollectMoney.this, "Server Connectivity error. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void totalCash(){
        total2000 = 2000*minteger;
        total500 = 500*minteger500;
        total200 = 200*minteger200;
        total100 = 100*minteger100;
        total50 = 50*minteger50;
        total20 = 20*minteger20;
        total10= 10*minteger10;
        totalchnage = mintegerchange;
        totalCash = total2000 + total500 + total200 + total100 + total50 + total20 + total10 + totalchnage;

        tvTotal.setText(String.valueOf(totalCash));
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (i == 0){
            dealerId = "1";
            checkClosingBalance();
            Toast.makeText(this, firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }else if (i==1){
            dealerId = "30";
            checkClosingBalance();
            Toast.makeText(this, firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }else if (i == 2){
            dealerId = "31";
            checkClosingBalance();
            Toast.makeText(this, firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}