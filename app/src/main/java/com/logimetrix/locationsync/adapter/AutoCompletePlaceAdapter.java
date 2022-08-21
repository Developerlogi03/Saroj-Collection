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

import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompletePlaceAdapter extends ArrayAdapter<RetailerModel.Retailer> {
    private List<RetailerModel.Retailer> allPlacesList;
    private List<RetailerModel.Retailer> filteredPlacesList;

    public AutoCompletePlaceAdapter(@NonNull Context context, @NonNull List<RetailerModel.Retailer> placesList) {
        super(context, 0, placesList);

        allPlacesList = new ArrayList<>(placesList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return placeFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_simple, parent, false
            );
        }
        TextView placeLabel = convertView.findViewById(R.id.autocomplete_item_place_label);

        RetailerModel.Retailer place = getItem(position);
        if (place != null) {
            placeLabel.setText(place.getName());
        }

        return convertView;
    }

    private Filter placeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            filteredPlacesList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredPlacesList.addAll(allPlacesList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (RetailerModel.Retailer place: allPlacesList) {
                    if (place.getName().toLowerCase().contains(filterPattern)) {
                        filteredPlacesList.add(place);
                    }
                }
            }

            results.values = filteredPlacesList;
            results.count = filteredPlacesList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((RetailerModel.Retailer) resultValue).getName();
        }
    };
}
