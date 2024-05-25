package com.example.weatherwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.ActivityMainBinding;
import com.example.weatherwise.model.CurrentWeatherData;
import com.example.weatherwise.viewmodels.HomeViewModel;
import com.example.weatherwise.api.WeatherAPI;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.HashSet;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";

    private ActivityMainBinding binding;

    private View root;

    private NavController mainNavController;

    private HashSet<Integer> bottomNavigationScreens;

    private HomeViewModel homeViewModel;

    private WeatherAPI weatherAPI;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private CurrentWeatherData currentWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        setupBottomNavigationSet();
        setupMainNavigationController();
        handleViewModel();
        handleBottomNav();
        setupRetroFit();
        getWeatherData();
    }

    private void handleViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setupRetroFit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherAPI = retrofit.create(WeatherAPI.class);
    }

    private void getWeatherData() {
        weatherAPI.getData(10.2945, 123.8811, "temperature_2m,is_day").enqueue(new Callback<CurrentWeatherData>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CurrentWeatherData> call, @NonNull Response<CurrentWeatherData> response) {

                if (response.isSuccessful() && response.body() == null) {
                    Log.e("API_ERROR", "Response not successful: " + response.message());
                    return;
                }

                CurrentWeatherData data = response.body();

                if (Objects.requireNonNull(data).getCurrentWeather() == null) {
                    Log.e("API_ERROR", "Current weather data is null");
                    return;
                }

                homeViewModel.setCurrentWeatherDataMutableLiveData(new MutableLiveData<>(data));
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeatherData> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    private void setupBottomNavigationSet() {
        bottomNavigationScreens = new HashSet<>();

        bottomNavigationScreens.add(R.id.homeFragment);
        bottomNavigationScreens.add(R.id.profileFragment);
    }

    private void setupMainNavigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        mainNavController = Objects.requireNonNull(navHostFragment).getNavController();

        // Adding destination changed listener for bottom navigation bar
        mainNavController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if(bottomNavigationScreens.contains(navDestination.getId())) {
                showBottomNavBar();
                showFloatingPhoneButton();
            } else {
                hideBottomNavBar();
                hideFloatingPhoneButton();
            }
        });
    }


    private void handleBottomNav() {
        // TODO
        binding.containerBottomNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.home:
                    mainNavController.navigate(R.id.homeFragment);
                    break;
                case R.id.health:
                    break;
                case R.id.games:
                    break;
                case R.id.profile:
                    mainNavController.navigate(R.id.profileFragment);
                    break;
            }
            return true;
        });
    }

    private void handleFloatingPhoneButton() {
        // TODO
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


}