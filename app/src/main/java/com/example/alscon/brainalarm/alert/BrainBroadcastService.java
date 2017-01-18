package com.example.alscon.brainalarm.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.alscon.brainalarm.Brain;
import com.example.alscon.brainalarm.service.BrainServiceBroadcastReceiver;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainBroadcastService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mathBrainServiceIntent = new Intent(context, BrainServiceBroadcastReceiver.class);
        context.sendBroadcast(mathBrainServiceIntent, null);

        StaticWakeLock.wakeLockOn(context);
        Bundle bundle = intent.getExtras();
        final Brain brain = (Brain)bundle.getSerializable("brain");

        Intent mathBrainAlertActivityIntent = new Intent(context, BrainAlertActivity.class);
        mathBrainAlertActivityIntent.putExtra("brain", brain);
        mathBrainAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mathBrainAlertActivityIntent);
    }
}
