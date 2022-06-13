package com.dashwood.schedulewatch.log;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class T {
    public static void log(String message) {
        Log.i("LOG", message);
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
