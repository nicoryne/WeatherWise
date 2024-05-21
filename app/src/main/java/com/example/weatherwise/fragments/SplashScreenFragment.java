package com.example.weatherwise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentBoardingBinding;
import com.example.weatherwise.databinding.FragmentSplashScreenBinding;
import com.example.weatherwise.util.MainController;

public class SplashScreenFragment extends Fragment {

    private final String DEBUG_TAG = "SplashScreenFragment";

    private FragmentSplashScreenBinding binding;

    private static final long SPLASH_SCREEN_DURATION = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        new Handler().postDelayed(() -> {

        }, SPLASH_SCREEN_DURATION);
        return root;
    }
}