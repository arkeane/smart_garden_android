package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.maiot.smart_garden_android.R;
import com.maiot.smart_garden_android.backend.PlantTrigger;
import com.maiot.smart_garden_android.backend.service.ServerCaller;
import com.maiot.smart_garden_android.backend.service.SmartGardenService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TriggerFragment extends Fragment {
    private final String name;

    private SeekBar seekbarHumidity;
    private SeekBar seekbarTemperature;
    private SeekBar seekbarLight;
    private SeekBar seekbarMoisture;

    private EditText editTextHumidity;
    private EditText editTextTemperature;
    private EditText editTextLight;
    private EditText editTextMoisture;

    private Button buttonUpdate;
    private Button buttonEnable;
    private Button buttonDisable;
    private ImageButton buttonBack;

    public TriggerFragment(String name) {
        this.name = name;
    }

    public void bindSbToEt(SeekBar sb, EditText et) {
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                et.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        // bind the EditText to the SeekBar
        et.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String text = et.getText().toString();
                // check if the EditText is empty or is not a number
                if (text.isEmpty() || !text.matches("\\d+(\\.\\d+)?")) {
                    Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    et.setText("0");
                    return;
                }
                Float value = Float.parseFloat(text);
                sb.setProgress(value.intValue());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.triggers_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekbarHumidity = view.findViewById(R.id.sliderHumidity);
        seekbarTemperature = view.findViewById(R.id.sliderTemperature);
        seekbarLight = view.findViewById(R.id.sliderBrightness);
        seekbarMoisture = view.findViewById(R.id.sliderMoisture);

        editTextHumidity = view.findViewById(R.id.etHumidity);
        editTextTemperature = view.findViewById(R.id.etTemperature);
        editTextLight = view.findViewById(R.id.etBrightness);
        editTextMoisture = view.findViewById(R.id.etMoisture);

        buttonUpdate = view.findViewById(R.id.btnUpdateTrigger);
        buttonEnable = view.findViewById(R.id.btnEnableTrigger);
        buttonDisable = view.findViewById(R.id.btnDisableTrigger);
        buttonBack = view.findViewById(R.id.btnBackView);

        bindSbToEt(seekbarHumidity, editTextHumidity);
        bindSbToEt(seekbarTemperature, editTextTemperature);
        bindSbToEt(seekbarLight, editTextLight);
        bindSbToEt(seekbarMoisture, editTextMoisture);

        String url = getString(R.string.server);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ResponseBody> call = retrofit.create(SmartGardenService.class).getPlantTriggers(name);
        ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
        try {
            caller.call();
        } catch (Exception e) {
            caller.connError(this);
            return;
        }

        Response<ResponseBody> response = caller.getResponse();
        Integer responseCode = caller.getResponseCode();

        if (responseCode != 200) {
            return;
        }

        String json = null;
        try {
            json = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.i("TriggerFragment", json);

        PlantTrigger plantTrigger = new PlantTrigger(json);

        boolean enabled = plantTrigger.getStatus();
        int humidity_trigger = plantTrigger.getHumidity().intValue();
        int temperature_trigger = plantTrigger.getTemperature().intValue();
        int light_trigger = plantTrigger.getLight().intValue();
        int moisture_trigger = plantTrigger.getMoisture().intValue();

        if (enabled) {
            buttonEnable.setEnabled(false);
            buttonDisable.setEnabled(true);
        } else {
            buttonEnable.setEnabled(true);
            buttonDisable.setEnabled(false);
        }

        seekbarHumidity.setProgress(humidity_trigger);
        seekbarTemperature.setProgress(temperature_trigger);
        seekbarLight.setProgress(light_trigger);
        seekbarMoisture.setProgress(moisture_trigger);

        editTextHumidity.setText(Integer.toString(humidity_trigger));
        editTextTemperature.setText(Integer.toString(temperature_trigger));
        editTextLight.setText(Integer.toString(light_trigger));
        editTextMoisture.setText(Integer.toString(moisture_trigger));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlantViewFragment(name)).commit();
            }
        });

        // on click listeners for the buttons
        buttonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plantTrigger.setStatus(true);

                buttonEnable.setEnabled(false);
                buttonDisable.setEnabled(true);
            }
        });

        buttonDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plantTrigger.setStatus(false);

                buttonEnable.setEnabled(true);
                buttonDisable.setEnabled(false);
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plantTrigger.setHumidity(Float.parseFloat(editTextHumidity.getText().toString()));
                plantTrigger.setTemperature(Float.parseFloat(editTextTemperature.getText().toString()));
                plantTrigger.setLight(Float.parseFloat(editTextLight.getText().toString()));
                plantTrigger.setMoisture(Float.parseFloat(editTextMoisture.getText().toString()));

                Log.i("TriggerFragment", plantTrigger.toJson());

                Call<ResponseBody> call = retrofit.create(SmartGardenService.class).triggerPlant(plantTrigger);
                ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
                try {
                    caller.call();
                } catch (Exception e) {
                    caller.connError(TriggerFragment.this);
                    return;
                }

                Response<ResponseBody> response = caller.getResponse();
                Integer responseCode = caller.getResponseCode();
                Log.i("TriggerFragment", responseCode.toString());

                if (responseCode != 200) {
                    return;
                }

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlantViewFragment(name)).commit();
            }
        });

    }
}
