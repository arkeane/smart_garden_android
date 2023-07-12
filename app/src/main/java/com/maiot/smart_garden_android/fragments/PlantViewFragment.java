package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.Plant;
import com.maiot.smart_garden_android.backend.SensorData;
import com.maiot.smart_garden_android.backend.service.ServerCaller;
import com.maiot.smart_garden_android.backend.service.SmartGardenService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private GraphView moistureGraph;
    private GraphView temperatureGraph;
    private GraphView humidityGraph;
    private GraphView lightGraph;

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
        ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
        try {
            caller.call();
        } catch (Exception e) {
            caller.connError(this);
            return null;
        }
        Response<ResponseBody> response = caller.getResponse();
        Integer responseCode = caller.getResponseCode();

        if (responseCode != 200) {
            return null;
        }

        String json = null;
        try {
            json = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPlantName = requireView().findViewById(R.id.tvPlantName);
        tvPlantDescription = requireView().findViewById(R.id.tvPlantDescription);
        tvPlantCreated = requireView().findViewById(R.id.tvPlantCreated);

        moistureGraph = requireView().findViewById(R.id.gvMoistureGraph);
        temperatureGraph = requireView().findViewById(R.id.gvTemperatureGraph);
        humidityGraph = requireView().findViewById(R.id.gvHumidityGraph);
        lightGraph = requireView().findViewById(R.id.gvLightGraph);

        String url = "http://10.10.10.112:4567/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ResponseBody> call = retrofit.create(SmartGardenService.class).getPlantInfo(this.name);
        ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
        try {
            caller.call();
        } catch (Exception e) {
            caller.connError(this);
            return;
        }
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

            Plant plant = new Plant(json);

            tvPlantDescription.setText(plant.getType());
            tvPlantCreated.setText(plant.getPlanted().toString());

            String data = getData(retrofit);
            SensorData[] sensorData;

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);
            Gson gson = new GsonBuilder()
                    .setDateFormat(dateFormat.toPattern())
                    .create();
            sensorData = gson.fromJson(data, SensorData[].class);

            moistureGraph.removeAllSeries();
            LineGraphSeries<DataPoint> moisture_series = new LineGraphSeries<>();
            moisture_series.setDrawDataPoints(true);
            moisture_series.setColor(getResources().getColor(R.color.nord14));
            moisture_series.setDataPointsRadius(10);
            moisture_series.setThickness(8);
            for (SensorData sensorDatum : sensorData) {
                Date timestamp = sensorDatum.getDate();
                Float moisture = sensorDatum.getMoisture();
                moisture_series.appendData(new DataPoint(timestamp, moisture), true, 100);
            }
            moistureGraph.addSeries(moisture_series);


        } else {
            tvPlantName.setText("Error");
            tvPlantDescription.setText("Error");
            tvPlantCreated.setText("Error");
        }

    }
}
