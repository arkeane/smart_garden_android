package com.maiot.smart_garden_android.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class PlantTrigger {
    @SerializedName("name")
    private final String name;
    @SerializedName("trigger_status")
    private Boolean status;
    @SerializedName("temperature_trigger")
    private Float temperature;
    @SerializedName("humidity_trigger")
    private Float humidity;
    @SerializedName("light_trigger")
    private Float light;
    @SerializedName("moisture_trigger")
    private Float moisture;

    public PlantTrigger(String json) {
        Gson gson = new GsonBuilder()
                .create();
        PlantTrigger trig = gson.fromJson(json, PlantTrigger.class);
        this.name = trig.getName();
        this.status = trig.getStatus();
        this.temperature = trig.getTemperature();
        this.humidity = trig.getHumidity();
        this.light = trig.getLight();
        this.moisture = trig.getMoisture();
    }

    public String getName() {
        return name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getLight() {
        return light;
    }

    public void setLight(Float light) {
        this.light = light;
    }

    public Float getMoisture() {
        return moisture;
    }

    public void setMoisture(Float moisture) {
        this.moisture = moisture;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .create();
        return gson.toJson(this);
    }
}
