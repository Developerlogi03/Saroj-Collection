package com.logimetrix.locationsync;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Retailersadapter extends RecyclerView.Adapter<Retailersadapter.HolderAIReport>{
    ArrayList<Retailerpojo> aitRecords;
    Context context;
    public Retailersadapter(Context context, ArrayList<Retailerpojo> aiTpojo) {
        aitRecords = aiTpojo;
        this.context = context;
    }
    @NonNull
    @Override
    public Retailersadapter.HolderAIReport onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.retailers, viewGroup, false);
        return (new HolderAIReport(view));
    }
    @Override
    public void onBindViewHolder(@NonNull Retailersadapter.HolderAIReport holderAIReport, int i) {
        try {
            final Retailerpojo rj=aitRecords.get(i);
          //  System.out.println("response is :: "+rj.getName());
          //  String no = String.valueOf(i + 1);
          //  System.out.println("id is ::"+rj.getName());
            holderAIReport.retailer_name.setText(rj.getName());
            holderAIReport.retailer_nmobile.setText(rj.getMobile_number());
            holderAIReport.retailer_address.setText(rj.getAddress());
            holderAIReport.list_no.setText("#"+rj.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return aitRecords.size();

    }
    public class HolderAIReport extends RecyclerView.ViewHolder {
        TextView retailer_name,retailer_nmobile,retailer_address,list_no;
        ImageView ait_sublist_iv_edit;
        public HolderAIReport(@NonNull View itemView) {
            super(itemView);
            retailer_name = itemView.findViewById(R.id.retailer_name);
            retailer_nmobile = itemView.findViewById(R.id.retailer_nmobile);
            retailer_address = itemView.findViewById(R.id.retailer_address);
            list_no = itemView.findViewById(R.id.list_no);
            ait_sublist_iv_edit = itemView.findViewById(R.id.ait_sublist_iv_edit);
        }
    }
}
