package com.logimetrix.locationsync;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface  APIInterface {
    @FormUrlEncoded
    @POST("/api/login")
    Call<String> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/gpsStatus")
    Call<String> gpsStatus(@Field("userid") String userid, @Field("status") String status, @Field("bat") String bat);

    @FormUrlEncoded
    @POST("/api/save-latlng")
    Call<String> sendlocation(@Field("user_id") String user_id, @Field("lat") String lat, @Field("lng") String lng, @Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("/api/retailers")
    Call<String> retalers(@Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("/api/user/add-payment-by-cheque")

    Call<String> addpayment( @Field("location") String location,@Field("user_id") String user_id,@Field("api_token") String api_token,@Field("retailer_id") String retailer_id, @Field("mode") String mode, @Field("amount") String amount, @Field("multipals_cheque_no") String multipals_cheque_no, @Field("transaction_date") String date);

    @FormUrlEncoded
    @POST("/api/user/add-payment-by-cash")
    Call<String> addpayment1( @Field("location") String location,@Field("user_id") String user_id,@Field("api_token") String api_token,@Field("retailer_id") String retailer_id, @Field("mode") String mode, @Field("amount") String amount, @Field("multipals_cheque_no") String multipals_cheque_no, @Field("transaction_date") String date);

}
