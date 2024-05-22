package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.weatherwise.databinding.FragmentOnboardingSecondBinding;

public class OnboardingFragmentSecond extends Fragment {

    private final String DEBUG_TAG = "OnboardingFragmentSecond";

    private FragmentOnboardingSecondBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnboardingSecondBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}