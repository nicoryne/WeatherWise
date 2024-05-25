package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentOnboardingFirstBinding;
import com.example.weatherwise.databinding.FragmentOnboardingSecondBinding;

public class OnboardingFragmentSecond extends Fragment {

    private final String DEBUG_TAG = "OnboardingFragmentSecond";

    private FragmentOnboardingSecondBinding binding;

    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnboardingSecondBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        handleNextButton();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleNextButton() {
        binding.btnNext.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_onboardingFragmentSecond2_to_boardingFragment));
    }
}