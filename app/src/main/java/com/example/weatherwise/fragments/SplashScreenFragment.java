package com.example.weatherwise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentBoardingBinding;
import com.example.weatherwise.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    private final String DEBUG_TAG = "SplashScreenFragment";

    private FragmentSplashScreenBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        return root;
    }
}