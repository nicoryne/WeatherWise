package com.example.weatherwise.server;

import com.example.weatherwise.model.User;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HTTPClientService {
    @FormUrlEncoded
    @POST("user/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("user/register")
    Call<User> register (
            @Field("username") String username,
            @Field("password") String password,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("gender") String gender,
            @Field("country") String country,
            @Field("city") String city,
            @Field("address") String address,
            @Field("zip") int zip,
            @Field("phone_number") int phoneNumber,
            @Field("birthdate") Date birthdate
    );
}
