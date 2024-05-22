package com.example.weatherwise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.example.weatherwise.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";

    private ActivityLauncherBinding binding;

    private final int SPLASH_SCREEN_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void handleSplashScreen() {
        new Handler().postDelayed(() -> {

        }, SPLASH_SCREEN_DURATION);
    }
}