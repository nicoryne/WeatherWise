package com.example.weatherwise.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationManager {

    private static final String DEBUG_TAG = "LocationManager";
    private static final long UPDATE_INTERVAL = 10 * 1000; // 10 seconds
    private static final long FASTEST_INTERVAL = 2000; // 2 seconds
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private static LocationManager instance;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location currentLocation;

    private LocationUpdateListener locationUpdateListener;

    public interface LocationUpdateListener {
        void onLocationUpdated(Location location);
    }

    private LocationManager() {}

    public static synchronized LocationManager getInstance() {
        if (instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = new LocationRequest.Builder(UPDATE_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLocation = location;
                        logLocation(location);
                        if (locationUpdateListener != null) {
                            Log.d(DEBUG_TAG, "LocationUpdateListener is set. Notifying listener.");
                            locationUpdateListener.onLocationUpdated(location);
                        } else {
                            Log.d(DEBUG_TAG, "LocationUpdateListener is not set.");
                        }
                    }
                }
            }
        };
    }

    private void logLocation(Location location) {
        Log.i(DEBUG_TAG, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.locationUpdateListener = listener;
    }

    public void startLocationUpdates(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public void stopLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
