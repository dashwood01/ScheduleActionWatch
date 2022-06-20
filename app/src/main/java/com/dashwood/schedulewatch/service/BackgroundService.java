package com.dashwood.schedulewatch.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dashwood.schedulewatch.R;
import com.dashwood.schedulewatch.WhichOneActivity;
import com.dashwood.schedulewatch.data.Data;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service implements SensorEventListener {
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;

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
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
        setSensorEnable();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batInfoReceiver, intentFilter);
        addNotification();
    }

    private void setSensorEnable() {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor detectLowLatency = mSensorManager.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT);
        mSensorManager.registerListener(this, detectLowLatency, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final BroadcastReceiver batInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            if (!isPlugged(ctxt))
                return;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            if (level > 95)
                enableTimer();
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
        if (checkTwoSettings()) {
            if (!wifiManager.isWifiEnabled() && !locationManager.isLocationEnabled()) {
                soundNotifyLocation();
                addNotificationForEnable();
            } else if (!wifiManager.isWifiEnabled())
                checkWifiWhatToDo();
            else if (!locationManager.isLocationEnabled())
                checkLocationWhatToDo();
            return;
        }
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off), false,
                getString(R.string.pref_key_wifi_from_take_off)))
            checkWifiWhatToDo();
    }

    private void disableService() {
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_bluetooth_from_take_off),
                false, getString(R.string.pref_key_bluetooth_from_take_off)))
            disableBluetooth();
        if (checkTwoSettings()) {
            soundNotifyLocation();
            addNotificationForDisable();
            return;
        }
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off), false,
                getString(R.string.pref_key_wifi_from_take_off)))
            disableWifi();
    }

    private boolean checkTwoSettings() {
        return (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off), false,
                getString(R.string.pref_key_wifi_from_take_off)) && Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off), false,
                getString(R.string.pref_key_location_from_take_off)));
    }

    @SuppressLint("MissingPermission")
    private void enableBluetooth() {
        soundNotifyBluetoothEnable();
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

    private void checkLocationWhatToDo() {
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off_just_for_off), false,
                getString(R.string.pref_key_location_from_take_off_just_for_off)))
            disableLocation();
        else if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off_for_off_and_on), false,
                getString(R.string.pref_key_location_from_take_off_for_off_and_on)))
            if (locationManager.isLocationEnabled())
                disableLocation();
            else
                enableLocation();
    }

    private void checkWifiWhatToDo() {
        if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off_just_for_off), false,
                getString(R.string.pref_key_wifi_from_take_off_just_for_off)))
            disableWifi();
        else if (Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off_for_off_and_on), false,
                getString(R.string.pref_key_wifi_from_take_off_for_off_and_on)))
            if (wifiManager.isWifiEnabled())
                disableWifi();
            else
                enableWifi();
    }

    private void enableWifi() {
        if (wifiManager.isWifiEnabled())
            return;
        soundNotifyWifi();
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void disableWifi() {
        if (!wifiManager.isWifiEnabled())
            return;
        soundNotifyWifi();
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void enableLocation() {
        if (locationManager.isLocationEnabled())
            return;
        soundNotifyLocation();
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void disableLocation() {
        if (!locationManager.isLocationEnabled())
            return;
        soundNotifyLocation();
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

    private void addNotificationForDisable() {
        Intent intent = new Intent(getApplicationContext(), WhichOneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        String NOTIFICATION_CHANNEL_ID = "com.dashwood.schedulewatch";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Turn off WIFI and GPS")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_STATUS)
                .setContentIntent(notifyPendingIntent)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(33658, notification);
    }

    private void addNotificationForEnable() {
        Intent intent = new Intent(getApplicationContext(), WhichOneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        String NOTIFICATION_CHANNEL_ID = "com.dashwood.schedulewatch";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Turn on WIFI and GPS")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_STATUS)
                .setContentIntent(notifyPendingIntent)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(3365, notification);
    }

    private boolean isPlugged(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private void enableTimer() {
        if (timer != null)
            return;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlugged(getApplicationContext()))
                    soundNotifyCharge();
                else {
                    timer.purge();
                    timer.cancel();
                    timer = null;
                }
            }
        }, 1000, 60000);
    }

    private void soundNotifyBluetoothEnable() {
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
    }

    private void soundNotifyWifi() {
        try {
            Uri pathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wifi_action);
            mediaPlayer.reset();
            mediaPlayer.setVolume(100f, 100f);
            mediaPlayer.setDataSource(getApplicationContext(), pathUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void soundNotifyLocation() {
        try {
            Uri pathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.location_action);
            mediaPlayer.reset();
            mediaPlayer.setVolume(100f, 100f);
            mediaPlayer.setDataSource(getApplicationContext(), pathUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void soundNotifyCharge() {
        try {
            Uri pathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.charge_full);
            mediaPlayer.reset();
            mediaPlayer.setVolume(100f, 100f);
            mediaPlayer.setDataSource(getApplicationContext(), pathUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
