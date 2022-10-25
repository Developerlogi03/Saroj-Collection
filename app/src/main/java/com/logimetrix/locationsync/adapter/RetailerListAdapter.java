package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RetailerListAdapter extends RecyclerView.Adapter<RetailerListAdapter.RetailerViewholder> implements Filterable {
    private List<RetailerModel.Retailer> retailerList;
    private List<RetailerModel.Retailer> exampleListFull;
    private List<RetailerModel.Retailer> movieListFiltered;
    private Context context;
    private CustomItemClickListener customItemClickListener;

    public RetailerListAdapter(List<RetailerModel.Retailer> retailerList, Context context, CustomItemClickListener customItemClickListener) {
        this.retailerList = retailerList;
        this.exampleListFull = retailerList;
        this.context = context;
        this.customItemClickListener = customItemClickListener;
    }


    public void setMovieList(Context context,final List<RetailerModel.Retailer> retailerList){
        this.context = context;
        if(this.retailerList == null){
            this.retailerList = retailerList;
            this.movieListFiltered = retailerList;
            notifyItemChanged(0, movieListFiltered.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return RetailerListAdapter.this.retailerList.size();
                }

                @Override
                public int getNewListSize() {
                    return retailerList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return RetailerListAdapter.this.retailerList.get(oldItemPosition).getName() == retailerList.get(newItemPosition).getName();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    RetailerModel.Retailer newMovie = RetailerListAdapter.this.retailerList.get(oldItemPosition);

                    RetailerModel.Retailer oldMovie = retailerList.get(newItemPosition);

                    return newMovie.getName() == oldMovie.getName() ;
                }
            });
            this.retailerList = retailerList;
            this.movieListFiltered = retailerList;
            result.dispatchUpdatesTo(this);
        }
    }



    @NonNull
    @Override
    public RetailerListAdapter.RetailerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_main,parent,false);
        final RetailerViewholder viewholder = new RetailerViewholder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClickListener.onItemClick(retailerList.get(viewholder.getAdapterPosition()),viewholder.getAdapterPosition());
            }
        });
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RetailerListAdapter.RetailerViewholder holder, int position) {
        holder.tvNameHindi.setText(retailerList.get(position).getAddress());
        holder.tvNameEng.setText(retailerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (retailerList != null)
            return retailerList.size();
        else
            return 0;
    }



    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            List<RetailerModel.Retailer> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(retailerList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (RetailerModel.Retailer item : retailerList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

                exampleListFull.clear();
                exampleListFull.addAll((List) results.values);
                notifyDataSetChanged();

        }
    };

    @Override
    public Filter getFilter() {
        return exampleFilter;
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
