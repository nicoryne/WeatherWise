package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherwise.R;
import com.example.weatherwise.data.CurrentWeatherData;
import com.example.weatherwise.databinding.FragmentHomeBinding;
import com.example.weatherwise.util.RetrofitModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private final String DEBUG_TAG = "HomeFragment";

    private FragmentHomeBinding binding;

    private View root;

    private RetrofitModel retrofitModel;

    private CurrentWeatherData currentWeatherData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        setupRetroFit();
        getWeatherData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRetroFit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitModel = retrofit.create(RetrofitModel.class);
    }

    private void getWeatherData() {
        retrofitModel.getData(10.2945, 123.8811, "temperature_2m,is_day").enqueue(new Callback<CurrentWeatherData>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CurrentWeatherData> call, @NonNull Response<CurrentWeatherData> response) {

                if (response.isSuccessful() && response.body() == null) {
                    Log.e("API_ERROR", "Response not successful: " + response.message());
                    return;
                }

                CurrentWeatherData data = response.body();

                if (Objects.requireNonNull(data).getCurrentWeather() == null) {
                    Log.e("API_ERROR", "Current weather data is null");
                    return;
                }

                currentWeatherData = data;
                setTemperature();
                setDayTimeIcon();
                setDate();
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeatherData> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setTemperature() {
        binding.tvTemp.setText((int) Math.ceil(currentWeatherData.getCurrentWeather().getTemperature()) + "°");
    }

    @SuppressLint("SetTextI18n")
    private void setDayTimeIcon() {
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
        String formattedDayOfWeek = currentDate.format(dayOfWeekFormatter);

        int dayOfMonth = currentDate.getDayOfMonth();

        String formattedDate = formattedDayOfWeek + " " + dayOfMonth;

        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        String formattedMonthYear = currentDate.format(monthYearFormatter);

        binding.tvDay.setText(formattedDate);
        binding.tvMonth.setText(formattedMonthYear);
    }

}