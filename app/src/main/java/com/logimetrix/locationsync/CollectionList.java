package com.logimetrix.locationsync;

import static org.greenrobot.eventbus.EventBus.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.logimetrix.locationsync.Modal.ExpenseVoucherListModel;
import com.logimetrix.locationsync.adapter.CollectionListAdapter;
import com.logimetrix.locationsync.adapter.VoucherApprovalAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionList extends AppCompatActivity {

    private RecyclerView recyclerView;
    ProgressBar progressBar;
    private LinearLayoutManager linearLayoutManager;
    public CollectionListAdapter collectionListAdapter;
    ImageView imgBack;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String api_token;
    int role_id;
    public ArrayList<ExpenseVoucherListModel.Expense> expenseArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_list);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        role_id = Integer.parseInt(pref.getString("role_id",null));
        System.out.println("access token is ::"+pref.getString("api_token",null));
        api_token=pref.getString("api_token",null);

        recyclerView = findViewById(R.id.rcyclRtlrLst);
        progressBar = findViewById(R.id.progressRecycler);
        imgBack = findViewById(R.id.back);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        fetchCollectionList(api_token);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void fetchCollectionList(String api_token) {

        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ExpenseVoucherListModel> call = apiInterface.expenseVoucherList(api_token);
        call.enqueue(new Callback<ExpenseVoucherListModel>() {
            @Override
            public void onResponse(Call<ExpenseVoucherListModel> call, Response<ExpenseVoucherListModel> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    if (response.body().getFlag()){
                        List<ExpenseVoucherListModel.Expense> expenseList;
                        expenseList = response.body().getExpenses();
                        expenseArrayList = new ArrayList<>(expenseList);

                        collectionListAdapter = new CollectionListAdapter(getApplicationContext(),expenseArrayList);
                        recyclerView.setAdapter(collectionListAdapter);

                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "onResponse: "+response.message());
                }
            }

            @Override
            public void onFailure(Call<ExpenseVoucherListModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CollectionList.this, "Connection Error! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}