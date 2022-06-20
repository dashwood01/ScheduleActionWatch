package com.dashwood.schedulewatch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

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
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        binding.recLauncherView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        binding.recLauncherView.setAdapter(adapterRecItemLauncher);

    }

    private void setAction() {

    }

    private void addButtons() {
        List<InformationItemLauncher> informationItemLauncherList = new ArrayList<>();
        InformationItemLauncher informationItemLauncher = new InformationItemLauncher();
        informationItemLauncher.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bluetooth));
        informationItemLauncher.setName(getString(R.string.item_text_bleutooth_schedule_when_watch_off));
        informationItemLauncher.setAction(view -> startActivity(new Intent(this, BluetoothActivity.class)));
        informationItemLauncher.setId(1);
        informationItemLauncher.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_back_bluetooth));
        informationItemLauncherList.add(informationItemLauncher);
        informationItemLauncher = new InformationItemLauncher();
        informationItemLauncher.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_wifi));
        informationItemLauncher.setName(getString(R.string.item_text_wifi_schedule_when_watch_off));
        informationItemLauncher.setAction(view -> startActivity(new Intent(this, WifiActivity.class)));
        informationItemLauncher.setId(2);
        informationItemLauncher.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_back_wifi));
        informationItemLauncherList.add(informationItemLauncher);
        informationItemLauncher = new InformationItemLauncher();
        informationItemLauncher.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_location));
        informationItemLauncher.setName(getString(R.string.item_text_location));
        informationItemLauncher.setAction(view -> startActivity(new Intent(this, LocationActivity.class)));
        informationItemLauncher.setId(3);
        informationItemLauncher.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_back_location));
        informationItemLauncherList.add(informationItemLauncher);
        informationItemLauncher = new InformationItemLauncher();
        informationItemLauncher.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_schedule));
        informationItemLauncher.setName(getString(R.string.item_text_schedule));
        informationItemLauncher.setAction(view -> T.toast(getApplicationContext(), getString(R.string.toast_text_coming_soon)));
        informationItemLauncher.setId(4);
        informationItemLauncher.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_back_schedule));
        informationItemLauncherList.add(informationItemLauncher);
        informationItemLauncher = new InformationItemLauncher();
        informationItemLauncher.setImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_about));
        informationItemLauncher.setName(getString(R.string.item_text_about));
        informationItemLauncher.setAction(view -> startActivity(new Intent(this, AboutActivity.class)));
        informationItemLauncher.setId(5);
        informationItemLauncher.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_back_about));
        informationItemLauncherList.add(informationItemLauncher);
        adapterRecItemLauncher.setInformationItemLauncherList(informationItemLauncherList);
    }


    static class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        private static final float MAX_ICON_PROGRESS = 0.65f;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {

            float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            float progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - progressToCenter);
            child.setScaleY(1 - progressToCenter);
        }
    }
}