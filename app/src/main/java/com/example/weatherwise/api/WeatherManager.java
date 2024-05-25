package com.example.weatherwise.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weatherwise.model.CurrentWeatherData;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherManager {

    private final String DEBUG_TAG = "WeatherManager";

    private WeatherRetrofit weatherRetrofit;

    private WeatherAPI weatherAPI;

    public WeatherManager() {
        this.weatherRetrofit = new WeatherRetrofit();
        weatherRetrofit.build();
        this.weatherAPI = weatherRetrofit.getRetrofit().create(WeatherAPI.class);
    }

    public void getCurrentWeatherData(double latitude, double longitude, String extension, DataCallback<CurrentWeatherData> weatherCallback) {
        weatherAPI.getData(latitude, longitude, extension).enqueue(new Callback<CurrentWeatherData>() {
            @Override
            public void onResponse(@NonNull Call<CurrentWeatherData> call, @NonNull Response<CurrentWeatherData> response) {
                if (response.isSuccessful() && response.body() == null) {
                    Log.e(DEBUG_TAG, "Response not successful: " + response.message());
                    return;
                }

                CurrentWeatherData data = response.body();

                if (Objects.requireNonNull(data).getCurrentWeather() == null) {
                    Log.e(DEBUG_TAG, "Current weather data is null");
                    return;
                }

                weatherCallback.onCallback(data);
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeatherData> call, @NonNull Throwable t) {
                Log.e(DEBUG_TAG, "Failed to fetch data", t);
            }
        });
    }



}
