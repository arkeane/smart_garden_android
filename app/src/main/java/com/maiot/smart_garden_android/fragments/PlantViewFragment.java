package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
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

    private Button btnWater;

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

    public void setGraph(GraphView graph, SensorData[] sensorData, String graphTitle) {
        SimpleDateFormat dateFormatGraph = new SimpleDateFormat("HH:mm", Locale.US);
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        series.setDrawDataPoints(true);
        series.setColor(getResources().getColor(R.color.nord14));
        series.setDataPointsRadius(10);
        series.setThickness(8);
        for (SensorData sensorDatum : sensorData) {
            Date timestamp = sensorDatum.getDate();
            double data = 0.0;
            switch (graphTitle){
                case "Moisture":
                    data = sensorDatum.getMoisture();
                    break;
                case "Temperature":
                    data = sensorDatum.getTemperature();
                    break;
                case "Humidity":
                    data = sensorDatum.getHumidity();
                    break;
                case "Light":
                    data = sensorDatum.getLight();
                    break;
            }

            series.appendData(new DataPoint(timestamp, data), true, 100);
        }
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return dateFormatGraph.format(new Date((long) value));
                } else {
                    return String.valueOf((int) value);
                }
            }
        });
        graph.setTitle(graphTitle);
        graph.addSeries(series);
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

        btnWater = requireView().findViewById(R.id.btnWater);

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

            setGraph(moistureGraph, sensorData, "Moisture");
            setGraph(temperatureGraph, sensorData, "Temperature");
            setGraph(humidityGraph, sensorData, "Humidity");
            setGraph(lightGraph, sensorData, "Light");
        } else {
            tvPlantName.setText("Error");
            tvPlantDescription.setText("Error");
            tvPlantCreated.setText("Error");
        }

        // on click listener for the water button
        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = retrofit.create(SmartGardenService.class).waterPlant(name);
                ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
                try {
                    caller.call();
                } catch (Exception e) {
                    caller.connError(PlantViewFragment.this);
                    return;
                }
                Response<ResponseBody> response = caller.getResponse();
                Integer responseCode = caller.getResponseCode();

                if (responseCode == 200) {
                    btnWater.setText("Watering...");
                    btnWater.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnWater.setText("Water");
                            btnWater.setEnabled(true);
                        }
                    }, 30000);
                } else {
                    Toast.makeText(getContext(), "Error watering plant", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
