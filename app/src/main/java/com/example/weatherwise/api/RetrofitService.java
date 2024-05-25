package com.example.weatherwise.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private final String url;

    private Retrofit retrofit;

    public RetrofitService(String url) {
        this.url = url;
    }

    public void build() {
        if(retrofit != null || url == null) return;

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
