package com.logimetrix.locationsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
       // Toast.makeText(context, "ACTION_SCREEN_ON!!", Toast.LENGTH_LONG).show();
        String action = intent.getAction();
        if(Intent.ACTION_SCREEN_ON.equals(action)) {
           // Toast.makeText(context, "ACTION_SCREEN_ON!!", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ProcessingService.class));
            } else {
                context.startService(new Intent(context, ProcessingService.class));
            }
        }
        else if(Intent.ACTION_SCREEN_OFF.equals(action)) {
            // stop the service
           // Toast.makeText(context, "ACTION_SCREEN_OFF!!", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ProcessingService.class));
            } else {
                context.startService(new Intent(context, ProcessingService.class));
            }
        }
        else if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
           // Toast.makeText(context, "BOOT_COMPLETED!!", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ProcessingService.class));
            } else {
                context.startService(new Intent(context, ProcessingService.class));
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ProcessingService.class));
            } else {
                context.startService(new Intent(context, ProcessingService.class));
            }
        }
    }
}