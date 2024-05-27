package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherwise.R;
import com.example.weatherwise.adapter.HomeAdapter;
import com.example.weatherwise.model.CurrentWeatherData;
import com.example.weatherwise.databinding.FragmentHomeBinding;
import com.example.weatherwise.viewmodels.HomeViewModel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private final String DEBUG_TAG = "HomeFragment";

    private FragmentHomeBinding binding;

    private View root;

    private ArrayList<Fragment> fragments;

    private HomeAdapter homeAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        fragments = new ArrayList<>();
        fragments.add(new HomeTemperatureFragment());
        fragments.add(new HomeFitnessFragment());

        homeAdapter = new HomeAdapter(requireActivity(), fragments);
        binding.pagerStatus.setAdapter(homeAdapter);
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}