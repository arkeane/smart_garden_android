package com.maiot.smart_garden_android.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Plant implements java.io.Serializable {
    @SerializedName("name")
    private final String name;
    @SerializedName("type")
    private final String type;
    @SerializedName("data_set")
    private ArrayList<SensorData> data_set;
    @SerializedName("planted")
    private Date planted;

    public Plant(String json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);

        Gson gson = new GsonBuilder()
                .setDateFormat(dateFormat.toPattern())
                .create();
        Plant plant = gson.fromJson(json, Plant.class);
        this.name = plant.getName();
        this.type = plant.getType();
        this.planted = plant.getPlanted();
        this.data_set = plant.getData_set();
    }

    public Plant(String name, String type) {
        Gson gson = new GsonBuilder()
                .create();
        Plant plant = gson.fromJson("{\"name\":\"" + name + "\",\"type\":\"" + type + "\"}", Plant.class);
        this.name = plant.getName();
        this.type = plant.getType();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Date getPlanted() {
        return planted;
    }

    public ArrayList<SensorData> getData_set() {
        return data_set;
    }
}
