package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.logimetrix.locationsync.Modal.RetailerCustomModel;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RetailerListCustomAdapter extends RecyclerView.Adapter<RetailerListCustomAdapter.MyViewHolder> implements Filterable {

    private List<RetailerCustomModel> retailerCustomModelList;
    private List<RetailerCustomModel> cbackup;
    private Context context;
    private CustomItemClickListener customItemClickListener;
    public String color;


    public RetailerListCustomAdapter(List<RetailerCustomModel> retailerCustomModelList, Context context, CustomItemClickListener customItemClickListener, String color) {
        this.retailerCustomModelList = retailerCustomModelList;
        this.context = context;
        cbackup = new ArrayList<>(retailerCustomModelList);
        this.customItemClickListener = customItemClickListener;
        this.color = color;
    }

    @NonNull
    @Override
    public RetailerListCustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_main, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onRetailerClick(retailerCustomModelList.get(myViewHolder.getAdapterPosition()),myViewHolder.getAdapterPosition());
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RetailerListCustomAdapter.MyViewHolder holder, int position) {
        RetailerCustomModel cc = retailerCustomModelList.get(position);
        holder.retailerName.setText(cc.getRetailer_name());
        holder.hindi.setVisibility(View.GONE);
        holder.cardView.setCardBackgroundColor(Color.parseColor(color));

    }

    @Override
    public int getItemCount() {
        return retailerCustomModelList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RetailerCustomModel> filteredData = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredData.addAll(cbackup);

            }
            else {
                for (RetailerCustomModel obj:cbackup)
                {
                    if (obj.getRetailer_name().toString().toLowerCase().contains(constraint.toString().toLowerCase(Locale.ROOT)))filteredData.add(obj);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredData;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            retailerCustomModelList.clear();
            retailerCustomModelList.addAll((ArrayList<RetailerCustomModel>)results.values);
            notifyDataSetChanged();

        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView retailerName,hindi;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            retailerName = itemView.findViewById(R.id.tvNameEng);
            cardView = itemView.findViewById(R.id.cardviewRetailers);
            hindi = itemView.findViewById(R.id.tvNameHindi);
        }
    }
}
