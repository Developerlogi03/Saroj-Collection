package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.logimetrix.locationsync.Modal.DealerModal;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteDealerAdapter extends ArrayAdapter<DealerModal.Dealer> {
    private List<DealerModal.Dealer> allDealerList;
    private List<DealerModal.Dealer> filteredDealerList;

    public AutoCompleteDealerAdapter(@NonNull Context context,@NonNull List<DealerModal.Dealer> dealerList){
        super(context,0,dealerList);
        allDealerList = new ArrayList<>(dealerList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return dealerFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_simple,parent,false);
        }
        TextView placeDealer = convertView.findViewById(R.id.autocomplete_item_place_label);
        DealerModal.Dealer dealer = getItem(position);
        if (dealer != null){
            placeDealer.setText(dealer.getName());
        }

        return convertView;
    }

    private Filter dealerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            filteredDealerList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredDealerList.addAll(allDealerList);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (DealerModal.Dealer dealer: allDealerList) {
                    if (dealer.getName().toLowerCase().contains(filterPattern)) {
                        filteredDealerList.add(dealer);
                    }
                }
            }
            results.values =filteredDealerList;
            results.count  =filteredDealerList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((DealerModal.Dealer) resultValue).getName();
        }
    };

}
