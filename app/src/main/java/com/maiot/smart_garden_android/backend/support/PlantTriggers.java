package com.maiot.smart_garden_android.backend.support;

import com.google.gson.annotations.SerializedName;

public class PlantTriggers {
    @SerializedName("name")
    public String name;
    @SerializedName("temperature")
    public Float temperature;
    @SerializedName("light")
    public Float light;
    @SerializedName("humidity")
    public Float humidity;
    @SerializedName("moisture")
    public Float moisture;

    public PlantTriggers(String name, Float temperature, Float light, Float humidity, Float moisture) {
        this.name = name;
        this.temperature = temperature;
        this.light = light;
        this.humidity = humidity;
        this.moisture = moisture;
    }
}
