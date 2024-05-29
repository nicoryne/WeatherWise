package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentHealthBinding;
import com.example.weatherwise.model.Health;
import com.example.weatherwise.model.HydrationSetting;
import com.example.weatherwise.viewmodels.HealthViewModel;
import com.example.weatherwise.worker.HydrationReminderWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HealthFragment extends Fragment implements SensorEventListener {

    private final String DEBUG_TAG = "HealthFragment";

    private FragmentHealthBinding binding;
    private View root;
    private HealthViewModel healthViewModel;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int totalSteps = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(inflater, container, false);
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
        healthViewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
        healthViewModel.getHydrationSettingLiveData().observe(getViewLifecycleOwner(), this::setHydrationSettingData);
        healthViewModel.getHealthLiveData().observe(getViewLifecycleOwner(), this::setHealthSettingsData);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (stepSensor == null) {
                showToast("Step sensor unavailable!");
            }
        }
        binding.iconWater.setOnClickListener(v -> {
            String[] waterLevels = {"240ml", "350ml", "470ml", "600ml"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setItems(waterLevels, (dialog, which) -> {
                double waterConsumption = 0.0;
                switch (which) {
                    case 0:
                        waterConsumption = 0.240;
                        break;
                    case 1:
                        waterConsumption = 0.350;
                        break;
                    case 2:
                        waterConsumption = 0.470;
                        break;
                    case 3:
                        waterConsumption = 0.600;
                        break;
                }

                Health currentHealth = healthViewModel.getHealthLiveData().getValue();
                if (currentHealth != null) {
                    double currentWaterConsumption = currentHealth.getWaterConsumption();
                    double newWaterConsumption = currentWaterConsumption + waterConsumption;
                    healthViewModel.updateWaterConsumption(newWaterConsumption);
                    updateWaterConsumptionInFirestore(newWaterConsumption);
                }
            });
            builder.show();
        });
    }

    private void updateWaterConsumptionInFirestore(double waterConsumption) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference userRef = firestore.collection("health").document(userId);

        Map<String, Object> healthMap = new HashMap<>();
        healthMap.put("waterConsumption", waterConsumption);

        userRef.update(healthMap)
                .addOnSuccessListener(aVoid -> Log.d(DEBUG_TAG, "Water consumption updated successfully in Firestore"))
                .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Error updating water consumption in Firestore", e));
    }

    private void resetTime() {
        binding.npHour.setValue(0);
        binding.npMinute.setValue(0);
    }

    private void setHydrationSettingData(HydrationSetting hydrationSettingData) {
        binding.npHour.setValue(hydrationSettingData.getHydrationHour());
        binding.npMinute.setValue(hydrationSettingData.getHydrationMinute());
    }

    @SuppressLint("SetTextI18n")
    private void setHealthSettingsData(Health health) {
        binding.tvSteps.setText(String.valueOf(health.getSteps()));
        binding.tvDistance.setText(String.valueOf((health.getSteps() / 1000.0) * 0.74));
        binding.tvWater.setText(health.getWaterConsumption() + " l");
        setHydrationLevel(health.getWaterConsumption());
    }

    @SuppressLint("SetTextI18n")
    private void setHydrationLevel(double waterConsumption) {
        String message = null;

        if (waterConsumption >= 0.0 && waterConsumption <= 1.0) {
           message = "Bad";
       } else if (waterConsumption > 1.0 && waterConsumption <= 2.0) {
           message = "Good";
       } else if (waterConsumption >= 2.0) {
           message = "Excellent";
       }

       binding.tvHydration.setText(message);
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
        healthViewModel.getHydrationSettingLiveData().observe(getViewLifecycleOwner(), hydrationSetting -> {
            if (hydrationSetting != null) {
                long interval = TimeUnit.HOURS.toMillis(hydrationSetting.getHydrationHour()) +
                        TimeUnit.MINUTES.toMillis(hydrationSetting.getHydrationMinute());

                WorkRequest hydrationWorkRequest = new PeriodicWorkRequest.Builder(HydrationReminderWorker.class, interval, TimeUnit.MILLISECONDS)
                        .build();

                WorkManager.getInstance(requireContext()).enqueue(hydrationWorkRequest);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (stepSensor != null) {
            sensorManager.unregisterListener(this);
        }
        updateStepsInFirebase();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            totalSteps = (int) event.values[0];
            binding.tvSteps.setText(String.valueOf(totalSteps));
            saveStepsLocally(totalSteps);
            updateStepsInFirebase();
        }
    }

    private void saveStepsLocally(int steps) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("steps", steps);
        editor.apply();
    }

    private int getLocalSteps() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("steps", 0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed
    }

    private void updateStepsInFirebase() {
        int steps = getLocalSteps();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference userRef = firestore.collection("health").document(userId);

        Map<String, Object> healthMap = new HashMap<>();
        healthMap.put("steps", steps);

        userRef.update(healthMap)
                .addOnSuccessListener(aVoid -> Log.d(DEBUG_TAG, "Steps updated successfully in Firebase"))
                .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Error updating steps in Firebase", e));
    }
}
