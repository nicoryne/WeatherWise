package com.example.weatherwise.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentOnboardingFirstBinding;

public class OnboardingFragmentFirst extends Fragment {

    private final String DEBUG_TAG = "OnboardingFragmentFirst";

    private FragmentOnboardingFirstBinding binding;

    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnboardingFirstBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        handleNextButton();
        handleSkipButton();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleNextButton() {
        binding.btnNext.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_onboardingFragmentFirst_to_onboardingFragmentSecond2);
        });
    }

    private void handleSkipButton() {
        binding.btnSkip.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_onboardingFragmentFirst_to_boardingFragment);
        });
    }
}