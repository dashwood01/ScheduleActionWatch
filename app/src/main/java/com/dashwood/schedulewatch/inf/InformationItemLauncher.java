package com.dashwood.schedulewatch.inf;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

public class InformationItemLauncher {
    private int id;
    private String name;
    private Drawable image;
    private View.OnClickListener action;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public View.OnClickListener getAction() {
        return action;
    }

    public void setAction(View.OnClickListener action) {
        this.action = action;
    }
}
