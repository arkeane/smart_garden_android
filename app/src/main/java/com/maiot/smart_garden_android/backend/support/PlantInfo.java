package com.maiot.smart_garden_android.backend.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlantInfo implements java.io.Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("planted")
    private Date planted;


    public PlantInfo(String name, String type, Date planted) {
        this.name = name;
        this.type = type;
        this.planted = planted;
    }

    public PlantInfo(String json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);

        Gson gson = new GsonBuilder()
                .setDateFormat(dateFormat.toPattern())
                .create();
        PlantInfo plant = gson.fromJson(json, PlantInfo.class);
        this.name = plant.getName();
        this.type = plant.getType();
        this.planted = plant.getPlanted();
    }

    @Override
    public String toString() {
        return "Plant{" +
                "name='" + this.name + '\'' +
                ", type='" + this.type + '\'' +
                ", planted=" + this.planted +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getPlanted() {
        return planted;
    }

    public void setPlanted(Date planted) {
        this.planted = planted;
    }
}
