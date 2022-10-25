package com.logimetrix.locationsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.Modal.RetailerCustomModel;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.Modal.WeekModel;
import com.logimetrix.locationsync.adapter.RetailerListCustomAdapter;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerListActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    SearchView searchView;
    FloatingActionButton floatbtn;
    LinearLayoutManager linearLayoutManager;

    public RetailerListCustomAdapter adapter;
    ProgressBar progressBar;
    private List<RetailerCustomModel> retailerCustomModelList = new ArrayList<>();


    String color, groupId, retailerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_list);
        
        color = getIntent().getStringExtra("color");

        groupId = getIntent().getStringExtra("groupId");

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


        recyclerView = findViewById(R.id.rcyclRtlrLst);
        progressBar = findViewById(R.id.progressRecycler);
        ImageView back = findViewById(R.id.back);
        searchView = findViewById(R.id.searchView);
        floatbtn=findViewById(R.id.floatbtn);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        fetchRetailers();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0){
                    floatbtn.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.GONE);

                }
                else
                {
                    floatbtn.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                }
            }
        });
         floatbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 recyclerView.scrollToPosition(0);

             }
         });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        
    }

    private void fetchRetailers() {
        progressBar.setVisibility(View.VISIBLE);

        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<String> call = apiInterface.groupWiseRetailerList(token,groupId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);

                    if (!response.body().isEmpty()){
                        String jsonResponse = response.body().toString();
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(jsonResponse);

                            if (jsonObject.optString("flag").equals("true")){

                                JSONArray dataArray  = jsonObject.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    RetailerCustomModel retailerCustomModel = new RetailerCustomModel();
                                    retailerCustomModel.setRetailer_name(dataobj.getString("retailer_name").toString());
                                    retailerCustomModel.setId(dataobj.getString("id").toString());
                                    retailerCustomModelList.add(retailerCustomModel);

                                    adapter = new RetailerListCustomAdapter(retailerCustomModelList, getApplicationContext(), new CustomItemClickListener() {
                                        @Override
                                        public void onItemClick(RetailerModel.Retailer retailer, int position) {

                                        }

                                        @Override
                                        public void onWeekClick(WeekModel weekModel, int position) {

                                        }

                                        @Override
                                        public void onGroupClick(GroupModel groupModel, int position) {

                                        }

                                        @Override
                                        public void onRetailerClick(RetailerCustomModel retailerCustomModel, int position) {
                                            Intent intent = new Intent(RetailerListActivity.this, LedgerActivity.class);
                                            intent.putExtra("routeId",retailerCustomModel.getId());
                                            //   intent.putExtra("color", color);
                                            startActivity(intent);
                                        }
                                    },color);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                }

                                //

                            }else if (jsonObject.optString("flag").equals("false")){

                                if (jsonObject.optString("is_token_expired").equals("true")){
                                    new SweetAlertDialog(RetailerListActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                Toast.makeText(RetailerListActivity.this,jsonObject.optString("message").toString() , Toast.LENGTH_SHORT).show();

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

    private void logout() {

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        Intent i = new Intent(RetailerListActivity.this,MainActivity.class);
        startActivity(i);
        finish();

    }
}