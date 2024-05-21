package com.example.weatherwise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentBoardingBinding;
public class BoardingFragment extends Fragment {

    private final String DEBUG_TAG = "BoardingFragment";

    private FragmentBoardingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBoardingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        return root;
    }
}