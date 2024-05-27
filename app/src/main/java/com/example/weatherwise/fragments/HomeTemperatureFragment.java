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
import com.example.weatherwise.databinding.FragmentHomeTemperatureBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.example.weatherwise.model.CurrentWeatherData;
import com.example.weatherwise.viewmodels.HomeViewModel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HomeTemperatureFragment extends Fragment {

    private final String DEBUG_TAG = "HomeTemperatureFragment";

    private FragmentHomeTemperatureBinding binding;

    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeTemperatureBinding.inflate(getLayoutInflater());
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
        setTemperature(currentWeatherData);
        setDayTimeIcon(currentWeatherData);
        setDate();
    }


    @SuppressLint("SetTextI18n")
    private void setTemperature(CurrentWeatherData currentWeatherData) {
        binding.tvTemp.setText((int) Math.ceil(currentWeatherData.getCurrentWeather().getTemperature()) + "°");
    }

    @SuppressLint("SetTextI18n")
    private void setDayTimeIcon(CurrentWeatherData currentWeatherData) {
        if (currentWeatherData.getCurrentWeather().getIsDay() == 0) {
            binding.tvCondition.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.night, null), null, null, null);
            binding.tvCondition.setText("Night");
        } else {
            binding.tvCondition.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.sun, null), null, null, null);
            binding.tvCondition.setText("Day");
        }
        binding.tvCondition.setCompoundDrawablePadding(16);
    }

    private void setDate() {
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneId.of("GMT+8"));

        DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("E");
        String formattedDayOfWeek = currentDate.format(dayOfWeekFormatter).toUpperCase();

        int dayOfMonth = currentDate.getDayOfMonth();

        String formattedDate = formattedDayOfWeek + " " + dayOfMonth;

        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        String formattedMonthYear = currentDate.format(monthYearFormatter).toUpperCase();

        binding.tvDay.setText(formattedDate);
        binding.tvMonth.setText(formattedMonthYear);
    }
}