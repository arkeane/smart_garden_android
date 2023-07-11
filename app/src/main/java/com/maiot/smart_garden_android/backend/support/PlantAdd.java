package com.maiot.smart_garden_android.backend.support;

import com.google.gson.annotations.SerializedName;

public class PlantAdd {
    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;

    public PlantAdd(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
