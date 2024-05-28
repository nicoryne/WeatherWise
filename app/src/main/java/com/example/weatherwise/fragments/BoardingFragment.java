package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentBoardingBinding;
import com.example.weatherwise.databinding.FragmentSplashScreenBinding;

public class BoardingFragment extends Fragment {

    private final String DEBUG_TAG = "BoardingFragment";

    private FragmentBoardingBinding binding;

    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBoardingBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        handleLoginButton();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleLoginButton() {
        binding.btnLogin.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_boardingFragment_to_signInFragment));

        binding.btnSignUp.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_boardingFragment_to_signUpFragment));
    }
}