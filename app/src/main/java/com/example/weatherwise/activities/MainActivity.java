package com.example.weatherwise.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.weatherwise.MainViewModel;
import com.example.weatherwise.R;
import com.example.weatherwise.databinding.ActivityMainBinding;
import com.example.weatherwise.fragments.BoardingFragment;
import com.example.weatherwise.fragments.OnboardingFragmentSecond;
import com.example.weatherwise.fragments.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";

    private ActivityMainBinding binding;

    private View root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);
    }

    private void handleBottomNav() {
//        binding.containerBottomNav.setOnItemSelectedListener(item -> {
//            switch(item.getItemId()) {
//                case R.id.home:
//                    replaceFragment(new BoardingFragment());
//                    break;
//                case R.id.health:
//                    replaceFragment(new SplashScreenFragment());
//                    break;
//                case R.id.games:
//                    replaceFragment(new OnboardingFragmentSecond());
//                    break;
//            }
//            return true;
//        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

    public void showBottomNavBar() {
        binding.containerBottomNav.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavBar() {
        binding.containerBottomNav.setVisibility(View.GONE);
    }
}