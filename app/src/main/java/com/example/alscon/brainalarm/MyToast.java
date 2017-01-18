package com.example.alscon.brainalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by Alscon on 24-Nov-16.
 */

public class MyToast {
    private static LayoutInflater mInflater;
    private static android.widget.Toast mToast;
    private static View mView;

    public static void clock(Context context, String msg) {
        clock(context, msg, android.widget.Toast.LENGTH_SHORT);
    }

    public static void clock(Context context, String msg, int duration) {
        mInflater = LayoutInflater.from(context);
        mView = mInflater.inflate(R.layout.toast, null);
        initSetButtonMsg(msg);
        mToast = new android.widget.Toast(context);
        mToast.setView(mView);
        mToast.setDuration(duration);
        mToast.show();
    }

    private static Button initSetButtonMsg(String msg) {
        Button mButton = (Button) mView.findViewById(R.id.button);
        mButton.setText(msg);
        return mButton;
    }

    public static void CancelCurrentToast() {
        if (mToast != null)
            mToast.cancel();
    }
}
