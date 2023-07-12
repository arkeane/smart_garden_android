package com.maiot.smart_garden_android.backend;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ServerCaller {

    Call<ResponseBody> call;
    Future fut;
    Integer responseCode;
    Response<ResponseBody> response;
    private final ExecutorService executor;


    public ServerCaller(Call<ResponseBody> call) {
        this.executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        this.call = call;
    }

    public void call() {
        fut = executor.submit(new RequestHandler(call));
        try {
            response = (Response<ResponseBody>) fut.get();
            responseCode = response.code();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public Response<ResponseBody> getResponse() {
        return response;
    }

}
