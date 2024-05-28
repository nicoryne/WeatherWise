package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherwise.R;
import com.example.weatherwise.adapter.HomeAdapter;
import com.example.weatherwise.adapter.NotificationAdapter;
import com.example.weatherwise.databinding.FragmentHomeBinding;
import com.example.weatherwise.model.Notification;
import com.google.type.DateTime;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private final String DEBUG_TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private ArrayList<Fragment> fragments;
    private ArrayList<Notification> notifications;
    private HomeAdapter homeAdapter;
    private NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        fragments = new ArrayList<>();
        fragments.add(new HomeTemperatureFragment());
        fragments.add(new HomeRainFragment());

        notifications = new ArrayList<>();
        DateTime dateTime = DateTime.newBuilder().setSeconds((int) System.currentTimeMillis() / 1000).build();
        notifications.add(new Notification("Rehydrate yourself", "Every 2 hours", R.drawable.water, dateTime));
        notifications.add(new Notification("Rehydrate yourself", "Every 2 hours", R.drawable.water, dateTime));
        notifications.add(new Notification("Rehydrate yourself", "Every 2 hours", R.drawable.water, dateTime));

        homeAdapter = new HomeAdapter(requireActivity(), fragments);
        notificationAdapter = new NotificationAdapter(requireContext(), notifications);

        binding.pagerStatus.setAdapter(homeAdapter);
        binding.rvNotifications.setAdapter(notificationAdapter);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
