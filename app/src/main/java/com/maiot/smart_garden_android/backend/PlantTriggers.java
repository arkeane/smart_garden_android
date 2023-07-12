package com.maiot.smart_garden_android.backend;

import com.google.gson.annotations.SerializedName;

public class PlantTriggers {
    @SerializedName("plant")
    private String name;
    @SerializedName("temperature_trigger")
    private String temperature;
    @SerializedName("humidity_trigger")
    private String humidity;
    @SerializedName("light_trigger")
    private String light;
    @SerializedName("moisture_trigger")
    private String moisture;
}
