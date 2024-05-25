package com.example.weatherwise.api;

import com.example.weatherwise.model.CurrentWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("/v1/forecast")
    Call<CurrentWeatherData> getData(@Query("latitude") double latitude,
                                     @Query("longitude") double longitude,
                                     @Query("current") String current);
}
