package com.maiot.smart_garden_android.backend;

import com.google.gson.Gson;

import java.util.Date;

public class Plant {
    public static String name;
    public static String type;
    public static Date planted;
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
