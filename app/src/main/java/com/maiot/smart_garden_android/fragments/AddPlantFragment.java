package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.support.PlantAdd;
import com.maiot.smart_garden_android.backend.SmartGardenService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPlantFragment extends Fragment {
    private Button btnInsertImage;
    private Button btnAddPlant;

    private EditText etPlantName;
    private EditText etPlantDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_editor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // per la scienza
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnInsertImage = getView().findViewById(R.id.btnInsertImage);
        btnAddPlant = getView().findViewById(R.id.btnAddPlant);

        etPlantName = getView().findViewById(R.id.etPlantName);
        etPlantDescription = getView().findViewById(R.id.etPlantDescription);

        String url = "http://10.10.10.112:4567/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SmartGardenService service = retrofit.create(SmartGardenService.class);

        btnInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plantName = etPlantName.getText().toString();
                String plantDescription = etPlantDescription.getText().toString();

                PlantAdd plant = new PlantAdd(plantName, plantDescription);

                Call<String> res = service.registerPlant(plant);
                try {
                    res.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Log.i("AddPlantFragment", "onClick: " + plant);
            }
        });
    }
}
