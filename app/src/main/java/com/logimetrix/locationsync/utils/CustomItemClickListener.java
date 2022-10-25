package com.logimetrix.locationsync.utils;


import com.logimetrix.locationsync.Modal.GroupModel;
import com.logimetrix.locationsync.Modal.RetailerCustomModel;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.Modal.WeekModel;

public interface CustomItemClickListener {
    public void onItemClick(RetailerModel.Retailer retailer, int position);
    public void onWeekClick(WeekModel weekModel, int position);
    public void onGroupClick(GroupModel groupModel, int position);
    public void onRetailerClick(RetailerCustomModel retailerCustomModel, int position);
}
