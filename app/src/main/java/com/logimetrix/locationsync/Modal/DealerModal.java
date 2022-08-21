package com.logimetrix.locationsync.Modal;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DealerModal {

    @SerializedName("flag")
    @Expose
    private Boolean flag;
    @SerializedName("dealers")
    @Expose
    private List<Dealer> dealers = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public List<Dealer> getDealers() {
        return dealers;
    }

    public void setDealers(List<Dealer> dealers) {
        this.dealers = dealers;
    }

    public class Dealer {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("unique_id")
        @Expose
        private String uniqueId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("hindi_name")
        @Expose
        private String hindiName;
        @SerializedName("phone")
        @Expose
        private Object phone;
        @SerializedName("address1")
        @Expose
        private String address1;
        @SerializedName("hindi_address1")
        @Expose
        private String hindiAddress1;
        @SerializedName("address2")
        @Expose
        private Object address2;
        @SerializedName("hindi_address2")
        @Expose
        private Object hindiAddress2;
        @SerializedName("pin_code")
        @Expose
        private String pinCode;
        @SerializedName("owner_name")
        @Expose
        private Object ownerName;
        @SerializedName("district")
        @Expose
        private String district;
        @SerializedName("mobile_number")
        @Expose
        private String mobileNumber;
        @SerializedName("email")
        @Expose
        private Object email;
        @SerializedName("ifms_code")
        @Expose
        private Object ifmsCode;
        @SerializedName("gst_number")
        @Expose
        private String gstNumber;
        @SerializedName("show_separate_report")
        @Expose
        private Integer showSeparateReport;
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

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
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

        public Object getPhone() {
            return phone;
        }

        public void setPhone(Object phone) {
            this.phone = phone;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getHindiAddress1() {
            return hindiAddress1;
        }

        public void setHindiAddress1(String hindiAddress1) {
            this.hindiAddress1 = hindiAddress1;
        }

        public Object getAddress2() {
            return address2;
        }

        public void setAddress2(Object address2) {
            this.address2 = address2;
        }

        public Object getHindiAddress2() {
            return hindiAddress2;
        }

        public void setHindiAddress2(Object hindiAddress2) {
            this.hindiAddress2 = hindiAddress2;
        }

        public String getPinCode() {
            return pinCode;
        }

        public void setPinCode(String pinCode) {
            this.pinCode = pinCode;
        }

        public Object getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(Object ownerName) {
            this.ownerName = ownerName;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public Object getEmail() {
            return email;
        }

        public void setEmail(Object email) {
            this.email = email;
        }

        public Object getIfmsCode() {
            return ifmsCode;
        }

        public void setIfmsCode(Object ifmsCode) {
            this.ifmsCode = ifmsCode;
        }

        public String getGstNumber() {
            return gstNumber;
        }

        public void setGstNumber(String gstNumber) {
            this.gstNumber = gstNumber;
        }

        public Integer getShowSeparateReport() {
            return showSeparateReport;
        }

        public void setShowSeparateReport(Integer showSeparateReport) {
            this.showSeparateReport = showSeparateReport;
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

