package com.example.alscon.brainalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.alscon.brainalarm.service.BrainServiceBroadcastReceiver;

import java.lang.reflect.Field;

/**
 * Created by Alscon on 23-Nov-16.
 */
public abstract class BaseActivity extends ActionBarActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }


    protected void callMathBrainScheduleService() {
        Intent mathAlarmServiceIntent = new Intent(this, BrainServiceBroadcastReceiver.class);
        sendBroadcast(mathAlarmServiceIntent, null);
    }
}
