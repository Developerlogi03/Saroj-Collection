package com.logimetrix.locationsync.fragments;


import static android.content.Context.MODE_PRIVATE;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.logimetrix.locationsync.APIClient;
import com.logimetrix.locationsync.APIInterface;
import com.logimetrix.locationsync.CollectMoney;
import com.logimetrix.locationsync.Mains;
import com.logimetrix.locationsync.Modal.ClosingBalance;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.utils.GpsTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentTwo extends Fragment implements AdapterView.OnItemSelectedListener{

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
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, viewGroup, false);
        pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        retailerId = getArguments().getString("retailerId");

        hiddenCash = (RelativeLayout) view.findViewById(R.id.hiddenCash);
        hiddenCheque= (LinearLayout) view.findViewById(R.id.hiddenLinearCheque);


        Spinner spin = view.findViewById(R.id.spinnerDealers);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter ad = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,firm);

        spin.setAdapter(ad);


        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        et_uniqueid=(EditText) view.findViewById(R.id.et_uniqueid);
        bankname=(EditText) view.findViewById(R.id.bankname);
        amount=(EditText) view.findViewById(R.id.amount);
        progressBar = view.findViewById(R.id.progressRecycler);
        date123 = view.findViewById(R.id.date123);
        tvClosingBal = view.findViewById(R.id.tvClosingBal);
        tvtotalCredit = view.findViewById(R.id.tvtotalCredit);
        tvtotalDebit = view.findViewById(R.id.tvtotalDebit);

        btnIncrease = view.findViewById(R.id.increase);
        btnDecrease = view.findViewById(R.id.decrease);
        tvValue = view.findViewById(R.id.integer_number);

        tvTotal = view.findViewById(R.id.tvTotal);
        btncashDeposit = view.findViewById(R.id.btnDepositCash);

        btnIncrease500 = view.findViewById(R.id.increase500);
        btnDecrease500 = view.findViewById(R.id.decrease500);
        tvValue500 = view.findViewById(R.id.integer_number500);

        btnIncrease200 = view.findViewById(R.id.increase200);
        btnDecrease200 = view.findViewById(R.id.decrease200);
        tvValue200 = view.findViewById(R.id.integer_number200);

        btnIncrease100 = view.findViewById(R.id.increase100);
        btnDecrease100 = view.findViewById(R.id.decrease100);
        tvValue100 = view.findViewById(R.id.integer_number100);

        btnIncrease50 = view.findViewById(R.id.increase50);
        btnDecrease50 = view.findViewById(R.id.decrease50);
        tvValue50 = view.findViewById(R.id.integer_number50);

        btnIncrease20 = view.findViewById(R.id.increase20);
        btnDecrease20 = view.findViewById(R.id.decrease20);
        tvValue20 = view.findViewById(R.id.integer_number20);

        btnIncrease10 = view.findViewById(R.id.increase10);
        btnDecrease10 = view.findViewById(R.id.decrease10);
        tvValue10 = view.findViewById(R.id.integer_number10);

        btnIncreaseChnage = view.findViewById(R.id.increasecoin);
        btnDecreasechnage = view.findViewById(R.id.decreasecoin);
        tvValuechnage = view.findViewById(R.id.integer_numbercoin);

        etPaymentrefNoCash = view.findViewById(R.id.etpaymentrefcash);
        etPaymentrefNocheque = view.findViewById(R.id.etpaymentrefcheque);


        date123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

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



        Button depositCheque=(Button) view.findViewById(R.id.btnDepositCheque);
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
                            ProgressDialog progressdialog = new ProgressDialog(getContext());
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
                                            Toast.makeText(getContext(),"Payment Added Successfully!",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getContext(), Mains.class));
                                            getActivity().finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getContext(),"Error!",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Amount can't be zero.", Toast.LENGTH_SHORT).show();
                }

                //  Toast.makeText(CollectMoney.this, strLatitude+strLongitude, Toast.LENGTH_SHORT).show();
            }
        });

        // totalCash();
        ToggleSwitch toggleSwitch = (ToggleSwitch) view.findViewById(R.id.toggleSwitch);

        toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                position = toggleSwitch.getCheckedTogglePosition();
                if(position == 0){
                    if (closingBalint != 0){
                        hiddenCash.setVisibility(View.VISIBLE);
                        hiddenCheque.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "Cash payment selected", Toast.LENGTH_SHORT).show();
                } else {
                    if (closingBalint != 0){
                        hiddenCash.setVisibility(View.GONE);
                        hiddenCheque.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getContext(), "Cheque payment selected", Toast.LENGTH_SHORT).show();
                }


            }
        });



        return view;
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
                ProgressDialog progressdialog = new ProgressDialog(getContext());
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
                                Toast.makeText(getContext(), "Payment Added Successfully!", Toast.LENGTH_LONG).show();

                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Error!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
                Toast.makeText(getContext(), "Server Connectivity error. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
        if (i == 0){
            dealerId = "1";
            checkClosingBalance();
            Toast.makeText(getContext(), firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }else if (i==1){
            dealerId = "30";
            checkClosingBalance();
            Toast.makeText(getContext(), firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }else if (i == 2){
            dealerId = "31";
            checkClosingBalance();
            Toast.makeText(getContext(), firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
