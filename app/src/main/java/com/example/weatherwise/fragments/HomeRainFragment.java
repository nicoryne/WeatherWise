package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentHomeRainBinding;
import com.example.weatherwise.model.CurrentWeatherData;
import com.example.weatherwise.viewmodels.HomeViewModel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HomeRainFragment extends Fragment {

    private final String DEBUG_TAG = "HomeRainFragment";

    private FragmentHomeRainBinding binding;

    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeRainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        setup();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setup() {
        HomeViewModel model = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        model.getCurrentWeatherLiveData().observe(getViewLifecycleOwner(), this::setWeatherBubble);
    }

    private void setWeatherBubble(CurrentWeatherData currentWeatherData) {
        setRainData(currentWeatherData);
    }


    @SuppressLint("SetTextI18n")
    private void setRainData(CurrentWeatherData currentWeatherData) {
        binding.tvHumidity.setText(String.valueOf(currentWeatherData.getCurrentWeather().getHumidity()));
        binding.tvPrecipitation.setText("Precipitation: " + currentWeatherData.getCurrentWeather().getPrecipitation() + "%");
        binding.tvWind.setText("Wind: " + currentWeatherData.getCurrentWeather().getWindSpeed() + "km/h");
        binding.layoutRain.setBackgroundResource(R.drawable.circle_rain);

        int color = getResources().getColor(R.color.rain_bg, requireContext().getTheme());
        binding.containerRain.setBackgroundColor(color);
    }
}