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
import com.example.weatherwise.databinding.FragmentSplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {

    private final String DEBUG_TAG = "SplashScreenFragment";

    private FragmentSplashScreenBinding binding;

    private View root;

    private final int SPLASH_SCREEN_DURATION = 3000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        startSplashScreen();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void startSplashScreen() {
        new Handler().postDelayed(() -> {
            Navigation.findNavController(root).navigate(R.id.action_splashScreenFragment_to_onboardingFragmentFirst);
        }, SPLASH_SCREEN_DURATION);
    }
}