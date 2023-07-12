package com.maiot.smart_garden_android.backend;

import com.maiot.smart_garden_android.backend.support.PlantAdd;
import com.maiot.smart_garden_android.backend.support.PlantTriggers;
import com.maiot.smart_garden_android.backend.support.PlantWater;

import okhttp3.ResponseBody;
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
    Call<ResponseBody> getAllPlantsInfo();

    @GET("plants/names")
    Call<ResponseBody> getAllPlantsNames();

    @GET("plants/all")
    Call<ResponseBody> getAllPlantsData();

    @GET("plants/{name}/info")
    Call<ResponseBody> getPlantInfo(@Path("name") String name);

    @GET("plants/{name}")
    Call<ResponseBody> getPlantData(@Path("name") String name);

    @GET("plants/{name}/timespan?from={from_milliseconds}&to={to_milliseconds}")
    Call<ResponseBody> getPlantData(@Path("name") String name, @Query("from") long from_milliseconds, @Query("to") long to_milliseconds);

    @POST("plants/register")
    Call<ResponseBody> registerPlant(@Body PlantAdd body);

    @DELETE("plants/{name}")
    Call<ResponseBody> deletePlant(@Path("name") String name);

    @PUT("sensors/water")
    Call<ResponseBody> waterPlant(@Body PlantWater name);

    @PUT("sensors/trigger")
    Call<ResponseBody> triggerPlant(@Body PlantTriggers body);
}
