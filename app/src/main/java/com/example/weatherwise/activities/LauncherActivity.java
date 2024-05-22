package com.example.weatherwise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.weatherwise.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "LauncherActivity";

    private ActivityLauncherBinding binding;

    private final int SPLASH_SCREEN_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handleSplashScreen();
    }

    private void handleSplashScreen() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LauncherActivity.this, BoardingActivity.class);
            startActivity(intent);
        }, SPLASH_SCREEN_DURATION);
    }
}