package com.dashwood.schedulewatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.dashwood.schedulewatch.databinding.ActivityWhichOneBinding;
import com.dashwood.schedulewatch.databinding.ActivityWifiBinding;

public class WhichOneActivity extends Activity {

    private ActivityWhichOneBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWhichOneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NotificationManagerCompat.from(this).cancelAll();
        setAction();
    }

    private void setAction() {
        binding.btnWifi.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));

        binding.btnLocation.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();

    }
}