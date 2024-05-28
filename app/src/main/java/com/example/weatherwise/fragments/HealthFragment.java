package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentHealthBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HealthFragment extends Fragment {

    private final String DEBUG_TAG = "TemplateFragment";

    private FragmentHealthBinding binding;

    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        setup();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setup() {
        binding.btnLocate.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.mapFragment));
        binding.npHour.setMaxValue(24);
        binding.npMinute.setMaxValue(60);
        binding.btnSaveTime.setOnClickListener(v -> saveTime());
        binding.btnReset.setOnClickListener(v -> resetTime());
    }

    private void resetTime() {
        binding.npHour.setValue(0);
        binding.npMinute.setValue(0);
    }

    private void saveTime() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference userRef = firestore.collection("hydrate_time").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        Map<String, Object> hydrateTimeMap = new HashMap<>();
        hydrateTimeMap.put("hours", binding.npHour.getValue());
        hydrateTimeMap.put("minutes", binding.npMinute.getValue());
        userRef.update(hydrateTimeMap).addOnSuccessListener(v -> showToast("Time saved successfully!"));

    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}