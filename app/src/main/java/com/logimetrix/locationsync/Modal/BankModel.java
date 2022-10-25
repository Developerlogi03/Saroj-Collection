package com.logimetrix.locationsync.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BankModel {

    @SerializedName("flag")
    @Expose
    private Boolean flag;
    @SerializedName("bank_accounts")
    @Expose
    private List<BankAccount> bankAccounts = null;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public class BankAccount {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("bank_id")
        @Expose
        private Integer bankId;
        @SerializedName("bank_branch")
        @Expose
        private String bankBranch;
        @SerializedName("account_number")
        @Expose
        private String accountNumber;
        @SerializedName("ifsc_code")
        @Expose
        private Object ifscCode;
        @SerializedName("account_holder_name")
        @Expose
        private String accountHolderName;
        @SerializedName("is_active")
        @Expose
        private Integer isActive;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("bank")
        @Expose
        private Bank bank;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getBankId() {
            return bankId;
        }

        public void setBankId(Integer bankId) {
            this.bankId = bankId;
        }

        public String getBankBranch() {
            return bankBranch;
        }

        public void setBankBranch(String bankBranch) {
            this.bankBranch = bankBranch;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public Object getIfscCode() {
            return ifscCode;
        }

        public void setIfscCode(Object ifscCode) {
            this.ifscCode = ifscCode;
        }

        public String getAccountHolderName() {
            return accountHolderName;
        }

        public void setAccountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
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

        public Bank getBank() {
            return bank;
        }

        public void setBank(Bank bank) {
            this.bank = bank;
        }


        public class Bank {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
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

}


