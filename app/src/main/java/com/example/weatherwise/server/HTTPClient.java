package com.example.weatherwise.server;

import com.example.weatherwise.model.User;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTTPClient {

    private final String DEBUG_TAG = "HTTPClient";

    private final HTTPClientService httpClientService;

    public HTTPClient(String url) {

        // initialize client
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        // build connection
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        httpClientService = retrofit.create(HTTPClientService.class);
    }

    public HTTPClientService getHttpClientService() {
        return httpClientService;
    }
}
