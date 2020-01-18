package com.lzvitali.tripforum;

import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

public class AppSuperClass extends AppCompatActivity
{

    // For battery broadcast
    private static BatteryReceiver mBatteryReceiver = new BatteryReceiver();
    private static IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);


    // -------------------------------- For battery broadcast --------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mBatteryReceiver, mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        unregisterReceiver(mBatteryReceiver);
        super.onPause();
    }
    // End: For battery broadcast ------------------------------------------------------------------

}
