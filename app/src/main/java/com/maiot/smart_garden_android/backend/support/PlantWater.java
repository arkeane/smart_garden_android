package com.maiot.smart_garden_android.backend.support;

import com.google.gson.annotations.SerializedName;

public class PlantWater {
    @SerializedName("name")
    public String name;

    public PlantWater(String name) {
        this.name = name;
    }
}
