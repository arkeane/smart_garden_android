package com.maiot.smart_garden_android.backend.service;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.fragments.ConnErrorFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Response;

public class ServerCaller<T> {

    Call<T> call;
    Future fut;
    Integer responseCode;
    Response<T> response;
    private final ExecutorService executor;


    public ServerCaller(Call<T> call) {
        this.executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        this.call = call;
    }

    public void call() {
        fut = executor.submit(new RequestHandler<T>(call));
        try {
            response = (Response<T>) fut.get();
            responseCode = response.code();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void connError(Fragment context) {
        context.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConnErrorFragment()).commit();
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public Response<T> getResponse() {
        return response;
    }

}
