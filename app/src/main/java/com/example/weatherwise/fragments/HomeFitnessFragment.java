package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.weatherwise.databinding.FragmentHomeFitnessBinding;

public class HomeFitnessFragment extends Fragment implements SensorEventListener {

    private final String DEBUG_TAG = "HomeFitnessFragment";

    private FragmentHomeFitnessBinding binding;

    private SensorManager sensorManager;

    private Sensor stepSensor;

    private int totalSteps = 0;

    private int previousTotalSteps = 0;  // Initialize this to zero and manage persistence if needed

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeFitnessBinding.inflate(inflater, container, false);
        setup();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepSensor == null) {
            showToast("Error reading sensor");
            return;
        }
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void setup() {
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        setFitnessBubble();
    }

    @SuppressLint("SetTextI18n")
    private void setFitnessBubble() {
        binding.tvDistance.setText("3 km");
        binding.tvDailyGoal.setText("/ 800");
        binding.tvSteps.setText("0");
        binding.tvStreak.setText("3");
        binding.pbSteps.setMax(800);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - previousTotalSteps;
            binding.tvSteps.setText(String.valueOf(currentSteps));
            binding.pbSteps.setProgress(currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes here
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
