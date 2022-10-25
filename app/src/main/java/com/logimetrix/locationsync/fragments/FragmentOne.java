package com.logimetrix.locationsync.fragments;


import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.logimetrix.locationsync.APIClient;
import com.logimetrix.locationsync.APIInterface;
import com.logimetrix.locationsync.LedgerActivity;
import com.logimetrix.locationsync.MainActivity;
import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.Modal.LedgerModel;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.Modal.WeekModel;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.RetailerListActivity;
import com.logimetrix.locationsync.adapter.FragmentOneAdapter;
import com.logimetrix.locationsync.adapter.RetailerListCustomAdapter;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentOne extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences pref;
    String retailerId, fileUrl;
    FragmentOneAdapter adapter;
    private List<LedgerModel> ledgerModelList = new ArrayList<>();
    private Context context;

    TextView totalCredit, totalDebit, totalBal;
    Button btnLedger;

    // Progress Dialog
    private ProgressDialog dialog;
    public static final int progress_bar_type = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, viewGroup, false);
        pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        retailerId = getArguments().getString("retailerId");

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshTbr);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_200,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Downloading...");
        dialog.setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getContext();
        recycler = view.findViewById(R.id.recycler_table);
        totalCredit = view.findViewById(R.id.tvTotalCredit);
        totalDebit = view.findViewById(R.id.tvTotalDebit);
        totalBal = view.findViewById(R.id.tvTotalBal);
        btnLedger = view.findViewById(R.id.ledger);


        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recycler.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);
                // Fetching data from server
                fetchLedgerDetails();
            }
        });

        btnLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchFileUrl();
            }
        });

    }

    private void downloadFile(String url) {

        Uri uri = Uri.parse(url);
        DownloadManager.Request r = new DownloadManager.Request(uri);

        String file = url.substring( url.lastIndexOf('/')+ 1, url.length() );

        // This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file);
        r.allowScanningByMediaScanner();

        // Notify user when download is completed
        // (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Start download
        DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        long enq = dm.enqueue(r);
        Log.d("download:",uri.toString());

    }

    private void fetchLedgerDetails() {

        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        Call<String> call = apiInterface.retailerledgerlist(token,retailerId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    mSwipeRefreshLayout.setRefreshing(false);
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());

                        if (jsonObject.optString("flag").equals("true")){

                            JSONArray dataArray  = jsonObject.getJSONArray("total");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                totalCredit.setText("Credit : ₹ "+dataobj.optString("totalCredit"));
                                totalDebit.setText("Debit : ₹ "+ dataobj.optString("totalDebit"));
                                int diff = dataobj.optInt("totalCredit") - dataobj.optInt("totalDebit");
                                String value = String.valueOf(diff).trim();
                                String finVal = value.replace("-","");
                                if (diff > 0){
                                    totalBal.setTextColor(Color.parseColor("#329932"));
                                }else
                                    totalBal.setTextColor(Color.parseColor("#ff3232"));

                                totalBal.setText("₹ "+finVal);
                            }

                            JSONArray jsonArray = jsonObject.getJSONArray("ledger_detail");

                            for (int j =0; j<jsonArray.length(); j++){
                                JSONObject obj = jsonArray.getJSONObject(j);

                                LedgerModel ledgerModel = new LedgerModel();
                                ledgerModel.setCredit(obj.optString("credit"));

                                ledgerModel.setDebit(obj.optString("debit"));
                                ledgerModel.setParticular(obj.optString("particular"));
                                ledgerModel.setTransaction_date(obj.optString("transaction_date"));
                                ledgerModelList.add(ledgerModel);

                                adapter = new FragmentOneAdapter(ledgerModelList,getContext());
                                recycler.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }


                            //

                        }else if (jsonObject.optString("flag").equals("false")){

                            if (jsonObject.optString("is_token_expired").equals("true")){
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
                            Toast.makeText(getActivity(),jsonObject.optString("message").toString() , Toast.LENGTH_SHORT).show();


                            // Toast.makeText(FarmerBasicKYC.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchFileUrl(){

        dialog.show();
        String token = pref.getString("api_token",null);
        APIInterface apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        Call<String> call = apiInterface.ledgerfileUrl(token,retailerId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.optString("flag").equals("true")){
                            if (!jsonObject.optString("excel_path").equals("")){
                                fileUrl = jsonObject.optString("excel_path");
                                dialog.dismiss();
                                downloadFile(fileUrl);

                            }else {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Error fetching file.", Toast.LENGTH_SHORT).show();
                            }

                        }else if (jsonObject.optString("flag").equals("false")){

                            if (jsonObject.optString("is_token_expired").equals("true")){
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
                            Toast.makeText(getActivity(),jsonObject.optString("message").toString() , Toast.LENGTH_SHORT).show();


                            // Toast.makeText(FarmerBasicKYC.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
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

        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();

    }

    @Override
    public void onRefresh() {
        fetchLedgerDetails();
    }
}
