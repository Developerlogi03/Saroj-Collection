package com.logimetrix.locationsync.Modal;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ClosingBalance {

    @SerializedName("total_credit")
    @Expose
    private Integer totalCredit;
    @SerializedName("total_debit")
    @Expose
    private Integer totalDebit;
    @SerializedName("closing_balance")
    @Expose
    private Integer closingBalance;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Integer totalCredit) {
        this.totalCredit = totalCredit;
    }

    public Integer getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(Integer totalDebit) {
        this.totalDebit = totalDebit;
    }

    public Integer getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Integer closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

