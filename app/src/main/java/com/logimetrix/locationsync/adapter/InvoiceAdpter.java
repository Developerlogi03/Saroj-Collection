package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.logimetrix.locationsync.Modal.InvoiceModel;
import com.logimetrix.locationsync.R;

import java.util.ArrayList;
import java.util.List;

public class InvoiceAdpter extends RecyclerView.Adapter<InvoiceAdpter.InvoiceViewholder>{
    List<InvoiceModel> invoiceModelList ;
    Context icontext;

    public InvoiceAdpter(List<InvoiceModel> invoiceModelList, Context icontext) {
        this.invoiceModelList = invoiceModelList;
        this.icontext = icontext;
    }

    @NonNull
    @Override
    public InvoiceAdpter.InvoiceViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View iview = LayoutInflater.from(icontext).inflate(R.layout.item_total_balance_layout,parent,false);

        return new InvoiceViewholder(iview);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdpter.InvoiceViewholder holder, int position) {
        holder.date.setText(invoiceModelList.get(position).getInvoice_date());
        holder.quantity.setText(invoiceModelList.get(position).getInvoice_number());
        holder.total.setText(invoiceModelList.get(position).getTotal());
        holder.remaning.setText(invoiceModelList.get(position).getRemaining_amount());

    }

    @Override
    public int getItemCount() {
        if (invoiceModelList != null)
            return invoiceModelList.size();
        else
            return 0;
    }

    public class InvoiceViewholder extends RecyclerView.ViewHolder {
        TextView date,quantity,total,remaning;
        public InvoiceViewholder(@NonNull View itemView) {
            super(itemView);
            date =itemView.findViewById(R.id.date);
            quantity =itemView.findViewById(R.id.particular);
            total =itemView.findViewById(R.id.debit);
            remaning =itemView.findViewById(R.id.credit);
        }
    }
}
