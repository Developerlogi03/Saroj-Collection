package com.logimetrix.locationsync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.Modal.InvoiceModel;
import com.logimetrix.locationsync.adapter.GroupAdapter;
import com.logimetrix.locationsync.adapter.InvoiceAdpter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LinearLayoutManager linearLayoutManager;
    public InvoiceAdpter adapter;
    TextView invoice_date,invoice_number,total,remaing_Amount;

    String retailer_id;
    private List<InvoiceModel> invoicelist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        recyclerView=findViewById(R.id.invoicerecycler_table);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        retailer_id = getIntent().getStringExtra("retail_id");
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ApiInvoicedata();

    }
    public void ApiInvoicedata(){
        String token = pref.getString("api_token",null);
      //  Toast.makeText(this, retailer_id, Toast.LENGTH_LONG).show();
        APIInterface apiInterface =APIClient.getClient().create(APIInterface.class);
        Call<String> call =apiInterface.retailerInvoicelist(token,retailer_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    if (!response.body().isEmpty()){
                        String jsonResposne =response.body().toString();
                        Log.d("Response", jsonResposne);
                        JSONObject jsonObject =null;
                        try {
                            jsonObject =new JSONObject(jsonResposne);

                            if (jsonObject.optString("flag").equals("true")){

                                JSONArray jsonArray=jsonObject.getJSONArray("invoices");
                                for (int i =0; i< jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    InvoiceModel invoiceModel =new InvoiceModel();

                                    invoiceModel.setInvoice_number(object.optString("invoice_number"));
                                    invoiceModel.setInvoice_date(object.optString("invoice_date"));
                                    invoiceModel.setTotal(object.optString("total"));
                                    invoiceModel.setRemaining_amount(object.optString("remaining_amount"));
                                    invoicelist.add(invoiceModel);

                                    adapter = new InvoiceAdpter(invoicelist,InvoiceActivity.this);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(InvoiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}