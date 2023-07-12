package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.Plant;
import com.maiot.smart_garden_android.backend.ServerCaller;
import com.maiot.smart_garden_android.backend.SmartGardenService;
import com.maiot.smart_garden_android.backend.support.PlantInfo;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantViewFragment extends Fragment {
    private final String name;

    private TextView tvPlantName;
    private TextView tvPlantDescription;
    private TextView tvPlantCreated;

    public PlantViewFragment(String name) {
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_view, container, false);
    }

    public String getData(retrofit2.Retrofit retrofit) {
        long to = System.currentTimeMillis();
        long from = to - 1000 * 60 * 60 * 12;
        Call<ResponseBody> call = retrofit.create(SmartGardenService.class).getPlantData(this.name, from, to);
        ServerCaller caller = new ServerCaller(call);
        caller.call();
        Response<ResponseBody> response = caller.getResponse();
        Integer responseCode = caller.getResponseCode();

        return "Data";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPlantName = getView().findViewById(R.id.tvPlantName);
        tvPlantDescription = getView().findViewById(R.id.tvPlantDescription);
        tvPlantCreated = getView().findViewById(R.id.tvPlantCreated);

        String url = "http://10.10.10.112:4567/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ResponseBody> call = retrofit.create(SmartGardenService.class).getPlantInfo(this.name);
        ServerCaller caller = new ServerCaller(call);
        caller.call();
        Response<ResponseBody> response = caller.getResponse();
        Integer responseCode = caller.getResponseCode();


        if (responseCode == 200) {
            tvPlantName.setText(this.name);

            String json = null;
            try {
                json = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Log.i("PlantViewFragment", json);

            PlantInfo plant = new PlantInfo(json);

            tvPlantDescription.setText(plant.getType());
            tvPlantCreated.setText(plant.getPlanted().toString());
        } else {
            tvPlantName.setText("Error");
            tvPlantDescription.setText("Error");
            tvPlantCreated.setText("Error");
        }

    }
}
