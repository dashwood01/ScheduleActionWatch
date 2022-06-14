package com.dashwood.schedulewatch.diff;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.dashwood.schedulewatch.inf.InformationItemLauncher;

import java.util.List;

public class DiffUtilLauncherCallback extends DiffUtil.Callback {
    private final List<InformationItemLauncher> newList;
    private final List<InformationItemLauncher> oldList;

    public DiffUtilLauncherCallback(List<InformationItemLauncher> newList, List<InformationItemLauncher> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList == null ? 0 : oldList.size();
    }


    @Override
    public int getNewListSize() {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
