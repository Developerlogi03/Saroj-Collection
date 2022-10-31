package com.logimetrix.locationsync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.Modal.RetailerCustomModel;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.Modal.WeekModel;
import com.logimetrix.locationsync.adapter.GroupAdapter;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    public GroupAdapter adapter;
    ProgressBar progressBar;
    String routId;
    String groupId, groupName;
    TextView txtdebit,txtCredit;
    String color = "";
    private List<GroupModel> groupList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        routId = getIntent().getStringExtra("routeId");


        recyclerView = findViewById(R.id.rcyclRtlrLst);
        progressBar = findViewById(R.id.progressRecycler);
        ImageView back = findViewById(R.id.back);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        fetchGroups();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void fetchGroups() {
        progressBar.setVisibility(View.VISIBLE);

        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<String> call = apiInterface.groupList(token,routId);
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
                                    boolean iscolor = false;

                                   int diff = dataobj.optInt("total_credit") - dataobj.optInt("total_debit");
                                    String value = String.valueOf(diff).trim();
                                    String finVal = value.replace("-","");

                                    groupId = dataobj.getString("group_id");
                                    groupName = dataobj.getString("group_name");
                                    if (!dataobj.getString("color").isEmpty()){
                                        color = dataobj.getString("color");
                                        iscolor = true;
                                    }else
                                        color = "#186FA0";
                                    GroupModel groupModel = new GroupModel();
                                        groupModel.setGroupId(groupId);
                                        groupModel.setGroupColor(color);
                                        groupModel.setGroupName(groupName);
                                        groupModel.setColor(iscolor);
                                        groupModel.setAmount("₹ "+finVal);
                                        groupList.add(groupModel);

                                        adapter = new GroupAdapter(groupList, getApplicationContext(), new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(RetailerModel.Retailer retailer, int position) {

                                            }

                                            @Override
                                            public void onWeekClick(WeekModel weekModel, int position) {

                                            }

                                            @Override
                                            public void onGroupClick(GroupModel groupModel, int position) {
                                                Intent intent = new Intent(GroupActivity.this, RetailerListActivity.class);
                                                intent.putExtra("groupId",groupModel.getGroupId());
                                                if (groupModel.isColor()){
                                                    intent.putExtra("color", groupModel.getGroupColor());
                                                }
                                                else
                                                    intent.putExtra("color",color);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onRetailerClick(RetailerCustomModel retailerCustomModel, int position) {

                                            }
                                        });
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                }

                                //

                            }else if (jsonObject.optString("flag").equals("false")){

                                if (jsonObject.optString("is_token_expired").equals("true")){
                                    new SweetAlertDialog(GroupActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                Toast.makeText(GroupActivity.this,jsonObject.optString("message").toString() , Toast.LENGTH_SHORT).show();

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

        Intent i = new Intent(GroupActivity.this,MainActivity.class);
        startActivity(i);
        finish();

    }
}