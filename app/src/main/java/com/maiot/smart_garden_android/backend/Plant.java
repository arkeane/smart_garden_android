package com.maiot.smart_garden_android.backend;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Plant {
    @SerializedName("name")
    public static String name;
    @SerializedName("type")
    public static String type;
    @SerializedName("planted")
    public static Date planted;
    @SerializedName("data_set")
    public static SensorData data_set;

    public Plant(String json) {
        new Gson().fromJson(json, Plant.class);
        try {
            this.name = Plant.getName();
            this.type = Plant.getType();
            this.planted = Plant.getPlanted();
            this.data_set = Plant.getData_set();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON string.");
        }
    }

    private static SensorData getData_set() {
        return data_set;
    }

    public static String getName() {
        return name;
    }

    public static String getType() {
        return type;
    }

    public static Date getPlanted() {
        return planted;
    }
}
