package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.Plant;
import com.maiot.smart_garden_android.backend.service.ServerCaller;
import com.maiot.smart_garden_android.backend.service.SmartGardenService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPlantFragment extends Fragment {
    private ImageButton btnBack;
    private Button btnAddPlant;

    private EditText etPlantName;
    private EditText etPlantDescription;
    private TextView tvPlantCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_editor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = getView().findViewById(R.id.btnBackList);
        btnAddPlant = getView().findViewById(R.id.btnAddPlant);

        etPlantName = getView().findViewById(R.id.etPlantName);
        etPlantDescription = getView().findViewById(R.id.etPlantDescription);

        tvPlantCreated = getView().findViewById(R.id.tvPlantCreated);

        String url = getString(R.string.server);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SmartGardenService service = retrofit.create(SmartGardenService.class);

        btnAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plantName = etPlantName.getText().toString();
                String plantDescription = etPlantDescription.getText().toString();

                Plant plant = new Plant(plantName, plantDescription);

                Call<ResponseBody> call = service.registerPlant(plant);
                ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
                try {
                    caller.call();
                } catch (Exception e) {
                    caller.connError(AddPlantFragment.this);
                    return;
                }
                Response<ResponseBody> response = caller.getResponse();
                Integer responseCode = caller.getResponseCode();

                switch (responseCode) {
                    case 201:
                        tvPlantCreated.setText("Plant created!");
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlantViewFragment(plantName)).commit();
                        return;
                    case 409:
                        tvPlantCreated.setText("Plant already exists!");
                        return;
                    case 400:
                        tvPlantCreated.setText("Bad request!");
                        return;
                    case 500:
                        tvPlantCreated.setText("Internal server error!");
                        return;
                    default:
                        tvPlantCreated.setText("Unknown error!");
                }
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
