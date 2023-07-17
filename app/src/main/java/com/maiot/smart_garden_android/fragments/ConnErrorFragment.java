package com.maiot.smart_garden_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.maiot.smart_garden_android.R;

public class ConnErrorFragment extends Fragment {
    private Button btnTryAgain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.conn_error, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTryAgain = requireView().findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(v -> {
            PlantListFragment plantListFragment = new PlantListFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, plantListFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });
    }

}
