package com.maiot.smart_garden_android.backend.service;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RequestHandler<T> implements Callable {

    private Call<T> call;
    private Response<T> response;

    public RequestHandler(Call<T> call) {
        this.call = call;
    }

    @Override
    public Response<T> call() {
        try {
            response = call.execute();

            return response;
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
