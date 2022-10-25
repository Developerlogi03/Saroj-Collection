package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.logimetrix.locationsync.Modal.LedgerModel;
import com.logimetrix.locationsync.R;

import java.util.List;

public class FragmentOneAdapter extends RecyclerView.Adapter<FragmentOneAdapter.MyViewHolder> {
    private List<LedgerModel> ledgerModelList;
    private Context context;
  //  public String color;


    public FragmentOneAdapter(List<LedgerModel> ledgerModelList, Context context) {
        this.ledgerModelList = ledgerModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public FragmentOneAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_total_balance_layout, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentOneAdapter.MyViewHolder holder, int position) {
        LedgerModel cc  = ledgerModelList.get(position);
        holder.date.setText(cc.getTransaction_date());
        holder.credit.setText("₹ "+cc.getCredit());
        holder.debit.setText("₹ "+cc.getDebit());
        holder.particular.setText(cc.getParticular());
        if (cc.getDebit().equals("0")){
            holder.total_balance_lay.setBackgroundColor(Color.parseColor("#cce5cc"));
        }else if (cc.getCredit().equals("0")){
            holder.total_balance_lay.setBackgroundColor(Color.parseColor("#ffcccc"));//
        }

    }

    @Override
    public int getItemCount() {
        return ledgerModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date,particular,debit,credit;
        LinearLayout total_balance_lay;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            particular = itemView.findViewById(R.id.particular);
            debit = itemView.findViewById(R.id.debit);
            credit = itemView.findViewById(R.id.credit);
            total_balance_lay = itemView.findViewById(R.id.total_balance_lay);
        }
    }
}
