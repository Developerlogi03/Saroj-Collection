package com.logimetrix.locationsync.adapter;

import static android.content.Context.MODE_PRIVATE;
import static org.greenrobot.eventbus.EventBus.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.logimetrix.locationsync.APIClient;
import com.logimetrix.locationsync.APIInterface;
import com.logimetrix.locationsync.GroupActivity;
import com.logimetrix.locationsync.MainActivity;
import com.logimetrix.locationsync.Modal.ExpenseVoucherListModel;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.VoucherApproval;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherApprovalAdapter extends RecyclerView.Adapter<VoucherApprovalAdapter.VoucherViewHolder> {
    public Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String voucher_id,status;
    public List<ExpenseVoucherListModel.Expense> expensesList;

    public VoucherApprovalAdapter(Context context, List<ExpenseVoucherListModel.Expense> expensesList) {
        this.context = context;
        this.expensesList = expensesList;
    }

    @NonNull
    @Override
    public VoucherApprovalAdapter.VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approval,parent,false);
        final VoucherViewHolder viewHolder = new VoucherViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           }
        });

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull VoucherApprovalAdapter.VoucherViewHolder holder, int position) {
        holder.tvName.setText(expensesList.get(position).getDealerId().toString());
        holder.tvAmount.setText("₹"+expensesList.get(position).getAmount().toString());
        holder.tvType.setText(expensesList.get(position).getMode().toString().toUpperCase());
        status = expensesList.get(position).getApproveStatus();
        voucher_id =expensesList.get(position).getId().toString();
        Log.d("s1",status);

        if (status.equals("0")){
            holder.linearAction.setVisibility(View.VISIBLE);
        }else if (status.equals("1")){
            holder.linearAction.setVisibility(View.GONE);
            holder.tvStatus.setText("Approved");
            Log.d("s2",status);

        }
        else if (status.equals("2")){
            holder.linearAction.setVisibility(View.GONE);
            holder.tvStatus.setText("Amount Dispersed");
            Log.d("s2",status);

        }

    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvType, tvStatus;
        MaterialCardView container;
        LinearLayout linearAction;
        Button btnApprove, btnReject;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvamount);
            tvType = itemView.findViewById(R.id.tvCashCheque);
            container = itemView.findViewById(R.id.container);
            linearAction = itemView.findViewById(R.id.linearCheckCross);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnDecline);
            tvStatus = itemView.findViewById(R.id.tvStatus);


            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Approve");
                    Toast.makeText(context.getApplicationContext(), "Approved", Toast.LENGTH_SHORT).show();
                    ApiStatus("1");

                }
            });
            
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Reject");
                    ApiStatus("2");
                    Toast.makeText(context.getApplicationContext(), "Rejected", Toast.LENGTH_SHORT).show();

                }
            });

        }
        public  void  ApiStatus(String statuss){

            pref = context.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            String token = pref.getString("api_token",null);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<String> call = apiInterface.updateExpenseVoucher(token,statuss,voucher_id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        Log.d("staus",response.body().toString());
                        if (!response.body().isEmpty()){
                            try {
                                JSONObject jsonObject= new JSONObject(response.body().toString());
                                if (jsonObject.optString("flag").equals("true")){
                                    Toast.makeText(context.getApplicationContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                                else if (jsonObject.optString("flag").equals("false")) {

                                    if (jsonObject.optString("is_token_expired").equals("true")) {
                                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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

            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);

        }
    }
}
