package com.dashwood.schedulewatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setNew();
        setView();
        setAction();
        addButtons();
    }

    private void setNew() {
        startForegroundService(new Intent(this, BackgroundService.class));
        adapterRecItemLauncher = new AdapterRecItemLauncher();
    }

    private void setView() {
        binding.recLauncherView.setEdgeItemsCenteringEnabled(true);
        binding.recLauncherView.setLayoutManager(
                new WearableLinearLayoutManager(this));
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        binding.recLauncherView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        binding.recLauncherView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recLauncherView.setAdapter(adapterRecItemLauncher);

    }

    private void setAction() {

    }

    private void addButtons() {
        List<InformationItemLauncher> informationItemLauncherList = new ArrayList<>();
        InformationItemLauncher informationItemLauncherBluetooth = new InformationItemLauncher();
        informationItemLauncherBluetooth.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bluetooth));
        informationItemLauncherBluetooth.setName(getString(R.string.item_text_bleutooth_schedule_when_watch_off));
        informationItemLauncherBluetooth.setAction(view -> startActivity(new Intent(this, BluetoothActivity.class)));
        informationItemLauncherBluetooth.setId(1);
        informationItemLauncherList.add(informationItemLauncherBluetooth);
        InformationItemLauncher informationItemLauncherWifi = new InformationItemLauncher();
        informationItemLauncherWifi.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_wifi));
        informationItemLauncherWifi.setName(getString(R.string.item_text_wifi_schedule_when_watch_off));
        informationItemLauncherWifi.setAction(view -> startActivity(new Intent(this, WifiActivity.class)));
        informationItemLauncherWifi.setId(2);
        informationItemLauncherList.add(informationItemLauncherWifi);
        InformationItemLauncher informationItemLauncherSchedule = new InformationItemLauncher();
        informationItemLauncherSchedule.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_schedule));
        informationItemLauncherSchedule.setName(getString(R.string.item_text_schedule));
        informationItemLauncherSchedule.setAction(view -> T.toast(getApplicationContext(), getString(R.string.toast_text_coming_soon)));
        informationItemLauncherSchedule.setId(3);
        informationItemLauncherList.add(informationItemLauncherSchedule);
        InformationItemLauncher informationItemLauncherAbout = new InformationItemLauncher();
        informationItemLauncherAbout.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_about));
        informationItemLauncherAbout.setName(getString(R.string.item_text_about));
        informationItemLauncherAbout.setAction(view -> startActivity( new Intent(this, AboutActivity.class)));
        informationItemLauncherAbout.setId(4);
        informationItemLauncherList.add(informationItemLauncherAbout);
        adapterRecItemLauncher.setInformationItemLauncherList(informationItemLauncherList);
    }


    static class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        private static final float MAX_ICON_PROGRESS = 0.65f;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {

            // Figure out % progress from top to bottom
            float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            // Normalize for center
            float progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            // Adjust to the maximum scale
            progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - progressToCenter);
            child.setScaleY(1 - progressToCenter);
        }
    }
}