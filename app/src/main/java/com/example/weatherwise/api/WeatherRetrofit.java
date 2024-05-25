package com.example.weatherwise.api;

public class WeatherRetrofit extends RetrofitService {

    public WeatherRetrofit() {
        super("https://api.open-meteo.com/");
    }

}
