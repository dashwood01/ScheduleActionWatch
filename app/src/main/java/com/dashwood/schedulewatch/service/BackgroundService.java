package com.dashwood.schedulewatch.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.dashwood.schedulewatch.R;
import com.dashwood.schedulewatch.data.Data;
import com.dashwood.schedulewatch.log.T;

public class BackgroundService extends Service implements SensorEventListener {
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private WifiManager wifiManager;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isCreated = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        T.log("Is : " + isCreated);
        if (isCreated)
            return;
        isCreated = true;
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
        setSensorEnable();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batInfoReceiver, intentFilter);
      /*  Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, m_serviceConnection, BIND_AUTO_CREATE);*/
        addNotification();
    }

    private void setSensorEnable() {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor detectLowLatency = mSensorManager.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT);
        mSensorManager.registerListener(this, detectLowLatency, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private BroadcastReceiver batInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            T.log(level + "%");
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)
            return;
        if (event.values[0] == 1.0f)
            enableService();
        else if (event.values[0] == 0.0f)
            disableService();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void enableService() {
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_bluetooth_from_take_off),
                false, getString(R.string.pref_key_bluetooth_from_take_off)))
            enableBluetooth();
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off), false,
                getString(R.string.pref_key_wifi_from_take_off)))
            enableWifi();
    }

    private void disableService() {
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_bluetooth_from_take_off),
                false, getString(R.string.pref_key_bluetooth_from_take_off)))
            disableBluetooth();
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off), false,
                getString(R.string.pref_key_wifi_from_take_off)))
            disableWifi();
    }

    @SuppressLint("MissingPermission")
    private void enableBluetooth() {
        try {
            Uri pathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.blue_on);
            mediaPlayer.reset();
            mediaPlayer.setVolume(100f, 100f);
            mediaPlayer.setDataSource(getApplicationContext(), pathUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bluetoothAdapter.enable();
    }

    @SuppressLint("MissingPermission")
    private void disableBluetooth() {
        try {
            Uri pathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.blue_off);
            mediaPlayer.reset();
            mediaPlayer.setVolume(100f, 100f);
            mediaPlayer.setDataSource(getApplicationContext(), pathUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bluetoothAdapter.disable();
    }

    private void enableWifi() {
        if (wifiManager.isWifiEnabled())
            return;
        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void disableWifi() {
        if (wifiManager.isWifiEnabled()) {
            try {
                Uri pathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wifi_off);
                mediaPlayer.reset();
                mediaPlayer.setVolume(100f, 100f);
                mediaPlayer.setDataSource(getApplicationContext(), pathUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onDestroy() {
        T.log("Ondestory");
        super.onDestroy();
    }


    private void addNotification() {
        String NOTIFICATION_CHANNEL_ID = "com.dashwood.schedulewatch";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Application run in background")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}