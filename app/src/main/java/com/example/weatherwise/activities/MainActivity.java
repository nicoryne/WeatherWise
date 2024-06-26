package com.example.weatherwise.activities;

import static com.example.weatherwise.manager.LocationManager.LOCATION_PERMISSION_REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weatherwise.R;
import com.example.weatherwise.api.DataCallback;
import com.example.weatherwise.manager.WeatherManager;
import com.example.weatherwise.databinding.ActivityMainBinding;
import com.example.weatherwise.manager.LocationManager;
import com.example.weatherwise.model.CurrentWeatherData;
import com.example.weatherwise.model.Health;
import com.example.weatherwise.viewmodels.HealthViewModel;
import com.example.weatherwise.viewmodels.HomeViewModel;
import com.example.weatherwise.worker.TemperatureReminderWorker;

import java.util.HashSet;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationManager.LocationUpdateListener {

    private final String DEBUG_TAG = "MainActivity";

    private ActivityMainBinding binding;
    private View root;
    private NavController mainNavController;
    private HashSet<Integer> bottomNavigationScreens;
    private HomeViewModel homeViewModel;
    private WeatherManager weatherManager;
    private LocationManager locationManager;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        setup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.startLocationUpdates(this);
            } else {
                showToast("Location permission denied");
            }
        }

        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                showToast("Call permission denied");
            }
        }
    }

    private void setup() {
        setupBottomNavigationSet();
        setupMainNavigationController();
        handleViewModel();
        handleBottomNav();
        handleFloatingPhoneButton();
        weatherManager = new WeatherManager();
        locationManager = LocationManager.getInstance();
        locationManager.initialize(this);
        locationManager.setLocationUpdateListener(this);
        locationManager.startLocationUpdates(this);
        createNotificationChannel();
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null) {
            Log.d(DEBUG_TAG, "Location updated: Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
            fetchWeather(location.getLatitude(), location.getLongitude(), "temperature_2m,relative_humidity_2m,is_day,precipitation,rain,wind_speed_10m");
        } else {
            Log.e(DEBUG_TAG, "Location is null");
        }
    }

    public void fetchWeather(double latitude, double longitude, String extension) {
        Log.d(DEBUG_TAG, "Fetching weather data for Latitude: " + latitude + ", Longitude: " + longitude);
        weatherManager.getCurrentWeatherData(latitude, longitude, extension, new DataCallback<CurrentWeatherData>() {
            @Override
            public void onCallback(CurrentWeatherData data) {
                homeViewModel.setCurrentWeatherDataMutableLiveData(new MutableLiveData<>(data));
                double currentTemperature = Objects.requireNonNull(homeViewModel.getCurrentWeatherLiveData().getValue()).getCurrentWeather().getTemperature();
                enqueueTemperatureWorker(currentTemperature);
                Log.d(DEBUG_TAG, "Weather data fetched successfully");
            }

            @Override
            public void onError(Throwable t) {
                Log.e(DEBUG_TAG, "Error fetching weather data", t);
            }
        });
    }

    private void setupBottomNavigationSet() {
        bottomNavigationScreens = new HashSet<>();
        bottomNavigationScreens.add(R.id.homeFragment);
        bottomNavigationScreens.add(R.id.profileFragment);
        bottomNavigationScreens.add(R.id.gameFragment);
        bottomNavigationScreens.add(R.id.healthFragment);
    }

    private void setupMainNavigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        mainNavController = Objects.requireNonNull(navHostFragment).getNavController();

        mainNavController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (bottomNavigationScreens.contains(navDestination.getId())) {
                showBottomNavBar();
                showFloatingPhoneButton();
            } else {
                hideBottomNavBar();
                hideFloatingPhoneButton();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void handleBottomNav() {
        binding.containerBottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    mainNavController.navigate(R.id.homeFragment);
                    break;
                case R.id.health:
                    mainNavController.navigate(R.id.healthFragment);
                    break;
                case R.id.games:
                    mainNavController.navigate(R.id.gameFragment);
                    break;
                case R.id.profile:
                    mainNavController.navigate(R.id.profileFragment);
                    break;
            }
            return true;
        });
    }

    private void handleFloatingPhoneButton() {
        binding.btnFloatingPhone.setOnClickListener(v -> makePhoneCall());
    }

    private void handleViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void makePhoneCall() {
        String phoneNumber = "tel:+639325377770";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        } else {
            startPhoneCall(phoneNumber);
        }
    }

    private void startPhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        startActivity(callIntent);
    }

    private void createNotificationChannel() {
        CharSequence name = "WeatherWiseReminderChannel";
        String description = "Channel for Weather Wise Notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("WeatherWise", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void enqueueTemperatureWorker(double temperature) {
        Data inputData = new Data.Builder()
                .putDouble("temperature", temperature)
                .build();

        OneTimeWorkRequest temperatureWorkRequest = new OneTimeWorkRequest.Builder(TemperatureReminderWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(this).enqueue(temperatureWorkRequest);
    }

    private void showBottomNavBar() {
        binding.containerBottomNav.setVisibility(View.VISIBLE);
    }

    private void hideBottomNavBar() {
        binding.containerBottomNav.setVisibility(View.GONE);
    }

    private void showFloatingPhoneButton() {
        binding.btnFloatingPhone.setVisibility(View.VISIBLE);
    }

    private void hideFloatingPhoneButton() {
        binding.btnFloatingPhone.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
