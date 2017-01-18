package com.example.alscon.brainalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        },SPLASH_DISPLAY_LENTH);
    }

    private void nextActivity() {
        Intent intent = new Intent(LogoActivity.this, BrainActivity.class);
        LogoActivity.this.startActivity(intent);
        LogoActivity.this.finish();
    }
}
