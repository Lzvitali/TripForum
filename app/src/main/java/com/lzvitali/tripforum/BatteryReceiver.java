package com.lzvitali.tripforum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;

import androidx.appcompat.app.AppCompatActivity;


// reference: https://www.youtube.com/watch?v=c-cRLjwsGg8

public class BatteryReceiver extends BroadcastReceiver
{
    private Boolean isMessageDialogShowed = false;  // we want to show the  only once the dialog message
                                                    // show when it is 30% and don't show again in 29

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED))
        {
            // Percentage
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int percentage = level * 100 / scale;


            // if percentage lower than 30 and the phone is not charging raise a dialog message
            if(percentage <= 30 && status != BatteryManager.BATTERY_STATUS_CHARGING && !isMessageDialogShowed)
            {
                new DialogForLowBattery("Alert").show(((AppCompatActivity)context)
                        .getSupportFragmentManager(),null);
                isMessageDialogShowed = true;

            }
            // for allowing to show the dialog message when it will be less than 30 next time
            else if(percentage > 30 && isMessageDialogShowed)
            {
                isMessageDialogShowed = false;
            }

        }

    }




}
