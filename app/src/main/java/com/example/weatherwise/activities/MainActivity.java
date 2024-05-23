package com.example.weatherwise.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.ActivityMainBinding;

import java.util.HashSet;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";

    private ActivityMainBinding binding;

    private View root;

    private NavController mainNavController;

    private HashSet<Integer> bottomNavigationScreens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        setupBottomNavigationSet();
        setupMainNavigationController();
    }

    private void setupBottomNavigationSet() {
        bottomNavigationScreens = new HashSet<>();

        bottomNavigationScreens.add(R.id.homeFragment);
    }

    private void setupMainNavigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        mainNavController = Objects.requireNonNull(navHostFragment).getNavController();

        // Adding destination changed listener for bottom navigation bar
        mainNavController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if(bottomNavigationScreens.contains(navDestination.getId())) {
                showBottomNavBar();
            } else {
                hideBottomNavBar();
            }
        });
    }


    private void handleBottomNav() {
        binding.containerBottomNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.home:
                    break;
                case R.id.health:
                    break;
                case R.id.games:
                    break;
                case R.id.profile:
                    break;
            }
            return true;
        });
    }

    public void showBottomNavBar() {
        binding.containerBottomNav.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavBar() {
        binding.containerBottomNav.setVisibility(View.GONE);
    }
}