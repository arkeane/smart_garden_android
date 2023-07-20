package com.maiot.smart_garden_android.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.Plant;
import com.maiot.smart_garden_android.backend.SensorData;
import com.maiot.smart_garden_android.backend.service.Gravatar;
import com.maiot.smart_garden_android.backend.service.ServerCaller;
import com.maiot.smart_garden_android.backend.service.SmartGardenAPICalls;

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

    private ImageView ivPlantImage;

    private TextView tvPlantName;
    private TextView tvPlantDescription;
    private TextView tvPlantCreated;
    private TextView tvTime;
    private TextView tvMoisture;
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvLight;

    private GraphView moistureGraph;
    private GraphView temperatureGraph;
    private GraphView humidityGraph;
    private GraphView lightGraph;

    private ImageButton btnBack;
    private Button btnWater;
    private Button btnTriggers;

    public PlantViewFragment(String name) {
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_view, container, false);
    }

    public String getData(retrofit2.Retrofit retrofit) {
        long to = System.currentTimeMillis();
        long from = to - 1000 * 60 * 60 * 24;
        Call<ResponseBody> call = retrofit.create(SmartGardenAPICalls.class).getPlantData(this.name, from, to);
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

    public void setTvWithLatestData(SensorData[] sensorData, TextView tv, String dataType) {

        // if sensorData is empty, return
        if (sensorData.length == 0) {
            return;
        }
        // Get latest data from sensorData based on timestamp
        SensorData latestData = sensorData[0];
        for (SensorData sensorDatum : sensorData) {
            if (sensorDatum.getDate().after(latestData.getDate())) {
                latestData = sensorDatum;
            }
        }

        switch (dataType) {
            case "Moisture":
                tv.setText(String.format(Locale.US, "M: %d", (int) latestData.getMoisture()));
                break;
            case "Temperature":
                tv.setText(String.format(Locale.US, "T: %d°C", (int) latestData.getTemperature()));
                break;
            case "Humidity":
                tv.setText(String.format(Locale.US, "H: %d%%", (int) latestData.getHumidity()));
                break;
            case "Light":
                tv.setText(String.format(Locale.US, "L: %d", (int) latestData.getLight()));
                break;
            case "Time":
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                tv.setText(dateFormat.format(latestData.getDate()));
                break;
        }
    }

    public void setGraph(GraphView graph, SensorData[] sensorData, String graphTitle) {
        SimpleDateFormat dateFormatGraph = new SimpleDateFormat("HH:mm", Locale.US);
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        series.setDrawDataPoints(false);
        series.setDataPointsRadius(10);
        series.setThickness(10);
        series.setColor(getResources().getColor(R.color.nord14));
        for (SensorData sensorDatum : sensorData) {
            Date timestamp = sensorDatum.getDate();
            double data = 0.0;
            switch (graphTitle) {
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
                    return dateFormatGraph.format(new Date((long) value));
                } else {
                    return String.valueOf((int) value);
                }
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(24);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graph.getViewport().setMinX(new Date().getTime() - 1000 * 60 * 60 * 12);
        graph.getViewport().setMaxX(new Date().getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.setTitle(graphTitle);
        graph.addSeries(series);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivPlantImage = requireView().findViewById(R.id.ivPlantImage);

        tvPlantName = requireView().findViewById(R.id.tvPlantName);
        tvPlantDescription = requireView().findViewById(R.id.tvPlantDescription);
        tvPlantCreated = requireView().findViewById(R.id.tvPlantCreated);
        tvTime = requireView().findViewById(R.id.tvTime);
        tvMoisture = requireView().findViewById(R.id.tvMoisture);
        tvTemperature = requireView().findViewById(R.id.tvTemperature);
        tvHumidity = requireView().findViewById(R.id.tvHumidity);
        tvLight = requireView().findViewById(R.id.tvLight);

        moistureGraph = requireView().findViewById(R.id.gvMoistureGraph);
        temperatureGraph = requireView().findViewById(R.id.gvTemperatureGraph);
        humidityGraph = requireView().findViewById(R.id.gvHumidityGraph);
        lightGraph = requireView().findViewById(R.id.gvLightGraph);

        btnBack = requireView().findViewById(R.id.btnBackList);
        btnWater = requireView().findViewById(R.id.btnWater);
        btnTriggers = requireView().findViewById(R.id.btnTriggers);

        String url = getString(R.string.server);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ResponseBody> call = retrofit.create(SmartGardenAPICalls.class).getPlantInfo(this.name);
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

            Gravatar gravatar = new Gravatar("robohash", 200);
            Bitmap avatar = gravatar.getAvatar(this.name);
            if (avatar != null) {
                ivPlantImage.setImageBitmap(avatar);
            }
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

            setTvWithLatestData(sensorData, tvTime, "Time");
            setTvWithLatestData(sensorData, tvMoisture, "Moisture");
            setTvWithLatestData(sensorData, tvTemperature, "Temperature");
            setTvWithLatestData(sensorData, tvHumidity, "Humidity");
            setTvWithLatestData(sensorData, tvLight, "Light");

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
                Call<ResponseBody> call = retrofit.create(SmartGardenAPICalls.class).waterPlant(name);
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
                    Toast.makeText(getContext(), "Watering plant for 2 seconds", Toast.LENGTH_LONG).show();
                    btnWater.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnWater.setEnabled(true);
                        }
                    }, 2000);
                } else {
                    Toast.makeText(getContext(), "Error watering plant", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTriggers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TriggerFragment(name)).commit();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlantListFragment()).commit();
            }
        });
    }
}
