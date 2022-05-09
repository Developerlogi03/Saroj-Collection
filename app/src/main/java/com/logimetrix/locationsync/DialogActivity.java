package com.logimetrix.locationsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class DialogActivity extends Activity {
    private static DialogActivity dialogactivity;
    // AcmeSharedpreferences acmeSharedpreferences;
    public static DialogActivity CheckforDialogActivityInstance() {
        return dialogactivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogforalarm);
        // acmeSharedpreferences = AcmeSharedpreferences.getsharedprefInstance(getApplicationContext());
        Log.v("", " -------------- onCreate of DialogActivity ------------------");
        // if(!acmeSharedpreferences.getGpsStatus()) {
        showDialog(111);
        // }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        if (id == 111) {
            return new AlertDialog.Builder(DialogActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Alert")
                    .setMessage("Please switch on the GPS...")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 222);
                          /*  Intent i=new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(i,22);*/
                        }
                    })
                    .create();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222) {
            showdialogforgps();
        }
    }

    private void showdialogforgps() {
        if (!isGPSenabled()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(DialogActivity.this);
            alertbox.setTitle("Alert");
            alertbox.setMessage("Please switch on the GPS...!");
            alertbox.setCancelable(false);
            alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 222);
                    /*Intent i=new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivityForResult(i,22);
*/
                }
            });
            alertbox.show();
        } else {
            finish();
        }
    }

    private boolean isGPSenabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean bool = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return bool;
    }

    @Override
    public void onBackPressed() {
        // finish();
        startActivity(new Intent(this, MainActivity.class));
        finish();

       /* if(acmeSharedpreferences.getGpsStatus()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 222);
        }*/
    }
}