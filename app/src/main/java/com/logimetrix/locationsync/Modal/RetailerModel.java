package com.logimetrix.locationsync.Modal;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetailerModel {

    @SerializedName("flag")
    @Expose
    private Boolean flag;
    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getTokenExpired() {
        return isTokenExpired;
    }

    public void setTokenExpired(Boolean tokenExpired) {
        isTokenExpired = tokenExpired;
    }

    @SerializedName("is_token_expired")
    @Expose
    private Boolean isTokenExpired;
    @SerializedName("retailers")
    @Expose
    private List<Retailer> retailers = null;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public List<Retailer> getRetailers() {
        return retailers;
    }

    public void setRetailers(List<Retailer> retailers) {
        this.retailers = retailers;
    }

    public class Retailer {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("hindi_name")
        @Expose
        private String hindiName;
        @SerializedName("mobile_number")
        @Expose
        private Object mobileNumber;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("hindi_address")
        @Expose
        private String hindiAddress;
        @SerializedName("gst_number")
        @Expose
        private Object gstNumber;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHindiName() {
            return hindiName;
        }

        public void setHindiName(String hindiName) {
            this.hindiName = hindiName;
        }

        public Object getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(Object mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getHindiAddress() {
            return hindiAddress;
        }

        public void setHindiAddress(String hindiAddress) {
            this.hindiAddress = hindiAddress;
        }

        public Object getGstNumber() {
            return gstNumber;
        }

        public void setGstNumber(Object gstNumber) {
            this.gstNumber = gstNumber;
        }

        public Integer getIsActive() {
            return isActive;
        }

        public void setIsActive(Integer isActive) {
            this.isActive = isActive;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }


}

