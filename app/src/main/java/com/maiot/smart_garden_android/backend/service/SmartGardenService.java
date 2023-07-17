package com.maiot.smart_garden_android.backend.service;

import com.maiot.smart_garden_android.backend.Plant;
import com.maiot.smart_garden_android.backend.PlantTrigger;

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

    @GET("sensors/triggers/{name}")
    Call<ResponseBody> getPlantTriggers(@Path("name") String name);

    @GET("plants/{name}/timespan")
    Call<ResponseBody> getPlantData(@Path("name") String name, @Query("from") long from_milliseconds, @Query("to") long to_milliseconds);

    @POST("plants/register")
    Call<ResponseBody> registerPlant(@Body Plant body);

    @DELETE("plants/{name}")
    Call<ResponseBody> deletePlant(@Path("name") String name);

    @PUT("sensors/water")
    Call<ResponseBody> waterPlant(@Body String name);

    @PUT("sensors/triggers")
    Call<ResponseBody> triggerPlant(@Body PlantTrigger body);

    @GET("avatar/{name}")
    Call<ResponseBody> getAvatar(@Path("name") String name, @Query("s") int size, @Query("d") String type);
}
