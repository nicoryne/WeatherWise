package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private final String DEBUG_TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private ArrayList<Fragment> fragments;
    private ArrayList<Notification> notifications;
    private HomeAdapter homeAdapter;
    private NotificationAdapter notificationAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        fragments = new ArrayList<>();
        fragments.add(new HomeTemperatureFragment());
        fragments.add(new HomeRainFragment());

        notifications = new ArrayList<>();
        homeAdapter = new HomeAdapter(requireActivity(), fragments);
        notificationAdapter = new NotificationAdapter(requireContext(), notifications);

        binding.pagerStatus.setAdapter(homeAdapter);
        binding.rvNotifications.setAdapter(notificationAdapter);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        listenForNotifications();

        return binding.getRoot();
    }

    private void listenForNotifications() {
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)  // Limit to recent 10 notifications
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(DEBUG_TAG, "Error listening for notifications", e);
                            return;
                        }

                        notifications.clear();
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED ||
                                    dc.getType() == DocumentChange.Type.MODIFIED ||
                                    dc.getType() == DocumentChange.Type.REMOVED) {
                                DocumentSnapshot document = dc.getDocument();
                                String message = document.getString("message");
                                long timestamp = document.getLong("timestamp");
                                DateTime dateTime = DateTime.newBuilder().setSeconds((int) (timestamp / 1000)).build();
                                Notification notification = new Notification(message, "Rehydrate yourself", R.drawable.water, dateTime);
                                notifications.add(notification);
                            }
                        }
                        notificationAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
