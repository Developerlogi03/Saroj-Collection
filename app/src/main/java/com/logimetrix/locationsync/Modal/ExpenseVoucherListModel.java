package com.logimetrix.locationsync.Modal;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ExpenseVoucherListModel {

    @SerializedName("flag")
    @Expose
    private Boolean flag;
    @SerializedName("expenses")
    @Expose
    private List<Expense> expenses = null;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }


    public class Expense {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("dealer_id")
        @Expose
        private Integer dealerId;
        @SerializedName("bank_id")
        @Expose
        private Integer bankId;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("reference_number")
        @Expose
        private Object referenceNumber;
        @SerializedName("transaction_date")
        @Expose
        private Object transactionDate;
        @SerializedName("generate_user_id")
        @Expose
        private Integer generateUserId;
        @SerializedName("approve_date")
        @Expose
        private Object approveDate;
        @SerializedName("approve_user_id")
        @Expose
        private Object approveUserId;
        @SerializedName("approve_status")
        @Expose
        private Object approveStatus;
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

        public Integer getDealerId() {
            return dealerId;
        }

        public void setDealerId(Integer dealerId) {
            this.dealerId = dealerId;
        }

        public Integer getBankId() {
            return bankId;
        }

        public void setBankId(Integer bankId) {
            this.bankId = bankId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Object getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(Object referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public Object getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(Object transactionDate) {
            this.transactionDate = transactionDate;
        }

        public Integer getGenerateUserId() {
            return generateUserId;
        }

        public void setGenerateUserId(Integer generateUserId) {
            this.generateUserId = generateUserId;
        }

        public Object getApproveDate() {
            return approveDate;
        }

        public void setApproveDate(Object approveDate) {
            this.approveDate = approveDate;
        }

        public Object getApproveUserId() {
            return approveUserId;
        }

        public void setApproveUserId(Object approveUserId) {
            this.approveUserId = approveUserId;
        }

        public Object getApproveStatus() {
            return approveStatus;
        }

        public void setApproveStatus(Object approveStatus) {
            this.approveStatus = approveStatus;
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

