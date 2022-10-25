package com.logimetrix.locationsync;

import com.logimetrix.locationsync.Modal.BankModel;
import com.logimetrix.locationsync.Modal.ClosingBalance;
import com.logimetrix.locationsync.Modal.DealerModal;
import com.logimetrix.locationsync.Modal.ExpenseVoucherListModel;
import com.logimetrix.locationsync.Modal.RetailerModel;
import com.logimetrix.locationsync.Modal.VoucherResponseModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface  APIInterface {
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
    @POST("/api/active-ledger-retailer-list")
    Call<RetailerModel> retalers(@Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("/api/routes")
    Call<String> week(@Field("api_token") String api_token);


    @FormUrlEncoded
    @POST("/api/group-retailer-list")
    Call<String> groupList(@Field("api_token") String api_token,@Field("route_id") String route_id);


    @FormUrlEncoded
    @POST("/api/groupwise-retailer-list")
    Call<String> groupWiseRetailerList(@Field("api_token") String api_token,@Field("group_id") String group_id);


    //  @FormUrlEncoded
  //  @POST("/api/retailers")
  //  Call<RetailerModel> retalers(@Field("api_token") String api_token);



    @FormUrlEncoded
    @POST("/api/bank-accounts")
    Call<BankModel> bankDetails(@Field("api_token") String api_token);


    @FormUrlEncoded
    @POST("/api/collection-list")
    Call<BankModel> collectionList(@Field("api_token") String api_token);//change model class

    @FormUrlEncoded
    @POST("/api/add-expense-voucher")
    Call<VoucherResponseModel> voucherResponse(@Field("api_token") String api_token,
                                               @Field("dealer_id") String dealerId,
                                               @Field("mode") String mode,
                                               @Field("bank_id") String bankId,
                                               @Field("amount") String amount,
                                               @Field("type") String expenseType,
                                               @Field("transaction_date") String transactionDate);


    @FormUrlEncoded
    @POST("/api/retailers")
    Call<List<RetailerModel.Retailer>> retalersNew(@Field("api_token") String api_token);


    @FormUrlEncoded
    @POST("/api/get-expense-voucher")
    Call<ExpenseVoucherListModel> expenseVoucherList(@Field("api_token") String api_token);


    @FormUrlEncoded
    @POST("/api/dealers")
    Call<DealerModal> dealers(@Field("api_token") String api_token);


    @FormUrlEncoded
    @POST("/api/user/party-ledger-closing")
    Call<ClosingBalance> closingBal(@Field("api_token") String api_token,
                                    @Field("dealer_id") String dealerId,
                                    @Field("retailer_id") String retailerId);

    @FormUrlEncoded
    @POST("/api/user/add-payment-by-cheque")
    Call<String> addpayment( @Field("location") String location,@Field("user_id") String user_id,@Field("api_token") String api_token,@Field("retailer_id") String retailer_id, @Field("mode") String mode, @Field("amount") String amount, @Field("multipals_cheque_no") String multipals_cheque_no, @Field("transaction_date") String date);

    @FormUrlEncoded
    @POST("/api/user/add-payment-by-cash")
    Call<String> addpayment1( @Field("location") String location,@Field("user_id") String user_id,@Field("api_token") String api_token,@Field("retailer_id") String retailer_id, @Field("mode") String mode, @Field("amount") String amount, @Field("multipals_cheque_no") String multipals_cheque_no, @Field("transaction_date") String date);

    @FormUrlEncoded
    @POST("/api/user/add-payment-by-cash")
    Call<String> addCashpayment1( @Field("location") String location,@Field("user_id") String user_id,@Field("api_token") String api_token,@Field("retailer_id") String retailer_id, @Field("mode") String mode, @Field("amount") String amount, @Field("multipals_cheque_no") String multipals_cheque_no,@Field("dealer_id") String dealerID,@Field("reference_no") String referenceNo);

    @FormUrlEncoded
    @POST("/api/user/add-payment-by-cheque")
    Call<String> addChequepayment( @Field("location") String location,@Field("user_id") String user_id,@Field("api_token") String api_token,@Field("retailer_id") String retailer_id, @Field("mode") String mode, @Field("amount") String amount, @Field("multipals_cheque_no") String multipals_cheque_no, @Field("dealer_id") String dealerID,@Field("reference_no") String referenceNo);

    @FormUrlEncoded
    @POST("/api/retailer-ledger-list")
    Call<String> retailerledgerlist( @Field("api_token") String token,@Field("retailer_id") String retailer_id);

    @FormUrlEncoded
    @POST("/api/retailer-ledger-excel")
    Call<String> ledgerfileUrl( @Field("api_token") String token,@Field("retailer_id") String retailer_id);

}
