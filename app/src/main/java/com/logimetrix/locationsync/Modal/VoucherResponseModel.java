package com.logimetrix.locationsync.Modal;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoucherResponseModel {

    @SerializedName("flag")
    @Expose
    private Boolean flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private Object type;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

}



