package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;

import com.example.weatherwise.databinding.FragmentHomeFitnessBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFitnessFragment extends Fragment implements SensorEventListener {

    private final String DEBUG_TAG = "HomeFitnessFragment";

    private FragmentHomeFitnessBinding binding;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Sensor accelerometerSensor;

    private int totalSteps = 0;
    private int previousTotalSteps = 0;
    private int currentSteps = 0;

    private static final String PREFS_NAME = "fitnessPrefs";
    private static final String STEPS_KEY = "previousTotalSteps";
    private static final String DATE_KEY = "lastSavedDate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreviousTotalSteps();
        checkIfNewDay();
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
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreviousTotalSteps();
        sensorManager.unregisterListener(this);
    }

    private void setup() {
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        binding.pbSteps.setProgress(currentSteps);
        setFitnessBubble();
    }

    @SuppressLint("SetTextI18n")
    private void setFitnessBubble() {
        binding.tvDailyGoal.setText("3 km");
        binding.tvSteps.setText("0");
        binding.tvStreak.setText("3");
        binding.pbSteps.setMax(800); // Set the maximum value for the progress bar
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            currentSteps = totalSteps - previousTotalSteps;
            binding.tvSteps.setText(String.valueOf(currentSteps));
            binding.pbSteps.setProgress(currentSteps);
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x * x + y * y + z * z);
            if (magnitude > 15) {
                binding.tvActivity.setText("Running");
            } else if (magnitude > 5) {
                binding.tvActivity.setText("Walking");
            } else {
                binding.tvActivity.setText("Stationary");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed here
    }

    private void loadPreviousTotalSteps() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getInt(STEPS_KEY, 0);
    }

    private void savePreviousTotalSteps() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(STEPS_KEY, previousTotalSteps);
        editor.putString(DATE_KEY, getCurrentDate());
        editor.apply();
    }

    private void checkIfNewDay() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastSavedDate = sharedPreferences.getString(DATE_KEY, "");
        String currentDate = getCurrentDate();

        if (!currentDate.equals(lastSavedDate)) {
            previousTotalSteps = totalSteps;
            savePreviousTotalSteps();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
