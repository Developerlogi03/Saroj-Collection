package com.logimetrix.locationsync;

public class Retailerpojo {
    private String id;
    private String name;
    private String hindi_name;
    private String mobile_number;
    private String address;
    private String hindi_address;
    private String gst_number;
    private String is_active;
    private String created_at;
    private String updated_at;

    public Retailerpojo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHindi_name() {
        return hindi_name;
    }

    public void setHindi_name(String hindi_name) {
        this.hindi_name = hindi_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHindi_address() {
        return hindi_address;
    }

    public void setHindi_address(String hindi_address) {
        this.hindi_address = hindi_address;
    }

    public String getGst_number() {
        return gst_number;
    }

    public void setGst_number(String gst_number) {
        this.gst_number = gst_number;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return name + "\n" + mobile_number + "\n" + address;
    }
}
