package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder>{

    private List<GroupModel> groupModelList;
    private Context context;
    private CustomItemClickListener customItemClickListener;

    public GroupAdapter(List<GroupModel> groupModelList, Context context, CustomItemClickListener customItemClickListener) {
        this.groupModelList = groupModelList;
        this.context = context;
        this.customItemClickListener = customItemClickListener;
    }

    @NonNull
    @Override
    public GroupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_layout, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onGroupClick(groupModelList.get(myViewHolder.getAdapterPosition()),myViewHolder.getAdapterPosition());
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.MyViewHolder holder, int position) {
        GroupModel cc = groupModelList.get(position);
        holder.groupName.setText(cc.getGroupName());
        holder.amount.setText(cc.getAmount());

        if (cc.isColor()){
            holder.cardView.setCardBackgroundColor(Color.parseColor(cc.getGroupColor()));
        }

    }

    @Override
    public int getItemCount() {
        return groupModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, hindi,amount;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.tvNameGroup);
            cardView = itemView.findViewById(R.id.cardviewGroupRetailers);
            amount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
