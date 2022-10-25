package com.logimetrix.locationsync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.logimetrix.locationsync.Modal.WeekModel;
import com.logimetrix.locationsync.R;
import com.logimetrix.locationsync.utils.CustomItemClickListener;

import java.util.List;

public class WeekListAdapter extends RecyclerView.Adapter<WeekListAdapter.MyViewHolder> {

    private List<WeekModel> weekModelList;
    private Context context;
    private CustomItemClickListener customItemClickListener;

    public WeekListAdapter(List<WeekModel> weekModelList, Context context, CustomItemClickListener customItemClickListener) {
        this.weekModelList = weekModelList;
        this.context = context;
        this.customItemClickListener = customItemClickListener;
    }

    @NonNull
    @Override
    public WeekListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_main, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClickListener.onWeekClick(weekModelList.get(myViewHolder.getAdapterPosition()),myViewHolder.getAdapterPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeekListAdapter.MyViewHolder holder, int position) {
        WeekModel cc = weekModelList.get(position);

        holder.dayName.setText(cc.getName());
        holder.hindiNAme.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return weekModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dayName, hindiNAme;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayName = itemView.findViewById(R.id.tvNameEng);
            hindiNAme = itemView.findViewById(R.id.tvNameHindi);
        }
    }
}
