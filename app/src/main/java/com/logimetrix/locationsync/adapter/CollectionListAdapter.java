package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.logimetrix.locationsync.Modal.ExpenseVoucherListModel;

import java.util.List;

public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.CollectionViewHolder> {
    public Context context;
    public List<ExpenseVoucherListModel.Expense> collectionList;

    public CollectionListAdapter(Context context, List<ExpenseVoucherListModel.Expense> collectionList) {
        this.context = context;
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public CollectionListAdapter.CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionListAdapter.CollectionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {
        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
