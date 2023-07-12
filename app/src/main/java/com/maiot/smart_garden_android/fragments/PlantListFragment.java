package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.service.ServerCaller;
import com.maiot.smart_garden_android.backend.service.SmartGardenService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantListFragment extends Fragment {
    private TextView tvPlantListTitle;
    private ListView lvPlantsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPlantListTitle = getView().findViewById(R.id.tvPlantListTitle);
        lvPlantsList = getView().findViewById(R.id.lvPlantsList);

        String url = "http://10.10.10.112:4567/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ResponseBody> call = retrofit.create(SmartGardenService.class).getAllPlantsNames();
        ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
        try {
            caller.call();
        } catch (Exception e) {
            caller.connError(this);
            return;
        }

        Response<ResponseBody> response = caller.getResponse();
        int responseCode = response.code();


        if (responseCode == 200) {
            String[] plants;
            String json;
            try {
                json = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Log.i("json", json);
            plants = new Gson().fromJson(json, String[].class);

            java.util.Arrays.sort(plants);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_plant_list, R.id.tvItemPlantName, plants);
            lvPlantsList.setAdapter(adapter);
        } else if (responseCode >= 500) {
            tvPlantListTitle.setText("Server error");
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddPlantFragment()).commit();
        }

        lvPlantsList.setOnItemClickListener((p1, p2, i, p4) -> {
            String plantName = (String) lvPlantsList.getItemAtPosition(i);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlantViewFragment(plantName)).commit();
        });
    }
}
