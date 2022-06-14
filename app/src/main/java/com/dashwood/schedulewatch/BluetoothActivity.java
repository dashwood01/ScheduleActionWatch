package com.dashwood.schedulewatch;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.dashwood.schedulewatch.data.Data;
import com.dashwood.schedulewatch.databinding.ActivityBlueToothBinding;
import com.dashwood.schedulewatch.log.T;

public class BluetoothActivity extends Activity {

    private ActivityBlueToothBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlueToothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.swUseIt.setOnCheckedChangeListener((compoundButton, b) -> Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_bluetooth_from_take_off),
                b, getString(R.string.pref_key_bluetooth_from_take_off)));
        binding.swUseIt.setChecked(Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_bluetooth_from_take_off),
                false, getString(R.string.pref_key_bluetooth_from_take_off)));
    }
}