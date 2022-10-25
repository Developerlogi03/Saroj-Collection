package com.logimetrix.locationsync.adapter;

import static org.greenrobot.eventbus.EventBus.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.logimetrix.locationsync.Modal.ExpenseVoucherListModel;
import com.logimetrix.locationsync.R;

import java.util.List;

public class VoucherApprovalAdapter extends RecyclerView.Adapter<VoucherApprovalAdapter.VoucherViewHolder> {
    public Context context;
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
        if (expensesList.get(position).getApproveStatus() == null || expensesList.get(position).getApproveStatus()== "0"){
            holder.linearAction.setVisibility(View.VISIBLE);
        }else if (expensesList.get(position).getApproveStatus().toString().equals("1")){
            holder.tvStatus.setText("Approved");
            Log.d(TAG, "onBindViewHolder: "+expensesList.get(position).getApproveStatus().toString());

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
                }
            });
            
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Reject");
                }
            });

        }
    }
}
