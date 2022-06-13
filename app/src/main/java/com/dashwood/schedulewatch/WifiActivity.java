package com.dashwood.schedulewatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.dashwood.schedulewatch.data.Data;
import com.dashwood.schedulewatch.databinding.ActivityWifiBinding;

public class WifiActivity extends Activity {

    private ActivityWifiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWifiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.swUseIt.setOnCheckedChangeListener((compoundButton, b) -> {
            Data.saveBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off),
                    b, getString(R.string.pref_key_wifi_from_take_off));
            if (b){
                //setEnableOfFeature();
                return;
            }
            //setDisableOfFeature();
        });
        binding.swUseIt.setChecked(Data.readBoolPreference(getApplicationContext(), getString(R.string.pref_home_wifi_from_take_off),
                false, getString(R.string.pref_key_wifi_from_take_off)));
    }
}