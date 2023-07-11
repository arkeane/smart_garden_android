package com.maiot.smart_garden_android.backend;

import com.maiot.smart_garden_android.backend.support.PlantAdd;
import com.maiot.smart_garden_android.backend.support.PlantTriggers;
import com.maiot.smart_garden_android.backend.support.PlantWater;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SmartGardenService {
    @GET("plants/info")
    Call<String> getAllPlantsInfo();

    @GET("plants/all")
    Call<String> getAllPlantsData();

    @GET("plants/{name}")
    Call<String> getPlantData(@Path("name") String name);

    @GET("plants/{name}/timespan?from={from_milliseconds}&to={to_milliseconds}")
    Call<String> getPlantData(@Path("name") String name, @Query("from") long from_milliseconds, @Query("to")long to_milliseconds);

    @POST("plants/register")
    Call<String> registerPlant(@Body PlantAdd body);

    @DELETE("plants/{name}")
    Call<String> deletePlant(@Path("name") String name);

    @PUT("sensors/water")
    Call<String> waterPlant(@Body PlantWater name);

    @PUT("sensors/trigger")
    Call<String> triggerPlant(@Body PlantTriggers body);
}
