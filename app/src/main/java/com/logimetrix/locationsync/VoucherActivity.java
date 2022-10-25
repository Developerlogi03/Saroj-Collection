package com.logimetrix.locationsync;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.logimetrix.locationsync.Modal.BankModel;
import com.logimetrix.locationsync.Modal.VoucherResponseModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner firmSpinner, bankSpinner;
    EditText edtDate, edtAmount;
    Button btnSubmit;
    SharedPreferences pref;
    RadioGroup radioGroup;
    RadioButton rbCash, rbCheque;
    TextView tvExpenseType;
    Context context;

    DatePickerDialog datePickerDialog;

    String[] firm = { "MOSARAM SHIVRAM DAS - VIJAY LAXMI NAGAR", "MOSARAM CONSUMER PRODUCTS PVT - VIJAY LAXMI NAGAR",
            "MOSARAM CONSUMER PRODUCTS PVT - DEVI KALI ROAD"};


    public ArrayList<BankModel.BankAccount> mylist = new ArrayList<>();
    public String  mode = "Cash", date = " ", strAmount, bankId = "2",dealerId;
    double  dblAmount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        firmSpinner = findViewById(R.id.spinnerFirm);
        bankSpinner = findViewById(R.id.spinBank);

        edtDate = findViewById(R.id.edtDate);
        edtAmount = findViewById(R.id.edtAmount);
        tvExpenseType = findViewById(R.id.tvExpensetype);

        radioGroup = (RadioGroup)findViewById(R.id.groupradio);
        rbCash = findViewById(R.id.radia_id1);
        rbCheque = findViewById(R.id.radia_id2);

        btnSubmit = findViewById(R.id.btnSubmit);

        firmSpinner.setOnItemSelectedListener(this);
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item,firm);
        firmSpinner.setAdapter(ad);
        fetchBanks();



        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(VoucherActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        //  String currentdate = sdf.format(new Date());
                        //  String calanderDate = sdf.format(newDate.getTime());
                        try {
                            edtDate.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                            date = edtDate.getText().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });



        // Uncheck or reset the radio buttons initially
      //  radioGroup.clearCheck();

        rbCash.setChecked(true);
        rbCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    edtDate.setVisibility(View.VISIBLE);
                    mode = rbCash.getText().toString();
                }
            }
        });

        rbCheque.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtDate.setVisibility(View.GONE);
                    mode = rbCheque.getText().toString();
                }
            }
        });


        // Add the Listener to the RadioGroup
      //  radioGroup.setOnCheckedChangeListener(
      //          new RadioGroup
      //                  .OnCheckedChangeListener() {
      //              @Override
//
      //              // The flow will come here when
      //              // any of the radio buttons in the radioGroup
      //              // has been clicked
//
      //              // Check which radio button has been clicked
      //              public void onCheckedChanged(RadioGroup group, int checkedId){
      //                  // Get the selected Radio Button
      //                  RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
      //                  int selectedId = radioButton.getId();
      //                  int id = radioGroup.getCheckedRadioButtonId();
      //                  Toast.makeText(VoucherActivity.this, radioButton.getText() +" "+id, Toast.LENGTH_SHORT).show();
      //              }
      //          });
      //


        tvExpenseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(VoucherActivity.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select One Name:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VoucherActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Electricity");
                arrayAdapter.add("Generator");
                arrayAdapter.add("Mobile");
                arrayAdapter.add("Office");
                arrayAdapter.add("Repair & Maintenance");
                arrayAdapter.add("Salary");
                arrayAdapter.add("Sales");
                arrayAdapter.add("Staff");
                arrayAdapter.add("Stationary");
                arrayAdapter.add("Travelling");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(VoucherActivity.this);
                        builderInner.setMessage(strName + " expense type.");
                        builderInner.setTitle("You have selected");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                tvExpenseType.setText(strName);
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
            }
        });

    }

    private void expensetype(){
        String token = pref.getString("api_token",null);

    }

    public void voucherRequest() {
        String token = pref.getString("api_token",null);
        strAmount = edtAmount.getText().toString();
        SweetAlertDialog progressdialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        // ProgressDialog progressdialog = new ProgressDialog(MainActivity.this);
        progressdialog.setTitleText("Please Wait");
        progressdialog.getProgressHelper().setBarColor(Color.parseColor("#186FA0"));
        progressdialog.show();
        progressdialog.setCancelable(false);


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        //change the datatype from server for inputs
        String dealer = dealerId.toLowerCase();
        String md = mode.toString().toLowerCase();
        String bank = bankId.toString().toLowerCase();
        String amount = strAmount.toString().toLowerCase();
        String extype = "1";
        String strDate = date.toString();

        Log.d("Tag Voucher - ",token+" "+dealer+" "+md+" "+bank+" "+amount+" "+strDate);
        Call<VoucherResponseModel> call = apiInterface.voucherResponse(token,dealer,md,bank,amount,extype,strDate);
            //need to update the expense type
        call.enqueue(new Callback<VoucherResponseModel>() {
            @Override
            public void onResponse(Call<VoucherResponseModel> call, Response<VoucherResponseModel> response) {
                if (response.isSuccessful()){
                    if (response.body().getFlag()){
                        progressdialog.dismiss();
                        Toast.makeText(VoucherActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        progressdialog.dismiss();
                        Toast.makeText(VoucherActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progressdialog.dismiss();
                    Toast.makeText(VoucherActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VoucherResponseModel> call, Throwable t) {
                progressdialog.dismiss();
                Toast.makeText(VoucherActivity.this, "Connectivity error, Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchBanks() {
        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getRetrofit().create(APIInterface.class);
       Call<BankModel> call = apiInterface.bankDetails(token);
       call.enqueue(new Callback<BankModel>() {
           @Override
           public void onResponse(Call<BankModel> call, Response<BankModel> response) {
               if (response.isSuccessful()){
                   Toast.makeText(VoucherActivity.this, "Success", Toast.LENGTH_SHORT).show();
                  // mylist = response.body().getBankAccounts();
                   List<BankModel.BankAccount> listInvoice;                   //Changes for Search Filter
                   listInvoice = response.body().getBankAccounts();
                   mylist = new ArrayList<>(listInvoice);
                   System.out.print("list -- " + mylist);
               }
           }

           @Override
           public void onFailure(Call<BankModel> call, Throwable t) {
               Toast.makeText(VoucherActivity.this, "Connection Error, Please try again!", Toast.LENGTH_SHORT).show();
           }
       });

    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

        if (i == 0){
            dealerId = "1";
            Toast.makeText(this, firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }else if (i==1){
            dealerId = "30";
            Toast.makeText(this, firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }else if (i == 2){
            dealerId = "31";
            Toast.makeText(this, firm[i]+" Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void submitVoucher(View view) {
        voucherRequest();
    }

    public void selectExpensetype(View view) {

    }
}