package com.example.weatherwise.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import android.os.Bundle;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.ActivityBoardingBinding;
import com.example.weatherwise.databinding.ActivityLauncherBinding;
import com.example.weatherwise.fragments.OnboardingFragmentFirst;

public class BoardingActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "BoardingActivity";

    private ActivityBoardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}