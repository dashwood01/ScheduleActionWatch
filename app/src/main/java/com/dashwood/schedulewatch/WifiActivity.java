package com.dashwood.schedulewatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.dashwood.schedulewatch.data.Data;
import com.dashwood.schedulewatch.databinding.ActivityWifiBinding;
import com.dashwood.schedulewatch.inf.InformationItemLauncher;
import com.dashwood.schedulewatch.log.T;

import java.util.ArrayList;
import java.util.List;

public class WifiActivity extends Activity {

    private ActivityWifiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWifiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setView();
        setAction();
    }

    private void setView() {
    }

    private void setAction() {
        binding.swUseIt.setOnCheckedChangeListener((compoundButton, b) -> {
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off),
                    b, getString(R.string.pref_key_wifi_from_take_off));
            if (b) {
                setEnableOfFeature();
                return;
            }
            setDisableOfFeature();
        });
        binding.swUseIt.setChecked(Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off),
                false, getString(R.string.pref_key_wifi_from_take_off)));
        binding.swJustForOff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                setEnableJustForWifi();
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off_just_for_off),
                    b, getString(R.string.pref_key_wifi_from_take_off_just_for_off));
        });

        binding.swForOffAndOn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                setEnableForWifi();
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off_for_off_and_on),
                    b, getString(R.string.pref_key_wifi_from_take_off_for_off_and_on));
        });

    }

    private void setEnableOfFeature() {
        binding.swForOffAndOn.setEnabled(true);
        binding.swJustForOff.setEnabled(true);
    }

    private void setDisableOfFeature() {
        binding.swForOffAndOn.setEnabled(false);
        binding.swJustForOff.setEnabled(false);
        binding.swForOffAndOn.setChecked(false);
        binding.swJustForOff.setChecked(false);
    }

    private void setEnableJustForWifi() {
        binding.swForOffAndOn.setChecked(false);
    }


    private void setEnableForWifi() {
        binding.swJustForOff.setChecked(false);
    }
}