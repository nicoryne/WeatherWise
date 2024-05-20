package com.example.weatherwise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.weatherwise.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private final static String DEBUG_TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


}