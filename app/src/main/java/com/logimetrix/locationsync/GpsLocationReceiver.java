package com.logimetrix.locationsync;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsLocationReceiver extends BroadcastReceiver
{
    APIInterface apiInterface = APIClient1.getClient().create(APIInterface.class);
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    @Override
    public void onReceive(Context context, Intent intent)
    {
       // Toast.makeText(context, "Gps On.", Toast.LENGTH_SHORT).show();
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

    }

    public void syncgps(String status,String bat)
    {

        Call<String> call1 = apiInterface.gpsStatus(pref.getString("id",""),status,bat);
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println(status+" :: service response is :: "+response);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //   progressdialog.dismiss();
                call.cancel();
            }
        });
    }
}
