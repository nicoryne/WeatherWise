package com.example.weatherwise.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.sql.Timestamp;

public class User {

    @SerializedName("id")
    private int userId;

    @SerializedName("created_at")
    private Timestamp createdAt;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("gender")
    private String gender;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    @SerializedName("address")
    private String address;

    @SerializedName("zip")
    private int zip;

    @SerializedName("phone_number")
    private int phoneNumber;

    @SerializedName("birthdate")
    private Date birthdate;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public int getZip() {
        return zip;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public Date getBirthdate() {
        return birthdate;
    }
}
