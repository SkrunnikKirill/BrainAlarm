package com.example.alscon.brainalarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alscon.brainalarm.Brain;
import com.example.alscon.brainalarm.alert.BrainBroadcastService;
import com.example.alscon.brainalarm.datebase.DataBase;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(this.getClass().getSimpleName(), "onCreate()");
        super.onCreate();
    }

    private Brain getNext(){
        Set<Brain> brainQueue = new TreeSet<>(new Comparator<Brain>() {
            @Override
            public int compare(Brain lhs, Brain rhs) {
                int result = 0;
                long diff = lhs.getBrainTime().getTimeInMillis() - rhs.getBrainTime().getTimeInMillis();
                if(diff>0){
                    return 1;
                }else if (diff < 0){
                    return -1;
                }
                return result;
            }
        });

        DataBase.init(getApplicationContext());
        List<Brain> brains = DataBase.getAll();

        for(Brain brain : brains){
            if(brain.getBrainActive())
                brainQueue.add(brain);
        }
        if(brainQueue.iterator().hasNext()){
            return brainQueue.iterator().next();
        }else{
            return null;
        }
    }
    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        DataBase.deactivate();
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this.getClass().getSimpleName(),"onStartCommand()");
        Brain brain = getNext();
        if(null != brain){
            brain.schedule(getApplicationContext());
            Log.d(this.getClass().getSimpleName(),brain.getTimeUntilNextBrainMessage());

        }else{
            Intent myIntent = new Intent(getApplicationContext(), BrainBroadcastService.class);
            myIntent.putExtra("brain", new Brain());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager brainManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            brainManager.cancel(pendingIntent);
        }
        return START_NOT_STICKY;
    }
}
