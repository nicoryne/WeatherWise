package com.example.weatherwise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.ActivityBoardingBinding;
import com.example.weatherwise.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "BoardingActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}