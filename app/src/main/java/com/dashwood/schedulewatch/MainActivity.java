package com.dashwood.schedulewatch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;

import com.dashwood.schedulewatch.adapter.AdapterRecItemLauncher;
import com.dashwood.schedulewatch.databinding.ActivityMainBinding;
import com.dashwood.schedulewatch.inf.InformationItemLauncher;
import com.dashwood.schedulewatch.log.T;
import com.dashwood.schedulewatch.service.BackgroundService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;
    private AdapterRecItemLauncher adapterRecItemLauncher;

    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startForegroundService(new Intent(this, BackgroundService.class));


        binding.recLauncherView.setEdgeItemsCenteringEnabled(true);
        binding.recLauncherView.setLayoutManager(
                new WearableLinearLayoutManager(this));
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        binding.recLauncherView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        adapterRecItemLauncher = new AdapterRecItemLauncher();
        binding.recLauncherView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recLauncherView.setAdapter(adapterRecItemLauncher);

        setViewForButtons();
    }

    private void setViewForButtons() {
        List<InformationItemLauncher> informationItemLauncherList = new ArrayList<>();
        InformationItemLauncher informationItemLauncherBluetooth = new InformationItemLauncher();
        informationItemLauncherBluetooth.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bluetooth));
        informationItemLauncherBluetooth.setName(getString(R.string.item_text_bleutooth_schedule_when_watch_off));
        informationItemLauncherBluetooth.setAction(view -> startActivity(new Intent(MainActivity.this, BlueToothActivity.class)));
        informationItemLauncherBluetooth.setId(1);
        informationItemLauncherList.add(informationItemLauncherBluetooth);
        InformationItemLauncher informationItemLauncherWifi = new InformationItemLauncher();
        informationItemLauncherWifi.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_wifi));
        informationItemLauncherWifi.setName(getString(R.string.item_text_wifi_schedule_when_watch_off));
        informationItemLauncherWifi.setAction(view -> startActivity(new Intent(MainActivity.this, WifiActivity.class)));
        informationItemLauncherWifi.setId(2);
        informationItemLauncherList.add(informationItemLauncherWifi);
        InformationItemLauncher informationItemLauncherSchedule = new InformationItemLauncher();
        informationItemLauncherSchedule.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_schedule));
        informationItemLauncherSchedule.setName(getString(R.string.item_text_schedule));
        informationItemLauncherSchedule.setAction(view -> {
            //startActivity(new Intent(MainActivity.this, BlueToothActivity.class))
            T.toast(getApplicationContext(), getString(R.string.toast_text_coming_soon));
            BatteryManager batteryManager = (BatteryManager) getSystemService(Service.BATTERY_SERVICE);
            T.log("LEVEL : " + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
        });
        informationItemLauncherSchedule.setId(3);
        informationItemLauncherList.add(informationItemLauncherSchedule);
        InformationItemLauncher informationItemLauncherAbout = new InformationItemLauncher();
        informationItemLauncherAbout.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_about));
        informationItemLauncherAbout.setName(getString(R.string.item_text_about));
        informationItemLauncherAbout.setAction(view -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
        informationItemLauncherAbout.setId(4);
        informationItemLauncherList.add(informationItemLauncherAbout);
        adapterRecItemLauncher.setInformationItemLauncherList(informationItemLauncherList);
    }


    static class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        /**
         * How much should we scale the icon at most.
         */
        private static final float MAX_ICON_PROGRESS = 0.65f;

        private float progressToCenter;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {

            // Figure out % progress from top to bottom
            float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            // Normalize for center
            progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            // Adjust to the maximum scale
            progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - progressToCenter);
            child.setScaleY(1 - progressToCenter);
        }
    }
}