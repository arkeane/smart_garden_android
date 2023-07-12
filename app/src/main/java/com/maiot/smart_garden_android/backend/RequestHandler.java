package com.maiot.smart_garden_android.backend;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RequestHandler implements Callable {

    private Call<ResponseBody> call;
    private Response<ResponseBody> response;

    public RequestHandler(Call<ResponseBody> call) {
        this.call = call;
    }

    @Override
    public Response<ResponseBody> call() {
        try {
            response = call.execute();

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
