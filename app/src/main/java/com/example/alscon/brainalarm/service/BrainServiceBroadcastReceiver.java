package com.example.alscon.brainalarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BrainService.class);
        context.startService(serviceIntent);
    }
}
