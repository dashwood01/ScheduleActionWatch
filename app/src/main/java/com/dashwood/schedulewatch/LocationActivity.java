package com.dashwood.schedulewatch;

import android.app.Activity;
import android.os.Bundle;

import com.dashwood.schedulewatch.data.Data;
import com.dashwood.schedulewatch.databinding.ActivityLocationBinding;

public class LocationActivity extends Activity {
    private ActivityLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setAction();
        setView();
    }

    private void setView() {
        binding.swUseIt.setChecked(Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off),
                false, getString(R.string.pref_key_location_from_take_off)));
        binding.swJustForOff.setChecked(Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off_just_for_off),
                false, getString(R.string.pref_key_location_from_take_off_just_for_off)));
        binding.swForOffAndOn.setChecked(Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off_for_off_and_on),
                false, getString(R.string.pref_key_location_from_take_off_for_off_and_on)));
    }

    private void setAction() {
        binding.swUseIt.setOnCheckedChangeListener((compoundButton, b) -> {
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off),
                    b, getString(R.string.pref_key_location_from_take_off));
            if (b) {
                setEnableOfFeature();
                return;
            }
            setDisableOfFeature();
        });
        binding.swJustForOff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                setEnableJustForWifi();
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off_just_for_off),
                    b, getString(R.string.pref_key_location_from_take_off_just_for_off));
        });
        binding.swForOffAndOn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                setEnableForWifi();
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off_for_off_and_on),
                    b, getString(R.string.pref_key_location_from_take_off_for_off_and_on));
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

    private void setOffStoredIfNoneSwitchChecked() {
        if (!binding.swForOffAndOn.isChecked() && !binding.swJustForOff.isChecked())
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_location_from_take_off),
                    false, getString(R.string.pref_key_location_from_take_off));
    }

    @Override
    protected void onDestroy() {
        setOffStoredIfNoneSwitchChecked();
        binding = null;
        super.onDestroy();
    }
}