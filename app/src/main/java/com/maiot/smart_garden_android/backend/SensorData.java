package com.maiot.smart_garden_android.backend;

import com.google.gson.Gson;

import java.util.Date;

public class SensorData implements java.io.Serializable {
    private final Date date;
    private final float temperature;
    private final float humidity;
    private final float light;
    private final float moisture;

    public SensorData(float temperature, float humidity, float light, float moisture) {
        this.date = new Date();
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.moisture = moisture;
    }

    public Date getDate() {
        return date;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
