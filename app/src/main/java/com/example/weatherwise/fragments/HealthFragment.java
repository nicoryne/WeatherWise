package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.weatherwise.R;
import com.example.weatherwise.activities.MainActivity;
import com.example.weatherwise.databinding.FragmentHealthBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.example.weatherwise.model.HydrationSetting;
import com.example.weatherwise.viewmodels.HealthViewModel;
import com.example.weatherwise.viewmodels.ProfileViewModel;
import com.example.weatherwise.worker.HydrationReminderWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HealthFragment extends Fragment {

    private final String DEBUG_TAG = "HealthFragment";

    private FragmentHealthBinding binding;
    private View root;
    private HealthViewModel model;

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
        model = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
        model.getHydrationSettingLiveData().observe(getViewLifecycleOwner(), this::setHydrationSettingData);
    }

    private void resetTime() {
        binding.npHour.setValue(0);
        binding.npMinute.setValue(0);
    }

    private void setHydrationSettingData(HydrationSetting hydrationSettingData) {
        binding.npHour.setValue(hydrationSettingData.getHydrationHour());
        binding.npMinute.setValue(hydrationSettingData.getHydrationMinute());
    }

    private void saveTime() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference userRef = firestore.collection("hydration_setting").document(userId);

        Map<String, Object> hydrateTimeMap = new HashMap<>();
        hydrateTimeMap.put("hydrationHour", binding.npHour.getValue());
        hydrateTimeMap.put("hydrationMinute", binding.npMinute.getValue());

        userRef.set(hydrateTimeMap)
                .addOnSuccessListener(aVoid -> {
                    showToast("Time saved successfully!");
                    scheduleHydrationReminders();
                })
                .addOnFailureListener(e -> showToast("Failed to save time"));
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void scheduleHydrationReminders() {
        model.getHydrationSettingLiveData().observe(getViewLifecycleOwner(), hydrationSetting -> {
            if (hydrationSetting != null) {
                long interval = TimeUnit.HOURS.toMillis(hydrationSetting.getHydrationHour()) +
                        TimeUnit.MINUTES.toMillis(hydrationSetting.getHydrationMinute());

                WorkRequest hydrationWorkRequest = new PeriodicWorkRequest.Builder(HydrationReminderWorker.class, interval, TimeUnit.MILLISECONDS)
                        .build();

                WorkManager.getInstance(requireContext()).enqueue(hydrationWorkRequest);
            }
        });
    }
}