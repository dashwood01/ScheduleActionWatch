package com.dashwood.schedulewatch.inf;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Objects;

public class InformationItemLauncher {
    private int id;
    private String name;
    private Drawable image;
    private View.OnClickListener action;
    private int color;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformationItemLauncher that = (InformationItemLauncher) o;
        return id == that.id && name.equals(that.name) && image.equals(that.image) && action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, action);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
