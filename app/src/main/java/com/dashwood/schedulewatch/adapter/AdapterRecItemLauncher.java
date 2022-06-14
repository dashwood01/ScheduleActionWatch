package com.dashwood.schedulewatch.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dashwood.schedulewatch.databinding.RecItemLauncherBinding;
import com.dashwood.schedulewatch.diff.DiffUtilLauncherCallback;
import com.dashwood.schedulewatch.inf.InformationItemLauncher;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecItemLauncher extends RecyclerView.Adapter<AdapterRecItemLauncher.ViewHolder> {
    private List<InformationItemLauncher> informationItemLauncherList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RecItemLauncherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setInformation(informationItemLauncherList.get(position));
    }

    public void setInformationItemLauncherList(List<InformationItemLauncher> informationItemLauncherList) {
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtilLauncherCallback(informationItemLauncherList, this.informationItemLauncherList));
        this.informationItemLauncherList = informationItemLauncherList;
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return informationItemLauncherList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecItemLauncherBinding binding;

        public ViewHolder(@NonNull RecItemLauncherBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}

