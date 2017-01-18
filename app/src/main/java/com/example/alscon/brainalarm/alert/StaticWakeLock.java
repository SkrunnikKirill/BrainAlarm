package com.example.alscon.brainalarm.alert;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class StaticWakeLock {
    private static PowerManager.WakeLock sWakeLock = null;

    public static void wakeLockOn(Context context){
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (sWakeLock == null){
            sWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MATH_ALARM");
        }
        sWakeLock.acquire();
    }

    public static void wakeLockOff(Context context){
        try{
            if (sWakeLock != null){
                sWakeLock.release();
            }
        }catch (Exception e){

        }
    }
}
