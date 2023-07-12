package com.maiot.smart_garden_android.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorData implements java.io.Serializable {
    @SerializedName("date")
    private final Date date;
    @SerializedName("temperature")
    private final float temperature;
    @SerializedName("humidity")
    private final float humidity;
    @SerializedName("light")
    private final float light;
    @SerializedName("moisture")
    private final float moisture;

    public SensorData(String json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);

        Gson gson = new GsonBuilder()
                .setDateFormat(dateFormat.toPattern())
                .create();
        SensorData sensorData = gson.fromJson(json, SensorData.class);
        this.date = sensorData.getDate();
        this.temperature = sensorData.getTemperature();
        this.humidity = sensorData.getHumidity();
        this.light = sensorData.getLight();
        this.moisture = sensorData.getMoisture();
    }
    public Date getDate() {
        return date;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getLight() {
        return light;
    }

    public float getMoisture() {
        return moisture;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
