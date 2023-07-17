package com.maiot.smart_garden_android.backend.service;

import android.graphics.Bitmap;

public class ListItem {
    public String name;
    public Bitmap avatar;

    public ListItem(String name, Bitmap avatar) {
        this.name = name;
        this.avatar = avatar;
    }
}
