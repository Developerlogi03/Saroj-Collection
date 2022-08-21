package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.R;

import java.util.ArrayList;
import java.util.List;

public class RetailerListAdapter extends RecyclerView.Adapter<RetailerListAdapter.RetailerViewholder> implements Filterable {
    private List<RetailerModel.Retailer> retailerList;
    private List<RetailerModel.Retailer> movieListFiltered;
    private Context context;

    public RetailerListAdapter(final List<RetailerModel.Retailer> retailerList, Context context) {
        this.retailerList = retailerList;
        this.context = context;
    }

    @NonNull
    @Override
    public RetailerListAdapter.RetailerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_main,parent,false);
        return new RetailerViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetailerListAdapter.RetailerViewholder holder, int position) {
        holder.tvNameHindi.setText(retailerList.get(position).getHindiName());
        holder.tvNameEng.setText(retailerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (retailerList != null)
            return retailerList.size();
        else
            return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    movieListFiltered = retailerList;
                } else {
                    List<RetailerModel.Retailer> filteredList = new ArrayList<>();
                    for (RetailerModel.Retailer movie : retailerList) {
                        if (movie.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(movie);
                        }
                    }
                    movieListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = movieListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieListFiltered = (ArrayList<RetailerModel.Retailer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class RetailerViewholder extends RecyclerView.ViewHolder {
        MaterialCardView container;
        TextView tvNameHindi, tvNameEng;
        public RetailerViewholder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            tvNameEng = itemView.findViewById(R.id.tvNameEng);
            tvNameHindi = itemView.findViewById(R.id.tvNameHindi);
        }
    }
}
