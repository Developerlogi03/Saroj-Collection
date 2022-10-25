package com.logimetrix.locationsync.Modal;

public class LedgerModel {
    String retailer_name;
    String totalCredit;
    String totalDebit;

    String debit;
    String particular;
    String credit;
    String transaction_date;

    public String getRetailer_name() {
        return retailer_name;
    }

    public void setRetailer_name(String retailer_name) {
        this.retailer_name = retailer_name;
    }

    public String getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(String totalCredit) {
        this.totalCredit = totalCredit;
    }

    public String getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(String totalDebit) {
        this.totalDebit = totalDebit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }
}
